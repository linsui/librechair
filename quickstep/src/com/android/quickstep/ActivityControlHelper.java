/*
 * Copyright (C) 2018 The Android Open Source Project
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
package com.android.quickstep;

import static android.view.View.TRANSLATION_Y;

import static com.android.launcher3.LauncherAnimUtils.OVERVIEW_TRANSITION_MS;
import static com.android.launcher3.LauncherAnimUtils.SCALE_PROPERTY;
import static com.android.launcher3.LauncherState.BACKGROUND_APP;
import static com.android.launcher3.LauncherState.FAST_OVERVIEW;
import static com.android.launcher3.LauncherState.NORMAL;
import static com.android.launcher3.LauncherState.OVERVIEW;
import static com.android.launcher3.LauncherStateManager.ANIM_ALL;
import static com.android.launcher3.allapps.AllAppsTransitionController.ALL_APPS_PROGRESS;
import static com.android.launcher3.allapps.AllAppsTransitionController.SCRIM_PROGRESS;
import static com.android.launcher3.anim.Interpolators.ACCEL_DEACCEL;
import static com.android.launcher3.anim.Interpolators.LINEAR;
import static com.android.quickstep.TouchConsumer.INTERACTION_NORMAL;
import static com.android.quickstep.TouchConsumer.INTERACTION_QUICK_SCRUB;
import static com.android.quickstep.WindowTransformSwipeHandler.RECENTS_ATTACH_DURATION;
import static com.android.quickstep.views.RecentsView.CONTENT_ALPHA;
import static com.android.systemui.shared.system.NavigationBarCompat.HIT_TARGET_BACK;
import static com.android.systemui.shared.system.NavigationBarCompat.HIT_TARGET_ROTATION;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import android.util.Log;
import android.view.View;

import android.view.animation.Interpolator;
import com.android.launcher3.BaseDraggingActivity;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherInitListener;
import com.android.launcher3.LauncherState;
import com.android.launcher3.LauncherStateManager;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.allapps.AllAppsTransitionController;
import com.android.launcher3.allapps.DiscoveryBounce;
import com.android.launcher3.anim.AnimationSuccessListener;
import com.android.launcher3.anim.AnimatorPlaybackController;
import com.android.launcher3.anim.AnimatorSetBuilder;
import com.android.launcher3.dragndrop.DragLayer;
import com.android.launcher3.uioverrides.FastOverviewState;
import com.android.launcher3.uioverrides.OverviewState;
import com.android.launcher3.userevent.nano.LauncherLogProto;
import com.android.launcher3.util.MultiValueAlpha.AlphaProperty;
import com.android.launcher3.views.FloatingIconView;
import com.android.quickstep.TouchConsumer.InteractionType;
import com.android.quickstep.util.ClipAnimationHelper;
import com.android.quickstep.util.LayoutUtils;
import com.android.quickstep.util.RemoteAnimationProvider;
import com.android.quickstep.util.RemoteAnimationTargetSet;
import com.android.quickstep.util.TransformedRect;
import com.android.quickstep.views.LauncherLayoutListener;
import com.android.quickstep.views.RecentsView;
import com.android.quickstep.views.TaskView;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

/**
 * Utility class which abstracts out the logical differences between Launcher and RecentsActivity.
 */
@TargetApi(Build.VERSION_CODES.P)
public interface ActivityControlHelper<T extends BaseDraggingActivity> {

    LayoutListener createLayoutListener(T activity);

    /**
     * Updates the UI to indicate quick interaction.
     */
    void onQuickInteractionStart(T activity, @Nullable RunningTaskInfo taskInfo,
            boolean activityVisible);

    float getTranslationYForQuickScrub(TransformedRect targetRect, DeviceProfile dp,
            Context context);

    void executeOnWindowAvailable(T activity, Runnable action);

    void onTransitionCancelled(T activity, boolean activityVisible);

    int getSwipeUpDestinationAndLength(DeviceProfile dp, Context context,
            @InteractionType int interactionType, TransformedRect outRect);

    void onSwipeUpComplete(T activity);

    @NonNull
    HomeAnimationFactory prepareHomeUI(T activity);

