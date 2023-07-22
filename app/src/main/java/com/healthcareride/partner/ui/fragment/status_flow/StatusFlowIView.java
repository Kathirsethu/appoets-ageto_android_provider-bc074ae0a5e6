package com.healthcareride.partner.ui.fragment.status_flow;

import com.healthcareride.partner.base.MvpView;
import com.healthcareride.partner.data.network.model.TimerResponse;

public interface StatusFlowIView extends MvpView {

    void onSuccess(Object object);

    void onWaitingTimeSuccess(TimerResponse object);

    void onError(Throwable e);
}
