package com.healthcareride.partner.ui.activity.profile;

import com.healthcareride.partner.base.MvpView;
import com.healthcareride.partner.data.network.model.UserResponse;

public interface ProfileIView extends MvpView {

    void onSuccess(UserResponse user);

    void onSuccessUpdate(UserResponse object);

    void onError(Throwable e);

    void onSuccessPhoneNumber(Object object);

    void onVerifyPhoneNumberError(Throwable e);

}
