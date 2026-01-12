package uit.se100.enums.seat;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public enum SeatClass {
    ECONOMY(100),
    BUSINESS(200),
    FIRST_CLASS(300);

    private final BigDecimal price;

    SeatClass(int price) {
        this.price = new BigDecimal(price);
    }

}
