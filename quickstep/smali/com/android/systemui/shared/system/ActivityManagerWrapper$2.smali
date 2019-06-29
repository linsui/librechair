.class Lcom/android/systemui/shared/system/ActivityManagerWrapper$2;
.super Landroid/view/IRecentsAnimationRunner$Stub;
.source "ActivityManagerWrapper.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/android/systemui/shared/system/ActivityManagerWrapper;->startRecentsActivity(Landroid/content/Intent;Lcom/android/systemui/shared/system/AssistDataReceiver;Lcom/android/systemui/shared/system/RecentsAnimationListener;Ljava/util/function/Consumer;Landroid/os/Handler;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/android/systemui/shared/system/ActivityManagerWrapper;

.field final synthetic val$animationHandler:Lcom/android/systemui/shared/system/RecentsAnimationListener;


# direct methods
.method constructor <init>(Lcom/android/systemui/shared/system/ActivityManagerWrapper;Lcom/android/systemui/shared/system/RecentsAnimationListener;)V
    .registers 3
    .param p1, "this$0"    # Lcom/android/systemui/shared/system/ActivityManagerWrapper;

    .line 220
    iput-object p1, p0, Lcom/android/systemui/shared/system/ActivityManagerWrapper$2;->this$0:Lcom/android/systemui/shared/system/ActivityManagerWrapper;

    iput-object p2, p0, Lcom/android/systemui/shared/system/ActivityManagerWrapper$2;->val$animationHandler:Lcom/android/systemui/shared/system/RecentsAnimationListener;

    invoke-direct {p0}, Landroid/view/IRecentsAnimationRunner$Stub;-><init>()V

    return-void
.end method


# virtual methods
.method public onAnimationCanceled()V
    .registers 2

    .line 233
    iget-object v0, p0, Lcom/android/systemui/shared/system/ActivityManagerWrapper$2;->val$animationHandler:Lcom/android/systemui/shared/system/RecentsAnimationListener;

    invoke-interface {v0}, Lcom/android/systemui/shared/system/RecentsAnimationListener;->onAnimationCanceled()V

    .line 234
    return-void
.end method

.method public onAnimationStart(Landroid/view/IRecentsAnimationController;[Landroid/view/RemoteAnimationTarget;Landroid/graphics/Rect;Landroid/graphics/Rect;)V
    .registers 8
    .param p1, "controller"    # Landroid/view/IRecentsAnimationController;
    .param p2, "apps"    # [Landroid/view/RemoteAnimationTarget;
    .param p3, "homeContentInsets"    # Landroid/graphics/Rect;
    .param p4, "minimizedHomeBounds"    # Landroid/graphics/Rect;

    .line 224
    new-instance v0, Lcom/android/systemui/shared/system/RecentsAnimationControllerCompat;

    invoke-direct {v0, p1}, Lcom/android/systemui/shared/system/RecentsAnimationControllerCompat;-><init>(Landroid/view/IRecentsAnimationController;)V

    .line 226
    .local v0, "controllerCompat":Lcom/android/systemui/shared/system/RecentsAnimationControllerCompat;
    nop

    .line 227
    invoke-static {p2}, Lcom/android/systemui/shared/system/RemoteAnimationTargetCompat;->wrap([Landroid/view/RemoteAnimationTarget;)[Lcom/android/systemui/shared/system/RemoteAnimationTargetCompat;

    move-result-object v1

    .line 228
    .local v1, "appsCompat":[Lcom/android/systemui/shared/system/RemoteAnimationTargetCompat;
    iget-object v2, p0, Lcom/android/systemui/shared/system/ActivityManagerWrapper$2;->val$animationHandler:Lcom/android/systemui/shared/system/RecentsAnimationListener;

    invoke-interface {v2, v0, v1, p3, p4}, Lcom/android/systemui/shared/system/RecentsAnimationListener;->onAnimationStart(Lcom/android/systemui/shared/system/RecentsAnimationControllerCompat;[Lcom/android/systemui/shared/system/RemoteAnimationTargetCompat;Landroid/graphics/Rect;Landroid/graphics/Rect;)V

    .line 230
    return-void
.end method
