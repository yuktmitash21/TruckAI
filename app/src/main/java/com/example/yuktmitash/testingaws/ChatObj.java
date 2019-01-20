package com.example.yuktmitash.testingaws;

public class ChatObj {

    private User sender;
    private String message;

    public ChatObj() { }

    public ChatObj (User sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

