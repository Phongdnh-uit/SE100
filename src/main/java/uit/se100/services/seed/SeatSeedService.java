package uit.se100.services.seed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uit.se100.dtos.seat.BatchCreateSeatRequest;
import uit.se100.dtos.seat.SeatLayout;
import uit.se100.dtos.seat.SeatResponse;
import uit.se100.entities.aircraft.Aircraft;
import uit.se100.enums.seat.SeatClass;
import uit.se100.repositories.aircraft.AircraftRepository;
import uit.se100.services.seat.SeatService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatSeedService {

    private final SeatService seatService;
    private final AircraftRepository aircraftRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void seed() {

        List<Aircraft> aircrafts = aircraftRepository.findAll();

        if (aircrafts.isEmpty()) {
            log.warn("No aircraft found, skip seat seeding");
            return;
        }

        for (Aircraft aircraft : aircrafts) {
            SeatLayout layout = resolveLayout(aircraft);
            seedAircraft(aircraft.getId(), layout);
        }

        log.info("Seeded seats for {} aircraft(s)", aircrafts.size());
    }

    private void seedAircraft(Long aircraftId, SeatLayout layout) {

        Map<SeatClass, BatchCreateSeatRequest.ClassSeatRequest> classSeatRequests = new HashMap<>();

        // FIRST CLASS
        classSeatRequests.put(
                SeatClass.FIRST_CLASS,
                new BatchCreateSeatRequest.ClassSeatRequest(
                        1,
                        1,
                        SeatLayout.BUSINESS_2_2,
                        List.of()
                )
        );

        // BUSINESS
        classSeatRequests.put(
                SeatClass.BUSINESS,
                new BatchCreateSeatRequest.ClassSeatRequest(
                        layout == SeatLayout.WIDE_2_4_2 ? 2 : 1,
                        layout == SeatLayout.WIDE_2_4_2 ? 2 : 1,
                        layout == SeatLayout.WIDE_2_4_2
                                ? SeatLayout.SMALL_1_2
                                : SeatLayout.BUSINESS_2_2,
                        List.of()
                )
        );

        // ECONOMY
        classSeatRequests.put(
                SeatClass.ECONOMY,
                new BatchCreateSeatRequest.ClassSeatRequest(
                        layout == SeatLayout.WIDE_2_4_2 ? 3 : 2,
                        resolveLastRow(layout),
                        layout,
                        List.of()
                )
        );

        BatchCreateSeatRequest request =
                new BatchCreateSeatRequest(aircraftId, classSeatRequests);

        List<SeatResponse> responses = seatService.batchCreate(request);

        log.info("Aircraft {} → created {} seats", aircraftId, responses.size());
    }

    /**
     * Quyết định layout theo aircraft
     */
    private SeatLayout resolveLayout(Aircraft aircraft) {
        String model = aircraft.getModel().toUpperCase();

        if (model.contains("787") || model.contains("777") || aircraft.getSeatCapacity() >= 280) {
            return SeatLayout.WIDE_2_4_2;
        }

        return SeatLayout.ECONOMY_3_3; // A320 / A321 / B737
    }

    private int resolveLastRow(SeatLayout layout) {
        return switch (layout) {
            case WIDE_2_4_2 -> 30;
            default -> 25;
        };
    }
}

