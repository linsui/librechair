.class Lcom/android/systemui/shared/recents/model/RecentsTaskLoader$1;
.super Ljava/lang/Object;
.source "RecentsTaskLoader.java"

# interfaces
.implements Lcom/android/systemui/shared/recents/model/TaskKeyLruCache$EvictionCallback;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;


# direct methods
.method constructor <init>(Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;)V
    .registers 2
    .param p1, "this$0"    # Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;

    .line 85
    iput-object p1, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader$1;->this$0:Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onEntryEvicted(Lcom/android/systemui/shared/recents/model/Task$TaskKey;)V
    .registers 4
    .param p1, "key"    # Lcom/android/systemui/shared/recents/model/Task$TaskKey;

    .line 88
    if-eqz p1, :cond_f

    .line 89
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader$1;->this$0:Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;

    # getter for: Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->mActivityInfoCache:Landroid/util/LruCache;
    invoke-static {v0}, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->access$000(Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;)Landroid/util/LruCache;

    move-result-object v0

    invoke-virtual {p1}, Lcom/android/systemui/shared/recents/model/Task$TaskKey;->getComponent()Landroid/content/ComponentName;

    move-result-object v1

    invoke-virtual {v0, v1}, Landroid/util/LruCache;->remove(Ljava/lang/Object;)Ljava/lang/Object;

    .line 91
    :cond_f
    return-void
.end method
