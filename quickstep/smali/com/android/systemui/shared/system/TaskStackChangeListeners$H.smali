.class final Lcom/android/systemui/shared/system/TaskStackChangeListeners$H;
.super Landroid/os/Handler;
.source "TaskStackChangeListeners.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/android/systemui/shared/system/TaskStackChangeListeners;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x12
    name = "H"
.end annotation


# static fields
.field private static final ON_ACTIVITY_DISMISSING_DOCKED_STACK:I = 0x7

.field private static final ON_ACTIVITY_FORCED_RESIZABLE:I = 0x6

.field private static final ON_ACTIVITY_LAUNCH_ON_SECONDARY_DISPLAY_FAILED:I = 0xb

.field private static final ON_ACTIVITY_PINNED:I = 0x3

.field private static final ON_ACTIVITY_REQUESTED_ORIENTATION_CHANGE:I = 0xf

.field private static final ON_ACTIVITY_UNPINNED:I = 0xa

.field private static final ON_PINNED_ACTIVITY_RESTART_ATTEMPT:I = 0x4

.field private static final ON_PINNED_STACK_ANIMATION_ENDED:I = 0x5

.field private static final ON_PINNED_STACK_ANIMATION_STARTED:I = 0x9

.field private static final ON_TASK_CREATED:I = 0xc

.field private static final ON_TASK_MOVED_TO_FRONT:I = 0xe

.field private static final ON_TASK_PROFILE_LOCKED:I = 0x8

.field private static final ON_TASK_REMOVED:I = 0xd

.field private static final ON_TASK_SNAPSHOT_CHANGED:I = 0x2

.field private static final ON_TASK_STACK_CHANGED:I = 0x1


# instance fields
.field final synthetic this$0:Lcom/android/systemui/shared/system/TaskStackChangeListeners;


# direct methods
.method public constructor <init>(Lcom/android/systemui/shared/system/TaskStackChangeListeners;Landroid/os/Looper;)V
    .registers 3
    .param p2, "looper"    # Landroid/os/Looper;

    .line 188
    iput-object p1, p0, Lcom/android/systemui/shared/system/TaskStackChangeListeners$H;->this$0:Lcom/android/systemui/shared/system/TaskStackChangeListeners;

    .line 189
    invoke-direct {p0, p2}, Landroid/os/Handler;-><init>(Landroid/os/Looper;)V

    .line 190
    return-void
.end method


