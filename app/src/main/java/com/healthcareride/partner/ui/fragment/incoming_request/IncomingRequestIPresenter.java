package com.healthcareride.partner.ui.fragment.incoming_request;

import com.healthcareride.partner.base.MvpPresenter;

public interface IncomingRequestIPresenter<V extends IncomingRequestIView> extends MvpPresenter<V> {

    void accept(Integer id);
    void cancel(Integer id);
}
