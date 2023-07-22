package com.healthcareride.partner.ui.activity.payment;

import com.healthcareride.partner.base.MvpPresenter;

public interface PaymentIPresenter<V extends PaymentIView> extends MvpPresenter<V> {
    void deleteCard(String cardId);

    void card();
}
