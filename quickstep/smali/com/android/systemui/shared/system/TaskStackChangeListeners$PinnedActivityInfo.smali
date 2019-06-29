.class Lcom/android/systemui/shared/system/TaskStackChangeListeners$PinnedActivityInfo;
.super Ljava/lang/Object;
.source "TaskStackChangeListeners.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/android/systemui/shared/system/TaskStackChangeListeners;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0xa
    name = "PinnedActivityInfo"
.end annotation


# instance fields
.field final mPackageName:Ljava/lang/String;

.field final mStackId:I

.field final mTaskId:I

.field final mUserId:I


# direct methods
.method constructor <init>(Ljava/lang/String;III)V
    .registers 5
    .param p1, "packageName"    # Ljava/lang/String;
    .param p2, "userId"    # I
    .param p3, "taskId"    # I
    .param p4, "stackId"    # I

    .line 308
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 309
    iput-object p1, p0, Lcom/android/systemui/shared/system/TaskStackChangeListeners$PinnedActivityInfo;->mPackageName:Ljava/lang/String;

    .line 310
    iput p2, p0, Lcom/android/systemui/shared/system/TaskStackChangeListeners$PinnedActivityInfo;->mUserId:I

    .line 311
    iput p3, p0, Lcom/android/systemui/shared/system/TaskStackChangeListeners$PinnedActivityInfo;->mTaskId:I

    .line 312
    iput p4, p0, Lcom/android/systemui/shared/system/TaskStackChangeListeners$PinnedActivityInfo;->mStackId:I

    .line 313
    return-void
.end method
