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

package ch.deletescape.lawnchair.feed.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.SeekBar;

public class AnimatingSeekbar extends SeekBar {

    private static final Interpolator DEFAULT_INTERPOLATER = new AccelerateDecelerateInterpolator();

    private ValueAnimator animator;
    private ValueAnimator animatorSecondary;
    private boolean animate = true;

    public AnimatingSeekbar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public AnimatingSeekbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimatingSeekbar(Context context) {
        super(context);
    }

    public boolean isAnimated() {
        return animate;
    }

    public void setAnimated(boolean animate) {
        this.animate = animate;
    }

    @Override
    public synchronized void setProgress(int progress) {
        if (!animate) {
            super.setProgress(progress);
            return;
        }
        if (animator != null)
            animator.cancel();
        if (animator == null) {
            animator = ValueAnimator.ofInt(getProgress(), progress);
            animator.setInterpolator(DEFAULT_INTERPOLATER);
            animator.addUpdateListener(
                    animation -> AnimatingSeekbar.super.setProgress((Integer) animation.getAnimatedValue()));
        } else
            animator.setIntValues(getProgress(), progress);
        animator.start();

    }

    @Override
    public synchronized void setSecondaryProgress(int secondaryProgress) {
        if (!animate) {
            super.setSecondaryProgress(secondaryProgress);
            return;
        }
        if (animatorSecondary != null)
            animatorSecondary.cancel();
        if (animatorSecondary == null) {
            animatorSecondary = ValueAnimator.ofInt(getProgress(), secondaryProgress);
            animatorSecondary.setInterpolator(DEFAULT_INTERPOLATER);
            animatorSecondary.addUpdateListener(animation -> AnimatingSeekbar.super.setSecondaryProgress((Integer) animation
                    .getAnimatedValue()));
        } else
            animatorSecondary.setIntValues(getProgress(), secondaryProgress);
        animatorSecondary.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animator != null)
            animator.cancel();
        if (animatorSecondary != null)
            animatorSecondary.cancel();
    }

}