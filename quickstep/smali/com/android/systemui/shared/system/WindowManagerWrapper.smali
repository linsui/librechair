.class public Lcom/android/systemui/shared/system/WindowManagerWrapper;
.super Ljava/lang/Object;
.source "WindowManagerWrapper.java"


# static fields
.field public static final ACTIVITY_TYPE_STANDARD:I = 0x1

.field public static final NAV_BAR_POS_BOTTOM:I = 0x4

.field public static final NAV_BAR_POS_INVALID:I = -0x1

.field public static final NAV_BAR_POS_LEFT:I = 0x1

.field public static final NAV_BAR_POS_RIGHT:I = 0x2

.field private static final TAG:Ljava/lang/String; = "WindowManagerWrapper"

.field public static final TRANSIT_ACTIVITY_CLOSE:I = 0x7

.field public static final TRANSIT_ACTIVITY_OPEN:I = 0x6

.field public static final TRANSIT_ACTIVITY_RELAUNCH:I = 0x12

.field public static final TRANSIT_DOCK_TASK_FROM_RECENTS:I = 0x13

.field public static final TRANSIT_KEYGUARD_GOING_AWAY:I = 0x14

.field public static final TRANSIT_KEYGUARD_GOING_AWAY_ON_WALLPAPER:I = 0x15

.field public static final TRANSIT_KEYGUARD_OCCLUDE:I = 0x16

.field public static final TRANSIT_KEYGUARD_UNOCCLUDE:I = 0x17

.field public static final TRANSIT_NONE:I = 0x0

.field public static final TRANSIT_TASK_CLOSE:I = 0x9

.field public static final TRANSIT_TASK_IN_PLACE:I = 0x11

.field public static final TRANSIT_TASK_OPEN:I = 0x8

.field public static final TRANSIT_TASK_OPEN_BEHIND:I = 0x10

.field public static final TRANSIT_TASK_TO_BACK:I = 0xb

.field public static final TRANSIT_TASK_TO_FRONT:I = 0xa

.field public static final TRANSIT_UNSET:I = -0x1

.field public static final TRANSIT_WALLPAPER_CLOSE:I = 0xc

.field public static final TRANSIT_WALLPAPER_INTRA_CLOSE:I = 0xf

.field public static final TRANSIT_WALLPAPER_INTRA_OPEN:I = 0xe

.field public static final TRANSIT_WALLPAPER_OPEN:I = 0xd

.field public static final WINDOWING_MODE_FREEFORM:I = 0x5

.field public static final WINDOWING_MODE_FULLSCREEN:I = 0x1

.field public static final WINDOWING_MODE_PINNED:I = 0x2

.field public static final WINDOWING_MODE_SPLIT_SCREEN_PRIMARY:I = 0x3

.field public static final WINDOWING_MODE_SPLIT_SCREEN_SECONDARY:I = 0x4

.field public static final WINDOWING_MODE_UNDEFINED:I

.field private static final sInstance:Lcom/android/systemui/shared/system/WindowManagerWrapper;


# direct methods
.method static constructor <clinit>()V
    .registers 1

    .line 82
    new-instance v0, Lcom/android/systemui/shared/system/WindowManagerWrapper;

    invoke-direct {v0}, Lcom/android/systemui/shared/system/WindowManagerWrapper;-><init>()V

    sput-object v0, Lcom/android/systemui/shared/system/WindowManagerWrapper;->sInstance:Lcom/android/systemui/shared/system/WindowManagerWrapper;

    return-void
.end method

.method public constructor <init>()V
    .registers 1

    .line 36
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method public static getInstance()Lcom/android/systemui/shared/system/WindowManagerWrapper;
    .registers 1

    .line 85
    sget-object v0, Lcom/android/systemui/shared/system/WindowManagerWrapper;->sInstance:Lcom/android/systemui/shared/system/WindowManagerWrapper;

    return-object v0
.end method


