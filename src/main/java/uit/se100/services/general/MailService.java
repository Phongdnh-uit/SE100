package uit.se100.services.general;

import java.util.Map;

public interface MailService {

  void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml);

  void sendEmailFromTemplate(
      String to, String subject, String templateName, Map<String, Object> model);
}
