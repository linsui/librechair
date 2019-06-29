.class public Lcom/android/systemui/shared/recents/utilities/AnimationProps;
.super Ljava/lang/Object;
.source "AnimationProps.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/android/systemui/shared/recents/utilities/AnimationProps$PropType;
    }
.end annotation


# static fields
.field public static final ALL:I = 0x0

.field public static final ALPHA:I = 0x4

.field public static final BOUNDS:I = 0x6

.field public static final DIM_ALPHA:I = 0x7

.field public static final IMMEDIATE:Lcom/android/systemui/shared/recents/utilities/AnimationProps;

.field private static final LINEAR_INTERPOLATOR:Landroid/view/animation/Interpolator;

.field public static final SCALE:I = 0x5

.field public static final TRANSLATION_X:I = 0x1

.field public static final TRANSLATION_Y:I = 0x2

.field public static final TRANSLATION_Z:I = 0x3


# instance fields
.field private mListener:Landroid/animation/Animator$AnimatorListener;

.field private mPropDuration:Landroid/util/SparseLongArray;

.field private mPropInterpolators:Landroid/util/SparseArray;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Landroid/util/SparseArray<",
            "Landroid/view/animation/Interpolator;",
            ">;"
        }
    .end annotation
.end field

.field private mPropStartDelay:Landroid/util/SparseLongArray;


# direct methods
.method static constructor <clinit>()V
    .registers 3

    .line 39
    new-instance v0, Landroid/view/animation/LinearInterpolator;

    invoke-direct {v0}, Landroid/view/animation/LinearInterpolator;-><init>()V

    sput-object v0, Lcom/android/systemui/shared/recents/utilities/AnimationProps;->LINEAR_INTERPOLATOR:Landroid/view/animation/Interpolator;

    .line 40
    new-instance v0, Lcom/android/systemui/shared/recents/utilities/AnimationProps;

    sget-object v1, Lcom/android/systemui/shared/recents/utilities/AnimationProps;->LINEAR_INTERPOLATOR:Landroid/view/animation/Interpolator;

    const/4 v2, 0x0

    invoke-direct {v0, v2, v1}, Lcom/android/systemui/shared/recents/utilities/AnimationProps;-><init>(ILandroid/view/animation/Interpolator;)V

    sput-object v0, Lcom/android/systemui/shared/recents/utilities/AnimationProps;->IMMEDIATE:Lcom/android/systemui/shared/recents/utilities/AnimationProps;

    return-void
.end method

.method public constructor <init>()V
    .registers 1

    .line 63
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method public constructor <init>(IILandroid/view/animation/Interpolator;)V
    .registers 5
    .param p1, "startDelay"    # I
    .param p2, "duration"    # I
    .param p3, "interpolator"    # Landroid/view/animation/Interpolator;

    .line 87
    const/4 v0, 0x0

    invoke-direct {p0, p1, p2, p3, v0}, Lcom/android/systemui/shared/recents/utilities/AnimationProps;-><init>(IILandroid/view/animation/Interpolator;Landroid/animation/Animator$AnimatorListener;)V

    .line 88
    return-void
.end method

.method public constructor <init>(IILandroid/view/animation/Interpolator;Landroid/animation/Animator$AnimatorListener;)V
    .registers 6
    .param p1, "startDelay"    # I
    .param p2, "duration"    # I
    .param p3, "interpolator"    # Landroid/view/animation/Interpolator;
    .param p4, "listener"    # Landroid/animation/Animator$AnimatorListener;

    .line 95
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 96
    const/4 v0, 0x0

    invoke-virtual {p0, v0, p1}, Lcom/android/systemui/shared/recents/utilities/AnimationProps;->setStartDelay(II)Lcom/android/systemui/shared/recents/utilities/AnimationProps;

    .line 97
    invoke-virtual {p0, v0, p2}, Lcom/android/systemui/shared/recents/utilities/AnimationProps;->setDuration(II)Lcom/android/systemui/shared/recents/utilities/AnimationProps;

    .line 98
    invoke-virtual {p0, v0, p3}, Lcom/android/systemui/shared/recents/utilities/AnimationProps;->setInterpolator(ILandroid/view/animation/Interpolator;)Lcom/android/systemui/shared/recents/utilities/AnimationProps;

    .line 99
    invoke-virtual {p0, p4}, Lcom/android/systemui/shared/recents/utilities/AnimationProps;->setListener(Landroid/animation/Animator$AnimatorListener;)Lcom/android/systemui/shared/recents/utilities/AnimationProps;

    .line 100
    return-void
.end method

