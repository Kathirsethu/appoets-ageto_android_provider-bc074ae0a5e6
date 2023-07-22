package com.healthcareride.partner.ui.activity.notification_manager;

import com.healthcareride.partner.base.MvpPresenter;

public interface NotificationManagerIPresenter<V extends NotificationManagerIView> extends MvpPresenter<V> {
    void getNotificationManager();
}
