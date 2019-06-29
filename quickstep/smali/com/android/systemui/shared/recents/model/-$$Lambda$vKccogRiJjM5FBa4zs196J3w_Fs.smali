.class public final synthetic Lcom/android/systemui/shared/recents/model/-$$Lambda$vKccogRiJjM5FBa4zs196J3w_Fs;
.super Ljava/lang/Object;
.source "lambda"

# interfaces
.implements Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader$OnIdleChangedListener;


# instance fields
.field private final synthetic f$0:Lcom/android/systemui/shared/recents/model/HighResThumbnailLoader;


# direct methods
.method public synthetic constructor <init>(Lcom/android/systemui/shared/recents/model/HighResThumbnailLoader;)V
    .registers 2

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    iput-object p1, p0, Lcom/android/systemui/shared/recents/model/-$$Lambda$vKccogRiJjM5FBa4zs196J3w_Fs;->f$0:Lcom/android/systemui/shared/recents/model/HighResThumbnailLoader;

    return-void
.end method


# virtual methods
.method public final onIdleChanged(Z)V
    .registers 3

    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/-$$Lambda$vKccogRiJjM5FBa4zs196J3w_Fs;->f$0:Lcom/android/systemui/shared/recents/model/HighResThumbnailLoader;

    invoke-virtual {v0, p1}, Lcom/android/systemui/shared/recents/model/HighResThumbnailLoader;->setTaskLoadQueueIdle(Z)V

    return-void
.end method
