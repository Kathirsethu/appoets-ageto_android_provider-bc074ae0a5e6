package com.healthcareride.partner.ui.activity.main;

import com.healthcareride.partner.base.MvpView;
import com.healthcareride.partner.data.network.model.Help;
import com.healthcareride.partner.data.network.model.SettingsResponse;
import com.healthcareride.partner.data.network.model.TripResponse;
import com.healthcareride.partner.data.network.model.UserResponse;

public interface MainIView extends MvpView {
    void onSuccess(UserResponse user);

    void onError(Throwable e);

    void onSuccessLogout(Object object);

    void onSuccess(TripResponse tripResponse);

    void onSuccess(SettingsResponse response);

    void onSettingError(Throwable e);

    void onSuccessProviderAvailable(Object object);

    void onSuccessFCM(Object object);

    void onSuccessLocationUpdate(TripResponse tripResponse);

    void onSuccess(Help help);
}
