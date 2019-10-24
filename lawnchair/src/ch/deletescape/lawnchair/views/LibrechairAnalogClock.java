/*
 * Copyright (c) 2019 oldosfan.
 * Copyright (c) 2019 the Lawnchair developers
 *
 *     This file is part of Librechair.
 *
 *     Librechair is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Librechair is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Librechair.  If not, see <https://www.gnu.org/licenses/>.
 */

/*
 * Copyright (C) 2008 The Android Open Source Project
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

package ch.deletescape.lawnchair.views;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.View;

import com.android.launcher3.R;

import java.time.LocalTime;

import ch.deletescape.lawnchair.LawnchairUtilsKt;

/**
 * This is an adaption of the platform AnalogClock retrofitted for Java 8 time
 */
public class LibrechairAnalogClock extends View {
    private LocalTime mCalendar;
    private Drawable mHourHand;
    private Drawable mMinuteHand;
    private Drawable mDial;
    private int mDialWidth;
    private int mDialHeight;

    private boolean mAttached;

    private float mMinutes;
    private float mHour;
    private boolean mChanged;

    public LibrechairAnalogClock(Context context) {
        this(context, null);
    }

    public LibrechairAnalogClock(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LibrechairAnalogClock(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public LibrechairAnalogClock(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        final Resources r = context.getResources();
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.LibrechairAnalogClock, defStyleAttr, defStyleRes);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveAttributeDataForStyleable(context, R.styleable.LibrechairAnalogClock,
                    attrs, a, defStyleAttr, defStyleRes);
        }

        mDial = a.getDrawable(R.styleable.LibrechairAnalogClock_clockDial);
        if (mDial == null) {
            int resId = context.getResources().getIdentifier("clock_dial", "drawable", "android");
            mDial = context.getDrawable(resId);
        }

        mHourHand = a.getDrawable(R.styleable.LibrechairAnalogClock_clockHandHour);
        if (mHourHand == null) {
            int resId = context.getResources().getIdentifier("clock_hand_hour", "drawable", "android");
            mHourHand = context.getDrawable(resId);
        }

        mMinuteHand = a.getDrawable(R.styleable.LibrechairAnalogClock_clockHandMinute);
        if (mMinuteHand == null) {
            int resId = context.getResources().getIdentifier("clock_hand_minute", "drawable", "android");
            mMinuteHand = context.getDrawable(resId);
        }

        mCalendar = LocalTime.now();

        mDialWidth = mDial.getIntrinsicWidth();
        mDialHeight = mDial.getIntrinsicHeight();
    }


    public void setTint(int tint) {
        mDial = LawnchairUtilsKt.tint(mDial, tint);
        mHourHand = LawnchairUtilsKt.tint(mHourHand, tint);
        mMinuteHand = LawnchairUtilsKt.tint(mMinuteHand, tint);
        postInvalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        float hScale = 1.0f;
        float vScale = 1.0f;

        if (widthMode != MeasureSpec.UNSPECIFIED && widthSize < mDialWidth) {
            hScale = (float) widthSize / (float) mDialWidth;
        }

        if (heightMode != MeasureSpec.UNSPECIFIED && heightSize < mDialHeight) {
            vScale = (float) heightSize / (float) mDialHeight;
        }

        float scale = Math.min(hScale, vScale);

        setMeasuredDimension(resolveSizeAndState((int) (mDialWidth * scale), widthMeasureSpec, 0),
                resolveSizeAndState((int) (mDialHeight * scale), heightMeasureSpec, 0));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mChanged = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        boolean changed = mChanged;
        if (changed) {
            mChanged = false;
        }

        int availableWidth = getMeasuredWidth();
        int availableHeight = getMeasuredHeight();

        int x = availableWidth / 2;
        int y = availableHeight / 2;

        final Drawable dial = mDial;
        int w = dial.getIntrinsicWidth();
        int h = dial.getIntrinsicHeight();

        boolean scaled = false;

        if (availableWidth < w || availableHeight < h) {
            scaled = true;
            float scale = Math.min((float) availableWidth / (float) w,
                    (float) availableHeight / (float) h);
            canvas.save();
            canvas.scale(scale, scale, x, y);
        }

        if (changed) {
            dial.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
        }
        dial.draw(canvas);

        canvas.save();
        canvas.rotate(mHour / 12.0f * 360.0f, x, y);
        final Drawable hourHand = mHourHand;
        if (changed) {
            w = hourHand.getIntrinsicWidth();
            h = hourHand.getIntrinsicHeight();
            hourHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
        }
        hourHand.draw(canvas);
        canvas.restore();

        canvas.save();
        canvas.rotate(mMinutes / 60.0f * 360.0f, x, y);

        final Drawable minuteHand = mMinuteHand;
        if (changed) {
            w = minuteHand.getIntrinsicWidth();
            h = minuteHand.getIntrinsicHeight();
            minuteHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
        }
        minuteHand.draw(canvas);
        canvas.restore();

        if (scaled) {
            canvas.restore();
        }
    }

    public void updateTime(LocalTime time) {

        int hour = time.getHour();
        int minute = time.getMinute();
        int second = time.getSecond();

        mMinutes = minute + second / 60.0f;
        mHour = hour + mMinutes / 60.0f;
        mChanged = true;

        updateContentDescription(mCalendar);
    }
    private void updateContentDescription(LocalTime time) {
        final int flags = DateUtils.FORMAT_SHOW_TIME;
        String contentDescription = DateUtils.formatDateTime(getContext(),
                time.toSecondOfDay() * 1000, flags);
        setContentDescription(contentDescription);
    }
}
