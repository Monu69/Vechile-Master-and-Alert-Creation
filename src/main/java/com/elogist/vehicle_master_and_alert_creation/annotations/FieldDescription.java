package com.elogist.vehicle_master_and_alert_creation.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface FieldDescription {

    public String description() default "";

    public String unit() default "";

}
