

package com.comerzzia.pos.core.gui.validation.validators.date;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = DateValidator.class)
@Documented
public @interface IsValidDate {

    String message() default "{default}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    boolean future() default true;
    boolean past() default true;
    boolean isDateTime() default false;
        
    @Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        IsValidDate[] decimals();
        IsValidDate[] past();
    }
}
