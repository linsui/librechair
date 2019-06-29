.class public abstract Lcom/android/systemui/shared/system/AssistDataReceiver;
.super Ljava/lang/Object;
.source "AssistDataReceiver.java"


# direct methods
.method public constructor <init>()V
    .registers 1

    .line 25
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onHandleAssistData(Landroid/os/Bundle;)V
    .registers 2
    .param p1, "resultData"    # Landroid/os/Bundle;

    .line 26
    return-void
.end method

.method public onHandleAssistScreenshot(Landroid/graphics/Bitmap;)V
    .registers 2
    .param p1, "screenshot"    # Landroid/graphics/Bitmap;

    .line 27
    return-void
.end method
