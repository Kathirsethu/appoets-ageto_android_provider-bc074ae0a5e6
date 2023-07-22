package com.healthcareride.partner.ui.activity.splash;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.healthcareride.partner.BuildConfig;
import com.healthcareride.partner.R;
import com.healthcareride.partner.base.BaseActivity;
import com.healthcareride.partner.common.Constants;
import com.healthcareride.partner.common.SharedHelper;
import com.healthcareride.partner.common.Utilities;
import com.healthcareride.partner.data.network.model.CheckVersion;
import com.healthcareride.partner.ui.activity.main.MainActivity;
import com.healthcareride.partner.ui.activity.welcome.WelcomeActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class SplashActivity extends BaseActivity implements SplashIView {

    private static final String TAG = "SplashActivity";
    private SplashPresenter<SplashActivity> presenter;

    @Override
    public int getLayoutId() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_splash;
    }

    @Override
    public void initView() {
        presenter = new SplashPresenter<>();
        presenter.attachView(this);

//        checkVersion();
        redirectToScreen();
        printHashKey();
        Utilities.printV("FCM TOKEN ===> ", deviceToken);
        Utilities.printV("FCM TOKEN ID ===> ", deviceId);
    }

    private void checkVersion() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("version", BuildConfig.VERSION_CODE);
        map.put("device_type", BuildConfig.DEVICE_TYPE);
        map.put("sender", "provider");
        presenter.checkVersion(map);

        Utilities.printV("FCM TOKEN ID===>", SharedHelper.getKeyFCM(this, "device_id"));
    }

    private void checkUserAppInstalled() {
        if (Utilities.isPackageExisted(SplashActivity.this, Constants.userAppPackageName))
            showWarningAlert(getString(R.string.user_provider_app_warning));
        else redirectToScreen();
    }

    private void showWarningAlert(String message) {
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SplashActivity.this);
            alertDialogBuilder
                    .setTitle(getResources().getString(R.string.warning))
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.continue_app),
                            (dialog, id) -> redirectToScreen());
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void verifyAppInstalled() {
        checkUserAppInstalled();
    }

    private void redirectToScreen() {
        if (SharedHelper.getKey(this, Constants.SharedPref.LOGGGED_IN).equalsIgnoreCase("true"))
            startActivity(new Intent(activity(), MainActivity.class));
        else
            startActivity(new Intent(activity(), WelcomeActivity.class));
    }

    @Override
    public void onSuccess(Object user) {

    }

    @Override
    public void onSuccess(CheckVersion version) {
        try {
            Utilities.printV("jsonObj===>", version.getForceUpdate() + "");
            if (!version.getForceUpdate())
                presenter.handlerCall();
            else
                showAlertDialog(version.getUrl());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlertDialog(String url) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(getString(R.string.update_version_message));
        builder.setPositiveButton("Update", (dialog, which) -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        });
        builder.show();
    }

    @Override
    public void onError(Throwable e) {
        hideLoading();
        if (e != null)
            onErrorBase(e);
    }

    @Override
    public void onCheckVersionError(Throwable e) {
        hideLoading();
        presenter.handlerCall();
    }

    public void printHashKey() {
        /*try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(),
                    PackageManager.GET_SIGNING_CERTIFICATES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                if (hashKey != null)
                    Log.i(TAG, "printHashKey() Hash Key: " + hashKey);
            }
        } catch (Exception e) {
            Log.e(TAG, "printHashKey()", e);
        }*/
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA");
            if (Build.VERSION.SDK_INT >= 28) {
                PackageInfo packageInfo = getPackageManager().getPackageInfo
                        (getPackageName(), PackageManager.GET_SIGNING_CERTIFICATES);
                Signature[] signatures = packageInfo.signingInfo.getApkContentsSigners();
                for (Signature signature : signatures) {
                    md.update(signature.toByteArray());
                    Log.d("KeyHash:", new String(Base64.encode(md.digest(), Base64.DEFAULT)));
                }
            } else {
                PackageInfo info = getPackageManager().getPackageInfo
                        (BuildConfig.APPLICATION_ID, PackageManager.GET_SIGNATURES);
                for (Signature signature : info.signatures) {
                    md.update(signature.toByteArray());
                    Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                }
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        NotificationManager notificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }
}
