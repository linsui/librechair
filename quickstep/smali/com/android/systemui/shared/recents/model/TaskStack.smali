.class public Lcom/android/systemui/shared/recents/model/TaskStack;
.super Ljava/lang/Object;
.source "TaskStack.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/android/systemui/shared/recents/model/TaskStack$TaskStackCallbacks;
    }
.end annotation


# static fields
.field private static final TAG:Ljava/lang/String; = "TaskStack"


# instance fields
.field private mCb:Lcom/android/systemui/shared/recents/model/TaskStack$TaskStackCallbacks;

.field private final mRawTaskList:Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList<",
            "Lcom/android/systemui/shared/recents/model/Task;",
            ">;"
        }
    .end annotation
.end field

.field private final mStackTaskList:Lcom/android/systemui/shared/recents/model/FilteredTaskList;


# direct methods
.method public constructor <init>()V
    .registers 3

    .line 68
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 64
    new-instance v0, Ljava/util/ArrayList;

    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    iput-object v0, p0, Lcom/android/systemui/shared/recents/model/TaskStack;->mRawTaskList:Ljava/util/ArrayList;

    .line 65
    new-instance v0, Lcom/android/systemui/shared/recents/model/FilteredTaskList;

    invoke-direct {v0}, Lcom/android/systemui/shared/recents/model/FilteredTaskList;-><init>()V

    iput-object v0, p0, Lcom/android/systemui/shared/recents/model/TaskStack;->mStackTaskList:Lcom/android/systemui/shared/recents/model/FilteredTaskList;

    .line 70
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/TaskStack;->mStackTaskList:Lcom/android/systemui/shared/recents/model/FilteredTaskList;

    sget-object v1, Lcom/android/systemui/shared/recents/model/-$$Lambda$TaskStack$gkuBLLtJ6FV7PDAxT__KECDzTOI;->INSTANCE:Lcom/android/systemui/shared/recents/model/-$$Lambda$TaskStack$gkuBLLtJ6FV7PDAxT__KECDzTOI;

    invoke-virtual {v0, v1}, Lcom/android/systemui/shared/recents/model/FilteredTaskList;->setFilter(Lcom/android/systemui/shared/recents/model/TaskFilter;)Z

    .line 71
    return-void
.end method

.method private createTaskKeyMapFromList(Ljava/util/List;)Landroid/util/ArrayMap;
    .registers 7
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/util/List<",
            "Lcom/android/systemui/shared/recents/model/Task;",
            ">;)",
            "Landroid/util/ArrayMap<",
            "Lcom/android/systemui/shared/recents/model/Task$TaskKey;",
            "Lcom/android/systemui/shared/recents/model/Task;",
            ">;"
        }
    .end annotation

    .line 374
    .local p1, "tasks":Ljava/util/List;, "Ljava/util/List<Lcom/android/systemui/shared/recents/model/Task;>;"
    new-instance v0, Landroid/util/ArrayMap;

    invoke-interface {p1}, Ljava/util/List;->size()I

    move-result v1

    invoke-direct {v0, v1}, Landroid/util/ArrayMap;-><init>(I)V

    .line 375
    .local v0, "map":Landroid/util/ArrayMap;, "Landroid/util/ArrayMap<Lcom/android/systemui/shared/recents/model/Task$TaskKey;Lcom/android/systemui/shared/recents/model/Task;>;"
    invoke-interface {p1}, Ljava/util/List;->size()I

    move-result v1

    .line 376
    .local v1, "taskCount":I
    const/4 v2, 0x0

    .line 376
    .local v2, "i":I
    :goto_e
    if-ge v2, v1, :cond_1e

    .line 377
    invoke-interface {p1, v2}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v3

    check-cast v3, Lcom/android/systemui/shared/recents/model/Task;

    .line 378
    .local v3, "task":Lcom/android/systemui/shared/recents/model/Task;
    iget-object v4, v3, Lcom/android/systemui/shared/recents/model/Task;->key:Lcom/android/systemui/shared/recents/model/Task$TaskKey;

    invoke-virtual {v0, v4, v3}, Landroid/util/ArrayMap;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    .line 376
    .end local v3    # "task":Lcom/android/systemui/shared/recents/model/Task;
    add-int/lit8 v2, v2, 0x1

    goto :goto_e

    .line 380
    .end local v2    # "i":I
    :cond_1e
    return-object v0
.end method

.method private getNextLaunchTargetRaw()Lcom/android/systemui/shared/recents/model/Task;
    .registers 5

    .line 297
    invoke-virtual {p0}, Lcom/android/systemui/shared/recents/model/TaskStack;->getTaskCount()I

    move-result v0

    .line 298
    .local v0, "taskCount":I
    const/4 v1, 0x0

    if-nez v0, :cond_8

    .line 299
    return-object v1

    .line 301
    :cond_8
    invoke-virtual {p0}, Lcom/android/systemui/shared/recents/model/TaskStack;->getLaunchTarget()Lcom/android/systemui/shared/recents/model/Task;

    move-result-object v2

    invoke-virtual {p0, v2}, Lcom/android/systemui/shared/recents/model/TaskStack;->indexOfTask(Lcom/android/systemui/shared/recents/model/Task;)I

    move-result v2

    .line 302
    .local v2, "launchTaskIndex":I
    const/4 v3, -0x1

    if-eq v2, v3, :cond_22

    if-lez v2, :cond_22

    .line 303
    invoke-virtual {p0}, Lcom/android/systemui/shared/recents/model/TaskStack;->getTasks()Ljava/util/ArrayList;

    move-result-object v1

    add-int/lit8 v3, v2, -0x1

    invoke-virtual {v1, v3}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/android/systemui/shared/recents/model/Task;

    return-object v1

    .line 305
    :cond_22
    return-object v1
