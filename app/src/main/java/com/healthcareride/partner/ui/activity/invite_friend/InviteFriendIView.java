package com.healthcareride.partner.ui.activity.invite_friend;

import com.healthcareride.partner.base.MvpView;
import com.healthcareride.partner.data.network.model.UserResponse;

public interface InviteFriendIView extends MvpView {

    void onSuccess(UserResponse response);
    void onError(Throwable e);

}
