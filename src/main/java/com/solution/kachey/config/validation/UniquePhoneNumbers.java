package com.solution.kachey.config.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UniquePhoneNumbersValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniquePhoneNumbers {
    String message() default "Phone numbers must be unique";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
