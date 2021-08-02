package it.unipd.stage.sl.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigInteger;

/*
I wouldn't call this efficient, but it avoids repeating checks
 */
public class BigIntegerValidator implements ConstraintValidator<BigIntegerA, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            new BigInteger(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
