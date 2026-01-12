package uit.se100.entities.baggage;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uit.se100.entities.BaseEntity;
import uit.se100.entities.flight.Flight;
import uit.se100.entities.passenger.Passenger;
import uit.se100.enums.baggage.BaggageType;

import java.math.BigDecimal;

/**
 * Baggage entity representing baggage information for a passenger on a flight.
 *
 * <p>Baggage can be either carry-on or checked:
 * <ul>
 *   <li>Carry-on baggage: maximum 7kg free, additional charges apply for excess weight
 *   <li>Checked baggage:
 *     <ul>
 *       <li>Economy: 20kg free
 *       <li>Business: 30kg free
 *       <li>First class: 40kg free
 *     </ul>
 * </ul>
 *
 * <p>Excess baggage fee: 100,000 VND for each 5kg (rounded up to nearest 5kg)
 */
@Getter
@Setter
@Entity
@Table(name = "baggage")
public class Baggage extends BaseEntity {

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BaggageType type;

    @Column(name = "weight", nullable = false, precision = 6, scale = 2)
    private BigDecimal weight;

    @Column(name = "extra_fee", precision = 15, scale = 2)
    private BigDecimal extraFee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id", nullable = false)
    private Passenger passenger;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;
}

