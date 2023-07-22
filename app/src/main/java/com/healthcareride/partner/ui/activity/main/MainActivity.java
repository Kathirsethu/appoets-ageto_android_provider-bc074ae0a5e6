package com.healthcareride.partner.ui.activity.main;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.healthcareride.partner.BuildConfig;
import com.healthcareride.partner.MvpApplication;
import com.healthcareride.partner.R;
import com.healthcareride.partner.base.BaseActivity;
import com.healthcareride.partner.common.ChatHeadService;
import com.healthcareride.partner.common.Constants;
import com.healthcareride.partner.common.GPSTracker;
import com.healthcareride.partner.common.LocaleHelper;
import com.healthcareride.partner.common.PolyUtil;
import com.healthcareride.partner.common.SharedHelper;
import com.healthcareride.partner.common.Utilities;
import com.healthcareride.partner.common.chat.ChatActivity;
import com.healthcareride.partner.common.fcm.MyFireBaseMessagingService;
import com.healthcareride.partner.common.swipe_button.OnStateChangeListener;
import com.healthcareride.partner.common.swipe_button.SwipeButton;
import com.healthcareride.partner.data.network.model.Help;
import com.healthcareride.partner.data.network.model.LatLngFireBaseDB;
import com.healthcareride.partner.data.network.model.SettingsResponse;
import com.healthcareride.partner.data.network.model.TripResponse;
import com.healthcareride.partner.data.network.model.UserResponse;
import com.healthcareride.partner.ui.activity.add_card.AddCardActivity;
import com.healthcareride.partner.ui.activity.document.DocumentActivity;
import com.healthcareride.partner.ui.activity.earnings.EarningsActivity;
import com.healthcareride.partner.ui.activity.help.HelpActivity;
import com.healthcareride.partner.ui.activity.instant_ride.InstantRideActivity;
import com.healthcareride.partner.ui.activity.invite.InviteActivity;
import com.healthcareride.partner.ui.activity.invite_friend.InviteFriendActivity;
import com.healthcareride.partner.ui.activity.notification_manager.NotificationManagerActivity;
import com.healthcareride.partner.ui.activity.profile.ProfileActivity;
import com.healthcareride.partner.ui.activity.setting.SettingsActivity;
import com.healthcareride.partner.ui.activity.summary.SummaryActivity;
import com.healthcareride.partner.ui.activity.wallet.WalletActivity;
import com.healthcareride.partner.ui.activity.your_trips.YourTripActivity;
import com.healthcareride.partner.ui.bottomsheetdialog.invoice_flow.InvoiceDialogFragment;
import com.healthcareride.partner.ui.bottomsheetdialog.rating.RatingDialogFragment;
import com.healthcareride.partner.ui.fragment.incoming_request.IncomingRequestFragment;
import com.healthcareride.partner.ui.fragment.offline.OfflineFragment;
import com.healthcareride.partner.ui.fragment.status_flow.StatusFlowFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.healthcareride.partner.MvpApplication.DATUM;
import static com.healthcareride.partner.MvpApplication.DEFAULT_ZOOM;
import static com.healthcareride.partner.MvpApplication.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.healthcareride.partner.MvpApplication.canGoToChatScreen;
import static com.healthcareride.partner.MvpApplication.isChatScreenOpen;
import static com.healthcareride.partner.MvpApplication.mLastKnownLocation;
import static com.healthcareride.partner.common.Constants.checkStatus.ARRIVED;
import static com.healthcareride.partner.common.Constants.checkStatus.PICKEDUP;
import static com.healthcareride.partner.common.Constants.checkStatus.STARTED;

