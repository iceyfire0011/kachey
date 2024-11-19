package com.solution.kachey.config.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UniquePhoneNumbersValidator implements ConstraintValidator<UniquePhoneNumbers, List<String>> {

    @Override
    public boolean isValid(List<String> phoneNumbers, ConstraintValidatorContext context) {
        if (phoneNumbers == null || phoneNumbers.isEmpty()) {
            return true; // No duplicates if the list is empty or null
        }

        // Check if there are duplicate phone numbers
        Set<String> uniqueNumbers = phoneNumbers.stream().collect(Collectors.toSet());
        return uniqueNumbers.size() == phoneNumbers.size();
    }
}