package uit.se100.entities.payment;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uit.se100.entities.BaseEntity;
import uit.se100.entities.ticket.Ticket;
import uit.se100.enums.payments.TransactionStatus;
import uit.se100.enums.payments.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "transactions")
public class Transaction extends BaseEntity {
    @ManyToOne()
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    // Mã giao dịch do hệ thống bạn tạo
    @Column(name = "provider_txn_ref", nullable = false, unique = true)
    private String providerTxnRef;

    // Mã giao dịch do VNPay / MoMo trả về
    @Column(name = "provider_txn_no")
    private String providerTxnNo;

//    // Raw response từ gateway (JSON)
//    @Lob
//    @Column(columnDefinition = "TEXT")
//    private String providerResponse;

    private Instant paidAt;

    private String failReason;
}
