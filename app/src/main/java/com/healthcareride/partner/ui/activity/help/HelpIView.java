package com.healthcareride.partner.ui.activity.help;

import com.healthcareride.partner.base.MvpView;
import com.healthcareride.partner.data.network.model.Help;

public interface HelpIView extends MvpView {

    void onSuccess(Help object);

    void onError(Throwable e);
}
