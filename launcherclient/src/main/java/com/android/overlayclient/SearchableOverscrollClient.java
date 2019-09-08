package com.android.overlayclient;

import android.os.Bundle;

public interface SearchableOverscrollClient extends WorkspaceOverscrollClient {
    boolean startSearch(byte[] options, Bundle parameters);
}
