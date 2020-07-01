package com.app.barfi.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.app.barfi.Objects.UserObject;
import com.app.barfi.R;

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

        ImageView mVerified = convertView.findViewById(R.id.verified);

        TextView mSchool = convertView.findViewById(R.id.school);
        TextView mCity = convertView.findViewById(R.id.city);
        TextView mDegree = convertView.findViewById(R.id.degree);
        TextView mTown = convertView.findViewById(R.id.homeTown);
        TextView mStatus = convertView.findViewById(R.id.status);
        TextView mReligion = convertView.findViewById(R.id.religion);
        TextView mHeight = convertView.findViewById(R.id.height);
        TextView mZodiac = convertView.findViewById(R.id.zodiac);
        TextView mDrinking = convertView.findViewById(R.id.drinking);
        TextView mSmoking = convertView.findViewById(R.id.smoking);
        TextView mExercise = convertView.findViewById(R.id.exercise);
        TextView mPets = convertView.findViewById(R.id.pets);
        TextView mKids = convertView.findViewById(R.id.kids);
        TextView mDiet = convertView.findViewById(R.id.diet);
        TextView mPolitics = convertView.findViewById(R.id.politics);
        TextView mReading = convertView.findViewById(R.id.reading);

        name.setText(card_item.getName() + ", " + card_item.getAge());

        if (!card_item.getVerification().equals("")) {
            mVerified.setVisibility(View.VISIBLE);
        } else mVerified.setVisibility(View.GONE);

        if(!card_item.getJob().equals(""))
        { mJob.setText(card_item.getJob());
        } else mJob.setVisibility(View.GONE);

        if(!card_item.getSchool().equals(""))
        { mSchool.setText(card_item.getSchool());
        } else mSchool.setVisibility(View.GONE);


        if(!card_item.getProfileImageUrl().equals("default"))
            Glide.with(convertView.getContext()).load(card_item.getProfileImageUrl()).thumbnail(0.1f).into(image);



        if(!card_item.getDegree().equals(""))
        { mDegree.setText(card_item.getDegree());
        } else mDegree.setVisibility(View.GONE);

        if(!card_item.getSchool().equals(""))
        { mSchool.setText(card_item.getSchool());
        } else mSchool.setVisibility(View.GONE);

        if(!card_item.getCity().equals(""))
        { mCity.setText(card_item.getCity());
        } else mCity.setVisibility(View.GONE);



        if(!card_item.getTown().equals(""))
        { mTown.setText("From "+ card_item.getTown());
        } else mTown.setVisibility(View.GONE);


        // extraaa info

        if(!card_item.getStatus().equals(""))
        { mStatus.setText(card_item.getStatus());
        } else mStatus.setVisibility(View.GONE);

        if(!card_item.getReligion().equals(""))
        { mReligion.setText(card_item.getReligion());
        } else mReligion.setVisibility(View.GONE);

        if(!card_item.getHeight().equals(""))
        { mHeight.setText(card_item.getHeight());
        } else mHeight.setVisibility(View.GONE);

        if(!card_item.getZodiac().equals(""))
        { mZodiac.setText(card_item.getZodiac());
        } else mZodiac.setVisibility(View.GONE);

        if(!card_item.getDrinking().equals(""))
        { mDrinking.setText(card_item.getDrinking());
        } else mDrinking.setVisibility(View.GONE);

        if(!card_item.getSmoking().equals(""))
        { mSmoking.setText(card_item.getSmoking());
        } else mSmoking.setVisibility(View.GONE);

        if(!card_item.getExercise().equals(""))
        { mExercise.setText(card_item.getExercise());
        } else mExercise.setVisibility(View.GONE);

        if(!card_item.getPets().equals(""))
        { mPets.setText(card_item.getPets());
        } else mPets.setVisibility(View.GONE);

        if(!card_item.getKids().equals(""))
        { mKids.setText(card_item.getKids());
        } else mKids.setVisibility(View.GONE);

        if(!card_item.getDiet().equals(""))
        { mDiet.setText(card_item.getDiet());
        } else mDiet.setVisibility(View.GONE);

        if(!card_item.getPolitics().equals(""))
        { mPolitics.setText(card_item.getPolitics());
        } else mPolitics.setVisibility(View.GONE);

        if(!card_item.getReading().equals(""))
        { mReading.setText(card_item.getReading());
        } else mReading.setVisibility(View.GONE);



        return convertView;

    }
}
