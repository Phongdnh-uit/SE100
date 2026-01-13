package uit.se100.services.ticket;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uit.se100.constants.AppConstant;
import uit.se100.dtos.PageResponse;
import uit.se100.dtos.passenger.PassengerRequest;
import uit.se100.dtos.passenger.PassengerResponse;
import uit.se100.dtos.ticket.ReserveTicketRequest;
import uit.se100.dtos.ticket.TicketResponse;
import uit.se100.entities.flight.Flight;
import uit.se100.entities.flight.FlightSeat;
import uit.se100.entities.passenger.Passenger;
import uit.se100.entities.payment.Transaction;
import uit.se100.entities.ticket.Ticket;
import uit.se100.enums.RoleEnum;
import uit.se100.enums.payments.TransactionStatus;
import uit.se100.enums.seat.SeatClass;
import uit.se100.enums.ticket.TicketStatus;
import uit.se100.exceptions.errors.ApiException;
import uit.se100.exceptions.errors.ErrorCode;
import uit.se100.mappers.ticket.TicketMapper;
import uit.se100.repositories.flight.FlightRepository;
import uit.se100.repositories.flight.FlightSeatRepository;
import uit.se100.repositories.passenger.PassengerRepository;
import uit.se100.repositories.payment.TransactionRepository;
import uit.se100.repositories.ticket.TicketRepository;
import uit.se100.securities.CustomUserDetails;
import uit.se100.services.CrudService;
import uit.se100.services.PaymentService;
import uit.se100.services.flight.FlightService;
import uit.se100.utils.SecurityUtils;

import java.math.BigDecimal;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final FlightSeatRepository flightSeatRepository;
    private final PassengerRepository passengerRepository;
    private final FlightRepository flightRepository;
    private final TicketMapper ticketMapper;
    private final FlightService flightService;
    private final CrudService<Passenger, Long, PassengerRequest, PassengerResponse> passengerService;
    private final PaymentService paymentService;
    private final TransactionRepository transactionRepository;

    /**
     * Đặt giữ chỗ vé cho một chuyến bay.
     *
     * <p>
     * Luồng xử lý:
     * <ul>
     *   <li>Kiểm tra người dùng hiện tại và thông tin hành khách</li>
     *   <li>Kiểm tra số vé đang giữ của hành khách cho chuyến bay (tối đa 7 vé)</li>
     *   <li>Tìm ghế trống theo hạng vé yêu cầu</li>
     *   <li>
     *     Nếu còn ghế:
     *     <ul>
     *       <li>Đánh dấu ghế là RESERVED</li>
     *       <li>Tạo vé với trạng thái RESERVED</li>
     *     </ul>
     *   </li>
     *   <li>
     *     Nếu hết ghế:
     *     <ul>
     *       <li>Tạo vé với trạng thái WAITING</li>
     *     </ul>
     *   </li>
     * </ul>
     * </p>
     *
     * <p>
     * Vé ở trạng thái RESERVED phải được thanh toán trong vòng 24 giờ,
     * nếu không hệ thống sẽ tự động hủy.
     * </p>
     *
     * @param request thông tin yêu cầu đặt vé, bao gồm:
     *                <ul>
     *                  <li>flightId – mã chuyến bay</li>
     *                  <li>ticketClass – hạng vé (economy, business, first)</li>
     *                </ul>
     * @return thông tin vé đã được tạo, bao gồm trạng thái vé, ghế (nếu có)
     * @throws Exception nếu:
     *                   <ul>
     *                     <li>Hành khách không tồn tại</li>
     *                     <li>Chuyến bay không tồn tại</li>
     *                     <li>Hành khách đã giữ tối đa 7 vé cho chuyến bay</li>
     *                   </ul>
     */

    @Transactional
    public TicketResponse reserveTicket(ReserveTicketRequest request) {

//        CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
//        if (currentUser == null) throw new ApiException(ErrorCode.AUTHENTICATION_REQUIRED, "Current user not found");
//
//        Long currentUserId = currentUser.getId();
//
//        // Get passenger
//        Passenger passenger = passengerRepository.findById(currentUserId)
//                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Passenger not found"));

        Passenger passenger = this.getCurrentPassenger();

        // Check max tickets per flight
        int reservedCount = ticketRepository.countActiveTickets(passenger.getId(), request.flightId());
        if (reservedCount >= AppConstant.MAX_RESERVED_TICKET_PER_PERSON) {
            throw new ApiException(ErrorCode.OPERATION_NOT_ALLOWED, "Passenger can reserve max 7 tickets per flight");
        }

        // Get flight
        Flight flight = flightRepository.findById(request.flightId())
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Flight not found"));

        // Try to find available seat
        FlightSeat seat = flightSeatRepository.findFirstBySeatClassAndFlightIdOrderByPriceAsc(request.seatClass(), flight.getId()).orElse(null);

        if (seat == null) {
            throw new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Seat not found");

        }

        var price = seat.getPrice();

        // Create ticket
        Ticket ticket = new Ticket();
        ticket.setFlight(flight);
        ticket.setPassenger(passenger);
        ticket.setTicketClass(request.seatClass());
        ticket.setPrice(price);
        ticket.setBookedAt(Instant.now());

        ticket.setSeat(seat);
        ticket.setStatus(TicketStatus.RESERVED);
        flightService.updateFlightStatusWhenSeatChanged(seat);

        ticketRepository.save(ticket);

        return ticketMapper.toResponse(ticket);
    }

    @Override
    public PageResponse<TicketResponse> findByPassengerId(Pageable pageable) {

        Passenger passenger = this.getCurrentPassenger();

        var result = this.ticketRepository.findByPassengerId(passenger.getId(), pageable);

        return PageResponse.fromPage(result.map(ticketMapper::toResponse));
    }

    @Override
    public void refundTicket(Long ticketId) {
        //check ticket
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Ticket not found"));

        CustomUserDetails currentUser = SecurityUtils.getCurrentUser();

        assert currentUser != null;
        if (!canRefundTicket(currentUser, ticket)) throw new ApiException(ErrorCode.FORBIDDEN);

        Transaction transaction = transactionRepository.findByTicketIdAndStatusOrderById(ticketId, TransactionStatus.SUCCESS);

        if (transaction == null) throw new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Transaction not found");


        paymentService.refundTransaction(ticketId);
    }

    @Override
    public TicketResponse findById(Long ticketId) {
        var result = ticketRepository.findById(ticketId).orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Ticket not found"));
        return ticketMapper.toResponse(result);
    }

    private boolean canRefundTicket(CustomUserDetails currentUser, Ticket ticket) {
        if (currentUser.getRole() == RoleEnum.PASSENGER) {
            return ticket.getPassenger().getId().equals(currentUser.getId());
        }
        return currentUser.getRole() == RoleEnum.ADMIN || currentUser.getRole() == RoleEnum.EMPLOYEE;
    }

    BigDecimal getPriceFromSeatClass(SeatClass seatClass, Long flightId) {
        FlightSeat flightSeat = flightSeatRepository.findFirstBySeatClassAndFlightIdOrderByPriceAsc(seatClass, flightId).orElse(null);


        return flightSeat == null ? BigDecimal.valueOf(100000) : flightSeat.getPrice();
    }

    Passenger getCurrentPassenger() {
        CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null) throw new ApiException(ErrorCode.AUTHENTICATION_REQUIRED, "Current user not found");

        Long currentUserId = currentUser.getId();

        // Get passenger
        Passenger passenger = passengerRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Passenger not found"));

        return passenger;
    }


}
