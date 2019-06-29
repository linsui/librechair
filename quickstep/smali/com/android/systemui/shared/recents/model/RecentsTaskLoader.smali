.class public Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;
.super Ljava/lang/Object;
.source "RecentsTaskLoader.java"


# static fields
.field private static final DEBUG:Z = false

.field public static final SVELTE_DISABLE_CACHE:I = 0x2

.field public static final SVELTE_DISABLE_LOADING:I = 0x3

.field public static final SVELTE_LIMIT_CACHE:I = 0x1

.field public static final SVELTE_NONE:I = 0x0

.field private static final TAG:Ljava/lang/String; = "RecentsTaskLoader"


# instance fields
.field private final mActivityInfoCache:Landroid/util/LruCache;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Landroid/util/LruCache<",
            "Landroid/content/ComponentName;",
            "Landroid/content/pm/ActivityInfo;",
            ">;"
        }
    .end annotation
.end field

.field private final mActivityLabelCache:Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Lcom/android/systemui/shared/recents/model/TaskKeyLruCache<",
            "Ljava/lang/String;",
            ">;"
        }
    .end annotation
.end field

.field private mClearActivityInfoOnEviction:Lcom/android/systemui/shared/recents/model/TaskKeyLruCache$EvictionCallback;

.field private final mContentDescriptionCache:Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Lcom/android/systemui/shared/recents/model/TaskKeyLruCache<",
            "Ljava/lang/String;",
            ">;"
        }
    .end annotation
.end field

.field private mDefaultTaskBarBackgroundColor:I

.field private mDefaultTaskViewBackgroundColor:I

.field private final mHighResThumbnailLoader:Lcom/android/systemui/shared/recents/model/HighResThumbnailLoader;

.field private final mIconCache:Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Lcom/android/systemui/shared/recents/model/TaskKeyLruCache<",
            "Landroid/graphics/drawable/Drawable;",
            ">;"
        }
    .end annotation
.end field

.field private final mIconLoader:Lcom/android/systemui/shared/recents/model/IconLoader;

.field private final mLoadQueue:Lcom/android/systemui/shared/recents/model/TaskResourceLoadQueue;

.field private final mLoader:Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;

.field private final mMaxIconCacheSize:I

.field private final mMaxThumbnailCacheSize:I

.field private mNumVisibleTasksLoaded:I

.field private mSvelteLevel:I

.field private final mTempCache:Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;
    .annotation build Lcom/android/internal/annotations/GuardedBy;
        value = {
            "this"
        }
    .end annotation

    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache<",
            "Lcom/android/systemui/shared/recents/model/ThumbnailData;",
            ">;"
        }
    .end annotation
.end field

.field private final mThumbnailCache:Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;
    .annotation build Lcom/android/internal/annotations/GuardedBy;
        value = {
            "this"
        }
    .end annotation

    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache<",
            "Lcom/android/systemui/shared/recents/model/ThumbnailData;",
            ">;"
        }
    .end annotation
.end field


