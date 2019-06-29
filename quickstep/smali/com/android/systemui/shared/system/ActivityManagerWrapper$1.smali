.class Lcom/android/systemui/shared/system/ActivityManagerWrapper$1;
.super Landroid/app/IAssistDataReceiver$Stub;
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

.field final synthetic val$assistDataReceiver:Lcom/android/systemui/shared/system/AssistDataReceiver;


# direct methods
.method constructor <init>(Lcom/android/systemui/shared/system/ActivityManagerWrapper;Lcom/android/systemui/shared/system/AssistDataReceiver;)V
    .registers 3
    .param p1, "this$0"    # Lcom/android/systemui/shared/system/ActivityManagerWrapper;

    .line 209
    iput-object p1, p0, Lcom/android/systemui/shared/system/ActivityManagerWrapper$1;->this$0:Lcom/android/systemui/shared/system/ActivityManagerWrapper;

    iput-object p2, p0, Lcom/android/systemui/shared/system/ActivityManagerWrapper$1;->val$assistDataReceiver:Lcom/android/systemui/shared/system/AssistDataReceiver;

    invoke-direct {p0}, Landroid/app/IAssistDataReceiver$Stub;-><init>()V

    return-void
.end method


# virtual methods
.method public onHandleAssistData(Landroid/os/Bundle;)V
    .registers 3
    .param p1, "resultData"    # Landroid/os/Bundle;

    .line 211
    iget-object v0, p0, Lcom/android/systemui/shared/system/ActivityManagerWrapper$1;->val$assistDataReceiver:Lcom/android/systemui/shared/system/AssistDataReceiver;

    invoke-virtual {v0, p1}, Lcom/android/systemui/shared/system/AssistDataReceiver;->onHandleAssistData(Landroid/os/Bundle;)V

    .line 212
    return-void
.end method

.method public onHandleAssistScreenshot(Landroid/graphics/Bitmap;)V
    .registers 3
    .param p1, "screenshot"    # Landroid/graphics/Bitmap;

    .line 214
    iget-object v0, p0, Lcom/android/systemui/shared/system/ActivityManagerWrapper$1;->val$assistDataReceiver:Lcom/android/systemui/shared/system/AssistDataReceiver;

    invoke-virtual {v0, p1}, Lcom/android/systemui/shared/system/AssistDataReceiver;->onHandleAssistScreenshot(Landroid/graphics/Bitmap;)V

    .line 215
    return-void
.end method
