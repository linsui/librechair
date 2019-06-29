.class Lcom/android/systemui/shared/recents/view/RecentsTransition$1;
.super Ljava/lang/Object;
.source "RecentsTransition.java"

# interfaces
.implements Landroid/app/ActivityOptions$OnAnimationStartedListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/android/systemui/shared/recents/view/RecentsTransition;->createAspectScaleAnimation(Landroid/content/Context;Landroid/os/Handler;ZLcom/android/systemui/shared/recents/view/AppTransitionAnimationSpecsFuture;Ljava/lang/Runnable;)Landroid/app/ActivityOptions;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field private mHandled:Z

.field final synthetic val$animationStartCallback:Ljava/lang/Runnable;


# direct methods
.method constructor <init>(Ljava/lang/Runnable;)V
    .registers 2

    .line 46
    iput-object p1, p0, Lcom/android/systemui/shared/recents/view/RecentsTransition$1;->val$animationStartCallback:Ljava/lang/Runnable;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onAnimationStarted()V
    .registers 2

    .line 53
    iget-boolean v0, p0, Lcom/android/systemui/shared/recents/view/RecentsTransition$1;->mHandled:Z

    if-eqz v0, :cond_5

    .line 54
    return-void

    .line 56
    :cond_5
    const/4 v0, 0x1

    iput-boolean v0, p0, Lcom/android/systemui/shared/recents/view/RecentsTransition$1;->mHandled:Z

    .line 58
    iget-object v0, p0, Lcom/android/systemui/shared/recents/view/RecentsTransition$1;->val$animationStartCallback:Ljava/lang/Runnable;

    if-eqz v0, :cond_11

    .line 59
    iget-object v0, p0, Lcom/android/systemui/shared/recents/view/RecentsTransition$1;->val$animationStartCallback:Ljava/lang/Runnable;

    invoke-interface {v0}, Ljava/lang/Runnable;->run()V

    .line 61
    :cond_11
    return-void
.end method
