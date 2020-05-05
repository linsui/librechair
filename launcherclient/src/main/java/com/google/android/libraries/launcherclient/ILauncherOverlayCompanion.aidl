package com.google.android.libraries.launcherclient;

import com.google.android.libraries.launcherclient.ILauncherInterface;

interface ILauncherOverlayCompanion {
    boolean shouldScrollWorkspace();
    boolean shouldFadeWorkspaceDuringScroll();
    oneway void restartProcess();
    oneway void attachInterface(ILauncherInterface interfaze);
    boolean onBackPressed();
}
