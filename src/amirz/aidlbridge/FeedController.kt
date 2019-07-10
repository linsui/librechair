/*
 * Copyright (C) 2019 The Android Open Source Project
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
package amirz.aidlbridge

import amirz.aidlbridge.Interpolators.LINEAR
import amirz.aidlbridge.Interpolators.scrollInterpolatorForVelocity
import amirz.aidlbridge.Utilities.SINGLE_FRAME_MS
import android.animation.*
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.util.Property
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import com.android.launcher3.R
import com.android.launcher3.util.FlingBlockCheck
import com.android.launcher3.util.PendingAnimation

class FeedController(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs),
                                                              SwipeDetector.Listener {
    protected val mDetector: SwipeDetector
    protected var mStartState: FeedState? = null
    protected var mFromState: FeedState? = null
    protected var mToState: FeedState? = null
    protected var mCurrentAnimation: AnimatorPlaybackController? = null
    protected var mPendingAnimation: PendingAnimation? = null
    private val mNoIntercept: Boolean = false
    private var mStartProgress: Float = 0.toFloat()
    // Ratio of transition process [0, 1] to drag displacement (px)
    private var mProgressMultiplier: Float = 0.toFloat()
    private var mDisplacementShift: Float = 0.toFloat()
    private var mCanBlockFling: Boolean = false
    private val mFlingBlockCheck = FlingBlockCheck()
    private var mFeedBackground: View? = null
    private var mFeedContent: View? = null
    private var mCurrentState: FeedState? = FeedState.CLOSED
    private var mDownTime: Long = 0
    private var mLastScroll = 0f
    private var mProgress: Float = 0.toFloat()
    private var mLauncherFeed: LauncherFeed? = null

    private val swipeDirection: Int
        get() {
            val fromState = mCurrentState
            var swipeDirection = 0
            if (getTargetState(fromState!!, true) !== fromState) {
                swipeDirection = swipeDirection or SwipeDetector.DIRECTION_POSITIVE
            }
            if (getTargetState(fromState!!, false) !== fromState) {
                swipeDirection = swipeDirection or SwipeDetector.DIRECTION_NEGATIVE
            }
            return swipeDirection
        }

    protected val shiftRange: Float
        get() = width.toFloat()

    init {
        mDetector = SwipeDetector(context, this, SwipeDetector.HORIZONTAL)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        mFeedContent = findViewById(R.id.feed_recycler)
        mFeedBackground = findViewById(R.id.overlay_feed)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
            closeOverlay(true, 0)
            return true
        }
        return super.dispatchKeyEvent(event)
    }

    fun closeOverlay(animated: Boolean, duration: Int) {
        var duration = duration
        if (!animated) {
            setProgress(0f, true)
        } else {
            if (duration == 0) {
                duration = 350
            }
            val animator = ObjectAnimator.ofFloat(this, PROGRESS, 1f, 0f)
            animator.duration = duration.toLong()
            animator.interpolator = Interpolators.DEACCEL_1_5
            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    mCurrentState = FeedState.CLOSED
                }
            })
            animator.start()
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        setProgress(mCurrentState!!.progress, false)
    }

    fun setLauncherFeed(launcherFeed: LauncherFeed) {
        mLauncherFeed = launcherFeed
    }

    private fun setProgress(progress: Float, notify: Boolean) {
        Log.d(TAG, "setProgress: $progress")
        mProgress = progress
        if (notify) {
            mLauncherFeed!!.onProgress(mProgress, mDetector.isDraggingOrSettling)
        }
        mFeedBackground!!.alpha = mProgress
        mFeedContent!!.translationX = (-1 + mProgress) * shiftRange
    }

    private fun time(): Long {
        return System.currentTimeMillis()
    }

    fun startScroll() {
        mDownTime = time()
        onInterceptTouchEvent(
            MotionEvent.obtain(mDownTime, mDownTime, MotionEvent.ACTION_DOWN, 0f, 0f, 0))
    }

    fun onScroll(progress: Float) {
        mLastScroll = progress
        onTouchEvent(
            MotionEvent.obtain(mDownTime, time(), MotionEvent.ACTION_MOVE, mLastScroll * width, 0f,
                               0))
    }

    fun endScroll() {
        onTouchEvent(
            MotionEvent.obtain(mDownTime, time(), MotionEvent.ACTION_UP, mLastScroll * width, 0f,
                               0))
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (mCurrentAnimation != null && mFromState === FeedState.OPEN) {
            return false
        }
        mDetector.setDetectableScrollConditions(swipeDirection, mCurrentAnimation != null)
        mDetector.onTouchEvent(ev)
        return mDetector.isDraggingOrSettling
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return mDetector.onTouchEvent(event)
    }

    /**
     * Returns the state to go to from fromState given the drag direction. If there is no state in
     * that direction, returns fromState.
     */
    protected fun getTargetState(fromState: FeedState, isDragTowardPositive: Boolean): FeedState {
        return if (fromState === FeedState.CLOSED) FeedState.OPEN else FeedState.CLOSED
    }

    protected fun initCurrentAnimation(): Float {
        val range = shiftRange
        val maxAccuracy = (2 * range).toLong()

        val startShift = mFromState!!.progress * range
        val endShift = mToState!!.progress * range

        var totalShift = endShift - startShift

        cancelPendingAnim()
        mCurrentAnimation = AnimatorPlaybackController
                .wrap(createAnim(mToState!!, maxAccuracy), maxAccuracy) { this.clearState() }

        if (totalShift == 0f) {
            totalShift = shiftRange
        }

        return 1 / totalShift
    }

    private fun createAnim(toState: FeedState, duration: Long): AnimatorSet {
        val translationX = ObjectAnimator.ofFloat(this, PROGRESS, toState.progress)
        translationX.duration = duration
        translationX.interpolator = LINEAR

        val animatorSet = AnimatorSet()
        animatorSet.play(translationX)
        return animatorSet
    }

    private fun cancelPendingAnim() {
        if (mPendingAnimation != null) {
            mPendingAnimation!!.finish(false, 0)
            mPendingAnimation = null
        }
    }

    private fun reinitCurrentAnimation(reachedToState: Boolean,
                                       isDragTowardPositive: Boolean): Boolean {
        val newFromState = if (mFromState == null) mCurrentState
        else if (reachedToState) mToState else mFromState
        val newToState = getTargetState(newFromState!!, isDragTowardPositive)

        if (newFromState === mFromState && newToState === mToState || newFromState === newToState) {
            return false
        }

        mFromState = newFromState
        mToState = newToState

        mStartProgress = 0f
        if (mCurrentAnimation != null) {
            mCurrentAnimation!!.onCancelRunnable = null
        }

        mProgressMultiplier = initCurrentAnimation()
        mCurrentAnimation!!.dispatchOnStart()
        return true
    }

    override fun onDragStart(start: Boolean) {
        mStartState = mCurrentState
        if (mCurrentAnimation == null) {
            mFromState = mStartState
            mToState = null
            cancelAnimationControllers()
            reinitCurrentAnimation(false, mDetector.wasInitialTouchPositive())
            mDisplacementShift = 0f
        } else {
            mCurrentAnimation!!.pause()
            mStartProgress = mCurrentAnimation!!.progressFraction
        }
        mCanBlockFling = false
        mFlingBlockCheck.unblockFling()
    }

    override fun onDrag(displacement: Float, velocity: Float): Boolean {
        val deltaProgress = mProgressMultiplier * (displacement - mDisplacementShift)
        val progress = deltaProgress + mStartProgress
        updateProgress(progress)
        val isDragTowardPositive = displacement - mDisplacementShift < 0
        if (progress <= 0) {
            if (reinitCurrentAnimation(false, isDragTowardPositive)) {
                mDisplacementShift = displacement
                if (mCanBlockFling) {
                    mFlingBlockCheck.blockFling()
                }
            }
        } else if (progress >= 1) {
            if (reinitCurrentAnimation(true, isDragTowardPositive)) {
                mDisplacementShift = displacement
                if (mCanBlockFling) {
                    mFlingBlockCheck.blockFling()
                }
            }
        } else {
            mFlingBlockCheck.onEvent()
        }

        return true
    }

    protected fun updateProgress(fraction: Float) {
        mCurrentAnimation!!.setPlayFraction(fraction)
    }

    override fun onDragEnd(velocity: Float, fling: Boolean) {
        var fling = fling
        val blockedFling = fling && mFlingBlockCheck.isBlocked
        if (blockedFling) {
            fling = false
        }

        val targetState: FeedState?
        val progress = mCurrentAnimation!!.progressFraction
        val interpolatedProgress = mCurrentAnimation!!.interpolator.getInterpolation(progress)
        if (fling) {
            targetState = if (java.lang.Float.compare(Math.signum(velocity), Math.signum(
                        mProgressMultiplier)) == 0) mToState
            else mFromState
            // snap to top or bottom using the release velocity
        } else {
            targetState =
                    if (interpolatedProgress > SUCCESS_TRANSITION_PROGRESS) mToState else mFromState
        }

        val endProgress: Float
        val startProgress: Float
        val duration: Long
        // Increase the duration if we prevented the fling, as we are going against a high velocity.
        val durationMultiplier =
                if (blockedFling && targetState === mFromState) blockedFlingDurationFactor(velocity)
                else 1

        if (targetState === mToState) {
            endProgress = 1f
            if (progress >= 1) {
                duration = 0
                startProgress = 1f
            } else {
                startProgress = Utilities.boundToRange(
                    progress + velocity * SINGLE_FRAME_MS.toFloat() * mProgressMultiplier, 0f, 1f)
                duration = SwipeDetector.calculateDuration(velocity,
                                                           endProgress - Math.max(progress,
                                                                                  0f)) * durationMultiplier
            }
        } else {
            // Let the state manager know that the animation didn't go to the target state,
            // but don't cancel ourselves (we already clean up when the animation completes).
            val onCancel = mCurrentAnimation!!.onCancelRunnable
            mCurrentAnimation!!.onCancelRunnable = null
            mCurrentAnimation!!.dispatchOnCancel()
            mCurrentAnimation!!.onCancelRunnable = onCancel

            endProgress = 0f
            if (progress <= 0) {
                duration = 0
                startProgress = 0f
            } else {
                startProgress = Utilities.boundToRange(
                    progress + velocity * SINGLE_FRAME_MS.toFloat() * mProgressMultiplier, 0f, 1f)
                duration = SwipeDetector.calculateDuration(velocity, Math.min(progress,
                                                                              1f) - endProgress) * durationMultiplier
            }
        }

        mCurrentAnimation!!.setEndAction { onSwipeInteractionCompleted(targetState) }
        val anim = mCurrentAnimation!!.animationPlayer
        anim.setFloatValues(startProgress, endProgress)
        updateSwipeCompleteAnimation(anim, duration, targetState, velocity, fling)
        mCurrentAnimation!!.dispatchOnStart()
        anim.start()
    }

    protected fun updateSwipeCompleteAnimation(animator: ValueAnimator, expectedDuration: Long,
                                               targetState: FeedState?, velocity: Float,
                                               isFling: Boolean) {
        animator.setDuration(expectedDuration).interpolator =
                scrollInterpolatorForVelocity(velocity)
    }

    protected fun onSwipeInteractionCompleted(targetState: FeedState?) {
        cancelAnimationControllers()
        var shouldGoToTargetState = true
        if (mPendingAnimation != null) {
            val reachedTarget = mToState === targetState
            mPendingAnimation!!.finish(reachedTarget, 0)
            mPendingAnimation = null
            shouldGoToTargetState = !reachedTarget
        }
        if (shouldGoToTargetState) {
            mCurrentState = targetState
        }
        mLauncherFeed!!.onProgress(mProgress, false)
    }

    protected fun clearState() {
        cancelAnimationControllers()
    }

    private fun cancelAnimationControllers() {
        mCurrentAnimation = null
        mDetector.finishedScrolling()
        mDetector.setDetectableScrollConditions(0, false)
    }

    companion object {

        // Progress after which the transition is assumed to be a success in case user does not fling
        val SUCCESS_TRANSITION_PROGRESS = 0.5f
        private val TAG = "FeedController"
        val PROGRESS: Property<FeedController, Float> =
                object : Property<FeedController, Float>(Float::class.java, "progress") {
                    override fun get(`object`: FeedController): Float? {
                        return `object`.mProgress
                    }

                    override fun set(`object`: FeedController, value: Float?) {
                        `object`.setProgress(value!!, true)
                    }
                }

        fun blockedFlingDurationFactor(velocity: Float): Int {
            return Utilities.boundToRange(Math.abs(velocity) / 2, 2f, 6f).toInt()
        }
    }
}
