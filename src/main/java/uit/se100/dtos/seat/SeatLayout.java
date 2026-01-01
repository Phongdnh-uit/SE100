package uit.se100.dtos.seat;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Kiểu bố trí ghế (Ví dụ: 2-2, 3-3)")
public enum SeatLayout {
  @Schema(description = "Cấu hình 2-2 (A-C, D-F)")
  BUSINESS_2_2(List.of("A", "C", "D", "F")),

  @Schema(description = "Cấu hình 3-3 (A-B-C, D-E-F)")
  ECONOMY_3_3(List.of("A", "B", "C", "D", "E", "F")),

  @Schema(description = "Cấu hình 2-4-2 (A-C, D-E-F-G, H-K)")
  WIDE_2_4_2(List.of("A", "C", "D", "E", "F", "G", "H", "K")),

  @Schema(description = "Cấu hình nhỏ 1-2 (A, C-D)")
  SMALL_1_2(List.of("A", "C", "D"));

  private final List<String> seatLetters;

  SeatLayout(List<String> seatLetters) {
    this.seatLetters = seatLetters;
  }

  public List<String> getSeatLetters() {
    return seatLetters;
  }
}
