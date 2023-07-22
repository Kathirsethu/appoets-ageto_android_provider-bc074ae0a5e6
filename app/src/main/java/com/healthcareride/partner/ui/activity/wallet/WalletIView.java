package com.healthcareride.partner.ui.activity.wallet;

import com.healthcareride.partner.base.MvpView;
import com.healthcareride.partner.data.network.model.WalletMoneyAddedResponse;
import com.healthcareride.partner.data.network.model.WalletResponse;

public interface WalletIView extends MvpView {

    void onSuccess(WalletResponse response);

    void onSuccess(WalletMoneyAddedResponse response);

    void onError(Throwable e);
}
