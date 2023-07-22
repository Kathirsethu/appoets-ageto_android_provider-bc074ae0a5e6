package com.healthcareride.partner.ui.activity.password;

import com.healthcareride.partner.base.MvpView;
import com.healthcareride.partner.data.network.model.ForgotResponse;
import com.healthcareride.partner.data.network.model.User;

public interface PasswordIView extends MvpView {

    void onSuccess(ForgotResponse forgotResponse);

    void onSuccess(User object);

    void onError(Throwable e);
}
