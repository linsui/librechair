.class public Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier;
.super Ljava/lang/Object;
.source "SyncRtSurfaceTransactionApplier.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier$SurfaceParams;
    }
.end annotation


# instance fields
.field private final mTargetSurface:Landroid/view/Surface;

.field private final mTargetViewRootImpl:Landroid/view/ViewRootImpl;

.field private final mTmpFloat9:[F


# direct methods
.method public constructor <init>(Landroid/view/View;)V
    .registers 4
    .param p1, "targetView"    # Landroid/view/View;

    .line 40
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 35
    const/16 v0, 0x9

    new-array v0, v0, [F

    iput-object v0, p0, Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier;->mTmpFloat9:[F

    .line 41
    const/4 v0, 0x0

    if-eqz p1, :cond_11

    invoke-virtual {p1}, Landroid/view/View;->getViewRootImpl()Landroid/view/ViewRootImpl;

    move-result-object v1

    goto :goto_12

    :cond_11
    move-object v1, v0

    :goto_12
    iput-object v1, p0, Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier;->mTargetViewRootImpl:Landroid/view/ViewRootImpl;

    .line 42
    iget-object v1, p0, Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier;->mTargetViewRootImpl:Landroid/view/ViewRootImpl;

    if-eqz v1, :cond_1d

    iget-object v0, p0, Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier;->mTargetViewRootImpl:Landroid/view/ViewRootImpl;

    iget-object v0, v0, Landroid/view/ViewRootImpl;->mSurface:Landroid/view/Surface;

    nop

    :cond_1d
    iput-object v0, p0, Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier;->mTargetSurface:Landroid/view/Surface;

    .line 43
    return-void
.end method

.method static synthetic access$000(Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier;)Landroid/view/Surface;
    .registers 2
    .param p0, "x0"    # Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier;

    .line 31
    iget-object v0, p0, Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier;->mTargetSurface:Landroid/view/Surface;

    return-object v0
.end method

.method static synthetic access$100(Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier;)[F
    .registers 2
    .param p0, "x0"    # Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier;

    .line 31
    iget-object v0, p0, Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier;->mTmpFloat9:[F

    return-object v0
.end method

.method static synthetic access$200(Landroid/view/SurfaceControl$Transaction;Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier$SurfaceParams;[F)V
    .registers 3
    .param p0, "x0"    # Landroid/view/SurfaceControl$Transaction;
    .param p1, "x1"    # Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier$SurfaceParams;
    .param p2, "x2"    # [F

    .line 31
    invoke-static {p0, p1, p2}, Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier;->applyParams(Landroid/view/SurfaceControl$Transaction;Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier$SurfaceParams;[F)V

    return-void
.end method

.method private static applyParams(Landroid/view/SurfaceControl$Transaction;Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier$SurfaceParams;[F)V
    .registers 5
    .param p0, "t"    # Landroid/view/SurfaceControl$Transaction;
    .param p1, "params"    # Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier$SurfaceParams;
    .param p2, "tmpFloat9"    # [F

    .line 81
    iget-object v0, p1, Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier$SurfaceParams;->surface:Landroid/view/SurfaceControl;

    iget-object v1, p1, Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier$SurfaceParams;->matrix:Landroid/graphics/Matrix;

    invoke-virtual {p0, v0, v1, p2}, Landroid/view/SurfaceControl$Transaction;->setMatrix(Landroid/view/SurfaceControl;Landroid/graphics/Matrix;[F)Landroid/view/SurfaceControl$Transaction;

    .line 82
    iget-object v0, p1, Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier$SurfaceParams;->surface:Landroid/view/SurfaceControl;

    iget-object v1, p1, Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier$SurfaceParams;->windowCrop:Landroid/graphics/Rect;

    invoke-virtual {p0, v0, v1}, Landroid/view/SurfaceControl$Transaction;->setWindowCrop(Landroid/view/SurfaceControl;Landroid/graphics/Rect;)Landroid/view/SurfaceControl$Transaction;

    .line 83
    iget-object v0, p1, Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier$SurfaceParams;->surface:Landroid/view/SurfaceControl;

    iget v1, p1, Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier$SurfaceParams;->alpha:F

    invoke-virtual {p0, v0, v1}, Landroid/view/SurfaceControl$Transaction;->setAlpha(Landroid/view/SurfaceControl;F)Landroid/view/SurfaceControl$Transaction;

    .line 84
    iget-object v0, p1, Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier$SurfaceParams;->surface:Landroid/view/SurfaceControl;

    iget v1, p1, Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier$SurfaceParams;->layer:I

    invoke-virtual {p0, v0, v1}, Landroid/view/SurfaceControl$Transaction;->setLayer(Landroid/view/SurfaceControl;I)Landroid/view/SurfaceControl$Transaction;

    .line 85
    iget-object v0, p1, Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier$SurfaceParams;->surface:Landroid/view/SurfaceControl;

    invoke-virtual {p0, v0}, Landroid/view/SurfaceControl$Transaction;->show(Landroid/view/SurfaceControl;)Landroid/view/SurfaceControl$Transaction;

    .line 86
    return-void
.end method

.method public static applyParams(Lcom/android/systemui/shared/system/TransactionCompat;Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier$SurfaceParams;)V
    .registers 4
    .param p0, "t"    # Lcom/android/systemui/shared/system/TransactionCompat;
    .param p1, "params"    # Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier$SurfaceParams;

    .line 77
    iget-object v0, p0, Lcom/android/systemui/shared/system/TransactionCompat;->mTransaction:Landroid/view/SurfaceControl$Transaction;

    iget-object v1, p0, Lcom/android/systemui/shared/system/TransactionCompat;->mTmpValues:[F

    invoke-static {v0, p1, v1}, Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier;->applyParams(Landroid/view/SurfaceControl$Transaction;Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier$SurfaceParams;[F)V

    .line 78
    return-void
.end method


# virtual methods
.method public varargs scheduleApply([Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier$SurfaceParams;)V
    .registers 4
    .param p1, "params"    # [Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier$SurfaceParams;

    .line 52
    iget-object v0, p0, Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier;->mTargetViewRootImpl:Landroid/view/ViewRootImpl;

    if-nez v0, :cond_5

    .line 53
    return-void

    .line 55
    :cond_5
    iget-object v0, p0, Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier;->mTargetViewRootImpl:Landroid/view/ViewRootImpl;

    new-instance v1, Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier$1;

    invoke-direct {v1, p0, p1}, Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier$1;-><init>(Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier;[Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier$SurfaceParams;)V

    invoke-virtual {v0, v1}, Landroid/view/ViewRootImpl;->registerRtFrameCallback(Landroid/view/ThreadedRenderer$FrameDrawingCallback;)V

    .line 73
    iget-object v0, p0, Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier;->mTargetViewRootImpl:Landroid/view/ViewRootImpl;

    invoke-virtual {v0}, Landroid/view/ViewRootImpl;->getView()Landroid/view/View;

    move-result-object v0

    invoke-virtual {v0}, Landroid/view/View;->invalidate()V

    .line 74
    return-void
.end method
