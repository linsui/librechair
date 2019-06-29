.class public Lcom/android/systemui/shared/system/RemoteAnimationTargetCompat;
.super Ljava/lang/Object;
.source "RemoteAnimationTargetCompat.java"


# static fields
.field public static final ACTIVITY_TYPE_ASSISTANT:I = 0x4

.field public static final ACTIVITY_TYPE_HOME:I = 0x2

.field public static final ACTIVITY_TYPE_RECENTS:I = 0x3

.field public static final ACTIVITY_TYPE_STANDARD:I = 0x1

.field public static final ACTIVITY_TYPE_UNDEFINED:I = 0x0

.field public static final MODE_CLOSING:I = 0x1

.field public static final MODE_OPENING:I


# instance fields
.field public final activityType:I

.field public final clipRect:Landroid/graphics/Rect;

.field public final contentInsets:Landroid/graphics/Rect;

.field public final isNotInRecents:Z

.field public final isTranslucent:Z

.field public final leash:Lcom/android/systemui/shared/system/SurfaceControlCompat;

.field public final mode:I

.field public final position:Landroid/graphics/Point;

.field public final prefixOrderIndex:I

.field public final sourceContainerBounds:Landroid/graphics/Rect;

.field public final taskId:I


# direct methods
.method public constructor <init>(Landroid/view/RemoteAnimationTarget;)V
    .registers 4
    .param p1, "app"    # Landroid/view/RemoteAnimationTarget;

    .line 50
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 51
    iget v0, p1, Landroid/view/RemoteAnimationTarget;->taskId:I

    iput v0, p0, Lcom/android/systemui/shared/system/RemoteAnimationTargetCompat;->taskId:I

    .line 52
    iget v0, p1, Landroid/view/RemoteAnimationTarget;->mode:I

    iput v0, p0, Lcom/android/systemui/shared/system/RemoteAnimationTargetCompat;->mode:I

    .line 53
    new-instance v0, Lcom/android/systemui/shared/system/SurfaceControlCompat;

    iget-object v1, p1, Landroid/view/RemoteAnimationTarget;->leash:Landroid/view/SurfaceControl;

    invoke-direct {v0, v1}, Lcom/android/systemui/shared/system/SurfaceControlCompat;-><init>(Landroid/view/SurfaceControl;)V

    iput-object v0, p0, Lcom/android/systemui/shared/system/RemoteAnimationTargetCompat;->leash:Lcom/android/systemui/shared/system/SurfaceControlCompat;

    .line 54
    iget-boolean v0, p1, Landroid/view/RemoteAnimationTarget;->isTranslucent:Z

    iput-boolean v0, p0, Lcom/android/systemui/shared/system/RemoteAnimationTargetCompat;->isTranslucent:Z

    .line 55
    iget-object v0, p1, Landroid/view/RemoteAnimationTarget;->clipRect:Landroid/graphics/Rect;

    iput-object v0, p0, Lcom/android/systemui/shared/system/RemoteAnimationTargetCompat;->clipRect:Landroid/graphics/Rect;

    .line 56
    iget-object v0, p1, Landroid/view/RemoteAnimationTarget;->position:Landroid/graphics/Point;

    iput-object v0, p0, Lcom/android/systemui/shared/system/RemoteAnimationTargetCompat;->position:Landroid/graphics/Point;

    .line 57
    iget-object v0, p1, Landroid/view/RemoteAnimationTarget;->sourceContainerBounds:Landroid/graphics/Rect;

    iput-object v0, p0, Lcom/android/systemui/shared/system/RemoteAnimationTargetCompat;->sourceContainerBounds:Landroid/graphics/Rect;

    .line 58
    iget v0, p1, Landroid/view/RemoteAnimationTarget;->prefixOrderIndex:I

    iput v0, p0, Lcom/android/systemui/shared/system/RemoteAnimationTargetCompat;->prefixOrderIndex:I

    .line 59
    iget-boolean v0, p1, Landroid/view/RemoteAnimationTarget;->isNotInRecents:Z

    iput-boolean v0, p0, Lcom/android/systemui/shared/system/RemoteAnimationTargetCompat;->isNotInRecents:Z

    .line 60
    iget-object v0, p1, Landroid/view/RemoteAnimationTarget;->contentInsets:Landroid/graphics/Rect;

    iput-object v0, p0, Lcom/android/systemui/shared/system/RemoteAnimationTargetCompat;->contentInsets:Landroid/graphics/Rect;

    .line 61
    iget-object v0, p1, Landroid/view/RemoteAnimationTarget;->windowConfiguration:Landroid/app/WindowConfiguration;

    invoke-virtual {v0}, Landroid/app/WindowConfiguration;->getActivityType()I

    move-result v0

    iput v0, p0, Lcom/android/systemui/shared/system/RemoteAnimationTargetCompat;->activityType:I

    .line 62
    return-void
.end method

.method public static wrap([Landroid/view/RemoteAnimationTarget;)[Lcom/android/systemui/shared/system/RemoteAnimationTargetCompat;
    .registers 5
    .param p0, "apps"    # [Landroid/view/RemoteAnimationTarget;

    .line 65
    array-length v0, p0

    new-array v0, v0, [Lcom/android/systemui/shared/system/RemoteAnimationTargetCompat;

    .line 67
    .local v0, "appsCompat":[Lcom/android/systemui/shared/system/RemoteAnimationTargetCompat;
    const/4 v1, 0x0

    .line 67
    .local v1, "i":I
    :goto_4
    array-length v2, p0

    if-ge v1, v2, :cond_13

    .line 68
    new-instance v2, Lcom/android/systemui/shared/system/RemoteAnimationTargetCompat;

    aget-object v3, p0, v1

    invoke-direct {v2, v3}, Lcom/android/systemui/shared/system/RemoteAnimationTargetCompat;-><init>(Landroid/view/RemoteAnimationTarget;)V

    aput-object v2, v0, v1

    .line 67
    add-int/lit8 v1, v1, 0x1

    goto :goto_4

    .line 70
    .end local v1    # "i":I
    :cond_13
    return-object v0
.end method
