package com.app.barfi.Adapter;

import android.content.Context;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.barfi.Objects.MessageObject;
import com.app.barfi.R;

import java.util.List;

/**
 * Adapter responsible for handling the display of the messages to the user
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolders>{
    private List<MessageObject> chatList;
    private Context context;


    public MessageAdapter(List<MessageObject> matchesList, Context context){
        this.chatList = matchesList;
        this.context = context;
    }

    @Override
    public MessageViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        MessageViewHolders rcv = new MessageViewHolders(layoutView);

        return rcv;
    }


    /**
     * Populate the item_message with user in the current position
     *
     * Changes the message aspect if it is from the current user or the match
     *
     * @param holder
     * @param position - position of the list
     */
    @Override
    public void onBindViewHolder(MessageViewHolders holder, int position) {

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.mMessage.getLayoutParams();
        params.leftMargin = 20; params.rightMargin = 20;
        holder.mMessage.setLayoutParams(params);

        holder.mMessage.setText(chatList.get(position).getMessage());



        // make date visible for msgs
        if(position!=0) {

            if (!chatList.get(position).getDate().equals(chatList.get(position - 1).getDate())) {
                holder.mDate.setText(chatList.get(position).getDate());
                holder.mDate.setVisibility(View.VISIBLE);
            } else
                holder.mDate.setVisibility(View.GONE);
        }else {
            holder.mDate.setText(chatList.get(position).getDate());
            holder.mDate.setVisibility(View.VISIBLE);

        }



        if(chatList.get(position).getCurrentUser()){
            holder.mMessage.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
            holder.mLayout.setGravity(Gravity.END);
            holder.mLayout.setPadding(100,0,0,0);
            holder.mDate.setPadding(0,15,100,0);
            holder.mContainer.setBackground(ContextCompat.getDrawable(context, R.drawable.message_right));

            if(position==chatList.size()-1) {
                holder.mDelivery.setVisibility(View.VISIBLE);
                holder.mDelivery.setText(chatList.get(position).getDelivery());
            } else holder.mDelivery.setVisibility(View.GONE);

        }else{
            holder.mMessage.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
            holder.mLayout.setGravity(Gravity.START);
            holder.mLayout.setPadding(0,0,100,0);
            holder.mDate.setPadding(100,15,0,0);
            holder.mContainer.setBackground(ContextCompat.getDrawable(context, R.drawable.message_left));

            holder.mDelivery.setVisibility(View.GONE);

        }


    }


    @Override
    public int getItemCount() {
        return this.chatList.size();
    }


    class MessageViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView mMessage;
        public TextView mDate, mDelivery;
        public LinearLayout mContainer, mLayout;
        public MessageViewHolders(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mLayout = itemView.findViewById(R.id.layout);
            mMessage = itemView.findViewById(R.id.message);
            mContainer = itemView.findViewById(R.id.container);

            mDate = itemView.findViewById(R.id.date);
            mDelivery = itemView.findViewById(R.id.delivery);
        }

        @Override
        public void onClick(View view) {
        }
    }

}
