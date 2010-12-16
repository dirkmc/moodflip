package validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.sf.oval.configuration.annotation.Constraint;

/**
 * Checks for bad words. Message key: validation.badWord $1: field name $2: bad
 * words that were found (comma separated)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.FIELD, ElementType.PARAMETER })
@Constraint(checkWith = CaptchaCheck.class)
public @interface Captcha {
    String message() default CaptchaCheck.mes;
}
