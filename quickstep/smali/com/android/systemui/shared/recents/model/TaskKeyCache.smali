.class public abstract Lcom/android/systemui/shared/recents/model/TaskKeyCache;
.super Ljava/lang/Object;
.source "TaskKeyCache.java"


# annotations
.annotation system Ldalvik/annotation/Signature;
    value = {
        "<V:",
        "Ljava/lang/Object;",
        ">",
        "Ljava/lang/Object;"
    }
.end annotation


# static fields
.field protected static final TAG:Ljava/lang/String; = "TaskKeyCache"


# instance fields
.field protected final mKeys:Landroid/util/SparseArray;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Landroid/util/SparseArray<",
            "Lcom/android/systemui/shared/recents/model/Task$TaskKey;",
            ">;"
        }
    .end annotation
.end field


# direct methods
.method public constructor <init>()V
    .registers 2

    .line 27
    .local p0, "this":Lcom/android/systemui/shared/recents/model/TaskKeyCache;, "Lcom/android/systemui/shared/recents/model/TaskKeyCache<TV;>;"
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 31
    new-instance v0, Landroid/util/SparseArray;

    invoke-direct {v0}, Landroid/util/SparseArray;-><init>()V

    iput-object v0, p0, Lcom/android/systemui/shared/recents/model/TaskKeyCache;->mKeys:Landroid/util/SparseArray;

    return-void
.end method


# virtual methods
.method final evictAll()V
    .registers 2

    .line 81
    .local p0, "this":Lcom/android/systemui/shared/recents/model/TaskKeyCache;, "Lcom/android/systemui/shared/recents/model/TaskKeyCache<TV;>;"
    invoke-virtual {p0}, Lcom/android/systemui/shared/recents/model/TaskKeyCache;->evictAllCache()V

    .line 82
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/TaskKeyCache;->mKeys:Landroid/util/SparseArray;

    invoke-virtual {v0}, Landroid/util/SparseArray;->clear()V

    .line 83
    return-void
.end method

.method protected abstract evictAllCache()V
.end method

.method final get(Lcom/android/systemui/shared/recents/model/Task$TaskKey;)Ljava/lang/Object;
    .registers 3
    .param p1, "key"    # Lcom/android/systemui/shared/recents/model/Task$TaskKey;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Lcom/android/systemui/shared/recents/model/Task$TaskKey;",
            ")TV;"
        }
    .end annotation

    .line 38
    .local p0, "this":Lcom/android/systemui/shared/recents/model/TaskKeyCache;, "Lcom/android/systemui/shared/recents/model/TaskKeyCache<TV;>;"
    iget v0, p1, Lcom/android/systemui/shared/recents/model/Task$TaskKey;->id:I

    invoke-virtual {p0, v0}, Lcom/android/systemui/shared/recents/model/TaskKeyCache;->getCacheEntry(I)Ljava/lang/Object;

    move-result-object v0

    return-object v0
.end method

.method final getAndInvalidateIfModified(Lcom/android/systemui/shared/recents/model/Task$TaskKey;)Ljava/lang/Object;
    .registers 8
    .param p1, "key"    # Lcom/android/systemui/shared/recents/model/Task$TaskKey;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Lcom/android/systemui/shared/recents/model/Task$TaskKey;",
            ")TV;"
        }
    .end annotation

    .line 46
    .local p0, "this":Lcom/android/systemui/shared/recents/model/TaskKeyCache;, "Lcom/android/systemui/shared/recents/model/TaskKeyCache<TV;>;"
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/TaskKeyCache;->mKeys:Landroid/util/SparseArray;

    iget v1, p1, Lcom/android/systemui/shared/recents/model/Task$TaskKey;->id:I

    invoke-virtual {v0, v1}, Landroid/util/SparseArray;->get(I)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/android/systemui/shared/recents/model/Task$TaskKey;

    .line 47
    .local v0, "lastKey":Lcom/android/systemui/shared/recents/model/Task$TaskKey;
    if-eqz v0, :cond_1f

    .line 48
    iget v1, v0, Lcom/android/systemui/shared/recents/model/Task$TaskKey;->windowingMode:I

    iget v2, p1, Lcom/android/systemui/shared/recents/model/Task$TaskKey;->windowingMode:I

    if-ne v1, v2, :cond_1a

    iget-wide v1, v0, Lcom/android/systemui/shared/recents/model/Task$TaskKey;->lastActiveTime:J

    iget-wide v3, p1, Lcom/android/systemui/shared/recents/model/Task$TaskKey;->lastActiveTime:J

    cmp-long v5, v1, v3

    if-eqz v5, :cond_1f

    .line 52
    :cond_1a
    invoke-virtual {p0, p1}, Lcom/android/systemui/shared/recents/model/TaskKeyCache;->remove(Lcom/android/systemui/shared/recents/model/Task$TaskKey;)V

    .line 53
    const/4 v1, 0x0

    return-object v1

    .line 58
    :cond_1f
    iget v1, p1, Lcom/android/systemui/shared/recents/model/Task$TaskKey;->id:I

    invoke-virtual {p0, v1}, Lcom/android/systemui/shared/recents/model/TaskKeyCache;->getCacheEntry(I)Ljava/lang/Object;

    move-result-object v1

    return-object v1
