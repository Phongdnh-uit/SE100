package uit.se100.services.general;

import jakarta.mail.internet.MimeMessage;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RequiredArgsConstructor
@Service
public class MailServiceImpl implements MailService {
  private final JavaMailSender mailSender;
  private final TemplateEngine templateEngine;

  @Value("${spring.mail.sender}")
  private String FROM_EMAIL;

  /**
   * @param to : recipient of the email
   * @param subject : subject of the email
   * @param content : content of the email, can be HTML or plain text
   * @param isMultipart : if the email contains attachments
   * @param isHtml : if the email content is HTML
   */
  @Async
  public void sendEmail(
      String to, String subject, String content, boolean isMultipart, boolean isHtml) {

    MimeMessage message = mailSender.createMimeMessage();
    try {
      MimeMessageHelper helper = new MimeMessageHelper(message, isMultipart, "UTF-8");
      helper.setTo(to);
      helper.setFrom(FROM_EMAIL);
      helper.setSubject(subject);
      helper.setText(content, isHtml);
      mailSender.send(message);
    } catch (Exception e) {
      throw new RuntimeException("Failed to send email", e);
    }
  }

  /**
   * @param to : recipient of the email
   * @param subject : subject of the email
   * @param templateName : name of the email template
   * @param model : model data to be used in the template
   */
  @Async
  public void sendEmailFromTemplate(
      String to, String subject, String templateName, Map<String, Object> model) {
    Context context = new Context();
    context.setVariables(model);
    String content = templateEngine.process(templateName, context);
    sendEmail(to, subject, content, false, true);
  }
}
