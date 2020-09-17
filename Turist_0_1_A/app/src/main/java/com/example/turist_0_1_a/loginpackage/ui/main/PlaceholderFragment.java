package com.example.turist_0_1_a.loginpackage.ui.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.turist_0_1_a.R;
import com.example.turist_0_1_a.loginpackage.ListActivity;
import com.example.turist_0_1_a.loginpackage.TabControlActivity;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private String username;


    private PageViewModel pageViewModel;
    private int fragmentNumber;
    private float distance;
    private SeekBar distanceSeekBar;
    private EditText distanceView;
    private Switch familyFriendly;
    private boolean familyFriendlyValue;
    private Button search;
    private ImageView img;

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        fragmentNumber=index;
        pageViewModel.setIndex(index);
    }

    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_tab_control, container, false);

        img = (ImageView) root.findViewById(R.id.destination);
        if(fragmentNumber==1){
            img.setImageResource(R.drawable.hotel);
        }else if(fragmentNumber==2){
            img.setImageResource(R.drawable.park);
        }else if(fragmentNumber==3){
            img.setImageResource(R.drawable.museum);
        }else if(fragmentNumber==4){
            img.setImageResource(R.drawable.restaurant);
        }

        distance = (float) 5.0;
        distanceView = (EditText) root.findViewById(R.id.distanceView);
        distanceView.setText(distance+"km");

        distanceSeekBar = (SeekBar) root.findViewById(R.id.distanceBar);
        distanceSeekBar.setProgress(50);

        distanceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                distance= (float) (i/10.0);
                distanceView.setText(distance+"km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        familyFriendly = (Switch) root.findViewById(R.id.familySwitch);
        familyFriendly.setChecked(true);
        familyFriendlyValue = true;
        familyFriendly.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                familyFriendlyValue = b;
            }
        });

        search = (Button) root.findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(fragmentNumber==1){
                    Intent listIntent = new Intent(getActivity(), ListActivity.class);
                    listIntent.putExtra("destination","atm");
                    listIntent.putExtra("distance",distance);
                    listIntent.putExtra("family",familyFriendlyValue);
                    startActivity(listIntent);
                }else if(fragmentNumber==2){
                    Intent listIntent = new Intent(getActivity(), ListActivity.class);
                    listIntent.putExtra("destination","park");
                    listIntent.putExtra("distance",distance);
                    listIntent.putExtra("family",familyFriendlyValue);
                    startActivity(listIntent);
                }else if(fragmentNumber==3){
                    Intent listIntent = new Intent(getActivity(), ListActivity.class);
                    listIntent.putExtra("destination","museum");
                    listIntent.putExtra("distance",distance);
                    listIntent.putExtra("family",familyFriendlyValue);
                    startActivity(listIntent);
                }else if(fragmentNumber==4){
                    Intent listIntent = new Intent(getActivity(), ListActivity.class);
                    listIntent.putExtra("destination","restaurant");
                    listIntent.putExtra("distance",distance);
                    listIntent.putExtra("family",familyFriendlyValue);
                    startActivity(listIntent);
                }
            }
        });

        return root;
    }

    private void setUsername(String u){
        username=u;
    }

}