    AnimationFactory prepareRecentsUI(T activity, boolean activityVisible, boolean animateActivity,
            Consumer<AnimatorPlaybackController> callback);

    ActivityInitListener createActivityInitListener(BiPredicate<T, Boolean> onInitListener);

    @Nullable
    T getCreatedActivity();

    @UiThread
    @Nullable
    RecentsView getVisibleRecentsView();

    @UiThread
    boolean switchToRecentsIfVisible(boolean fromRecentsButton);

    Rect getOverviewWindowBounds(Rect homeBounds, RemoteAnimationTargetCompat target);

    boolean shouldMinimizeSplitScreen();

    /**
     * @return {@code true} if recents activity should be started immediately on touchDown,
     *         {@code false} if it should deferred until some threshold is crossed.
     */
    boolean deferStartingActivity(int downHitTarget);

    boolean supportsLongSwipe(T activity);

    AlphaProperty getAlphaProperty(T activity);

    /**
     * Must return a non-null controller is supportsLongSwipe was true.
     */
    LongSwipeHelper getLongSwipeController(T activity, RemoteAnimationTargetSet targetSet);

    /**
     * Used for containerType in {@link com.android.launcher3.logging.UserEventDispatcher}
     */
    int getContainerType();

    class LauncherActivityControllerHelper implements ActivityControlHelper<Launcher> {

        @Override
        public LayoutListener createLayoutListener(Launcher activity) {
            return new LauncherLayoutListener(activity);
        }

        @Override
        public void onQuickInteractionStart(Launcher activity, RunningTaskInfo taskInfo,
                boolean activityVisible) {
            LauncherState fromState = activity.getStateManager().getState();
            activity.getStateManager().goToState(FAST_OVERVIEW, activityVisible);

            QuickScrubController controller = activity.<RecentsView>getOverviewPanel()
                    .getQuickScrubController();
            controller.onQuickScrubStart(activityVisible && !fromState.overviewUi, this);
        }

        @Override
        public float getTranslationYForQuickScrub(TransformedRect targetRect, DeviceProfile dp,
                Context context) {
            // The padding calculations are exactly same as that of RecentsView.setInsets
            int topMargin = context.getResources()
                    .getDimensionPixelSize(R.dimen.task_thumbnail_top_margin);
            int paddingTop = targetRect.rect.top - topMargin - dp.getInsets().top;
            int paddingBottom = dp.availableHeightPx + dp.getInsets().top - targetRect.rect.bottom;

            return FastOverviewState.OVERVIEW_TRANSLATION_FACTOR * (paddingBottom - paddingTop);
        }

        @Override
        public void executeOnWindowAvailable(Launcher activity, Runnable action) {
            activity.getWorkspace().runOnOverlayHidden(action);
        }

        @Override
        public int getSwipeUpDestinationAndLength(DeviceProfile dp, Context context,
                @InteractionType int interactionType, TransformedRect outRect) {
            LayoutUtils.calculateLauncherTaskSize(context, dp, outRect.rect);
            if (interactionType == INTERACTION_QUICK_SCRUB) {
                outRect.scale = FastOverviewState.getOverviewScale(dp, outRect.rect, context);
            }
            if (dp.isVerticalBarLayout()) {
                Rect targetInsets = dp.getInsets();
                int hotseatInset = dp.isSeascape() ? targetInsets.left : targetInsets.right;
                return dp.hotseatBarSizePx + hotseatInset;
            } else {
                return LayoutUtils.getShelfTrackingDistance(context, dp);
            }
        }

        @Override
        public void onTransitionCancelled(Launcher activity, boolean activityVisible) {
            LauncherState startState = activity.getStateManager().getRestState();
            activity.getStateManager().goToState(startState, activityVisible);
        }

        @Override
        public void onSwipeUpComplete(Launcher activity) {
            // Re apply state in case we did something funky during the transition.
            activity.getStateManager().reapplyState();
            DiscoveryBounce.showForOverviewIfNeeded(activity);
        }