public class MainActivity extends BaseActivity implements
        MainIView,
        NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraIdleListener,
        DirectionCallback {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int APP_PERMISSION_REQUEST = 102;
    private static final int CHATHEAD_OVERLAY_PERMISSION_REQUEST_CODE = 104;
    private static final String PICK_UP_LOCATION = "Pick Up Location";
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    @BindView(R.id.container)
    FrameLayout container;
    @BindView(R.id.menu)
    ImageView menu;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.top_layout)
    LinearLayout topLayout;
    @BindView(R.id.lnrLocationHeader)
    LinearLayout lnrLocationHeader;
    @BindView(R.id.lblLocationType)
    TextView lblLocationType;
    @BindView(R.id.lblLocationName)
    TextView lblLocationName;
    @BindView(R.id.offline_container)
    FrameLayout offlineContainer;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.gps)
    ImageView gps;
    @BindView(R.id.navigation_img)
    ImageView navigationImg;
    @BindView(R.id.sbChangeStatus)
    SwipeButton sbChangeStatus;
    TextView lblMenuName, lblMenuEmail;
    ImageView imgMenu, imgStatus;
    MainPresenter presenter = new MainPresenter();
    SupportMapFragment mapFragment;
    GoogleMap googleMap;

    private boolean doesMapLoaded;
    private Runnable r;
    private Handler h;
    private int delay = 5000;
    private String STATUS = "";
    private String CURRENT_DEST_ADDRESS = "";
    private String ACCOUNTSTATUS = "";
    private boolean mLocationPermissionGranted;
    private FusedLocationProviderClient mFusedLocation;
    private BottomSheetBehavior bottomSheetBehavior;
    private Intent gpsServiceIntent;
    //    private Intent floatingWidgetIntent;
    private DatabaseReference mProviderLocation;
    private ArrayList<LatLng> polyLinePoints;
    private Polyline mPolyline;
    private boolean canReRoute = true, canCarAnim = true, canShowChangeDestDialog = true;
    private LatLng start = null, end = null;
    private boolean doInstantRideBooked;

    // Boolean to ensure that the request for fetching the route between the source and
    // destination is attempted only once again with the API key obtained from the settings API
    // if it gets failed for the app configured API key.
    private boolean routeRequestedAgain = false;

    private String alternateApiKey;
    private LatLng sourceLatLng;
    private LatLng destLatLng;

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            HashMap<String, Object> params = new HashMap<>();
            if (mLastKnownLocation != null) {
                params.put("latitude", mLastKnownLocation.getLatitude());
                params.put("longitude", mLastKnownLocation.getLongitude());
            }
            presenter.getTrip(params);
        }
    };
    private int canMapAnimate;
    private Marker srcMarker;

    private static void startFloatingViewService(Activity activity) {
        // *** You must follow these rules when obtain the cutout(FloatingViewManager
        // .findCutoutSafeArea) ***
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // 1. 'windowLayoutInDisplayCutoutMode' do not be set to 'never'
            if (activity.getWindow().getAttributes().layoutInDisplayCutoutMode == WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER) {
                throw new RuntimeException("'windowLayoutInDisplayCutoutMode' do not be set to " +
                        "'never'");
            }
            // 2. Do not set Activity to landscape
            if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                throw new RuntimeException("Do not set Activity to landscape");
            }
        }

        final Class<? extends Service> service;
        service = ChatHeadService.class;

        final Intent intent = new Intent(activity, service);
        //intent.putExtra(ChatHeadService.EXTRA_CUTOUT_SAFE_AREA, FloatingViewManager
        // .findCutoutSafeArea(activity));
        ContextCompat.startForegroundService(activity, intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        doesMapLoaded = false;
        ButterKnife.bind(this);
        presenter.attachView(this);
        presenter.help();
        registerReceiver(myReceiver, new IntentFilter(MyFireBaseMessagingService.INTENT_FILTER));

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);

        imgMenu = headerView.findViewById(R.id.imgMenu);
        lblMenuName = headerView.findViewById(R.id.lblMenuName);

        imgStatus = headerView.findViewById(R.id.imgStatus);
        lblMenuEmail = headerView.findViewById(R.id.lblMenuEmail);

        headerView.setOnClickListener(v -> {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation
                    (this, imgMenu, ViewCompat.getTransitionName(imgMenu));
            startActivity(new Intent(MainActivity.this, ProfileActivity.class), options.toBundle());
        });

        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        bottomSheetBehavior = BottomSheetBehavior.from(container);
        bottomSheetBehavior.setHideable(false);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // Un used
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // Un used
            }
        });

        String profileUrl = getIntent().getStringExtra("avartar");
        if (profileUrl != null && !profileUrl.isEmpty())
            Glide.with(activity())
                    .load(profileUrl)
                    .apply(RequestOptions
                            .placeholderOf(R.drawable.ic_user_placeholder)
                            .dontAnimate()
                            .error(R.drawable.ic_user_placeholder))
                    .into(imgMenu);

        sbChangeStatus.setOnStateChangeListener(new OnStateChangeListener() {
            @Override
            public void onStateChange(boolean active) {
                if (active) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("service_status", "offline");
                    presenter.providerAvailable(map);
                }
            }
        });

        /*if (floatingWidgetIntent == null)
            floatingWidgetIntent = new Intent(MainActivity.this, FloatWidgetService.class);*/

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View view, float v) {

            }

            @Override
            public void onDrawerOpened(@NonNull View view) {
                presenter.getSettings();
            }

            @Override
            public void onDrawerClosed(@NonNull View view) {

            }

            @Override
            public void onDrawerStateChanged(int i) {

            }
        });

        showFloatingView(activity(), true);

    }

    @SuppressLint("NewApi")
    private void showFloatingView(Context context, boolean isShowOverlayPermission) {
        // API22以下かチェック
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            startFloatingViewService(activity());
            return;
        }

        if (Settings.canDrawOverlays(context)) {
            startFloatingViewService(activity());
            return;
        }

        if (isShowOverlayPermission) {
            final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + context.getPackageName()));
            startActivityForResult(intent, CHATHEAD_OVERLAY_PERMISSION_REQUEST_CODE);
        }
    }

    private void startCheckStatusCall() {
        try {
            h = new Handler();
            r = () -> {
                if (mLastKnownLocation != null) {
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("latitude", mLastKnownLocation.getLatitude());
                    params.put("longitude", mLastKnownLocation.getLongitude());
                    presenter.getTrip(params);
                } else if (DATUM.getStatus().equals(STARTED) || DATUM.getStatus().equals(ARRIVED)
                        || DATUM.getStatus().equals(PICKEDUP)) if (canMapAnimate % 3 == 0) {
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (LatLng latLng : polyLinePoints) builder.include(latLng);
                    LatLngBounds bounds = builder.build();
                    try {
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 250));
                    } catch (Exception e) {
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 90));
                    }
                }
                canMapAnimate++;
                h.postDelayed(r, delay);
            };
            h.postDelayed(r, delay);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != REQUEST_CHECK_SETTINGS) {
            if (requestCode == APP_PERMISSION_REQUEST && resultCode == RESULT_OK) openMap();
            else if (requestCode == Constants.INSTANT_RIDE_REQUEST_CODE) {
                if (resultCode == RESULT_OK)
                    doInstantRideBooked = data.getBooleanExtra("doInstantRideBooked", false);
                else
                    doInstantRideBooked = false;
            } else
                Toast.makeText(this, "Draw over other app permission not enable.",
                        Toast.LENGTH_SHORT).show();

            if (requestCode == CHATHEAD_OVERLAY_PERMISSION_REQUEST_CODE) {
                showFloatingView(activity(), false);
            }
        }
    }

    private void openMap() {
        /*try {
            startService(floatingWidgetIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        if (PICK_UP_LOCATION.equalsIgnoreCase(lblLocationType.getText().toString())) {
            if (DATUM.getSAddress() != null && !DATUM.getSAddress().isEmpty()) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + DATUM.getSAddress());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        } else if (DATUM.getDAddress() != null && !DATUM.getDAddress().isEmpty()) {
            try {
                PackageInfo packageInfo = getPackageManager()
                        .getPackageInfo("com.google.android.apps.maps",
                                PackageManager.GET_META_DATA);
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + DATUM.getDAddress());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage(packageInfo.packageName);
                startActivity(mapIntent);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ACCOUNTSTATUS = "";
        STATUS = "";
        // gpsServiceIntent = new Intent(this, GPSTracker.class);
        //startService(gpsServiceIntent);
        presenter.getProfile();
        HashMap<String, Object> params = new HashMap<>();
        if (mLastKnownLocation != null) {
            params.put("latitude", mLastKnownLocation.getLatitude());
            params.put("longitude", mLastKnownLocation.getLongitude());
            SharedHelper.putKey(this, Constants.SharedPref.LATITUDE,
                    String.valueOf(mLastKnownLocation.getLatitude()));
            SharedHelper.putKey(this, Constants.SharedPref.LONGITUDE,
                    String.valueOf(mLastKnownLocation.getLongitude()));
        }
        presenter.getTrip(params);
        registerReceiver(myReceiver, new IntentFilter(MyFireBaseMessagingService.INTENT_FILTER));
        startCheckStatusCall();

        NotificationManager notificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(myReceiver);
        h.removeCallbacks(r);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        /*if (floatingWidgetIntent != null)
            stopService(floatingWidgetIntent);*/
        // if (gpsServiceIntent != null) stopService(gpsServiceIntent);
    }

    private boolean isServiceRunning() {
        ActivityManager manager =
                (ActivityManager) activity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service :
                manager.getRunningServices(Integer.MAX_VALUE)) {
            if (GPSTracker.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_card:
                startActivity(new Intent(this, AddCardActivity.class));
                break;
            case R.id.nav_instant_ride:
                doInstantRideBooked = true;
                startActivityForResult(new Intent(this, InstantRideActivity.class),
                        Constants.INSTANT_RIDE_REQUEST_CODE);
                break;
            case R.id.nav_your_trips:
                startActivity(new Intent(this, YourTripActivity.class));
                break;
            case R.id.nav_earnings:
                startActivity(new Intent(this, EarningsActivity.class));
                break;
            case R.id.nav_wallet:
                startActivity(new Intent(this, WalletActivity.class));
                break;
            case R.id.nav_summary:
                startActivity(new Intent(this, SummaryActivity.class));
                break;
            case R.id.nav_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                intent.putExtra("setting", "isClick");
                startActivity(intent);
                break;
            case R.id.nav_notification:
                startActivity(new Intent(this, NotificationManagerActivity.class));
                break;
            case R.id.nav_help:
                startActivity(new Intent(this, HelpActivity.class));
                break;
            case R.id.nav_share:
                navigateToShareScreen();
                break;
            case R.id.nav_document:
                startActivity(new Intent(this, DocumentActivity.class));
                break;
            case R.id.nav_invite_referral:
                startActivity(new Intent(this, InviteActivity.class));
                break;
            case R.id.nav_invite_friends:
                startActivity(new Intent(this, InviteFriendActivity.class));
                break;
            case R.id.nav_logout:
                ShowLogoutPopUp();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public void onCameraIdle() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void onCameraMove() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setOnMapLoadedCallback(() -> {
            doesMapLoaded = true;
        });
        try {
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));
        } catch (Resources.NotFoundException e) {
            Log.d("Map:Style", "Can't find style. Error: ");
        }
        this.googleMap = googleMap;
        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();

        boolean serviceRunningStatus = isServiceRunning();

        if (serviceRunningStatus) {
            Intent serviceIntent = new Intent(this, GPSTracker.class);
            stopService(serviceIntent);
        }
        if (!serviceRunningStatus) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                activity().startService(new Intent(activity(), GPSTracker.class));
            } else {
                Intent serviceIntent = new Intent(activity(), GPSTracker.class);
                ContextCompat.startForegroundService(activity(), serviceIntent);
            }
        }
    }


    @OnClick({R.id.menu, R.id.nav_view, R.id.navigation_img, R.id.gps})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.menu:

                if (drawerLayout.isDrawerOpen(GravityCompat.START))
                    drawerLayout.closeDrawer(GravityCompat.START);
                else {
                    UserResponse user = new Gson().fromJson(SharedHelper.getKey(this, "userInfo")
                            , UserResponse.class);
                    if (user != null) {
                        SharedHelper.putKey(this, Constants.SharedPref.CURRENCY,
                                user.getCurrency());
                        Constants.Currency = SharedHelper.getKey(this,
                                Constants.SharedPref.CURRENCY);

                        lblMenuName.setText(String.format("%s %s", user.getFirstName(),
                                user.getLastName()));
                        lblMenuEmail.setText(user.getEmail());
                        SharedHelper.putKey(activity(), Constants.SharedPref.PICTURE,
                                user.getAvatar());
                        Glide.with(activity())
                                .load(BuildConfig.BASE_IMAGE_URL + user.getAvatar())
                                .apply(RequestOptions.placeholderOf(R.drawable.ic_user_placeholder)
                                        .dontAnimate()
                                        .error(R.drawable.ic_user_placeholder))
                                .into(imgMenu);
                    } else presenter.getProfile();
                    drawerLayout.openDrawer(Gravity.START);
                }

                break;
            case R.id.nav_view:
                break;
            case R.id.gps:
                if (mLastKnownLocation != null) {
                    LatLng currentLatLng = new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude());
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,
                            DEFAULT_ZOOM));
                }
                break;
            case R.id.navigation_img:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this))
                    startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName())), APP_PERMISSION_REQUEST);
                else openMap();
                break;
        }
    }

    public void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) mLocationPermissionGranted = true;
        else
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }

    private void updateLocationUI() {
        if (googleMap == null) return;
        try {
            if (mLocationPermissionGranted) {
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                googleMap.getUiSettings().setCompassEnabled(false);
                googleMap.setOnCameraMoveListener(this);
                googleMap.setOnCameraIdleListener(this);
            } else {
                googleMap.setMyLocationEnabled(false);
                googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                googleMap.getUiSettings().setCompassEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocation.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // Set the map's camera position to the current location of the device.
                        mLastKnownLocation = task.getResult();
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                                        mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude()),
                                DEFAULT_ZOOM));
                    } else {
                        Log.e("Map", "Current location is null. Using defaults.: %s",
                                task.getException());
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation,
                                DEFAULT_ZOOM));
                        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                });
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    updateLocationUI();
                    getDeviceLocation();
                }
            }
        }
    }

    private void changeFragment(Fragment fragment) {

        if (isFinishing()) return;

        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, fragment, fragment.getTag());
            transaction.commitAllowingStateLoss();
            sbChangeStatus.setVisibility(View.GONE);
        } else {
            if (IncomingRequestFragment.countDownTimer != null) {
                IncomingRequestFragment.countDownTimer.cancel();
                if (IncomingRequestFragment.mPlayer.isPlaying())
                    IncomingRequestFragment.mPlayer.stop();
            }
            container.removeAllViews();
            sbChangeStatus.collapseButton();
            sbChangeStatus.setVisibility(View.VISIBLE);
            lnrLocationHeader.setVisibility(View.GONE);
            googleMap.clear();
        }
    }

    private void offlineFragment(String s) {
        Fragment fragment = new OfflineFragment();
        Bundle b = new Bundle();
        b.putString("status", s);
        fragment.setArguments(b);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.offline_container, fragment, fragment.getTag());
        transaction.commitAllowingStateLoss();
        ACCOUNTSTATUS = "";
    }

    @Override
    public void onSuccess(SettingsResponse response) {
        SharedHelper.putKey(this, SharedHelper.GOOGLE_API_KEY, response.getApiKey());
        alternateApiKey = response.getApiKey();
        if (response.getReferral().getReferral().equalsIgnoreCase("1")) navMenuVisibility(true);
        else navMenuVisibility(false);
    }

    @Override
    public void onSettingError(Throwable e) {
        navMenuVisibility(false);
    }

    private void navMenuVisibility(boolean visibility) {
        Menu nav_Menu = navView.getMenu();
        MenuItem nav_invite_friend = nav_Menu.findItem(R.id.nav_invite_friends);
        nav_invite_friend.setVisible(visibility);
    }

    @Override
    public void onSuccess(UserResponse user) {
        if (user != null) {
            MvpApplication.userResponse = user;
            Constants.showOTP = user.getRide_otp().equals("1");
            String dd = LocaleHelper.getLanguage(this);
            if (user.getProfile() != null && user.getProfile().getLanguage() != null &&
                    !user.getProfile().getLanguage().equalsIgnoreCase(dd)) {
                LocaleHelper.setLocale(getApplicationContext(), user.getProfile().getLanguage());
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK));
            }
            lblMenuName.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));
            lblMenuEmail.setText(user.getEmail());
            SharedHelper.putKey(activity(), Constants.SharedPref.PICTURE, user.getAvatar());
            SharedHelper.putKey(activity(),"stripeUrl",user.getStripeUrl());
            SharedHelper.putKey(activity(),"stripeAccountId",user.getStripeAccId());
            Glide.with(activity())
                    .load(BuildConfig.BASE_IMAGE_URL + user.getAvatar())
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_user_placeholder)
                            .dontAnimate()
                            .error(R.drawable.ic_user_placeholder))
                    .into(imgMenu);
            SharedHelper.putKey(this, Constants.SharedPref.STRIPE_PUBLISHABLE_KEY,
                    user.getStripePublishableKey());
            SharedHelper.putKey(this, Constants.SharedPref.USER_ID, String.valueOf(user.getId()));
            SharedHelper.putKey(this, Constants.SharedPref.USER_NAME, user.getFirstName()
                    + " " + user.getLastName());
            SharedHelper.putKey(this, Constants.SharedPref.USER_AVATAR,
                    BuildConfig.BASE_IMAGE_URL + user.getAvatar());
            SharedHelper.putKey(this, Constants.SharedPref.CURRENCY, user.getCurrency());
            SharedHelper.putKey(this, Constants.SharedPref.SERVICE_TYPE,
                    user.getService().getServiceType().getId());
            SharedHelper.putKey(this, Constants.SharedPref.USER_INFO, printJSON(user));
            Constants.Currency = SharedHelper.getKey(this, Constants.SharedPref.CURRENCY);
            int card = user.getCard();
            if (card == 0) {
                Menu nav_Menu = navView.getMenu();
                nav_Menu.findItem(R.id.nav_card).setVisible(false);
            } else {
                Menu nav_Menu = navView.getMenu();
                nav_Menu.findItem(R.id.nav_card).setVisible(true);
            }
            SharedHelper.putKey(this, "card", card);

            SharedHelper.putKey(this, Constants.ReferalKey.REFERRAL_CODE,
                    user.getReferral_unique_id());
            SharedHelper.putKey(this, Constants.ReferalKey.REFERRAL_COUNT,
                    user.getReferral_count());
            SharedHelper.putKey(this, Constants.ReferalKey.REFERRAL_TEXT, user.getReferral_text());
            SharedHelper.putKey(this, Constants.ReferalKey.REFERRAL_TOTAL_TEXT,
                    user.getReferral_total_text());
        }
    }

    @Override
    public void onError(Throwable e) {
        hideLoading();
        if (e != null)
            onErrorBase(e);
    }

    @Override
    public void onSuccessLogout(Object object) {
        Utilities.LogoutApp(activity(), "");
    }

    @Override
    public void onSuccess(TripResponse response) {
        if (!response.getRequests().isEmpty() &&
                response.getRequests().get(0).getRequest().getOtp() == null)
            Constants.showOTP = false;
        String accountStatus = response.getAccountStatus();
        String serviceStatus = response.getServiceStatus();

        MvpApplication.tripResponse = response;

        if (!ACCOUNTSTATUS.equalsIgnoreCase(accountStatus)) {
            ACCOUNTSTATUS = accountStatus;
            if (accountStatus.equalsIgnoreCase(Constants.User.Account.PENDING_DOCUMENT)) {
                startActivity(new Intent(MainActivity.this, DocumentActivity.class));
                imgStatus.setImageResource(R.drawable.banner_waiting);
            } else if (accountStatus.equalsIgnoreCase(Constants.User.Account.PENDING_CARD)) {
                Intent intent = new Intent(getApplicationContext(), AddCardActivity.class);
                intent.putExtra(Constants.User.Account.PENDING_CARD, true);
                startActivity(intent);
                imgStatus.setImageResource(R.drawable.banner_waiting);
            } else if (accountStatus.equalsIgnoreCase(Constants.User.Account.ONBOARDING)) {
                offlineFragment(Constants.User.Account.ONBOARDING);
                imgStatus.setImageResource(R.drawable.banner_waiting);
            } else if (Constants.User.Account.BANNED.equalsIgnoreCase(accountStatus)) {
                offlineFragment(Constants.User.Account.BANNED);
                imgStatus.setImageResource(R.drawable.banner_banned);
            } else if (Constants.User.Account.APPROVED.equalsIgnoreCase(accountStatus)
                    && Constants.User.Service.OFFLINE.equalsIgnoreCase(serviceStatus)) {
                offlineFragment(Constants.User.Service.OFFLINE);
                imgStatus.setImageResource(R.drawable.banner_active);
            } else if (Constants.User.Account.APPROVED.equalsIgnoreCase(accountStatus)
                    && Constants.User.Service.ACTIVE.equalsIgnoreCase(serviceStatus)) {
                offlineContainer.removeAllViews();
                imgStatus.setImageResource(R.drawable.banner_active);
            } else if (Constants.User.Account.BALANCE.equalsIgnoreCase(accountStatus)
                    || Constants.User.Service.BALANCE.equalsIgnoreCase(serviceStatus)) {
                offlineFragment(Constants.User.Service.BALANCE);
                imgStatus.setImageResource(R.drawable.banner_active);
            }
        }

        if (response.getRequests().isEmpty()) {
            CURRENT_DEST_ADDRESS = "";
            googleMap.clear();
            getDeviceLocation();
            changeFlow(Constants.checkStatus.EMPTY);
        } else {
            MvpApplication.time_to_left = response.getRequests().get(0).getTimeLeftToRespond();
            DATUM = response.getRequests().get(0).getRequest();
            changeFlow(DATUM.getStatus());
        }

        if (canGoToChatScreen) {
            if (!isChatScreenOpen && DATUM != null) {
                Intent i = new Intent(MainActivity.this, ChatActivity.class);
                i.putExtra(Constants.SharedPref.REQUEST_ID, String.valueOf(DATUM.getId()));
                startActivity(i);
            }
            canGoToChatScreen = false;
        }

    }

    @Override
    public void onSuccessProviderAvailable(Object object) {
        offlineFragment("");
        sbChangeStatus.toggleState();
    }

    @Override
    public void onSuccessFCM(Object object) {
        Utilities.printV("onSuccessFCM", "onSuccessFCM");
    }

    @Override
    public void onSuccessLocationUpdate(TripResponse tripResponse) {

    }

    @Override
    public void onSuccess(Help help) {
        MvpApplication.helpNumber = help.getContactNumber();
    }

    @SuppressLint("StringFormatInvalid")
    public void changeFlow(String status) {
        MenuItem logoutItem = navView.getMenu().findItem(R.id.nav_logout);
        if (status.isEmpty() || status.equalsIgnoreCase(Constants.checkStatus.EMPTY))
            logoutItem.setEnabled(true);
        else {
            logoutItem.setEnabled(false);
        }

        System.out.println("RRR status = [" + status + "]");

        if (!DATUM.getDAddress().equalsIgnoreCase(CURRENT_DEST_ADDRESS)
                && status.equalsIgnoreCase(Constants.checkStatus.PICKEDUP))
            if (canShowChangeDestDialog && !doInstantRideBooked)
                showDestinationAlert(String.format(getString(R.string.destination_change_to),
                        DATUM.getDAddress()));

        System.out.println("RRR getProviderId = " + "loc_p_" + DATUM.getProviderId());
        switch (status) {
            case Constants.checkStatus.ACCEPTED:
            case STARTED:
            case ARRIVED:
            case Constants.checkStatus.PICKEDUP:
                String refPath = "loc_p_" + DATUM.getProviderId();
                if (!refPath.equals("loc_p_0") && mProviderLocation == null) {
                    mProviderLocation = FirebaseDatabase.getInstance().getReference()
                            .child("loc_p_" + DATUM.getProviderId());
                    updateDriverNavigation();
                }
                break;
            case Constants.checkStatus.DROPPED:
            case Constants.checkStatus.COMPLETED:
                mProviderLocation = null;
                break;
            default:
                mProviderLocation = null;
                break;
        }

        if (!STATUS.equalsIgnoreCase(status)) {
            STATUS = status;
            CURRENT_DEST_ADDRESS = DATUM.getDAddress();
            lblLocationType.setText(getString(R.string.pick_up_location));
            if (DATUM != null && DATUM.getSAddress() != null && !DATUM.getSAddress().isEmpty())
                lblLocationName.setText(DATUM.getSAddress());
            CURRENT_DEST_ADDRESS = DATUM.getDAddress();
            switch (status) {
                case Constants.checkStatus.EMPTY:
                    mProviderLocation = null;
                    changeFragment(null);
                    break;
                case Constants.checkStatus.SEARCHING:
                    changeFragment(new IncomingRequestFragment());
                    break;
                case Constants.checkStatus.ACCEPTED:
                    lnrLocationHeader.setVisibility(View.VISIBLE);
                    changeFragment(new StatusFlowFragment());
                    break;
                case STARTED:
                    lnrLocationHeader.setVisibility(View.VISIBLE);
                    changeFragment(new StatusFlowFragment());
                    break;
                case ARRIVED:
                    googleMap.clear();
                    lblLocationType.setText(getString(R.string.drop_off_location));
                    lblLocationName.setText(DATUM.getDAddress());
                    lnrLocationHeader.setVisibility(View.VISIBLE);
                    changeFragment(new StatusFlowFragment());
                    break;
                case Constants.checkStatus.PICKEDUP:
                    lblLocationType.setText(getString(R.string.drop_off_location));
                    lblLocationName.setText(DATUM.getDAddress());
                    lnrLocationHeader.setVisibility(View.VISIBLE);
                    changeFragment(new StatusFlowFragment());
                    break;
                case Constants.checkStatus.DROPPED:
                    lblLocationType.setText(getString(R.string.drop_off_location));
                    lblLocationName.setText(DATUM.getDAddress());
                    changeFragment(new InvoiceDialogFragment());
                    break;
                case Constants.checkStatus.COMPLETED:
                    lblLocationType.setText(getString(R.string.drop_off_location));
                    lblLocationName.setText(DATUM.getDAddress());
                    changeFragment(new RatingDialogFragment());
                    break;
                default:
                    break;
            }
        } else System.out.println("Opened Dialogs ==> " + hasOpenedDialogs());
    }

    private void updateDriverNavigation() {
        mProviderLocation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    LatLngFireBaseDB fbData = dataSnapshot.getValue(LatLngFireBaseDB.class);
                    assert fbData != null;
                    double lat = fbData.getLat();
                    double lng = fbData.getLng();

                    System.out.println("RRR Lat: " + lat + " Lng: " + lng);
                    if (lng > 0 && lat > 0)
                        if (STARTED.equalsIgnoreCase(DATUM.getStatus()) ||
                                ARRIVED.equalsIgnoreCase(DATUM.getStatus()) ||
                                PICKEDUP.equalsIgnoreCase(DATUM.getStatus())) {
                            LatLng source = null, destination = null;
                            switch (DATUM.getStatus()) {
                                case STARTED:
                                    source = new LatLng(lat, lng);
                                    destination = new LatLng(DATUM.getSLatitude(),
                                            DATUM.getSLongitude());
                                    break;
                                case ARRIVED:
                                case PICKEDUP:
                                    source = new LatLng(lat, lng);
                                    destination = new LatLng(DATUM.getDLatitude(),
                                            DATUM.getDLongitude());
                                    break;
                            }
                            if (polyLinePoints == null || polyLinePoints.size() < 2 || mPolyline == null)
                                drawRoute(source, destination, false);
                            else {
//                                int index = checkForReRoute(source);
//                                if (index < 0) reRoutingDelay(source, destination);
//                                else polyLineRerouting(source, index);
                            }

                            if (start != null) {
                                SharedHelper.putCurrentLocation(MainActivity.this, start);
                                end = start;
                            }
                            start = source;
                            if (end != null && canCarAnim) {
                                if (start != null && (start.latitude != end.latitude ||
                                        start.longitude != end.longitude))
                                    carAnim(srcMarker, end, start);
                            }
                        } else mProviderLocation = null;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("RRR ", "Failed to read value.", error.toException());
            }
        });
    }

    private void reRoutingDelay(LatLng source, LatLng destination) {
        if (canReRoute) {
            canReRoute = !canReRoute;
            drawRoute(source, destination, false);
            new Handler().postDelayed(() -> canReRoute = true, 8000);
        }
    }

    private void polyLineRerouting(LatLng point, int index) {
        if (index > 0) {
            polyLinePoints.subList(0, index + 1).clear();
            polyLinePoints.add(0, point);
            mPolyline.remove();

            mPolyline = googleMap.addPolyline(DirectionConverter.createPolyline
                    (this, polyLinePoints, 2, getResources().getColor(R.color.colorAccent)));

            System.out.println("RRR mPolyline = " + polyLinePoints.size());
        } else System.out.println("RRR mPolyline = Failed");
    }

    private int checkForReRoute(LatLng point) {
        if (polyLinePoints != null && polyLinePoints.size() > 0) {
            System.out.println("RRR indexOnEdgeOrPath = " +
                    new PolyUtil().indexOnEdgeOrPath(point, polyLinePoints, false, true, 100));
            //      indexOnEdgeOrPath returns -1 if the point is outside the polyline
            //      returns the index position if the point is inside the polyline
            return new PolyUtil().indexOnEdgeOrPath(point, polyLinePoints, false, true, 100);
        } else return -1;
    }

    public boolean hasOpenedDialogs() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragments)
            if (fragment != null && fragment.isVisible()) return true;
        return false;
    }

    private void showDestinationAlert(String message) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(getString(R.string.ride_updated))
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.yes),
                            (dialog, id) -> updateDestination());
            builder.create().show();
            canShowChangeDestDialog = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void carAnim(final Marker marker, final LatLng start, final LatLng end) {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(1900);
        final LatLngInterface latLngInterpolator = new LatLngInterface.LinearFixed();
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(valueAnimator1 -> {
            try {
                canCarAnim = false;
                float v = valueAnimator1.getAnimatedFraction();
                LatLng newPos = latLngInterpolator.interpolate(v, start, end);
                marker.setPosition(newPos);
                marker.setAnchor(0.5f, 0.5f);
                marker.setRotation(bearingBetweenLocations(start, end));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                canCarAnim = true;
            }
        });
        animator.start();
    }

    private double rotateMarker(double currentLat, double currentLng,
                                double nextLat, double nextLng) {
        double degToRad = Math.PI / 180.0;
        double phi1 = currentLat * degToRad;
        double phi2 = nextLat * degToRad;
        double lam1 = currentLng * degToRad;
        double lam2 = nextLng * degToRad;

        return Math.atan2(
                Math.sin(lam2 - lam1) * Math.cos(phi2),
                Math.cos(phi1) * Math.sin(phi2) - Math.sin(phi1)
                        * Math.cos(phi2) * Math.cos(lam2 - lam1))
                * 180 / Math.PI;
    }

    private void updateDestination() {
        STATUS = "";
        if (googleMap != null) googleMap.clear();
    }

    public void ShowLogoutPopUp() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage(getString(R.string.log_out_title))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok), (dialog, id) -> {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("id", SharedHelper.getKey(activity(),
                            Constants.SharedPref.USER_ID) + "");
                    presenter.logout(map);
                }).setNegativeButton(getString(R.string.cancel), (dialog, id) -> {
            String user_id = SharedHelper.getKey(activity(), Constants.SharedPref.USER_ID);
            Utilities.printV("user_id===>", user_id);
            dialog.cancel();
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void drawRoute(LatLng source, LatLng destination, boolean changeApiKey) {
        String apiKey =
                changeApiKey ? alternateApiKey : getResources().getString(R.string.google_directions_key);
        sourceLatLng = source;
        destLatLng = destination;
        if (!changeApiKey) {
            // Resetting the request again once a new request is initiated.
            routeRequestedAgain = false;
        }
        GoogleDirection.withServerKey(apiKey)
                .from(source)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
                .execute(this);
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        if (direction.isOK()) {
            googleMap.clear();
            Route route = direction.getRouteList().get(0);
            if (!route.getLegList().isEmpty()) {

                Leg startLeg = route.getLegList().get(0);
                Leg endLeg = route.getLegList().get(0);

                LatLng origin = new LatLng(startLeg.getStartLocation().getLatitude(),
                        startLeg.getStartLocation().getLongitude());
                LatLng destination = new LatLng(endLeg.getEndLocation().getLatitude(),
                        endLeg.getEndLocation().getLongitude());

                srcMarker = googleMap.addMarker(new MarkerOptions()
                        .position(origin)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ambulance)));
                googleMap.addMarker(new MarkerOptions()
                        .position(destination)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.des_icon)))
                        .setTag(endLeg);
            }

            polyLinePoints = route.getLegList().get(0).getDirectionPoint();
            mPolyline = googleMap.addPolyline(DirectionConverter.createPolyline
                    (this, polyLinePoints, 2, getResources().getColor(R.color.colorAccent)));
            if (doesMapLoaded)
                setCameraWithCoordinationBounds(route);
        } else {
            if (!routeRequestedAgain && alternateApiKey != null) {
                routeRequestedAgain = true;
                drawRoute(sourceLatLng, destLatLng, true);
            } else {
//                Log.d(TAG, direction.getErrorMessage());
                Toast.makeText(getApplicationContext(), direction.getErrorMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {
        Log.d(TAG, t.getLocalizedMessage());
        Toast.makeText(getApplicationContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }

    private void setCameraWithCoordinationBounds(Route route) {
        LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
        LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    public void navigateToShareScreen() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey Checkout this app," +
                getString(R.string.app_name) + "\nhttps://play.google.com/store/apps/details?id=" +
                BuildConfig.APPLICATION_ID);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    public float bearingBetweenLocations(LatLng latLng1, LatLng latLng2) {
        double PI = 3.14159;
        double lat1 = latLng1.latitude * PI / 180;
        double long1 = latLng1.longitude * PI / 180;
        double lat2 = latLng2.latitude * PI / 180;
        double long2 = latLng2.longitude * PI / 180;

        double dLon = (long2 - long1);
        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;
        return (float) brng;
    }

    private interface LatLngInterface {
        LatLng interpolate(float fraction, LatLng a, LatLng b);

        class LinearFixed implements LatLngInterface {
            @Override
            public LatLng interpolate(float fraction, LatLng a, LatLng b) {
                double lat = (b.latitude - a.latitude) * fraction + a.latitude;
                double lngDelta = b.longitude - a.longitude;
                if (Math.abs(lngDelta) > 180) lngDelta -= Math.signum(lngDelta) * 360;
                double lng = lngDelta * fraction + a.longitude;
                return new LatLng(lat, lng);
            }
        }
    }

}
