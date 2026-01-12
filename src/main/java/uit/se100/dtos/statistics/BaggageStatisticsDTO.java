package uit.se100.dtos.statistics;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaggageStatisticsDTO {
    private Long totalBaggageCount;
    private BigDecimal totalWeight;
    private Long overweightCount;
    private BigDecimal totalExtraFee;
    private Long carryOnCount;
    private Long checkedCount;
}
