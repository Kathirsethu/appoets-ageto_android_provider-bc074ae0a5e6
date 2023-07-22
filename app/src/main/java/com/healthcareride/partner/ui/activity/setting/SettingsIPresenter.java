package com.healthcareride.partner.ui.activity.setting;

import com.healthcareride.partner.base.MvpPresenter;

public interface SettingsIPresenter<V extends SettingsIView> extends MvpPresenter<V> {
    void changeLanguage(String languageID);
}
