package com.timpra.barfi.Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.timpra.barfi.R;


/**
 * Fragment that allows the user to choose between going to the login or registration fragment
 */
public class MenuFragment extends Fragment implements View.OnClickListener {

    Button mLogin, mRegistration;

    Button mRegister;

    View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null)
            view = inflater.inflate(R.layout.fragment_menu, container, false);
        else
            container.removeView(view);


        return view;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeObjects();

        /*mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AuthenticationActivity.this, RegisterActivityOtp.class);
                startActivity(intent);

            }

        });*/
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.registration:
                ((AuthenticationActivity) getActivity()).registrationClick();
                break;
            case R.id.login:
                ((AuthenticationActivity) getActivity()).loginClick();
                break;
            case R.id.register:
                Intent intent = new Intent(getActivity(), RegisterActivityOtp.class);
                startActivity(intent);
                break;
        }
    }


    /**
     * initializes the design Elements
     */

    private void initializeObjects(){
        mLogin = view.findViewById(R.id.login);
        mRegistration = view.findViewById(R.id.registration);
        mRegister = view.findViewById(R.id.register);

        mRegistration.setOnClickListener(this);
        mLogin.setOnClickListener(this);
        mRegister.setOnClickListener(this);

    }
}