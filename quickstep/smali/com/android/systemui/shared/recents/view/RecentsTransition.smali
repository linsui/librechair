.class public Lcom/android/systemui/shared/recents/view/RecentsTransition;
.super Ljava/lang/Object;
.source "RecentsTransition.java"


# direct methods
.method public constructor <init>()V
    .registers 1

    .line 38
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method public static createAspectScaleAnimation(Landroid/content/Context;Landroid/os/Handler;ZLcom/android/systemui/shared/recents/view/AppTransitionAnimationSpecsFuture;Ljava/lang/Runnable;)Landroid/app/ActivityOptions;
    .registers 7
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "handler"    # Landroid/os/Handler;
    .param p2, "scaleUp"    # Z
    .param p3, "animationSpecsFuture"    # Lcom/android/systemui/shared/recents/view/AppTransitionAnimationSpecsFuture;
    .param p4, "animationStartCallback"    # Ljava/lang/Runnable;

    .line 46
    new-instance v0, Lcom/android/systemui/shared/recents/view/RecentsTransition$1;

    invoke-direct {v0, p4}, Lcom/android/systemui/shared/recents/view/RecentsTransition$1;-><init>(Ljava/lang/Runnable;)V

    .line 63
    .local v0, "animStartedListener":Landroid/app/ActivityOptions$OnAnimationStartedListener;
    nop

    .line 65
    if-eqz p3, :cond_d

    invoke-virtual {p3}, Lcom/android/systemui/shared/recents/view/AppTransitionAnimationSpecsFuture;->getFuture()Landroid/view/IAppTransitionAnimationSpecsFuture;

    move-result-object v1

    goto :goto_e

    :cond_d
    const/4 v1, 0x0

    .line 63
    :goto_e
    invoke-static {p0, p1, v1, v0, p2}, Landroid/app/ActivityOptions;->makeMultiThumbFutureAspectScaleAnimation(Landroid/content/Context;Landroid/os/Handler;Landroid/view/IAppTransitionAnimationSpecsFuture;Landroid/app/ActivityOptions$OnAnimationStartedListener;Z)Landroid/app/ActivityOptions;

    move-result-object v1

    .line 67
    .local v1, "opts":Landroid/app/ActivityOptions;
    return-object v1
.end method

.method public static createHardwareBitmap(IILjava/util/function/Consumer;)Landroid/graphics/Bitmap;
    .registers 6
    .param p0, "width"    # I
    .param p1, "height"    # I
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(II",
            "Ljava/util/function/Consumer<",
            "Landroid/graphics/Canvas;",
            ">;)",
            "Landroid/graphics/Bitmap;"
        }
    .end annotation

    .line 111
    .local p2, "consumer":Ljava/util/function/Consumer;, "Ljava/util/function/Consumer<Landroid/graphics/Canvas;>;"
    const-string v0, "RecentsTransition"

    const/4 v1, 0x0

    invoke-static {v0, v1}, Landroid/view/RenderNode;->create(Ljava/lang/String;Landroid/view/View;)Landroid/view/RenderNode;

    move-result-object v0

    .line 112
    .local v0, "node":Landroid/view/RenderNode;
    const/4 v1, 0x0

    invoke-virtual {v0, v1, v1, p0, p1}, Landroid/view/RenderNode;->setLeftTopRightBottom(IIII)Z

    .line 113
    invoke-virtual {v0, v1}, Landroid/view/RenderNode;->setClipToBounds(Z)Z

    .line 114
    invoke-virtual {v0, p0, p1}, Landroid/view/RenderNode;->start(II)Landroid/view/DisplayListCanvas;

    move-result-object v1

    .line 115
    .local v1, "c":Landroid/view/DisplayListCanvas;
    invoke-interface {p2, v1}, Ljava/util/function/Consumer;->accept(Ljava/lang/Object;)V

    .line 116
    invoke-virtual {v0, v1}, Landroid/view/RenderNode;->end(Landroid/view/DisplayListCanvas;)V

    .line 117
    invoke-static {v0, p0, p1}, Landroid/view/ThreadedRenderer;->createHardwareBitmap(Landroid/view/RenderNode;II)Landroid/graphics/Bitmap;

    move-result-object v2

    return-object v2
.end method

.method public static drawViewIntoHardwareBitmap(IILandroid/view/View;FI)Landroid/graphics/Bitmap;
    .registers 6
    .param p0, "width"    # I
    .param p1, "height"    # I
    .param p2, "view"    # Landroid/view/View;
    .param p3, "scale"    # F
    .param p4, "eraseColor"    # I

    .line 92
    new-instance v0, Lcom/android/systemui/shared/recents/view/RecentsTransition$3;

    invoke-direct {v0, p3, p4, p2}, Lcom/android/systemui/shared/recents/view/RecentsTransition$3;-><init>(FILandroid/view/View;)V

    invoke-static {p0, p1, v0}, Lcom/android/systemui/shared/recents/view/RecentsTransition;->createHardwareBitmap(IILjava/util/function/Consumer;)Landroid/graphics/Bitmap;

    move-result-object v0

    return-object v0
.end method

.method public static wrapStartedListener(Landroid/os/Handler;Ljava/lang/Runnable;)Landroid/os/IRemoteCallback;
    .registers 3
    .param p0, "handler"    # Landroid/os/Handler;
    .param p1, "animationStartCallback"    # Ljava/lang/Runnable;

    .line 75
    if-nez p1, :cond_4

    .line 76
    const/4 v0, 0x0

    return-object v0

    .line 78
    :cond_4
    new-instance v0, Lcom/android/systemui/shared/recents/view/RecentsTransition$2;

    invoke-direct {v0, p0, p1}, Lcom/android/systemui/shared/recents/view/RecentsTransition$2;-><init>(Landroid/os/Handler;Ljava/lang/Runnable;)V

    return-object v0
.end method
