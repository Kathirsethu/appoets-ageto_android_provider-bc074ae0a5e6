package com.healthcareride.partner.ui.activity.stripewebview;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;

import androidx.appcompat.widget.Toolbar;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.healthcareride.partner.R;
import com.healthcareride.partner.base.BaseActivity;
import com.healthcareride.partner.common.SharedHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StripeWebViewActivity extends BaseActivity {
@BindView(R.id.stripe_webview)
    WebView stripWebview;
@BindView(R.id.toolbar)
Toolbar toolbar;
    @Override
    public int getLayoutId() {
        return R.layout.activity_stripe_web_view;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.stripewebview));
        stripWebview.getSettings().setJavaScriptEnabled(true);
        stripWebview.setWebViewClient(new MyWebViewClient());
        stripWebview.setWebChromeClient(new WebChromeClient());
        stripWebview.loadUrl(SharedHelper.getKey(StripeWebViewActivity.this,"stripeUrl"));
    }

    private class MyWebViewClient extends WebViewClient
    {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            super.onPageFinished(view, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                if(request.getUrl().toString() !=null && request.getUrl().toString() .contains("https://schedule.tranxit.co/provider/stripe/account"))
                {
                    Uri uri= Uri.parse(request.getUrl().toString());
                    if(uri.getQueryParameter("code")!=null)
                    {
                        Intent intent=new Intent();
                        intent.putExtra("code",uri.getQueryParameter("code"));
                        setResult(RESULT_OK,intent);
                        finish();
                    }
                }
                return true;
            }
            return false;
        }
    }
}