.method public constructor <init>(ILandroid/view/animation/Interpolator;)V
    .registers 5
    .param p1, "duration"    # I
    .param p2, "interpolator"    # Landroid/view/animation/Interpolator;

    .line 70
    const/4 v0, 0x0

    const/4 v1, 0x0

    invoke-direct {p0, v0, p1, p2, v1}, Lcom/android/systemui/shared/recents/utilities/AnimationProps;-><init>(IILandroid/view/animation/Interpolator;Landroid/animation/Animator$AnimatorListener;)V

    .line 71
    return-void
.end method

.method public constructor <init>(ILandroid/view/animation/Interpolator;Landroid/animation/Animator$AnimatorListener;)V
    .registers 5
    .param p1, "duration"    # I
    .param p2, "interpolator"    # Landroid/view/animation/Interpolator;
    .param p3, "listener"    # Landroid/animation/Animator$AnimatorListener;

    .line 79
    const/4 v0, 0x0

    invoke-direct {p0, v0, p1, p2, p3}, Lcom/android/systemui/shared/recents/utilities/AnimationProps;-><init>(IILandroid/view/animation/Interpolator;Landroid/animation/Animator$AnimatorListener;)V

    .line 80
    return-void
.end method


# virtual methods
.method public apply(ILandroid/animation/ValueAnimator;)Landroid/animation/ValueAnimator;
    .registers 5
    .param p1, "propertyType"    # I
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "<T:",
            "Landroid/animation/ValueAnimator;",
            ">(ITT;)TT;"
        }
    .end annotation

    .line 120
    .local p2, "animator":Landroid/animation/ValueAnimator;, "TT;"
    invoke-virtual {p0, p1}, Lcom/android/systemui/shared/recents/utilities/AnimationProps;->getStartDelay(I)J

    move-result-wide v0

    invoke-virtual {p2, v0, v1}, Landroid/animation/ValueAnimator;->setStartDelay(J)V

    .line 121
    invoke-virtual {p0, p1}, Lcom/android/systemui/shared/recents/utilities/AnimationProps;->getDuration(I)J

    move-result-wide v0

    invoke-virtual {p2, v0, v1}, Landroid/animation/ValueAnimator;->setDuration(J)Landroid/animation/ValueAnimator;

    .line 122
    invoke-virtual {p0, p1}, Lcom/android/systemui/shared/recents/utilities/AnimationProps;->getInterpolator(I)Landroid/view/animation/Interpolator;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/animation/ValueAnimator;->setInterpolator(Landroid/animation/TimeInterpolator;)V

    .line 123
    return-object p2
.end method

.method public createAnimator(Ljava/util/List;)Landroid/animation/AnimatorSet;
    .registers 4
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/util/List<",
            "Landroid/animation/Animator;",
            ">;)",
            "Landroid/animation/AnimatorSet;"
        }
    .end annotation

    .line 107
    .local p1, "animators":Ljava/util/List;, "Ljava/util/List<Landroid/animation/Animator;>;"
    new-instance v0, Landroid/animation/AnimatorSet;

    invoke-direct {v0}, Landroid/animation/AnimatorSet;-><init>()V

    .line 108
    .local v0, "anim":Landroid/animation/AnimatorSet;
    iget-object v1, p0, Lcom/android/systemui/shared/recents/utilities/AnimationProps;->mListener:Landroid/animation/Animator$AnimatorListener;

    if-eqz v1, :cond_e

    .line 109
    iget-object v1, p0, Lcom/android/systemui/shared/recents/utilities/AnimationProps;->mListener:Landroid/animation/Animator$AnimatorListener;

    invoke-virtual {v0, v1}, Landroid/animation/AnimatorSet;->addListener(Landroid/animation/Animator$AnimatorListener;)V

    .line 111
    :cond_e
    invoke-virtual {v0, p1}, Landroid/animation/AnimatorSet;->playTogether(Ljava/util/Collection;)V

    .line 112
    return-object v0
.end method

