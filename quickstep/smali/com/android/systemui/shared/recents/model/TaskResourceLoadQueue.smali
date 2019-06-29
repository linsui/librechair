.class Lcom/android/systemui/shared/recents/model/TaskResourceLoadQueue;
.super Ljava/lang/Object;
.source "TaskResourceLoadQueue.java"


# instance fields
.field private final mQueue:Ljava/util/concurrent/ConcurrentLinkedQueue;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/concurrent/ConcurrentLinkedQueue<",
            "Lcom/android/systemui/shared/recents/model/Task;",
            ">;"
        }
    .end annotation
.end field


# direct methods
.method constructor <init>()V
    .registers 2

    .line 24
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 26
    new-instance v0, Ljava/util/concurrent/ConcurrentLinkedQueue;

    invoke-direct {v0}, Ljava/util/concurrent/ConcurrentLinkedQueue;-><init>()V

    iput-object v0, p0, Lcom/android/systemui/shared/recents/model/TaskResourceLoadQueue;->mQueue:Ljava/util/concurrent/ConcurrentLinkedQueue;

    return-void
.end method


# virtual methods
.method addTask(Lcom/android/systemui/shared/recents/model/Task;)V
    .registers 3
    .param p1, "t"    # Lcom/android/systemui/shared/recents/model/Task;

    .line 30
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/TaskResourceLoadQueue;->mQueue:Ljava/util/concurrent/ConcurrentLinkedQueue;

    invoke-virtual {v0, p1}, Ljava/util/concurrent/ConcurrentLinkedQueue;->contains(Ljava/lang/Object;)Z

    move-result v0

    if-nez v0, :cond_d

    .line 31
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/TaskResourceLoadQueue;->mQueue:Ljava/util/concurrent/ConcurrentLinkedQueue;

    invoke-virtual {v0, p1}, Ljava/util/concurrent/ConcurrentLinkedQueue;->add(Ljava/lang/Object;)Z

    .line 33
    :cond_d
    monitor-enter p0

    .line 34
    :try_start_e
    invoke-virtual {p0}, Ljava/lang/Object;->notifyAll()V

    .line 35
    monitor-exit p0

    .line 36
    return-void

    .line 35
    :catchall_13
    move-exception v0

    monitor-exit p0
    :try_end_15
    .catchall {:try_start_e .. :try_end_15} :catchall_13

    throw v0
.end method

.method clearTasks()V
    .registers 2

    .line 53
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/TaskResourceLoadQueue;->mQueue:Ljava/util/concurrent/ConcurrentLinkedQueue;

    invoke-virtual {v0}, Ljava/util/concurrent/ConcurrentLinkedQueue;->clear()V

    .line 54
    return-void
.end method

.method isEmpty()Z
    .registers 2

    .line 58
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/TaskResourceLoadQueue;->mQueue:Ljava/util/concurrent/ConcurrentLinkedQueue;

    invoke-virtual {v0}, Ljava/util/concurrent/ConcurrentLinkedQueue;->isEmpty()Z

    move-result v0

    return v0
.end method

.method nextTask()Lcom/android/systemui/shared/recents/model/Task;
    .registers 2

    .line 43
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/TaskResourceLoadQueue;->mQueue:Ljava/util/concurrent/ConcurrentLinkedQueue;

    invoke-virtual {v0}, Ljava/util/concurrent/ConcurrentLinkedQueue;->poll()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/android/systemui/shared/recents/model/Task;

    return-object v0
.end method

.method removeTask(Lcom/android/systemui/shared/recents/model/Task;)V
    .registers 3
    .param p1, "t"    # Lcom/android/systemui/shared/recents/model/Task;

    .line 48
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/TaskResourceLoadQueue;->mQueue:Ljava/util/concurrent/ConcurrentLinkedQueue;

    invoke-virtual {v0, p1}, Ljava/util/concurrent/ConcurrentLinkedQueue;->remove(Ljava/lang/Object;)Z

    .line 49
    return-void
.end method
