package com.healthcareride.partner.ui.activity.instant_ride;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.healthcareride.partner.R;
import com.healthcareride.partner.base.BaseActivity;
import com.healthcareride.partner.common.Constants;
import com.healthcareride.partner.common.RecyclerItemClickListener;
import com.healthcareride.partner.common.SharedHelper;
import com.healthcareride.partner.data.network.model.EstimateFare;
import com.healthcareride.partner.data.network.model.TripResponse;
import com.healthcareride.partner.ui.adapter.PlacesAutoCompleteAdapter;
import com.healthcareride.partner.ui.countrypicker.Country;
import com.healthcareride.partner.ui.countrypicker.CountryPicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

import static com.healthcareride.partner.MvpApplication.DEFAULT_ZOOM;
import static com.healthcareride.partner.MvpApplication.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.healthcareride.partner.MvpApplication.mLastKnownLocation;

public class InstantRideActivity extends BaseActivity
        implements GoogleMap.OnCameraIdleListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        InstantRideIView, RecyclerItemClickListener.OnItemClickListener {

    private static final String TAG = InstantRideActivity.class.getSimpleName();

    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(new LatLng(-0, 0),
            new LatLng(0, 0));
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.etDestination)
    EditText etDestination;
    @BindView(R.id.llPhoneNumberContainer)
    LinearLayout llPhoneNumberContainer;
    @BindView(R.id.etPhoneNumber)
    EditText etPhoneNumber;
    @BindView(R.id.rvLocation)
    RecyclerView rvLocation;
    @BindView(R.id.cvLocationsContainer)
    CardView cvLocationsContainer;
    @BindView(R.id.countryImage)
    ImageView countryImage;
    @BindView(R.id.countryNumber)
    TextView tvCountryCode;
    String countryCode = "+1";
    String countryFlag = "US";
    CountryPicker mCountryPicker;
    private boolean isEnableIdle = false;
    private boolean canShowKeyboard, mLocationPermission;
    private GoogleMap mGoogleMap;
    private FusedLocationProviderClient mFusedLocation;
    private BottomSheetBehavior mBottomSheetBehavior;
    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;
    private InstantRidePresenter<InstantRideActivity> presenter = new InstantRidePresenter<>();
    private Map<String, Object> instantRide;
    private AlertDialog mDialog;

    private PlacesClient placesClient;
    private AutocompleteSessionToken token;
    private RectangularBounds bounds;

    @Override

    public int getLayoutId() {
        return R.layout.activity_instant_ride;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void initView() {
        // Initialize Places.
        Places.initialize(getApplicationContext(),
                getResources().getString(R.string.google_map_key));

        // Create a new Places client instance.
        placesClient = Places.createClient(this);

        // Create a new token for the autocomplete session. Pass this to
        // FindAutocompletePredictionsRequest, and once again when the user makes a selection
        // (for example when calling fetchPlace()).
        token = AutocompleteSessionToken.newInstance();

        // Create a RectangularBounds object.
        bounds = RectangularBounds.newInstance(BOUNDS_INDIA);

        ButterKnife.bind(this);
        presenter.attachView(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.instant_ride));

        instantRide = new HashMap<>();
        instantRide.put("service_type", SharedHelper.getIntKey(this,
                Constants.SharedPref.SERVICE_TYPE));

        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(googleMap -> {
            mGoogleMap = googleMap;
            try {
                mGoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,
                        R.raw.style_json));
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            }
            getLocationPermission();
            updateLocationUI();
            getDeviceLocation();
        });

        mBottomSheetBehavior = BottomSheetBehavior.from(cvLocationsContainer);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        rvLocation.setLayoutManager(mLinearLayoutManager);
        rvLocation.addOnItemTouchListener(new RecyclerItemClickListener(this, this));
        mAutoCompleteAdapter = new PlacesAutoCompleteAdapter(this);
        rvLocation.setAdapter(mAutoCompleteAdapter);

        etDestination.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (canShowKeyboard) hideKeyboard();
                if (!s.toString().isEmpty() && s.length() > 2 && placesClient != null) {
                    getAutocompletePredictions(s.toString());
                    if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED)
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else
                    rvLocation.setVisibility(View.GONE);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

        });

        etPhoneNumber.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (s.length() == 10) hideKeyboard();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        etPhoneNumber.setOnTouchListener((arg0, arg1) -> {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            rvLocation.setVisibility(View.GONE);
            return false;
        });

        etDestination.setOnTouchListener((arg0, arg1) -> {
            canShowKeyboard = false;
            return false;
        });

        setCountryList();
    }

    private void setLocationText(@NonNull String address, @NonNull LatLng latLng) {
        canShowKeyboard = true;
        etDestination.setText(address);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        instantRide.put("d_latitude", latLng.latitude);
        instantRide.put("d_longitude", latLng.longitude);
        instantRide.put("d_address", address);
    }

    @Override
    public void onCameraIdle() {
        try {
            CameraPosition cameraPosition = mGoogleMap.getCameraPosition();
            if (isEnableIdle) {
                String address = getAddress(cameraPosition.target);
                System.out.println("onCameraIdle " + address);
                hideKeyboard();
                setLocationText(address, cameraPosition.target);
                rvLocation.setVisibility(View.GONE);
            }
            isEnableIdle = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getDeviceLocation() {
        try {
            if (mLocationPermission) {
                Task<Location> locationResult = mFusedLocation.getLastLocation();
                locationResult.addOnSuccessListener(location -> {
                    mLastKnownLocation = location;
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                            mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                    instantRide.put("s_latitude", location.getLatitude());
                    instantRide.put("s_longitude", location.getLongitude());
                    instantRide.put("s_address",
                            getAddress(new LatLng(location.getLatitude(),
                                    location.getLongitude())));
                });
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            mLocationPermission = true;
        else ActivityCompat.requestPermissions(this, new String[]
                        {Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }

    private void updateLocationUI() {
        if (mGoogleMap == null) return;
        try {
            if (mLocationPermission) {
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                mGoogleMap.setOnCameraIdleListener(this);
            } else {
                mGoogleMap.setMyLocationEnabled(false);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermission = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermission = true;
                    updateLocationUI();
                    getDeviceLocation();
                }
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v("Google API Callback", "Connection Suspended");
        Log.v("Code", String.valueOf(i));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v("Error Code", String.valueOf(connectionResult.getErrorCode()));
        Toast.makeText(this, "API_NOT_CONNECTED", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        else {
            setResult(RESULT_CANCELED);
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.location_pick_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                if (validate()) {
                    showLoading();
                    presenter.estimateFare(instantRide);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean validate() {
        if (etPhoneNumber.getText().toString().length() > 0) {
            instantRide.put("mobile", etPhoneNumber.getText().toString());
            instantRide.put("country_code", tvCountryCode.getText().toString());
        } else {
            Toasty.error(this, getString(R.string.invalid_mobile), Toast.LENGTH_SHORT, true).show();
            return false;
        }

        if (etDestination.getText().toString().length() > 3)
            instantRide.put("d_address", etDestination.getText().toString());
        else {
            Toasty.error(this, getString(R.string.enter_destination), Toast.LENGTH_SHORT, true).show();
            return false;
        }
        return true;
    }

    @OnClick({R.id.ivResetDest, R.id.qr_scan})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivResetDest:
                etDestination.requestFocus();
                etDestination.setText(null);
                break;

            case R.id.qr_scan:
                IntentIntegrator intentIntegrator = new IntentIntegrator(this);
                intentIntegrator.setPrompt("Scan a QRcode");
                intentIntegrator.setOrientationLocked(false);
                intentIntegrator.initiateScan();
                break;
        }
    }

    @Override
    public void onSuccess(EstimateFare estimateFare) {
        hideLoading();
        showConfirmationDialog(estimateFare.getEstimatedFare(), instantRide);
    }

    @Override
    public void onSuccess(TripResponse response) {
        hideLoading();
        mDialog.dismiss();
        Intent intent = new Intent();
        intent.putExtra("doInstantRideBooked", true);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void showConfirmationDialog(double estimatedFare, Map<String, Object> params) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_instant_ride, null);

        TextView tvPickUp = view.findViewById(R.id.tvPickUp);
        TextView tvDrop = view.findViewById(R.id.tvDrop);
        TextView tvPhone = view.findViewById(R.id.tvPhone);
        TextView tvFare = view.findViewById(R.id.tvFare);

        tvPickUp.setText(Objects.requireNonNull(params.get("s_address")).toString());
        tvDrop.setText(Objects.requireNonNull(params.get("d_address")).toString());
        tvPhone.setText(new StringBuilder()
                .append(params.get("country_code"))
                .append(params.get("mobile")));
        tvFare.setText(String.valueOf(estimatedFare));

        builder.setView(view);
        mDialog = builder.create();
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        view.findViewById(R.id.tvSubmit).setOnClickListener(view1 -> {
            showLoading();
            presenter.requestInstantRide(params);
        });

        view.findViewById(R.id.tvCancel).setOnClickListener(view1 -> mDialog.dismiss());
        mDialog.show();
    }

    @Override
    public void onError(Throwable throwable) {
        hideLoading();
        if (throwable != null)
            onErrorBase(throwable);
    }

    private void setCountryList() {
        mCountryPicker = CountryPicker.newInstance("Select Country");
        List<Country> countryList = Country.getAllCountries();
        Collections.sort(countryList, (s1, s2) -> s1.getName().compareToIgnoreCase(s2.getName()));
        mCountryPicker.setCountriesList(countryList);

        setListener();
    }

    private void setListener() {
        mCountryPicker.setListener((name, code, dialCode, flagDrawableResID) -> {
            tvCountryCode.setText(dialCode);
            countryCode = dialCode;
            countryImage.setImageResource(flagDrawableResID);
            mCountryPicker.dismiss();
        });

        countryImage.setOnClickListener(v -> mCountryPicker.show(getSupportFragmentManager(),
                "COUNTRY_PICKER"));

        tvCountryCode.setOnClickListener(v -> mCountryPicker.show(getSupportFragmentManager(),
                "COUNTRY_PICKER"));

        getUserCountryInfo();
    }

    private void getUserCountryInfo() {
        Country country = getDeviceCountry(InstantRideActivity.this);
        countryImage.setImageResource(country.getFlag());
        tvCountryCode.setText(country.getDialCode());
        countryCode = country.getDialCode();
        countryFlag = country.getCode();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null)
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            else try {
                String scanResult = result.getContents().trim();
                System.out.println("RRR scanResult = " + scanResult);
                JSONObject jObject = new JSONObject(scanResult);
                etPhoneNumber.setText(jObject.optString("phone_number"));
                tvCountryCode.setText(TextUtils.isEmpty(jObject.optString("country_code"))
                        ? countryCode : jObject.optString("country_code"));
            } catch (JSONException e) {
                e.printStackTrace();
                tvCountryCode.setText(countryCode);
            }
        } else super.onActivityResult(requestCode, resultCode, data);
    }

    private void getAutocompletePredictions(String query) {
        // Use the builder to create a FindAutocompletePredictionsRequest.
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                // Call either setLocationBias() OR setLocationRestriction().
                .setLocationBias(bounds)
                //.setLocationRestriction(bounds)
                .setTypeFilter(TypeFilter.GEOCODE)
                .setSessionToken(token)
                .setQuery(query)
                .build();
        placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener((response) -> {
                    mAutoCompleteAdapter.setPredictions(response.getAutocompletePredictions());
                    mAutoCompleteAdapter.notifyDataSetChanged();
                    rvLocation.setVisibility(View.VISIBLE);
                })
                .addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                        Toast.makeText(getApplicationContext(), apiException.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onItemClick(View view, int position) {
        String locationName = mAutoCompleteAdapter.getPredictions()
                .get(position).getFullText(null).toString();
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(locationName, 1);
            if (!addresses.isEmpty()) {
                canShowKeyboard = true;
                Address address = addresses.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
                setLocationText(locationName, latLng);
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                rvLocation.setVisibility(View.GONE);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.println(Log.ERROR, TAG, e.toString());
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
