.class Lcom/android/systemui/shared/system/WindowCallbacksCompat$1;
.super Ljava/lang/Object;
.source "WindowCallbacksCompat.java"

# interfaces
.implements Landroid/view/WindowCallbacks;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/android/systemui/shared/system/WindowCallbacksCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/android/systemui/shared/system/WindowCallbacksCompat;


# direct methods
.method constructor <init>(Lcom/android/systemui/shared/system/WindowCallbacksCompat;)V
    .registers 2
    .param p1, "this$0"    # Lcom/android/systemui/shared/system/WindowCallbacksCompat;

    .line 27
    iput-object p1, p0, Lcom/android/systemui/shared/system/WindowCallbacksCompat$1;->this$0:Lcom/android/systemui/shared/system/WindowCallbacksCompat;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onContentDrawn(IIII)Z
    .registers 6
    .param p1, "offsetX"    # I
    .param p2, "offsetY"    # I
    .param p3, "sizeX"    # I
    .param p4, "sizeY"    # I

    .line 49
    iget-object v0, p0, Lcom/android/systemui/shared/system/WindowCallbacksCompat$1;->this$0:Lcom/android/systemui/shared/system/WindowCallbacksCompat;

    invoke-virtual {v0, p1, p2, p3, p4}, Lcom/android/systemui/shared/system/WindowCallbacksCompat;->onContentDrawn(IIII)Z

    move-result v0

    return v0
.end method

.method public onPostDraw(Landroid/view/DisplayListCanvas;)V
    .registers 3
    .param p1, "canvas"    # Landroid/view/DisplayListCanvas;

    .line 59
    iget-object v0, p0, Lcom/android/systemui/shared/system/WindowCallbacksCompat$1;->this$0:Lcom/android/systemui/shared/system/WindowCallbacksCompat;

    invoke-virtual {v0, p1}, Lcom/android/systemui/shared/system/WindowCallbacksCompat;->onPostDraw(Landroid/graphics/Canvas;)V

    .line 60
    return-void
.end method

.method public onRequestDraw(Z)V
    .registers 3
    .param p1, "reportNextDraw"    # Z

    .line 54
    iget-object v0, p0, Lcom/android/systemui/shared/system/WindowCallbacksCompat$1;->this$0:Lcom/android/systemui/shared/system/WindowCallbacksCompat;

    invoke-virtual {v0, p1}, Lcom/android/systemui/shared/system/WindowCallbacksCompat;->onRequestDraw(Z)V

    .line 55
    return-void
.end method

.method public onWindowDragResizeEnd()V
    .registers 2

    .line 44
    iget-object v0, p0, Lcom/android/systemui/shared/system/WindowCallbacksCompat$1;->this$0:Lcom/android/systemui/shared/system/WindowCallbacksCompat;

    invoke-virtual {v0}, Lcom/android/systemui/shared/system/WindowCallbacksCompat;->onWindowDragResizeEnd()V

    .line 45
    return-void
.end method

.method public onWindowDragResizeStart(Landroid/graphics/Rect;ZLandroid/graphics/Rect;Landroid/graphics/Rect;I)V
    .registers 12
    .param p1, "initialBounds"    # Landroid/graphics/Rect;
    .param p2, "fullscreen"    # Z
    .param p3, "systemInsets"    # Landroid/graphics/Rect;
    .param p4, "stableInsets"    # Landroid/graphics/Rect;
    .param p5, "resizeMode"    # I

    .line 38
    iget-object v0, p0, Lcom/android/systemui/shared/system/WindowCallbacksCompat$1;->this$0:Lcom/android/systemui/shared/system/WindowCallbacksCompat;

    move-object v1, p1

    move v2, p2

    move-object v3, p3

    move-object v4, p4

    move v5, p5

    invoke-virtual/range {v0 .. v5}, Lcom/android/systemui/shared/system/WindowCallbacksCompat;->onWindowDragResizeStart(Landroid/graphics/Rect;ZLandroid/graphics/Rect;Landroid/graphics/Rect;I)V

    .line 40
    return-void
.end method

.method public onWindowSizeIsChanging(Landroid/graphics/Rect;ZLandroid/graphics/Rect;Landroid/graphics/Rect;)V
    .registers 6
    .param p1, "newBounds"    # Landroid/graphics/Rect;
    .param p2, "fullscreen"    # Z
    .param p3, "systemInsets"    # Landroid/graphics/Rect;
    .param p4, "stableInsets"    # Landroid/graphics/Rect;

    .line 31
    iget-object v0, p0, Lcom/android/systemui/shared/system/WindowCallbacksCompat$1;->this$0:Lcom/android/systemui/shared/system/WindowCallbacksCompat;

    invoke-virtual {v0, p1, p2, p3, p4}, Lcom/android/systemui/shared/system/WindowCallbacksCompat;->onWindowSizeIsChanging(Landroid/graphics/Rect;ZLandroid/graphics/Rect;Landroid/graphics/Rect;)V

    .line 33
    return-void
.end method
