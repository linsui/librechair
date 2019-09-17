package com.google.android.libraries.launcherclient;

interface ILauncherOverlayCompanion {
    boolean shouldScrollWorkspace();
    boolean shouldFadeWorkspaceDuringScroll();
    oneway void restartProcess();
}