# direct methods
.method public constructor <init>(Landroid/content/Context;III)V
    .registers 11
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "maxThumbnailCacheSize"    # I
    .param p3, "maxIconCacheSize"    # I
    .param p4, "svelteLevel"    # I

    .line 95
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 73
    new-instance v0, Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;

    invoke-direct {v0}, Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;-><init>()V

    iput-object v0, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mThumbnailCache:Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;

    .line 75
    new-instance v0, Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;

    invoke-direct {v0}, Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;-><init>()V

    iput-object v0, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mTempCache:Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;

    .line 85
    new-instance v0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader$1;

    invoke-direct {v0, p0}, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader$1;-><init>(Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;)V

    iput-object v0, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mClearActivityInfoOnEviction:Lcom/android/systemui/shared/recents/model/TaskKeyLruCache$EvictionCallback;

    .line 96
    iput p2, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mMaxThumbnailCacheSize:I

    .line 97
    iput p3, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mMaxIconCacheSize:I

    .line 98
    iput p4, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mSvelteLevel:I

    .line 101
    invoke-static {}, Landroid/app/ActivityManager;->getMaxRecentTasksStatic()I

    move-result v0

    .line 102
    .local v0, "numRecentTasks":I
    new-instance v1, Lcom/android/systemui/shared/recents/model/HighResThumbnailLoader;

    invoke-static {}, Lcom/android/systemui/shared/system/ActivityManagerWrapper;->getInstance()Lcom/android/systemui/shared/system/ActivityManagerWrapper;

    move-result-object v2

    .line 103
    invoke-static {}, Landroid/os/Looper;->getMainLooper()Landroid/os/Looper;

    move-result-object v3

    invoke-static {}, Landroid/app/ActivityManager;->isLowRamDeviceStatic()Z

    move-result v4

    invoke-direct {v1, v2, v3, v4}, Lcom/android/systemui/shared/recents/model/HighResThumbnailLoader;-><init>(Lcom/android/systemui/shared/system/ActivityManagerWrapper;Landroid/os/Looper;Z)V

    iput-object v1, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mHighResThumbnailLoader:Lcom/android/systemui/shared/recents/model/HighResThumbnailLoader;

    .line 104
    new-instance v1, Lcom/android/systemui/shared/recents/model/TaskResourceLoadQueue;

    invoke-direct {v1}, Lcom/android/systemui/shared/recents/model/TaskResourceLoadQueue;-><init>()V

    iput-object v1, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mLoadQueue:Lcom/android/systemui/shared/recents/model/TaskResourceLoadQueue;

    .line 105
    new-instance v1, Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;

    iget v2, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mMaxIconCacheSize:I

    iget-object v3, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mClearActivityInfoOnEviction:Lcom/android/systemui/shared/recents/model/TaskKeyLruCache$EvictionCallback;

    invoke-direct {v1, v2, v3}, Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;-><init>(ILcom/android/systemui/shared/recents/model/TaskKeyLruCache$EvictionCallback;)V

    iput-object v1, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mIconCache:Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;

    .line 106
    new-instance v1, Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;

    iget-object v2, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mClearActivityInfoOnEviction:Lcom/android/systemui/shared/recents/model/TaskKeyLruCache$EvictionCallback;

    invoke-direct {v1, v0, v2}, Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;-><init>(ILcom/android/systemui/shared/recents/model/TaskKeyLruCache$EvictionCallback;)V

    iput-object v1, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mActivityLabelCache:Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;

    .line 107
    new-instance v1, Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;

    iget-object v2, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mClearActivityInfoOnEviction:Lcom/android/systemui/shared/recents/model/TaskKeyLruCache$EvictionCallback;

    invoke-direct {v1, v0, v2}, Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;-><init>(ILcom/android/systemui/shared/recents/model/TaskKeyLruCache$EvictionCallback;)V

    iput-object v1, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mContentDescriptionCache:Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;

    .line 109
    new-instance v1, Landroid/util/LruCache;

    invoke-direct {v1, v0}, Landroid/util/LruCache;-><init>(I)V

    iput-object v1, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mActivityInfoCache:Landroid/util/LruCache;

    .line 111
    iget-object v1, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mIconCache:Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;

    iget-object v2, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mActivityInfoCache:Landroid/util/LruCache;

    invoke-virtual {p0, p1, v1, v2}, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->createNewIconLoader(Landroid/content/Context;Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;Landroid/util/LruCache;)Lcom/android/systemui/shared/recents/model/IconLoader;

    move-result-object v1

    iput-object v1, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mIconLoader:Lcom/android/systemui/shared/recents/model/IconLoader;

    .line 112
    new-instance v1, Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;

    iget-object v2, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mLoadQueue:Lcom/android/systemui/shared/recents/model/TaskResourceLoadQueue;

    iget-object v3, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mIconLoader:Lcom/android/systemui/shared/recents/model/IconLoader;

    iget-object v4, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mHighResThumbnailLoader:Lcom/android/systemui/shared/recents/model/HighResThumbnailLoader;

    invoke-virtual {v4}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    new-instance v5, Lcom/android/systemui/shared/recents/model/-$$Lambda$vKccogRiJjM5FBa4zs196J3w_Fs;

    invoke-direct {v5, v4}, Lcom/android/systemui/shared/recents/model/-$$Lambda$vKccogRiJjM5FBa4zs196J3w_Fs;-><init>(Lcom/android/systemui/shared/recents/model/HighResThumbnailLoader;)V

    invoke-direct {v1, v2, v3, v5}, Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;-><init>(Lcom/android/systemui/shared/recents/model/TaskResourceLoadQueue;Lcom/android/systemui/shared/recents/model/IconLoader;Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader$OnIdleChangedListener;)V

    iput-object v1, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mLoader:Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;

    .line 114
    return-void
.end method

.method static synthetic access$000(Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;)Landroid/util/LruCache;
    .registers 2
    .param p0, "x0"    # Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;

    .line 44
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mActivityInfoCache:Landroid/util/LruCache;

    return-object v0
.end method

.method private stopLoader()V
    .registers 2

    .line 402
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mLoader:Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;

    invoke-virtual {v0}, Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;->stop()V

    .line 403
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mLoadQueue:Lcom/android/systemui/shared/recents/model/TaskResourceLoadQueue;

    invoke-virtual {v0}, Lcom/android/systemui/shared/recents/model/TaskResourceLoadQueue;->clearTasks()V

    .line 404
    return-void
.end method


# virtual methods
.method protected createNewIconLoader(Landroid/content/Context;Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;Landroid/util/LruCache;)Lcom/android/systemui/shared/recents/model/IconLoader;
    .registers 5
    .param p1, "context"    # Landroid/content/Context;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/content/Context;",
            "Lcom/android/systemui/shared/recents/model/TaskKeyLruCache<",
            "Landroid/graphics/drawable/Drawable;",
            ">;",
            "Landroid/util/LruCache<",
            "Landroid/content/ComponentName;",
            "Landroid/content/pm/ActivityInfo;",
            ">;)",
            "Lcom/android/systemui/shared/recents/model/IconLoader;"
        }
    .end annotation

    .line 118
    .local p2, "iconCache":Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;, "Lcom/android/systemui/shared/recents/model/TaskKeyLruCache<Landroid/graphics/drawable/Drawable;>;"
    .local p3, "activityInfoCache":Landroid/util/LruCache;, "Landroid/util/LruCache<Landroid/content/ComponentName;Landroid/content/pm/ActivityInfo;>;"
    new-instance v0, Lcom/android/systemui/shared/recents/model/IconLoader$DefaultIconLoader;

    invoke-direct {v0, p1, p2, p3}, Lcom/android/systemui/shared/recents/model/IconLoader$DefaultIconLoader;-><init>(Landroid/content/Context;Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;Landroid/util/LruCache;)V

    return-object v0
