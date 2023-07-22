package com.healthcareride.partner.ui.activity.forgot_password;

import com.healthcareride.partner.base.MvpPresenter;

import java.util.HashMap;

public interface ForgotIPresenter<V extends ForgotIView> extends MvpPresenter<V> {

    void forgot(HashMap<String, Object> obj);

}
