.class public Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier$SurfaceParams;
.super Ljava/lang/Object;
.source "SyncRtSurfaceTransactionApplier.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x9
    name = "SurfaceParams"
.end annotation


# instance fields
.field final alpha:F

.field final layer:I

.field final matrix:Landroid/graphics/Matrix;

.field final surface:Landroid/view/SurfaceControl;

.field final windowCrop:Landroid/graphics/Rect;


# direct methods
.method public constructor <init>(Lcom/android/systemui/shared/system/SurfaceControlCompat;FLandroid/graphics/Matrix;Landroid/graphics/Rect;I)V
    .registers 7
    .param p1, "surface"    # Lcom/android/systemui/shared/system/SurfaceControlCompat;
    .param p2, "alpha"    # F
    .param p3, "matrix"    # Landroid/graphics/Matrix;
    .param p4, "windowCrop"    # Landroid/graphics/Rect;
    .param p5, "layer"    # I

    .line 100
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 101
    iget-object v0, p1, Lcom/android/systemui/shared/system/SurfaceControlCompat;->mSurfaceControl:Landroid/view/SurfaceControl;

    iput-object v0, p0, Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier$SurfaceParams;->surface:Landroid/view/SurfaceControl;

    .line 102
    iput p2, p0, Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier$SurfaceParams;->alpha:F

    .line 103
    new-instance v0, Landroid/graphics/Matrix;

    invoke-direct {v0, p3}, Landroid/graphics/Matrix;-><init>(Landroid/graphics/Matrix;)V

    iput-object v0, p0, Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier$SurfaceParams;->matrix:Landroid/graphics/Matrix;

    .line 104
    new-instance v0, Landroid/graphics/Rect;

    invoke-direct {v0, p4}, Landroid/graphics/Rect;-><init>(Landroid/graphics/Rect;)V

    iput-object v0, p0, Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier$SurfaceParams;->windowCrop:Landroid/graphics/Rect;

    .line 105
    iput p5, p0, Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier$SurfaceParams;->layer:I

    .line 106
    return-void
.end method
