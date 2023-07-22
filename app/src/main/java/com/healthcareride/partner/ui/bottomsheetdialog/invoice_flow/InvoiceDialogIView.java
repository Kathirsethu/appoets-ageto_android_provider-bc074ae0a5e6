package com.healthcareride.partner.ui.bottomsheetdialog.invoice_flow;

import com.healthcareride.partner.base.MvpView;

public interface InvoiceDialogIView extends MvpView {

    void onSuccess(Object object);
    void onError(Throwable e);
}
