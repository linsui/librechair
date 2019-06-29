.class public interface abstract Lcom/android/systemui/shared/recents/model/Task$TaskCallbacks;
.super Ljava/lang/Object;
.source "Task.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/android/systemui/shared/recents/model/Task;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x609
    name = "TaskCallbacks"
.end annotation


# virtual methods
.method public abstract onTaskDataLoaded(Lcom/android/systemui/shared/recents/model/Task;Lcom/android/systemui/shared/recents/model/ThumbnailData;)V
.end method

.method public abstract onTaskDataUnloaded()V
.end method

.method public abstract onTaskWindowingModeChanged()V
.end method
