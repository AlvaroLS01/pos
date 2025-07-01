

package com.comerzzia.pos.core.gui.validation.validators.number;

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
@Constraint(validatedBy = NumericValidator.class)
@Documented
public @interface IsNumeric {

    String message() default "{default}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    int decimals() default 0;
    boolean isAmount() default false;
    boolean permitsZero() default true;
    int precision() default 0; //0 - no valida precision
        
    @Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @Documented
    @interface List {
    	IsNumeric[] decimals();
        IsNumeric[] isAmount();
        IsNumeric[] scale();
    }
}
