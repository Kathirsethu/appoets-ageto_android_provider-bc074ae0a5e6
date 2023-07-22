package com.healthcareride.partner.ui.activity.change_password;

import com.healthcareride.partner.base.MvpView;

public interface ChangePasswordIView extends MvpView {


    void onSuccess(Object object);
    void onError(Throwable e);
}
