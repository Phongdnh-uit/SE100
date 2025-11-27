package uit.se100.utils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import uit.se100.annotations.ValidPhone;

public class PhoneValidator implements ConstraintValidator<ValidPhone, String> {

  private String defaultRegion;

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null || value.isEmpty()) {
      return false;
    }
    return ValidationUtil.isValidPhoneNumber(value, defaultRegion);
  }

  @Override
  public void initialize(ValidPhone constraintAnnotation) {
    this.defaultRegion = constraintAnnotation.defaultRegion();
  }
}
