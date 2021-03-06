package com.example.logisticsfree.home;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.logisticsfree.MainActivity;
import com.example.logisticsfree.R;
import com.example.logisticsfree.setting.ProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment {
    private final String TAG = "SettingFragment";

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    public SettingFragment() {
        // Required empty public constructor
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Settings");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        final String[] settingItem = {"Profile", "Manage Profile", "Vehical", "Signout"};

        ListView listView = (ListView) view.findViewById(R.id.mainSetting);
        ArrayAdapter<String> listViewAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                settingItem
        ) {
            @Override
            public View getDropDownView(int position, View convertView,  ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                Log.d(TAG, "getDropDownView: " + position);
                return view;
            }
        };
        listView.setAdapter(listViewAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent intent = new Intent(getActivity(), ProfileActivity.class);
                    startActivity(intent);
                }
                /*else if (position==1){
                    Intent intent=new Intent(getActivity(), ProfileActivity.class);
                    startActivity(intent);
                }
                else if(position==2){
                    Intent intent=new Intent(getActivity(), ProfileActivity.class);
                    startActivity(intent);
                }
                */
                else if (position == 3) {

                    if (mUser != null) {
                        mAuth.signOut();
                        mUser = null;

                        startActivity(new Intent(getActivity(), MainActivity.class));
                        getActivity().finish();
                    }
                }
            }
        });

        return view;
    }

}
