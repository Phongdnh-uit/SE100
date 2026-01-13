package uit.se100.services.seed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uit.se100.dtos.seat.BatchCreateSeatRequest;
import uit.se100.dtos.seat.SeatLayout;
import uit.se100.dtos.seat.SeatResponse;
import uit.se100.enums.seat.SeatClass;
import uit.se100.services.seat.SeatService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatSeedService {

    private final SeatService seatService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void seed() {
        // Seed Aircraft 1 & 2 (A320 - 180 seats)
        seedAircraft(1L, 1, 5, SeatLayout.ECONOMY_3_3);
        seedAircraft(2L, 1, 5, SeatLayout.ECONOMY_3_3);

        // Seed Aircraft 3 (A321 - 220 seats)
        seedAircraft(3L, 1, 5, SeatLayout.ECONOMY_3_3);

        // Seed Aircraft 5 (B787 - 300 seats)
        seedAircraft(5L, 1, 5, SeatLayout.WIDE_2_4_2);

        log.info("Seeded seats for all aircraft");
    }

    private void seedAircraft(Long aircraftId, int fromRow, int toRow, SeatLayout layout) {
        Map<SeatClass, BatchCreateSeatRequest.ClassSeatRequest> classSeatRequests = new HashMap<>();

        // First Class: Row 1 only
        classSeatRequests.put(
                SeatClass.FIRST_CLASS,
                new BatchCreateSeatRequest.ClassSeatRequest(
                        1,
                        1,
                        layout == SeatLayout.WIDE_2_4_2 ? SeatLayout.BUSINESS_2_2 : SeatLayout.BUSINESS_2_2,
                        List.of()
                )
        );

        // Business: Row 1 (second part) or Row 2 start
        classSeatRequests.put(
                SeatClass.BUSINESS,
                new BatchCreateSeatRequest.ClassSeatRequest(
                        1,
                        layout == SeatLayout.WIDE_2_4_2 ? 2 : 1,
                        layout == SeatLayout.WIDE_2_4_2 ? SeatLayout.SMALL_1_2 : SeatLayout.BUSINESS_2_2,
                        layout == SeatLayout.WIDE_2_4_2 ? List.of() : List.of()
                )
        );

        // Economy: Rows 2-5
        classSeatRequests.put(
                SeatClass.ECONOMY,
                new BatchCreateSeatRequest.ClassSeatRequest(
                        layout == SeatLayout.WIDE_2_4_2 ? 3 : 2,
                        5,
                        layout,
                        List.of()
                )
        );

        BatchCreateSeatRequest request = new BatchCreateSeatRequest(aircraftId, classSeatRequests);
        List<SeatResponse> responses = seatService.batchCreate(request);

        log.info("Created {} seats for aircraft {}", responses.size(), aircraftId);
    }
}

