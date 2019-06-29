.class Lcom/android/systemui/shared/system/ActivityManagerWrapper$5$1;
.super Ljava/lang/Object;
.source "ActivityManagerWrapper.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/android/systemui/shared/system/ActivityManagerWrapper$5;->run()V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$1:Lcom/android/systemui/shared/system/ActivityManagerWrapper$5;

.field final synthetic val$finalResult:Z


# direct methods
.method constructor <init>(Lcom/android/systemui/shared/system/ActivityManagerWrapper$5;Z)V
    .registers 3
    .param p1, "this$1"    # Lcom/android/systemui/shared/system/ActivityManagerWrapper$5;

    .line 319
    iput-object p1, p0, Lcom/android/systemui/shared/system/ActivityManagerWrapper$5$1;->this$1:Lcom/android/systemui/shared/system/ActivityManagerWrapper$5;

    iput-boolean p2, p0, Lcom/android/systemui/shared/system/ActivityManagerWrapper$5$1;->val$finalResult:Z

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public run()V
    .registers 3

    .line 322
    iget-object v0, p0, Lcom/android/systemui/shared/system/ActivityManagerWrapper$5$1;->this$1:Lcom/android/systemui/shared/system/ActivityManagerWrapper$5;

    iget-object v0, v0, Lcom/android/systemui/shared/system/ActivityManagerWrapper$5;->val$resultCallback:Ljava/util/function/Consumer;

    iget-boolean v1, p0, Lcom/android/systemui/shared/system/ActivityManagerWrapper$5$1;->val$finalResult:Z

    invoke-static {v1}, Ljava/lang/Boolean;->valueOf(Z)Ljava/lang/Boolean;

    move-result-object v1

    invoke-interface {v0, v1}, Ljava/util/function/Consumer;->accept(Ljava/lang/Object;)V

    .line 323
    return-void
.end method
