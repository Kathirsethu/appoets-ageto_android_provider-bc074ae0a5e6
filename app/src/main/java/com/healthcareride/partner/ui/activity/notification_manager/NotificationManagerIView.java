package com.healthcareride.partner.ui.activity.notification_manager;

import com.healthcareride.partner.base.MvpView;
import com.healthcareride.partner.data.network.model.NotificationManager;

import java.util.List;

public interface NotificationManagerIView extends MvpView {

    void onSuccess(List<NotificationManager> managers);

    void onError(Throwable e);

}