.end method

.method protected abstract getCacheEntry(I)Ljava/lang/Object;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(I)TV;"
        }
    .end annotation
.end method

.method final put(Lcom/android/systemui/shared/recents/model/Task$TaskKey;Ljava/lang/Object;)V
    .registers 6
    .param p1, "key"    # Lcom/android/systemui/shared/recents/model/Task$TaskKey;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Lcom/android/systemui/shared/recents/model/Task$TaskKey;",
            "TV;)V"
        }
    .end annotation

    .line 63
    .local p0, "this":Lcom/android/systemui/shared/recents/model/TaskKeyCache;, "Lcom/android/systemui/shared/recents/model/TaskKeyCache<TV;>;"
    .local p2, "value":Ljava/lang/Object;, "TV;"
    if-eqz p1, :cond_12

    if-nez p2, :cond_5

    goto :goto_12

    .line 67
    :cond_5
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/TaskKeyCache;->mKeys:Landroid/util/SparseArray;

    iget v1, p1, Lcom/android/systemui/shared/recents/model/Task$TaskKey;->id:I

    invoke-virtual {v0, v1, p1}, Landroid/util/SparseArray;->put(ILjava/lang/Object;)V

    .line 68
    iget v0, p1, Lcom/android/systemui/shared/recents/model/Task$TaskKey;->id:I

    invoke-virtual {p0, v0, p2}, Lcom/android/systemui/shared/recents/model/TaskKeyCache;->putCacheEntry(ILjava/lang/Object;)V

    .line 69
    return-void

    .line 64
    :cond_12
    :goto_12
    const-string v0, "TaskKeyCache"

    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    const-string v2, "Unexpected null key or value: "

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    const-string v2, ", "

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1, p2}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-static {v0, v1}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    .line 65
    return-void
.end method

.method protected abstract putCacheEntry(ILjava/lang/Object;)V
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(ITV;)V"
        }
    .end annotation
.end method

.method final remove(Lcom/android/systemui/shared/recents/model/Task$TaskKey;)V
    .registers 4
    .param p1, "key"    # Lcom/android/systemui/shared/recents/model/Task$TaskKey;

    .line 75
    .local p0, "this":Lcom/android/systemui/shared/recents/model/TaskKeyCache;, "Lcom/android/systemui/shared/recents/model/TaskKeyCache<TV;>;"
    iget v0, p1, Lcom/android/systemui/shared/recents/model/Task$TaskKey;->id:I

    invoke-virtual {p0, v0}, Lcom/android/systemui/shared/recents/model/TaskKeyCache;->removeCacheEntry(I)V

    .line 76
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/TaskKeyCache;->mKeys:Landroid/util/SparseArray;

    iget v1, p1, Lcom/android/systemui/shared/recents/model/Task$TaskKey;->id:I

    invoke-virtual {v0, v1}, Landroid/util/SparseArray;->remove(I)V

    .line 77
    return-void
.end method

.method protected abstract removeCacheEntry(I)V
.end method
