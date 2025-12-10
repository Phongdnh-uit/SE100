package uit.se100.utils;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;

public class ValidationUtil {

  private static final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
  private static final EmailValidator emailValidator = new EmailValidator();

  public static boolean isValidPhoneNumber(String phoneNumber, String regionCode) {
    try {
      var numberProto = phoneUtil.parse(phoneNumber, regionCode);
      return phoneUtil.isValidNumber(numberProto);
    } catch (Exception e) {
      return false;
    }
  }

  public static String formatPhoneToE164(String phoneNumber, String regionCode) {
    try {
      var numberProto = phoneUtil.parse(phoneNumber, regionCode);
      return phoneUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.E164);
    } catch (Exception e) {
      return null;
    }
  }
}
