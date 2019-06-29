.class Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;
.super Ljava/lang/Object;
.source "BackgroundTaskLoader.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader$OnIdleChangedListener;
    }
.end annotation


# static fields
.field static DEBUG:Z

.field static TAG:Ljava/lang/String;


# instance fields
.field private mCancelled:Z

.field private mContext:Landroid/content/Context;

.field private final mIconLoader:Lcom/android/systemui/shared/recents/model/IconLoader;

.field private final mLoadQueue:Lcom/android/systemui/shared/recents/model/TaskResourceLoadQueue;

.field private final mLoadThread:Landroid/os/HandlerThread;

.field private final mLoadThreadHandler:Landroid/os/Handler;

.field private final mMainThreadHandler:Landroid/os/Handler;

.field private final mOnIdleChangedListener:Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader$OnIdleChangedListener;

.field private mStarted:Z

.field private mWaitingOnLoadQueue:Z


# direct methods
.method static constructor <clinit>()V
    .registers 1

    .line 31
    const-string v0, "BackgroundTaskLoader"

    sput-object v0, Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;->TAG:Ljava/lang/String;

    .line 32
    const/4 v0, 0x0

    sput-boolean v0, Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;->DEBUG:Z

    return-void
.end method

.method public constructor <init>(Lcom/android/systemui/shared/recents/model/TaskResourceLoadQueue;Lcom/android/systemui/shared/recents/model/IconLoader;Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader$OnIdleChangedListener;)V
    .registers 7
    .param p1, "loadQueue"    # Lcom/android/systemui/shared/recents/model/TaskResourceLoadQueue;
    .param p2, "iconLoader"    # Lcom/android/systemui/shared/recents/model/IconLoader;
    .param p3, "onIdleChangedListener"    # Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader$OnIdleChangedListener;

    .line 50
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 51
    iput-object p1, p0, Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;->mLoadQueue:Lcom/android/systemui/shared/recents/model/TaskResourceLoadQueue;

    .line 52
    iput-object p2, p0, Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;->mIconLoader:Lcom/android/systemui/shared/recents/model/IconLoader;

    .line 53
    new-instance v0, Landroid/os/Handler;

    invoke-direct {v0}, Landroid/os/Handler;-><init>()V

    iput-object v0, p0, Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;->mMainThreadHandler:Landroid/os/Handler;

    .line 54
    iput-object p3, p0, Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;->mOnIdleChangedListener:Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader$OnIdleChangedListener;

    .line 55
    new-instance v0, Landroid/os/HandlerThread;

    const-string v1, "Recents-TaskResourceLoader"

    const/16 v2, 0xa

    invoke-direct {v0, v1, v2}, Landroid/os/HandlerThread;-><init>(Ljava/lang/String;I)V

    iput-object v0, p0, Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;->mLoadThread:Landroid/os/HandlerThread;

    .line 57
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;->mLoadThread:Landroid/os/HandlerThread;

    invoke-virtual {v0}, Landroid/os/HandlerThread;->start()V

    .line 58
    new-instance v0, Landroid/os/Handler;

    iget-object v1, p0, Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;->mLoadThread:Landroid/os/HandlerThread;

    invoke-virtual {v1}, Landroid/os/HandlerThread;->getLooper()Landroid/os/Looper;

    move-result-object v1

    invoke-direct {v0, v1}, Landroid/os/Handler;-><init>(Landroid/os/Looper;)V

    iput-object v0, p0, Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;->mLoadThreadHandler:Landroid/os/Handler;

    .line 59
    return-void
.end method

.method static synthetic lambda$processLoadQueueItem$2(Lcom/android/systemui/shared/recents/model/Task;Lcom/android/systemui/shared/recents/model/ThumbnailData;Landroid/graphics/drawable/Drawable;)V
    .registers 3
    .param p0, "t"    # Lcom/android/systemui/shared/recents/model/Task;
    .param p1, "thumbnailData"    # Lcom/android/systemui/shared/recents/model/ThumbnailData;
    .param p2, "icon"    # Landroid/graphics/drawable/Drawable;

    .line 146
    invoke-virtual {p0, p1, p2}, Lcom/android/systemui/shared/recents/model/Task;->notifyTaskDataLoaded(Lcom/android/systemui/shared/recents/model/ThumbnailData;Landroid/graphics/drawable/Drawable;)V

    return-void
.end method

.method static synthetic lambda$run$0(Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;)V
    .registers 3

    .line 114
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;->mOnIdleChangedListener:Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader$OnIdleChangedListener;

    const/4 v1, 0x1

    invoke-interface {v0, v1}, Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader$OnIdleChangedListener;->onIdleChanged(Z)V

    return-void
.end method

.method static synthetic lambda$run$1(Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;)V
    .registers 3

    .line 117
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;->mOnIdleChangedListener:Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader$OnIdleChangedListener;

    const/4 v1, 0x0

    invoke-interface {v0, v1}, Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader$OnIdleChangedListener;->onIdleChanged(Z)V

    return-void
.end method

