package com.gui.domain.validators;

import com.gui.domain.User;

/**
 * Validator for Utilizator entity
 */
public class UserValidator implements Validator<User> {
    @Override
    public void validate(User entity) throws ValidationException {
        StringBuilder stringBuilder = new StringBuilder();
        String errors = "";
        if (entity.getFirstName().equals(""))
            stringBuilder.append("You need a first name!\n");
        if (entity.getLastName().equals(""))
            stringBuilder.append("You need a last name!\n");
        if (entity.getLastName().length()+entity.getLastName().length()>35)
            stringBuilder.append("Your name is too big!\n");
        if (!entity.getGender().equals("Male") && !entity.getGender().equals("Female") && !entity.getGender().equals("Others"))
            stringBuilder.append("The gender can be only Male,Female or Others \n");
        if(entity.getEmail().isEmpty())
            stringBuilder.append("You need an email!");
        else if(!(entity.getEmail().contains("@")|| entity.getEmail().contains(".")))
            stringBuilder.append("Incorrect email!");
        if(entity.getPassword().isEmpty())
            stringBuilder.append("You need a password!");
        errors = stringBuilder.toString();
        if (errors.length() > 0)
            throw new ValidationException(errors);

    }
}
