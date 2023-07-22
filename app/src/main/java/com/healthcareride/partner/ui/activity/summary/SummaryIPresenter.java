package com.healthcareride.partner.ui.activity.summary;


import com.healthcareride.partner.base.MvpPresenter;

public interface SummaryIPresenter<V extends SummaryIView> extends MvpPresenter<V> {

    void getSummary();
}
