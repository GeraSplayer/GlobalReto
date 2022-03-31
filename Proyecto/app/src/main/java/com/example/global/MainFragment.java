package com.example.global;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.global.adapter.RecyclerAdapter;
import com.example.global.classes.Item;
import com.example.global.databinding.FragmentMainBinding;
import com.example.global.interfaces.ActivityListener;
import com.example.global.interfaces.NetworkCallback;
import com.example.global.interfaces.fragmentListener;
import com.example.global.network.Networking;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MainFragment extends Fragment implements fragmentListener {
    
    private FragmentMainBinding fragmentMainBinding;
    private NavController navController;
    private Context mContext;
    private RecyclerAdapter RecyclerAdapter;
    private List<Item> itemList;
    private String mTerm;
    private LatLng mLocation;
    private ActivityListener mListener;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_main, container, false);
        fragmentMainBinding = FragmentMainBinding.inflate(inflater, container, false);
        mContext = fragmentMainBinding.getRoot().getContext();
        return fragmentMainBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        itemList = new ArrayList<>();
        RecyclerAdapter = new RecyclerAdapter(itemList, this);
        fragmentMainBinding.rvItemList.setLayoutManager(new LinearLayoutManager(view.getContext()));
        fragmentMainBinding.rvItemList.setAdapter(RecyclerAdapter);
        fragmentMainBinding.rvItemList.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));
        RecyclerAdapter.notifyDataSetChanged();

        fragmentMainBinding.tvFirstTime.setVisibility(View.GONE);
        if(getArguments() != null){
            MainFragmentArgs args = MainFragmentArgs.fromBundle(getArguments());
            mTerm = args.getTerm();
            mLocation = args.getLocation();
            mListener = args.getListener();
            if(mTerm != null && mLocation != null) {
                getItemList(mTerm, mLocation);
            }else {
                fragmentMainBinding.tvFirstTime.setVisibility(View.VISIBLE);
            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        fragmentMainBinding.tvFirstTime.setVisibility(View.GONE);
        if(mTerm !=null && mLocation != null) {
            getItemList(mTerm, mLocation);
        }else {
            fragmentMainBinding.tvFirstTime.setVisibility(View.VISIBLE);
        }
    }

    private void getItemList(String term, LatLng location){
        new Networking(mContext).execute("search", new NetworkCallback() {
            @Override
            public void onWorkFinish(Object data) {
                if(getActivity() ==null)
                    return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(data != null) {
                            itemList.clear();
                            itemList.addAll((Collection<? extends Item>) data);
                            fragmentMainBinding.rvItemList.setVisibility(View.VISIBLE);
                            fragmentMainBinding.tvNoneResults.setVisibility(View.GONE);
                            RecyclerAdapter.notifyDataSetChanged();
                        }else {
                            itemList.clear();
                            RecyclerAdapter.notifyDataSetChanged();
                            fragmentMainBinding.rvItemList.setVisibility(View.GONE);
                            fragmentMainBinding.tvNoneResults.setVisibility(View.VISIBLE);
                        }
                        if(mListener != null)
                            mListener.hideProgressBar();
                    }
                });
            }
        }, term, location.latitude, location.longitude);
    }

    @Override
    public void onClickListener(String id) {
        mListener.onDetallesClick(MainFragmentDirections.actionMainFragmentToDetalleFragment(), id, mLocation);
    }

}