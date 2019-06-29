.class Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier$1;
.super Ljava/lang/Object;
.source "SyncRtSurfaceTransactionApplier.java"

# interfaces
.implements Landroid/view/ThreadedRenderer$FrameDrawingCallback;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier;->scheduleApply([Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier$SurfaceParams;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier;

.field final synthetic val$params:[Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier$SurfaceParams;


# direct methods
.method constructor <init>(Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier;[Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier$SurfaceParams;)V
    .registers 3
    .param p1, "this$0"    # Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier;

    .line 55
    iput-object p1, p0, Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier$1;->this$0:Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier;

    iput-object p2, p0, Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier$1;->val$params:[Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier$SurfaceParams;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onFrameDraw(J)V
    .registers 8
    .param p1, "frame"    # J

    .line 57
    iget-object v0, p0, Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier$1;->this$0:Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier;

    # getter for: Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier;->mTargetSurface:Landroid/view/Surface;
    invoke-static {v0}, Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier;->access$000(Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier;)Landroid/view/Surface;

    move-result-object v0

    if-eqz v0, :cond_43

    iget-object v0, p0, Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier$1;->this$0:Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier;

    # getter for: Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier;->mTargetSurface:Landroid/view/Surface;
    invoke-static {v0}, Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier;->access$000(Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier;)Landroid/view/Surface;

    move-result-object v0

    invoke-virtual {v0}, Landroid/view/Surface;->isValid()Z

    move-result v0

    if-nez v0, :cond_15

    goto :goto_43

    .line 60
    :cond_15
    new-instance v0, Landroid/view/SurfaceControl$Transaction;

    invoke-direct {v0}, Landroid/view/SurfaceControl$Transaction;-><init>()V

    .line 61
    .local v0, "t":Landroid/view/SurfaceControl$Transaction;
    iget-object v1, p0, Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier$1;->val$params:[Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier$SurfaceParams;

    array-length v1, v1

    add-int/lit8 v1, v1, -0x1

    .line 61
    .local v1, "i":I
    :goto_1f
    if-ltz v1, :cond_3c

    .line 62
    iget-object v2, p0, Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier$1;->val$params:[Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier$SurfaceParams;

    aget-object v2, v2, v1

    .line 63
    .local v2, "surfaceParams":Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier$SurfaceParams;
    iget-object v3, v2, Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier$SurfaceParams;->surface:Landroid/view/SurfaceControl;

    .line 64
    .local v3, "surface":Landroid/view/SurfaceControl;
    iget-object v4, p0, Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier$1;->this$0:Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier;

    # getter for: Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier;->mTargetSurface:Landroid/view/Surface;
    invoke-static {v4}, Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier;->access$000(Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier;)Landroid/view/Surface;

    move-result-object v4

    invoke-virtual {v0, v3, v4, p1, p2}, Landroid/view/SurfaceControl$Transaction;->deferTransactionUntilSurface(Landroid/view/SurfaceControl;Landroid/view/Surface;J)Landroid/view/SurfaceControl$Transaction;

    .line 65
    iget-object v4, p0, Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier$1;->this$0:Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier;

    # getter for: Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier;->mTmpFloat9:[F
    invoke-static {v4}, Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier;->access$100(Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier;)[F

    move-result-object v4

    # invokes: Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier;->applyParams(Landroid/view/SurfaceControl$Transaction;Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier$SurfaceParams;[F)V
    invoke-static {v0, v2, v4}, Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier;->access$200(Landroid/view/SurfaceControl$Transaction;Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier$SurfaceParams;[F)V

    .line 61
    .end local v2    # "surfaceParams":Lcom/android/systemui/shared/system/SyncRtSurfaceTransactionApplier$SurfaceParams;
    .end local v3    # "surface":Landroid/view/SurfaceControl;
    add-int/lit8 v1, v1, -0x1

    goto :goto_1f

    .line 67
    .end local v1    # "i":I
    :cond_3c
    invoke-virtual {v0}, Landroid/view/SurfaceControl$Transaction;->setEarlyWakeup()Landroid/view/SurfaceControl$Transaction;

    .line 68
    invoke-virtual {v0}, Landroid/view/SurfaceControl$Transaction;->apply()V

    .line 69
    return-void

    .line 58
    .end local v0    # "t":Landroid/view/SurfaceControl$Transaction;
    :cond_43
    :goto_43
    return-void
.end method
