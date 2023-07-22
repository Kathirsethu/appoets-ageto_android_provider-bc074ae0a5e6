package com.healthcareride.partner.ui.fragment.dispute_status;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.healthcareride.partner.MvpApplication;
import com.healthcareride.partner.R;
import com.healthcareride.partner.base.BaseBottomSheetDialogFragment;
import com.healthcareride.partner.data.network.model.Dispute;
import com.healthcareride.partner.data.network.model.HistoryDetail;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DisputeStatusFragment extends BaseBottomSheetDialogFragment implements DisputeStatusIView, View.OnClickListener {

    private static final String TRIP_KEY = "trip_data";
    @BindView(R.id.dispute_status_container)
    LinearLayout disputeStatusContainer;
    @BindView(R.id.ivSupportCall)
    ImageView callImage;
    @BindView(R.id.user_dispute)
    TextView userDispute;
    @BindView(R.id.admin_comment)
    TextView adminComment;
    @BindView(R.id.dispute_status)
    TextView disputeStatus;
    @BindView(R.id.llAdminComments)
    LinearLayout adminCommentLayout;
    private HistoryDetail datum;

    private DisputeStatusPresenter<DisputeStatusFragment> presenter =
            new DisputeStatusPresenter<>();

    public static DisputeStatusFragment newInstance(HistoryDetail datum) {
        Bundle args = new Bundle();
        DisputeStatusFragment fragment = new DisputeStatusFragment();
        args.putSerializable(TRIP_KEY, datum);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_fragment_dispute_status;
    }

    @Override
    public void initView(View view) {
        ButterKnife.bind(this, view);
        callImage.setOnClickListener(this);

        Bundle bundle = getArguments();
        datum = (HistoryDetail) Objects.requireNonNull(bundle).getSerializable(TRIP_KEY);

        Dispute dispute = datum != null ? datum.getDispute() : null;
        if (dispute != null) {
            disputeStatusContainer.setVisibility(View.VISIBLE);
            userDispute.setText(dispute.getDisputeName());
            disputeStatus.setText(dispute.getStatus().toUpperCase());
            if (dispute.getIsAdmin().equals(1)) {
                adminCommentLayout.setVisibility(View.VISIBLE);
                adminComment.setText(dispute.getComments());
            }
            if (dispute.getStatus().contains("open")) {
                disputeStatus.setTextColor(getResources().getColor(R.color.open_word));
                disputeStatus.setBackground(getResources().getDrawable(R.drawable.button_round_status_opened));
            } else {
                disputeStatus.setTextColor(getResources().getColor(R.color.close_word));
                disputeStatus.setBackground(getResources().getDrawable(R.drawable.button_round_status_closed));
            }
        } else {
            disputeStatusContainer.setVisibility(View.GONE);
            Toast.makeText(getActivity().getApplicationContext(), "Dispute is null",
                    Toast.LENGTH_SHORT).show();
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        }
    }

    private void callPhoneNumber(String mobileNumber) {
        if (mobileNumber != null && !mobileNumber.isEmpty())
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mobileNumber)));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivSupportCall)
            callPhoneNumber(MvpApplication.helpNumber);
    }
}
