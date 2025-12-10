package uit.se100.entities.passenger;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uit.se100.entities.BaseEntity;
import uit.se100.entities.authentication.User;
import uit.se100.enums.passenger.TierEnum;

@Getter
@Setter
@Entity
@Table(name = "passengers")
public class Passenger extends BaseEntity {

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(name = "date_of_birth")
    private java.time.LocalDate dateOfBirth;

    @Column(length = 50)
    private String nationality;

    @Column(name = "id_number", length = 50)
    private String idNumber;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(length = 20)
    private String phone;


    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TierEnum tier;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
