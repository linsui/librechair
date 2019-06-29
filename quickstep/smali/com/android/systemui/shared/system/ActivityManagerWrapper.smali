.class public Lcom/android/systemui/shared/system/ActivityManagerWrapper;
.super Ljava/lang/Object;
.source "ActivityManagerWrapper.java"


# static fields
.field public static final CLOSE_SYSTEM_WINDOWS_REASON_RECENTS:Ljava/lang/String; = "recentapps"

.field private static final TAG:Ljava/lang/String; = "ActivityManagerWrapper"

.field private static final sInstance:Lcom/android/systemui/shared/system/ActivityManagerWrapper;


# instance fields
.field private final mBackgroundExecutor:Lcom/android/systemui/shared/system/BackgroundExecutor;

.field private final mPackageManager:Landroid/content/pm/PackageManager;

.field private final mTaskStackChangeListeners:Lcom/android/systemui/shared/system/TaskStackChangeListeners;


# direct methods
.method static constructor <clinit>()V
    .registers 1

    .line 72
    new-instance v0, Lcom/android/systemui/shared/system/ActivityManagerWrapper;

    invoke-direct {v0}, Lcom/android/systemui/shared/system/ActivityManagerWrapper;-><init>()V

    sput-object v0, Lcom/android/systemui/shared/system/ActivityManagerWrapper;->sInstance:Lcom/android/systemui/shared/system/ActivityManagerWrapper;

    return-void
.end method

.method private constructor <init>()V
    .registers 4

    .line 81
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 82
    invoke-static {}, Landroid/app/AppGlobals;->getInitialApplication()Landroid/app/Application;

    move-result-object v0

    .line 83
    .local v0, "context":Landroid/content/Context;
    invoke-virtual {v0}, Landroid/content/Context;->getPackageManager()Landroid/content/pm/PackageManager;

    move-result-object v1

    iput-object v1, p0, Lcom/android/systemui/shared/system/ActivityManagerWrapper;->mPackageManager:Landroid/content/pm/PackageManager;

    .line 84
    invoke-static {}, Lcom/android/systemui/shared/system/BackgroundExecutor;->get()Lcom/android/systemui/shared/system/BackgroundExecutor;

    move-result-object v1

    iput-object v1, p0, Lcom/android/systemui/shared/system/ActivityManagerWrapper;->mBackgroundExecutor:Lcom/android/systemui/shared/system/BackgroundExecutor;

    .line 85
    new-instance v1, Lcom/android/systemui/shared/system/TaskStackChangeListeners;

    invoke-static {}, Landroid/os/Looper;->getMainLooper()Landroid/os/Looper;

    move-result-object v2

    invoke-direct {v1, v2}, Lcom/android/systemui/shared/system/TaskStackChangeListeners;-><init>(Landroid/os/Looper;)V

    iput-object v1, p0, Lcom/android/systemui/shared/system/ActivityManagerWrapper;->mTaskStackChangeListeners:Lcom/android/systemui/shared/system/TaskStackChangeListeners;

    .line 86
    return-void
.end method

.method private getBadgedLabel(Ljava/lang/String;I)Ljava/lang/String;
    .registers 5
    .param p1, "label"    # Ljava/lang/String;
    .param p2, "userId"    # I

    .line 194
    invoke-static {}, Landroid/os/UserHandle;->myUserId()I

    move-result v0

    if-eq p2, v0, :cond_15

    .line 195
    iget-object v0, p0, Lcom/android/systemui/shared/system/ActivityManagerWrapper;->mPackageManager:Landroid/content/pm/PackageManager;

    new-instance v1, Landroid/os/UserHandle;

    invoke-direct {v1, p2}, Landroid/os/UserHandle;-><init>(I)V

    invoke-virtual {v0, p1, v1}, Landroid/content/pm/PackageManager;->getUserBadgedLabel(Ljava/lang/CharSequence;Landroid/os/UserHandle;)Ljava/lang/CharSequence;

    move-result-object v0

    invoke-interface {v0}, Ljava/lang/CharSequence;->toString()Ljava/lang/String;

    move-result-object p1

    .line 197
    :cond_15
    return-object p1
.end method

.method public static getInstance()Lcom/android/systemui/shared/system/ActivityManagerWrapper;
    .registers 1

    .line 89
    sget-object v0, Lcom/android/systemui/shared/system/ActivityManagerWrapper;->sInstance:Lcom/android/systemui/shared/system/ActivityManagerWrapper;

    return-object v0
.end method


