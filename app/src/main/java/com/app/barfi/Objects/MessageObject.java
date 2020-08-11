package com.app.barfi.Objects;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

/**
 * Object of each message
 */

public class MessageObject {
    private String message = "";
    private Boolean currentUser = false;
    private String timestamp = "";
    private String date = "";
    private String delivery = "sending...";

    private String textKey = "";

    public MessageObject(){}

    public void parseObject(DataSnapshot dataSnapshot){
        if(!dataSnapshot.exists()){return;}

        textKey = dataSnapshot.getKey();

        if(dataSnapshot.child("text").getValue()!=null)
            message = dataSnapshot.child("text").getValue().toString();


        String createdByUser;
        if(dataSnapshot.child("createdByUser").getValue()!=null){
            createdByUser = dataSnapshot.child("createdByUser").getValue().toString();
            if(createdByUser.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                currentUser = true;
            }
        }

        if(dataSnapshot.child("timestamp").getValue()!=null)
            timestamp = dataSnapshot.child("timestamp").getValue().toString();

        if(dataSnapshot.child("date").getValue()!=null)
            date = dataSnapshot.child("date").getValue().toString();

        if(dataSnapshot.child("delivery").getValue()!=null)
            delivery = dataSnapshot.child("delivery").getValue().toString();


    }

    public String getMessage(){
        return message;
    }
    public void setMessage(String userID){
        this.message = message;
    }

    public Boolean getCurrentUser(){
        return currentUser;
    }
    public void setCurrentUser(Boolean currentUser){
        this.currentUser = currentUser;
    }


    public String getTimestamp(){
        return timestamp;
    }
    public void setTimestamp(String timestamp){this.timestamp = timestamp;}

    public String getDate(){
        return date;
    }
    public void setDate(String date){this.date = date;}

    public String getDelivery(){ return delivery; }
    public void setDelivery(String delivery){this.delivery = delivery;}

    public String getTextKey(){ return textKey; }
    public void setTextKey(String textKey){this.textKey = textKey;}

}
