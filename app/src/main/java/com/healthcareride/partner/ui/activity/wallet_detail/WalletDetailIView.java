package com.healthcareride.partner.ui.activity.wallet_detail;

import com.healthcareride.partner.base.MvpView;
import com.healthcareride.partner.data.network.model.Transaction;

import java.util.ArrayList;

public interface WalletDetailIView extends MvpView {
    void setAdapter(ArrayList<Transaction> myList);
}