.method public getDuration(I)J
    .registers 9
    .param p1, "propertyType"    # I

    .line 166
    iget-object v0, p0, Lcom/android/systemui/shared/recents/utilities/AnimationProps;->mPropDuration:Landroid/util/SparseLongArray;

    const-wide/16 v1, 0x0

    if-eqz v0, :cond_1b

    .line 167
    iget-object v0, p0, Lcom/android/systemui/shared/recents/utilities/AnimationProps;->mPropDuration:Landroid/util/SparseLongArray;

    const-wide/16 v3, -0x1

    invoke-virtual {v0, p1, v3, v4}, Landroid/util/SparseLongArray;->get(IJ)J

    move-result-wide v5

    .line 168
    .local v5, "duration":J
    cmp-long v0, v5, v3

    if-eqz v0, :cond_13

    .line 169
    return-wide v5

    .line 171
    :cond_13
    iget-object v0, p0, Lcom/android/systemui/shared/recents/utilities/AnimationProps;->mPropDuration:Landroid/util/SparseLongArray;

    const/4 v3, 0x0

    invoke-virtual {v0, v3, v1, v2}, Landroid/util/SparseLongArray;->get(IJ)J

    move-result-wide v0

    return-wide v0

    .line 173
    .end local v5    # "duration":J
    :cond_1b
    return-wide v1
.end method

.method public getInterpolator(I)Landroid/view/animation/Interpolator;
    .registers 6
    .param p1, "propertyType"    # I

    .line 192
    iget-object v0, p0, Lcom/android/systemui/shared/recents/utilities/AnimationProps;->mPropInterpolators:Landroid/util/SparseArray;

    if-eqz v0, :cond_1b

    .line 193
    iget-object v0, p0, Lcom/android/systemui/shared/recents/utilities/AnimationProps;->mPropInterpolators:Landroid/util/SparseArray;

    invoke-virtual {v0, p1}, Landroid/util/SparseArray;->get(I)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Landroid/view/animation/Interpolator;

    .line 194
    .local v0, "interp":Landroid/view/animation/Interpolator;
    if-eqz v0, :cond_f

    .line 195
    return-object v0

    .line 197
    :cond_f
    iget-object v1, p0, Lcom/android/systemui/shared/recents/utilities/AnimationProps;->mPropInterpolators:Landroid/util/SparseArray;

    const/4 v2, 0x0

    sget-object v3, Lcom/android/systemui/shared/recents/utilities/AnimationProps;->LINEAR_INTERPOLATOR:Landroid/view/animation/Interpolator;

    invoke-virtual {v1, v2, v3}, Landroid/util/SparseArray;->get(ILjava/lang/Object;)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Landroid/view/animation/Interpolator;

    return-object v1

    .line 199
    .end local v0    # "interp":Landroid/view/animation/Interpolator;
    :cond_1b
    sget-object v0, Lcom/android/systemui/shared/recents/utilities/AnimationProps;->LINEAR_INTERPOLATOR:Landroid/view/animation/Interpolator;

    return-object v0
.end method

.method public getListener()Landroid/animation/Animator$AnimatorListener;
    .registers 2

    .line 214
    iget-object v0, p0, Lcom/android/systemui/shared/recents/utilities/AnimationProps;->mListener:Landroid/animation/Animator$AnimatorListener;

    return-object v0
.end method

.method public getStartDelay(I)J
    .registers 9
    .param p1, "propertyType"    # I

    .line 141
    iget-object v0, p0, Lcom/android/systemui/shared/recents/utilities/AnimationProps;->mPropStartDelay:Landroid/util/SparseLongArray;

    const-wide/16 v1, 0x0

    if-eqz v0, :cond_1b

    .line 142
    iget-object v0, p0, Lcom/android/systemui/shared/recents/utilities/AnimationProps;->mPropStartDelay:Landroid/util/SparseLongArray;

    const-wide/16 v3, -0x1

    invoke-virtual {v0, p1, v3, v4}, Landroid/util/SparseLongArray;->get(IJ)J

    move-result-wide v5

    .line 143
    .local v5, "startDelay":J
    cmp-long v0, v5, v3

    if-eqz v0, :cond_13

    .line 144
    return-wide v5

    .line 146
    :cond_13
    iget-object v0, p0, Lcom/android/systemui/shared/recents/utilities/AnimationProps;->mPropStartDelay:Landroid/util/SparseLongArray;

    const/4 v3, 0x0

    invoke-virtual {v0, v3, v1, v2}, Landroid/util/SparseLongArray;->get(IJ)J

    move-result-wide v0

    return-wide v0

    .line 148
    .end local v5    # "startDelay":J
    :cond_1b
    return-wide v1
.end method

