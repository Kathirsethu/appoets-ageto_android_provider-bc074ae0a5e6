package com.healthcareride.partner.ui.bottomsheetdialog.rating;

import com.healthcareride.partner.base.MvpView;
import com.healthcareride.partner.data.network.model.Rating;

public interface RatingDialogIView extends MvpView {

    void onSuccess(Rating rating);
    void onError(Throwable e);
}
