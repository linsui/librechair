.class Lcom/android/systemui/shared/system/RemoteAnimationAdapterCompat$1$1;
.super Ljava/lang/Object;
.source "RemoteAnimationAdapterCompat.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/android/systemui/shared/system/RemoteAnimationAdapterCompat$1;->onAnimationStart([Landroid/view/RemoteAnimationTarget;Landroid/view/IRemoteAnimationFinishedCallback;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/android/systemui/shared/system/RemoteAnimationAdapterCompat$1;

.field final synthetic val$finishedCallback:Landroid/view/IRemoteAnimationFinishedCallback;


# direct methods
.method constructor <init>(Lcom/android/systemui/shared/system/RemoteAnimationAdapterCompat$1;Landroid/view/IRemoteAnimationFinishedCallback;)V
    .registers 3
    .param p1, "this$0"    # Lcom/android/systemui/shared/system/RemoteAnimationAdapterCompat$1;

    .line 51
    iput-object p1, p0, Lcom/android/systemui/shared/system/RemoteAnimationAdapterCompat$1$1;->this$0:Lcom/android/systemui/shared/system/RemoteAnimationAdapterCompat$1;

    iput-object p2, p0, Lcom/android/systemui/shared/system/RemoteAnimationAdapterCompat$1$1;->val$finishedCallback:Landroid/view/IRemoteAnimationFinishedCallback;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public run()V
    .registers 4

    .line 55
    :try_start_0
    iget-object v0, p0, Lcom/android/systemui/shared/system/RemoteAnimationAdapterCompat$1$1;->val$finishedCallback:Landroid/view/IRemoteAnimationFinishedCallback;

    invoke-interface {v0}, Landroid/view/IRemoteAnimationFinishedCallback;->onAnimationFinished()V
    :try_end_5
    .catch Landroid/os/RemoteException; {:try_start_0 .. :try_end_5} :catch_6

    .line 59
    goto :goto_e

    .line 56
    :catch_6
    move-exception v0

    .line 57
    .local v0, "e":Landroid/os/RemoteException;
    const-string v1, "ActivityOptionsCompat"

    const-string v2, "Failed to call app controlled animation finished callback"

    invoke-static {v1, v2, v0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    .line 60
    .end local v0    # "e":Landroid/os/RemoteException;
    :goto_e
    return-void
.end method
