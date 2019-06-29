.class Lcom/android/systemui/shared/system/ActivityManagerWrapper$7;
.super Ljava/lang/Object;
.source "ActivityManagerWrapper.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/android/systemui/shared/system/ActivityManagerWrapper;->removeTask(I)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/android/systemui/shared/system/ActivityManagerWrapper;

.field final synthetic val$taskId:I


# direct methods
.method constructor <init>(Lcom/android/systemui/shared/system/ActivityManagerWrapper;I)V
    .registers 3
    .param p1, "this$0"    # Lcom/android/systemui/shared/system/ActivityManagerWrapper;

    .line 383
    iput-object p1, p0, Lcom/android/systemui/shared/system/ActivityManagerWrapper$7;->this$0:Lcom/android/systemui/shared/system/ActivityManagerWrapper;

    iput p2, p0, Lcom/android/systemui/shared/system/ActivityManagerWrapper$7;->val$taskId:I

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public run()V
    .registers 5

    .line 387
    :try_start_0
    invoke-static {}, Landroid/app/ActivityManager;->getService()Landroid/app/IActivityManager;

    move-result-object v0

    iget v1, p0, Lcom/android/systemui/shared/system/ActivityManagerWrapper$7;->val$taskId:I

    invoke-interface {v0, v1}, Landroid/app/IActivityManager;->removeTask(I)Z
    :try_end_9
    .catch Landroid/os/RemoteException; {:try_start_0 .. :try_end_9} :catch_a

    .line 390
    goto :goto_23

    .line 388
    :catch_a
    move-exception v0

    .line 389
    .local v0, "e":Landroid/os/RemoteException;
    const-string v1, "ActivityManagerWrapper"

    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    const-string v3, "Failed to remove task="

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget v3, p0, Lcom/android/systemui/shared/system/ActivityManagerWrapper$7;->val$taskId:I

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    invoke-static {v1, v2, v0}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    .line 391
    .end local v0    # "e":Landroid/os/RemoteException;
    :goto_23
    return-void
.end method