.method private processLoadQueueItem()V
    .registers 6

    .line 135
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;->mLoadQueue:Lcom/android/systemui/shared/recents/model/TaskResourceLoadQueue;

    invoke-virtual {v0}, Lcom/android/systemui/shared/recents/model/TaskResourceLoadQueue;->nextTask()Lcom/android/systemui/shared/recents/model/Task;

    move-result-object v0

    .line 136
    .local v0, "t":Lcom/android/systemui/shared/recents/model/Task;
    if-eqz v0, :cond_45

    .line 137
    iget-object v1, p0, Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;->mIconLoader:Lcom/android/systemui/shared/recents/model/IconLoader;

    invoke-virtual {v1, v0}, Lcom/android/systemui/shared/recents/model/IconLoader;->getIcon(Lcom/android/systemui/shared/recents/model/Task;)Landroid/graphics/drawable/Drawable;

    move-result-object v1

    .line 138
    .local v1, "icon":Landroid/graphics/drawable/Drawable;
    sget-boolean v2, Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;->DEBUG:Z

    if-eqz v2, :cond_2a

    sget-object v2, Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;->TAG:Ljava/lang/String;

    new-instance v3, Ljava/lang/StringBuilder;

    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V

    const-string v4, "Loading thumbnail: "

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v4, v0, Lcom/android/systemui/shared/recents/model/Task;->key:Lcom/android/systemui/shared/recents/model/Task$TaskKey;

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v3

    invoke-static {v2, v3}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 140
    :cond_2a
    invoke-static {}, Lcom/android/systemui/shared/system/ActivityManagerWrapper;->getInstance()Lcom/android/systemui/shared/system/ActivityManagerWrapper;

    move-result-object v2

    iget-object v3, v0, Lcom/android/systemui/shared/recents/model/Task;->key:Lcom/android/systemui/shared/recents/model/Task$TaskKey;

    iget v3, v3, Lcom/android/systemui/shared/recents/model/Task$TaskKey;->id:I

    const/4 v4, 0x1

    invoke-virtual {v2, v3, v4}, Lcom/android/systemui/shared/system/ActivityManagerWrapper;->getTaskThumbnail(IZ)Lcom/android/systemui/shared/recents/model/ThumbnailData;

    move-result-object v2

    .line 143
    .local v2, "thumbnailData":Lcom/android/systemui/shared/recents/model/ThumbnailData;
    iget-boolean v3, p0, Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;->mCancelled:Z

    if-nez v3, :cond_45

    .line 145
    iget-object v3, p0, Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;->mMainThreadHandler:Landroid/os/Handler;

    new-instance v4, Lcom/android/systemui/shared/recents/model/-$$Lambda$BackgroundTaskLoader$mJeiv3P4w5EJwXqKPoDi48s7tFI;

    invoke-direct {v4, v0, v2, v1}, Lcom/android/systemui/shared/recents/model/-$$Lambda$BackgroundTaskLoader$mJeiv3P4w5EJwXqKPoDi48s7tFI;-><init>(Lcom/android/systemui/shared/recents/model/Task;Lcom/android/systemui/shared/recents/model/ThumbnailData;Landroid/graphics/drawable/Drawable;)V

    invoke-virtual {v3, v4}, Landroid/os/Handler;->post(Ljava/lang/Runnable;)Z

    .line 149
    .end local v1    # "icon":Landroid/graphics/drawable/Drawable;
    .end local v2    # "thumbnailData":Lcom/android/systemui/shared/recents/model/ThumbnailData;
    :cond_45
    return-void
.end method