# virtual methods
.method public cancelRecentsAnimation(Z)V
    .registers 5
    .param p1, "restoreHomeStackPosition"    # Z

    .line 263
    :try_start_0
    invoke-static {}, Landroid/app/ActivityManager;->getService()Landroid/app/IActivityManager;

    move-result-object v0

    invoke-interface {v0, p1}, Landroid/app/IActivityManager;->cancelRecentsAnimation(Z)V
    :try_end_7
    .catch Landroid/os/RemoteException; {:try_start_0 .. :try_end_7} :catch_8

    .line 266
    goto :goto_10

    .line 264
    :catch_8
    move-exception v0

    .line 265
    .local v0, "e":Landroid/os/RemoteException;
    const-string v1, "ActivityManagerWrapper"

    const-string v2, "Failed to cancel recents animation"

    invoke-static {v1, v2, v0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    .line 267
    .end local v0    # "e":Landroid/os/RemoteException;
    :goto_10
    return-void
.end method

.method public cancelWindowTransition(I)V
    .registers 6
    .param p1, "taskId"    # I

    .line 400
    :try_start_0
    invoke-static {}, Landroid/app/ActivityManager;->getService()Landroid/app/IActivityManager;

    move-result-object v0

    invoke-interface {v0, p1}, Landroid/app/IActivityManager;->cancelTaskWindowTransition(I)V
    :try_end_7
    .catch Landroid/os/RemoteException; {:try_start_0 .. :try_end_7} :catch_8

    .line 403
    goto :goto_1f

    .line 401
    :catch_8
    move-exception v0

    .line 402
    .local v0, "e":Landroid/os/RemoteException;
    const-string v1, "ActivityManagerWrapper"

    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    const-string v3, "Failed to cancel window transition for task="

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v2, p1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    invoke-static {v1, v2, v0}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    .line 404
    .end local v0    # "e":Landroid/os/RemoteException;
    :goto_1f
    return-void
.end method

.method public closeSystemWindows(Ljava/lang/String;)V
    .registers 4
    .param p1, "reason"    # Ljava/lang/String;

    .line 367
    iget-object v0, p0, Lcom/android/systemui/shared/system/ActivityManagerWrapper;->mBackgroundExecutor:Lcom/android/systemui/shared/system/BackgroundExecutor;

    new-instance v1, Lcom/android/systemui/shared/system/ActivityManagerWrapper$6;

    invoke-direct {v1, p0, p1}, Lcom/android/systemui/shared/system/ActivityManagerWrapper$6;-><init>(Lcom/android/systemui/shared/system/ActivityManagerWrapper;Ljava/lang/String;)V

    invoke-virtual {v0, v1}, Lcom/android/systemui/shared/system/BackgroundExecutor;->submit(Ljava/lang/Runnable;)Ljava/util/concurrent/Future;

    .line 377
    return-void
.end method

.method public getBadgedActivityLabel(Landroid/content/pm/ActivityInfo;I)Ljava/lang/String;
    .registers 4
    .param p1, "info"    # Landroid/content/pm/ActivityInfo;
    .param p2, "userId"    # I

    .line 161
    iget-object v0, p0, Lcom/android/systemui/shared/system/ActivityManagerWrapper;->mPackageManager:Landroid/content/pm/PackageManager;

    invoke-virtual {p1, v0}, Landroid/content/pm/ActivityInfo;->loadLabel(Landroid/content/pm/PackageManager;)Ljava/lang/CharSequence;

    move-result-object v0

    invoke-interface {v0}, Ljava/lang/CharSequence;->toString()Ljava/lang/String;

    move-result-object v0

    invoke-direct {p0, v0, p2}, Lcom/android/systemui/shared/system/ActivityManagerWrapper;->getBadgedLabel(Ljava/lang/String;I)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getBadgedApplicationLabel(Landroid/content/pm/ApplicationInfo;I)Ljava/lang/String;
    .registers 4
    .param p1, "appInfo"    # Landroid/content/pm/ApplicationInfo;
    .param p2, "userId"    # I

    .line 168
    iget-object v0, p0, Lcom/android/systemui/shared/system/ActivityManagerWrapper;->mPackageManager:Landroid/content/pm/PackageManager;

    invoke-virtual {p1, v0}, Landroid/content/pm/ApplicationInfo;->loadLabel(Landroid/content/pm/PackageManager;)Ljava/lang/CharSequence;

    move-result-object v0

    invoke-interface {v0}, Ljava/lang/CharSequence;->toString()Ljava/lang/String;

    move-result-object v0

    invoke-direct {p0, v0, p2}, Lcom/android/systemui/shared/system/ActivityManagerWrapper;->getBadgedLabel(Ljava/lang/String;I)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public getBadgedContentDescription(Landroid/content/pm/ActivityInfo;ILandroid/app/ActivityManager$TaskDescription;)Ljava/lang/String;
    .registers 9
    .param p1, "info"    # Landroid/content/pm/ActivityInfo;
    .param p2, "userId"    # I
    .param p3, "td"    # Landroid/app/ActivityManager$TaskDescription;

    .line 178
    if-eqz p3, :cond_d

    invoke-virtual {p3}, Landroid/app/ActivityManager$TaskDescription;->getLabel()Ljava/lang/String;

    move-result-object v0

    if-eqz v0, :cond_d

    .line 179
    invoke-virtual {p3}, Landroid/app/ActivityManager$TaskDescription;->getLabel()Ljava/lang/String;

    move-result-object v0

    .line 179
    .local v0, "activityLabel":Ljava/lang/String;
    goto :goto_17

    .line 181
    .end local v0    # "activityLabel":Ljava/lang/String;
    :cond_d
    iget-object v0, p0, Lcom/android/systemui/shared/system/ActivityManagerWrapper;->mPackageManager:Landroid/content/pm/PackageManager;

    invoke-virtual {p1, v0}, Landroid/content/pm/ActivityInfo;->loadLabel(Landroid/content/pm/PackageManager;)Ljava/lang/CharSequence;

    move-result-object v0

    invoke-interface {v0}, Ljava/lang/CharSequence;->toString()Ljava/lang/String;

    move-result-object v0

    .line 183
    .restart local v0    # "activityLabel":Ljava/lang/String;
    :goto_17
    iget-object v1, p1, Landroid/content/pm/ActivityInfo;->applicationInfo:Landroid/content/pm/ApplicationInfo;

    iget-object v2, p0, Lcom/android/systemui/shared/system/ActivityManagerWrapper;->mPackageManager:Landroid/content/pm/PackageManager;

    invoke-virtual {v1, v2}, Landroid/content/pm/ApplicationInfo;->loadLabel(Landroid/content/pm/PackageManager;)Ljava/lang/CharSequence;

    move-result-object v1

    invoke-interface {v1}, Ljava/lang/CharSequence;->toString()Ljava/lang/String;

    move-result-object v1

    .line 184
    .local v1, "applicationLabel":Ljava/lang/String;
    invoke-direct {p0, v1, p2}, Lcom/android/systemui/shared/system/ActivityManagerWrapper;->getBadgedLabel(Ljava/lang/String;I)Ljava/lang/String;

    move-result-object v2

    .line 185
    .local v2, "badgedApplicationLabel":Ljava/lang/String;
    invoke-virtual {v1, v0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v3

    if-eqz v3, :cond_30

    .line 186
    nop

    .line 185
    move-object v3, v2

    goto :goto_44

    .line 187
    :cond_30
    new-instance v3, Ljava/lang/StringBuilder;

    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v3, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string v4, " "

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v3, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v3

    .line 185
    :goto_44
    return-object v3
.end method

.method public getCurrentUserId()I
    .registers 3

    .line 98
    :try_start_0
    invoke-static {}, Landroid/app/ActivityManager;->getService()Landroid/app/IActivityManager;

    move-result-object v0

    invoke-interface {v0}, Landroid/app/IActivityManager;->getCurrentUser()Landroid/content/pm/UserInfo;

    move-result-object v0

    .line 99
    .local v0, "ui":Landroid/content/pm/UserInfo;
    if-eqz v0, :cond_d

    iget v1, v0, Landroid/content/pm/UserInfo;->id:I
    :try_end_c
    .catch Landroid/os/RemoteException; {:try_start_0 .. :try_end_c} :catch_f

    goto :goto_e

    :cond_d
    const/4 v1, 0x0

    :goto_e
    return v1

    .line 100
    .end local v0    # "ui":Landroid/content/pm/UserInfo;
    :catch_f
    move-exception v0

    .line 101
    .local v0, "e":Landroid/os/RemoteException;
    invoke-virtual {v0}, Landroid/os/RemoteException;->rethrowFromSystemServer()Ljava/lang/RuntimeException;

    move-result-object v1

    throw v1
.end method

.method public getRecentTasks(II)Ljava/util/List;
    .registers 6
    .param p1, "numTasks"    # I
    .param p2, "userId"    # I
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(II)",
            "Ljava/util/List<",
            "Landroid/app/ActivityManager$RecentTaskInfo;",
            ">;"
        }
    .end annotation

    .line 132
    :try_start_0
    invoke-static {}, Landroid/app/ActivityManager;->getService()Landroid/app/IActivityManager;

    move-result-object v0

    const/4 v1, 0x2

    invoke-interface {v0, p1, v1, p2}, Landroid/app/IActivityManager;->getRecentTasks(III)Landroid/content/pm/ParceledListSlice;

    move-result-object v0

    .line 133
    invoke-virtual {v0}, Landroid/content/pm/ParceledListSlice;->getList()Ljava/util/List;

    move-result-object v0
    :try_end_d
    .catch Landroid/os/RemoteException; {:try_start_0 .. :try_end_d} :catch_e

    .line 132
    return-object v0

    .line 134
    :catch_e
    move-exception v0

    .line 135
    .local v0, "e":Landroid/os/RemoteException;
    const-string v1, "ActivityManagerWrapper"

    const-string v2, "Failed to get recent tasks"

    invoke-static {v1, v2, v0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    .line 136
    new-instance v1, Ljava/util/ArrayList;

    invoke-direct {v1}, Ljava/util/ArrayList;-><init>()V

    return-object v1
.end method

.method public getRunningTask()Landroid/app/ActivityManager$RunningTaskInfo;
    .registers 2

    .line 109
    const/4 v0, 0x3

    invoke-virtual {p0, v0}, Lcom/android/systemui/shared/system/ActivityManagerWrapper;->getRunningTask(I)Landroid/app/ActivityManager$RunningTaskInfo;

    move-result-object v0

    return-object v0
.end method

.method public getRunningTask(I)Landroid/app/ActivityManager$RunningTaskInfo;
    .registers 6
    .param p1, "ignoreActivityType"    # I
        .annotation build Landroid/app/WindowConfiguration$ActivityType;
        .end annotation
    .end param

    .line 116
    const/4 v0, 0x0

    :try_start_1
    invoke-static {}, Landroid/app/ActivityManager;->getService()Landroid/app/IActivityManager;

    move-result-object v1

    const/4 v2, 0x1

    const/4 v3, 0x2

    invoke-interface {v1, v2, p1, v3}, Landroid/app/IActivityManager;->getFilteredTasks(III)Ljava/util/List;

    move-result-object v1

    .line 118
    .local v1, "tasks":Ljava/util/List;, "Ljava/util/List<Landroid/app/ActivityManager$RunningTaskInfo;>;"
    invoke-interface {v1}, Ljava/util/List;->isEmpty()Z

    move-result v2

    if-eqz v2, :cond_12

    .line 119
    return-object v0

    .line 121
    :cond_12
    const/4 v2, 0x0

    invoke-interface {v1, v2}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Landroid/app/ActivityManager$RunningTaskInfo;
    :try_end_19
    .catch Landroid/os/RemoteException; {:try_start_1 .. :try_end_19} :catch_1a

    return-object v2

    .line 122
    .end local v1    # "tasks":Ljava/util/List;, "Ljava/util/List<Landroid/app/ActivityManager$RunningTaskInfo;>;"
    :catch_1a
    move-exception v1

    .line 123
    .local v1, "e":Landroid/os/RemoteException;
    return-object v0
.end method

.method public getTaskThumbnail(IZ)Lcom/android/systemui/shared/recents/model/ThumbnailData;
    .registers 7
    .param p1, "taskId"    # I
    .param p2, "reducedResolution"    # Z

    .line 144
    const/4 v0, 0x0

    .line 146
    .local v0, "snapshot":Landroid/app/ActivityManager$TaskSnapshot;
    :try_start_1
    invoke-static {}, Landroid/app/ActivityManager;->getService()Landroid/app/IActivityManager;

    move-result-object v1

    invoke-interface {v1, p1, p2}, Landroid/app/IActivityManager;->getTaskSnapshot(IZ)Landroid/app/ActivityManager$TaskSnapshot;

    move-result-object v1
    :try_end_9
    .catch Landroid/os/RemoteException; {:try_start_1 .. :try_end_9} :catch_b

    move-object v0, v1

    .line 149
    goto :goto_13

    .line 147
    :catch_b
    move-exception v1

    .line 148
    .local v1, "e":Landroid/os/RemoteException;
    const-string v2, "ActivityManagerWrapper"

    const-string v3, "Failed to retrieve task snapshot"

    invoke-static {v2, v3, v1}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    .line 150
    .end local v1    # "e":Landroid/os/RemoteException;
    :goto_13
    if-eqz v0, :cond_1b

    .line 151
    new-instance v1, Lcom/android/systemui/shared/recents/model/ThumbnailData;

    invoke-direct {v1, v0}, Lcom/android/systemui/shared/recents/model/ThumbnailData;-><init>(Landroid/app/ActivityManager$TaskSnapshot;)V

    return-object v1

    .line 153
    :cond_1b
    new-instance v1, Lcom/android/systemui/shared/recents/model/ThumbnailData;

    invoke-direct {v1}, Lcom/android/systemui/shared/recents/model/ThumbnailData;-><init>()V

    return-object v1
.end method

.method public isLockToAppActive()Z
    .registers 3

    .line 430
    const/4 v0, 0x0

    :try_start_1
    invoke-static {}, Landroid/app/ActivityManager;->getService()Landroid/app/IActivityManager;

    move-result-object v1

    invoke-interface {v1}, Landroid/app/IActivityManager;->getLockTaskModeState()I

    move-result v1
    :try_end_9
    .catch Landroid/os/RemoteException; {:try_start_1 .. :try_end_9} :catch_e

    if-eqz v1, :cond_d

    const/4 v0, 0x1

    nop

    :cond_d
    return v0

    .line 431
    :catch_e
    move-exception v1

    .line 432
    .local v1, "e":Landroid/os/RemoteException;
    return v0
.end method

.method public isScreenPinningActive()Z
    .registers 4

    .line 411
    const/4 v0, 0x0

    :try_start_1
    invoke-static {}, Landroid/app/ActivityManager;->getService()Landroid/app/IActivityManager;

    move-result-object v1

    invoke-interface {v1}, Landroid/app/IActivityManager;->getLockTaskModeState()I

    move-result v1
    :try_end_9
    .catch Landroid/os/RemoteException; {:try_start_1 .. :try_end_9} :catch_f

    const/4 v2, 0x2

    if-ne v1, v2, :cond_e

    const/4 v0, 0x1

    nop

    :cond_e
    return v0

    .line 412
    :catch_f
    move-exception v1

    .line 413
    .local v1, "e":Landroid/os/RemoteException;
    return v0
.end method

.method public isScreenPinningEnabled()Z
    .registers 4

    .line 421
    invoke-static {}, Landroid/app/AppGlobals;->getInitialApplication()Landroid/app/Application;

    move-result-object v0

    invoke-virtual {v0}, Landroid/app/Application;->getContentResolver()Landroid/content/ContentResolver;

    move-result-object v0

    .line 422
    .local v0, "cr":Landroid/content/ContentResolver;
    const-string v1, "lock_to_app_enabled"

    const/4 v2, 0x0

    invoke-static {v0, v1, v2}, Landroid/provider/Settings$System;->getInt(Landroid/content/ContentResolver;Ljava/lang/String;I)I

    move-result v1

    if-eqz v1, :cond_13

    const/4 v2, 0x1

    nop

    :cond_13
    return v2
.end method

.method public registerTaskStackListener(Lcom/android/systemui/shared/system/TaskStackChangeListener;)V
    .registers 5
    .param p1, "listener"    # Lcom/android/systemui/shared/system/TaskStackChangeListener;

    .line 348
    iget-object v0, p0, Lcom/android/systemui/shared/system/ActivityManagerWrapper;->mTaskStackChangeListeners:Lcom/android/systemui/shared/system/TaskStackChangeListeners;

    monitor-enter v0

    .line 349
    :try_start_3
    iget-object v1, p0, Lcom/android/systemui/shared/system/ActivityManagerWrapper;->mTaskStackChangeListeners:Lcom/android/systemui/shared/system/TaskStackChangeListeners;

    invoke-static {}, Landroid/app/ActivityManager;->getService()Landroid/app/IActivityManager;

    move-result-object v2

    invoke-virtual {v1, v2, p1}, Lcom/android/systemui/shared/system/TaskStackChangeListeners;->addListener(Landroid/app/IActivityManager;Lcom/android/systemui/shared/system/TaskStackChangeListener;)V

    .line 350
    monitor-exit v0

    .line 351
    return-void

    .line 350
    :catchall_e
    move-exception v1

    monitor-exit v0
    :try_end_10
    .catchall {:try_start_3 .. :try_end_10} :catchall_e

    throw v1
.end method

.method public removeTask(I)V
    .registers 4
    .param p1, "taskId"    # I

    .line 383
    iget-object v0, p0, Lcom/android/systemui/shared/system/ActivityManagerWrapper;->mBackgroundExecutor:Lcom/android/systemui/shared/system/BackgroundExecutor;

    new-instance v1, Lcom/android/systemui/shared/system/ActivityManagerWrapper$7;

    invoke-direct {v1, p0, p1}, Lcom/android/systemui/shared/system/ActivityManagerWrapper$7;-><init>(Lcom/android/systemui/shared/system/ActivityManagerWrapper;I)V

    invoke-virtual {v0, v1}, Lcom/android/systemui/shared/system/BackgroundExecutor;->submit(Ljava/lang/Runnable;)Ljava/util/concurrent/Future;

    .line 393
    return-void
.end method

.method public showVoiceSession(Landroid/os/IBinder;Landroid/os/Bundle;I)Z
    .registers 7
    .param p1, "token"    # Landroid/os/IBinder;
    .param p2, "args"    # Landroid/os/Bundle;
    .param p3, "flags"    # I

    .line 441
    const-string v0, "voiceinteraction"

    .line 442
    invoke-static {v0}, Landroid/os/ServiceManager;->getService(Ljava/lang/String;)Landroid/os/IBinder;

    move-result-object v0

    .line 441
    invoke-static {v0}, Lcom/android/internal/app/IVoiceInteractionManagerService$Stub;->asInterface(Landroid/os/IBinder;)Lcom/android/internal/app/IVoiceInteractionManagerService;

    move-result-object v0

    .line 443
    .local v0, "service":Lcom/android/internal/app/IVoiceInteractionManagerService;
    const/4 v1, 0x0

    if-nez v0, :cond_e

    .line 444
    return v1

    .line 447
    :cond_e
    :try_start_e
    invoke-interface {v0, p1, p2, p3}, Lcom/android/internal/app/IVoiceInteractionManagerService;->showSessionFromSession(Landroid/os/IBinder;Landroid/os/Bundle;I)Z

    move-result v2
    :try_end_12
    .catch Landroid/os/RemoteException; {:try_start_e .. :try_end_12} :catch_13

    return v2

    .line 448
    :catch_13
    move-exception v2

    .line 449
    .local v2, "e":Landroid/os/RemoteException;
    return v1
.end method

.method public startActivityFromRecents(ILandroid/app/ActivityOptions;)Z
    .registers 5
    .param p1, "taskId"    # I
    .param p2, "options"    # Landroid/app/ActivityOptions;

    .line 335
    if-nez p2, :cond_4

    const/4 v0, 0x0

    goto :goto_8

    :cond_4
    :try_start_4
    invoke-virtual {p2}, Landroid/app/ActivityOptions;->toBundle()Landroid/os/Bundle;

    move-result-object v0

    .line 336
    .local v0, "optsBundle":Landroid/os/Bundle;
    :goto_8
    invoke-static {}, Landroid/app/ActivityManager;->getService()Landroid/app/IActivityManager;

    move-result-object v1

    invoke-interface {v1, p1, v0}, Landroid/app/IActivityManager;->startActivityFromRecents(ILandroid/os/Bundle;)I
    :try_end_f
    .catch Ljava/lang/Exception; {:try_start_4 .. :try_end_f} :catch_11

    .line 337
    const/4 v1, 0x1

    return v1

    .line 338
    .end local v0    # "optsBundle":Landroid/os/Bundle;
    :catch_11
    move-exception v0

    .line 339
    .local v0, "e":Ljava/lang/Exception;
    const/4 v1, 0x0

    return v1
.end method

.method public startActivityFromRecentsAsync(Lcom/android/systemui/shared/recents/model/Task$TaskKey;Landroid/app/ActivityOptions;IILjava/util/function/Consumer;Landroid/os/Handler;)V
    .registers 15
    .param p1, "taskKey"    # Lcom/android/systemui/shared/recents/model/Task$TaskKey;
    .param p2, "options"    # Landroid/app/ActivityOptions;
    .param p3, "windowingMode"    # I
    .param p4, "activityType"    # I
    .param p6, "resultCallbackHandler"    # Landroid/os/Handler;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Lcom/android/systemui/shared/recents/model/Task$TaskKey;",
            "Landroid/app/ActivityOptions;",
            "II",
            "Ljava/util/function/Consumer<",
            "Ljava/lang/Boolean;",
            ">;",
            "Landroid/os/Handler;",
            ")V"
        }
    .end annotation

    .line 289
    .local p5, "resultCallback":Ljava/util/function/Consumer;, "Ljava/util/function/Consumer<Ljava/lang/Boolean;>;"
    iget v0, p1, Lcom/android/systemui/shared/recents/model/Task$TaskKey;->windowingMode:I

    const/4 v1, 0x3

    if-ne v0, v1, :cond_10

    .line 292
    if-nez p2, :cond_b

    .line 293
    invoke-static {}, Landroid/app/ActivityOptions;->makeBasic()Landroid/app/ActivityOptions;

    move-result-object p2

    .line 295
    :cond_b
    const/4 v0, 0x4

    invoke-virtual {p2, v0}, Landroid/app/ActivityOptions;->setLaunchWindowingMode(I)V

    goto :goto_20

    .line 296
    :cond_10
    if-nez p3, :cond_14

    if-eqz p4, :cond_20

    .line 298
    :cond_14
    if-nez p2, :cond_1a

    .line 299
    invoke-static {}, Landroid/app/ActivityOptions;->makeBasic()Landroid/app/ActivityOptions;

    move-result-object p2

    .line 301
    :cond_1a
    invoke-virtual {p2, p3}, Landroid/app/ActivityOptions;->setLaunchWindowingMode(I)V

    .line 302
    invoke-virtual {p2, p4}, Landroid/app/ActivityOptions;->setLaunchActivityType(I)V

    .line 304
    :cond_20
    :goto_20
    move-object v3, p2

    .line 308
    .local v3, "finalOptions":Landroid/app/ActivityOptions;
    iget-object v6, p0, Lcom/android/systemui/shared/system/ActivityManagerWrapper;->mBackgroundExecutor:Lcom/android/systemui/shared/system/BackgroundExecutor;

    new-instance v7, Lcom/android/systemui/shared/system/ActivityManagerWrapper$5;

    move-object v0, v7

    move-object v1, p0

    move-object v2, p1

    move-object v4, p5

    move-object v5, p6

    invoke-direct/range {v0 .. v5}, Lcom/android/systemui/shared/system/ActivityManagerWrapper$5;-><init>(Lcom/android/systemui/shared/system/ActivityManagerWrapper;Lcom/android/systemui/shared/recents/model/Task$TaskKey;Landroid/app/ActivityOptions;Ljava/util/function/Consumer;Landroid/os/Handler;)V

    invoke-virtual {v6, v7}, Lcom/android/systemui/shared/system/BackgroundExecutor;->submit(Ljava/lang/Runnable;)Ljava/util/concurrent/Future;

    .line 328
    return-void
.end method

.method public startActivityFromRecentsAsync(Lcom/android/systemui/shared/recents/model/Task$TaskKey;Landroid/app/ActivityOptions;Ljava/util/function/Consumer;Landroid/os/Handler;)V
    .registers 12
    .param p1, "taskKey"    # Lcom/android/systemui/shared/recents/model/Task$TaskKey;
    .param p2, "options"    # Landroid/app/ActivityOptions;
    .param p4, "resultCallbackHandler"    # Landroid/os/Handler;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Lcom/android/systemui/shared/recents/model/Task$TaskKey;",
            "Landroid/app/ActivityOptions;",
            "Ljava/util/function/Consumer<",
            "Ljava/lang/Boolean;",
            ">;",
            "Landroid/os/Handler;",
            ")V"
        }
    .end annotation

    .line 276
    .local p3, "resultCallback":Ljava/util/function/Consumer;, "Ljava/util/function/Consumer<Ljava/lang/Boolean;>;"
    const/4 v3, 0x0

    const/4 v4, 0x0

    move-object v0, p0

    move-object v1, p1

    move-object v2, p2

    move-object v5, p3

    move-object v6, p4

    invoke-virtual/range {v0 .. v6}, Lcom/android/systemui/shared/system/ActivityManagerWrapper;->startActivityFromRecentsAsync(Lcom/android/systemui/shared/recents/model/Task$TaskKey;Landroid/app/ActivityOptions;IILjava/util/function/Consumer;Landroid/os/Handler;)V

    .line 278
    return-void