.end method

.method static synthetic lambda$new$0(Landroid/util/SparseArray;Lcom/android/systemui/shared/recents/model/Task;I)Z
    .registers 4
    .param p0, "taskIdMap"    # Landroid/util/SparseArray;
    .param p1, "t"    # Lcom/android/systemui/shared/recents/model/Task;
    .param p2, "index"    # I

    .line 70
    iget-boolean v0, p1, Lcom/android/systemui/shared/recents/model/Task;->isStackTask:Z

    return v0
.end method


# virtual methods
.method public computeAllTasksList()Ljava/util/ArrayList;
    .registers 3
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()",
            "Ljava/util/ArrayList<",
            "Lcom/android/systemui/shared/recents/model/Task;",
            ">;"
        }
    .end annotation

    .line 238
    new-instance v0, Ljava/util/ArrayList;

    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    .line 239
    .local v0, "tasks":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Lcom/android/systemui/shared/recents/model/Task;>;"
    iget-object v1, p0, Lcom/android/systemui/shared/recents/model/TaskStack;->mStackTaskList:Lcom/android/systemui/shared/recents/model/FilteredTaskList;

    invoke-virtual {v1}, Lcom/android/systemui/shared/recents/model/FilteredTaskList;->getTasks()Ljava/util/ArrayList;

    move-result-object v1

    invoke-virtual {v0, v1}, Ljava/util/ArrayList;->addAll(Ljava/util/Collection;)Z

    .line 240
    return-object v0
.end method

