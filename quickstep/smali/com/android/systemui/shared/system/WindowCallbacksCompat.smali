.class public Lcom/android/systemui/shared/system/WindowCallbacksCompat;
.super Ljava/lang/Object;
.source "WindowCallbacksCompat.java"


# instance fields
.field private final mView:Landroid/view/View;

.field private final mWindowCallbacks:Landroid/view/WindowCallbacks;


# direct methods
.method public constructor <init>(Landroid/view/View;)V
    .registers 3
    .param p1, "view"    # Landroid/view/View;

    .line 65
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 27
    new-instance v0, Lcom/android/systemui/shared/system/WindowCallbacksCompat$1;

    invoke-direct {v0, p0}, Lcom/android/systemui/shared/system/WindowCallbacksCompat$1;-><init>(Lcom/android/systemui/shared/system/WindowCallbacksCompat;)V

    iput-object v0, p0, Lcom/android/systemui/shared/system/WindowCallbacksCompat;->mWindowCallbacks:Landroid/view/WindowCallbacks;

    .line 66
    iput-object p1, p0, Lcom/android/systemui/shared/system/WindowCallbacksCompat;->mView:Landroid/view/View;

    .line 67
    return-void
.end method


# virtual methods
.method public attach()Z
    .registers 3

    .line 94
    iget-object v0, p0, Lcom/android/systemui/shared/system/WindowCallbacksCompat;->mView:Landroid/view/View;

    invoke-virtual {v0}, Landroid/view/View;->getViewRootImpl()Landroid/view/ViewRootImpl;

    move-result-object v0

    .line 95
    .local v0, "root":Landroid/view/ViewRootImpl;
    if-eqz v0, :cond_12

    .line 96
    iget-object v1, p0, Lcom/android/systemui/shared/system/WindowCallbacksCompat;->mWindowCallbacks:Landroid/view/WindowCallbacks;

    invoke-virtual {v0, v1}, Landroid/view/ViewRootImpl;->addWindowCallbacks(Landroid/view/WindowCallbacks;)V

    .line 97
    invoke-virtual {v0}, Landroid/view/ViewRootImpl;->requestInvalidateRootRenderNode()V

    .line 98
    const/4 v1, 0x1

    return v1

    .line 100
    :cond_12
    const/4 v1, 0x0

    return v1
.end method

.method public detach()V
    .registers 3

    .line 104
    iget-object v0, p0, Lcom/android/systemui/shared/system/WindowCallbacksCompat;->mView:Landroid/view/View;

    invoke-virtual {v0}, Landroid/view/View;->getViewRootImpl()Landroid/view/ViewRootImpl;

    move-result-object v0

    .line 105
    .local v0, "root":Landroid/view/ViewRootImpl;
    if-eqz v0, :cond_d

    .line 106
    iget-object v1, p0, Lcom/android/systemui/shared/system/WindowCallbacksCompat;->mWindowCallbacks:Landroid/view/WindowCallbacks;

    invoke-virtual {v0, v1}, Landroid/view/ViewRootImpl;->removeWindowCallbacks(Landroid/view/WindowCallbacks;)V

    .line 108
    :cond_d
    return-void
.end method

.method public onContentDrawn(IIII)Z
    .registers 6
    .param p1, "offsetX"    # I
    .param p2, "offsetY"    # I
    .param p3, "sizeX"    # I
    .param p4, "sizeY"    # I

    .line 78
    const/4 v0, 0x0

    return v0
.end method

.method public onPostDraw(Landroid/graphics/Canvas;)V
    .registers 2
    .param p1, "canvas"    # Landroid/graphics/Canvas;

    .line 87
    return-void
.end method

.method public onRequestDraw(Z)V
    .registers 2
    .param p1, "reportNextDraw"    # Z

    .line 82
    if-eqz p1, :cond_5

    .line 83
    invoke-virtual {p0}, Lcom/android/systemui/shared/system/WindowCallbacksCompat;->reportDrawFinish()V

    .line 85
    :cond_5
    return-void
.end method

.method public onWindowDragResizeEnd()V
    .registers 1

    .line 75
    return-void
.end method

.method public onWindowDragResizeStart(Landroid/graphics/Rect;ZLandroid/graphics/Rect;Landroid/graphics/Rect;I)V
    .registers 6
    .param p1, "initialBounds"    # Landroid/graphics/Rect;
    .param p2, "fullscreen"    # Z
    .param p3, "systemInsets"    # Landroid/graphics/Rect;
    .param p4, "stableInsets"    # Landroid/graphics/Rect;
    .param p5, "resizeMode"    # I

    .line 73
    return-void
.end method

.method public onWindowSizeIsChanging(Landroid/graphics/Rect;ZLandroid/graphics/Rect;Landroid/graphics/Rect;)V
    .registers 5
    .param p1, "newBounds"    # Landroid/graphics/Rect;
    .param p2, "fullscreen"    # Z
    .param p3, "systemInsets"    # Landroid/graphics/Rect;
    .param p4, "stableInsets"    # Landroid/graphics/Rect;

    .line 70
    return-void
.end method

.method public reportDrawFinish()V
    .registers 2

    .line 90
    iget-object v0, p0, Lcom/android/systemui/shared/system/WindowCallbacksCompat;->mView:Landroid/view/View;

    invoke-virtual {v0}, Landroid/view/View;->getViewRootImpl()Landroid/view/ViewRootImpl;

    move-result-object v0

    invoke-virtual {v0}, Landroid/view/ViewRootImpl;->reportDrawFinish()V

    .line 91
    return-void
.end method
