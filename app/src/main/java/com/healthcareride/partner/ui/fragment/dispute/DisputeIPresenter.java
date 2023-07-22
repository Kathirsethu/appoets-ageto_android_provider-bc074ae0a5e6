package com.healthcareride.partner.ui.fragment.dispute;


import com.healthcareride.partner.base.MvpPresenter;

import java.util.HashMap;

public interface DisputeIPresenter<V extends DisputeIView> extends MvpPresenter<V> {
    void dispute(HashMap<String, Object> obj);
    void getDispute();
}
