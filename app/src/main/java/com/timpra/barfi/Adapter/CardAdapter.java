package com.timpra.barfi.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.timpra.barfi.Objects.UserObject;
import com.timpra.barfi.R;

import java.util.List;


/**
 * Adapter responsible for handling the display of the Cards to the user
 */
public class CardAdapter extends ArrayAdapter<UserObject>{

    Context context;

    public CardAdapter(Context context, int resourceId, List<UserObject> items){
        super(context, resourceId, items);
    }

    /**
     * Populate the item_message with user in the current position
     *
     * Changes the message aspect if it is from the current user or the match
     *
     * @param position - position of the list
     */
    public View getView(int position, View convertView, ViewGroup parent){
        UserObject card_item = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_card, parent, false);
        }



        TextView name = convertView.findViewById(R.id.name);
        ImageView image = convertView.findViewById(R.id.image);
        TextView mJob = convertView.findViewById(R.id.job);
        TextView mSchool = convertView.findViewById(R.id.school);

        name.setText(card_item.getName() + ", " + card_item.getAge());

        if(!card_item.getJob().equals(""))
        { mJob.setText(card_item.getJob());
        } else mJob.setVisibility(View.GONE);

        if(!card_item.getSchool().equals(""))
        { mSchool.setText(card_item.getSchool());
        } else mSchool.setVisibility(View.GONE);


        if(!card_item.getProfileImageUrl().equals("default"))
            Glide.with(convertView.getContext()).load(card_item.getProfileImageUrl()).into(image);



        return convertView;

    }
}
