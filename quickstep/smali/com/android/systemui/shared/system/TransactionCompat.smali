.class public Lcom/android/systemui/shared/system/TransactionCompat;
.super Ljava/lang/Object;
.source "TransactionCompat.java"


# instance fields
.field final mTmpValues:[F

.field final mTransaction:Landroid/view/SurfaceControl$Transaction;


# direct methods
.method public constructor <init>()V
    .registers 2

    .line 32
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 30
    const/16 v0, 0x9

    new-array v0, v0, [F

    iput-object v0, p0, Lcom/android/systemui/shared/system/TransactionCompat;->mTmpValues:[F

    .line 33
    new-instance v0, Landroid/view/SurfaceControl$Transaction;

    invoke-direct {v0}, Landroid/view/SurfaceControl$Transaction;-><init>()V

    iput-object v0, p0, Lcom/android/systemui/shared/system/TransactionCompat;->mTransaction:Landroid/view/SurfaceControl$Transaction;

    .line 34
    return-void
.end method


# virtual methods
.method public apply()V
    .registers 2

    .line 37
    iget-object v0, p0, Lcom/android/systemui/shared/system/TransactionCompat;->mTransaction:Landroid/view/SurfaceControl$Transaction;

    invoke-virtual {v0}, Landroid/view/SurfaceControl$Transaction;->apply()V

    .line 38
    return-void
.end method

.method public deferTransactionUntil(Lcom/android/systemui/shared/system/SurfaceControlCompat;Landroid/os/IBinder;J)Lcom/android/systemui/shared/system/TransactionCompat;
    .registers 7
    .param p1, "surfaceControl"    # Lcom/android/systemui/shared/system/SurfaceControlCompat;
    .param p2, "handle"    # Landroid/os/IBinder;
    .param p3, "frameNumber"    # J

    .line 93
    iget-object v0, p0, Lcom/android/systemui/shared/system/TransactionCompat;->mTransaction:Landroid/view/SurfaceControl$Transaction;

    iget-object v1, p1, Lcom/android/systemui/shared/system/SurfaceControlCompat;->mSurfaceControl:Landroid/view/SurfaceControl;

    invoke-virtual {v0, v1, p2, p3, p4}, Landroid/view/SurfaceControl$Transaction;->deferTransactionUntil(Landroid/view/SurfaceControl;Landroid/os/IBinder;J)Landroid/view/SurfaceControl$Transaction;

    .line 94
    return-object p0
.end method

.method public deferTransactionUntil(Lcom/android/systemui/shared/system/SurfaceControlCompat;Landroid/view/Surface;J)Lcom/android/systemui/shared/system/TransactionCompat;
    .registers 7
    .param p1, "surfaceControl"    # Lcom/android/systemui/shared/system/SurfaceControlCompat;
    .param p2, "barrier"    # Landroid/view/Surface;
    .param p3, "frameNumber"    # J

    .line 99
    iget-object v0, p0, Lcom/android/systemui/shared/system/TransactionCompat;->mTransaction:Landroid/view/SurfaceControl$Transaction;

    iget-object v1, p1, Lcom/android/systemui/shared/system/SurfaceControlCompat;->mSurfaceControl:Landroid/view/SurfaceControl;

    invoke-virtual {v0, v1, p2, p3, p4}, Landroid/view/SurfaceControl$Transaction;->deferTransactionUntilSurface(Landroid/view/SurfaceControl;Landroid/view/Surface;J)Landroid/view/SurfaceControl$Transaction;

    .line 101
    return-object p0
.end method

.method public hide(Lcom/android/systemui/shared/system/SurfaceControlCompat;)Lcom/android/systemui/shared/system/TransactionCompat;
    .registers 4
    .param p1, "surfaceControl"    # Lcom/android/systemui/shared/system/SurfaceControlCompat;

    .line 46
    iget-object v0, p0, Lcom/android/systemui/shared/system/TransactionCompat;->mTransaction:Landroid/view/SurfaceControl$Transaction;

    iget-object v1, p1, Lcom/android/systemui/shared/system/SurfaceControlCompat;->mSurfaceControl:Landroid/view/SurfaceControl;

    invoke-virtual {v0, v1}, Landroid/view/SurfaceControl$Transaction;->hide(Landroid/view/SurfaceControl;)Landroid/view/SurfaceControl$Transaction;

    .line 47
    return-object p0
.end method

.method public setAlpha(Lcom/android/systemui/shared/system/SurfaceControlCompat;F)Lcom/android/systemui/shared/system/TransactionCompat;
    .registers 5
    .param p1, "surfaceControl"    # Lcom/android/systemui/shared/system/SurfaceControlCompat;
    .param p2, "alpha"    # F

    .line 66
    iget-object v0, p0, Lcom/android/systemui/shared/system/TransactionCompat;->mTransaction:Landroid/view/SurfaceControl$Transaction;

    iget-object v1, p1, Lcom/android/systemui/shared/system/SurfaceControlCompat;->mSurfaceControl:Landroid/view/SurfaceControl;

    invoke-virtual {v0, v1, p2}, Landroid/view/SurfaceControl$Transaction;->setAlpha(Landroid/view/SurfaceControl;F)Landroid/view/SurfaceControl$Transaction;

    .line 67
    return-object p0
.end method

.method public setColor(Lcom/android/systemui/shared/system/SurfaceControlCompat;[F)Lcom/android/systemui/shared/system/TransactionCompat;
    .registers 5
    .param p1, "surfaceControl"    # Lcom/android/systemui/shared/system/SurfaceControlCompat;
    .param p2, "color"    # [F

    .line 110
    iget-object v0, p0, Lcom/android/systemui/shared/system/TransactionCompat;->mTransaction:Landroid/view/SurfaceControl$Transaction;

    iget-object v1, p1, Lcom/android/systemui/shared/system/SurfaceControlCompat;->mSurfaceControl:Landroid/view/SurfaceControl;

    invoke-virtual {v0, v1, p2}, Landroid/view/SurfaceControl$Transaction;->setColor(Landroid/view/SurfaceControl;[F)Landroid/view/SurfaceControl$Transaction;

    .line 111
    return-object p0
.end method

.method public setEarlyWakeup()Lcom/android/systemui/shared/system/TransactionCompat;
    .registers 2

    .line 105
    iget-object v0, p0, Lcom/android/systemui/shared/system/TransactionCompat;->mTransaction:Landroid/view/SurfaceControl$Transaction;

    invoke-virtual {v0}, Landroid/view/SurfaceControl$Transaction;->setEarlyWakeup()Landroid/view/SurfaceControl$Transaction;

    .line 106
    return-object p0
.end method

.method public setFinalCrop(Lcom/android/systemui/shared/system/SurfaceControlCompat;Landroid/graphics/Rect;)Lcom/android/systemui/shared/system/TransactionCompat;
    .registers 5
    .param p1, "surfaceControl"    # Lcom/android/systemui/shared/system/SurfaceControlCompat;
    .param p2, "crop"    # Landroid/graphics/Rect;

    .line 87
    iget-object v0, p0, Lcom/android/systemui/shared/system/TransactionCompat;->mTransaction:Landroid/view/SurfaceControl$Transaction;

    iget-object v1, p1, Lcom/android/systemui/shared/system/SurfaceControlCompat;->mSurfaceControl:Landroid/view/SurfaceControl;

    invoke-virtual {v0, v1, p2}, Landroid/view/SurfaceControl$Transaction;->setFinalCrop(Landroid/view/SurfaceControl;Landroid/graphics/Rect;)Landroid/view/SurfaceControl$Transaction;

    .line 88
    return-object p0
.end method

.method public setLayer(Lcom/android/systemui/shared/system/SurfaceControlCompat;I)Lcom/android/systemui/shared/system/TransactionCompat;
    .registers 5
    .param p1, "surfaceControl"    # Lcom/android/systemui/shared/system/SurfaceControlCompat;
    .param p2, "z"    # I

    .line 61
    iget-object v0, p0, Lcom/android/systemui/shared/system/TransactionCompat;->mTransaction:Landroid/view/SurfaceControl$Transaction;

    iget-object v1, p1, Lcom/android/systemui/shared/system/SurfaceControlCompat;->mSurfaceControl:Landroid/view/SurfaceControl;

    invoke-virtual {v0, v1, p2}, Landroid/view/SurfaceControl$Transaction;->setLayer(Landroid/view/SurfaceControl;I)Landroid/view/SurfaceControl$Transaction;

    .line 62
    return-object p0
.end method

.method public setMatrix(Lcom/android/systemui/shared/system/SurfaceControlCompat;FFFF)Lcom/android/systemui/shared/system/TransactionCompat;
    .registers 12
    .param p1, "surfaceControl"    # Lcom/android/systemui/shared/system/SurfaceControlCompat;
    .param p2, "dsdx"    # F
    .param p3, "dtdx"    # F
    .param p4, "dtdy"    # F
    .param p5, "dsdy"    # F

    .line 72
    iget-object v0, p0, Lcom/android/systemui/shared/system/TransactionCompat;->mTransaction:Landroid/view/SurfaceControl$Transaction;

    iget-object v1, p1, Lcom/android/systemui/shared/system/SurfaceControlCompat;->mSurfaceControl:Landroid/view/SurfaceControl;

    move v2, p2

    move v3, p3

    move v4, p4

    move v5, p5

    invoke-virtual/range {v0 .. v5}, Landroid/view/SurfaceControl$Transaction;->setMatrix(Landroid/view/SurfaceControl;FFFF)Landroid/view/SurfaceControl$Transaction;

    .line 73
    return-object p0
.end method

.method public setMatrix(Lcom/android/systemui/shared/system/SurfaceControlCompat;Landroid/graphics/Matrix;)Lcom/android/systemui/shared/system/TransactionCompat;
    .registers 6
    .param p1, "surfaceControl"    # Lcom/android/systemui/shared/system/SurfaceControlCompat;
    .param p2, "matrix"    # Landroid/graphics/Matrix;

    .line 77
    iget-object v0, p0, Lcom/android/systemui/shared/system/TransactionCompat;->mTransaction:Landroid/view/SurfaceControl$Transaction;

    iget-object v1, p1, Lcom/android/systemui/shared/system/SurfaceControlCompat;->mSurfaceControl:Landroid/view/SurfaceControl;

    iget-object v2, p0, Lcom/android/systemui/shared/system/TransactionCompat;->mTmpValues:[F

    invoke-virtual {v0, v1, p2, v2}, Landroid/view/SurfaceControl$Transaction;->setMatrix(Landroid/view/SurfaceControl;Landroid/graphics/Matrix;[F)Landroid/view/SurfaceControl$Transaction;

    .line 78
    return-object p0
.end method

.method public setPosition(Lcom/android/systemui/shared/system/SurfaceControlCompat;FF)Lcom/android/systemui/shared/system/TransactionCompat;
    .registers 6
    .param p1, "surfaceControl"    # Lcom/android/systemui/shared/system/SurfaceControlCompat;
    .param p2, "x"    # F
    .param p3, "y"    # F

    .line 51
    iget-object v0, p0, Lcom/android/systemui/shared/system/TransactionCompat;->mTransaction:Landroid/view/SurfaceControl$Transaction;

    iget-object v1, p1, Lcom/android/systemui/shared/system/SurfaceControlCompat;->mSurfaceControl:Landroid/view/SurfaceControl;

    invoke-virtual {v0, v1, p2, p3}, Landroid/view/SurfaceControl$Transaction;->setPosition(Landroid/view/SurfaceControl;FF)Landroid/view/SurfaceControl$Transaction;

    .line 52
    return-object p0
.end method

.method public setSize(Lcom/android/systemui/shared/system/SurfaceControlCompat;II)Lcom/android/systemui/shared/system/TransactionCompat;
    .registers 6
    .param p1, "surfaceControl"    # Lcom/android/systemui/shared/system/SurfaceControlCompat;
    .param p2, "w"    # I
    .param p3, "h"    # I

    .line 56
    iget-object v0, p0, Lcom/android/systemui/shared/system/TransactionCompat;->mTransaction:Landroid/view/SurfaceControl$Transaction;

    iget-object v1, p1, Lcom/android/systemui/shared/system/SurfaceControlCompat;->mSurfaceControl:Landroid/view/SurfaceControl;

    invoke-virtual {v0, v1, p2, p3}, Landroid/view/SurfaceControl$Transaction;->setSize(Landroid/view/SurfaceControl;II)Landroid/view/SurfaceControl$Transaction;

    .line 57
    return-object p0
.end method

.method public setWindowCrop(Lcom/android/systemui/shared/system/SurfaceControlCompat;Landroid/graphics/Rect;)Lcom/android/systemui/shared/system/TransactionCompat;
    .registers 5
    .param p1, "surfaceControl"    # Lcom/android/systemui/shared/system/SurfaceControlCompat;
    .param p2, "crop"    # Landroid/graphics/Rect;

    .line 82
    iget-object v0, p0, Lcom/android/systemui/shared/system/TransactionCompat;->mTransaction:Landroid/view/SurfaceControl$Transaction;

    iget-object v1, p1, Lcom/android/systemui/shared/system/SurfaceControlCompat;->mSurfaceControl:Landroid/view/SurfaceControl;

    invoke-virtual {v0, v1, p2}, Landroid/view/SurfaceControl$Transaction;->setWindowCrop(Landroid/view/SurfaceControl;Landroid/graphics/Rect;)Landroid/view/SurfaceControl$Transaction;

    .line 83
    return-object p0
.end method

.method public show(Lcom/android/systemui/shared/system/SurfaceControlCompat;)Lcom/android/systemui/shared/system/TransactionCompat;
    .registers 4
    .param p1, "surfaceControl"    # Lcom/android/systemui/shared/system/SurfaceControlCompat;

    .line 41
    iget-object v0, p0, Lcom/android/systemui/shared/system/TransactionCompat;->mTransaction:Landroid/view/SurfaceControl$Transaction;

    iget-object v1, p1, Lcom/android/systemui/shared/system/SurfaceControlCompat;->mSurfaceControl:Landroid/view/SurfaceControl;

    invoke-virtual {v0, v1}, Landroid/view/SurfaceControl$Transaction;->show(Landroid/view/SurfaceControl;)Landroid/view/SurfaceControl$Transaction;

    .line 42
    return-object p0
.end method