        @NonNull
        @Override
        public HomeAnimationFactory prepareHomeUI(Launcher activity) {
            final DeviceProfile dp = activity.getDeviceProfile();
            final RecentsView recentsView = activity.getOverviewPanel();
            final TaskView runningTaskView = recentsView.getRunningTaskView();
            final View workspaceView;
            if (runningTaskView != null && runningTaskView.getTask().key.getComponent() != null) {
                workspaceView = activity.getWorkspace().getFirstMatchForAppClose(
                        runningTaskView.getTask().key.getComponent().getPackageName(),
                        UserHandle.of(runningTaskView.getTask().key.userId));
            } else {
                workspaceView = null;
            }
            final RectF iconLocation = new RectF();
            final FloatingIconView floatingView = workspaceView != null
                    ? FloatingIconView.getFloatingIconView(activity, workspaceView,
                    true /* hideOriginal */, iconLocation, false /* isOpening */, null /* recycle */)
                    : null;

            return new HomeAnimationFactory() {
                @Nullable
                @Override
                public View getFloatingView() {
                    return floatingView;
                }

                @NonNull
                @Override
                public RectF getWindowTargetRect() {
                    final int halfIconSize = dp.iconSizePx / 2;
                    final float targetCenterX = dp.availableWidthPx / 2f;
                    final float targetCenterY = dp.availableHeightPx - dp.hotseatBarSizePx;

                    if (workspaceView != null) {
                        return new RectF(iconLocation);
                    } else {
                        // Fallback to animate to center of screen.
                        return new RectF(targetCenterX - halfIconSize, targetCenterY - halfIconSize,
                                targetCenterX + halfIconSize, targetCenterY + halfIconSize);
                    }
                }

                @NonNull
                @Override
                public Animator createActivityAnimationToHome() {
                    long accuracy = 2 * Math.max(dp.widthPx, dp.heightPx);
                    return activity.getStateManager().createAnimationToNewWorkspace(
                            NORMAL, accuracy).getTarget();
                }
            };
        }

        @Override
        public AnimationFactory prepareRecentsUI(Launcher activity, boolean activityVisible,
                boolean animateActivity, Consumer<AnimatorPlaybackController> callback) {
            final LauncherState startState = activity.getStateManager().getState();

            LauncherState resetState = startState;
            if (startState.disableRestore) {
                resetState = activity.getStateManager().getRestState();
            }
            activity.getStateManager().setRestState(resetState);

            final LauncherState fromState;
            if (!activityVisible) {
                // Since the launcher is not visible, we can safely reset the scroll position.
                // This ensures then the next swipe up to all-apps starts from scroll 0.
                activity.getAppsView().reset(false /* animate */);
                fromState = animateActivity ? BACKGROUND_APP : OVERVIEW;
                activity.getStateManager().goToState(fromState, false);

                // Optimization, hide the all apps view to prevent layout while initializing
                activity.getAppsView().getContentView().setVisibility(View.GONE);
            } else {
                fromState = startState;
            }

            return new AnimationFactory() {
                private Animator mShelfAnim;
                private ShelfAnimState mShelfState;
                private Animator mAttachToWindowAnim;
                private boolean mIsAttachedToWindow;

                @Override
                public void createActivityController(long transitionLength,
                        @InteractionType int interactionType) {
                    createActivityControllerInternal(activity, activityVisible, fromState,
                            transitionLength, interactionType, callback);
                }

                @Override
                public void onTransitionCancelled() {
                    activity.getStateManager().goToState(startState, false /* animate */);
                }

                @Override
                public void setShelfState(ShelfAnimState shelfState, Interpolator interpolator,
                        long duration) {
                    if (mShelfState == shelfState) {
                        return;
                    }
                    mShelfState = shelfState;
                    Log.d("ACH", "Set shelf state to " + mShelfState);
                    if (mShelfAnim != null) {
                        mShelfAnim.cancel();
                    }
                    if (mShelfState == ShelfAnimState.CANCEL) {
                        return;
                    }
                    float shelfHiddenProgress = BACKGROUND_APP.getVerticalProgress(activity);
                    float shelfOverviewProgress = OVERVIEW.getVerticalProgress(activity);
                    float shelfPeekingProgress = shelfHiddenProgress
                            - (shelfHiddenProgress - shelfOverviewProgress) * 0.25f;
                    float toProgress = mShelfState == ShelfAnimState.HIDE
                            ? shelfHiddenProgress
                            : mShelfState == ShelfAnimState.PEEK
                                    ? shelfPeekingProgress
                                    : shelfOverviewProgress;
                    mShelfAnim = createShelfProgressAnim(activity, toProgress);
                    mShelfAnim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mShelfAnim = null;
                        }
                    });
                    mShelfAnim.setInterpolator(interpolator);
                    mShelfAnim.setDuration(duration);
                    mShelfAnim.start();
                }

