.class Lcom/android/systemui/shared/recents/model/TaskKeyLruCache$1;
.super Landroid/util/LruCache;
.source "TaskKeyLruCache.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;-><init>(ILcom/android/systemui/shared/recents/model/TaskKeyLruCache$EvictionCallback;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "Landroid/util/LruCache<",
        "Ljava/lang/Integer;",
        "TV;>;"
    }
.end annotation


# instance fields
.field final synthetic this$0:Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;


# direct methods
.method constructor <init>(Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;I)V
    .registers 3
    .param p1, "this$0"    # Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;
    .param p2, "x0"    # I

    .line 48
    .local p0, "this":Lcom/android/systemui/shared/recents/model/TaskKeyLruCache$1;, "Lcom/android/systemui/shared/recents/model/TaskKeyLruCache$1;"
    iput-object p1, p0, Lcom/android/systemui/shared/recents/model/TaskKeyLruCache$1;->this$0:Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;

    invoke-direct {p0, p2}, Landroid/util/LruCache;-><init>(I)V

    return-void
.end method


# virtual methods
.method protected entryRemoved(ZLjava/lang/Integer;Ljava/lang/Object;Ljava/lang/Object;)V
    .registers 8
    .param p1, "evicted"    # Z
    .param p2, "taskId"    # Ljava/lang/Integer;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(Z",
            "Ljava/lang/Integer;",
            "TV;TV;)V"
        }
    .end annotation

    .line 52
    .local p0, "this":Lcom/android/systemui/shared/recents/model/TaskKeyLruCache$1;, "Lcom/android/systemui/shared/recents/model/TaskKeyLruCache$1;"
    .local p3, "oldV":Ljava/lang/Object;, "TV;"
    .local p4, "newV":Ljava/lang/Object;, "TV;"
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/TaskKeyLruCache$1;->this$0:Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;

    # getter for: Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;->mEvictionCallback:Lcom/android/systemui/shared/recents/model/TaskKeyLruCache$EvictionCallback;
    invoke-static {v0}, Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;->access$000(Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;)Lcom/android/systemui/shared/recents/model/TaskKeyLruCache$EvictionCallback;

    move-result-object v0

    if-eqz v0, :cond_1f

    .line 53
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/TaskKeyLruCache$1;->this$0:Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;

    # getter for: Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;->mEvictionCallback:Lcom/android/systemui/shared/recents/model/TaskKeyLruCache$EvictionCallback;
    invoke-static {v0}, Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;->access$000(Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;)Lcom/android/systemui/shared/recents/model/TaskKeyLruCache$EvictionCallback;

    move-result-object v0

    iget-object v1, p0, Lcom/android/systemui/shared/recents/model/TaskKeyLruCache$1;->this$0:Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;

    iget-object v1, v1, Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;->mKeys:Landroid/util/SparseArray;

    invoke-virtual {p2}, Ljava/lang/Integer;->intValue()I

    move-result v2

    invoke-virtual {v1, v2}, Landroid/util/SparseArray;->get(I)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/android/systemui/shared/recents/model/Task$TaskKey;

    invoke-interface {v0, v1}, Lcom/android/systemui/shared/recents/model/TaskKeyLruCache$EvictionCallback;->onEntryEvicted(Lcom/android/systemui/shared/recents/model/Task$TaskKey;)V

    .line 55
    :cond_1f
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/TaskKeyLruCache$1;->this$0:Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;

    iget-object v0, v0, Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;->mKeys:Landroid/util/SparseArray;

    invoke-virtual {p2}, Ljava/lang/Integer;->intValue()I

    move-result v1

    invoke-virtual {v0, v1}, Landroid/util/SparseArray;->remove(I)V

    .line 56
    return-void
.end method

.method protected bridge synthetic entryRemoved(ZLjava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
    .registers 5

    .line 48
    .local p0, "this":Lcom/android/systemui/shared/recents/model/TaskKeyLruCache$1;, "Lcom/android/systemui/shared/recents/model/TaskKeyLruCache$1;"
    check-cast p2, Ljava/lang/Integer;

    invoke-virtual {p0, p1, p2, p3, p4}, Lcom/android/systemui/shared/recents/model/TaskKeyLruCache$1;->entryRemoved(ZLjava/lang/Integer;Ljava/lang/Object;Ljava/lang/Object;)V

    return-void
.end method
