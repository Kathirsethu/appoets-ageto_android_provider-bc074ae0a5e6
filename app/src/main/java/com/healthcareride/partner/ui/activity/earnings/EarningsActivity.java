package com.healthcareride.partner.ui.activity.earnings;

import android.annotation.SuppressLint;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.healthcareride.partner.MvpApplication;
import com.healthcareride.partner.R;
import com.healthcareride.partner.base.BaseActivity;
import com.healthcareride.partner.common.CircularProgressBar;
import com.healthcareride.partner.common.Constants;
import com.healthcareride.partner.common.Utilities;
import com.healthcareride.partner.data.network.model.EarningsList;
import com.healthcareride.partner.data.network.model.Payment;
import com.healthcareride.partner.data.network.model.Ride;
import com.healthcareride.partner.ui.adapter.EarningsTripAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.healthcareride.partner.common.Utilities.animateTextView;

public class EarningsActivity extends BaseActivity implements EarningsIView {

    EarningsPresenter presenter = new EarningsPresenter();
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.earnings_bar)
    CircularProgressBar earningsBar;
    @BindView(R.id.target_txt)
    TextView targetTxt;
    @BindView(R.id.lblEarnings)
    TextView lblEarnings;
    @BindView(R.id.rides_rv)
    RecyclerView ridesRv;
    @BindView(R.id.error_image)
    ImageView errorImage;
    @BindView(R.id.errorLayout)
    RelativeLayout errorLayout;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.layout_tab)
    LinearLayout tabLayout;

    List<Ride> list = new ArrayList<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_earnings;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        presenter.attachView(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.earnings));
        ridesRv.setLayoutManager(new LinearLayoutManager(activity(), LinearLayoutManager.VERTICAL
                , false));
        ridesRv.setItemAnimator(new DefaultItemAnimator());
        ridesRv.setHasFixedSize(true);
        showLoading();
        presenter.getEarnings();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //do whatever
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSuccess(EarningsList earningsLists) {

        Utilities.printV("SIZE", earningsLists.getRides().size() + "");
        hideLoading();
        list.clear();
        list.addAll(earningsLists.getRides());
        loadAdapter(earningsLists);
    }

    @Override
    public void onError(Throwable e) {
        hideLoading();
        if (e != null)
            onErrorBase(e);
    }


    @SuppressLint("SetTextI18n")
    private void loadAdapter(EarningsList earningsLists) {

        //ProgressBar Animation
        earningsBar.setProgressWithAnimation(earningsLists.getRidesCount(), 1500);
        //Animated TextView
        animateTextView(0, earningsLists.getRidesCount(),
                Integer.parseInt(earningsLists.getTarget()), targetTxt);
        //Set Total Amount Value
        double total_price = 0;
        double tripTotalAmt = 0;
        for (Ride ride : earningsLists.getRides()) {
            Payment payment = ride.getPayment();
            if (payment != null) {
                tripTotalAmt = payment.getProviderPay();
                // tripTotalAmt = payment.getFixed() + payment.getDistance() + payment.getTax();
                total_price += tripTotalAmt;
            }
        }

        String currency = Constants.Currency;
        lblEarnings.setText(currency + " " + MvpApplication.getInstance().getNewNumberFormat(
                Double.parseDouble(String.valueOf(total_price))));

        if (list.size() > 0) {
            EarningsTripAdapter adapter = new EarningsTripAdapter(list/*, activity()*/);
            ridesRv.setAdapter(adapter);
            tabLayout.setVisibility(View.VISIBLE);
            ridesRv.setVisibility(View.VISIBLE);
            errorLayout.setVisibility(View.GONE);
        } else {
            tabLayout.setVisibility(View.GONE);
            ridesRv.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
        }
    }

}
