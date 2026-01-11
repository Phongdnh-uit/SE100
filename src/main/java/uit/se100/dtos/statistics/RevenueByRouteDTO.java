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
public class RevenueByRouteDTO {
    private Long routeId;
    private String origin;
    private String destination;
    private Boolean isExternal;
    private Long totalFlights;
    private Long totalTicketsSold;
    private BigDecimal totalRevenue;
}
