package com.healthcareride.partner.ui.activity.add_card;

import com.healthcareride.partner.base.MvpPresenter;

public interface AddCardIPresenter<V extends AddCardIView> extends MvpPresenter<V> {

    void addCard(String stripeToken);
    void addStripeCode(String stripeCode);
}