# virtual methods
.method public run()V
    .registers 4

    .line 91
    :cond_0
    :goto_0
    iget-boolean v0, p0, Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;->mCancelled:Z

    if-eqz v0, :cond_1a

    .line 94
    const/4 v0, 0x0

    iput-object v0, p0, Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;->mContext:Landroid/content/Context;

    .line 96
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;->mLoadThread:Landroid/os/HandlerThread;

    monitor-enter v0

    .line 98
    :try_start_a
    iget-object v1, p0, Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;->mLoadThread:Landroid/os/HandlerThread;

    invoke-virtual {v1}, Ljava/lang/Object;->wait()V
    :try_end_f
    .catch Ljava/lang/InterruptedException; {:try_start_a .. :try_end_f} :catch_12
    .catchall {:try_start_a .. :try_end_f} :catchall_10

    .line 101
    goto :goto_16

    .line 102
    :catchall_10
    move-exception v1

    goto :goto_18

    .line 99
    :catch_12
    move-exception v1

    .line 100
    .local v1, "ie":Ljava/lang/InterruptedException;
    :try_start_13
    invoke-virtual {v1}, Ljava/lang/InterruptedException;->printStackTrace()V

    .line 102
    .end local v1    # "ie":Ljava/lang/InterruptedException;
    :goto_16
    monitor-exit v0

    goto :goto_0

    :goto_18
    monitor-exit v0
    :try_end_19
    .catchall {:try_start_13 .. :try_end_19} :catchall_10

    throw v1

    .line 106
    :cond_1a
    invoke-direct {p0}, Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;->processLoadQueueItem()V

    .line 109
    iget-boolean v0, p0, Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;->mCancelled:Z

    if-nez v0, :cond_0

    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;->mLoadQueue:Lcom/android/systemui/shared/recents/model/TaskResourceLoadQueue;

    invoke-virtual {v0}, Lcom/android/systemui/shared/recents/model/TaskResourceLoadQueue;->isEmpty()Z

    move-result v0

    if-eqz v0, :cond_0

    .line 110
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;->mLoadQueue:Lcom/android/systemui/shared/recents/model/TaskResourceLoadQueue;

    monitor-enter v0

    .line 112
    const/4 v1, 0x1

    :try_start_2d
    iput-boolean v1, p0, Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;->mWaitingOnLoadQueue:Z

    .line 113
    iget-object v1, p0, Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;->mMainThreadHandler:Landroid/os/Handler;

    new-instance v2, Lcom/android/systemui/shared/recents/model/-$$Lambda$BackgroundTaskLoader$gaMb8n3irXHj3SpODGi50cngupE;

    invoke-direct {v2, p0}, Lcom/android/systemui/shared/recents/model/-$$Lambda$BackgroundTaskLoader$gaMb8n3irXHj3SpODGi50cngupE;-><init>(Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;)V

    invoke-virtual {v1, v2}, Landroid/os/Handler;->post(Ljava/lang/Runnable;)Z

    .line 115
    iget-object v1, p0, Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;->mLoadQueue:Lcom/android/systemui/shared/recents/model/TaskResourceLoadQueue;

    invoke-virtual {v1}, Ljava/lang/Object;->wait()V

    .line 116
    iget-object v1, p0, Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;->mMainThreadHandler:Landroid/os/Handler;

    new-instance v2, Lcom/android/systemui/shared/recents/model/-$$Lambda$BackgroundTaskLoader$XRsMGIp0x8MAJ36UKSTd3DJ9dTg;

    invoke-direct {v2, p0}, Lcom/android/systemui/shared/recents/model/-$$Lambda$BackgroundTaskLoader$XRsMGIp0x8MAJ36UKSTd3DJ9dTg;-><init>(Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;)V

    invoke-virtual {v1, v2}, Landroid/os/Handler;->post(Ljava/lang/Runnable;)Z

    .line 118
    const/4 v1, 0x0

    iput-boolean v1, p0, Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;->mWaitingOnLoadQueue:Z
    :try_end_4b
    .catch Ljava/lang/InterruptedException; {:try_start_2d .. :try_end_4b} :catch_4e
    .catchall {:try_start_2d .. :try_end_4b} :catchall_4c

    .line 121
    goto :goto_52

    .line 122
    :catchall_4c
    move-exception v1

    goto :goto_54

    .line 119
    :catch_4e
    move-exception v1

    .line 120
    .restart local v1    # "ie":Ljava/lang/InterruptedException;
    :try_start_4f
    invoke-virtual {v1}, Ljava/lang/InterruptedException;->printStackTrace()V

    .line 122
    .end local v1    # "ie":Ljava/lang/InterruptedException;
    :goto_52
    monitor-exit v0

    goto :goto_0

    :goto_54
    monitor-exit v0
    :try_end_55
    .catchall {:try_start_4f .. :try_end_55} :catchall_4c

    throw v1
.end method

.method start(Landroid/content/Context;)V
    .registers 4
    .param p1, "context"    # Landroid/content/Context;

    .line 63
    iput-object p1, p0, Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;->mContext:Landroid/content/Context;

    .line 64
    const/4 v0, 0x0

    iput-boolean v0, p0, Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;->mCancelled:Z

    .line 65
    iget-boolean v0, p0, Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;->mStarted:Z

    if-nez v0, :cond_12

    .line 67
    const/4 v0, 0x1

    iput-boolean v0, p0, Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;->mStarted:Z

    .line 68
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;->mLoadThreadHandler:Landroid/os/Handler;

    invoke-virtual {v0, p0}, Landroid/os/Handler;->post(Ljava/lang/Runnable;)Z

    goto :goto_1b

    .line 71
    :cond_12
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;->mLoadThread:Landroid/os/HandlerThread;

    monitor-enter v0

    .line 72
    :try_start_15
    iget-object v1, p0, Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;->mLoadThread:Landroid/os/HandlerThread;

    invoke-virtual {v1}, Ljava/lang/Object;->notifyAll()V

    .line 73
    monitor-exit v0

    .line 75
    :goto_1b
    return-void

    .line 73
    :catchall_1c
    move-exception v1

    monitor-exit v0
    :try_end_1e
    .catchall {:try_start_15 .. :try_end_1e} :catchall_1c

    throw v1
.end method

.method stop()V
    .registers 2

    .line 80
    const/4 v0, 0x1

    iput-boolean v0, p0, Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;->mCancelled:Z

    .line 83
    iget-boolean v0, p0, Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;->mWaitingOnLoadQueue:Z

    if-eqz v0, :cond_a

    .line 84
    const/4 v0, 0x0

    iput-object v0, p0, Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;->mContext:Landroid/content/Context;

    .line 86
    :cond_a
    return-void
.end method
