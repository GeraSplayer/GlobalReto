package com.example.global;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.global.classes.Item;
import com.example.global.databinding.FragmentDetalleBinding;
import com.example.global.interfaces.NetworkCallback;
import com.example.global.network.Networking;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jetbrains.annotations.NotNull;

public class DetalleFragment extends Fragment {

    private FragmentDetalleBinding fragmentDetalleBinding;
    private GoogleMap googleMap;

    public DetalleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_detalle, container, false);
        fragmentDetalleBinding = FragmentDetalleBinding.inflate(inflater, container, false);
        return fragmentDetalleBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentDetalleBinding.mvMapa.onCreate(savedInstanceState);
        if(getArguments() != null){
            DetalleFragmentArgs args= DetalleFragmentArgs.fromBundle(getArguments());
            String id = args.getItemID();
            LatLng location = args.getLocation();

            //Search
            new Networking(view.getContext()).execute("detalle", new NetworkCallback() {
                @Override
                public void onWorkFinish(Object data) {
                    if(getActivity() == null)
                        return;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Item i = (Item) data;
                            fragmentDetalleBinding.setMItem(i);

                            fragmentDetalleBinding.mvMapa.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {

                                    LatLng coordinates = new LatLng(i.getILatitud(), i.getILongitud());
                                    googleMap.addMarker(new MarkerOptions().position(coordinates).title(i.getINombre()));
                                    if(location != null)
                                        googleMap.addMarker(new MarkerOptions().position(location).title("Yo"));
                                    if(ContextCompat.checkSelfPermission(fragmentDetalleBinding.getRoot().getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                                        googleMap.setMyLocationEnabled(true);
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15));

                                }
                            });
                        }

                    });
                }
            }, id);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        fragmentDetalleBinding.mvMapa.onResume();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        fragmentDetalleBinding.mvMapa.onDestroy();
    }
    @Override
    public void onPause() {
        super.onPause();
        fragmentDetalleBinding.mvMapa.onPause();
    }

}