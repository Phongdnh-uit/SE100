package uit.se100.services.flight;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uit.se100.dtos.PageResponse;
import uit.se100.dtos.flight.AssignSeatRequest;
import uit.se100.dtos.flight.FlightResponse;
import uit.se100.entities.flight.FlightSeat;
import uit.se100.entities.ticket.Ticket;
import uit.se100.enums.flight.FlightStatus;
import uit.se100.enums.seat.SeatClass;
import uit.se100.enums.seat.SeatStatus;
import uit.se100.enums.ticket.TicketStatus;
import uit.se100.exceptions.errors.ApiException;
import uit.se100.exceptions.errors.ErrorCode;
import uit.se100.mappers.flight.FlightMapper;
import uit.se100.repositories.employee.EmployeeRepository;
import uit.se100.repositories.flight.FlightRepository;
import uit.se100.repositories.flight.FlightSeatRepository;
import uit.se100.repositories.ticket.TicketRepository;
import uit.se100.services.general.MailService;

@Slf4j
@RequiredArgsConstructor
@Service
public class FlightServiceImpl implements FlightService {
  private final MailService mailService;
  private final FlightRepository flightRepository;
  private final FlightSeatRepository flightSeatRepository;
  private final TicketRepository ticketRepository;
  private final FlightMapper flightMapper;
  private final EmployeeRepository employeeRepository;

  @Async
  @Transactional
  @Override
  public void updateFlightStatusWhenSeatChanged(FlightSeat seat) {
    // Cập nhật trạng thái chuyến bay khi có sự thay đổi về ghế (đặt hoặc hủy)
    // Chỉ thay đổi nếu trạng thái đang ở OPEN hoặc FULL
    var flight = seat.getFlight();
    if (flight.getStatus() == FlightStatus.OPEN || flight.getStatus() == FlightStatus.FULL) {
      var totalSeats = flight.getFlightSeats().size();
      var bookedSeats =
          flight.getFlightSeats().stream()
              .filter(
                  s -> s.getStatus() == SeatStatus.BOOKED || s.getStatus() == SeatStatus.RESERVED)
              .count();
      if (bookedSeats == totalSeats) {
        flight.setStatus(FlightStatus.FULL);
      } else {
        flight.setStatus(FlightStatus.OPEN);
      }
      flightRepository.save(flight);
    }
  }

  @Transactional
  @Override
  public void delayFlight(Long flightId, Long delayMinutes) {
    var flightOpt = flightRepository.findById(flightId);
    if (!flightOpt.isPresent()) {
      throw new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Flight not found");
    }
    var flight = flightOpt.get();
    Instant oldDepartureTime = flight.getDepartureTime();
    flight.setStatus(FlightStatus.DELAYED);
    flight.setDepartureTime(flight.getDepartureTime().plusSeconds(delayMinutes * 60));
    flight.setArrivalTime(flight.getArrivalTime().plusSeconds(delayMinutes * 60));
    var newflight = flightRepository.save(flight);

    // Gửi mail tới tất cả hành khách trên chuyến bay thông báo về delay chuyến bay
    var tickets = flight.getTickets();
    tickets.forEach(
        t -> {
          var p = t.getPassenger();
          Map<String, Object> model = new HashMap<>();
          model.put("customerName", p.getFullName());
          model.put("flightNumber", newflight.getId());
          model.put("bookingCode", t.getId());
          model.put("oldTime", oldDepartureTime);
          model.put("newTime", newflight.getDepartureTime());
          mailService.sendEmailFromTemplate(
              p.getUser().getEmail(),
              "[Notification] Flight Delay Information",
              "flight-delayed",
              model);
        });
  }

  // Chưa có hoàn tiền vé trong trường hợp hủy chuyến bay
  @Transactional
  @Override
  public void cancelFlight(Long flightId) {
    var flightOpt = flightRepository.findById(flightId);
    if (!flightOpt.isPresent()) {
      throw new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Flight not found");
    }
    var flight = flightOpt.get();
    flight.setStatus(FlightStatus.CANCELED);
    var newflight = flightRepository.save(flight);
    // Gửi mail tới tất cả hành khách trên chuyến bay thông báo về hủy chuyến bay
    var tickets = flight.getTickets();
    tickets.forEach(
        t -> {
          var p = t.getPassenger();
          Map<String, Object> model = new HashMap<>();
          model.put("customerName", p.getFullName());
          model.put("flightNumber", newflight.getId());
          model.put("bookingCode", t.getId());
          mailService.sendEmailFromTemplate(
              p.getUser().getEmail(),
              "[Notification] Flight Cancellation Information",
              "flight-cancelled",
              model);
          // Cần hoàn tiền vé ở đây
        });
  }