                @Override
                public void setRecentsAttachedToAppWindow(boolean attached, boolean animate) {
                    if (mIsAttachedToWindow == attached && animate) {
                        return;
                    }
                    mIsAttachedToWindow = attached;
                    if (mAttachToWindowAnim != null) {
                        mAttachToWindowAnim.cancel();
                    }
                    mAttachToWindowAnim = ObjectAnimator.ofFloat(activity.getOverviewPanel(),
                            RecentsView.CONTENT_ALPHA, attached ? 1 : 0);
                    mAttachToWindowAnim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mAttachToWindowAnim = null;
                        }
                    });
                    mAttachToWindowAnim.setInterpolator(ACCEL_DEACCEL);
                    mAttachToWindowAnim.setDuration(animate ? RECENTS_ATTACH_DURATION : 0);
                    mAttachToWindowAnim.start();
                }
            };
        }

        private Animator createShelfProgressAnim(Launcher activity, float ... progressValues) {
            AnimatorSet anim = new AnimatorSet();
            AllAppsTransitionController controller = activity.getAllAppsController();

            ObjectAnimator shiftAnim = ObjectAnimator.ofFloat(
                    controller, ALL_APPS_PROGRESS, progressValues);
            shiftAnim.setInterpolator(LINEAR);
            anim.play(shiftAnim);

            ObjectAnimator scrimAnim = ObjectAnimator.ofFloat(
                    controller, SCRIM_PROGRESS, progressValues);
            scrimAnim.setInterpolator(LINEAR);
            anim.play(scrimAnim);

            return anim;
        }

        private void createActivityControllerInternal(Launcher activity, boolean wasVisible,
                LauncherState startState, long transitionLength,
                @InteractionType int interactionType,
                Consumer<AnimatorPlaybackController> callback) {
            LauncherState endState = interactionType == INTERACTION_QUICK_SCRUB
                    ? FAST_OVERVIEW : OVERVIEW;
            if (wasVisible && startState != BACKGROUND_APP) {
                DeviceProfile dp = activity.getDeviceProfile();
                long accuracy = 2 * Math.max(dp.widthPx, dp.heightPx);
                callback.accept(activity.getStateManager()
                        .createAnimationToNewWorkspace(startState, endState, accuracy));
                return;
            }
            if (startState == endState) {
                return;
            }

            AnimatorSet anim = new AnimatorSet();

            if (!activity.getDeviceProfile().isVerticalBarLayout() && !Utilities.getLawnchairPrefs(activity).getSwipeHome()) {
                AllAppsTransitionController controller = activity.getAllAppsController();
                float scrollRange = Math.max(controller.getShiftRange(), 1);
                float progressDelta = (transitionLength / scrollRange);

                float endProgress = endState.getVerticalProgress(activity);
                float startProgress = startState.getVerticalProgress(activity);
                ObjectAnimator shiftAnim = ObjectAnimator.ofFloat(
                        controller, ALL_APPS_PROGRESS, startProgress, endProgress);
                shiftAnim.setInterpolator(LINEAR);
                anim.play(shiftAnim);

                float scrimEndProgress = Utilities.getScrimProgress(activity, endState, endProgress);
                ObjectAnimator scrimAnim = ObjectAnimator.ofFloat(
                        controller, SCRIM_PROGRESS, scrimEndProgress + progressDelta, scrimEndProgress);
                scrimAnim.setInterpolator(LINEAR);
                anim.play(scrimAnim);
            }

            if (interactionType == INTERACTION_NORMAL) {
                playScaleDownAnim(anim, activity);
            }

            anim.setDuration(transitionLength * 2);
            activity.getStateManager().setCurrentAnimation(anim);
            AnimatorPlaybackController controller =
                    AnimatorPlaybackController.wrap(anim, transitionLength * 2);

            // Since we are changing the start position of the UI, reapply the state, at the end
            controller.setEndAction(() ->
                    activity.getStateManager().goToState(
                            controller.getProgressFraction() > 0.5 ? endState : startState, false));
            callback.accept(controller);
        }

        /**
         * Scale down recents from the center task being full screen to being in overview.
         */
        private void playScaleDownAnim(AnimatorSet anim, Launcher launcher) {
            RecentsView recentsView = launcher.getOverviewPanel();
            TaskView v = recentsView.getTaskViewAt(recentsView.getCurrentPage());
            if (v == null) {
                return;
            }
            ClipAnimationHelper clipHelper = new ClipAnimationHelper(launcher);
            clipHelper.fromTaskThumbnailView(v.getThumbnail(), (RecentsView) v.getParent(), null);
            if (!clipHelper.getSourceRect().isEmpty() && !clipHelper.getTargetRect().isEmpty()) {
                float fromScale = clipHelper.getSourceRect().width()
                        / clipHelper.getTargetRect().width();
                float fromTranslationY = clipHelper.getSourceRect().centerY()
                        - clipHelper.getTargetRect().centerY();
                Animator scale = ObjectAnimator.ofFloat(recentsView, SCALE_PROPERTY, fromScale, 1);
                Animator translateY = ObjectAnimator.ofFloat(recentsView, TRANSLATION_Y,
                        fromTranslationY, 0);
                scale.setInterpolator(LINEAR);
                translateY.setInterpolator(LINEAR);
                anim.playTogether(scale, translateY);
            }
        }

        @Override
        public ActivityInitListener createActivityInitListener(
                BiPredicate<Launcher, Boolean> onInitListener) {
            return new LauncherInitListener(onInitListener);
        }

        @Nullable
        @Override
        public Launcher getCreatedActivity() {
            LauncherAppState app = LauncherAppState.getInstanceNoCreate();
            if (app == null) {
                return null;
            }
            return (Launcher) app.getModel().getCallback();
        }

        @Nullable
        @UiThread
        private Launcher getVisibleLaucher() {
            Launcher launcher = getCreatedActivity();
            return (launcher != null) && launcher.isStarted() && launcher.hasWindowFocus() ?
                    launcher : null;
        }

        @Nullable
        @Override
        public RecentsView getVisibleRecentsView() {
            Launcher launcher = getVisibleLaucher();
            return launcher != null && launcher.getStateManager().getState().overviewUi
                    ? launcher.getOverviewPanel() : null;
        }

        @Override
        public boolean switchToRecentsIfVisible(boolean fromRecentsButton) {
            Launcher launcher = getVisibleLaucher();
            if (launcher != null) {
                if (fromRecentsButton) {
                    launcher.getUserEventDispatcher().logActionCommand(
                            LauncherLogProto.Action.Command.RECENTS_BUTTON,
                            getContainerType(),
                            LauncherLogProto.ContainerType.TASKSWITCHER);
                }
                launcher.getStateManager().goToState(OVERVIEW);
                return true;
            }
            return false;
        }

        @Override
        public boolean deferStartingActivity(int downHitTarget) {
            return downHitTarget == HIT_TARGET_BACK || downHitTarget == HIT_TARGET_ROTATION;
        }

        @Override
        public Rect getOverviewWindowBounds(Rect homeBounds, RemoteAnimationTargetCompat target) {
            return homeBounds;
        }

        @Override
        public boolean shouldMinimizeSplitScreen() {
            return true;
        }

        @Override
        public boolean supportsLongSwipe(Launcher activity) {
            return !activity.getDeviceProfile().isVerticalBarLayout();
        }

        @Override
        public LongSwipeHelper getLongSwipeController(Launcher activity,
                RemoteAnimationTargetSet targetSet) {
            if (activity.getDeviceProfile().isVerticalBarLayout()) {
                return null;
            }
            return new LongSwipeHelper(activity, targetSet);
        }

        @Override
        public AlphaProperty getAlphaProperty(Launcher activity) {
            return activity.getDragLayer().getAlphaProperty(DragLayer.ALPHA_INDEX_SWIPE_UP);
        }

        @Override
        public int getContainerType() {
            final Launcher launcher = getVisibleLaucher();
            return launcher != null ? launcher.getStateManager().getState().containerType
                    : LauncherLogProto.ContainerType.APP;
        }
    }

    class FallbackActivityControllerHelper implements ActivityControlHelper<RecentsActivity> {

        private final ComponentName mHomeComponent;
        private final Handler mUiHandler = new Handler(Looper.getMainLooper());

        public FallbackActivityControllerHelper(ComponentName homeComponent) {
            mHomeComponent = homeComponent;
        }

        @Override
        public void onQuickInteractionStart(RecentsActivity activity, RunningTaskInfo taskInfo,
                boolean activityVisible) {
            QuickScrubController controller = activity.<RecentsView>getOverviewPanel()
                    .getQuickScrubController();

            // TODO: match user is as well
            boolean startingFromHome = !activityVisible &&
                    (taskInfo == null || Objects.equals(taskInfo.topActivity, mHomeComponent));
            controller.onQuickScrubStart(startingFromHome, this);
            if (activityVisible) {
                mUiHandler.postDelayed(controller::onFinishedTransitionToQuickScrub,
                        OVERVIEW_TRANSITION_MS);
            }
        }

        @Override
        public float getTranslationYForQuickScrub(TransformedRect targetRect, DeviceProfile dp,
                Context context) {
            return 0;
        }

        @Override
        public void executeOnWindowAvailable(RecentsActivity activity, Runnable action) {
            action.run();
        }

        @Override
        public void onTransitionCancelled(RecentsActivity activity, boolean activityVisible) {
            // TODO:
        }

        @Override
        public int getSwipeUpDestinationAndLength(DeviceProfile dp, Context context,
                @InteractionType int interactionType, TransformedRect outRect) {
            LayoutUtils.calculateFallbackTaskSize(context, dp, outRect.rect);
            if (dp.isVerticalBarLayout()) {
                Rect targetInsets = dp.getInsets();
                int hotseatInset = dp.isSeascape() ? targetInsets.left : targetInsets.right;
                return dp.hotseatBarSizePx + hotseatInset;
            } else {
                return dp.heightPx - outRect.rect.bottom;
            }
        }

        @Override
        public void onSwipeUpComplete(RecentsActivity activity) {
            // TODO:
        }

        @NonNull
        @Override
        public HomeAnimationFactory prepareHomeUI(RecentsActivity activity) {
            RecentsView recentsView = activity.getOverviewPanel();

            return new HomeAnimationFactory() {
                @NonNull
                @Override
                public RectF getWindowTargetRect() {
                    float centerX = recentsView.getPivotX();
                    float centerY = recentsView.getPivotY();
                    return new RectF(centerX, centerY, centerX, centerY);
                }

                @NonNull
                @Override
                public Animator createActivityAnimationToHome() {
                    Animator anim = ObjectAnimator.ofFloat(recentsView, CONTENT_ALPHA, 0);
                    anim.addListener(new AnimationSuccessListener() {
                        @Override
                        public void onAnimationSuccess(Animator animator) {
                            recentsView.startHome();
                        }
                    });
                    return anim;
                }
            };
        }

        @Override
        public AnimationFactory prepareRecentsUI(RecentsActivity activity, boolean activityVisible,
                boolean animateActivity, Consumer<AnimatorPlaybackController> callback) {
            if (activityVisible) {
                return (transitionLength, interactionType) -> { };
            }

            RecentsView rv = activity.getOverviewPanel();
            rv.setContentAlpha(0);

            return new AnimationFactory() {

                boolean isAnimatingHome = false;

                @Override
                public void onRemoteAnimationReceived(RemoteAnimationTargetSet targets) {
                    isAnimatingHome = targets != null && targets.isAnimatingHome();
                    if (!isAnimatingHome) {
                        rv.setContentAlpha(1);
                    }
                    createActivityController(getSwipeUpDestinationAndLength(
                            activity.getDeviceProfile(), activity, INTERACTION_NORMAL,
                            new TransformedRect()), INTERACTION_NORMAL);
                }

                @Override
                public void createActivityController(long transitionLength, int interactionType) {
                    if (!isAnimatingHome) {
                        return;
                    }

                    ObjectAnimator anim = ObjectAnimator.ofFloat(rv, CONTENT_ALPHA, 0, 1);
                    anim.setDuration(transitionLength).setInterpolator(LINEAR);
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.play(anim);
                    callback.accept(AnimatorPlaybackController.wrap(animatorSet, transitionLength));
                }
            };
        }

        @Override
        public LayoutListener createLayoutListener(RecentsActivity activity) {
            // We do not change anything as part of layout changes in fallback activity. Return a
            // default layout listener.
            return new LayoutListener() {
                @Override
                public void open() { }

                @Override
                public void setHandler(WindowTransformSwipeHandler handler) { }

                @Override
                public void finish() { }

                @Override
                public void update(boolean shouldFinish, boolean isLongSwipe, RectF currentRect) { }
            };
        }

        @Override
        public ActivityInitListener createActivityInitListener(
                BiPredicate<RecentsActivity, Boolean> onInitListener) {
            return new RecentsActivityTracker(onInitListener);
        }

        @Nullable
        @Override
        public RecentsActivity getCreatedActivity() {
            return RecentsActivityTracker.getCurrentActivity();
        }

        @Nullable
        @Override
        public RecentsView getVisibleRecentsView() {
            RecentsActivity activity = getCreatedActivity();
            if (activity != null && activity.hasWindowFocus()) {
                return activity.getOverviewPanel();
            }
            return null;
        }

        @Override
        public boolean switchToRecentsIfVisible(boolean fromRecentsButton) {
            return false;
        }

        @Override
        public boolean deferStartingActivity(int downHitTarget) {
            // Always defer starting the activity when using fallback
            return true;
        }

        @Override
        public Rect getOverviewWindowBounds(Rect homeBounds, RemoteAnimationTargetCompat target) {
            // TODO: Remove this once b/77875376 is fixed
            return target.sourceContainerBounds;
        }

        @Override
        public boolean shouldMinimizeSplitScreen() {
            // TODO: Remove this once b/77875376 is fixed
            return false;
        }

        @Override
        public boolean supportsLongSwipe(RecentsActivity activity) {
            return false;
        }

        @Override
        public LongSwipeHelper getLongSwipeController(RecentsActivity activity,
                RemoteAnimationTargetSet targetSet) {
            return null;
        }

        @Override
        public AlphaProperty getAlphaProperty(RecentsActivity activity) {
            return activity.getDragLayer().getAlphaProperty(0);
        }

        @Override
        public int getContainerType() {
            return LauncherLogProto.ContainerType.SIDELOADED_LAUNCHER;
        }
    }

    interface LayoutListener {

        void open();

        void setHandler(WindowTransformSwipeHandler handler);

        void finish();

        void update(boolean shouldFinish, boolean isLongSwipe, RectF currentRect);
    }

    interface ActivityInitListener {

        void register();

        void unregister();

        void registerAndStartActivity(Intent intent, RemoteAnimationProvider animProvider,
                Context context, Handler handler, long duration);
    }

    interface AnimationFactory {

        enum ShelfAnimState {
            HIDE(true), PEEK(true), OVERVIEW(false), CANCEL(false);

            ShelfAnimState(boolean shouldPreformHaptic) {
                this.shouldPreformHaptic = shouldPreformHaptic;
            }

            public final boolean shouldPreformHaptic;
        }

        default void onRemoteAnimationReceived(RemoteAnimationTargetSet targets) { }

        void createActivityController(long transitionLength, @InteractionType int interactionType);

        default void onTransitionCancelled() { }

        default void setShelfState(ShelfAnimState animState, Interpolator interpolator,
                long duration) { }

        /**
         * @param attached Whether to show RecentsView alongside the app window. If false, recents
         *                 will be hidden by some property we can animate, e.g. alpha.
         * @param animate Whether to animate recents to/from its new attached state.
         */
        default void setRecentsAttachedToAppWindow(boolean attached, boolean animate) { }
    }

    interface HomeAnimationFactory {

        /** Return the floating view that will animate in sync with the closing window. */
        default @Nullable View getFloatingView() {
            return null;
        }

        @NonNull RectF getWindowTargetRect();

        @NonNull Animator createActivityAnimationToHome();
    }
}
