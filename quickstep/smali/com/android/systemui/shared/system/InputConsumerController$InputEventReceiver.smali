.class final Lcom/android/systemui/shared/system/InputConsumerController$InputEventReceiver;
.super Landroid/view/BatchedInputEventReceiver;
.source "InputConsumerController.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/android/systemui/shared/system/InputConsumerController;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x12
    name = "InputEventReceiver"
.end annotation


# instance fields
.field final synthetic this$0:Lcom/android/systemui/shared/system/InputConsumerController;


# direct methods
.method public constructor <init>(Lcom/android/systemui/shared/system/InputConsumerController;Landroid/view/InputChannel;Landroid/os/Looper;)V
    .registers 4
    .param p2, "inputChannel"    # Landroid/view/InputChannel;
    .param p3, "looper"    # Landroid/os/Looper;

    .line 65
    iput-object p1, p0, Lcom/android/systemui/shared/system/InputConsumerController$InputEventReceiver;->this$0:Lcom/android/systemui/shared/system/InputConsumerController;

    .line 66
    invoke-static {}, Landroid/view/Choreographer;->getSfInstance()Landroid/view/Choreographer;

    move-result-object p1

    invoke-direct {p0, p2, p3, p1}, Landroid/view/BatchedInputEventReceiver;-><init>(Landroid/view/InputChannel;Landroid/os/Looper;Landroid/view/Choreographer;)V

    .line 67
    return-void
.end method


# virtual methods
.method public onInputEvent(Landroid/view/InputEvent;I)V
    .registers 6
    .param p1, "event"    # Landroid/view/InputEvent;
    .param p2, "displayId"    # I

    .line 71
    const/4 v0, 0x1

    .line 73
    .local v0, "handled":Z
    :try_start_1
    iget-object v1, p0, Lcom/android/systemui/shared/system/InputConsumerController$InputEventReceiver;->this$0:Lcom/android/systemui/shared/system/InputConsumerController;

    # getter for: Lcom/android/systemui/shared/system/InputConsumerController;->mListener:Lcom/android/systemui/shared/system/InputConsumerController$TouchListener;
    invoke-static {v1}, Lcom/android/systemui/shared/system/InputConsumerController;->access$000(Lcom/android/systemui/shared/system/InputConsumerController;)Lcom/android/systemui/shared/system/InputConsumerController$TouchListener;

    move-result-object v1

    if-eqz v1, :cond_1b

    instance-of v1, p1, Landroid/view/MotionEvent;

    if-eqz v1, :cond_1b

    .line 74
    move-object v1, p1

    check-cast v1, Landroid/view/MotionEvent;

    .line 75
    .local v1, "ev":Landroid/view/MotionEvent;
    iget-object v2, p0, Lcom/android/systemui/shared/system/InputConsumerController$InputEventReceiver;->this$0:Lcom/android/systemui/shared/system/InputConsumerController;

    # getter for: Lcom/android/systemui/shared/system/InputConsumerController;->mListener:Lcom/android/systemui/shared/system/InputConsumerController$TouchListener;
    invoke-static {v2}, Lcom/android/systemui/shared/system/InputConsumerController;->access$000(Lcom/android/systemui/shared/system/InputConsumerController;)Lcom/android/systemui/shared/system/InputConsumerController$TouchListener;

    move-result-object v2

    invoke-interface {v2, v1}, Lcom/android/systemui/shared/system/InputConsumerController$TouchListener;->onTouchEvent(Landroid/view/MotionEvent;)Z

    move-result v2
    :try_end_1a
    .catchall {:try_start_1 .. :try_end_1a} :catchall_20

    move v0, v2

    .line 78
    .end local v1    # "ev":Landroid/view/MotionEvent;
    :cond_1b
    invoke-virtual {p0, p1, v0}, Lcom/android/systemui/shared/system/InputConsumerController$InputEventReceiver;->finishInputEvent(Landroid/view/InputEvent;Z)V

    .line 79
    nop

    .line 80
    return-void

    .line 78
    :catchall_20
    move-exception v1

    invoke-virtual {p0, p1, v0}, Lcom/android/systemui/shared/system/InputConsumerController$InputEventReceiver;->finishInputEvent(Landroid/view/InputEvent;Z)V

    throw v1
.end method
