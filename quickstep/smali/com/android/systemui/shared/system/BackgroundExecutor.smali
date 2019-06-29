.class public Lcom/android/systemui/shared/system/BackgroundExecutor;
.super Ljava/lang/Object;
.source "BackgroundExecutor.java"


# static fields
.field private static final sInstance:Lcom/android/systemui/shared/system/BackgroundExecutor;


# instance fields
.field private final mExecutorService:Ljava/util/concurrent/ExecutorService;


# direct methods
.method static constructor <clinit>()V
    .registers 1

    .line 29
    new-instance v0, Lcom/android/systemui/shared/system/BackgroundExecutor;

    invoke-direct {v0}, Lcom/android/systemui/shared/system/BackgroundExecutor;-><init>()V

    sput-object v0, Lcom/android/systemui/shared/system/BackgroundExecutor;->sInstance:Lcom/android/systemui/shared/system/BackgroundExecutor;

    return-void
.end method

.method public constructor <init>()V
    .registers 2

    .line 27
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 31
    const/4 v0, 0x2

    invoke-static {v0}, Ljava/util/concurrent/Executors;->newFixedThreadPool(I)Ljava/util/concurrent/ExecutorService;

    move-result-object v0

    iput-object v0, p0, Lcom/android/systemui/shared/system/BackgroundExecutor;->mExecutorService:Ljava/util/concurrent/ExecutorService;

    return-void
.end method

.method public static get()Lcom/android/systemui/shared/system/BackgroundExecutor;
    .registers 1

    .line 37
    sget-object v0, Lcom/android/systemui/shared/system/BackgroundExecutor;->sInstance:Lcom/android/systemui/shared/system/BackgroundExecutor;

    return-object v0
.end method


# virtual methods
.method public submit(Ljava/lang/Runnable;)Ljava/util/concurrent/Future;
    .registers 3
    .param p1, "runnable"    # Ljava/lang/Runnable;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/lang/Runnable;",
            ")",
            "Ljava/util/concurrent/Future<",
            "*>;"
        }
    .end annotation

    .line 51
    iget-object v0, p0, Lcom/android/systemui/shared/system/BackgroundExecutor;->mExecutorService:Ljava/util/concurrent/ExecutorService;

    invoke-interface {v0, p1}, Ljava/util/concurrent/ExecutorService;->submit(Ljava/lang/Runnable;)Ljava/util/concurrent/Future;

    move-result-object v0

    return-object v0
.end method

.method public submit(Ljava/lang/Runnable;Ljava/lang/Object;)Ljava/util/concurrent/Future;
    .registers 4
    .param p1, "runnable"    # Ljava/lang/Runnable;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "<T:",
            "Ljava/lang/Object;",
            ">(",
            "Ljava/lang/Runnable;",
            "TT;)",
            "Ljava/util/concurrent/Future<",
            "TT;>;"
        }
    .end annotation

    .line 59
    .local p2, "result":Ljava/lang/Object;, "TT;"
    iget-object v0, p0, Lcom/android/systemui/shared/system/BackgroundExecutor;->mExecutorService:Ljava/util/concurrent/ExecutorService;

    invoke-interface {v0, p1, p2}, Ljava/util/concurrent/ExecutorService;->submit(Ljava/lang/Runnable;Ljava/lang/Object;)Ljava/util/concurrent/Future;

    move-result-object v0

    return-object v0
.end method

.method public submit(Ljava/util/concurrent/Callable;)Ljava/util/concurrent/Future;
    .registers 3
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "<T:",
            "Ljava/lang/Object;",
            ">(",
            "Ljava/util/concurrent/Callable<",
            "TT;>;)",
            "Ljava/util/concurrent/Future<",
            "TT;>;"
        }
    .end annotation

    .line 44
    .local p1, "callable":Ljava/util/concurrent/Callable;, "Ljava/util/concurrent/Callable<TT;>;"
    iget-object v0, p0, Lcom/android/systemui/shared/system/BackgroundExecutor;->mExecutorService:Ljava/util/concurrent/ExecutorService;

    invoke-interface {v0, p1}, Ljava/util/concurrent/ExecutorService;->submit(Ljava/util/concurrent/Callable;)Ljava/util/concurrent/Future;

    move-result-object v0

    return-object v0
.end method
