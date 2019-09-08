package com.google.android.apps.nexuslauncher;

import android.animation.AnimatorSet;
import android.os.Bundle;

import com.android.launcher3.Launcher;
import com.google.android.apps.nexuslauncher.smartspace.SmartspaceView;

public class NexusLauncherActivity extends Launcher {

    private NexusLauncher mLauncher;

    public NexusLauncherActivity() {
        mLauncher = new NexusLauncher(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void playQsbAnimation() {
        mLauncher.mQsbAnimationController.dZ();
    }

    public AnimatorSet openQsb() {
        return mLauncher.mQsbAnimationController.openQsb();
    }

    public void registerSmartspaceView(SmartspaceView smartspace) {
        mLauncher.registerSmartspaceView(smartspace);
    }

    public NexusLauncher getLauncher() {
        return mLauncher;
    }
}
