.class Lcom/android/systemui/shared/system/ActivityManagerWrapper$6;
.super Ljava/lang/Object;
.source "ActivityManagerWrapper.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/android/systemui/shared/system/ActivityManagerWrapper;->closeSystemWindows(Ljava/lang/String;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/android/systemui/shared/system/ActivityManagerWrapper;

.field final synthetic val$reason:Ljava/lang/String;


# direct methods
.method constructor <init>(Lcom/android/systemui/shared/system/ActivityManagerWrapper;Ljava/lang/String;)V
    .registers 3
    .param p1, "this$0"    # Lcom/android/systemui/shared/system/ActivityManagerWrapper;

    .line 367
    iput-object p1, p0, Lcom/android/systemui/shared/system/ActivityManagerWrapper$6;->this$0:Lcom/android/systemui/shared/system/ActivityManagerWrapper;

    iput-object p2, p0, Lcom/android/systemui/shared/system/ActivityManagerWrapper$6;->val$reason:Ljava/lang/String;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public run()V
    .registers 4

    .line 371
    :try_start_0
    invoke-static {}, Landroid/app/ActivityManager;->getService()Landroid/app/IActivityManager;

    move-result-object v0

    iget-object v1, p0, Lcom/android/systemui/shared/system/ActivityManagerWrapper$6;->val$reason:Ljava/lang/String;

    invoke-interface {v0, v1}, Landroid/app/IActivityManager;->closeSystemDialogs(Ljava/lang/String;)V
    :try_end_9
    .catch Landroid/os/RemoteException; {:try_start_0 .. :try_end_9} :catch_a

    .line 374
    goto :goto_12

    .line 372
    :catch_a
    move-exception v0

    .line 373
    .local v0, "e":Landroid/os/RemoteException;
    const-string v1, "ActivityManagerWrapper"

    const-string v2, "Failed to close system windows"

    invoke-static {v1, v2, v0}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    .line 375
    .end local v0    # "e":Landroid/os/RemoteException;
    :goto_12
    return-void
.end method
