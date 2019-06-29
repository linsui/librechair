.class public Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;
.super Lcom/android/systemui/shared/recents/model/TaskKeyCache;
.source "TaskKeyStrongCache.java"


# annotations
.annotation system Ldalvik/annotation/Signature;
    value = {
        "<V:",
        "Ljava/lang/Object;",
        ">",
        "Lcom/android/systemui/shared/recents/model/TaskKeyCache<",
        "TV;>;"
    }
.end annotation


# static fields
.field private static final TAG:Ljava/lang/String; = "TaskKeyCache"


# instance fields
.field private final mCache:Landroid/util/ArrayMap;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Landroid/util/ArrayMap<",
            "Ljava/lang/Integer;",
            "TV;>;"
        }
    .end annotation
.end field


# direct methods
.method public constructor <init>()V
    .registers 2

    .line 28
    .local p0, "this":Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;, "Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache<TV;>;"
    invoke-direct {p0}, Lcom/android/systemui/shared/recents/model/TaskKeyCache;-><init>()V

    .line 32
    new-instance v0, Landroid/util/ArrayMap;

    invoke-direct {v0}, Landroid/util/ArrayMap;-><init>()V

    iput-object v0, p0, Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;->mCache:Landroid/util/ArrayMap;

    return-void
.end method


# virtual methods
.method final copyEntries(Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;)V
    .registers 6
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache<",
            "TV;>;)V"
        }
    .end annotation

    .line 35
    .local p0, "this":Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;, "Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache<TV;>;"
    .local p1, "other":Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;, "Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache<TV;>;"
    iget-object v0, p1, Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;->mKeys:Landroid/util/SparseArray;

    invoke-virtual {v0}, Landroid/util/SparseArray;->size()I

    move-result v0

    add-int/lit8 v0, v0, -0x1

    .line 35
    .local v0, "i":I
    :goto_8
    if-ltz v0, :cond_24

    .line 36
    iget-object v1, p1, Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;->mKeys:Landroid/util/SparseArray;

    invoke-virtual {v1, v0}, Landroid/util/SparseArray;->valueAt(I)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/android/systemui/shared/recents/model/Task$TaskKey;

    .line 37
    .local v1, "key":Lcom/android/systemui/shared/recents/model/Task$TaskKey;
    iget-object v2, p1, Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;->mCache:Landroid/util/ArrayMap;

    iget v3, v1, Lcom/android/systemui/shared/recents/model/Task$TaskKey;->id:I

    invoke-static {v3}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v3

    invoke-virtual {v2, v3}, Landroid/util/ArrayMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v2

    invoke-virtual {p0, v1, v2}, Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;->put(Lcom/android/systemui/shared/recents/model/Task$TaskKey;Ljava/lang/Object;)V

    .line 35
    .end local v1    # "key":Lcom/android/systemui/shared/recents/model/Task$TaskKey;
    add-int/lit8 v0, v0, -0x1

    goto :goto_8

    .line 39
    .end local v0    # "i":I
    :cond_24
    return-void
.end method

.method public dump(Ljava/lang/String;Ljava/io/PrintWriter;)V
    .registers 8
    .param p1, "prefix"    # Ljava/lang/String;
    .param p2, "writer"    # Ljava/io/PrintWriter;

    .line 42
    .local p0, "this":Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;, "Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache<TV;>;"
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v0, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string v1, "  "

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    .line 43
    .local v0, "innerPrefix":Ljava/lang/String;
    invoke-virtual {p2, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    const-string v1, "TaskKeyCache"

    invoke-virtual {p2, v1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    .line 44
    const-string v1, " numEntries="

    invoke-virtual {p2, v1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    iget-object v1, p0, Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;->mKeys:Landroid/util/SparseArray;

    invoke-virtual {v1}, Landroid/util/SparseArray;->size()I

    move-result v1

    invoke-virtual {p2, v1}, Ljava/io/PrintWriter;->print(I)V

    .line 45
    invoke-virtual {p2}, Ljava/io/PrintWriter;->println()V

    .line 46
    iget-object v1, p0, Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;->mKeys:Landroid/util/SparseArray;

    invoke-virtual {v1}, Landroid/util/SparseArray;->size()I

    move-result v1

    .line 47
    .local v1, "keyCount":I
    const/4 v2, 0x0

    .line 47
    .local v2, "i":I
    :goto_31
    if-ge v2, v1, :cond_48

    .line 48
    invoke-virtual {p2, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    iget-object v3, p0, Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;->mKeys:Landroid/util/SparseArray;

    iget-object v4, p0, Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;->mKeys:Landroid/util/SparseArray;

    invoke-virtual {v4, v2}, Landroid/util/SparseArray;->keyAt(I)I

    move-result v4

    invoke-virtual {v3, v4}, Landroid/util/SparseArray;->get(I)Ljava/lang/Object;

    move-result-object v3

    invoke-virtual {p2, v3}, Ljava/io/PrintWriter;->println(Ljava/lang/Object;)V

    .line 47
    add-int/lit8 v2, v2, 0x1

    goto :goto_31

    .line 50
    .end local v2    # "i":I
    :cond_48
    return-void
.end method

.method protected evictAllCache()V
    .registers 2

    .line 69
    .local p0, "this":Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;, "Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache<TV;>;"
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;->mCache:Landroid/util/ArrayMap;

    invoke-virtual {v0}, Landroid/util/ArrayMap;->clear()V

    .line 70
    return-void
.end method

.method protected getCacheEntry(I)Ljava/lang/Object;
    .registers 4
    .param p1, "id"    # I
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(I)TV;"
        }
    .end annotation

    .line 54
    .local p0, "this":Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;, "Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache<TV;>;"
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;->mCache:Landroid/util/ArrayMap;

    invoke-static {p1}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v1

    invoke-virtual {v0, v1}, Landroid/util/ArrayMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v0

    return-object v0
.end method

.method protected putCacheEntry(ILjava/lang/Object;)V
    .registers 5
    .param p1, "id"    # I
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(ITV;)V"
        }
    .end annotation

    .line 59
    .local p0, "this":Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;, "Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache<TV;>;"
    .local p2, "value":Ljava/lang/Object;, "TV;"
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;->mCache:Landroid/util/ArrayMap;

    invoke-static {p1}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v1

    invoke-virtual {v0, v1, p2}, Landroid/util/ArrayMap;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    .line 60
    return-void
.end method

.method protected removeCacheEntry(I)V
    .registers 4
    .param p1, "id"    # I

    .line 64
    .local p0, "this":Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;, "Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache<TV;>;"
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/TaskKeyStrongCache;->mCache:Landroid/util/ArrayMap;

    invoke-static {p1}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v1

    invoke-virtual {v0, v1}, Landroid/util/ArrayMap;->remove(Ljava/lang/Object;)Ljava/lang/Object;

    .line 65
    return-void
.end method
