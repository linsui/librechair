.class public abstract Lcom/android/systemui/shared/system/ActivityOptionsCompat;
.super Ljava/lang/Object;
.source "ActivityOptionsCompat.java"


# direct methods
.method public constructor <init>()V
    .registers 1

    .line 28
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method public static makeRemoteAnimation(Lcom/android/systemui/shared/system/RemoteAnimationAdapterCompat;)Landroid/app/ActivityOptions;
    .registers 2
    .param p0, "remoteAnimationAdapter"    # Lcom/android/systemui/shared/system/RemoteAnimationAdapterCompat;

    .line 44
    invoke-virtual {p0}, Lcom/android/systemui/shared/system/RemoteAnimationAdapterCompat;->getWrapped()Landroid/view/RemoteAnimationAdapter;

    move-result-object v0

    invoke-static {v0}, Landroid/app/ActivityOptions;->makeRemoteAnimation(Landroid/view/RemoteAnimationAdapter;)Landroid/app/ActivityOptions;

    move-result-object v0

    return-object v0
.end method

.method public static makeSplitScreenOptions(Z)Landroid/app/ActivityOptions;
    .registers 3
    .param p0, "dockTopLeft"    # Z

    .line 34
    invoke-static {}, Landroid/app/ActivityOptions;->makeBasic()Landroid/app/ActivityOptions;

    move-result-object v0

    .line 35
    .local v0, "options":Landroid/app/ActivityOptions;
    const/4 v1, 0x3

    invoke-virtual {v0, v1}, Landroid/app/ActivityOptions;->setLaunchWindowingMode(I)V

    .line 36
    if-eqz p0, :cond_c

    .line 37
    const/4 v1, 0x0

    goto :goto_d

    .line 38
    :cond_c
    const/4 v1, 0x1

    .line 36
    :goto_d
    invoke-virtual {v0, v1}, Landroid/app/ActivityOptions;->setSplitScreenCreateMode(I)V

    .line 39
    return-object v0
.end method