  @Override
  public PageResponse<FlightSeat> getSeatAvailable(
      Long flightId, SeatClass seatClass, Pageable pageable) {
    Page<FlightSeat> flightSeats =
        flightSeatRepository.findBySeatClassAndFlightIdAndStatusOrderById(
            seatClass, SeatStatus.AVAILABLE, flightId, pageable);

    return PageResponse.fromPage(flightSeats);
  }

  /**
   * Gán ghế cho vé khi check-in
   *
   * @param flightId - ID của chuyến bay
   * @param seatId - ID của ghế cần gán
   * @param ticketId - ID của vé cần gán ghế
   * @return FlightResponse - thông tin chuyến bay sau khi gán ghế
   * @throws ApiException nếu:
   *     <ul>
   *       <li>Vé không tồn tại
   *       <li>Vé không ở trạng thái PAID
   *       <li>Ghế không tồn tại
   *       <li>Ghế không thuộc chuyến bay
   *       <li>Ghế không trống (trạng thái không phải AVAILABLE)
   *     </ul>
   */
  @Transactional
  @Override
  public FlightResponse assignSeat(Long flightId, AssignSeatRequest seatRequest) {
    Long ticketId = seatRequest.getTicketId();
    Long seatId = seatRequest.getSeatId();
    // ...validate ticket exists and is PAID
    Ticket ticket =
        ticketRepository
            .findById(ticketId)
            .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Ticket not found"));

    if (ticket.getPassenger() != null
        && ticket.getPassenger().getUser() != null
        && !ticket.getPassenger().getUser().getEmail().equals(seatRequest.getPassengerEmail())) {
      throw new ApiException(
          ErrorCode.OPERATION_NOT_ALLOWED, "You are not allowed to assign seat for this ticket");
    }

    // Kiểm tra vé có trạng thái PAID
    if (ticket.getStatus() != TicketStatus.PAID) {
      throw new ApiException(
          ErrorCode.OPERATION_NOT_ALLOWED,
          "Only PAID tickets can be checked in. Current status: " + ticket.getStatus());
    }

    // ...validate seat exists and belongs to the flight
    FlightSeat seat =
        flightSeatRepository
            .findById(seatId)
            .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Seat not found"));

    // Kiểm tra ghế thuộc chuyến bay
    if (!seat.getFlight().getId().equals(flightId)) {
      throw new ApiException(ErrorCode.VALIDATION_ERROR, "Seat does not belong to this flight");
    }

    // ...validate seat is available
    if (seat.getStatus() != SeatStatus.AVAILABLE) {
      throw new ApiException(
          ErrorCode.OPERATION_NOT_ALLOWED,
          "Seat is not available. Current status: " + seat.getStatus());
    }

    // ...assign seat to ticket and update seat status
    ticket.setSeat(seat);
    seat.setStatus(SeatStatus.BOOKED);

    // Cập nhật ticket status - có thể có thêm trạng thái CHECK_IN trong tương lai
    // Nhưng hiện tại vẫn giữ PAID để thể hiện đã thanh toán
    ticketRepository.save(ticket);
    flightSeatRepository.save(seat);

    // ...update flight status
    updateFlightStatusWhenSeatChanged(seat);

    // ...return flight response
    var flight =
        flightRepository
            .findById(flightId)
            .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Flight not found"));

    return flightMapper.entityToResponse(flight);
  }

  @Transactional
  @Override
  public void updateFlightsToDeparted() {
    Instant currentTime = Instant.now();
    List<FlightStatus> eligibleStatuses =
        List.of(FlightStatus.OPEN, FlightStatus.FULL, FlightStatus.DELAYED);

    var flights = flightRepository.findFlightsReadyToDepart(currentTime, eligibleStatuses);

    if (!flights.isEmpty()) {
      log.info("Auto updating {} flight(s) to DEPARTED status", flights.size());
      flights.forEach(
          flight -> {
            flight.setStatus(FlightStatus.DEPARTED);
            flightRepository.save(flight);
            log.info("Flight {} has been updated to DEPARTED status", flight.getId());
          });
    }
  }

  @Transactional
  @Override
  public void updateFlightsToCompleted() {
    Instant currentTime = Instant.now();

    var flights = flightRepository.findFlightsReadyToComplete(currentTime, FlightStatus.DEPARTED);

    if (!flights.isEmpty()) {
      log.info("Auto updating {} flight(s) to COMPLETED status", flights.size());
      flights.forEach(
          flight -> {
            flight.setStatus(FlightStatus.COMPLETED);
            flightRepository.save(flight);
            log.info("Flight {} has been updated to COMPLETED status", flight.getId());
          });
    }
  }
}