.end method

.method public deleteTaskData(Lcom/android/systemui/shared/recents/model/Task;Z)V
    .registers 5
    .param p1, "t"    # Lcom/android/systemui/shared/recents/model/Task;
    .param p2, "notifyTaskDataUnloaded"    # Z

    .line 198
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mLoadQueue:Lcom/android/systemui/shared/recents/model/TaskResourceLoadQueue;

    invoke-virtual {v0, p1}, Lcom/android/systemui/shared/recents/model/TaskResourceLoadQueue;->removeTask(Lcom/android/systemui/shared/recents/model/Task;)V

    .line 199
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mIconCache:Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;

    iget-object v1, p1, Lcom/android/systemui/shared/recents/model/Task;->key:Lcom/android/systemui/shared/recents/model/Task$TaskKey;

    invoke-virtual {v0, v1}, Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;->remove(Lcom/android/systemui/shared/recents/model/Task$TaskKey;)V

    .line 200
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mActivityLabelCache:Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;

    iget-object v1, p1, Lcom/android/systemui/shared/recents/model/Task;->key:Lcom/android/systemui/shared/recents/model/Task$TaskKey;

    invoke-virtual {v0, v1}, Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;->remove(Lcom/android/systemui/shared/recents/model/Task$TaskKey;)V

    .line 201
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mContentDescriptionCache:Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;

    iget-object v1, p1, Lcom/android/systemui/shared/recents/model/Task;->key:Lcom/android/systemui/shared/recents/model/Task$TaskKey;

    invoke-virtual {v0, v1}, Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;->remove(Lcom/android/systemui/shared/recents/model/Task$TaskKey;)V

    .line 202
    if-eqz p2, :cond_29

    .line 203
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mIconLoader:Lcom/android/systemui/shared/recents/model/IconLoader;

    iget-object v1, p1, Lcom/android/systemui/shared/recents/model/Task;->key:Lcom/android/systemui/shared/recents/model/Task$TaskKey;

    iget v1, v1, Lcom/android/systemui/shared/recents/model/Task$TaskKey;->userId:I

    invoke-virtual {v0, v1}, Lcom/android/systemui/shared/recents/model/IconLoader;->getDefaultIcon(I)Landroid/graphics/drawable/Drawable;

    move-result-object v0

    invoke-virtual {p1, v0}, Lcom/android/systemui/shared/recents/model/Task;->notifyTaskDataUnloaded(Landroid/graphics/drawable/Drawable;)V

    .line 205
    :cond_29
    return-void
.end method

