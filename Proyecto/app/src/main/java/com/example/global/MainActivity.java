package com.example.global;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcel;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.global.databinding.ActivityMainBinding;
import com.example.global.interfaces.ActivityListener;
import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MainActivity extends AppCompatActivity  implements ActivityListener{

    ActivityMainBinding activityMainBinding;
    Context mContext;
    LatLng mLocation;
    NavController navController;
    boolean suggestFlag = false;
    boolean alreadySuggestFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        mContext = activityMainBinding.getRoot().getContext();
        View view = activityMainBinding.getRoot();
        setContentView(view);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mLocation = getLastLocation();
        }else{
            askLocationPermision();
        }

        NavHostFragment navHostFragment = activityMainBinding.fragmentContainerView.getFragment();
        navController = navHostFragment.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NotNull NavController navController, @NotNull NavDestination navDestination, @Nullable Bundle bundle) {
                if(navDestination.getId() == R.id.detalleFragment)
                    activityMainBinding.etSearch.setVisibility(View.GONE);
                if(navDestination.getId() == R.id.mainFragment) {
                    activityMainBinding.etSearch.setVisibility(View.VISIBLE);
                    alreadySuggestFlag = false;
                }
                if(navDestination.getId() == R.id.suggestFragment){
                    alreadySuggestFlag = true;
                }

            }
        });

        activityMainBinding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!suggestFlag) {
                    if(!alreadySuggestFlag) {
                        MainFragmentDirections.ActionMainFragmentToSuggestFragment actionMainFragmentToSuggestFragment = MainFragmentDirections.actionMainFragmentToSuggestFragment();
                        actionMainFragmentToSuggestFragment.setMListener(MainActivity.this);
                        actionMainFragmentToSuggestFragment.setLocation(mLocation);
                        actionMainFragmentToSuggestFragment.setTerm(charSequence.toString());
                        navController.navigate(actionMainFragmentToSuggestFragment);
                    }else{
                        SuggestFragmentDirections.ActionSuggestFragmentSelf actionSuggestFragmentSelf = SuggestFragmentDirections.actionSuggestFragmentSelf();
                        actionSuggestFragmentSelf.setMListener(MainActivity.this);
                        actionSuggestFragmentSelf.setLocation(mLocation);
                        actionSuggestFragmentSelf.setTerm(charSequence.toString());
                        navController.navigate(actionSuggestFragmentSelf);
                    }

                }else {
                    suggestFlag = false;
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        activityMainBinding.etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int action, KeyEvent keyEvent) {
                if(action == EditorInfo.IME_ACTION_SEARCH && !textView.getText().toString().isEmpty()){

                    textView.clearFocus();
                    hideKeyboard(mContext);
                    Toast.makeText(mContext, "Buscando", Toast.LENGTH_SHORT).show();

                    if(!alreadySuggestFlag) {
                        MainFragmentDirections.ActionMainFragmentSelf actionMainFragmentSelf = MainFragmentDirections.actionMainFragmentSelf();
                        actionMainFragmentSelf.setTerm(textView.getText().toString());
                        actionMainFragmentSelf.setLocation(mLocation);
                        navController.navigate(actionMainFragmentSelf);
                    }else{
                        SuggestFragmentDirections.ActionSuggestFragmentToMainFragment actionSuggestFragmentToMainFragment = SuggestFragmentDirections.actionSuggestFragmentToMainFragment();
                        actionSuggestFragmentToMainFragment.setTerm(textView.getText().toString());
                        actionSuggestFragmentToMainFragment.setLocation(mLocation);
                        navController.navigate(actionSuggestFragmentToMainFragment);
                    }

                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        navController.navigateUp();
        return super.onSupportNavigateUp();
    }

    private LatLng getLastLocation() throws SecurityException{
        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;

        for (String provider: providers){
            Location loc = locationManager.getLastKnownLocation(provider);
            if(bestLocation == null || loc.getAccuracy() < bestLocation.getAccuracy()){
                bestLocation = loc;
            }
            if(bestLocation == null){
                Toast.makeText(mContext, "no se pudo", Toast.LENGTH_SHORT).show();
                return null;
            }
            return new LatLng(bestLocation.getLatitude(), bestLocation.getLongitude());
        }
        return null;
    }
    private void askLocationPermision(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
        }
    }

    public void hideKeyboard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = activityMainBinding.getRoot();
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onSuggestClick(String suggestion) {
        suggestFlag =true;
        activityMainBinding.etSearch.setText(suggestion);
    }
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}