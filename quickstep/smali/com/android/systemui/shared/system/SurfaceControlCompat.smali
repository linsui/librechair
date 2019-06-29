.class public Lcom/android/systemui/shared/system/SurfaceControlCompat;
.super Ljava/lang/Object;
.source "SurfaceControlCompat.java"


# instance fields
.field mSurfaceControl:Landroid/view/SurfaceControl;


# direct methods
.method public constructor <init>(Landroid/view/SurfaceControl;)V
    .registers 2
    .param p1, "surfaceControl"    # Landroid/view/SurfaceControl;

    .line 24
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 25
    iput-object p1, p0, Lcom/android/systemui/shared/system/SurfaceControlCompat;->mSurfaceControl:Landroid/view/SurfaceControl;

    .line 26
    return-void
.end method
