package com.healthcareride.partner.ui.fragment.dispute;

import com.healthcareride.partner.base.MvpView;
import com.healthcareride.partner.data.network.model.DisputeResponse;

import java.util.List;

public interface DisputeIView extends MvpView {

    void onSuccessDispute(List<DisputeResponse> responseList);

    void onSuccess(Object object);

    void onError(Throwable e);
}
