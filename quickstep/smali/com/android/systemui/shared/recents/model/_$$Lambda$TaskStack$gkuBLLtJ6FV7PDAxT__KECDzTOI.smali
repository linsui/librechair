.class public final synthetic Lcom/android/systemui/shared/recents/model/_$$Lambda$TaskStack$gkuBLLtJ6FV7PDAxT__KECDzTOI;
.super Ljava/lang/Object;
.source "lambda"

# interfaces
.implements Lcom/android/systemui/shared/recents/model/TaskFilter;


# static fields
.field public static final synthetic INSTANCE:Lcom/android/systemui/shared/recents/model/_$$Lambda$TaskStack$gkuBLLtJ6FV7PDAxT__KECDzTOI;


# direct methods
.method static synthetic constructor <clinit>()V
    .registers 1

    new-instance v0, Lcom/android/systemui/shared/recents/model/_$$Lambda$TaskStack$gkuBLLtJ6FV7PDAxT__KECDzTOI;

    invoke-direct {v0}, Lcom/android/systemui/shared/recents/model/-$$Lambda$TaskStack$gkuBLLtJ6FV7PDAxT__KECDzTOI;-><init>()V

    sput-object v0, Lcom/android/systemui/shared/recents/model/_$$Lambda$TaskStack$gkuBLLtJ6FV7PDAxT__KECDzTOI;->INSTANCE:Lcom/android/systemui/shared/recents/model/_$$Lambda$TaskStack$gkuBLLtJ6FV7PDAxT__KECDzTOI;

    return-void
.end method

.method private synthetic constructor <init>()V
    .registers 1

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public final acceptTask(Landroid/util/SparseArray;Lcom/android/systemui/shared/recents/model/Task;I)Z
    .registers 4

    invoke-static {p1, p2, p3}, Lcom/android/systemui/shared/recents/model/TaskStack;->lambda$new$0(Landroid/util/SparseArray;Lcom/android/systemui/shared/recents/model/Task;I)Z

    move-result p1

    return p1
.end method
