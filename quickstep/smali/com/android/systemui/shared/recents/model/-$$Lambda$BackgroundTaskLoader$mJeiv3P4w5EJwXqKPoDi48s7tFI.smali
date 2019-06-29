.class public final synthetic Lcom/android/systemui/shared/recents/model/-$$Lambda$BackgroundTaskLoader$mJeiv3P4w5EJwXqKPoDi48s7tFI;
.super Ljava/lang/Object;
.source "lambda"

# interfaces
.implements Ljava/lang/Runnable;


# instance fields
.field private final synthetic f$0:Lcom/android/systemui/shared/recents/model/Task;

.field private final synthetic f$1:Lcom/android/systemui/shared/recents/model/ThumbnailData;

.field private final synthetic f$2:Landroid/graphics/drawable/Drawable;


# direct methods
.method public synthetic constructor <init>(Lcom/android/systemui/shared/recents/model/Task;Lcom/android/systemui/shared/recents/model/ThumbnailData;Landroid/graphics/drawable/Drawable;)V
    .registers 4

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    iput-object p1, p0, Lcom/android/systemui/shared/recents/model/-$$Lambda$BackgroundTaskLoader$mJeiv3P4w5EJwXqKPoDi48s7tFI;->f$0:Lcom/android/systemui/shared/recents/model/Task;

    iput-object p2, p0, Lcom/android/systemui/shared/recents/model/-$$Lambda$BackgroundTaskLoader$mJeiv3P4w5EJwXqKPoDi48s7tFI;->f$1:Lcom/android/systemui/shared/recents/model/ThumbnailData;

    iput-object p3, p0, Lcom/android/systemui/shared/recents/model/-$$Lambda$BackgroundTaskLoader$mJeiv3P4w5EJwXqKPoDi48s7tFI;->f$2:Landroid/graphics/drawable/Drawable;

    return-void
.end method


# virtual methods
.method public final run()V
    .registers 4

    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/-$$Lambda$BackgroundTaskLoader$mJeiv3P4w5EJwXqKPoDi48s7tFI;->f$0:Lcom/android/systemui/shared/recents/model/Task;

    iget-object v1, p0, Lcom/android/systemui/shared/recents/model/-$$Lambda$BackgroundTaskLoader$mJeiv3P4w5EJwXqKPoDi48s7tFI;->f$1:Lcom/android/systemui/shared/recents/model/ThumbnailData;

    iget-object v2, p0, Lcom/android/systemui/shared/recents/model/-$$Lambda$BackgroundTaskLoader$mJeiv3P4w5EJwXqKPoDi48s7tFI;->f$2:Landroid/graphics/drawable/Drawable;

    invoke-static {v0, v1, v2}, Lcom/android/systemui/shared/recents/model/BackgroundTaskLoader;->lambda$processLoadQueueItem$2(Lcom/android/systemui/shared/recents/model/Task;Lcom/android/systemui/shared/recents/model/ThumbnailData;Landroid/graphics/drawable/Drawable;)V

    return-void
.end method
