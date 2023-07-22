package com.healthcareride.partner.ui.fragment.offline;

import com.healthcareride.partner.base.MvpView;

public interface OfflineIView extends MvpView {

    void onSuccess(Object object);
    void onError(Throwable e);
}
