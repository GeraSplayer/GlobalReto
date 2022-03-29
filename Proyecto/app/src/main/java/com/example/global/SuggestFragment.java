package com.example.global;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.global.databinding.FragmentSuggestBinding;
import com.example.global.interfaces.ActivityListener;
import com.example.global.interfaces.NetworkCallback;
import com.example.global.network.Networking;
import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public class SuggestFragment extends Fragment {

    FragmentSuggestBinding fragmentSuggestBinding;
    Context mContext;
    ActivityListener mListener;
    LatLng mLocation;
    String mTerm;
    ArrayList<String> sugerencias;
    ArrayAdapter<String> adapter;

    public SuggestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        fragmentSuggestBinding = FragmentSuggestBinding.inflate(inflater, container, false);
        mContext = fragmentSuggestBinding.getRoot().getContext();
        return fragmentSuggestBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(getArguments() != null){
            SuggestFragmentArgs args = SuggestFragmentArgs.fromBundle(getArguments());
            mListener = args.getMListener();
            mLocation = args.getLocation();
            mTerm = args.getTerm();
        }

        sugerencias = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(mContext,android.R.layout.simple_list_item_1,sugerencias);
        fragmentSuggestBinding.lvSuggestions.setAdapter(adapter);
        fragmentSuggestBinding.lvSuggestions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mListener.onSuggestClick(sugerencias.get(i));
            }
        });
        getSuggestionList(mTerm, mLocation);

    }

    private void getSuggestionList(String term, LatLng location){
        new Networking(mContext).execute("autocomplete", new NetworkCallback() {
            @Override
            public void onWorkFinish(Object data) {
                if(getActivity() == null)
                    return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(data != null) {
                            sugerencias.clear();
                            sugerencias.addAll((Collection<? extends String>)data);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        }, term, location.latitude, location.longitude);
    }
}