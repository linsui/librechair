/*
 *     Copyright (c) 2017-2019 the Lawnchair team
 *     Copyright (c)  2019 oldosfan (would)
 *     This file is part of Lawnchair Launcher.
 *
 *     Lawnchair Launcher is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Lawnchair Launcher is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Lawnchair Launcher.  If not, see <https://www.gnu.org/licenses/>.
 */

package ch.deletescape.lawnchair.feed.widgets;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;

import com.android.launcher3.CheckLongPressHelper;
import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.android.launcher3.SimpleOnStylusPressListener;
import com.android.launcher3.StylusEventHelper;
import com.android.launcher3.Utilities;
import com.android.launcher3.dragndrop.DragLayer;

public class OverlayWidgetHost extends AppWidgetHost {

    public OverlayWidgetHost(Context context, int hostId) {
        super(context, hostId);
    }

    @Override
    protected AppWidgetHostView onCreateView(Context context, int appWidgetId,
            AppWidgetProviderInfo appWidget) {
        return new AppWidgetHostView(context) {

            private final CheckLongPressHelper mLongPressHelper = new CheckLongPressHelper(this);
            private final StylusEventHelper mStylusEventHelper = new StylusEventHelper(new SimpleOnStylusPressListener(this), this);

            private boolean scrollable = false;

            private boolean checkScrollableRecursively(ViewGroup viewGroup) {
                if (viewGroup instanceof AdapterView) {
                    return true;
                } else {
                    for (int i = 0; i < viewGroup.getChildCount(); i++) {
                        View child = viewGroup.getChildAt(i);
                        if (child instanceof ViewGroup) {
                            if (checkScrollableRecursively((ViewGroup) child)) {
                                return true;
                            }
                        }
                    }
                }
                return false;
            }

            @Override
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                scrollable = checkScrollableRecursively(this);
            }

            @Override
            public void setAppWidget(int appWidgetId, AppWidgetProviderInfo info) {
                super.setAppWidget(appWidgetId, info);
                setPadding(0, 0, 0, 0);
            }


            @Override
            public void updateAppWidget(RemoteViews remoteViews) {
                super.updateAppWidget(remoteViews);
            }

            @Override
            protected View getErrorView() {
                return LayoutInflater.from(this.getContext())
                        .inflate(R.layout.appwidget_error, this, false);
            }

            @Override
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (scrollable && ev.getAction() == MotionEvent.ACTION_DOWN) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    getParent().getParent().requestDisallowInterceptTouchEvent(true);
                }
                // Just in case the previous long press hasn't been cleared, we make sure to start fresh
                // on touch down.
                if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                    mLongPressHelper.cancelLongPress();
                }

                // Consume any touch events for ourselves after longpress is triggered
                if (mLongPressHelper.hasPerformedLongPress()) {
                    mLongPressHelper.cancelLongPress();
                    return true;
                }

                // Watch for longpress or stylus button press events at this level to
                // make sure users can always pick up this widget
                if (mStylusEventHelper.onMotionEvent(ev)) {
                    mLongPressHelper.cancelLongPress();
                    return true;
                }

                switch (ev.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (!mStylusEventHelper.inStylusButtonPressed()) {
                            mLongPressHelper.postCheckForLongPress();
                        }
                        break;
                    }

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        mLongPressHelper.cancelLongPress();
                }

                // Otherwise continue letting touch events fall through to children
                return super.onInterceptTouchEvent(ev);
            }
        };
    }
}
