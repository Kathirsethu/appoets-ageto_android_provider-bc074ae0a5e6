package com.healthcareride.partner.ui.fragment.past;


import com.healthcareride.partner.base.MvpView;
import com.healthcareride.partner.data.network.model.HistoryList;

import java.util.List;

public interface PastTripIView extends MvpView {

    void onSuccess(List<HistoryList> historyList);
    void onError(Throwable e);
}
