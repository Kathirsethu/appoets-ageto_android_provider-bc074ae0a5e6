package com.healthcareride.partner.ui.activity.earnings;


import com.healthcareride.partner.base.MvpPresenter;

public interface EarningsIPresenter<V extends EarningsIView> extends MvpPresenter<V> {

    void getEarnings();
}
