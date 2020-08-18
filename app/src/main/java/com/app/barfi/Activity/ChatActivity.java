package com.app.barfi.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.barfi.Objects.CurrentUserObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.app.barfi.Adapter.MessageAdapter;
import com.app.barfi.Objects.MessageObject;
import com.app.barfi.Objects.UserObject;
import com.app.barfi.R;
import com.app.barfi.Utils.SendNotification;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * Activity responsible for displaying chat's messages and for sending messages
 */
public class ChatActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mChatAdapter;
    private LinearLayoutManager mChatLayoutManager;

    private LinearLayout mllChat,mllSend,mllMove;
    private EditText mSendEditText;

    private ImageView mImage;

    private TextView mName;

    private TextView unMatch;

    private String currentUserID, matchId, chatId;

    DatabaseReference mDatabaseUser, mDatabaseChat;

    private ArrayList<MessageObject> resultsChat = new ArrayList<MessageObject>();

    private Boolean firstText = true;

    private ProgressBar progressBar;

    private Integer mSwitch=0;

    private TextView mTyping;

    private String matchName = "Barfi";
    private String currentUserName = "Barfi";

    private RelativeLayout rlCanvas;

    private CardView cvNotification;
    private Button mEnableNotification;
    private ImageView mCloseNotification;


    //private MyEditText mSendEditText;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        matchId = getIntent().getExtras().getString("matchId");

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("connections").child("matches").child(matchId).child("ChatId");
        mDatabaseChat = FirebaseDatabase.getInstance().getReference().child("Chat");

        progressBar = findViewById(R.id.pgsBar);
        progressBar.setVisibility(View.VISIBLE);


        cvNotification = findViewById(R.id.cvNotification);
        mEnableNotification = findViewById(R.id.enable);
        mCloseNotification = findViewById(R.id.close);
        cvNotification.setVisibility(View.GONE);


        currentUserName = ((CurrentUserObject) getApplication()).getName();
       // Toast.makeText(ChatActivity.this, "Name= "+ currentUserName, Toast.LENGTH_SHORT).show();


        unMatch = findViewById(R.id.unMatch);
        unMatch.setOnClickListener(view -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
            builder.setMessage( Html.fromHtml("<font color='#2d2d2d'>Removing will delete this profile from connection & delete the chat</font>"));
            builder.setTitle( Html.fromHtml("<font color='#2d2d2d'>Are you sure?</font>"));
            builder.setCancelable(true);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                  //  finish();
                    FirebaseDatabase.getInstance().getReference().child("Chat").child(chatId).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Users").child(matchId).child("connections").child("matches").child(currentUserID).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("connections").child("matches").child(matchId).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Users").child(matchId).child("connections").child("yeps").child(currentUserID).removeValue();


                    finish();

                    /*Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();*/
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // If user click no, then dialog box is canceled.
                    dialog.cancel();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });


        getChatId();

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(true);
        mChatLayoutManager = new LinearLayoutManager(this);
        mChatLayoutManager.setSmoothScrollbarEnabled(true);
        mRecyclerView.setLayoutManager(mChatLayoutManager);
        mChatAdapter = new MessageAdapter(resultsChat, this);
        mRecyclerView.setAdapter(mChatAdapter);


        //Push the view when the keyboard opens
        mRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    mRecyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mRecyclerView.smoothScrollToPosition(mChatAdapter.getItemCount());
                        }
                    }, 100);
                }
            }
        });




        mName = findViewById(R.id.name);
        mImage = findViewById(R.id.image);

        mSendEditText = findViewById(R.id.message);



        mllChat = findViewById(R.id.llChat);
        mllSend = findViewById(R.id.llSend);
        mllMove = findViewById(R.id.firstMove);

        rlCanvas = findViewById(R.id.rlCanvas);

        mTyping = findViewById(R.id.typing);


        mSendEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //if its not first text the database value for typing will be changed and the switch will be activated for futher value change.
                if(mSwitch == 1) {
                    mDatabaseChat.child(chatId).child(currentUserID).child("typing").setValue(true);
                    if (count == 0)
                        mDatabaseChat.child(chatId).child(currentUserID).child("typing").setValue(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });





        ImageView mSendButton = findViewById(R.id.send);
        ImageView mBack = findViewById(R.id.back);


        mBack.setOnClickListener(v -> super.onBackPressed());

        // not finishing the chat activity on back
      //  mBack.setOnClickListener(v -> super.onBackPressed());

        mSendButton.setOnClickListener(view -> sendMessage());

        getMatchInfo();


        //inflate the zoom card view on photo click
        mImage.setOnClickListener(view -> getMatchProfile());
        mName.setOnClickListener(view -> getMatchProfile());



        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                if(mSwitch==1)
                mDatabaseChat.child(chatId).child(currentUserID).child("typing").setValue(false);
                return false;
            }
        });

        mllChat.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                if(mSwitch==1)
                mDatabaseChat.child(chatId).child(currentUserID).child("typing").setValue(false);
                return false;
            }
        });


        if(!NotificationManagerCompat.from(getApplicationContext()).areNotificationsEnabled()) {
            cvNotification.setVisibility(View.VISIBLE);

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    cvNotification.setVisibility(View.GONE);
                }
            }, 5000);

        }else{
            cvNotification.setVisibility(View.GONE);
        }


        mCloseNotification.setOnClickListener(view -> cvNotification.setVisibility(View.GONE));
     //   mEnableNotification.setOnClickListener(view -> startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)));

        mEnableNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                startActivity(settingsIntent);
            }
        });


    }

    /**
     * Send new message to this chat
     *
     * Checks if text to send is empty before that.
     */
    private void sendMessage() {
        String sendMessageText = mSendEditText.getText().toString().trim();

        /*Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        String date = day + "-" + month +  "-" + year;*/

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("EE, dd MMMM yyyy");
        String date = format.format(calendar.getTime());

        if(!sendMessageText.isEmpty()){


            if(firstText) {
                FirebaseDatabase.getInstance().getReference().child("Users").child(matchId).child("connections").child("matches").child(currentUserID).child("chatted").setValue(true);
                FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("connections").child("matches").child(matchId).child("chatted").setValue(true);
                mDatabaseChat.child(chatId).child("text").setValue("");
            }



            DatabaseReference newMessageDb = mDatabaseChat.push();


            //check msg delivery
            newMessageDb.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        for (int i = 0; i < resultsChat.size(); i++) {
                            if (resultsChat.get(i).getTextKey().equals(dataSnapshot.getKey())) {
                                newMessageDb.child("delivery").setValue("sent");
                                resultsChat.get(i).setDelivery("sent");
                                mChatAdapter.notifyDataSetChanged();

                            }

                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



            Map newMessage = new HashMap();
            newMessage.put("createdByUser", currentUserID);
            newMessage.put("text", sendMessageText);
            newMessage.put("timestamp", ServerValue.TIMESTAMP);
            newMessage.put("date", date);
            newMessage.put("delivery", "sending...");


            SendNotification sendNotification = new SendNotification();
            sendNotification.SendNotification(sendMessageText, currentUserName, matchId);

            newMessageDb.setValue(newMessage);




        }
        mSendEditText.setText(null);
    }





    /**
     * Get chatId and calls the getChatMessages() after
     */
    private void getChatId(){
        mDatabaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    chatId = dataSnapshot.getValue().toString();
                    mDatabaseChat = mDatabaseChat.child(chatId);
                    getChatMessages();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Get every message of the current chat and populates the recycler view
     * by adding each MessageObject to the resultsChat and updating the adapter
     *
     */
    private void getChatMessages() {

        mDatabaseChat.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                  //  Toast.makeText(ChatActivity.this, "Value event getchatmessages", Toast.LENGTH_SHORT).show();

                    mllMove.setVisibility(View.GONE);
                    rlCanvas.setVisibility(View.VISIBLE);
                    firstText = false;
                    mSwitch =1;

                    if(dataSnapshot.child(chatId).child(matchId).child("typing").exists()){
                        if(dataSnapshot.child(chatId).child(matchId).child("typing").getValue().equals(true))
                            mTyping.setVisibility(View.VISIBLE);
                        else mTyping.setVisibility(View.GONE);
                    }else
                        mTyping.setVisibility(View.GONE);



                }
                else {
                    mllMove.setVisibility(View.VISIBLE);
                    rlCanvas.setVisibility(View.GONE);
                    firstText = true;
                    progressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        mDatabaseChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists() && dataSnapshot.hasChild("createdByUser")){

                    progressBar.setVisibility(View.GONE);

                    MessageObject mMessage = new MessageObject();
                    mMessage.parseObject(dataSnapshot);

                    resultsChat.add(mMessage);
                    mChatLayoutManager.scrollToPosition(resultsChat.size() - 1);
                    mChatAdapter.notifyDataSetChanged();

                    //makes sure the recyclerview scrolls to the newest message
                    mRecyclerView.postDelayed(() -> mRecyclerView.scrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1), 1000);


                } progressBar.setVisibility(View.GONE);

            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }



    /**
     * Fetches match info and populates the related design elements
     */
    private void getMatchInfo(){
        DatabaseReference mMatchDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(matchId);
        mMatchDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){

                    UserObject mUser = new UserObject();
                    mUser.parseObject(dataSnapshot);

                    mName.setText(mUser.getName());
                    matchName = mUser.getName();
                    if(!mUser.getProfileImageUrl().equals("default"))
                        Glide.with(getApplicationContext()).load(mUser.getProfileImageUrl()).apply(RequestOptions.circleCropTransform()).into(mImage);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    //inflates match profile through zoom card

    private void getMatchProfile() {
        DatabaseReference mMatchDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(matchId);
        mMatchDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {

                    UserObject mUser = new UserObject();
                    mUser.parseObject(dataSnapshot);
                    Intent i = new Intent(ChatActivity.this, ZoomCardActivity.class);
                    i.putExtra("UserObject", mUser);
                    i.putExtra("ShowFab", false);
                    startActivity(i);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }


    @Override
    public void onPause() {
        super.onPause();
        if(mSwitch==1)
        mDatabaseChat.child(chatId).child(currentUserID).child("typing").setValue(false);
    }


//hover move, action down, action move, scroll, up, down,ACTION_OUTSIDE

   /* @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {


        if (getCurrentFocus()!= null && ev.getAction() == MotionEvent.ACTION_MOVE) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }


        return super.dispatchTouchEvent(ev);
    }*/




    //This hides keyboard only on pressing back

   /* private void showKeyboard(){
        InputMethodManager imgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        mSendEditText.requestFocus();
    }*/




}
