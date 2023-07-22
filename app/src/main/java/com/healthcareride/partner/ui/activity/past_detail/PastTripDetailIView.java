package com.healthcareride.partner.ui.activity.past_detail;


import com.healthcareride.partner.base.MvpView;
import com.healthcareride.partner.data.network.model.HistoryDetail;

public interface PastTripDetailIView extends MvpView {

    void onSuccess(HistoryDetail historyDetail);
    void onError(Throwable e);
}
