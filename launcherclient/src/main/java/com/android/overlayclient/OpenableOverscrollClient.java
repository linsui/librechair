package com.android.overlayclient;

import android.view.WindowManager;

public interface OpenableOverscrollClient extends WorkspaceOverscrollClient {
    void openOverlay(boolean animate);
    void closeOverlay(boolean animate);
}
