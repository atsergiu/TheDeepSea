package com.gui.domain;

import java.time.LocalDateTime;
import java.util.List;

public class Message extends Entity<Long> {
    User from;
    Long toGroup;
    String message;
    LocalDateTime date;
    Message reply;

    public Message(User from, Long toGroup, String message, LocalDateTime date) {
        this.from = from;
        this.toGroup = toGroup;
        this.message = message;
        this.date = date;
        this.reply = null;
    }

    public Message(User from, Long toGroup, String message) {
        this.from = from;
        this.toGroup = toGroup;
        this.message = message;
//  var dat = LocalDateTime.now();
//  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        date = LocalDateTime.now();
        this.reply = null;
    }

    @Override
    public String toString() {
        return "Message{" +
                "from=" + from +
                ", to=" + toGroup +
                ", message='" + message + '\'' +
                ", date=" + date +
                ", reply=" + reply +
                '}';
    }

    public User getFrom() {
        return from;
    }

    public Long getToGroup() {
        return toGroup;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public Message getReply() {
        return reply;
    }

    public void setReply(Message reply) {
        this.reply = reply;
    }


    public String getGUIMessage(){
        StringBuilder stringBuilder = new StringBuilder();
        var date=getDate().toString().replace("T"," ");
        stringBuilder.append(getFrom().getFullName()+" at "+date+": \n");
        stringBuilder.append("\t"+getMessage());
        return stringBuilder.toString();
    }


}
