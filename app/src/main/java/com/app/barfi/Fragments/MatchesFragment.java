package com.app.barfi.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.barfi.Activity.MainActivity;
import com.app.barfi.Activity.PaymentActivity;
import com.app.barfi.LikesContainerActivity;
import com.app.barfi.LikesFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.app.barfi.Adapter.ChatListAdapter;
import com.app.barfi.Adapter.MatchesAdapter;
import com.app.barfi.Objects.MatchesObject;
import com.app.barfi.Objects.MessageObject;
import com.app.barfi.Objects.UserObject;
import com.app.barfi.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Displays MAthces Available to the user in two recyclerView, one for the most recent ones
 * the other for the ones with the most recent messages
 */

public class MatchesFragment extends Fragment {


    private RecyclerView.Adapter mMatchesAdapter, mChatAdapter;


    private LinearLayoutManager mChatLayoutManager;
    private RecyclerView mChat;

    private LinearLayoutManager mMatchesLayoutManager;
    private RecyclerView mMatch;




    private String currentUserID;
    private View view;

    private TextView  mNoMatches, mConversations, mNoChats;
    private LinearLayout mKeepSwiping;

    private LinearLayout mllLikes;
    private TextView mLikes;
    private String likes;
    private int countLikes;
    private Boolean premium;


    private Boolean showNoChats;

    private LinearLayout mPgs;

