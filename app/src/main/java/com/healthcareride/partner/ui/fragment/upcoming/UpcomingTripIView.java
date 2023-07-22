package com.healthcareride.partner.ui.fragment.upcoming;

import com.healthcareride.partner.base.MvpView;
import com.healthcareride.partner.data.network.model.HistoryList;

import java.util.List;

public interface UpcomingTripIView extends MvpView {

    void onSuccess(List<HistoryList> historyList);
    void onError(Throwable e);
}
