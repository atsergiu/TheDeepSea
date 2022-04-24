package com.gui.domain.validators;

import com.gui.domain.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class EventValidator implements Validator<Event>{
    @Override
    public void validate(Event entity) throws ValidationException {
        StringBuilder stringBuilder = new StringBuilder();
        String errors = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date firstDate = null;
        try {
            firstDate = sdf.parse(entity.getDate());
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        Date secondDate= new Date();
        long diffInMillies =firstDate.getTime()- secondDate.getTime() ;
        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        if(diff<0)
            stringBuilder.append("Date can't be in the past!\n");
        if (entity.getDate().equals(""))
            stringBuilder.append("Date can't be empty!\n");
        if (entity.getLocation().equals(""))
            stringBuilder.append("Location can't be empty!\n");
        if (entity.getDescription().equals(""))
            stringBuilder.append("Description can't be empty!\n");
        if (entity.getName().equals(""))
            stringBuilder.append("Name can't be empty!\n");
        errors = stringBuilder.toString();
        if (errors.length() > 0)
            throw new ValidationException(errors);
    }
}
