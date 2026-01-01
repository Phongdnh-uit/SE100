package uit.se100.services.ticket;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uit.se100.constants.AppConstant;
import uit.se100.dtos.ticket.ReserveTicketRequest;
import uit.se100.dtos.ticket.TicketResponse;
import uit.se100.entities.flight.Flight;
import uit.se100.entities.passenger.Passenger;
import uit.se100.entities.seat.Seat;
import uit.se100.entities.ticket.Ticket;
import uit.se100.enums.seat.SeatClass;
import uit.se100.enums.seat.SeatStatus;
import uit.se100.enums.ticket.TicketStatus;
import uit.se100.exceptions.errors.ApiException;
import uit.se100.exceptions.errors.ErrorCode;
import uit.se100.mappers.ticket.TicketMapper;
import uit.se100.repositories.flight.FlightRepository;
import uit.se100.repositories.passenger.PassengerRepository;
import uit.se100.repositories.seat.SeatRepository;
import uit.se100.repositories.ticket.TicketRepository;
import uit.se100.securities.CustomUserDetails;
import uit.se100.utils.SecurityUtils;

import java.math.BigDecimal;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final SeatRepository seatRepository;
    private final PassengerRepository passengerRepository;
    private final FlightRepository flightRepository;
    private final TicketMapper ticketMapper;

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

        CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null) throw new ApiException(ErrorCode.AUTHENTICATION_REQUIRED, "Current user not found");

        Long currentUserId = currentUser.getId();

        // Get passenger
        Passenger passenger = passengerRepository.findById(currentUserId)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Passenger not found"));

        // Check max tickets per flight
        int reservedCount = ticketRepository.countActiveTickets(passenger.getId(), request.flightId());
        if (reservedCount >= AppConstant.MAX_RESERVED_TICKET_PER_PERSON) {
            throw new ApiException(ErrorCode.OPERATION_NOT_ALLOWED, "Passenger can reserve max 7 tickets per flight");
        }

        // Get flight
        Flight flight = flightRepository.findById(request.flightId())
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Flight not found"));

        // Try to find available seat
        // Seat seat = seatRepository.findAvailableSeat(request.flightId(), request.seatClass()).orElse(null);


        var price = this.getPriceFromSeatClass(request.seatClass());

        // Create ticket
        Ticket ticket = new Ticket();
        ticket.setFlight(flight);
        ticket.setPassenger(passenger);
        ticket.setTicketClass(request.seatClass());
        ticket.setPrice(price);
        ticket.setBookedAt(Instant.now());

        // if (seat != null) {
        //     // Seat available
        //     // seat.setStatus(SeatStatus.RESERVED);
        //     ticket.setSeat(seat);
        //     ticket.setStatus(TicketStatus.RESERVED);
        //     seatRepository.save(seat);
        // } else {
        //     // No seat available, add to waiting list
        //     ticket.setStatus(TicketStatus.WAITING);
        // }

        ticketRepository.save(ticket);

        return ticketMapper.toResponse(ticket);
    }

    BigDecimal getPriceFromSeatClass(SeatClass seatClass) {
        // Set price based on class (example prices)
        BigDecimal price = switch (seatClass) {
            case ECONOMY -> AppConstant.PRICE_TICKET_ECONOMY;
            case BUSINESS -> AppConstant.PRICE_TICKET_BUSINESS;
            case FIRST_CLASS -> AppConstant.PRICE_TICKET_FIRST_CLASS;
        };

        return price;
    }

}
