.class public Lcom/android/systemui/shared/system/RecentsAnimationControllerCompat;
.super Ljava/lang/Object;
.source "RecentsAnimationControllerCompat.java"


# static fields
.field private static final TAG:Ljava/lang/String;


# instance fields
.field private mAnimationController:Landroid/view/IRecentsAnimationController;


# direct methods
.method static constructor <clinit>()V
    .registers 1

    .line 28
    const-class v0, Lcom/android/systemui/shared/system/RecentsAnimationControllerCompat;

    invoke-virtual {v0}, Ljava/lang/Class;->getSimpleName()Ljava/lang/String;

    move-result-object v0

    sput-object v0, Lcom/android/systemui/shared/system/RecentsAnimationControllerCompat;->TAG:Ljava/lang/String;

    return-void
.end method

.method public constructor <init>()V
    .registers 1

    .line 32
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method public constructor <init>(Landroid/view/IRecentsAnimationController;)V
    .registers 2
    .param p1, "animationController"    # Landroid/view/IRecentsAnimationController;

    .line 34
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 35
    iput-object p1, p0, Lcom/android/systemui/shared/system/RecentsAnimationControllerCompat;->mAnimationController:Landroid/view/IRecentsAnimationController;

    .line 36
    return-void
.end method


# virtual methods
.method public finish(Z)V
    .registers 5
    .param p1, "toHome"    # Z

    .line 82
    :try_start_0
    iget-object v0, p0, Lcom/android/systemui/shared/system/RecentsAnimationControllerCompat;->mAnimationController:Landroid/view/IRecentsAnimationController;

    invoke-interface {v0, p1}, Landroid/view/IRecentsAnimationController;->finish(Z)V
    :try_end_5
    .catch Landroid/os/RemoteException; {:try_start_0 .. :try_end_5} :catch_6

    .line 85
    goto :goto_e

    .line 83
    :catch_6
    move-exception v0

    .line 84
    .local v0, "e":Landroid/os/RemoteException;
    sget-object v1, Lcom/android/systemui/shared/system/RecentsAnimationControllerCompat;->TAG:Ljava/lang/String;

    const-string v2, "Failed to finish recents animation"

    invoke-static {v1, v2, v0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    .line 86
    .end local v0    # "e":Landroid/os/RemoteException;
    :goto_e
    return-void
.end method

.method public hideCurrentInputMethod()V
    .registers 4

    .line 74
    :try_start_0
    iget-object v0, p0, Lcom/android/systemui/shared/system/RecentsAnimationControllerCompat;->mAnimationController:Landroid/view/IRecentsAnimationController;

    invoke-interface {v0}, Landroid/view/IRecentsAnimationController;->hideCurrentInputMethod()V
    :try_end_5
    .catch Landroid/os/RemoteException; {:try_start_0 .. :try_end_5} :catch_6

    .line 77
    goto :goto_e

    .line 75
    :catch_6
    move-exception v0

    .line 76
    .local v0, "e":Landroid/os/RemoteException;
    sget-object v1, Lcom/android/systemui/shared/system/RecentsAnimationControllerCompat;->TAG:Ljava/lang/String;

    const-string v2, "Failed to set hide input method"

    invoke-static {v1, v2, v0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    .line 78
    .end local v0    # "e":Landroid/os/RemoteException;
    :goto_e
    return-void
.end method

.method public screenshotTask(I)Lcom/android/systemui/shared/recents/model/ThumbnailData;
    .registers 5
    .param p1, "taskId"    # I

    .line 40
    :try_start_0
    iget-object v0, p0, Lcom/android/systemui/shared/system/RecentsAnimationControllerCompat;->mAnimationController:Landroid/view/IRecentsAnimationController;

    invoke-interface {v0, p1}, Landroid/view/IRecentsAnimationController;->screenshotTask(I)Landroid/app/ActivityManager$TaskSnapshot;

    move-result-object v0

    .line 41
    .local v0, "snapshot":Landroid/app/ActivityManager$TaskSnapshot;
    if-eqz v0, :cond_e

    new-instance v1, Lcom/android/systemui/shared/recents/model/ThumbnailData;

    invoke-direct {v1, v0}, Lcom/android/systemui/shared/recents/model/ThumbnailData;-><init>(Landroid/app/ActivityManager$TaskSnapshot;)V

    goto :goto_13

    :cond_e
    new-instance v1, Lcom/android/systemui/shared/recents/model/ThumbnailData;

    invoke-direct {v1}, Lcom/android/systemui/shared/recents/model/ThumbnailData;-><init>()V
    :try_end_13
    .catch Landroid/os/RemoteException; {:try_start_0 .. :try_end_13} :catch_14

    :goto_13
    return-object v1

    .line 42
    .end local v0    # "snapshot":Landroid/app/ActivityManager$TaskSnapshot;
    :catch_14
    move-exception v0

    .line 43
    .local v0, "e":Landroid/os/RemoteException;
    sget-object v1, Lcom/android/systemui/shared/system/RecentsAnimationControllerCompat;->TAG:Ljava/lang/String;

    const-string v2, "Failed to screenshot task"

    invoke-static {v1, v2, v0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    .line 44
    new-instance v1, Lcom/android/systemui/shared/recents/model/ThumbnailData;

    invoke-direct {v1}, Lcom/android/systemui/shared/recents/model/ThumbnailData;-><init>()V

    return-object v1
.end method

.method public setAnimationTargetsBehindSystemBars(Z)V
    .registers 5
    .param p1, "behindSystemBars"    # Z

    .line 58
    :try_start_0
    iget-object v0, p0, Lcom/android/systemui/shared/system/RecentsAnimationControllerCompat;->mAnimationController:Landroid/view/IRecentsAnimationController;

    invoke-interface {v0, p1}, Landroid/view/IRecentsAnimationController;->setAnimationTargetsBehindSystemBars(Z)V
    :try_end_5
    .catch Landroid/os/RemoteException; {:try_start_0 .. :try_end_5} :catch_6

    .line 61
    goto :goto_e

    .line 59
    :catch_6
    move-exception v0

    .line 60
    .local v0, "e":Landroid/os/RemoteException;
    sget-object v1, Lcom/android/systemui/shared/system/RecentsAnimationControllerCompat;->TAG:Ljava/lang/String;

    const-string v2, "Failed to set whether animation targets are behind system bars"

    invoke-static {v1, v2, v0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    .line 62
    .end local v0    # "e":Landroid/os/RemoteException;
    :goto_e
    return-void
.end method

.method public setInputConsumerEnabled(Z)V
    .registers 5
    .param p1, "enabled"    # Z

    .line 50
    :try_start_0
    iget-object v0, p0, Lcom/android/systemui/shared/system/RecentsAnimationControllerCompat;->mAnimationController:Landroid/view/IRecentsAnimationController;

    invoke-interface {v0, p1}, Landroid/view/IRecentsAnimationController;->setInputConsumerEnabled(Z)V
    :try_end_5
    .catch Landroid/os/RemoteException; {:try_start_0 .. :try_end_5} :catch_6

    .line 53
    goto :goto_e

    .line 51
    :catch_6
    move-exception v0

    .line 52
    .local v0, "e":Landroid/os/RemoteException;
    sget-object v1, Lcom/android/systemui/shared/system/RecentsAnimationControllerCompat;->TAG:Ljava/lang/String;

    const-string v2, "Failed to set input consumer enabled state"

    invoke-static {v1, v2, v0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    .line 54
    .end local v0    # "e":Landroid/os/RemoteException;
    :goto_e
    return-void
.end method

.method public setSplitScreenMinimized(Z)V
    .registers 5
    .param p1, "minimized"    # Z

    .line 66
    :try_start_0
    iget-object v0, p0, Lcom/android/systemui/shared/system/RecentsAnimationControllerCompat;->mAnimationController:Landroid/view/IRecentsAnimationController;

    invoke-interface {v0, p1}, Landroid/view/IRecentsAnimationController;->setSplitScreenMinimized(Z)V
    :try_end_5
    .catch Landroid/os/RemoteException; {:try_start_0 .. :try_end_5} :catch_6

    .line 69
    goto :goto_e

    .line 67
    :catch_6
    move-exception v0

    .line 68
    .local v0, "e":Landroid/os/RemoteException;
    sget-object v1, Lcom/android/systemui/shared/system/RecentsAnimationControllerCompat;->TAG:Ljava/lang/String;

    const-string v2, "Failed to set minimize dock"

    invoke-static {v1, v2, v0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    .line 70
    .end local v0    # "e":Landroid/os/RemoteException;
    :goto_e
    return-void
.end method
