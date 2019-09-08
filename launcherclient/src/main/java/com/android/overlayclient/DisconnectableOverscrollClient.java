package com.android.overlayclient;

public interface DisconnectableOverscrollClient extends WorkspaceOverscrollClient {
    void reconnect();
    void disconnect();
}
