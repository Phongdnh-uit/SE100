package uit.se100.services.flight;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uit.se100.entities.flight.FlightSeat;
import uit.se100.enums.flight.FlightStatus;
import uit.se100.enums.seat.SeatStatus;
import uit.se100.exceptions.errors.ApiException;
import uit.se100.exceptions.errors.ErrorCode;
import uit.se100.repositories.employee.EmployeeRepository;
import uit.se100.repositories.flight.FlightRepository;
import uit.se100.services.general.MailService;

@Slf4j
@RequiredArgsConstructor
@Service
public class FlightServiceImpl implements FlightService {
  private final MailService mailService;
  private final FlightRepository flightRepository;
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
    flight.setDurationMinutes(flight.getDurationMinutes() + Math.toIntExact(delayMinutes));
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
            // Cập nhật số giờ bay cho phi hành đoàn
            var crews = flight.getCrewAssignments().stream().map(ca -> ca.getEmployee()).toList();
            crews.forEach(
                crew -> {
                  Integer totalHours =
                      Math.toIntExact(
                          crew.getTotalFlightHours() + flight.getDurationMinutes() / 60);
                  crew.setTotalFlightHours(totalHours);
                });
            employeeRepository.saveAll(crews);
          });
    }
  }
}
