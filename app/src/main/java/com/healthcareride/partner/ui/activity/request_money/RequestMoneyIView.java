package com.healthcareride.partner.ui.activity.request_money;

import com.healthcareride.partner.base.MvpView;
import com.healthcareride.partner.data.network.model.RequestDataResponse;

public interface RequestMoneyIView extends MvpView {

    void onSuccess(RequestDataResponse response);
    void onSuccess(Object response);
    void onError(Throwable e);

}
