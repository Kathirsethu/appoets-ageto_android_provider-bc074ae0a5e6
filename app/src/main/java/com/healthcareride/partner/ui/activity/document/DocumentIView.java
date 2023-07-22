package com.healthcareride.partner.ui.activity.document;

import com.healthcareride.partner.base.MvpView;
import com.healthcareride.partner.data.network.model.DriverDocumentResponse;

public interface DocumentIView extends MvpView {

    void onSuccess(DriverDocumentResponse response);

    void onDocumentSuccess(DriverDocumentResponse response);

    void onError(Throwable e);

    void onSuccessLogout(Object object);

}
