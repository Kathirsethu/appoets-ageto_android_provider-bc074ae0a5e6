package com.healthcareride.partner.ui.activity.wallet_detail;

import com.healthcareride.partner.base.MvpPresenter;
import com.healthcareride.partner.data.network.model.Transaction;

import java.util.ArrayList;

public interface WalletDetailIPresenter<V extends WalletDetailIView> extends MvpPresenter<V> {
    void setAdapter(ArrayList<Transaction> myList);
}
