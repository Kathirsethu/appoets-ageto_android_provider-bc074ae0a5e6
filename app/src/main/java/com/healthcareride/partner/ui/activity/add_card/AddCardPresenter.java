package com.healthcareride.partner.ui.activity.add_card;

import com.healthcareride.partner.base.BasePresenter;
import com.healthcareride.partner.data.network.APIClient;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AddCardPresenter<V extends AddCardIView> extends BasePresenter<V> implements AddCardIPresenter<V> {

    @Override
    public void addCard(String stripeToken) {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .addcard(stripeToken)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccess, getMvpView()::onError));
    }

    @Override
    public void addStripeCode(String stripeCode) {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .addStripeCode(stripeCode)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccessToken, getMvpView()::onError));
    }
}
