package com.app.barfi.Objects;


public class MatchesObject {
    private String  lastMessage,
                    chatId;
    private Boolean createdByCurrentUser = false;

    private String timestamp = "";

    UserObject mUser;

    public MatchesObject(UserObject mUser, String chatId, String lastMessage, Boolean createdByCurrentUser, String timestamp){
        this.mUser = mUser;
        this.lastMessage = lastMessage;
        this.chatId = chatId;
        this.createdByCurrentUser = createdByCurrentUser;
        this.timestamp = timestamp;
    }

    public UserObject getUser() {
        return mUser;
    }

    public String getLastMessage(){
        return lastMessage;
    }
    public String getChatId(){
        return chatId;
    }
    public Boolean getCreatedByCurrentUser(){return createdByCurrentUser;}

    public String getTimestamp () { return  timestamp;}



    public void setLastMessage(String lastMessage){
        this.lastMessage = lastMessage;
    }
    public void setChatId(String chatId){
        this.chatId = chatId;
    }
    public void setCreatedByCurrentUser(Boolean createdByCurrentUser){this.createdByCurrentUser = createdByCurrentUser;}

    public void setTimestamp (String timestamp) { this.timestamp = timestamp;}
}
