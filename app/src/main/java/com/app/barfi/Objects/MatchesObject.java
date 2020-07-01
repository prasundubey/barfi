package com.app.barfi.Objects;


public class MatchesObject {
    private String  lastMessage,
                    chatId;
    private Boolean createdByCurrentUser = false;

    UserObject mUser;

    public MatchesObject(UserObject mUser, String chatId, String lastMessage, Boolean createdByCurrentUser){
        this.mUser = mUser;
        this.lastMessage = lastMessage;
        this.chatId = chatId;
        this.createdByCurrentUser = createdByCurrentUser;
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


    public void setLastMessage(String lastMessage){
        this.lastMessage = lastMessage;
    }
    public void setChatId(String chatId){
        this.chatId = chatId;
    }
    public void setCreatedByCurrentUser(Boolean createdByCurrentUser){this.createdByCurrentUser = createdByCurrentUser;}
}
