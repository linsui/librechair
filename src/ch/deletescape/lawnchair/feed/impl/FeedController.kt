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
package ch.deletescape.lawnchair.feed.impl

import android.animation.*
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import android.util.AttributeSet
import android.util.Property
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import ch.deletescape.lawnchair.feed.anim.AnimationDelegate
import ch.deletescape.lawnchair.feed.anim.inflate
import ch.deletescape.lawnchair.feed.anim.interpolator.InterpolatorRegistry
import ch.deletescape.lawnchair.feed.impl.Interpolators.scrollInterpolatorForVelocity
import ch.deletescape.lawnchair.feed.impl.Utilities.SINGLE_FRAME_MS
import ch.deletescape.lawnchair.lawnchairPrefs
import ch.deletescape.lawnchair.persistence.feedPrefs
import ch.deletescape.lawnchair.useWhiteText
import com.android.launcher3.R
import com.android.launcher3.util.FlingBlockCheck
import com.android.launcher3.util.PendingAnimation
import kotlin.math.abs
import kotlin.math.roundToLong
import kotlin.math.sign

class FeedController(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs),
        SwipeDetector.Listener {
    var mOpenedCallback: (() -> Unit)? = null
    private val mDetector: SwipeDetector = SwipeDetector(context, this, SwipeDetector.HORIZONTAL)
    private var discardTouchEvents = false
    private var mStartState: FeedState? = null
    private var mFromState: FeedState? = null
    private var mToState: FeedState? = null
    private var mCurrentAnimationPlaybackController: AnimatorPlaybackController? = null
    private var mPendingAnimation: PendingAnimation? = null
    private var mStartProgress: Float = 0.toFloat()
    // Ratio of transition process [0, 1] to drag displacement (px)
    private var mProgressMultiplier: Float = 0.toFloat()
    private var mDisplacementShift: Float = 0.toFloat()
    private var mCanBlockFling: Boolean = false
    private val mFlingBlockCheck = FlingBlockCheck()
    private var mFeedBackground: View? = null
    private var mFeedContent: View? = null
    var mCurrentState: FeedState? = FeedState.CLOSED
        set(value) = {
            if (field != value && value eqp FeedState.OPEN && mOpenedCallback != null) {
                mOpenedCallback?.invoke()
            }
            field = value
        }()
    private var mDownTime: Long = 0
    private var mLastScroll = 0f
    private var mProgress: Float = 0.toFloat()
    var mLauncherFeed: LauncherFeed? = null
    var disallowInterceptCurrentTouchEvent = false
    var animationDelegate: AnimationDelegate =
            AnimationDelegate.inflate(context.lawnchairPrefs.feedAnimationDelegate)

    private val swipeDirection: Int
        get() {
            val fromState = mCurrentState
            var swipeDirection = 0
            if (!(getTargetState(fromState!!) eqp fromState)) {
                swipeDirection = swipeDirection or SwipeDetector.DIRECTION_POSITIVE
            }
            if (!(getTargetState(fromState) eqp fromState)) {
                swipeDirection = swipeDirection or SwipeDetector.DIRECTION_NEGATIVE
            }
            return swipeDirection
        }

    private val shiftRange: Float
        get() = width.toFloat()

    override fun onFinishInflate() {
        super.onFinishInflate()
        mFeedContent = findViewById(R.id.feed_content)
        mFeedBackground = findViewById(R.id.overlay_feed)
    }

    override fun setBackground(background: Drawable?) {
        super.setBackground(background)
        if (mLastScroll >= 0.5) {
            if (!useWhiteText(mLauncherFeed!!.backgroundColor, context)) {
                mLauncherFeed!!.feedController.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        } else {
            systemUiVisibility = 0
        }
    }

    fun closeOverlay(animated: Boolean, duration: Int) {
        if (!animated) {
            setProgress(0f, true)
        } else {
            val animator = ObjectAnimator.ofFloat(this, PROGRESS, 1f, 0f)
            animator.duration =
                    if (duration != 0) duration.toLong() else ((1.0 - context.feedPrefs.openingAnimationSpeed) * 350L * 2).roundToLong()
            animator.interpolator =
                    InterpolatorRegistry.ALL[context.feedPrefs.feedAnimationInterpolator]!!
            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    mCurrentState = FeedState.CLOSED
                }
            })
            animator.start()
        }
    }

    fun openOverlay(animated: Boolean, duration: Int) {
        if (!animated) {
            setProgress(0f, true)
        } else {
            val handler = Handler()
            visibility = View.VISIBLE
            val animator = ObjectAnimator.ofFloat(0f, 1f)
            animator.duration =
                    if (duration != 0) duration.toLong() else ((1.0 - context.feedPrefs.openingAnimationSpeed) * 350L * 2).roundToLong()
            animator.interpolator =
                    InterpolatorRegistry.ALL[context.feedPrefs.feedAnimationInterpolator]!!
            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    mCurrentState = FeedState.OPEN
                    mLauncherFeed?.endScroll()
                }
            })
            mLauncherFeed?.startScroll()
            animator.addUpdateListener {
                if (mLauncherFeed != null) {
                    handler.post { mLauncherFeed!!.onScroll(it.animatedFraction) }
                }
            }
            animator.start()
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        try {
            super.onLayout(changed, left, top, right, bottom)
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
        setProgress(mCurrentState!!.progress, false)
    }

    fun setLauncherFeed(launcherFeed: LauncherFeed) {
        mLauncherFeed = launcherFeed
    }

    private fun setProgress(progress: Float, notify: Boolean) {
        mProgress = progress

        if (progress >= 0.5) {
            if (!useWhiteText(mLauncherFeed!!.backgroundColor, context)) {
                mLauncherFeed!!.feedController.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        } else {
            systemUiVisibility = 0
        }

        if (notify) {
            mLauncherFeed!!.onProgress(mProgress, mDetector.isDraggingOrSettling)
        }
        animationDelegate.animate(mFeedContent!!, mFeedBackground!!, shiftRange, progress)
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
                MotionEvent.obtain(mDownTime, time(), MotionEvent.ACTION_MOVE, mLastScroll * width,
                        0f, 0))
    }

    fun endScroll() {
        onTouchEvent(
                MotionEvent.obtain(mDownTime, time(), MotionEvent.ACTION_UP, mLastScroll * width,
                        0f, 0))
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (disallowInterceptCurrentTouchEvent) {
            disallowInterceptCurrentTouchEvent = false
            return false
        }
        if (mCurrentAnimationPlaybackController != null &&
                mFromState eqp FeedState.OPEN) {
            return false
        }
        mDetector.setDetectableScrollConditions(swipeDirection,
                mCurrentAnimationPlaybackController != null)
        mDetector.onTouchEvent(ev)
        return mDetector.isDraggingOrSettling
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (discardTouchEvents) true else mDetector.onTouchEvent(event)
    }

    /**
     * Returns the state to go to from fromState given the drag direction. If there is no state in
     * that direction, returns fromState.
     */
    private fun getTargetState(fromState: FeedState): FeedState {
        return if (fromState eqp FeedState.CLOSED) FeedState.OPEN else FeedState.CLOSED
    }

    private fun initCurrentAnimation(): Float {
        val range = shiftRange
        val maxAccuracy =
                ((2 * range).toLong() * (1.0 - context.feedPrefs.openingAnimationSpeed)).roundToLong()

        val startShift = mFromState!!.progress * range
        val endShift = mToState!!.progress * range

        var totalShift = endShift - startShift

        cancelPendingAnim()
        mCurrentAnimationPlaybackController = AnimatorPlaybackController
                .wrap(createAnim(mToState!!, maxAccuracy), maxAccuracy) { this.clearState() }

        if (totalShift eqp 0f) {
            totalShift = shiftRange
        }

        return 1 / totalShift
    }

    private fun createAnim(toState: FeedState, duration: Long): AnimatorSet {
        val translationX = ObjectAnimator.ofFloat(this, PROGRESS, toState.progress)
        translationX.duration = duration
        translationX.interpolator =
                InterpolatorRegistry.ALL[context.feedPrefs.feedAnimationInterpolator]!!

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

    private fun reinitCurrentAnimation(reachedToState: Boolean): Boolean {
        val newFromState = when {
            mFromState eqp null -> mCurrentState
            reachedToState -> mToState
            else -> mFromState
        }
        val newToState = getTargetState(newFromState!!)

        if (newFromState eqp mFromState && newToState eqp mToState || newFromState eqp newToState) {
            return false
        }

        mFromState = newFromState
        mToState = newToState

        mStartProgress = 0f
        if (mCurrentAnimationPlaybackController != null) {
            mCurrentAnimationPlaybackController!!.onCancelRunnable = null
        }

        mProgressMultiplier = initCurrentAnimation()
        mCurrentAnimationPlaybackController!!.dispatchOnStart()
        return true
    }

    override fun onDragStart(start: Boolean) {
        mStartState = mCurrentState
        if (mCurrentAnimationPlaybackController eqp null) {
            mFromState = mStartState
            mToState = null
            cancelAnimationControllers()
            reinitCurrentAnimation(false)
            mDisplacementShift = 0f
        } else {
            mCurrentAnimationPlaybackController!!.pause()
            mStartProgress = mCurrentAnimationPlaybackController!!.progressFraction
        }
        mCanBlockFling = false
        mFlingBlockCheck.unblockFling()
    }

    override fun onDrag(displacement: Float, velocity: Float): Boolean {
        val deltaProgress =
                mProgressMultiplier * ((if (layoutDirection eqp View.LAYOUT_DIRECTION_RTL)
                    -1 else 1) * displacement - mDisplacementShift)
        val progress = deltaProgress + mStartProgress
        updateProgress(progress)
        if (progress <= 0) {
            if (reinitCurrentAnimation(false)) {
                mDisplacementShift = (if (layoutDirection eqp View.LAYOUT_DIRECTION_RTL)
                    -1 else 1) * displacement
                if (mCanBlockFling) {
                    mFlingBlockCheck.blockFling()
                }
            }
        } else if (progress >= 1) {
            if (reinitCurrentAnimation(true)) {
                mDisplacementShift = (if (layoutDirection eqp View.LAYOUT_DIRECTION_RTL)
                    -1 else 1) * displacement
                if (mCanBlockFling) {
                    mFlingBlockCheck.blockFling()
                }
            }
        } else {
            mFlingBlockCheck.onEvent()
        }

        return true
    }

    private fun updateProgress(fraction: Float) {
        mCurrentAnimationPlaybackController!!.setPlayFraction(fraction)
    }

    override fun onDragEnd(velocity: Float, _fling: Boolean) {
        var fling = _fling
        val blockedFling = fling && mFlingBlockCheck.isBlocked
        if (blockedFling) {
            fling = false
        }

        val targetState: FeedState?
        val progress =
                mCurrentAnimationPlaybackController!!.progressFraction * (if (layoutDirection eqp View.LAYOUT_DIRECTION_RTL)
                    -1 else 1)
        val interpolatedProgress =
                mCurrentAnimationPlaybackController!!.interpolator.getInterpolation(progress)
        targetState = if (fling) {
            if (sign(velocity).compareTo(sign(
                            mProgressMultiplier)) eqp 0) mToState else mFromState
            // snap to top or bottom using the release velocity
        } else {
            if (interpolatedProgress > SUCCESS_TRANSITION_PROGRESS) mToState else mFromState
        }

        val endProgress: Float
        val startProgress: Float
        val duration: Long
        // Increase the duration if we prevented the fling, as we are going against a high velocity.
        val durationMultiplier =
                ((if (blockedFling && targetState eqp mFromState) blockedFlingDurationFactor(
                        velocity)
                else 1) * (1.0 - context.feedPrefs.openingAnimationSpeed)).roundToLong()

        if (targetState eqp mToState) {
            endProgress = 1f
            if (progress >= 1) {
                duration = 0
                startProgress = 1f
            } else {
                startProgress = Utilities.boundToRange(
                        progress + velocity * SINGLE_FRAME_MS.toFloat() * mProgressMultiplier, 0f,
                        1f)
                duration = SwipeDetector.calculateDuration(velocity,
                        endProgress - progress.coerceAtLeast(0f)) * durationMultiplier
            }
        } else {
            // Let the state manager know that the animation didn't go to the target state,
            // but don't cancel ourselves (we already clean up when the animation completes).
            val onCancel = mCurrentAnimationPlaybackController!!.onCancelRunnable
            mCurrentAnimationPlaybackController!!.onCancelRunnable = null
            mCurrentAnimationPlaybackController!!.dispatchOnCancel()
            mCurrentAnimationPlaybackController!!.onCancelRunnable = onCancel

            endProgress = 0f
            if (progress <= 0) {
                duration = 0
                startProgress = 0f
            } else {
                startProgress = Utilities.boundToRange(
                        progress + velocity * SINGLE_FRAME_MS.toFloat() * mProgressMultiplier, 0f,
                        1f)
                duration = SwipeDetector.calculateDuration(velocity,
                        progress.coerceAtMost(1f) - endProgress) * durationMultiplier
            }
        }

        mCurrentAnimationPlaybackController!!.setEndAction {
            onSwipeInteractionCompleted(targetState)
        }
        val anim = mCurrentAnimationPlaybackController!!.animationPlayer
        anim.setFloatValues(startProgress, endProgress)
        updateSwipeCompleteAnimation(anim, duration, velocity)
        mCurrentAnimationPlaybackController!!.dispatchOnStart()
        anim.start()
    }

    private fun updateSwipeCompleteAnimation(animator: ValueAnimator,
                                             expectedDuration: Long,
                                             velocity: Float) {
        animator.setDuration(expectedDuration).interpolator =
                scrollInterpolatorForVelocity(velocity)
    }

    private fun onSwipeInteractionCompleted(targetState: FeedState?) {
        cancelAnimationControllers()
        var shouldGoToTargetState = true
        if (mPendingAnimation != null) {
            val reachedTarget = mToState eqp targetState
            mPendingAnimation!!.finish(reachedTarget, 0)
            mPendingAnimation = null
            shouldGoToTargetState = !reachedTarget
        }
        if (shouldGoToTargetState) {
            mCurrentState = targetState
        }
        mLauncherFeed!!.onProgress(mProgress, false)
    }

    private fun clearState() {
        cancelAnimationControllers()
    }

    private fun cancelAnimationControllers() {
        mCurrentAnimationPlaybackController = null
        mDetector.finishedScrolling()
        mDetector.setDetectableScrollConditions(0, false)
    }

    companion object {

        // Progress after which the transition is assumed to be a success in case user does not fling
        const val SUCCESS_TRANSITION_PROGRESS = 0.5f
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
            return Utilities.boundToRange(abs(velocity) / 2, 2f, 6f).toInt()
        }
    }
}
