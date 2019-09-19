package com.android.overlayclient;

import android.os.Bundle;

public interface SearchClient {
    boolean startSearch(byte[] blob, Bundle parameters);
    void requestVoiceDetection(boolean start);
    String getVoiceSearchLanguage();
    boolean isVoiceDetectionRunning();
}
