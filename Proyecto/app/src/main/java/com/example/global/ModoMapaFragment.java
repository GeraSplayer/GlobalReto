package com.example.global;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.global.adapter.RecyclerAdapter;
import com.example.global.classes.Item;
import com.example.global.databinding.FragmentMainBinding;
import com.example.global.databinding.FragmentModoMapaBinding;
import com.example.global.interfaces.ActivityListener;
import com.example.global.interfaces.NetworkCallback;
import com.example.global.network.Networking;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ModoMapaFragment extends Fragment {

    private FragmentModoMapaBinding fragmentModoMapaBinding;
    private Context mContext;
    private ActivityListener mListener;
    private NavController navController;
    private String mTerm;
    private LatLng mLocation;
    private List<Item> itemList;

    public ModoMapaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentModoMapaBinding = FragmentModoMapaBinding.inflate(inflater, container, false);
        mContext = fragmentModoMapaBinding.getRoot().getContext();
        return fragmentModoMapaBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentModoMapaBinding.mvModoMapa.onCreate(savedInstanceState);
        navController = Navigation.findNavController(view);
        itemList = new ArrayList<>();

        //fragmentModoMapaBinding.tvFirstTime.setVisibility(View.GONE);
        if(getArguments() != null){
            ModoMapaFragmentArgs args =  ModoMapaFragmentArgs.fromBundle(getArguments());
            mTerm = args.getTerm();
            mLocation = args.getLocation();
            mListener = args.getListener();
            if(mListener != null)
                mListener.showProgressBar();
            if(mTerm !=null && mLocation != null ) {
                if(!mTerm.equals(""))
                    getItemList(mTerm, mLocation);
                else
                    setInitMap(mLocation);
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        fragmentModoMapaBinding.mvModoMapa.onResume();

        if(mTerm !=null && mLocation != null ) {
            if(!mTerm.equals(""))
                getItemList(mTerm, mLocation);
            else
                setInitMap(mLocation);
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
                            //fragmentMainBinding.tvNoneResults.setVisibility(View.GONE);

                            fragmentModoMapaBinding.mvModoMapa.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {
                                    Marker mark = null;
                                    if(location != null) {
                                        mark = googleMap.addMarker(new MarkerOptions().position(location).title( getString(R.string.tagTu) ));
                                        if(mark != null)
                                            mark.setTag(getString(R.string.tagTu) );
                                    }
                                    if(ContextCompat.checkSelfPermission(fragmentModoMapaBinding.getRoot().getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                                        googleMap.setMyLocationEnabled(true);
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));

                                    for(int i = 0; i < itemList.size(); i++) {
                                        Item item = itemList.get(i);
                                        LatLng coordinates = new LatLng(item.getILatitud(), item.getILongitud());
                                        mark = googleMap.addMarker(new MarkerOptions()
                                                .position(coordinates)
                                                .title(item.getINombre())
                                        );
                                        if(mark != null)
                                            mark.setTag(item.getIId());

                                    }
                                    if(itemList.size() > 0) {
                                        LatLng coordinates = new LatLng(itemList.get(0).getILatitud(), itemList.get(0).getILongitud());
                                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 10));
                                    }

                                    googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                        @Override
                                        public void onInfoWindowClick(@NonNull @NotNull Marker marker) {
                                            if(marker.getTitle() == null)
                                                return;
                                            if(!marker.getTitle().equals(getString(R.string.tagTu)  )) {
                                                mListener.onDetallesClick(ModoMapaFragmentDirections.actionModoMapaFragmentToDetalleFragment(), marker.getTag().toString(), mLocation);
                                            }
                                        }
                                    });

                                }
                            });
                        }else {
                            itemList.clear();
                            //fragmentMainBinding.tvNoneResults.setVisibility(View.VISIBLE);
                        }
                        if(mListener != null)
                            mListener.hideProgressBar();
                    }
                });
            }
        }, term, location.latitude, location.longitude);
    }
    private void setInitMap(LatLng location){
        fragmentModoMapaBinding.mvModoMapa.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {
                Marker mark = null;
                if (location != null) {
                    mark = googleMap.addMarker(new MarkerOptions().position(location).title( getString(R.string.tagTu) ));
                    if (mark != null)
                        mark.setTag( getString(R.string.tagTu) );
                }
                if (ContextCompat.checkSelfPermission(fragmentModoMapaBinding.getRoot().getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    googleMap.setMyLocationEnabled(true);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
                if(mListener != null)
                    mListener.hideProgressBar();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fragmentModoMapaBinding.mvModoMapa.onDestroy();
    }
    @Override
    public void onPause() {
        super.onPause();
        fragmentModoMapaBinding.mvModoMapa.onPause();
    }
}