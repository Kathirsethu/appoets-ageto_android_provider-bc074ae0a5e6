package com.healthcareride.partner.ui.activity.wallet;

import com.healthcareride.partner.base.MvpPresenter;

import java.util.HashMap;

public interface WalletIPresenter<V extends WalletIView> extends MvpPresenter<V> {

    void getWalletData();
    void addMoney(HashMap<String, Object> obj);
}
