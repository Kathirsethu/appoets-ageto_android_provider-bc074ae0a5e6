package com.healthcareride.partner.ui.fragment.upcoming;


import com.healthcareride.partner.base.MvpPresenter;

public interface UpcomingTripIPresenter<V extends UpcomingTripIView> extends MvpPresenter<V> {

    void getUpcoming();

}
