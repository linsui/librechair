.class public interface abstract Lcom/android/systemui/shared/recents/model/TaskStack$TaskStackCallbacks;
.super Ljava/lang/Object;
.source "TaskStack.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/android/systemui/shared/recents/model/TaskStack;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x609
    name = "TaskStackCallbacks"
.end annotation


# virtual methods
.method public abstract onStackTaskAdded(Lcom/android/systemui/shared/recents/model/TaskStack;Lcom/android/systemui/shared/recents/model/Task;)V
.end method

.method public abstract onStackTaskRemoved(Lcom/android/systemui/shared/recents/model/TaskStack;Lcom/android/systemui/shared/recents/model/Task;Lcom/android/systemui/shared/recents/model/Task;Lcom/android/systemui/shared/recents/utilities/AnimationProps;ZZ)V
.end method

.method public abstract onStackTasksRemoved(Lcom/android/systemui/shared/recents/model/TaskStack;)V
.end method

.method public abstract onStackTasksUpdated(Lcom/android/systemui/shared/recents/model/TaskStack;)V
.end method
