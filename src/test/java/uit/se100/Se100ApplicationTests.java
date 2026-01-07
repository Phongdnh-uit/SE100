package uit.se100;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
      "spring.datasource.driverClassName=org.h2.Driver",
      "spring.datasource.username=sa",
      "spring.datasource.password=",
      "spring.jpa.hibernate.ddl-auto=create-drop",
      "spring.jpa.show-sql=true",
      "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
      "jwt.secret=xin_chao_ban!_day_la_du_an_uit_land_cho_do_an_1_cua_minh!_xin_cam_on_ban_da_doc_du_an_nay",
      "jwt.refresh-token.expiration=172800",
      "jwt.access-token.expiration=18000",
      "spring.mail.host=smtp.gmail.com",
      "spring.mail.port=587",
      "spring.mail.username=test@gmail.com",
      "spring.mail.password=password",
      "spring.mail.sender=password",
      "app.client.base-url=http://localhost:3000"
    })
class Se100ApplicationTests {

  @Test
  void contextLoads() {}
}
