package uit.se100.dtos.statistics;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.Instant;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueByTimeRangeDTO {
    private Instant fromDate;
    private Instant toDate;
    private Long totalTicketsSold;
    private BigDecimal totalRevenue;
    private BigDecimal averageTicketPrice;
}
