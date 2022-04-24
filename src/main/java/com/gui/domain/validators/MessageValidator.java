package com.gui.domain.validators;

import com.gui.domain.Message;

public class MessageValidator implements Validator<Message> {
    @Override
    public void validate(Message entity) throws ValidationException {
        StringBuilder stringBuilder = new StringBuilder();
        String errors = "";
        if (entity.getMessage().equals(""))
            stringBuilder.append("The message can't be empty\n");
        errors = stringBuilder.toString();

        if (errors.length() > 0)
            throw new ValidationException(errors);
    }
}
