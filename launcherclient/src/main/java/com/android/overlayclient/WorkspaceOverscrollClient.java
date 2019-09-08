package com.android.overlayclient;

import android.view.WindowManager;

public interface WorkspaceOverscrollClient {
    void onAttachedToWindow();
    void onDetachedFromWindow();
    void onResume();
    void onPause();
    void onStart();
    void onStop();
    void acceptLayoutParams(WindowManager.LayoutParams params);
    void startScroll();
    void onScroll(float percentage);
    void stopScroll();
}