    public MatchesFragment() {
    }


    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_matches, container, false);

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mNoMatches = view.findViewById(R.id.no_matches_layout);
        mNoChats = view.findViewById(R.id.no_chats_layout);
        mConversations = view.findViewById(R.id.conversations);
        mKeepSwiping = view.findViewById(R.id.keepSwiping);

        mLikes = view.findViewById(R.id.likes);
        mllLikes = view.findViewById(R.id.llLikes);

        mNoMatches.setVisibility(View.VISIBLE);
        mKeepSwiping.setVisibility(View.VISIBLE);
        mConversations.setVisibility(View.GONE);

        mPgs = view.findViewById(R.id.pgsLL);
        mPgs.setVisibility(View.VISIBLE);


        getUserMatchId();
        initChats();
        initNewMatches();
        getLikes();

        DatabaseReference mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        mUserDatabase.child("status").child("level").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    if (dataSnapshot.getValue().toString().equals("premium")) {
                        premium = true;
                    } else {
                        premium = false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mllLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(premium) {
                    Intent intent = new Intent(view.getContext(), LikesContainerActivity.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(view.getContext(), PaymentActivity.class);
                    intent.putExtra("lPremium", true);
                    startActivity(intent);
                }
            }
        });

        mPgs.setVisibility(View.GONE);

        return view;
    }

    /**
     * Initializes new Matches recyclerview
     */
    private void initNewMatches(){
        mMatch = view.findViewById(R.id.match);
        mMatch.setNestedScrollingEnabled(false);
        mMatch.setHasFixedSize(false);
        mMatchesLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mMatch.setLayoutManager(mMatchesLayoutManager);
        mMatchesAdapter = new MatchesAdapter(getDataSetMatches(), getContext());
        mMatch.setAdapter(mMatchesAdapter);
    }

    /**
     * Initializes new Chats recyclerview
     */
    private void initChats(){
        mChat = view.findViewById(R.id.chat);
        mChat.setNestedScrollingEnabled(false);
        mChat.setHasFixedSize(false);
        mChatLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
        mChat.setLayoutManager(mChatLayoutManager);
        mChatAdapter = new ChatListAdapter(getDataSetChat(), getContext());
        mChat.setAdapter(mChatAdapter);


    }



    //Count no of likes
    private void getLikes() {

        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users");

        DatabaseReference connectionDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("connections");
        connectionDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                   // countLikes = (int) dataSnapshot.child("yeps").getChildrenCount();
                    countLikes=0;
                     for(DataSnapshot yeps : dataSnapshot.child("yeps").getChildren()){
                         if(!dataSnapshot.child("matches").hasChild(yeps.getKey()) && !dataSnapshot.child("rejected").hasChild(yeps.getKey())) {


                            checkAccountStatus(yeps.getKey());

                          //    countLikes++;


                         }
                     }

                     /*if (countLikes!=0) {
                         mllLikes.setVisibility(View.VISIBLE);
                         mNoMatches.setVisibility(View.GONE);

                     }else mllLikes.setVisibility(View.GONE);


                     if(countLikes>99){
                         countLikes=99;
                         likes = Integer.toString(countLikes)+"+";
                     }else likes = Integer.toString(countLikes);

                    mLikes.setText(likes);*/

                }else mllLikes.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void checkAccountStatus(String userID){
        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users");
        userDb.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //has to be the same condition as likes fragment
                if(!dataSnapshot.exists()) { return; }
                if(!dataSnapshot.child("name").exists()){return;}
                if(!dataSnapshot.child("score").exists()){return;}
                if(!dataSnapshot.child("profileImageUrl").exists()){return;}

                countLikes++;

                if (countLikes!=0) {
                    mllLikes.setVisibility(View.VISIBLE);
                    mNoMatches.setVisibility(View.GONE);

                }else mllLikes.setVisibility(View.GONE);


                if(countLikes>99){
                    countLikes=99;
                    likes = Integer.toString(countLikes)+"+";
                }else likes = Integer.toString(countLikes);

                mLikes.setText(likes);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }



    private void getUserMatchId() {

       // DatabaseReference matchDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("connections").child("matches");

        Query matchDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("connections").child("matches").orderByChild("timestamp");
        matchDb.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                showNoChats = true;

                resultsMatchesChat.clear();

                resultsMatches.clear();
                resultsChat.clear();
                mMatchesAdapter.notifyDataSetChanged();
                mChatAdapter.notifyDataSetChanged();


                if (dataSnapshot.exists()){
                    mNoMatches.setVisibility(View.GONE);
                    mKeepSwiping.setVisibility(View.GONE);
                    mConversations.setVisibility(View.VISIBLE);
                    for(DataSnapshot match : dataSnapshot.getChildren()){

                        if (match.hasChild("timestamp"))
                            showNoChats=false;

                        FetchMatchInformation(match.getKey());
                    }

                    if (showNoChats)
                        mNoChats.setVisibility(View.VISIBLE);
                    else mNoChats.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }



    private void FetchMatchInformation(String key) {
        for(int i = 0; i < resultsMatches.size(); i++){
            if(resultsMatches.get(i).getUser().getUserId().equals(key))
                return;
        }

        //test
        for(int i = 0; i < resultsMatchesChat.size(); i++){
            if(resultsMatchesChat.get(i).getUser().getUserId().equals(key))
                return;
        }

        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(key);
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){

                    String  userId = dataSnapshot.getKey(),
                            chatId = "";

                    UserObject mUser = new UserObject();
                    mUser.parseObject(dataSnapshot);


                    if(dataSnapshot.child("connections").child("matches").child(FirebaseAuth.getInstance().getUid()).child("ChatId").getValue()!=null)
                        chatId = dataSnapshot.child("connections").child("matches").child(FirebaseAuth.getInstance().getUid()).child("ChatId").getValue().toString();


                    for(int i = 0; i < resultsMatches.size(); i++){
                        if(resultsMatches.get(i).getUser().getUserId().equals(userId))
                            return;
                    }

                    //test
                    for(int i = 0; i < resultsMatchesChat.size(); i++){
                        if(resultsMatchesChat.get(i).getUser().getUserId().equals(userId))
                            return;
                    }

                    MatchesObject obj = new MatchesObject(mUser, chatId, "", false);




                    //test
                    resultsMatchesChat.add(obj);

                    resultsMatches.add(obj);
                    mMatchesAdapter.notifyDataSetChanged();

                    if (dataSnapshot.child("connections").child("matches").child(FirebaseAuth.getInstance().getUid()).hasChild("timestamp"))
                        //test
                        resultsMatches.remove(obj);

                    if(resultsMatches.isEmpty() && countLikes==0)
                        mNoMatches.setVisibility(View.VISIBLE);

                    if(!chatId.equals("")) {
                        FetchLastMessage(chatId);

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    private void FetchLastMessage(String key) {

        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Chat").child(key);
        Query query = userDb.orderByKey().limitToLast(1);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    String chatId = dataSnapshot.getRef().getKey();

                    for (DataSnapshot firstSnapshot: dataSnapshot.getChildren()) { dataSnapshot = firstSnapshot; }

                    MessageObject mMessage = new MessageObject();
                    mMessage.parseObject(dataSnapshot);



                    // mChatLayoutManager.scrollToPosition(resultsMatches.size() - 1);
                    mChat.postDelayed(() -> mChat.scrollToPosition(mChat.getAdapter().getItemCount() - 1), 1000);

                    /*

                    for(int i = 0; i < resultsMatches.size(); i++){
                        if(resultsMatches.get(i).getChatId().equals(chatId)) {

                            resultsMatches.get(i).setLastMessage(mMessage.getMessage());
                            resultsMatches.get(i).setCreatedByCurrentUser(mMessage.getCurrentUser());


                            *//*for(int j = 0; j < resultsChat.size(); j++){
                                if(resultsChat.get(j).getChatId().equals(chatId)){
                                   // resultsChat.add(j, resultsMatches.get(i));
                                   resultsMatches.get(i).setLastMessage(mMessage.getMessage());


                                    //Dot appearance query
                                    if (!mMessage.getCurrentUser())
                                        resultsMatches.get(i).setDifferentCreatedBy(true);
                                    else resultsMatches.get(i).setDifferentCreatedBy(false);

                                    mChatAdapter.notifyDataSetChanged();

                                    return;
                                }
                            }*//*

                            resultsChat.add(resultsMatches.get(i));
                            mChatAdapter.notifyDataSetChanged();
                            return;

                        }
                    }
*/
                    //test
                    for(int i = 0; i < resultsMatchesChat.size(); i++){
                        if(resultsMatchesChat.get(i).getChatId().equals(chatId)) {

                            resultsMatchesChat.get(i).setLastMessage(mMessage.getMessage());
                            resultsMatchesChat.get(i).setCreatedByCurrentUser(mMessage.getCurrentUser());
                            resultsChat.add(resultsMatchesChat.get(i));
                            mChatAdapter.notifyDataSetChanged();
                            return;

                        }
                    }





                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }




    private ArrayList<MatchesObject> resultsChat = new ArrayList<>();
    private List<MatchesObject> getDataSetChat() {
        return resultsChat;
    }

    private ArrayList<MatchesObject> resultsMatches = new ArrayList<>();
    private List<MatchesObject> getDataSetMatches() {
        return resultsMatches;
    }



    private ArrayList<MatchesObject> resultsMatchesChat = new ArrayList<>();
    private List<MatchesObject> getDataSetMatchesChat() {
        return resultsMatchesChat;
    }

}