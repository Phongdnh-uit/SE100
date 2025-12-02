package uit.se100;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class Se100Application {

  public static void main(String[] args) {
    SpringApplication.run(Se100Application.class, args);
  }
}
