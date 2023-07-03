package pl.edu.ur.quizserver.validation;

import pl.edu.ur.quizserver.web.dto.ChangePasswordDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {

    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        final ChangePasswordDto personDto = (ChangePasswordDto)o;

        return personDto.getPassword().equals(personDto.getConfirmPassword());
    }
}
