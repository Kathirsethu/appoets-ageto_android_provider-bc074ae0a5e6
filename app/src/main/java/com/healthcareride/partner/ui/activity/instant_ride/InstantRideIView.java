package com.healthcareride.partner.ui.activity.instant_ride;

import com.healthcareride.partner.base.MvpView;
import com.healthcareride.partner.data.network.model.EstimateFare;
import com.healthcareride.partner.data.network.model.TripResponse;

public interface InstantRideIView extends MvpView {

    void onSuccess(EstimateFare estimateFare);

    void onSuccess(TripResponse response);

    void onError(Throwable e);

}
