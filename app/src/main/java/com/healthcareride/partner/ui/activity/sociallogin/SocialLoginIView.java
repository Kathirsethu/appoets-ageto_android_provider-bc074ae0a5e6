package com.healthcareride.partner.ui.activity.sociallogin;

import com.healthcareride.partner.base.MvpView;
import com.healthcareride.partner.data.network.model.Token;

public interface SocialLoginIView extends MvpView {

    void onSuccess(Token token);
    void onError(Throwable e);
}
