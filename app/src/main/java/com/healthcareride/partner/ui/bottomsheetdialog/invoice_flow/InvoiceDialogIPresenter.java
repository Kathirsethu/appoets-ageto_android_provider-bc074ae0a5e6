package com.healthcareride.partner.ui.bottomsheetdialog.invoice_flow;

import com.healthcareride.partner.base.MvpPresenter;

import java.util.HashMap;

public interface InvoiceDialogIPresenter<V extends InvoiceDialogIView> extends MvpPresenter<V> {

    void statusUpdate(HashMap<String, Object> obj, Integer id);

}