# virtual methods
.method public handleMessage(Landroid/os/Message;)V
    .registers 10
    .param p1, "msg"    # Landroid/os/Message;

    .line 194
    iget-object v0, p0, Lcom/android/systemui/shared/system/TaskStackChangeListeners$H;->this$0:Lcom/android/systemui/shared/system/TaskStackChangeListeners;

    # getter for: Lcom/android/systemui/shared/system/TaskStackChangeListeners;->mTaskStackListeners:Ljava/util/List;
    invoke-static {v0}, Lcom/android/systemui/shared/system/TaskStackChangeListeners;->access$000(Lcom/android/systemui/shared/system/TaskStackChangeListeners;)Ljava/util/List;

    move-result-object v0

    monitor-enter v0

    .line 195
    :try_start_7
    iget v1, p1, Landroid/os/Message;->what:I

    const/4 v2, 0x1

    packed-switch v1, :pswitch_data_248

    goto/16 :goto_242

    .line 291
    :pswitch_f
    iget-object v1, p0, Lcom/android/systemui/shared/system/TaskStackChangeListeners$H;->this$0:Lcom/android/systemui/shared/system/TaskStackChangeListeners;

    # getter for: Lcom/android/systemui/shared/system/TaskStackChangeListeners;->mTaskStackListeners:Ljava/util/List;
    invoke-static {v1}, Lcom/android/systemui/shared/system/TaskStackChangeListeners;->access$000(Lcom/android/systemui/shared/system/TaskStackChangeListeners;)Ljava/util/List;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/List;->size()I

    move-result v1

    sub-int/2addr v1, v2

    .line 291
    .local v1, "i":I
    :goto_1a
    if-ltz v1, :cond_242

    .line 292
    iget-object v2, p0, Lcom/android/systemui/shared/system/TaskStackChangeListeners$H;->this$0:Lcom/android/systemui/shared/system/TaskStackChangeListeners;

    # getter for: Lcom/android/systemui/shared/system/TaskStackChangeListeners;->mTaskStackListeners:Ljava/util/List;
    invoke-static {v2}, Lcom/android/systemui/shared/system/TaskStackChangeListeners;->access$000(Lcom/android/systemui/shared/system/TaskStackChangeListeners;)Ljava/util/List;

    move-result-object v2

    invoke-interface {v2, v1}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lcom/android/systemui/shared/system/TaskStackChangeListener;

    iget v3, p1, Landroid/os/Message;->arg1:I

    iget v4, p1, Landroid/os/Message;->arg2:I

    invoke-virtual {v2, v3, v4}, Lcom/android/systemui/shared/system/TaskStackChangeListener;->onActivityRequestedOrientationChanged(II)V

    .line 291
    add-int/lit8 v1, v1, -0x1

    goto :goto_1a

    .line 285
    .end local v1    # "i":I
    :pswitch_32
    iget-object v1, p0, Lcom/android/systemui/shared/system/TaskStackChangeListeners$H;->this$0:Lcom/android/systemui/shared/system/TaskStackChangeListeners;

    # getter for: Lcom/android/systemui/shared/system/TaskStackChangeListeners;->mTaskStackListeners:Ljava/util/List;
    invoke-static {v1}, Lcom/android/systemui/shared/system/TaskStackChangeListeners;->access$000(Lcom/android/systemui/shared/system/TaskStackChangeListeners;)Ljava/util/List;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/List;->size()I

    move-result v1

    sub-int/2addr v1, v2

    .line 285
    .restart local v1    # "i":I
    :goto_3d
    if-ltz v1, :cond_53

    .line 286
    iget-object v2, p0, Lcom/android/systemui/shared/system/TaskStackChangeListeners$H;->this$0:Lcom/android/systemui/shared/system/TaskStackChangeListeners;

    # getter for: Lcom/android/systemui/shared/system/TaskStackChangeListeners;->mTaskStackListeners:Ljava/util/List;
    invoke-static {v2}, Lcom/android/systemui/shared/system/TaskStackChangeListeners;->access$000(Lcom/android/systemui/shared/system/TaskStackChangeListeners;)Ljava/util/List;

    move-result-object v2

    invoke-interface {v2, v1}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lcom/android/systemui/shared/system/TaskStackChangeListener;

    iget v3, p1, Landroid/os/Message;->arg1:I

    invoke-virtual {v2, v3}, Lcom/android/systemui/shared/system/TaskStackChangeListener;->onTaskMovedToFront(I)V

    .line 285
    add-int/lit8 v1, v1, -0x1

    goto :goto_3d

    .line 288
    .end local v1    # "i":I
    :cond_53
    goto/16 :goto_242

    .line 279
    :pswitch_55
    iget-object v1, p0, Lcom/android/systemui/shared/system/TaskStackChangeListeners$H;->this$0:Lcom/android/systemui/shared/system/TaskStackChangeListeners;

    # getter for: Lcom/android/systemui/shared/system/TaskStackChangeListeners;->mTaskStackListeners:Ljava/util/List;
    invoke-static {v1}, Lcom/android/systemui/shared/system/TaskStackChangeListeners;->access$000(Lcom/android/systemui/shared/system/TaskStackChangeListeners;)Ljava/util/List;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/List;->size()I

    move-result v1

    sub-int/2addr v1, v2

    .line 279
    .restart local v1    # "i":I
    :goto_60
    if-ltz v1, :cond_76

    .line 280
    iget-object v2, p0, Lcom/android/systemui/shared/system/TaskStackChangeListeners$H;->this$0:Lcom/android/systemui/shared/system/TaskStackChangeListeners;

    # getter for: Lcom/android/systemui/shared/system/TaskStackChangeListeners;->mTaskStackListeners:Ljava/util/List;
    invoke-static {v2}, Lcom/android/systemui/shared/system/TaskStackChangeListeners;->access$000(Lcom/android/systemui/shared/system/TaskStackChangeListeners;)Ljava/util/List;

    move-result-object v2

    invoke-interface {v2, v1}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lcom/android/systemui/shared/system/TaskStackChangeListener;

    iget v3, p1, Landroid/os/Message;->arg1:I

    invoke-virtual {v2, v3}, Lcom/android/systemui/shared/system/TaskStackChangeListener;->onTaskRemoved(I)V

    .line 279
    add-int/lit8 v1, v1, -0x1

    goto :goto_60

    .line 282
    .end local v1    # "i":I
    :cond_76
    goto/16 :goto_242

    .line 272
    :pswitch_78
    iget-object v1, p0, Lcom/android/systemui/shared/system/TaskStackChangeListeners$H;->this$0:Lcom/android/systemui/shared/system/TaskStackChangeListeners;

    # getter for: Lcom/android/systemui/shared/system/TaskStackChangeListeners;->mTaskStackListeners:Ljava/util/List;
    invoke-static {v1}, Lcom/android/systemui/shared/system/TaskStackChangeListeners;->access$000(Lcom/android/systemui/shared/system/TaskStackChangeListeners;)Ljava/util/List;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/List;->size()I

    move-result v1

    sub-int/2addr v1, v2

    .line 272
    .restart local v1    # "i":I
    :goto_83
    if-ltz v1, :cond_9d

    .line 273
    iget-object v2, p0, Lcom/android/systemui/shared/system/TaskStackChangeListeners$H;->this$0:Lcom/android/systemui/shared/system/TaskStackChangeListeners;

    # getter for: Lcom/android/systemui/shared/system/TaskStackChangeListeners;->mTaskStackListeners:Ljava/util/List;
    invoke-static {v2}, Lcom/android/systemui/shared/system/TaskStackChangeListeners;->access$000(Lcom/android/systemui/shared/system/TaskStackChangeListeners;)Ljava/util/List;

    move-result-object v2

    invoke-interface {v2, v1}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lcom/android/systemui/shared/system/TaskStackChangeListener;

    iget v3, p1, Landroid/os/Message;->arg1:I

    iget-object v4, p1, Landroid/os/Message;->obj:Ljava/lang/Object;

    check-cast v4, Landroid/content/ComponentName;

    invoke-virtual {v2, v3, v4}, Lcom/android/systemui/shared/system/TaskStackChangeListener;->onTaskCreated(ILandroid/content/ComponentName;)V

    .line 272
    add-int/lit8 v1, v1, -0x1

    goto :goto_83

    .line 276
    .end local v1    # "i":I
    :cond_9d
    goto/16 :goto_242

    .line 260
    :pswitch_9f
    iget-object v1, p0, Lcom/android/systemui/shared/system/TaskStackChangeListeners$H;->this$0:Lcom/android/systemui/shared/system/TaskStackChangeListeners;

    # getter for: Lcom/android/systemui/shared/system/TaskStackChangeListeners;->mTaskStackListeners:Ljava/util/List;
    invoke-static {v1}, Lcom/android/systemui/shared/system/TaskStackChangeListeners;->access$000(Lcom/android/systemui/shared/system/TaskStackChangeListeners;)Ljava/util/List;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/List;->size()I

    move-result v1

    sub-int/2addr v1, v2

    .line 260
    .restart local v1    # "i":I
    :goto_aa
    if-ltz v1, :cond_be

    .line 261
    iget-object v2, p0, Lcom/android/systemui/shared/system/TaskStackChangeListeners$H;->this$0:Lcom/android/systemui/shared/system/TaskStackChangeListeners;

    # getter for: Lcom/android/systemui/shared/system/TaskStackChangeListeners;->mTaskStackListeners:Ljava/util/List;
    invoke-static {v2}, Lcom/android/systemui/shared/system/TaskStackChangeListeners;->access$000(Lcom/android/systemui/shared/system/TaskStackChangeListeners;)Ljava/util/List;

    move-result-object v2

    invoke-interface {v2, v1}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lcom/android/systemui/shared/system/TaskStackChangeListener;

    invoke-virtual {v2}, Lcom/android/systemui/shared/system/TaskStackChangeListener;->onActivityLaunchOnSecondaryDisplayFailed()V

    .line 260
    add-int/lit8 v1, v1, -0x1

    goto :goto_aa

    .line 263
    .end local v1    # "i":I
    :cond_be
    goto/16 :goto_242

    .line 222
    :pswitch_c0
    iget-object v1, p0, Lcom/android/systemui/shared/system/TaskStackChangeListeners$H;->this$0:Lcom/android/systemui/shared/system/TaskStackChangeListeners;

    # getter for: Lcom/android/systemui/shared/system/TaskStackChangeListeners;->mTaskStackListeners:Ljava/util/List;
    invoke-static {v1}, Lcom/android/systemui/shared/system/TaskStackChangeListeners;->access$000(Lcom/android/systemui/shared/system/TaskStackChangeListeners;)Ljava/util/List;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/List;->size()I

    move-result v1

    sub-int/2addr v1, v2

    .line 222
    .restart local v1    # "i":I
    :goto_cb
    if-ltz v1, :cond_df

    .line 223
    iget-object v2, p0, Lcom/android/systemui/shared/system/TaskStackChangeListeners$H;->this$0:Lcom/android/systemui/shared/system/TaskStackChangeListeners;

    # getter for: Lcom/android/systemui/shared/system/TaskStackChangeListeners;->mTaskStackListeners:Ljava/util/List;
    invoke-static {v2}, Lcom/android/systemui/shared/system/TaskStackChangeListeners;->access$000(Lcom/android/systemui/shared/system/TaskStackChangeListeners;)Ljava/util/List;

    move-result-object v2

    invoke-interface {v2, v1}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lcom/android/systemui/shared/system/TaskStackChangeListener;

    invoke-virtual {v2}, Lcom/android/systemui/shared/system/TaskStackChangeListener;->onActivityUnpinned()V

    .line 222
    add-int/lit8 v1, v1, -0x1

    goto :goto_cb

    .line 225
    .end local v1    # "i":I
    :cond_df
    goto/16 :goto_242

    .line 235
    :pswitch_e1
    iget-object v1, p0, Lcom/android/systemui/shared/system/TaskStackChangeListeners$H;->this$0:Lcom/android/systemui/shared/system/TaskStackChangeListeners;

    # getter for: Lcom/android/systemui/shared/system/TaskStackChangeListeners;->mTaskStackListeners:Ljava/util/List;
    invoke-static {v1}, Lcom/android/systemui/shared/system/TaskStackChangeListeners;->access$000(Lcom/android/systemui/shared/system/TaskStackChangeListeners;)Ljava/util/List;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/List;->size()I

    move-result v1

    sub-int/2addr v1, v2

    .line 235
    .restart local v1    # "i":I
    :goto_ec
    if-ltz v1, :cond_100

    .line 236
    iget-object v2, p0, Lcom/android/systemui/shared/system/TaskStackChangeListeners$H;->this$0:Lcom/android/systemui/shared/system/TaskStackChangeListeners;

    # getter for: Lcom/android/systemui/shared/system/TaskStackChangeListeners;->mTaskStackListeners:Ljava/util/List;
    invoke-static {v2}, Lcom/android/systemui/shared/system/TaskStackChangeListeners;->access$000(Lcom/android/systemui/shared/system/TaskStackChangeListeners;)Ljava/util/List;

    move-result-object v2

    invoke-interface {v2, v1}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lcom/android/systemui/shared/system/TaskStackChangeListener;

    invoke-virtual {v2}, Lcom/android/systemui/shared/system/TaskStackChangeListener;->onPinnedStackAnimationStarted()V

    .line 235
    add-int/lit8 v1, v1, -0x1

    goto :goto_ec

    .line 238
    .end local v1    # "i":I
    :cond_100
    goto/16 :goto_242

    .line 266
    :pswitch_102
    iget-object v1, p0, Lcom/android/systemui/shared/system/TaskStackChangeListeners$H;->this$0:Lcom/android/systemui/shared/system/TaskStackChangeListeners;

    # getter for: Lcom/android/systemui/shared/system/TaskStackChangeListeners;->mTaskStackListeners:Ljava/util/List;
    invoke-static {v1}, Lcom/android/systemui/shared/system/TaskStackChangeListeners;->access$000(Lcom/android/systemui/shared/system/TaskStackChangeListeners;)Ljava/util/List;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/List;->size()I

    move-result v1

    sub-int/2addr v1, v2

    .line 266
    .restart local v1    # "i":I
    :goto_10d
    if-ltz v1, :cond_125

    .line 267
    iget-object v2, p0, Lcom/android/systemui/shared/system/TaskStackChangeListeners$H;->this$0:Lcom/android/systemui/shared/system/TaskStackChangeListeners;

    # getter for: Lcom/android/systemui/shared/system/TaskStackChangeListeners;->mTaskStackListeners:Ljava/util/List;
    invoke-static {v2}, Lcom/android/systemui/shared/system/TaskStackChangeListeners;->access$000(Lcom/android/systemui/shared/system/TaskStackChangeListeners;)Ljava/util/List;

    move-result-object v2

    invoke-interface {v2, v1}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lcom/android/systemui/shared/system/TaskStackChangeListener;

    iget v3, p1, Landroid/os/Message;->arg1:I

    iget v4, p1, Landroid/os/Message;->arg2:I

    invoke-virtual {v2, v3, v4}, Lcom/android/systemui/shared/system/TaskStackChangeListener;->onTaskProfileLocked(II)V

    .line 266
    add-int/lit8 v1, v1, -0x1

    goto :goto_10d

    .line 269
    .end local v1    # "i":I
    :cond_125
    goto/16 :goto_242

    .line 254
    :pswitch_127
    iget-object v1, p0, Lcom/android/systemui/shared/system/TaskStackChangeListeners$H;->this$0:Lcom/android/systemui/shared/system/TaskStackChangeListeners;

    # getter for: Lcom/android/systemui/shared/system/TaskStackChangeListeners;->mTaskStackListeners:Ljava/util/List;
    invoke-static {v1}, Lcom/android/systemui/shared/system/TaskStackChangeListeners;->access$000(Lcom/android/systemui/shared/system/TaskStackChangeListeners;)Ljava/util/List;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/List;->size()I

    move-result v1

    sub-int/2addr v1, v2

    .line 254
    .restart local v1    # "i":I
    :goto_132
    if-ltz v1, :cond_146

    .line 255
    iget-object v2, p0, Lcom/android/systemui/shared/system/TaskStackChangeListeners$H;->this$0:Lcom/android/systemui/shared/system/TaskStackChangeListeners;

    # getter for: Lcom/android/systemui/shared/system/TaskStackChangeListeners;->mTaskStackListeners:Ljava/util/List;
    invoke-static {v2}, Lcom/android/systemui/shared/system/TaskStackChangeListeners;->access$000(Lcom/android/systemui/shared/system/TaskStackChangeListeners;)Ljava/util/List;

    move-result-object v2

    invoke-interface {v2, v1}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lcom/android/systemui/shared/system/TaskStackChangeListener;

    invoke-virtual {v2}, Lcom/android/systemui/shared/system/TaskStackChangeListener;->onActivityDismissingDockedStack()V

    .line 254
    add-int/lit8 v1, v1, -0x1

    goto :goto_132

    .line 257
    .end local v1    # "i":I
    :cond_146
    goto/16 :goto_242

    .line 247
    :pswitch_148
    iget-object v1, p0, Lcom/android/systemui/shared/system/TaskStackChangeListeners$H;->this$0:Lcom/android/systemui/shared/system/TaskStackChangeListeners;

    # getter for: Lcom/android/systemui/shared/system/TaskStackChangeListeners;->mTaskStackListeners:Ljava/util/List;
    invoke-static {v1}, Lcom/android/systemui/shared/system/TaskStackChangeListeners;->access$000(Lcom/android/systemui/shared/system/TaskStackChangeListeners;)Ljava/util/List;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/List;->size()I

    move-result v1

    sub-int/2addr v1, v2

    .line 247
    .restart local v1    # "i":I
    :goto_153
    if-ltz v1, :cond_16f

    .line 248
    iget-object v2, p0, Lcom/android/systemui/shared/system/TaskStackChangeListeners$H;->this$0:Lcom/android/systemui/shared/system/TaskStackChangeListeners;

    # getter for: Lcom/android/systemui/shared/system/TaskStackChangeListeners;->mTaskStackListeners:Ljava/util/List;
    invoke-static {v2}, Lcom/android/systemui/shared/system/TaskStackChangeListeners;->access$000(Lcom/android/systemui/shared/system/TaskStackChangeListeners;)Ljava/util/List;

    move-result-object v2

    invoke-interface {v2, v1}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lcom/android/systemui/shared/system/TaskStackChangeListener;

    iget-object v3, p1, Landroid/os/Message;->obj:Ljava/lang/Object;

    check-cast v3, Ljava/lang/String;

    iget v4, p1, Landroid/os/Message;->arg1:I

    iget v5, p1, Landroid/os/Message;->arg2:I

    invoke-virtual {v2, v3, v4, v5}, Lcom/android/systemui/shared/system/TaskStackChangeListener;->onActivityForcedResizable(Ljava/lang/String;II)V

    .line 247
    add-int/lit8 v1, v1, -0x1

    goto :goto_153

    .line 251
    .end local v1    # "i":I
    :cond_16f
    goto/16 :goto_242

    .line 241
    :pswitch_171
    iget-object v1, p0, Lcom/android/systemui/shared/system/TaskStackChangeListeners$H;->this$0:Lcom/android/systemui/shared/system/TaskStackChangeListeners;

    # getter for: Lcom/android/systemui/shared/system/TaskStackChangeListeners;->mTaskStackListeners:Ljava/util/List;
    invoke-static {v1}, Lcom/android/systemui/shared/system/TaskStackChangeListeners;->access$000(Lcom/android/systemui/shared/system/TaskStackChangeListeners;)Ljava/util/List;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/List;->size()I

    move-result v1

    sub-int/2addr v1, v2

    .line 241
    .restart local v1    # "i":I
    :goto_17c
    if-ltz v1, :cond_190

    .line 242
    iget-object v2, p0, Lcom/android/systemui/shared/system/TaskStackChangeListeners$H;->this$0:Lcom/android/systemui/shared/system/TaskStackChangeListeners;

    # getter for: Lcom/android/systemui/shared/system/TaskStackChangeListeners;->mTaskStackListeners:Ljava/util/List;
    invoke-static {v2}, Lcom/android/systemui/shared/system/TaskStackChangeListeners;->access$000(Lcom/android/systemui/shared/system/TaskStackChangeListeners;)Ljava/util/List;

    move-result-object v2

    invoke-interface {v2, v1}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lcom/android/systemui/shared/system/TaskStackChangeListener;

    invoke-virtual {v2}, Lcom/android/systemui/shared/system/TaskStackChangeListener;->onPinnedStackAnimationEnded()V

    .line 241
    add-int/lit8 v1, v1, -0x1

    goto :goto_17c

    .line 244
    .end local v1    # "i":I
    :cond_190
    goto/16 :goto_242

    .line 228
    :pswitch_192
    iget-object v1, p0, Lcom/android/systemui/shared/system/TaskStackChangeListeners$H;->this$0:Lcom/android/systemui/shared/system/TaskStackChangeListeners;

    # getter for: Lcom/android/systemui/shared/system/TaskStackChangeListeners;->mTaskStackListeners:Ljava/util/List;
    invoke-static {v1}, Lcom/android/systemui/shared/system/TaskStackChangeListeners;->access$000(Lcom/android/systemui/shared/system/TaskStackChangeListeners;)Ljava/util/List;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/List;->size()I

    move-result v1

    sub-int/2addr v1, v2

    .line 228
    .restart local v1    # "i":I
    :goto_19d
    if-ltz v1, :cond_1b8

    .line 229
    iget-object v3, p0, Lcom/android/systemui/shared/system/TaskStackChangeListeners$H;->this$0:Lcom/android/systemui/shared/system/TaskStackChangeListeners;

    # getter for: Lcom/android/systemui/shared/system/TaskStackChangeListeners;->mTaskStackListeners:Ljava/util/List;
    invoke-static {v3}, Lcom/android/systemui/shared/system/TaskStackChangeListeners;->access$000(Lcom/android/systemui/shared/system/TaskStackChangeListeners;)Ljava/util/List;

    move-result-object v3

    invoke-interface {v3, v1}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v3

    check-cast v3, Lcom/android/systemui/shared/system/TaskStackChangeListener;

    iget v4, p1, Landroid/os/Message;->arg1:I

    if-eqz v4, :cond_1b1

    const/4 v4, 0x1

    goto :goto_1b2

    :cond_1b1
    const/4 v4, 0x0

    :goto_1b2
    invoke-virtual {v3, v4}, Lcom/android/systemui/shared/system/TaskStackChangeListener;->onPinnedActivityRestartAttempt(Z)V

    .line 228
    add-int/lit8 v1, v1, -0x1

    goto :goto_19d

    .line 232
    .end local v1    # "i":I
    :cond_1b8
    goto/16 :goto_242

    .line 214
    :pswitch_1ba
    iget-object v1, p1, Landroid/os/Message;->obj:Ljava/lang/Object;

    check-cast v1, Lcom/android/systemui/shared/system/TaskStackChangeListeners$PinnedActivityInfo;

    .line 215
    .local v1, "info":Lcom/android/systemui/shared/system/TaskStackChangeListeners$PinnedActivityInfo;
    iget-object v3, p0, Lcom/android/systemui/shared/system/TaskStackChangeListeners$H;->this$0:Lcom/android/systemui/shared/system/TaskStackChangeListeners;

    # getter for: Lcom/android/systemui/shared/system/TaskStackChangeListeners;->mTaskStackListeners:Ljava/util/List;
    invoke-static {v3}, Lcom/android/systemui/shared/system/TaskStackChangeListeners;->access$000(Lcom/android/systemui/shared/system/TaskStackChangeListeners;)Ljava/util/List;

    move-result-object v3

    invoke-interface {v3}, Ljava/util/List;->size()I

    move-result v3

    sub-int/2addr v3, v2

    .line 215
    .local v3, "i":I
    :goto_1c9
    move v2, v3

    .line 215
    .end local v3    # "i":I
    .local v2, "i":I
    if-ltz v2, :cond_1e6

    .line 216
    iget-object v3, p0, Lcom/android/systemui/shared/system/TaskStackChangeListeners$H;->this$0:Lcom/android/systemui/shared/system/TaskStackChangeListeners;

    # getter for: Lcom/android/systemui/shared/system/TaskStackChangeListeners;->mTaskStackListeners:Ljava/util/List;
    invoke-static {v3}, Lcom/android/systemui/shared/system/TaskStackChangeListeners;->access$000(Lcom/android/systemui/shared/system/TaskStackChangeListeners;)Ljava/util/List;

    move-result-object v3

    invoke-interface {v3, v2}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v3

    check-cast v3, Lcom/android/systemui/shared/system/TaskStackChangeListener;

    iget-object v4, v1, Lcom/android/systemui/shared/system/TaskStackChangeListeners$PinnedActivityInfo;->mPackageName:Ljava/lang/String;

    iget v5, v1, Lcom/android/systemui/shared/system/TaskStackChangeListeners$PinnedActivityInfo;->mUserId:I

    iget v6, v1, Lcom/android/systemui/shared/system/TaskStackChangeListeners$PinnedActivityInfo;->mTaskId:I

    iget v7, v1, Lcom/android/systemui/shared/system/TaskStackChangeListeners$PinnedActivityInfo;->mStackId:I

    invoke-virtual {v3, v4, v5, v6, v7}, Lcom/android/systemui/shared/system/TaskStackChangeListener;->onActivityPinned(Ljava/lang/String;III)V

    .line 215
    add-int/lit8 v3, v2, -0x1

    .line 215
    .end local v2    # "i":I
    .restart local v3    # "i":I
    goto :goto_1c9

    .line 219
    .end local v3    # "i":I
    :cond_1e6
    goto :goto_242

    .line 205
    .end local v1    # "info":Lcom/android/systemui/shared/system/TaskStackChangeListeners$PinnedActivityInfo;
    :pswitch_1e7
    const-string v1, "onTaskSnapshotChanged"

    invoke-static {v1}, Landroid/os/Trace;->beginSection(Ljava/lang/String;)V

    .line 206
    iget-object v1, p0, Lcom/android/systemui/shared/system/TaskStackChangeListeners$H;->this$0:Lcom/android/systemui/shared/system/TaskStackChangeListeners;

    # getter for: Lcom/android/systemui/shared/system/TaskStackChangeListeners;->mTaskStackListeners:Ljava/util/List;
    invoke-static {v1}, Lcom/android/systemui/shared/system/TaskStackChangeListeners;->access$000(Lcom/android/systemui/shared/system/TaskStackChangeListeners;)Ljava/util/List;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/List;->size()I

    move-result v1

    sub-int/2addr v1, v2

    .line 206
    .local v1, "i":I
    :goto_1f7
    if-ltz v1, :cond_216

    .line 207
    iget-object v2, p0, Lcom/android/systemui/shared/system/TaskStackChangeListeners$H;->this$0:Lcom/android/systemui/shared/system/TaskStackChangeListeners;

    # getter for: Lcom/android/systemui/shared/system/TaskStackChangeListeners;->mTaskStackListeners:Ljava/util/List;
    invoke-static {v2}, Lcom/android/systemui/shared/system/TaskStackChangeListeners;->access$000(Lcom/android/systemui/shared/system/TaskStackChangeListeners;)Ljava/util/List;

    move-result-object v2

    invoke-interface {v2, v1}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lcom/android/systemui/shared/system/TaskStackChangeListener;

    iget v3, p1, Landroid/os/Message;->arg1:I

    new-instance v4, Lcom/android/systemui/shared/recents/model/ThumbnailData;

    iget-object v5, p1, Landroid/os/Message;->obj:Ljava/lang/Object;

    check-cast v5, Landroid/app/ActivityManager$TaskSnapshot;

    invoke-direct {v4, v5}, Lcom/android/systemui/shared/recents/model/ThumbnailData;-><init>(Landroid/app/ActivityManager$TaskSnapshot;)V

    invoke-virtual {v2, v3, v4}, Lcom/android/systemui/shared/system/TaskStackChangeListener;->onTaskSnapshotChanged(ILcom/android/systemui/shared/recents/model/ThumbnailData;)V

    .line 206
    add-int/lit8 v1, v1, -0x1

    goto :goto_1f7

    .line 210
    .end local v1    # "i":I
    :cond_216
    invoke-static {}, Landroid/os/Trace;->endSection()V

    .line 211
    goto :goto_242

    .line 197
    :pswitch_21a
    const-string v1, "onTaskStackChanged"

    invoke-static {v1}, Landroid/os/Trace;->beginSection(Ljava/lang/String;)V

    .line 198
    iget-object v1, p0, Lcom/android/systemui/shared/system/TaskStackChangeListeners$H;->this$0:Lcom/android/systemui/shared/system/TaskStackChangeListeners;

    # getter for: Lcom/android/systemui/shared/system/TaskStackChangeListeners;->mTaskStackListeners:Ljava/util/List;
    invoke-static {v1}, Lcom/android/systemui/shared/system/TaskStackChangeListeners;->access$000(Lcom/android/systemui/shared/system/TaskStackChangeListeners;)Ljava/util/List;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/List;->size()I

    move-result v1

    sub-int/2addr v1, v2

    .line 198
    .restart local v1    # "i":I
    :goto_22a
    if-ltz v1, :cond_23e

    .line 199
    iget-object v2, p0, Lcom/android/systemui/shared/system/TaskStackChangeListeners$H;->this$0:Lcom/android/systemui/shared/system/TaskStackChangeListeners;

    # getter for: Lcom/android/systemui/shared/system/TaskStackChangeListeners;->mTaskStackListeners:Ljava/util/List;
    invoke-static {v2}, Lcom/android/systemui/shared/system/TaskStackChangeListeners;->access$000(Lcom/android/systemui/shared/system/TaskStackChangeListeners;)Ljava/util/List;

    move-result-object v2

    invoke-interface {v2, v1}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lcom/android/systemui/shared/system/TaskStackChangeListener;

    invoke-virtual {v2}, Lcom/android/systemui/shared/system/TaskStackChangeListener;->onTaskStackChanged()V

    .line 198
    add-int/lit8 v1, v1, -0x1

    goto :goto_22a

    .line 201
    .end local v1    # "i":I
    :cond_23e
    invoke-static {}, Landroid/os/Trace;->endSection()V

    .line 202
    nop

    .line 298
    :cond_242
    :goto_242
    monitor-exit v0

    .line 299
    return-void

    .line 298
    :catchall_244
    move-exception v1

    monitor-exit v0
    :try_end_246
    .catchall {:try_start_7 .. :try_end_246} :catchall_244

    throw v1

    nop

    :pswitch_data_248
    .packed-switch 0x1
        :pswitch_21a
        :pswitch_1e7
        :pswitch_1ba
        :pswitch_192
        :pswitch_171
        :pswitch_148
        :pswitch_127
        :pswitch_102
        :pswitch_e1
        :pswitch_c0
        :pswitch_9f
        :pswitch_78
        :pswitch_55
        :pswitch_32
        :pswitch_f
    .end packed-switch
.end method
