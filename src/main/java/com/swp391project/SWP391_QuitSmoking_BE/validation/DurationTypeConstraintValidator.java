package com.swp391project.SWP391_QuitSmoking_BE.validation;
import com.swp391project.SWP391_QuitSmoking_BE.interfaces.IDurationAware;

import com.swp391project.SWP391_QuitSmoking_BE.enums.DurationType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DurationTypeConstraintValidator implements ConstraintValidator<ValidDurationTypeConstraint, IDurationAware> {
    @Override
    public void initialize(ValidDurationTypeConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(IDurationAware durationAware, ConstraintValidatorContext context) {
        if (durationAware == null) {
            return true;
        }

        Integer duration = durationAware.getDuration();
        DurationType durationType = durationAware.getDurationType();

        //check if fields are notnull before complex logic
        if (duration == null || durationType == null) {
            return true;
        }

        boolean isValid = false;
        String errorMessage = "";

        if (durationType == DurationType.MONTH) {
            if (duration >= 1 && duration <= 12) {
                isValid = true;
            } else {
                errorMessage = "When duration type is 'Month', duration must be between 1 and 12";
            }
        } else if (durationType == DurationType.DAY) {
            if (duration >= 1 && duration <= 31) {
                isValid = true;
            } else {
                errorMessage = "When duration type is 'Day', duration must be between 1 and 31";
            }
        } else {
            errorMessage = "Invalid duration type";
        }

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(errorMessage)
                    .addPropertyNode("duration") //Attach error to the 'duration' field
                    .addConstraintViolation();
        }

        return isValid;
    }
}
