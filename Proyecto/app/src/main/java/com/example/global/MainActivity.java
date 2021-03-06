package com.example.global;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavDirections;
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

    private ActivityMainBinding activityMainBinding;
    private Context mContext;
    private LatLng mLocation;
    private NavController navController;
    private boolean alreadySuggestFlag = false;
    private boolean modoMapaFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        mContext = activityMainBinding.getRoot().getContext();
        View view = activityMainBinding.getRoot();
        setContentView(view);

        //Obtiene la positión actual, y pide los permisos si no los han dado
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
                if(navDestination.getId() == R.id.detalleFragment) {
                    activityMainBinding.tilSearch.setVisibility(View.GONE);
                    alreadySuggestFlag = false;
                    activityMainBinding.floatingActionButton.setVisibility(View.GONE);
                }
                if(navDestination.getId() == R.id.mainFragment) {
                    activityMainBinding.tilSearch.setVisibility(View.VISIBLE);
                    alreadySuggestFlag = false;
                    modoMapaFlag = false;
                    activityMainBinding.floatingActionButton.setVisibility(View.VISIBLE);
                }
                if(navDestination.getId() == R.id.suggestFragment){
                    alreadySuggestFlag = true;
                    activityMainBinding.floatingActionButton.setVisibility(View.GONE);
                }
                if(navDestination.getId() == R.id.modoMapaFragment){
                    activityMainBinding.tilSearch.setVisibility(View.VISIBLE);
                    alreadySuggestFlag = false;
                    modoMapaFlag = true;
                    activityMainBinding.floatingActionButton.setVisibility(View.GONE);
                }
            }
        });

        activityMainBinding.tietSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (alreadySuggestFlag)
                    changeFragment(SuggestFragmentDirections.actionSuggestFragmentSelf(), charSequence.toString(), mLocation, MainActivity.this);
                else
                if(!modoMapaFlag)
                    changeFragment(MainFragmentDirections.actionMainFragmentToSuggestFragment(), charSequence.toString(), mLocation, MainActivity.this);
                else
                    changeFragment(ModoMapaFragmentDirections.actionModoMapaFragmentToSuggestFragment(), charSequence.toString(), mLocation, MainActivity.this);
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        activityMainBinding.tietSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int action, KeyEvent keyEvent) {
                if(action == EditorInfo.IME_ACTION_SEARCH && !textView.getText().toString().isEmpty()){
                    textView.clearFocus();
                    hideKeyboard(mContext);
                    Toast.makeText(mContext, "Buscando", Toast.LENGTH_SHORT).show();

                    if(!alreadySuggestFlag)
                        if(!modoMapaFlag)
                            changeFragment(MainFragmentDirections.actionMainFragmentSelf(), textView.getText().toString(), mLocation, MainActivity.this);
                        else
                            changeFragment(ModoMapaFragmentDirections.actionModoMapaFragmentSelf(), textView.getText().toString(), mLocation, MainActivity.this);
                    else
                        if(!modoMapaFlag)
                            changeFragment(SuggestFragmentDirections.actionSuggestFragmentToMainFragment(), textView.getText().toString(), mLocation, MainActivity.this);
                        else
                            changeFragment(SuggestFragmentDirections.actionSuggestFragmentToModoMapaFragment(), textView.getText().toString(), mLocation, MainActivity.this);


                    return true;
                }
                return false;
            }
        });
        activityMainBinding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String term = activityMainBinding.tietSearch.getText().toString();
                changeFragment(MainFragmentDirections.actionMainFragmentToModoMapaFragment(), term, mLocation, MainActivity.this );
            }
        });
    }

    private void changeFragment(NavDirections action, String term, LatLng loc, ActivityListener listener){
        if(action instanceof MainFragmentDirections.ActionMainFragmentToSuggestFragment){
            ((MainFragmentDirections.ActionMainFragmentToSuggestFragment)action).setListener(listener);
            ((MainFragmentDirections.ActionMainFragmentToSuggestFragment)action).setLocation(loc);
            ((MainFragmentDirections.ActionMainFragmentToSuggestFragment)action).setTerm(term);
        }else if(action instanceof SuggestFragmentDirections.ActionSuggestFragmentSelf){
            ((SuggestFragmentDirections.ActionSuggestFragmentSelf) action).setListener(listener);
            ((SuggestFragmentDirections.ActionSuggestFragmentSelf) action).setLocation(loc);
            ((SuggestFragmentDirections.ActionSuggestFragmentSelf) action).setTerm(term);
        }
        else if(action instanceof MainFragmentDirections.ActionMainFragmentSelf){
            ((MainFragmentDirections.ActionMainFragmentSelf) action).setListener(listener);
            ((MainFragmentDirections.ActionMainFragmentSelf) action).setLocation(loc);
            ((MainFragmentDirections.ActionMainFragmentSelf) action).setTerm(term);
        }else if(action instanceof SuggestFragmentDirections.ActionSuggestFragmentToMainFragment){
            ((SuggestFragmentDirections.ActionSuggestFragmentToMainFragment) action).setListener(listener);
            ((SuggestFragmentDirections.ActionSuggestFragmentToMainFragment) action).setLocation(loc);
            ((SuggestFragmentDirections.ActionSuggestFragmentToMainFragment) action).setTerm(term);
        }
        else if(action instanceof  MainFragmentDirections.ActionMainFragmentToModoMapaFragment){
            ((MainFragmentDirections.ActionMainFragmentToModoMapaFragment)action).setListener(listener);
            ((MainFragmentDirections.ActionMainFragmentToModoMapaFragment)action).setLocation(loc);
            ((MainFragmentDirections.ActionMainFragmentToModoMapaFragment)action).setTerm(term);
        }else if(action instanceof  ModoMapaFragmentDirections.ActionModoMapaFragmentToSuggestFragment){
            ((ModoMapaFragmentDirections.ActionModoMapaFragmentToSuggestFragment)action).setListener(listener);
            ((ModoMapaFragmentDirections.ActionModoMapaFragmentToSuggestFragment)action).setLocation(loc);
            ((ModoMapaFragmentDirections.ActionModoMapaFragmentToSuggestFragment)action).setTerm(term);
        }else if(action instanceof  ModoMapaFragmentDirections.ActionModoMapaFragmentSelf){
            ((ModoMapaFragmentDirections.ActionModoMapaFragmentSelf)action).setListener(listener);
            ((ModoMapaFragmentDirections.ActionModoMapaFragmentSelf)action).setLocation(loc);
            ((ModoMapaFragmentDirections.ActionModoMapaFragmentSelf)action).setTerm(term);
        }else if(action instanceof  SuggestFragmentDirections.ActionSuggestFragmentToModoMapaFragment){
            ((SuggestFragmentDirections.ActionSuggestFragmentToModoMapaFragment)action).setListener(listener);
            ((SuggestFragmentDirections.ActionSuggestFragmentToModoMapaFragment)action).setLocation(loc);
            ((SuggestFragmentDirections.ActionSuggestFragmentToModoMapaFragment)action).setTerm(term);
        }
        else if(action instanceof MainFragmentDirections.ActionMainFragmentToDetalleFragment){
            ((MainFragmentDirections.ActionMainFragmentToDetalleFragment)action).setListener(listener);
            ((MainFragmentDirections.ActionMainFragmentToDetalleFragment)action).setLocation(loc);
            ((MainFragmentDirections.ActionMainFragmentToDetalleFragment)action).setItemID(term);
        }else if(action instanceof ModoMapaFragmentDirections.ActionModoMapaFragmentToDetalleFragment){
            ((ModoMapaFragmentDirections.ActionModoMapaFragmentToDetalleFragment)action).setListener(listener);
            ((ModoMapaFragmentDirections.ActionModoMapaFragmentToDetalleFragment)action).setItemID(term);
            ((ModoMapaFragmentDirections.ActionModoMapaFragmentToDetalleFragment)action).setLocation(loc);
        }
        navController.navigate(action);
    }

    //--Metodo para hacer que el boton de la flechita '<-' en la action bar funcione como presionar el boton de back--
    @Override
    public boolean onSupportNavigateUp() {
        navController.navigateUp();
        return super.onSupportNavigateUp();
    }

    //--Metodos para obtner la posición actual del usuario, y pide permisos en caso de no tener los permisos correspondientes--
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

    //--Metodo para ocultar el teclado cuando empiece una busqueda
    public void hideKeyboard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = activityMainBinding.getRoot();
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    //--Metodo para escuchar el evento de que hayan presionado un item la de ListView dentro del fragemnt de SuggestFragment--
    @Override
    public void onSuggestClick(String suggestion) {
        activityMainBinding.tietSearch.setText(suggestion);
    }

    @Override
    public void onDetallesClick(NavDirections action, String term, LatLng loc) {
        changeFragment(action, term, loc, MainActivity.this);
    }

    @Override
    public void hideProgressBar() {
        activityMainBinding.clProgressBar.setVisibility(View.GONE);
    }
    public void showProgressBar() {
        activityMainBinding.clProgressBar.setVisibility(View.VISIBLE);
    }

    //--describeContents y writeToParcel son metodos implementados para poder pasar la activity actual como Safe args al fragment de SuggestFragment
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}