.end method

.method public startRecentsActivity(Landroid/content/Intent;Lcom/android/systemui/shared/system/AssistDataReceiver;Lcom/android/systemui/shared/system/RecentsAnimationListener;Ljava/util/function/Consumer;Landroid/os/Handler;)V
    .registers 9
    .param p1, "intent"    # Landroid/content/Intent;
    .param p2, "assistDataReceiver"    # Lcom/android/systemui/shared/system/AssistDataReceiver;
    .param p3, "animationHandler"    # Lcom/android/systemui/shared/system/RecentsAnimationListener;
    .param p5, "resultCallbackHandler"    # Landroid/os/Handler;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/content/Intent;",
            "Lcom/android/systemui/shared/system/AssistDataReceiver;",
            "Lcom/android/systemui/shared/system/RecentsAnimationListener;",
            "Ljava/util/function/Consumer<",
            "Ljava/lang/Boolean;",
            ">;",
            "Landroid/os/Handler;",
            ")V"
        }
    .end annotation

    .line 207
    .local p4, "resultCallback":Ljava/util/function/Consumer;, "Ljava/util/function/Consumer<Ljava/lang/Boolean;>;"
    const/4 v0, 0x0

    .line 208
    .local v0, "receiver":Landroid/app/IAssistDataReceiver;
    if-eqz p2, :cond_c

    .line 209
    :try_start_3
    new-instance v1, Lcom/android/systemui/shared/system/ActivityManagerWrapper$1;

    invoke-direct {v1, p0, p2}, Lcom/android/systemui/shared/system/ActivityManagerWrapper$1;-><init>(Lcom/android/systemui/shared/system/ActivityManagerWrapper;Lcom/android/systemui/shared/system/AssistDataReceiver;)V

    move-object v0, v1

    goto :goto_c

    .line 246
    .end local v0    # "receiver":Landroid/app/IAssistDataReceiver;
    :catch_a
    move-exception v0

    goto :goto_27

    .line 218
    .restart local v0    # "receiver":Landroid/app/IAssistDataReceiver;
    :cond_c
    :goto_c
    const/4 v1, 0x0

    .line 219
    .local v1, "runner":Landroid/view/IRecentsAnimationRunner;
    if-eqz p3, :cond_15

    .line 220
    new-instance v2, Lcom/android/systemui/shared/system/ActivityManagerWrapper$2;

    invoke-direct {v2, p0, p3}, Lcom/android/systemui/shared/system/ActivityManagerWrapper$2;-><init>(Lcom/android/systemui/shared/system/ActivityManagerWrapper;Lcom/android/systemui/shared/system/RecentsAnimationListener;)V

    move-object v1, v2

    .line 237
    :cond_15
    invoke-static {}, Landroid/app/ActivityManager;->getService()Landroid/app/IActivityManager;

    move-result-object v2

    invoke-interface {v2, p1, v0, v1}, Landroid/app/IActivityManager;->startRecentsActivity(Landroid/content/Intent;Landroid/app/IAssistDataReceiver;Landroid/view/IRecentsAnimationRunner;)V

    .line 238
    if-eqz p4, :cond_26

    .line 239
    new-instance v2, Lcom/android/systemui/shared/system/ActivityManagerWrapper$3;

    invoke-direct {v2, p0, p4}, Lcom/android/systemui/shared/system/ActivityManagerWrapper$3;-><init>(Lcom/android/systemui/shared/system/ActivityManagerWrapper;Ljava/util/function/Consumer;)V

    invoke-virtual {p5, v2}, Landroid/os/Handler;->post(Ljava/lang/Runnable;)Z
    :try_end_26
    .catch Ljava/lang/Exception; {:try_start_3 .. :try_end_26} :catch_a

    .line 255
    .end local v0    # "receiver":Landroid/app/IAssistDataReceiver;
    .end local v1    # "runner":Landroid/view/IRecentsAnimationRunner;
    :cond_26
    goto :goto_32

    .line 246
    :goto_27
    nop

    .line 247
    .local v0, "e":Ljava/lang/Exception;
    if-eqz p4, :cond_32

    .line 248
    new-instance v1, Lcom/android/systemui/shared/system/ActivityManagerWrapper$4;

    invoke-direct {v1, p0, p4}, Lcom/android/systemui/shared/system/ActivityManagerWrapper$4;-><init>(Lcom/android/systemui/shared/system/ActivityManagerWrapper;Ljava/util/function/Consumer;)V

    invoke-virtual {p5, v1}, Landroid/os/Handler;->post(Ljava/lang/Runnable;)Z

    .line 256
    .end local v0    # "e":Ljava/lang/Exception;
    :cond_32
    :goto_32
    return-void
.end method

.method public unregisterTaskStackListener(Lcom/android/systemui/shared/system/TaskStackChangeListener;)V
    .registers 4
    .param p1, "listener"    # Lcom/android/systemui/shared/system/TaskStackChangeListener;

    .line 358
    iget-object v0, p0, Lcom/android/systemui/shared/system/ActivityManagerWrapper;->mTaskStackChangeListeners:Lcom/android/systemui/shared/system/TaskStackChangeListeners;

    monitor-enter v0

    .line 359
    :try_start_3
    iget-object v1, p0, Lcom/android/systemui/shared/system/ActivityManagerWrapper;->mTaskStackChangeListeners:Lcom/android/systemui/shared/system/TaskStackChangeListeners;

    invoke-virtual {v1, p1}, Lcom/android/systemui/shared/system/TaskStackChangeListeners;->removeListener(Lcom/android/systemui/shared/system/TaskStackChangeListener;)V

    .line 360
    monitor-exit v0

    .line 361
    return-void

    .line 360
    :catchall_a
    move-exception v1

    monitor-exit v0
    :try_end_c
    .catchall {:try_start_3 .. :try_end_c} :catchall_a

    throw v1
.end method
