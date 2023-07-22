package com.healthcareride.partner.ui.activity.help;


import com.healthcareride.partner.base.MvpPresenter;

public interface HelpIPresenter<V extends HelpIView> extends MvpPresenter<V> {

    void getHelp();
}
