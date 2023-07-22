package com.healthcareride.partner.ui.activity.forgot_password;

import com.healthcareride.partner.base.MvpView;
import com.healthcareride.partner.data.network.model.ForgotResponse;

public interface ForgotIView extends MvpView {

    void onSuccess(ForgotResponse forgotResponse);
    void onError(Throwable e);
}
