package com.gui.domain.validators;

import com.gui.domain.Friendship;

/**
 * Validator for Utilizator entity
 */
public class FriendValidator implements Validator<Friendship> {
    @Override
    public void validate(Friendship entity) throws ValidationException {
        StringBuilder stringBuilder = new StringBuilder();
        String errors = "";
        if (!entity.getStatus().equals("Accepted") && !entity.getStatus().equals("Denied") && !entity.getStatus().equals("Pending"))
            stringBuilder.append("Invalid Status\n");
        errors = stringBuilder.toString();
        if (errors.length() > 0)
            throw new ValidationException(errors);
    }
}