.method public declared-synchronized dump(Ljava/lang/String;Ljava/io/PrintWriter;)V
    .registers 5
    .param p1, "prefix"    # Ljava/lang/String;
    .param p2, "writer"    # Ljava/io/PrintWriter;

    monitor-enter p0

    .line 407
    :try_start_1
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v0, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string v1, "  "

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    .line 409
    .local v0, "innerPrefix":Ljava/lang/String;
    invoke-virtual {p2, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    const-string v1, "RecentsTaskLoader"

    invoke-virtual {p2, v1}, Ljava/io/PrintWriter;->println(Ljava/lang/String;)V

    .line 410
    invoke-virtual {p2, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    const-string v1, "Icon Cache"

    invoke-virtual {p2, v1}, Ljava/io/PrintWriter;->println(Ljava/lang/String;)V

    .line 411
    iget-object v1, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mIconCache:Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;

    invoke-virtual {v1, v0, p2}, Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;->dump(Ljava/lang/String;Ljava/io/PrintWriter;)V

    .line 412
    invoke-virtual {p2, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    const-string v1, "Thumbnail Cache"

    invoke-virtual {p2, v1}, Ljava/io/PrintWriter;->println(Ljava/lang/String;)V

    .line 413
    iget-object v1, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mThumbnailCache:Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;

    invoke-virtual {v1, v0, p2}, Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;->dump(Ljava/lang/String;Ljava/io/PrintWriter;)V

    .line 414
    invoke-virtual {p2, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    const-string v1, "Temp Thumbnail Cache"

    invoke-virtual {p2, v1}, Ljava/io/PrintWriter;->println(Ljava/lang/String;)V

    .line 415
    iget-object v1, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mTempCache:Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;

    invoke-virtual {v1, v0, p2}, Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;->dump(Ljava/lang/String;Ljava/io/PrintWriter;)V
    :try_end_41
    .catchall {:try_start_1 .. :try_end_41} :catchall_43

    .line 416
    monitor-exit p0

    return-void

    .line 406
    .end local v0    # "innerPrefix":Ljava/lang/String;
    .end local p1    # "prefix":Ljava/lang/String;
    .end local p2    # "writer":Ljava/io/PrintWriter;
    :catchall_43
    move-exception p1

    monitor-exit p0

    throw p1
.end method

.method getActivityBackgroundColor(Landroid/app/ActivityManager$TaskDescription;)I
    .registers 3
    .param p1, "td"    # Landroid/app/ActivityManager$TaskDescription;

    .line 377
    if-eqz p1, :cond_d

    invoke-virtual {p1}, Landroid/app/ActivityManager$TaskDescription;->getBackgroundColor()I

    move-result v0

    if-eqz v0, :cond_d

    .line 378
    invoke-virtual {p1}, Landroid/app/ActivityManager$TaskDescription;->getBackgroundColor()I

    move-result v0

    return v0

    .line 380
    :cond_d
    iget v0, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mDefaultTaskViewBackgroundColor:I

    return v0
.end method

.method getActivityPrimaryColor(Landroid/app/ActivityManager$TaskDescription;)I
    .registers 3
    .param p1, "td"    # Landroid/app/ActivityManager$TaskDescription;

    .line 367
    if-eqz p1, :cond_d

    invoke-virtual {p1}, Landroid/app/ActivityManager$TaskDescription;->getPrimaryColor()I

    move-result v0

    if-eqz v0, :cond_d

    .line 368
    invoke-virtual {p1}, Landroid/app/ActivityManager$TaskDescription;->getPrimaryColor()I

    move-result v0

    return v0

    .line 370
    :cond_d
    iget v0, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mDefaultTaskBarBackgroundColor:I

    return v0
.end method

.method getAndUpdateActivityIcon(Lcom/android/systemui/shared/recents/model/Task$TaskKey;Landroid/app/ActivityManager$TaskDescription;Z)Landroid/graphics/drawable/Drawable;
    .registers 5
    .param p1, "taskKey"    # Lcom/android/systemui/shared/recents/model/Task$TaskKey;
    .param p2, "td"    # Landroid/app/ActivityManager$TaskDescription;
    .param p3, "loadIfNotCached"    # Z

    .line 325
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mIconLoader:Lcom/android/systemui/shared/recents/model/IconLoader;

    invoke-virtual {v0, p1, p2, p3}, Lcom/android/systemui/shared/recents/model/IconLoader;->getAndInvalidateIfModified(Lcom/android/systemui/shared/recents/model/Task$TaskKey;Landroid/app/ActivityManager$TaskDescription;Z)Landroid/graphics/drawable/Drawable;

    move-result-object v0

    return-object v0
.end method

.method getAndUpdateActivityInfo(Lcom/android/systemui/shared/recents/model/Task$TaskKey;)Landroid/content/pm/ActivityInfo;
    .registers 3
    .param p1, "taskKey"    # Lcom/android/systemui/shared/recents/model/Task$TaskKey;

    .line 388
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mIconLoader:Lcom/android/systemui/shared/recents/model/IconLoader;

    invoke-virtual {v0, p1}, Lcom/android/systemui/shared/recents/model/IconLoader;->getAndUpdateActivityInfo(Lcom/android/systemui/shared/recents/model/Task$TaskKey;)Landroid/content/pm/ActivityInfo;

    move-result-object v0

    return-object v0
.end method

.method getAndUpdateActivityTitle(Lcom/android/systemui/shared/recents/model/Task$TaskKey;Landroid/app/ActivityManager$TaskDescription;)Ljava/lang/String;
    .registers 7
    .param p1, "taskKey"    # Lcom/android/systemui/shared/recents/model/Task$TaskKey;
    .param p2, "td"    # Landroid/app/ActivityManager$TaskDescription;

    .line 268
    if-eqz p2, :cond_d

    invoke-virtual {p2}, Landroid/app/ActivityManager$TaskDescription;->getLabel()Ljava/lang/String;

    move-result-object v0

    if-eqz v0, :cond_d

    .line 269
    invoke-virtual {p2}, Landroid/app/ActivityManager$TaskDescription;->getLabel()Ljava/lang/String;

    move-result-object v0

    return-object v0

    .line 272
    :cond_d
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mActivityLabelCache:Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;

    invoke-virtual {v0, p1}, Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;->getAndInvalidateIfModified(Lcom/android/systemui/shared/recents/model/Task$TaskKey;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/lang/String;

    .line 273
    .local v0, "label":Ljava/lang/String;
    if-eqz v0, :cond_18

    .line 274
    return-object v0

    .line 277
    :cond_18
    invoke-virtual {p0, p1}, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->getAndUpdateActivityInfo(Lcom/android/systemui/shared/recents/model/Task$TaskKey;)Landroid/content/pm/ActivityInfo;

    move-result-object v1

    .line 278
    .local v1, "activityInfo":Landroid/content/pm/ActivityInfo;
    if-eqz v1, :cond_2e

    .line 279
    invoke-static {}, Lcom/android/systemui/shared/system/ActivityManagerWrapper;->getInstance()Lcom/android/systemui/shared/system/ActivityManagerWrapper;

    move-result-object v2

    iget v3, p1, Lcom/android/systemui/shared/recents/model/Task$TaskKey;->userId:I

    invoke-virtual {v2, v1, v3}, Lcom/android/systemui/shared/system/ActivityManagerWrapper;->getBadgedActivityLabel(Landroid/content/pm/ActivityInfo;I)Ljava/lang/String;

    move-result-object v0

    .line 281
    iget-object v2, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mActivityLabelCache:Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;

    invoke-virtual {v2, p1, v0}, Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;->put(Lcom/android/systemui/shared/recents/model/Task$TaskKey;Ljava/lang/Object;)V

    .line 282
    return-object v0

    .line 286
    :cond_2e
    const-string v2, ""

    return-object v2
.end method

.method getAndUpdateContentDescription(Lcom/android/systemui/shared/recents/model/Task$TaskKey;Landroid/app/ActivityManager$TaskDescription;)Ljava/lang/String;
    .registers 7
    .param p1, "taskKey"    # Lcom/android/systemui/shared/recents/model/Task$TaskKey;
    .param p2, "td"    # Landroid/app/ActivityManager$TaskDescription;

    .line 295
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mContentDescriptionCache:Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;

    invoke-virtual {v0, p1}, Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;->getAndInvalidateIfModified(Lcom/android/systemui/shared/recents/model/Task$TaskKey;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/lang/String;

    .line 296
    .local v0, "label":Ljava/lang/String;
    if-eqz v0, :cond_b

    .line 297
    return-object v0

    .line 301
    :cond_b
    invoke-virtual {p0, p1}, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->getAndUpdateActivityInfo(Lcom/android/systemui/shared/recents/model/Task$TaskKey;)Landroid/content/pm/ActivityInfo;

    move-result-object v1

    .line 302
    .local v1, "activityInfo":Landroid/content/pm/ActivityInfo;
    if-eqz v1, :cond_23

    .line 303
    invoke-static {}, Lcom/android/systemui/shared/system/ActivityManagerWrapper;->getInstance()Lcom/android/systemui/shared/system/ActivityManagerWrapper;

    move-result-object v2

    iget v3, p1, Lcom/android/systemui/shared/recents/model/Task$TaskKey;->userId:I

    invoke-virtual {v2, v1, v3, p2}, Lcom/android/systemui/shared/system/ActivityManagerWrapper;->getBadgedContentDescription(Landroid/content/pm/ActivityInfo;ILandroid/app/ActivityManager$TaskDescription;)Ljava/lang/String;

    move-result-object v0

    .line 305
    if-nez p2, :cond_22

    .line 311
    iget-object v2, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mContentDescriptionCache:Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;

    invoke-virtual {v2, p1, v0}, Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;->put(Lcom/android/systemui/shared/recents/model/Task$TaskKey;Ljava/lang/Object;)V

    .line 313
    :cond_22
    return-object v0

    .line 317
    :cond_23
    const-string v2, ""

    return-object v2
.end method

.method declared-synchronized getAndUpdateThumbnail(Lcom/android/systemui/shared/recents/model/Task$TaskKey;ZZ)Lcom/android/systemui/shared/recents/model/ThumbnailData;
    .registers 8
    .param p1, "taskKey"    # Lcom/android/systemui/shared/recents/model/Task$TaskKey;
    .param p2, "loadIfNotCached"    # Z
    .param p3, "storeInCache"    # Z

    monitor-enter p0

    .line 333
    :try_start_1
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mThumbnailCache:Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;

    invoke-virtual {v0, p1}, Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;->getAndInvalidateIfModified(Lcom/android/systemui/shared/recents/model/Task$TaskKey;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/android/systemui/shared/recents/model/ThumbnailData;
    :try_end_9
    .catchall {:try_start_1 .. :try_end_9} :catchall_41

    .line 334
    .local v0, "cached":Lcom/android/systemui/shared/recents/model/ThumbnailData;
    if-eqz v0, :cond_d

    .line 335
    monitor-exit p0

    return-object v0

    .line 338
    :cond_d
    :try_start_d
    iget-object v1, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mTempCache:Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;

    invoke-virtual {v1, p1}, Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;->getAndInvalidateIfModified(Lcom/android/systemui/shared/recents/model/Task$TaskKey;)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/android/systemui/shared/recents/model/ThumbnailData;

    move-object v0, v1

    .line 339
    if-eqz v0, :cond_1f

    .line 340
    iget-object v1, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mThumbnailCache:Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;

    invoke-virtual {v1, p1, v0}, Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;->put(Lcom/android/systemui/shared/recents/model/Task$TaskKey;Ljava/lang/Object;)V
    :try_end_1d
    .catchall {:try_start_d .. :try_end_1d} :catchall_41

    .line 341
    monitor-exit p0

    return-object v0

    .line 344
    :cond_1f
    if-eqz p2, :cond_3e

    .line 345
    :try_start_21
    iget v1, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mSvelteLevel:I

    const/4 v2, 0x3

    if-ge v1, v2, :cond_3e

    .line 347
    invoke-static {}, Lcom/android/systemui/shared/system/ActivityManagerWrapper;->getInstance()Lcom/android/systemui/shared/system/ActivityManagerWrapper;

    move-result-object v1

    iget v2, p1, Lcom/android/systemui/shared/recents/model/Task$TaskKey;->id:I

    const/4 v3, 0x1

    invoke-virtual {v1, v2, v3}, Lcom/android/systemui/shared/system/ActivityManagerWrapper;->getTaskThumbnail(IZ)Lcom/android/systemui/shared/recents/model/ThumbnailData;

    move-result-object v1

    .line 349
    .local v1, "thumbnailData":Lcom/android/systemui/shared/recents/model/ThumbnailData;
    iget-object v2, v1, Lcom/android/systemui/shared/recents/model/ThumbnailData;->thumbnail:Landroid/graphics/Bitmap;

    if-eqz v2, :cond_3e

    .line 350
    if-eqz p3, :cond_3c

    .line 351
    iget-object v2, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mThumbnailCache:Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;

    invoke-virtual {v2, p1, v1}, Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;->put(Lcom/android/systemui/shared/recents/model/Task$TaskKey;Ljava/lang/Object;)V
    :try_end_3c
    .catchall {:try_start_21 .. :try_end_3c} :catchall_41

    .line 353
    :cond_3c
    monitor-exit p0

    return-object v1

    .line 359
    .end local v1    # "thumbnailData":Lcom/android/systemui/shared/recents/model/ThumbnailData;
    :cond_3e
    const/4 v1, 0x0

    monitor-exit p0

    return-object v1

    .line 332
    .end local v0    # "cached":Lcom/android/systemui/shared/recents/model/ThumbnailData;
    .end local p1    # "taskKey":Lcom/android/systemui/shared/recents/model/Task$TaskKey;
    .end local p2    # "loadIfNotCached":Z
    .end local p3    # "storeInCache":Z
    :catchall_41
    move-exception p1

    monitor-exit p0

    throw p1
.end method

.method public getHighResThumbnailLoader()Lcom/android/systemui/shared/recents/model/HighResThumbnailLoader;
    .registers 2

    .line 141
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mHighResThumbnailLoader:Lcom/android/systemui/shared/recents/model/HighResThumbnailLoader;

    return-object v0
.end method

.method public getIconCacheSize()I
    .registers 2

    .line 132
    iget v0, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mMaxIconCacheSize:I

    return v0
.end method

.method public getThumbnailCacheSize()I
    .registers 2

    .line 137
    iget v0, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mMaxThumbnailCacheSize:I

    return v0
.end method

.method public loadTaskData(Lcom/android/systemui/shared/recents/model/Task;)V
    .registers 5
    .param p1, "t"    # Lcom/android/systemui/shared/recents/model/Task;

    .line 184
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mIconCache:Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;

    iget-object v1, p1, Lcom/android/systemui/shared/recents/model/Task;->key:Lcom/android/systemui/shared/recents/model/Task$TaskKey;

    invoke-virtual {v0, v1}, Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;->getAndInvalidateIfModified(Lcom/android/systemui/shared/recents/model/Task$TaskKey;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Landroid/graphics/drawable/Drawable;

    .line 185
    .local v0, "icon":Landroid/graphics/drawable/Drawable;
    if-eqz v0, :cond_e

    move-object v1, v0

    goto :goto_18

    :cond_e
    iget-object v1, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mIconLoader:Lcom/android/systemui/shared/recents/model/IconLoader;

    iget-object v2, p1, Lcom/android/systemui/shared/recents/model/Task;->key:Lcom/android/systemui/shared/recents/model/Task$TaskKey;

    iget v2, v2, Lcom/android/systemui/shared/recents/model/Task$TaskKey;->userId:I

    invoke-virtual {v1, v2}, Lcom/android/systemui/shared/recents/model/IconLoader;->getDefaultIcon(I)Landroid/graphics/drawable/Drawable;

    move-result-object v1

    :goto_18
    move-object v0, v1

    .line 186
    iget-object v1, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mLoadQueue:Lcom/android/systemui/shared/recents/model/TaskResourceLoadQueue;

    invoke-virtual {v1, p1}, Lcom/android/systemui/shared/recents/model/TaskResourceLoadQueue;->addTask(Lcom/android/systemui/shared/recents/model/Task;)V

    .line 187
    iget-object v1, p1, Lcom/android/systemui/shared/recents/model/Task;->thumbnail:Lcom/android/systemui/shared/recents/model/ThumbnailData;

    invoke-virtual {p1, v1, v0}, Lcom/android/systemui/shared/recents/model/Task;->notifyTaskDataLoaded(Lcom/android/systemui/shared/recents/model/ThumbnailData;Landroid/graphics/drawable/Drawable;)V

    .line 188
    return-void
.end method

.method public declared-synchronized loadTasks(Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan;Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan$Options;)V
    .registers 5
    .param p1, "plan"    # Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan;
    .param p2, "opts"    # Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan$Options;

    monitor-enter p0

    .line 162
    if-nez p2, :cond_d

    .line 163
    :try_start_3
    new-instance v0, Ljava/lang/RuntimeException;

    const-string v1, "Requires load options"

    invoke-direct {v0, v1}, Ljava/lang/RuntimeException;-><init>(Ljava/lang/String;)V

    throw v0

    .line 161
    .end local p1    # "plan":Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan;
    .end local p2    # "opts":Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan$Options;
    :catchall_b
    move-exception p1

    goto :goto_33

    .line 165
    .restart local p1    # "plan":Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan;
    .restart local p2    # "opts":Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan$Options;
    :cond_d
    iget-boolean v0, p2, Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan$Options;->onlyLoadForCache:Z

    if-eqz v0, :cond_21

    iget-boolean v0, p2, Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan$Options;->loadThumbnails:Z

    if-eqz v0, :cond_21

    .line 170
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mTempCache:Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;

    iget-object v1, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mThumbnailCache:Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;

    invoke-virtual {v0, v1}, Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;->copyEntries(Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;)V

    .line 171
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mThumbnailCache:Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;

    invoke-virtual {v0}, Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;->evictAll()V

    .line 173
    :cond_21
    invoke-virtual {p1, p2, p0}, Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan;->executePlan(Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan$Options;Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;)V

    .line 174
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mTempCache:Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;

    invoke-virtual {v0}, Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;->evictAll()V

    .line 175
    iget-boolean v0, p2, Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan$Options;->onlyLoadForCache:Z

    if-nez v0, :cond_31

    .line 176
    iget v0, p2, Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan$Options;->numVisibleTasks:I

    iput v0, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mNumVisibleTasksLoaded:I
    :try_end_31
    .catchall {:try_start_3 .. :try_end_31} :catchall_b

    .line 178
    :cond_31
    monitor-exit p0

    return-void

    .line 161
    .end local p1    # "plan":Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan;
    .end local p2    # "opts":Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan$Options;
    :goto_33
    monitor-exit p0

    throw p1
.end method

.method public onPackageChanged(Ljava/lang/String;)V
    .registers 6
    .param p1, "packageName"    # Ljava/lang/String;

    .line 252
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mActivityInfoCache:Landroid/util/LruCache;

    invoke-virtual {v0}, Landroid/util/LruCache;->snapshot()Ljava/util/Map;

    move-result-object v0

    .line 253
    .local v0, "activityInfoCache":Ljava/util/Map;, "Ljava/util/Map<Landroid/content/ComponentName;Landroid/content/pm/ActivityInfo;>;"
    invoke-interface {v0}, Ljava/util/Map;->keySet()Ljava/util/Set;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v1

    :goto_e
    invoke-interface {v1}, Ljava/util/Iterator;->hasNext()Z

    move-result v2

    if-eqz v2, :cond_2a

    invoke-interface {v1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Landroid/content/ComponentName;

    .line 254
    .local v2, "cn":Landroid/content/ComponentName;
    invoke-virtual {v2}, Landroid/content/ComponentName;->getPackageName()Ljava/lang/String;

    move-result-object v3

    invoke-virtual {v3, p1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v3

    if-eqz v3, :cond_29

    .line 258
    iget-object v3, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mActivityInfoCache:Landroid/util/LruCache;

    invoke-virtual {v3, v2}, Landroid/util/LruCache;->remove(Ljava/lang/Object;)Ljava/lang/Object;

    .line 260
    .end local v2    # "cn":Landroid/content/ComponentName;
    :cond_29
    goto :goto_e

    .line 261
    :cond_2a
    return-void
.end method

.method public declared-synchronized onTrimMemory(I)V
    .registers 5
    .param p1, "level"    # I

    monitor-enter p0

    .line 212
    const/4 v0, 0x5

    const/4 v1, 0x1

    if-eq p1, v0, :cond_68

    const/16 v0, 0xa

    if-eq p1, v0, :cond_4b

    const/16 v0, 0xf

    if-eq p1, v0, :cond_31

    const/16 v0, 0x14

    if-eq p1, v0, :cond_1e

    const/16 v0, 0x28

    if-eq p1, v0, :cond_68

    const/16 v0, 0x3c

    if-eq p1, v0, :cond_4b

    const/16 v0, 0x50

    if-eq p1, v0, :cond_31

    goto :goto_85

    .line 215
    :cond_1e
    :try_start_1e
    invoke-direct {p0}, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->stopLoader()V

    .line 216
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mIconCache:Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;

    iget v1, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mNumVisibleTasksLoaded:I

    iget v2, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mMaxIconCacheSize:I

    div-int/lit8 v2, v2, 0x2

    invoke-static {v1, v2}, Ljava/lang/Math;->max(II)I

    move-result v1

    invoke-virtual {v0, v1}, Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;->trimToSize(I)V

    .line 218
    goto :goto_85

    .line 236
    :cond_31
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mIconCache:Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;

    invoke-virtual {v0}, Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;->evictAll()V

    .line 237
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mActivityInfoCache:Landroid/util/LruCache;

    invoke-virtual {v0}, Landroid/util/LruCache;->evictAll()V

    .line 239
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mActivityLabelCache:Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;

    invoke-virtual {v0}, Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;->evictAll()V

    .line 240
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mContentDescriptionCache:Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;

    invoke-virtual {v0}, Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;->evictAll()V

    .line 241
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mThumbnailCache:Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;

    invoke-virtual {v0}, Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;->evictAll()V

    .line 242
    goto :goto_85

    .line 229
    :cond_4b
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mIconCache:Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;

    iget v2, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mMaxIconCacheSize:I

    div-int/lit8 v2, v2, 0x4

    invoke-static {v1, v2}, Ljava/lang/Math;->max(II)I

    move-result v2

    invoke-virtual {v0, v2}, Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;->trimToSize(I)V

    .line 230
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mActivityInfoCache:Landroid/util/LruCache;

    .line 231
    invoke-static {}, Landroid/app/ActivityManager;->getMaxRecentTasksStatic()I

    move-result v2

    div-int/lit8 v2, v2, 0x4

    .line 230
    invoke-static {v1, v2}, Ljava/lang/Math;->max(II)I

    move-result v1

    invoke-virtual {v0, v1}, Landroid/util/LruCache;->trimToSize(I)V

    .line 232
    goto :goto_85

    .line 222
    :cond_68
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mIconCache:Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;

    iget v2, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mMaxIconCacheSize:I

    div-int/lit8 v2, v2, 0x2

    invoke-static {v1, v2}, Ljava/lang/Math;->max(II)I

    move-result v2

    invoke-virtual {v0, v2}, Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;->trimToSize(I)V

    .line 223
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mActivityInfoCache:Landroid/util/LruCache;

    .line 224
    invoke-static {}, Landroid/app/ActivityManager;->getMaxRecentTasksStatic()I

    move-result v2

    div-int/lit8 v2, v2, 0x2

    .line 223
    invoke-static {v1, v2}, Ljava/lang/Math;->max(II)I

    move-result v1

    invoke-virtual {v0, v1}, Landroid/util/LruCache;->trimToSize(I)V
    :try_end_84
    .catchall {:try_start_1e .. :try_end_84} :catchall_87

    .line 225
    nop

    .line 246
    :goto_85
    monitor-exit p0

    return-void

    .line 211
    .end local p1    # "level":I
    :catchall_87
    move-exception p1

    monitor-exit p0

    throw p1
.end method

.method public declared-synchronized preloadTasks(Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan;I)V
    .registers 4
    .param p1, "plan"    # Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan;
    .param p2, "runningTaskId"    # I

    monitor-enter p0

    .line 146
    :try_start_1
    invoke-static {}, Lcom/android/systemui/shared/system/ActivityManagerWrapper;->getInstance()Lcom/android/systemui/shared/system/ActivityManagerWrapper;

    move-result-object v0

    invoke-virtual {v0}, Lcom/android/systemui/shared/system/ActivityManagerWrapper;->getCurrentUserId()I

    move-result v0

    invoke-virtual {p0, p1, p2, v0}, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->preloadTasks(Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan;II)V
    :try_end_c
    .catchall {:try_start_1 .. :try_end_c} :catchall_e

    .line 147
    monitor-exit p0

    return-void

    .line 145
    .end local p1    # "plan":Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan;
    .end local p2    # "runningTaskId":I
    :catchall_e
    move-exception p1

    monitor-exit p0

    throw p1
.end method

.method public declared-synchronized preloadTasks(Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan;II)V
    .registers 5
    .param p1, "plan"    # Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan;
    .param p2, "runningTaskId"    # I
    .param p3, "currentUserId"    # I

    monitor-enter p0

    .line 153
    :try_start_1
    const-string v0, "preloadPlan"

    invoke-static {v0}, Landroid/os/Trace;->beginSection(Ljava/lang/String;)V

    .line 154
    new-instance v0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan$PreloadOptions;

    invoke-direct {v0}, Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan$PreloadOptions;-><init>()V

    invoke-virtual {p1, v0, p0, p2, p3}, Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan;->preloadPlan(Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan$PreloadOptions;Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;II)V
    :try_end_e
    .catchall {:try_start_1 .. :try_end_e} :catchall_14

    .line 156
    :try_start_e
    invoke-static {}, Landroid/os/Trace;->endSection()V
    :try_end_11
    .catchall {:try_start_e .. :try_end_11} :catchall_19

    .line 157
    nop

    .line 158
    monitor-exit p0

    return-void

    .line 156
    :catchall_14
    move-exception v0

    :try_start_15
    invoke-static {}, Landroid/os/Trace;->endSection()V

    throw v0
    :try_end_19
    .catchall {:try_start_15 .. :try_end_19} :catchall_19

    .line 152
    .end local p1    # "plan":Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan;
    .end local p2    # "runningTaskId":I
    .end local p3    # "currentUserId":I
    :catchall_19
    move-exception p1

    monitor-exit p0

    throw p1
.end method

.method public setDefaultColors(II)V
    .registers 3
    .param p1, "defaultTaskBarBackgroundColor"    # I
    .param p2, "defaultTaskViewBackgroundColor"    # I

    .line 126
    iput p1, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mDefaultTaskBarBackgroundColor:I

    .line 127
    iput p2, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mDefaultTaskViewBackgroundColor:I

    .line 128
    return-void
.end method

.method public startLoader(Landroid/content/Context;)V
    .registers 3
    .param p1, "ctx"    # Landroid/content/Context;

    .line 395
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mLoader:Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;

    invoke-virtual {v0, p1}, Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;->start(Landroid/content/Context;)V

    .line 396
    return-void
.end method

.method public unloadTaskData(Lcom/android/systemui/shared/recents/model/Task;)V
    .registers 4
    .param p1, "t"    # Lcom/android/systemui/shared/recents/model/Task;

    .line 192
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mLoadQueue:Lcom/android/systemui/shared/recents/model/TaskResourceLoadQueue;

    invoke-virtual {v0, p1}, Lcom/android/systemui/shared/recents/model/TaskResourceLoadQueue;->removeTask(Lcom/android/systemui/shared/recents/model/Task;)V

    .line 193
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mIconLoader:Lcom/android/systemui/shared/recents/model/IconLoader;

    iget-object v1, p1, Lcom/android/systemui/shared/recents/model/Task;->key:Lcom/android/systemui/shared/recents/model/Task$TaskKey;

    iget v1, v1, Lcom/android/systemui/shared/recents/model/Task$TaskKey;->userId:I

    invoke-virtual {v0, v1}, Lcom/android/systemui/shared/recents/model/IconLoader;->getDefaultIcon(I)Landroid/graphics/drawable/Drawable;

    move-result-object v0

    invoke-virtual {p1, v0}, Lcom/android/systemui/shared/recents/model/Task;->notifyTaskDataUnloaded(Landroid/graphics/drawable/Drawable;)V

    .line 194
    return-void
.end method
