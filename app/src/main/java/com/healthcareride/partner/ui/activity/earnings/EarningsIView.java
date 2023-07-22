package com.healthcareride.partner.ui.activity.earnings;


import com.healthcareride.partner.base.MvpView;
import com.healthcareride.partner.data.network.model.EarningsList;

public interface EarningsIView extends MvpView {

    void onSuccess(EarningsList earningsLists);

    void onError(Throwable e);
}
