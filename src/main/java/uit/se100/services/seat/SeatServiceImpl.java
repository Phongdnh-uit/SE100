package uit.se100.services.seat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uit.se100.dtos.seat.BatchCreateSeatRequest;
import uit.se100.dtos.seat.SeatResponse;
import uit.se100.entities.seat.Seat;
import uit.se100.enums.seat.SeatClass;
import uit.se100.exceptions.errors.ApiException;
import uit.se100.exceptions.errors.ErrorCode;
import uit.se100.mappers.seat.SeatMapper;
import uit.se100.repositories.aircraft.AircraftRepository;
import uit.se100.repositories.seat.SeatRepository;

@RequiredArgsConstructor
@Service
public class SeatServiceImpl implements SeatService {

  private final AircraftRepository aircraftRepository;
  private final SeatRepository seatRepository;
  private final SeatMapper seatMapper;

  @Transactional
  @Override
  public List<SeatResponse> batchCreate(BatchCreateSeatRequest request) {
    // 1. ---- Validate ----
    Map<String, String> errors = new HashMap<>();
    if (!aircraftRepository.existsById(request.getAircraftId())) {
      errors.put("aircraftId", "Aircraft not found"); // hash cứng chưa ổn lắm, tạm
    }
    // list to save
    List<Seat> seatToCreate = new ArrayList<>();
    for (var entry : request.getClassSeatRequests().entrySet()) {
      SeatClass seatClass = entry.getKey();
      var classSeatRequest = entry.getValue();
      // check fromRow <= toRow
      if (classSeatRequest.getFromRow() > classSeatRequest.getToRow()) {
        errors.put(seatClass.name(), "From row must be less than or equal to To row");
      }
      // check excludedRows in range
      for (int row = classSeatRequest.getFromRow();
          row <= classSeatRequest.getToRow() && !classSeatRequest.getExcludedRows().contains(row);
          row++) {
        List<String> layout = classSeatRequest.getLayoutType().getSeatLetters();
        // generate seat numbers
        for (String seatLetter : layout) {
          String seatNumber = row + seatLetter;
          Seat seat = new Seat();
          seat.setAircraft(aircraftRepository.getReferenceById(request.getAircraftId()));
          seat.setSeatNumber(seatNumber);
          seat.setSeatClass(seatClass);
          // add to list
          seatToCreate.add(seat);
        }
      }
    }
    // if have errors, throw exception
    if (!errors.isEmpty()) {
      throw new ApiException(ErrorCode.VALIDATION_ERROR, errors);
    }

    // 2. ---- Save to DB ----
    List<Seat> createdSeats = seatRepository.saveAll(seatToCreate);
    // 3. ---- Prepare response ----
    List<SeatResponse> response = createdSeats.stream().map(seatMapper::entityToResponse).toList();
    return response;
  }
}