.method public isImmediate()Z
    .registers 9

    .line 221
    iget-object v0, p0, Lcom/android/systemui/shared/recents/utilities/AnimationProps;->mPropDuration:Landroid/util/SparseLongArray;

    invoke-virtual {v0}, Landroid/util/SparseLongArray;->size()I

    move-result v0

    .line 222
    .local v0, "count":I
    const/4 v1, 0x0

    const/4 v2, 0x0

    .line 222
    .local v2, "i":I
    :goto_8
    if-ge v2, v0, :cond_1a

    .line 223
    iget-object v3, p0, Lcom/android/systemui/shared/recents/utilities/AnimationProps;->mPropDuration:Landroid/util/SparseLongArray;

    invoke-virtual {v3, v2}, Landroid/util/SparseLongArray;->valueAt(I)J

    move-result-wide v3

    const-wide/16 v5, 0x0

    cmp-long v7, v3, v5

    if-lez v7, :cond_17

    .line 224
    return v1

    .line 222
    :cond_17
    add-int/lit8 v2, v2, 0x1

    goto :goto_8

    .line 227
    .end local v2    # "i":I
    :cond_1a
    const/4 v1, 0x1

    return v1
.end method

.method public setDuration(II)Lcom/android/systemui/shared/recents/utilities/AnimationProps;
    .registers 6
    .param p1, "propertyType"    # I
    .param p2, "duration"    # I

    .line 155
    iget-object v0, p0, Lcom/android/systemui/shared/recents/utilities/AnimationProps;->mPropDuration:Landroid/util/SparseLongArray;

    if-nez v0, :cond_b

    .line 156
    new-instance v0, Landroid/util/SparseLongArray;

    invoke-direct {v0}, Landroid/util/SparseLongArray;-><init>()V

    iput-object v0, p0, Lcom/android/systemui/shared/recents/utilities/AnimationProps;->mPropDuration:Landroid/util/SparseLongArray;

    .line 158
    :cond_b
    iget-object v0, p0, Lcom/android/systemui/shared/recents/utilities/AnimationProps;->mPropDuration:Landroid/util/SparseLongArray;

    int-to-long v1, p2

    invoke-virtual {v0, p1, v1, v2}, Landroid/util/SparseLongArray;->append(IJ)V

    .line 159
    return-object p0
.end method

.method public setInterpolator(ILandroid/view/animation/Interpolator;)Lcom/android/systemui/shared/recents/utilities/AnimationProps;
    .registers 4
    .param p1, "propertyType"    # I
    .param p2, "interpolator"    # Landroid/view/animation/Interpolator;

    .line 180
    iget-object v0, p0, Lcom/android/systemui/shared/recents/utilities/AnimationProps;->mPropInterpolators:Landroid/util/SparseArray;

    if-nez v0, :cond_b

    .line 181
    new-instance v0, Landroid/util/SparseArray;

    invoke-direct {v0}, Landroid/util/SparseArray;-><init>()V

    iput-object v0, p0, Lcom/android/systemui/shared/recents/utilities/AnimationProps;->mPropInterpolators:Landroid/util/SparseArray;

    .line 183
    :cond_b
    iget-object v0, p0, Lcom/android/systemui/shared/recents/utilities/AnimationProps;->mPropInterpolators:Landroid/util/SparseArray;

    invoke-virtual {v0, p1, p2}, Landroid/util/SparseArray;->append(ILjava/lang/Object;)V

    .line 184
    return-object p0
.end method

.method public setListener(Landroid/animation/Animator$AnimatorListener;)Lcom/android/systemui/shared/recents/utilities/AnimationProps;
    .registers 2
    .param p1, "listener"    # Landroid/animation/Animator$AnimatorListener;

    .line 206
    iput-object p1, p0, Lcom/android/systemui/shared/recents/utilities/AnimationProps;->mListener:Landroid/animation/Animator$AnimatorListener;

    .line 207
    return-object p0
.end method

.method public setStartDelay(II)Lcom/android/systemui/shared/recents/utilities/AnimationProps;
    .registers 6
    .param p1, "propertyType"    # I
    .param p2, "startDelay"    # I

    .line 130
    iget-object v0, p0, Lcom/android/systemui/shared/recents/utilities/AnimationProps;->mPropStartDelay:Landroid/util/SparseLongArray;

    if-nez v0, :cond_b

    .line 131
    new-instance v0, Landroid/util/SparseLongArray;

    invoke-direct {v0}, Landroid/util/SparseLongArray;-><init>()V

    iput-object v0, p0, Lcom/android/systemui/shared/recents/utilities/AnimationProps;->mPropStartDelay:Landroid/util/SparseLongArray;

    .line 133
    :cond_b
    iget-object v0, p0, Lcom/android/systemui/shared/recents/utilities/AnimationProps;->mPropStartDelay:Landroid/util/SparseLongArray;

    int-to-long v1, p2

    invoke-virtual {v0, p1, v1, v2}, Landroid/util/SparseLongArray;->append(IJ)V

    .line 134
    return-object p0
.end method
