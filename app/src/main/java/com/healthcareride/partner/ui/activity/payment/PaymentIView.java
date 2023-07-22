package com.healthcareride.partner.ui.activity.payment;

import com.healthcareride.partner.base.MvpView;
import com.healthcareride.partner.data.network.model.Card;

import java.util.List;

public interface PaymentIView extends MvpView {
    void onSuccess(Object card);

    void onSuccess(List<Card> cards);

    void onError(Throwable e);
}
