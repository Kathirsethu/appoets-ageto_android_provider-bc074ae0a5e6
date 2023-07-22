package com.healthcareride.partner.ui.activity.add_card;

import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.healthcareride.partner.ui.activity.stripewebview.StripeWebViewActivity;
import com.braintreepayments.cardform.view.CardForm;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.healthcareride.partner.R;
import com.healthcareride.partner.base.BaseActivity;
import com.healthcareride.partner.common.Constants;
import com.healthcareride.partner.common.SharedHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

public class AddCardActivity extends BaseActivity implements AddCardIView {

    /**
     * Test card
     * 5200 8282 8282 8210
     * 4000 0566 5566 5556
     */
    @BindView(R.id.card_form)
    CardForm cardForm;
    @BindView(R.id.submit)
    Button submit;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private AddCardPresenter<AddCardActivity> presenter = new AddCardPresenter<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_add_card;
    }

    @Override
    public void initView() {
        String title = getResources().getString(R.string.change_card_for_payments);
        if (getIntent() != null && getIntent().hasExtra(Constants.User.Account.PENDING_CARD)) {
            if (getIntent().getBooleanExtra(Constants.User.Account.PENDING_CARD, false))
                title = getResources().getString(R.string.add_card_details);
        }
        ButterKnife.bind(this);
        presenter.attachView(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(title);
      /*  cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .postalCodeRequired(false)
                .mobileNumberRequired(false)
                .actionLabel(getString(R.string.add_card_details))
                .setup(this);*/
    }

    @OnClick({R.id.submit,R.id.add_card})
    public void onViewClicked(View view) {
        switch (view.getId())
        {
            case R.id.submit:
                if (cardForm.getCardNumber().isEmpty()) {
                    Toast.makeText(this, getString(R.string.please_enter_card_number),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (cardForm.getExpirationMonth().isEmpty()) {
                    Toast.makeText(this, getString(R.string.please_enter_card_expiration_details),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (cardForm.getCvv().isEmpty()) {
                    Toast.makeText(this, getString(R.string.please_enter_card_cvv), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!TextUtils.isDigitsOnly(cardForm.getExpirationMonth()) || !TextUtils.isDigitsOnly(cardForm.getExpirationYear())) {
                    Toast.makeText(this, getString(R.string.please_enter_card_expiration_details),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                String cardNumber = cardForm.getCardNumber();
                int cardMonth = Integer.parseInt(cardForm.getExpirationMonth());
                int cardYear = Integer.parseInt(cardForm.getExpirationYear());
                String cardCvv = cardForm.getCvv();
                Log.d("CARD",
                        "CardDetails Number: " + cardNumber + "Month: " + cardMonth + " Year: " + cardYear + " Cvv " + cardCvv);
                Card card = new Card(cardNumber, cardMonth, cardYear, cardCvv);
                card.setCurrency("usd");
                if (TextUtils.isEmpty(SharedHelper.getKey(this,
                        Constants.SharedPref.STRIPE_PUBLISHABLE_KEY)))
                    showAToast(getString(R.string.stripe_key_missing));
                else addCard(card);
                break;
            case R.id.add_card:
                Intent intent=new Intent(AddCardActivity.this, StripeWebViewActivity.class);
                startActivityForResult(intent,100);
        }


    }

    @Override
    public void onSuccess(Object card) {
        hideLoading();
        Toast.makeText(this, getString(R.string.card_added), Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onSuccessToken(Object respone) {
        hideLoading();
        Toast.makeText(this, getString(R.string.card_added), Toast.LENGTH_SHORT).show();
        finish();
    }


    @Override
    public void onError(Throwable e) {
        hideLoading();
        if (e != null)
            onErrorBase(e);
    }

    private void addCard(Card card) {
        showLoading();
        Stripe stripe = new Stripe(this, SharedHelper.getKey(this,
                Constants.SharedPref.STRIPE_PUBLISHABLE_KEY));
        stripe.createToken(card,
                new TokenCallback() {
                    public void onSuccess(Token token) {
                        hideLoading();
                        Log.d("CARD:", " " + token.getId());
                        Log.d("CARD:", " " + token.getCard().getLast4());
                        String stripeToken = token.getId();
                        showLoading();
                        presenter.addCard(stripeToken);
                    }

                    public void onError(Exception error) {
                        hideLoading();
                        Toasty.error(getApplicationContext(), error.getLocalizedMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        presenter.detachView();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100)
        {
            if(data!=null)
            {
                Log.d("dfgsfdgcode",data.getStringExtra("code"));
                presenter.addStripeCode(data.getStringExtra("code"));
                showLoading();
            }
        }
    }
}
