package com.healthcareride.partner.ui.activity.invite_friend;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.text.Html;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.healthcareride.partner.MvpApplication;
import com.healthcareride.partner.R;
import com.healthcareride.partner.base.BaseActivity;
import com.healthcareride.partner.common.Constants;
import com.healthcareride.partner.common.SharedHelper;
import com.healthcareride.partner.data.network.model.UserResponse;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InviteFriendActivity extends BaseActivity implements InviteFriendIView {

    @BindView(R.id.invite_friend)
    TextView invite_friend;
    @BindView(R.id.referral_code)
    TextView referral_code;
    @BindView(R.id.llReferral)
    RelativeLayout referralLayout;
    @BindView(R.id.text_referral_count)
    TextView referralCountText;
    @BindView(R.id.text_referral_amount)
    TextView referralAmountText;
    private InviteFriendPresenter<InviteFriendActivity> inviteFriendPresenter =
            new InviteFriendPresenter<>();
    private StringBuilder currencyBuilder;

    @Override
    public int getLayoutId() {
        return R.layout.activity_invite_friend;
    }

    @Override
    public void initView() {
        currencyBuilder = new StringBuilder()
                .append(SharedHelper.getKey(this, Constants.SharedPref.CURRENCY))
                .append(" ");
        ButterKnife.bind(this);
        inviteFriendPresenter.attachView(this);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(getResources().getString(R.string.invite_friends));
        if (!SharedHelper.getKey(this, Constants.ReferalKey.REFERRAL_CODE).equalsIgnoreCase(""))
            updateUI();
        else inviteFriendPresenter.profile();
    }

    private void updateUI() {
        referral_code.setText(SharedHelper.getKey(this, Constants.ReferalKey.REFERRAL_CODE));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            invite_friend.setText(Html.fromHtml(SharedHelper.getKey(this,
                    Constants.ReferalKey.REFERRAL_TEXT), Html.FROM_HTML_MODE_COMPACT));
        } else {
            invite_friend.setText(Html.fromHtml(SharedHelper.getKey(this,
                    Constants.ReferalKey.REFERRAL_TEXT)));
        }
        if (MvpApplication.userResponse != null) {
            referralLayout.setVisibility(View.VISIBLE);
            referralCountText.setText(MvpApplication.userResponse.getReferral_count());
            referralAmountText.setText(new StringBuilder(currencyBuilder)
                    .append(MvpApplication.getInstance().getNewNumberFormat(
                            Double.parseDouble(MvpApplication.userResponse.getReferral_amount()))));
        } else
            referralLayout.setVisibility(View.GONE);
    }

    @Override
    public void onSuccess(UserResponse response) {
        MvpApplication.userResponse = response;
        SharedHelper.putKey(this, Constants.ReferalKey.REFERRAL_CODE,
                response.getReferral_unique_id());
        SharedHelper.putKey(this, Constants.ReferalKey.REFERRAL_COUNT,
                response.getReferral_count());
        SharedHelper.putKey(this, Constants.ReferalKey.REFERRAL_TEXT, response.getReferral_text());
        SharedHelper.putKey(this, Constants.ReferalKey.REFERRAL_TOTAL_TEXT,
                response.getReferral_total_text());
        updateUI();
    }

    @Override
    public void onError(Throwable throwable) {
        onErrorBase(throwable);
    }

    @SuppressLint("StringFormatInvalid")
    @OnClick({R.id.share})
    public void onClickAction(View view) {
        switch (view.getId()) {
            case R.id.share:
                try {
                    String appName = getString(R.string.app_name);
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, appName);
                    i.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_content_referral,
                            appName, SharedHelper.getKey(this,
                                    Constants.ReferalKey.REFERRAL_CODE)));
                    startActivity(Intent.createChooser(i, "choose one"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