.method public computeComponentsRemoved(Ljava/lang/String;I)Landroid/util/ArraySet;
    .registers 11
    .param p1, "packageName"    # Ljava/lang/String;
    .param p2, "userId"    # I
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/lang/String;",
            "I)",
            "Landroid/util/ArraySet<",
            "Landroid/content/ComponentName;",
            ">;"
        }
    .end annotation

    .line 333
    new-instance v0, Landroid/util/ArraySet;

    invoke-direct {v0}, Landroid/util/ArraySet;-><init>()V

    .line 334
    .local v0, "existingComponents":Landroid/util/ArraySet;, "Landroid/util/ArraySet<Landroid/content/ComponentName;>;"
    new-instance v1, Landroid/util/ArraySet;

    invoke-direct {v1}, Landroid/util/ArraySet;-><init>()V

    .line 335
    .local v1, "removedComponents":Landroid/util/ArraySet;, "Landroid/util/ArraySet<Landroid/content/ComponentName;>;"
    invoke-virtual {p0}, Lcom/android/systemui/shared/recents/model/TaskStack;->getTaskKeys()Ljava/util/ArrayList;

    move-result-object v2

    .line 336
    .local v2, "taskKeys":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Lcom/android/systemui/shared/recents/model/Task$TaskKey;>;"
    invoke-virtual {v2}, Ljava/util/ArrayList;->size()I

    move-result v3

    .line 337
    .local v3, "taskKeyCount":I
    const/4 v4, 0x0

    .line 337
    .local v4, "i":I
    :goto_13
    if-ge v4, v3, :cond_49

    .line 338
    invoke-virtual {v2, v4}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Lcom/android/systemui/shared/recents/model/Task$TaskKey;

    .line 341
    .local v5, "t":Lcom/android/systemui/shared/recents/model/Task$TaskKey;
    iget v6, v5, Lcom/android/systemui/shared/recents/model/Task$TaskKey;->userId:I

    if-eq v6, p2, :cond_20

    .line 341
    .end local v5    # "t":Lcom/android/systemui/shared/recents/model/Task$TaskKey;
    goto :goto_46

    .line 343
    .restart local v5    # "t":Lcom/android/systemui/shared/recents/model/Task$TaskKey;
    :cond_20
    invoke-virtual {v5}, Lcom/android/systemui/shared/recents/model/Task$TaskKey;->getComponent()Landroid/content/ComponentName;

    move-result-object v6

    .line 344
    .local v6, "cn":Landroid/content/ComponentName;
    invoke-virtual {v6}, Landroid/content/ComponentName;->getPackageName()Ljava/lang/String;

    move-result-object v7

    invoke-virtual {v7, p1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v7

    if-eqz v7, :cond_46

    .line 345
    invoke-virtual {v0, v6}, Landroid/util/ArraySet;->contains(Ljava/lang/Object;)Z

    move-result v7

    if-eqz v7, :cond_35

    .line 347
    goto :goto_46

    .line 349
    :cond_35
    invoke-static {}, Lcom/android/systemui/shared/system/PackageManagerWrapper;->getInstance()Lcom/android/systemui/shared/system/PackageManagerWrapper;

    move-result-object v7

    invoke-virtual {v7, v6, p2}, Lcom/android/systemui/shared/system/PackageManagerWrapper;->getActivityInfo(Landroid/content/ComponentName;I)Landroid/content/pm/ActivityInfo;

    move-result-object v7

    if-eqz v7, :cond_43

    .line 350
    invoke-virtual {v0, v6}, Landroid/util/ArraySet;->add(Ljava/lang/Object;)Z

    goto :goto_46

    .line 352
    :cond_43
    invoke-virtual {v1, v6}, Landroid/util/ArraySet;->add(Ljava/lang/Object;)Z

    .line 337
    .end local v5    # "t":Lcom/android/systemui/shared/recents/model/Task$TaskKey;
    .end local v6    # "cn":Landroid/content/ComponentName;
    :cond_46
    :goto_46
    add-int/lit8 v4, v4, 0x1

    goto :goto_13

    .line 356
    .end local v4    # "i":I
    :cond_49
    return-object v1
.end method

.method public dump(Ljava/lang/String;Ljava/io/PrintWriter;)V
    .registers 8
    .param p1, "prefix"    # Ljava/lang/String;
    .param p2, "writer"    # Ljava/io/PrintWriter;

    .line 384
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v0, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string v1, "  "

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    .line 386
    .local v0, "innerPrefix":Ljava/lang/String;
    invoke-virtual {p2, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    const-string v1, "TaskStack"

    invoke-virtual {p2, v1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    .line 387
    const-string v1, " numStackTasks="

    invoke-virtual {p2, v1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    iget-object v1, p0, Lcom/android/systemui/shared/recents/model/TaskStack;->mStackTaskList:Lcom/android/systemui/shared/recents/model/FilteredTaskList;

    invoke-virtual {v1}, Lcom/android/systemui/shared/recents/model/FilteredTaskList;->size()I

    move-result v1

    invoke-virtual {p2, v1}, Ljava/io/PrintWriter;->print(I)V

    .line 388
    invoke-virtual {p2}, Ljava/io/PrintWriter;->println()V

    .line 389
    iget-object v1, p0, Lcom/android/systemui/shared/recents/model/TaskStack;->mStackTaskList:Lcom/android/systemui/shared/recents/model/FilteredTaskList;

    invoke-virtual {v1}, Lcom/android/systemui/shared/recents/model/FilteredTaskList;->getTasks()Ljava/util/ArrayList;

    move-result-object v1

    .line 390
    .local v1, "tasks":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Lcom/android/systemui/shared/recents/model/Task;>;"
    invoke-virtual {v1}, Ljava/util/ArrayList;->size()I

    move-result v2

    .line 391
    .local v2, "taskCount":I
    const/4 v3, 0x0

    .line 391
    .local v3, "i":I
    :goto_35
    if-ge v3, v2, :cond_43

    .line 392
    invoke-virtual {v1, v3}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Lcom/android/systemui/shared/recents/model/Task;

    invoke-virtual {v4, v0, p2}, Lcom/android/systemui/shared/recents/model/Task;->dump(Ljava/lang/String;Ljava/io/PrintWriter;)V

    .line 391
    add-int/lit8 v3, v3, 0x1

    goto :goto_35

    .line 394
    .end local v3    # "i":I
    :cond_43
    return-void
.end method

.method public findTaskWithId(I)Lcom/android/systemui/shared/recents/model/Task;
    .registers 7
    .param p1, "taskId"    # I

    .line 315
    invoke-virtual {p0}, Lcom/android/systemui/shared/recents/model/TaskStack;->computeAllTasksList()Ljava/util/ArrayList;

    move-result-object v0

    .line 316
    .local v0, "tasks":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Lcom/android/systemui/shared/recents/model/Task;>;"
    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v1

    .line 317
    .local v1, "taskCount":I
    const/4 v2, 0x0

    .line 317
    .local v2, "i":I
    :goto_9
    if-ge v2, v1, :cond_1b

    .line 318
    invoke-virtual {v0, v2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v3

    check-cast v3, Lcom/android/systemui/shared/recents/model/Task;

    .line 319
    .local v3, "task":Lcom/android/systemui/shared/recents/model/Task;
    iget-object v4, v3, Lcom/android/systemui/shared/recents/model/Task;->key:Lcom/android/systemui/shared/recents/model/Task$TaskKey;

    iget v4, v4, Lcom/android/systemui/shared/recents/model/Task$TaskKey;->id:I

    if-ne v4, p1, :cond_18

    .line 320
    return-object v3

    .line 317
    .end local v3    # "task":Lcom/android/systemui/shared/recents/model/Task;
    :cond_18
    add-int/lit8 v2, v2, 0x1

    goto :goto_9

    .line 323
    .end local v2    # "i":I
    :cond_1b
    const/4 v2, 0x0

    return-object v2
.end method

.method public getFrontMostTask()Lcom/android/systemui/shared/recents/model/Task;
    .registers 3

    .line 208
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/TaskStack;->mStackTaskList:Lcom/android/systemui/shared/recents/model/FilteredTaskList;

    invoke-virtual {v0}, Lcom/android/systemui/shared/recents/model/FilteredTaskList;->getTasks()Ljava/util/ArrayList;

    move-result-object v0

    .line 209
    .local v0, "stackTasks":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Lcom/android/systemui/shared/recents/model/Task;>;"
    invoke-virtual {v0}, Ljava/util/ArrayList;->isEmpty()Z

    move-result v1

    if-eqz v1, :cond_e

    .line 210
    const/4 v1, 0x0

    return-object v1

    .line 212
    :cond_e
    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v1

    add-int/lit8 v1, v1, -0x1

    invoke-virtual {v0, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/android/systemui/shared/recents/model/Task;

    return-object v1
.end method

.method public getLaunchTarget()Lcom/android/systemui/shared/recents/model/Task;
    .registers 6

    .line 254
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/TaskStack;->mStackTaskList:Lcom/android/systemui/shared/recents/model/FilteredTaskList;

    invoke-virtual {v0}, Lcom/android/systemui/shared/recents/model/FilteredTaskList;->getTasks()Ljava/util/ArrayList;

    move-result-object v0

    .line 255
    .local v0, "tasks":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Lcom/android/systemui/shared/recents/model/Task;>;"
    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v1

    .line 256
    .local v1, "taskCount":I
    const/4 v2, 0x0

    .line 256
    .local v2, "i":I
    :goto_b
    if-ge v2, v1, :cond_1b

    .line 257
    invoke-virtual {v0, v2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v3

    check-cast v3, Lcom/android/systemui/shared/recents/model/Task;

    .line 258
    .local v3, "task":Lcom/android/systemui/shared/recents/model/Task;
    iget-boolean v4, v3, Lcom/android/systemui/shared/recents/model/Task;->isLaunchTarget:Z

    if-eqz v4, :cond_18

    .line 259
    return-object v3

    .line 256
    .end local v3    # "task":Lcom/android/systemui/shared/recents/model/Task;
    :cond_18
    add-int/lit8 v2, v2, 0x1

    goto :goto_b

    .line 262
    .end local v2    # "i":I
    :cond_1b
    const/4 v2, 0x0

    return-object v2
.end method

.method public getNextLaunchTarget()Lcom/android/systemui/shared/recents/model/Task;
    .registers 4

    .line 289
    invoke-direct {p0}, Lcom/android/systemui/shared/recents/model/TaskStack;->getNextLaunchTargetRaw()Lcom/android/systemui/shared/recents/model/Task;

    move-result-object v0

    .line 290
    .local v0, "nextLaunchTarget":Lcom/android/systemui/shared/recents/model/Task;
    if-eqz v0, :cond_7

    .line 291
    return-object v0

    .line 293
    :cond_7
    invoke-virtual {p0}, Lcom/android/systemui/shared/recents/model/TaskStack;->getTasks()Ljava/util/ArrayList;

    move-result-object v1

    invoke-virtual {p0}, Lcom/android/systemui/shared/recents/model/TaskStack;->getTaskCount()I

    move-result v2

    add-int/lit8 v2, v2, -0x1

    invoke-virtual {v1, v2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/android/systemui/shared/recents/model/Task;

    return-object v1
.end method

.method public getTaskCount()I
    .registers 2

    .line 247
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/TaskStack;->mStackTaskList:Lcom/android/systemui/shared/recents/model/FilteredTaskList;

    invoke-virtual {v0}, Lcom/android/systemui/shared/recents/model/FilteredTaskList;->size()I

    move-result v0

    return v0
.end method

.method public getTaskKeys()Ljava/util/ArrayList;
    .registers 7
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()",
            "Ljava/util/ArrayList<",
            "Lcom/android/systemui/shared/recents/model/Task$TaskKey;",
            ">;"
        }
    .end annotation

    .line 217
    new-instance v0, Ljava/util/ArrayList;

    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    .line 218
    .local v0, "taskKeys":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Lcom/android/systemui/shared/recents/model/Task$TaskKey;>;"
    invoke-virtual {p0}, Lcom/android/systemui/shared/recents/model/TaskStack;->computeAllTasksList()Ljava/util/ArrayList;

    move-result-object v1

    .line 219
    .local v1, "tasks":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Lcom/android/systemui/shared/recents/model/Task;>;"
    invoke-virtual {v1}, Ljava/util/ArrayList;->size()I

    move-result v2

    .line 220
    .local v2, "taskCount":I
    const/4 v3, 0x0

    .line 220
    .local v3, "i":I
    :goto_e
    if-ge v3, v2, :cond_1e

    .line 221
    invoke-virtual {v1, v3}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Lcom/android/systemui/shared/recents/model/Task;

    .line 222
    .local v4, "task":Lcom/android/systemui/shared/recents/model/Task;
    iget-object v5, v4, Lcom/android/systemui/shared/recents/model/Task;->key:Lcom/android/systemui/shared/recents/model/Task$TaskKey;

    invoke-virtual {v0, v5}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    .line 220
    .end local v4    # "task":Lcom/android/systemui/shared/recents/model/Task;
    add-int/lit8 v3, v3, 0x1

    goto :goto_e

    .line 224
    .end local v3    # "i":I
    :cond_1e
    return-object v0
.end method

.method public getTasks()Ljava/util/ArrayList;
    .registers 2
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()",
            "Ljava/util/ArrayList<",
            "Lcom/android/systemui/shared/recents/model/Task;",
            ">;"
        }
    .end annotation

    .line 231
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/TaskStack;->mStackTaskList:Lcom/android/systemui/shared/recents/model/FilteredTaskList;

    invoke-virtual {v0}, Lcom/android/systemui/shared/recents/model/FilteredTaskList;->getTasks()Ljava/util/ArrayList;

    move-result-object v0

    return-object v0
.end method

.method public indexOfTask(Lcom/android/systemui/shared/recents/model/Task;)I
    .registers 3
    .param p1, "t"    # Lcom/android/systemui/shared/recents/model/Task;

    .line 310
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/TaskStack;->mStackTaskList:Lcom/android/systemui/shared/recents/model/FilteredTaskList;

    invoke-virtual {v0, p1}, Lcom/android/systemui/shared/recents/model/FilteredTaskList;->indexOf(Lcom/android/systemui/shared/recents/model/Task;)I

    move-result v0

    return v0
.end method

.method public isNextLaunchTargetPip(J)Z
    .registers 10
    .param p1, "lastPipTime"    # J

    .line 269
    invoke-virtual {p0}, Lcom/android/systemui/shared/recents/model/TaskStack;->getLaunchTarget()Lcom/android/systemui/shared/recents/model/Task;

    move-result-object v0

    .line 270
    .local v0, "launchTarget":Lcom/android/systemui/shared/recents/model/Task;
    invoke-direct {p0}, Lcom/android/systemui/shared/recents/model/TaskStack;->getNextLaunchTargetRaw()Lcom/android/systemui/shared/recents/model/Task;

    move-result-object v1

    .line 271
    .local v1, "nextLaunchTarget":Lcom/android/systemui/shared/recents/model/Task;
    const/4 v2, 0x0

    const-wide/16 v3, 0x0

    const/4 v5, 0x1

    if-eqz v1, :cond_1d

    cmp-long v6, p1, v3

    if-lez v6, :cond_1d

    .line 273
    iget-object v3, v1, Lcom/android/systemui/shared/recents/model/Task;->key:Lcom/android/systemui/shared/recents/model/Task$TaskKey;

    iget-wide v3, v3, Lcom/android/systemui/shared/recents/model/Task$TaskKey;->lastActiveTime:J

    cmp-long v6, p1, v3

    if-lez v6, :cond_1c

    const/4 v2, 0x1

    nop

    :cond_1c
    return v2

    .line 274
    :cond_1d
    if-eqz v0, :cond_2a

    cmp-long v6, p1, v3

    if-lez v6, :cond_2a

    invoke-virtual {p0}, Lcom/android/systemui/shared/recents/model/TaskStack;->getTaskCount()I

    move-result v3

    if-ne v3, v5, :cond_2a

    .line 277
    return v5

    .line 279
    :cond_2a
    return v2
.end method

.method public removeAllTasks(Z)V
    .registers 6
    .param p1, "notifyStackChanges"    # Z

    .line 108
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/TaskStack;->mStackTaskList:Lcom/android/systemui/shared/recents/model/FilteredTaskList;

    invoke-virtual {v0}, Lcom/android/systemui/shared/recents/model/FilteredTaskList;->getTasks()Ljava/util/ArrayList;

    move-result-object v0

    .line 109
    .local v0, "tasks":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Lcom/android/systemui/shared/recents/model/Task;>;"
    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v1

    add-int/lit8 v1, v1, -0x1

    .line 109
    .local v1, "i":I
    :goto_c
    if-ltz v1, :cond_21

    .line 110
    invoke-virtual {v0, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lcom/android/systemui/shared/recents/model/Task;

    .line 111
    .local v2, "t":Lcom/android/systemui/shared/recents/model/Task;
    iget-object v3, p0, Lcom/android/systemui/shared/recents/model/TaskStack;->mStackTaskList:Lcom/android/systemui/shared/recents/model/FilteredTaskList;

    invoke-virtual {v3, v2}, Lcom/android/systemui/shared/recents/model/FilteredTaskList;->remove(Lcom/android/systemui/shared/recents/model/Task;)Z

    .line 112
    iget-object v3, p0, Lcom/android/systemui/shared/recents/model/TaskStack;->mRawTaskList:Ljava/util/ArrayList;

    invoke-virtual {v3, v2}, Ljava/util/ArrayList;->remove(Ljava/lang/Object;)Z

    .line 109
    .end local v2    # "t":Lcom/android/systemui/shared/recents/model/Task;
    add-int/lit8 v1, v1, -0x1

    goto :goto_c

    .line 114
    .end local v1    # "i":I
    :cond_21
    iget-object v1, p0, Lcom/android/systemui/shared/recents/model/TaskStack;->mCb:Lcom/android/systemui/shared/recents/model/TaskStack$TaskStackCallbacks;

    if-eqz v1, :cond_2c

    if-eqz p1, :cond_2c

    .line 116
    iget-object v1, p0, Lcom/android/systemui/shared/recents/model/TaskStack;->mCb:Lcom/android/systemui/shared/recents/model/TaskStack$TaskStackCallbacks;

    invoke-interface {v1, p0}, Lcom/android/systemui/shared/recents/model/TaskStack$TaskStackCallbacks;->onStackTasksRemoved(Lcom/android/systemui/shared/recents/model/TaskStack;)V

    .line 118
    :cond_2c
    return-void
.end method

.method public removeTask(Lcom/android/systemui/shared/recents/model/Task;Lcom/android/systemui/shared/recents/utilities/AnimationProps;Z)V
    .registers 5
    .param p1, "t"    # Lcom/android/systemui/shared/recents/model/Task;
    .param p2, "animation"    # Lcom/android/systemui/shared/recents/utilities/AnimationProps;
    .param p3, "fromDockGesture"    # Z

    .line 83
    const/4 v0, 0x1

    invoke-virtual {p0, p1, p2, p3, v0}, Lcom/android/systemui/shared/recents/model/TaskStack;->removeTask(Lcom/android/systemui/shared/recents/model/Task;Lcom/android/systemui/shared/recents/utilities/AnimationProps;ZZ)V

    .line 84
    return-void
.end method

.method public removeTask(Lcom/android/systemui/shared/recents/model/Task;Lcom/android/systemui/shared/recents/utilities/AnimationProps;ZZ)V
    .registers 13
    .param p1, "t"    # Lcom/android/systemui/shared/recents/model/Task;
    .param p2, "animation"    # Lcom/android/systemui/shared/recents/utilities/AnimationProps;
    .param p3, "fromDockGesture"    # Z
    .param p4, "dismissRecentsIfAllRemoved"    # Z

    .line 92
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/TaskStack;->mStackTaskList:Lcom/android/systemui/shared/recents/model/FilteredTaskList;

    invoke-virtual {v0, p1}, Lcom/android/systemui/shared/recents/model/FilteredTaskList;->contains(Lcom/android/systemui/shared/recents/model/Task;)Z

    move-result v0

    if-eqz v0, :cond_20

    .line 93
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/TaskStack;->mStackTaskList:Lcom/android/systemui/shared/recents/model/FilteredTaskList;

    invoke-virtual {v0, p1}, Lcom/android/systemui/shared/recents/model/FilteredTaskList;->remove(Lcom/android/systemui/shared/recents/model/Task;)Z

    .line 94
    invoke-virtual {p0}, Lcom/android/systemui/shared/recents/model/TaskStack;->getFrontMostTask()Lcom/android/systemui/shared/recents/model/Task;

    move-result-object v0

    .line 95
    .local v0, "newFrontMostTask":Lcom/android/systemui/shared/recents/model/Task;
    iget-object v1, p0, Lcom/android/systemui/shared/recents/model/TaskStack;->mCb:Lcom/android/systemui/shared/recents/model/TaskStack$TaskStackCallbacks;

    if-eqz v1, :cond_20

    .line 97
    iget-object v1, p0, Lcom/android/systemui/shared/recents/model/TaskStack;->mCb:Lcom/android/systemui/shared/recents/model/TaskStack$TaskStackCallbacks;

    move-object v2, p0

    move-object v3, p1

    move-object v4, v0

    move-object v5, p2

    move v6, p3

    move v7, p4

    invoke-interface/range {v1 .. v7}, Lcom/android/systemui/shared/recents/model/TaskStack$TaskStackCallbacks;->onStackTaskRemoved(Lcom/android/systemui/shared/recents/model/TaskStack;Lcom/android/systemui/shared/recents/model/Task;Lcom/android/systemui/shared/recents/model/Task;Lcom/android/systemui/shared/recents/utilities/AnimationProps;ZZ)V

    .line 101
    .end local v0    # "newFrontMostTask":Lcom/android/systemui/shared/recents/model/Task;
    :cond_20
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/TaskStack;->mRawTaskList:Ljava/util/ArrayList;

    invoke-virtual {v0, p1}, Ljava/util/ArrayList;->remove(Ljava/lang/Object;)Z

    .line 102
    return-void
.end method

.method public setCallbacks(Lcom/android/systemui/shared/recents/model/TaskStack$TaskStackCallbacks;)V
    .registers 2
    .param p1, "cb"    # Lcom/android/systemui/shared/recents/model/TaskStack$TaskStackCallbacks;

    .line 75
    iput-object p1, p0, Lcom/android/systemui/shared/recents/model/TaskStack;->mCb:Lcom/android/systemui/shared/recents/model/TaskStack$TaskStackCallbacks;

    .line 76
    return-void
.end method

.method public setTasks(Lcom/android/systemui/shared/recents/model/TaskStack;Z)V
    .registers 4
    .param p1, "stack"    # Lcom/android/systemui/shared/recents/model/TaskStack;
    .param p2, "notifyStackChanges"    # Z

    .line 125
    iget-object v0, p1, Lcom/android/systemui/shared/recents/model/TaskStack;->mRawTaskList:Ljava/util/ArrayList;

    invoke-virtual {p0, v0, p2}, Lcom/android/systemui/shared/recents/model/TaskStack;->setTasks(Ljava/util/List;Z)V

    .line 126
    return-void
.end method

.method public setTasks(Ljava/util/List;Z)V
    .registers 24
    .param p2, "notifyStackChanges"    # Z
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/util/List<",
            "Lcom/android/systemui/shared/recents/model/Task;",
            ">;Z)V"
        }
    .end annotation

    .line 136
    .local p1, "tasks":Ljava/util/List;, "Ljava/util/List<Lcom/android/systemui/shared/recents/model/Task;>;"
    move-object/from16 v7, p0

    .line 136
    iget-object v0, v7, Lcom/android/systemui/shared/recents/model/TaskStack;->mRawTaskList:Ljava/util/ArrayList;

    invoke-direct {v7, v0}, Lcom/android/systemui/shared/recents/model/TaskStack;->createTaskKeyMapFromList(Ljava/util/List;)Landroid/util/ArrayMap;

    move-result-object v8

    .line 137
    .local v8, "currentTasksMap":Landroid/util/ArrayMap;, "Landroid/util/ArrayMap<Lcom/android/systemui/shared/recents/model/Task$TaskKey;Lcom/android/systemui/shared/recents/model/Task;>;"
    invoke-direct/range {p0 .. p1}, Lcom/android/systemui/shared/recents/model/TaskStack;->createTaskKeyMapFromList(Ljava/util/List;)Landroid/util/ArrayMap;

    move-result-object v9

    .line 138
    .local v9, "newTasksMap":Landroid/util/ArrayMap;, "Landroid/util/ArrayMap<Lcom/android/systemui/shared/recents/model/Task$TaskKey;Lcom/android/systemui/shared/recents/model/Task;>;"
    new-instance v0, Ljava/util/ArrayList;

    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    move-object v10, v0

    .line 139
    .local v10, "addedTasks":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Lcom/android/systemui/shared/recents/model/Task;>;"
    new-instance v0, Ljava/util/ArrayList;

    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    move-object v11, v0

    .line 140
    .local v11, "removedTasks":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Lcom/android/systemui/shared/recents/model/Task;>;"
    new-instance v0, Ljava/util/ArrayList;

    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    move-object v12, v0

    .line 143
    .local v12, "allTasks":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Lcom/android/systemui/shared/recents/model/Task;>;"
    iget-object v0, v7, Lcom/android/systemui/shared/recents/model/TaskStack;->mCb:Lcom/android/systemui/shared/recents/model/TaskStack$TaskStackCallbacks;

    if-nez v0, :cond_25

    .line 144
    const/4 v0, 0x0

    .line 148
    .end local p2    # "notifyStackChanges":Z
    .local v0, "notifyStackChanges":Z
    move v13, v0

    goto :goto_27

    .line 148
    .end local v0    # "notifyStackChanges":Z
    .restart local p2    # "notifyStackChanges":Z
    :cond_25
    move/from16 v13, p2

    .line 148
    .end local p2    # "notifyStackChanges":Z
    .local v13, "notifyStackChanges":Z
    :goto_27
    iget-object v0, v7, Lcom/android/systemui/shared/recents/model/TaskStack;->mRawTaskList:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v0

    .line 149
    .local v0, "taskCount":I
    add-int/lit8 v1, v0, -0x1

    .line 149
    .local v1, "i":I
    :goto_2f
    if-ltz v1, :cond_49

    .line 150
    iget-object v2, v7, Lcom/android/systemui/shared/recents/model/TaskStack;->mRawTaskList:Ljava/util/ArrayList;

    invoke-virtual {v2, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lcom/android/systemui/shared/recents/model/Task;

    .line 151
    .local v2, "task":Lcom/android/systemui/shared/recents/model/Task;
    iget-object v3, v2, Lcom/android/systemui/shared/recents/model/Task;->key:Lcom/android/systemui/shared/recents/model/Task$TaskKey;

    invoke-virtual {v9, v3}, Landroid/util/ArrayMap;->containsKey(Ljava/lang/Object;)Z

    move-result v3

    if-nez v3, :cond_46

    .line 152
    if-eqz v13, :cond_46

    .line 153
    invoke-virtual {v11, v2}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    .line 149
    .end local v2    # "task":Lcom/android/systemui/shared/recents/model/Task;
    :cond_46
    add-int/lit8 v1, v1, -0x1

    goto :goto_2f

    .line 159
    .end local v1    # "i":I
    :cond_49
    invoke-interface/range {p1 .. p1}, Ljava/util/List;->size()I

    move-result v14

    .line 160
    .end local v0    # "taskCount":I
    .local v14, "taskCount":I
    const/4 v15, 0x0

    const/4 v0, 0x0

    .line 160
    .local v0, "i":I
    :goto_4f
    if-ge v0, v14, :cond_75

    .line 161
    move-object/from16 v6, p1

    invoke-interface {v6, v0}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/android/systemui/shared/recents/model/Task;

    .line 162
    .local v1, "newTask":Lcom/android/systemui/shared/recents/model/Task;
    iget-object v2, v1, Lcom/android/systemui/shared/recents/model/Task;->key:Lcom/android/systemui/shared/recents/model/Task$TaskKey;

    invoke-virtual {v8, v2}, Landroid/util/ArrayMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lcom/android/systemui/shared/recents/model/Task;

    .line 163
    .local v2, "currentTask":Lcom/android/systemui/shared/recents/model/Task;
    if-nez v2, :cond_69

    if-eqz v13, :cond_69

    .line 164
    invoke-virtual {v10, v1}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    goto :goto_6f

    .line 165
    :cond_69
    if-eqz v2, :cond_6f

    .line 168
    invoke-virtual {v2, v1}, Lcom/android/systemui/shared/recents/model/Task;->copyFrom(Lcom/android/systemui/shared/recents/model/Task;)V

    .line 169
    move-object v1, v2

    .line 171
    :cond_6f
    :goto_6f
    invoke-virtual {v12, v1}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    .line 160
    .end local v1    # "newTask":Lcom/android/systemui/shared/recents/model/Task;
    .end local v2    # "currentTask":Lcom/android/systemui/shared/recents/model/Task;
    add-int/lit8 v0, v0, 0x1

    goto :goto_4f

    .line 175
    .end local v0    # "i":I
    :cond_75
    move-object/from16 v6, p1

    invoke-virtual {v12}, Ljava/util/ArrayList;->size()I

    move-result v0

    add-int/lit8 v0, v0, -0x1

    .line 175
    .restart local v0    # "i":I
    :goto_7d
    if-ltz v0, :cond_8a

    .line 176
    invoke-virtual {v12, v0}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/android/systemui/shared/recents/model/Task;

    iput v0, v1, Lcom/android/systemui/shared/recents/model/Task;->temporarySortIndexInStack:I

    .line 175
    add-int/lit8 v0, v0, -0x1

    goto :goto_7d

    .line 179
    .end local v0    # "i":I
    :cond_8a
    iget-object v0, v7, Lcom/android/systemui/shared/recents/model/TaskStack;->mStackTaskList:Lcom/android/systemui/shared/recents/model/FilteredTaskList;

    invoke-virtual {v0, v12}, Lcom/android/systemui/shared/recents/model/FilteredTaskList;->set(Ljava/util/List;)V

    .line 180
    iget-object v0, v7, Lcom/android/systemui/shared/recents/model/TaskStack;->mRawTaskList:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->clear()V

    .line 181
    iget-object v0, v7, Lcom/android/systemui/shared/recents/model/TaskStack;->mRawTaskList:Ljava/util/ArrayList;

    invoke-virtual {v0, v12}, Ljava/util/ArrayList;->addAll(Ljava/util/Collection;)Z

    .line 184
    invoke-virtual {v11}, Ljava/util/ArrayList;->size()I

    move-result v5

    .line 185
    .local v5, "removedTaskCount":I
    invoke-virtual/range {p0 .. p0}, Lcom/android/systemui/shared/recents/model/TaskStack;->getFrontMostTask()Lcom/android/systemui/shared/recents/model/Task;

    move-result-object v16

    .line 186
    .local v16, "newFrontMostTask":Lcom/android/systemui/shared/recents/model/Task;
    const/4 v0, 0x0

    .line 186
    .restart local v0    # "i":I
    :goto_a2
    move v4, v0

    .line 186
    .end local v0    # "i":I
    .local v4, "i":I
    if-ge v4, v5, :cond_cb

    .line 187
    iget-object v0, v7, Lcom/android/systemui/shared/recents/model/TaskStack;->mCb:Lcom/android/systemui/shared/recents/model/TaskStack$TaskStackCallbacks;

    invoke-virtual {v11, v4}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v1

    move-object v2, v1

    check-cast v2, Lcom/android/systemui/shared/recents/model/Task;

    sget-object v17, Lcom/android/systemui/shared/recents/utilities/AnimationProps;->IMMEDIATE:Lcom/android/systemui/shared/recents/utilities/AnimationProps;

    const/16 v18, 0x0

    const/16 v19, 0x1

    move-object v1, v7

    move-object/from16 v3, v16

    move/from16 v20, v4

    move-object/from16 v4, v17

    .line 187
    .end local v4    # "i":I
    .local v20, "i":I
    move/from16 v17, v5

    move/from16 v5, v18

    .line 187
    .end local v5    # "removedTaskCount":I
    .local v17, "removedTaskCount":I
    move/from16 v6, v19

    invoke-interface/range {v0 .. v6}, Lcom/android/systemui/shared/recents/model/TaskStack$TaskStackCallbacks;->onStackTaskRemoved(Lcom/android/systemui/shared/recents/model/TaskStack;Lcom/android/systemui/shared/recents/model/Task;Lcom/android/systemui/shared/recents/model/Task;Lcom/android/systemui/shared/recents/utilities/AnimationProps;ZZ)V

    .line 186
    add-int/lit8 v0, v20, 0x1

    .line 186
    .end local v20    # "i":I
    .restart local v0    # "i":I
    move-object/from16 v6, p1

    move/from16 v5, v17

    goto :goto_a2

    .line 193
    .end local v0    # "i":I
    .end local v17    # "removedTaskCount":I
    .restart local v5    # "removedTaskCount":I
    :cond_cb
    move/from16 v17, v5

    .line 193
    .end local v5    # "removedTaskCount":I
    .restart local v17    # "removedTaskCount":I
    invoke-virtual {v10}, Ljava/util/ArrayList;->size()I

    move-result v0

    .line 194
    .local v0, "addedTaskCount":I
    nop

    .line 194
    .local v15, "i":I
    :goto_d2
    move v1, v15

    .line 194
    .end local v15    # "i":I
    .local v1, "i":I
    if-ge v1, v0, :cond_e3

    .line 195
    iget-object v2, v7, Lcom/android/systemui/shared/recents/model/TaskStack;->mCb:Lcom/android/systemui/shared/recents/model/TaskStack$TaskStackCallbacks;

    invoke-virtual {v10, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v3

    check-cast v3, Lcom/android/systemui/shared/recents/model/Task;

    invoke-interface {v2, v7, v3}, Lcom/android/systemui/shared/recents/model/TaskStack$TaskStackCallbacks;->onStackTaskAdded(Lcom/android/systemui/shared/recents/model/TaskStack;Lcom/android/systemui/shared/recents/model/Task;)V

    .line 194
    add-int/lit8 v15, v1, 0x1

    .line 194
    .end local v1    # "i":I
    .restart local v15    # "i":I
    goto :goto_d2

    .line 199
    .end local v15    # "i":I
    :cond_e3
    if-eqz v13, :cond_ea

    .line 200
    iget-object v1, v7, Lcom/android/systemui/shared/recents/model/TaskStack;->mCb:Lcom/android/systemui/shared/recents/model/TaskStack$TaskStackCallbacks;

    invoke-interface {v1, v7}, Lcom/android/systemui/shared/recents/model/TaskStack$TaskStackCallbacks;->onStackTasksUpdated(Lcom/android/systemui/shared/recents/model/TaskStack;)V

    .line 202
    :cond_ea
    return-void
.end method

.method public toString()Ljava/lang/String;
    .registers 7

    .line 361
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v1, "Stack Tasks ("

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v1, p0, Lcom/android/systemui/shared/recents/model/TaskStack;->mStackTaskList:Lcom/android/systemui/shared/recents/model/FilteredTaskList;

    invoke-virtual {v1}, Lcom/android/systemui/shared/recents/model/FilteredTaskList;->size()I

    move-result v1

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    const-string v1, "):\n"

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    .line 362
    .local v0, "str":Ljava/lang/String;
    iget-object v1, p0, Lcom/android/systemui/shared/recents/model/TaskStack;->mStackTaskList:Lcom/android/systemui/shared/recents/model/FilteredTaskList;

    invoke-virtual {v1}, Lcom/android/systemui/shared/recents/model/FilteredTaskList;->getTasks()Ljava/util/ArrayList;

    move-result-object v1

    .line 363
    .local v1, "tasks":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Lcom/android/systemui/shared/recents/model/Task;>;"
    invoke-virtual {v1}, Ljava/util/ArrayList;->size()I

    move-result v2

    .line 364
    .local v2, "taskCount":I
    const/4 v3, 0x0

    .line 364
    .local v3, "i":I
    :goto_27
    if-ge v3, v2, :cond_4f

    .line 365
    new-instance v4, Ljava/lang/StringBuilder;

    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v4, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string v5, "    "

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1, v3}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Lcom/android/systemui/shared/recents/model/Task;

    invoke-virtual {v5}, Lcom/android/systemui/shared/recents/model/Task;->toString()Ljava/lang/String;

    move-result-object v5

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string v5, "\n"

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    .line 364
    add-int/lit8 v3, v3, 0x1

    goto :goto_27

    .line 367
    .end local v3    # "i":I
    :cond_4f
    return-object v0
.end method
