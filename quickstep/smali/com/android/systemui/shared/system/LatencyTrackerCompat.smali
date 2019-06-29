.class public Lcom/android/systemui/shared/system/LatencyTrackerCompat;
.super Ljava/lang/Object;
.source "LatencyTrackerCompat.java"


# direct methods
.method public constructor <init>()V
    .registers 1

    .line 26
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method public static isEnabled(Landroid/content/Context;)Z
    .registers 2
    .param p0, "context"    # Landroid/content/Context;

    .line 28
    invoke-static {p0}, Lcom/android/internal/util/LatencyTracker;->isEnabled(Landroid/content/Context;)Z

    move-result v0

    return v0
.end method

.method public static logToggleRecents(I)V
    .registers 2
    .param p0, "duration"    # I

    .line 32
    const/4 v0, 0x1

    invoke-static {v0, p0}, Lcom/android/internal/util/LatencyTracker;->logAction(II)V

    .line 33
    return-void
.end method
