package com.healthcareride.partner.ui.activity.add_card;

import com.healthcareride.partner.base.MvpView;

public interface AddCardIView extends MvpView {

    void onSuccess(Object card);
    void onSuccessToken(Object respone);
    void onError(Throwable e);
}
