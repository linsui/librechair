/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.launcher3;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.android.launcher3.logging.UserEventDispatcher.LogContainerProvider;
import com.android.launcher3.userevent.nano.LauncherLogProto.ContainerType;
import com.android.launcher3.userevent.nano.LauncherLogProto.Target;

import ch.deletescape.lawnchair.LawnchairPreferences;

public class Hotseat extends FrameLayout implements LogContainerProvider, Insettable {

    private final Launcher mLauncher;
    private CellLayout mContent;

    @ViewDebug.ExportedProperty(category = "launcher")
    private boolean mHasVerticalHotseat;

    public Hotseat(Context context) {
        this(context, null);
    }

    public Hotseat(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Hotseat(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mLauncher = Launcher.getLauncher(context);
    }

    public CellLayout getLayout() {
        return mContent;
    }

    /* Get the orientation invariant order of the item in the hotseat for persistence. */
    int getOrderInHotseat(int x, int y) {
        int xOrder = mHasVerticalHotseat ? (mContent.getCountY() - y - 1) : x;
        int yOrder = mHasVerticalHotseat ? x * mContent.getCountY() : y * mContent.getCountX();
        return xOrder + yOrder;
    }

    /* Get the orientation specific coordinates given an invariant order in the hotseat. */
    int getCellXFromOrder(int rank) {
        int size = mHasVerticalHotseat ? mContent.getCountY() : mContent.getCountX();
        return mHasVerticalHotseat ? rank / size : rank % size;
    }

    int getCellYFromOrder(int rank) {
        int size = mHasVerticalHotseat ? mContent.getCountY() : mContent.getCountX();
        return mHasVerticalHotseat ? (mContent.getCountY() - ((rank % size) + 1)) : rank / size;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        LawnchairPreferences prefs = Utilities.getLawnchairPrefs(getContext());
        if (prefs.getDockHide()) {
            setVisibility(GONE);
        } else if (prefs.getDockSearchBar()) {
            inflate(getContext(), R.layout.search_container_hotseat, this);
        } else {
            View v = this.findViewById(R.id.search_container_hotseat);
            if (v != null) {
                removeView(v);
            }
        }

        mContent = findViewById(R.id.layout);
    }

    void resetLayout(boolean hasVerticalHotseat) {
        mContent.removeAllViewsInLayout();
        mHasVerticalHotseat = hasVerticalHotseat;
        InvariantDeviceProfile idp = mLauncher.getDeviceProfile().inv;
        int rows = Utilities.getLawnchairPrefs(mLauncher).getDockRowsCount();
        if (hasVerticalHotseat) {
            mContent.setGridSize(rows, idp.numHotseatIcons);
        } else {
            mContent.setGridSize(idp.numHotseatIcons, rows);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // We don't want any clicks to go through to the hotseat unless the workspace is in
        // the normal state or an accessible drag is in progress.
        return !mLauncher.getWorkspace().workspaceIconsCanBeDragged() &&
                !mLauncher.getAccessibilityDelegate().isInAccessibleDrag();
    }

    @Override
    public void fillInLogContainerData(View v, ItemInfo info, Target target, Target targetParent) {
        target.gridX = info.cellX;
        target.gridY = info.cellY;
        targetParent.containerType = ContainerType.HOTSEAT;
    }

    @Override
    public void setInsets(Rect insets) {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) getLayoutParams();
        DeviceProfile grid = mLauncher.getDeviceProfile();

        if (grid.isVerticalBarLayout()) {
            lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
            if (grid.isSeascape()) {
                lp.gravity = Gravity.LEFT;
                lp.width = grid.hotseatBarSizePx + insets.left;
            } else {
                lp.gravity = Gravity.RIGHT;
                lp.width = grid.hotseatBarSizePx + insets.right;
            }
        } else {
            lp.gravity = Gravity.BOTTOM;
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.height = grid.hotseatBarSizePx + insets.bottom;
        }
        Rect padding = grid.getHotseatLayoutPadding();
        getLayout().setPadding(padding.left, padding.top, padding.right, padding.bottom);

        setLayoutParams(lp);
        InsettableFrameLayout.dispatchInsets(this, insets);
    }
}
