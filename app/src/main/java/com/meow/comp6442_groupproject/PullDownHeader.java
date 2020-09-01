package com.meow.comp6442_groupproject;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewAnimator;

import androidx.annotation.NonNull;

import com.lcodecore.tkrefreshlayout.IHeaderView;
import com.lcodecore.tkrefreshlayout.OnAnimEndListener;
import com.wang.avi.AVLoadingIndicatorView;

public class PullDownHeader extends FrameLayout implements IHeaderView {

    View rootView;
    ImageView refreshArrow;
    AVLoadingIndicatorView loadingView;
    TextView refreshTextView;


    public PullDownHeader(@NonNull Context context) {
        super(context);
        initUi();
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onPullingDown(float fraction, float maxHeadHeight, float headHeight) {
        if (refreshArrow.getVisibility() == GONE) {
            refreshArrow.setVisibility(VISIBLE);
            loadingView.setVisibility(GONE);
        }
        if (fraction < 1f) refreshTextView.setText("pull down to refresh");
        if (fraction > 1f) refreshTextView.setText("release to refresh");
        refreshArrow.setRotation(fraction * headHeight / maxHeadHeight * 180);
    }

    @Override
    public void onPullReleasing(float fraction, float maxHeadHeight, float headHeight) {
        if (fraction < 1f) {
            if (refreshArrow.getVisibility() == GONE) {
                refreshArrow.setVisibility(VISIBLE);
                loadingView.setVisibility(GONE);
            }
            refreshTextView.setText("pull down to refresh");
            refreshArrow.setRotation(fraction * headHeight / maxHeadHeight * 180);

        }
    }

    @Override
    public void startAnim(float maxHeadHeight, float headHeight) {
        refreshTextView.setText("Refreshing");
        refreshArrow.setVisibility(GONE);
        loadingView.setVisibility(VISIBLE);
        loadingView.smoothToShow();
    }

    @Override
    public void onFinish(OnAnimEndListener listener) {
        loadingView.smoothToHide();
        listener.onAnimEnd();
    }

    @Override
    public void reset() {

    }

    private void initUi(){
        if (rootView == null) {
            rootView = View.inflate(getContext(), R.layout.pulldown_layout, null);
            refreshArrow = rootView.findViewById(R.id.iv_arrow);
            refreshTextView = rootView.findViewById(R.id.tv);
            loadingView = rootView.findViewById(R.id.iv_loading);
            loadingView.setIndicator("BallClipRotateIndicator");
            loadingView.setIndicatorColor(Color.parseColor("#FFFFFF"));
            addView(rootView);
        }
    }
}
