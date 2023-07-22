package com.healthcareride.partner.ui.activity.summary;


import com.healthcareride.partner.base.MvpView;
import com.healthcareride.partner.data.network.model.Summary;

public interface SummaryIView extends MvpView {

    void onSuccess(Summary object);

    void onError(Throwable e);
}
