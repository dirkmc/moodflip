package validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.sf.oval.configuration.annotation.Constraint;

/**
 * Checks that the field is unique in the database
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.FIELD, ElementType.PARAMETER })
@Constraint(checkWith = UniqueCheck.class)
public @interface Unique {
    //String message() default UniqueCheck.mes;
    String message() default "validation.unique";
}
