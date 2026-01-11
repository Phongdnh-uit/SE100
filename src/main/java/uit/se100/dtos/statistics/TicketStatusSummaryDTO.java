package uit.se100.dtos.statistics;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketStatusSummaryDTO {
    private Long refundedCount;
    private Long changedCount;
    private Long canceledCount;
    private Long totalAffectedTickets;
}
