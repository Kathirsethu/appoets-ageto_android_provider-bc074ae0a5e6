package com.healthcareride.partner.ui.activity.reset_password;

import com.healthcareride.partner.base.MvpPresenter;

import java.util.HashMap;

public interface ResetIPresenter<V extends ResetIView> extends MvpPresenter<V> {

    void reset(HashMap<String, Object> obj);

}
