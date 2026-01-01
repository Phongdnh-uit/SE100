package uit.se100.dtos.seat;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uit.se100.enums.seat.SeatClass;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BatchCreateSeatRequest {

  @NotNull private Long aircraftId;

  private Map<SeatClass, ClassSeatRequest> classSeatRequests;

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ClassSeatRequest {
    @NotNull
    @Min(1)
    private Integer fromRow;

    @Min(1)
    @NotNull
    private Integer toRow;

    @NotNull private SeatLayout layoutType;

    private List<Integer> excludedRows;
  }
}