# virtual methods
.method public getNavBarPosition()I
    .registers 4

    .line 163
    :try_start_0
    invoke-static {}, Landroid/view/WindowManagerGlobal;->getWindowManagerService()Landroid/view/IWindowManager;

    move-result-object v0

    invoke-interface {v0}, Landroid/view/IWindowManager;->getNavBarPosition()I

    move-result v0
    :try_end_8
    .catch Landroid/os/RemoteException; {:try_start_0 .. :try_end_8} :catch_9

    return v0

    .line 164
    :catch_9
    move-exception v0

    .line 165
    .local v0, "e":Landroid/os/RemoteException;
    const-string v1, "WindowManagerWrapper"

    const-string v2, "Failed to get nav bar position"

    invoke-static {v1, v2}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;)I

    .line 167
    .end local v0    # "e":Landroid/os/RemoteException;
    const/4 v0, -0x1

    return v0
.end method

.method public getStableInsets(Landroid/graphics/Rect;)V
    .registers 5
    .param p1, "outStableInsets"    # Landroid/graphics/Rect;

    .line 93
    :try_start_0
    invoke-static {}, Landroid/view/WindowManagerGlobal;->getWindowManagerService()Landroid/view/IWindowManager;

    move-result-object v0

    const/4 v1, 0x0

    invoke-interface {v0, v1, p1}, Landroid/view/IWindowManager;->getStableInsets(ILandroid/graphics/Rect;)V
    :try_end_8
    .catch Landroid/os/RemoteException; {:try_start_0 .. :try_end_8} :catch_9

    .line 97
    goto :goto_11

    .line 95
    :catch_9
    move-exception v0

    .line 96
    .local v0, "e":Landroid/os/RemoteException;
    const-string v1, "WindowManagerWrapper"

    const-string v2, "Failed to get stable insets"

    invoke-static {v1, v2, v0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    .line 98
    .end local v0    # "e":Landroid/os/RemoteException;
    :goto_11
    return-void
.end method

.method public overridePendingAppTransitionMultiThumbFuture(Lcom/android/systemui/shared/recents/view/AppTransitionAnimationSpecsFuture;Ljava/lang/Runnable;Landroid/os/Handler;Z)V
    .registers 8
    .param p1, "animationSpecFuture"    # Lcom/android/systemui/shared/recents/view/AppTransitionAnimationSpecsFuture;
    .param p2, "animStartedCallback"    # Ljava/lang/Runnable;
    .param p3, "animStartedCallbackHandler"    # Landroid/os/Handler;
    .param p4, "scaleUp"    # Z

    .line 107
    :try_start_0
    invoke-static {}, Landroid/view/WindowManagerGlobal;->getWindowManagerService()Landroid/view/IWindowManager;

    move-result-object v0

    .line 108
    invoke-virtual {p1}, Lcom/android/systemui/shared/recents/view/AppTransitionAnimationSpecsFuture;->getFuture()Landroid/view/IAppTransitionAnimationSpecsFuture;

    move-result-object v1

    .line 109
    invoke-static {p3, p2}, Lcom/android/systemui/shared/recents/view/RecentsTransition;->wrapStartedListener(Landroid/os/Handler;Ljava/lang/Runnable;)Landroid/os/IRemoteCallback;

    move-result-object v2

    .line 108
    invoke-interface {v0, v1, v2, p4}, Landroid/view/IWindowManager;->overridePendingAppTransitionMultiThumbFuture(Landroid/view/IAppTransitionAnimationSpecsFuture;Landroid/os/IRemoteCallback;Z)V
    :try_end_f
    .catch Landroid/os/RemoteException; {:try_start_0 .. :try_end_f} :catch_10

    .line 113
    goto :goto_18

    .line 111
    :catch_10
    move-exception v0

    .line 112
    .local v0, "e":Landroid/os/RemoteException;
    const-string v1, "WindowManagerWrapper"

    const-string v2, "Failed to override pending app transition (multi-thumbnail future): "

    invoke-static {v1, v2, v0}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    .line 114
    .end local v0    # "e":Landroid/os/RemoteException;
    :goto_18
    return-void
.end method

.method public overridePendingAppTransitionRemote(Lcom/android/systemui/shared/system/RemoteAnimationAdapterCompat;)V
    .registers 5
    .param p1, "remoteAnimationAdapter"    # Lcom/android/systemui/shared/system/RemoteAnimationAdapterCompat;

    .line 119
    :try_start_0
    invoke-static {}, Landroid/view/WindowManagerGlobal;->getWindowManagerService()Landroid/view/IWindowManager;

    move-result-object v0

    .line 120
    invoke-virtual {p1}, Lcom/android/systemui/shared/system/RemoteAnimationAdapterCompat;->getWrapped()Landroid/view/RemoteAnimationAdapter;

    move-result-object v1

    .line 119
    invoke-interface {v0, v1}, Landroid/view/IWindowManager;->overridePendingAppTransitionRemote(Landroid/view/RemoteAnimationAdapter;)V
    :try_end_b
    .catch Landroid/os/RemoteException; {:try_start_0 .. :try_end_b} :catch_c

    .line 123
    goto :goto_14

    .line 121
    :catch_c
    move-exception v0

    .line 122
    .local v0, "e":Landroid/os/RemoteException;
    const-string v1, "WindowManagerWrapper"

    const-string v2, "Failed to override pending app transition (remote): "

    invoke-static {v1, v2, v0}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    .line 124
    .end local v0    # "e":Landroid/os/RemoteException;
    :goto_14
    return-void
.end method

.method public setNavBarVirtualKeyHapticFeedbackEnabled(Z)V
    .registers 5
    .param p1, "enabled"    # Z

    .line 131
    :try_start_0
    invoke-static {}, Landroid/view/WindowManagerGlobal;->getWindowManagerService()Landroid/view/IWindowManager;

    move-result-object v0

    .line 132
    invoke-interface {v0, p1}, Landroid/view/IWindowManager;->setNavBarVirtualKeyHapticFeedbackEnabled(Z)V
    :try_end_7
    .catch Landroid/os/RemoteException; {:try_start_0 .. :try_end_7} :catch_8

    .line 135
    goto :goto_10

    .line 133
    :catch_8
    move-exception v0

    .line 134
    .local v0, "e":Landroid/os/RemoteException;
    const-string v1, "WindowManagerWrapper"

    const-string v2, "Failed to enable or disable navigation bar button haptics: "

    invoke-static {v1, v2, v0}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    .line 136
    .end local v0    # "e":Landroid/os/RemoteException;
    :goto_10
    return-void
.end method

.method public setRecentsVisibility(Z)V
    .registers 5
    .param p1, "visible"    # Z

    .line 148
    :try_start_0
    invoke-static {}, Landroid/view/WindowManagerGlobal;->getWindowManagerService()Landroid/view/IWindowManager;

    move-result-object v0

    invoke-interface {v0, p1}, Landroid/view/IWindowManager;->setRecentsVisibility(Z)V
    :try_end_7
    .catch Landroid/os/RemoteException; {:try_start_0 .. :try_end_7} :catch_8

    .line 151
    goto :goto_10

    .line 149
    :catch_8
    move-exception v0

    .line 150
    .local v0, "e":Landroid/os/RemoteException;
    const-string v1, "WindowManagerWrapper"

    const-string v2, "Failed to set recents visibility"

    invoke-static {v1, v2}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;)I

    .line 152
    .end local v0    # "e":Landroid/os/RemoteException;
    :goto_10
    return-void
.end method

.method public setShelfHeight(ZI)V
    .registers 6
    .param p1, "visible"    # Z
    .param p2, "shelfHeight"    # I

    .line 140
    :try_start_0
    invoke-static {}, Landroid/view/WindowManagerGlobal;->getWindowManagerService()Landroid/view/IWindowManager;

    move-result-object v0

    invoke-interface {v0, p1, p2}, Landroid/view/IWindowManager;->setShelfHeight(ZI)V
    :try_end_7
    .catch Landroid/os/RemoteException; {:try_start_0 .. :try_end_7} :catch_8

    .line 143
    goto :goto_10

    .line 141
    :catch_8
    move-exception v0

    .line 142
    .local v0, "e":Landroid/os/RemoteException;
    const-string v1, "WindowManagerWrapper"

    const-string v2, "Failed to set shelf height"

    invoke-static {v1, v2}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;)I

    .line 144
    .end local v0    # "e":Landroid/os/RemoteException;
    :goto_10
    return-void
.end method
