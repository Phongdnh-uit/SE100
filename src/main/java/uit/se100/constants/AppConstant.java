package uit.se100.constants;

import java.math.BigDecimal;

public interface AppConstant {
    long MAX_RESERVED_TICKET_PER_PERSON = 7;
    BigDecimal PRICE_TICKET_ECONOMY = new BigDecimal("100.00");
    BigDecimal PRICE_TICKET_BUSINESS = new BigDecimal("200.00");
    BigDecimal PRICE_TICKET_FIRST_CLASS = new BigDecimal("300.00");

}
