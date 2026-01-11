package uit.se100.projections;

import uit.se100.enums.seat.SeatClass;

public interface SeatAvailableProjection {
    SeatClass getSeatClass();

    Long getAvailableSeats();
}

