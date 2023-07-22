package com.healthcareride.partner.ui.activity.upcoming_detail;


import com.healthcareride.partner.base.MvpPresenter;

public interface UpcomingTripDetailIPresenter<V extends UpcomingTripDetailIView> extends MvpPresenter<V> {

    void getUpcomingDetail(String request_id);

}
