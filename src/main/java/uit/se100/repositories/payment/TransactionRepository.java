package uit.se100.repositories.payment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uit.se100.entities.payment.Transaction;
import uit.se100.enums.payments.TransactionStatus;
import uit.se100.enums.payments.TransactionType;
import uit.se100.repositories.SimpleRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * TransactionRepository
 * <p>
 * Repository để quản lý các giao dịch thanh toán
 */
@Repository
public interface TransactionRepository extends SimpleRepository<Transaction, Long> {

    /**
     * Tìm giao dịch theo provider transaction reference
     */
    Optional<Transaction> findByProviderTxnRef(String providerTxnRef);

    /**
     * Tìm giao dịch theo provider transaction number (từ provider)
     */
    Optional<Transaction> findByProviderTxnNo(String providerTxnNo);

    /**
     * Lấy danh sách giao dịch của một vé
     */
    List<Transaction> findByTicketId(Long ticketId);

    /**
     * Lấy danh sách giao dịch theo trạng thái
     */
    List<Transaction> findByStatus(TransactionStatus status);

    /**
     * Lấy danh sách giao dịch theo loại
     */
    List<Transaction> findByType(TransactionType type);

    /**
     * Lấy danh sách giao dịch trong khoảng thời gian
     */
    @Query("""
            SELECT t FROM Transaction t 
            WHERE t.createdAt BETWEEN :startDate AND :endDate
            ORDER BY t.createdAt DESC
            """)
    Page<Transaction> findByDateRange(
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            Pageable pageable
    );

    /**
     * Lấy danh sách giao dịch thành công
     */
    List<Transaction> findByStatusAndType(TransactionStatus status, TransactionType type);

    /**
     * Đếm số giao dịch thành công
     */
    long countByStatusAndType(TransactionStatus status, TransactionType type);
}

