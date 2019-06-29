.class public final synthetic Lcom/android/systemui/shared/recents/model/-$$Lambda$HighResThumbnailLoader$1$s-1KK0EnA0WJuK_oehEz11H5MbU;
.super Ljava/lang/Object;
.source "lambda"

# interfaces
.implements Ljava/lang/Runnable;


# instance fields
.field private final synthetic f$0:Lcom/android/systemui/shared/recents/model/HighResThumbnailLoader$1;

.field private final synthetic f$1:Lcom/android/systemui/shared/recents/model/Task;

.field private final synthetic f$2:Lcom/android/systemui/shared/recents/model/ThumbnailData;


# direct methods
.method public synthetic constructor <init>(Lcom/android/systemui/shared/recents/model/HighResThumbnailLoader$1;Lcom/android/systemui/shared/recents/model/Task;Lcom/android/systemui/shared/recents/model/ThumbnailData;)V
    .registers 4

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    iput-object p1, p0, Lcom/android/systemui/shared/recents/model/-$$Lambda$HighResThumbnailLoader$1$s-1KK0EnA0WJuK_oehEz11H5MbU;->f$0:Lcom/android/systemui/shared/recents/model/HighResThumbnailLoader$1;

    iput-object p2, p0, Lcom/android/systemui/shared/recents/model/-$$Lambda$HighResThumbnailLoader$1$s-1KK0EnA0WJuK_oehEz11H5MbU;->f$1:Lcom/android/systemui/shared/recents/model/Task;

    iput-object p3, p0, Lcom/android/systemui/shared/recents/model/-$$Lambda$HighResThumbnailLoader$1$s-1KK0EnA0WJuK_oehEz11H5MbU;->f$2:Lcom/android/systemui/shared/recents/model/ThumbnailData;

    return-void
.end method


# virtual methods
.method public final run()V
    .registers 4

    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/-$$Lambda$HighResThumbnailLoader$1$s-1KK0EnA0WJuK_oehEz11H5MbU;->f$0:Lcom/android/systemui/shared/recents/model/HighResThumbnailLoader$1;

    iget-object v1, p0, Lcom/android/systemui/shared/recents/model/-$$Lambda$HighResThumbnailLoader$1$s-1KK0EnA0WJuK_oehEz11H5MbU;->f$1:Lcom/android/systemui/shared/recents/model/Task;

    iget-object v2, p0, Lcom/android/systemui/shared/recents/model/-$$Lambda$HighResThumbnailLoader$1$s-1KK0EnA0WJuK_oehEz11H5MbU;->f$2:Lcom/android/systemui/shared/recents/model/ThumbnailData;

    invoke-static {v0, v1, v2}, Lcom/android/systemui/shared/recents/model/HighResThumbnailLoader$1;->lambda$loadTask$0(Lcom/android/systemui/shared/recents/model/HighResThumbnailLoader$1;Lcom/android/systemui/shared/recents/model/Task;Lcom/android/systemui/shared/recents/model/ThumbnailData;)V

    return-void
.end method
