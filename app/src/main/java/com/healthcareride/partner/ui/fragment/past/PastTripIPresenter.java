package com.healthcareride.partner.ui.fragment.past;


import com.healthcareride.partner.base.MvpPresenter;

public interface PastTripIPresenter<V extends PastTripIView> extends MvpPresenter<V> {

    void getHistory();

}
