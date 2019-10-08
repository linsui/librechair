package com.android.overlayclient;

public interface OpenableOverscrollClient extends WorkspaceOverscrollClient {
    void openOverlay(boolean animate);
    void closeOverlay(boolean animate);
}
