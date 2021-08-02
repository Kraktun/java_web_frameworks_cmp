package it.unipd.stage.sl.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/*
Example of how to write a custom annotation to check that a string is a valid big integer
 */
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE,
ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = BigIntegerValidator.class)
public @interface BigIntegerA {
    String message() default "This field must be a BigInteger";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
