package com.healthcareride.partner.ui.activity.reset_password;

import com.healthcareride.partner.base.MvpView;

public interface ResetIView extends MvpView{

    void onSuccess(Object object);
    void onError(Throwable e);
}
