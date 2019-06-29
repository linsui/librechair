.class public Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan;
.super Ljava/lang/Object;
.source "RecentsTaskLoadPlan.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan$Options;,
        Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan$PreloadOptions;
    }
.end annotation


# instance fields
.field private final mContext:Landroid/content/Context;

.field private final mKeyguardManager:Landroid/app/KeyguardManager;

.field private mRawTasks:Ljava/util/List;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/List<",
            "Landroid/app/ActivityManager$RecentTaskInfo;",
            ">;"
        }
    .end annotation
.end field

.field private mStack:Lcom/android/systemui/shared/recents/model/TaskStack;

.field private final mTmpLockedUsers:Landroid/util/SparseBooleanArray;


# direct methods
.method public constructor <init>(Landroid/content/Context;)V
    .registers 3
    .param p1, "context"    # Landroid/content/Context;

    .line 73
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 71
    new-instance v0, Landroid/util/SparseBooleanArray;

    invoke-direct {v0}, Landroid/util/SparseBooleanArray;-><init>()V

    iput-object v0, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan;->mTmpLockedUsers:Landroid/util/SparseBooleanArray;

    .line 74
    iput-object p1, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan;->mContext:Landroid/content/Context;

    .line 75
    const-string v0, "keyguard"

    invoke-virtual {p1, v0}, Landroid/content/Context;->getSystemService(Ljava/lang/String;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Landroid/app/KeyguardManager;

    iput-object v0, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan;->mKeyguardManager:Landroid/app/KeyguardManager;

    .line 76
    return-void
.end method


# virtual methods
.method public executePlan(Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan$Options;Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;)V
    .registers 15
    .param p1, "opts"    # Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan$Options;
    .param p2, "loader"    # Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;

    .line 168
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan;->mContext:Landroid/content/Context;

    invoke-virtual {v0}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    move-result-object v0

    .line 171
    .local v0, "res":Landroid/content/res/Resources;
    iget-object v1, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan;->mStack:Lcom/android/systemui/shared/recents/model/TaskStack;

    invoke-virtual {v1}, Lcom/android/systemui/shared/recents/model/TaskStack;->getTasks()Ljava/util/ArrayList;

    move-result-object v1

    .line 172
    .local v1, "tasks":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Lcom/android/systemui/shared/recents/model/Task;>;"
    invoke-virtual {v1}, Ljava/util/ArrayList;->size()I

    move-result v2

    .line 173
    .local v2, "taskCount":I
    const/4 v3, 0x0

    const/4 v4, 0x0

    .line 173
    .local v4, "i":I
    :goto_12
    if-ge v4, v2, :cond_64

    .line 174
    invoke-virtual {v1, v4}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Lcom/android/systemui/shared/recents/model/Task;

    .line 175
    .local v5, "task":Lcom/android/systemui/shared/recents/model/Task;
    iget-object v6, v5, Lcom/android/systemui/shared/recents/model/Task;->key:Lcom/android/systemui/shared/recents/model/Task$TaskKey;

    .line 177
    .local v6, "taskKey":Lcom/android/systemui/shared/recents/model/Task$TaskKey;
    iget-object v7, v5, Lcom/android/systemui/shared/recents/model/Task;->key:Lcom/android/systemui/shared/recents/model/Task$TaskKey;

    iget v7, v7, Lcom/android/systemui/shared/recents/model/Task$TaskKey;->id:I

    iget v8, p1, Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan$Options;->runningTaskId:I

    const/4 v9, 0x1

    if-ne v7, v8, :cond_27

    const/4 v7, 0x1

    goto :goto_28

    :cond_27
    const/4 v7, 0x0

    .line 178
    .local v7, "isRunningTask":Z
    :goto_28
    iget v8, p1, Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan$Options;->numVisibleTasks:I

    sub-int v8, v2, v8

    if-lt v4, v8, :cond_30

    const/4 v8, 0x1

    goto :goto_31

    :cond_30
    const/4 v8, 0x0

    .line 179
    .local v8, "isVisibleTask":Z
    :goto_31
    iget v10, p1, Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan$Options;->numVisibleTaskThumbnails:I

    sub-int v10, v2, v10

    if-lt v4, v10, :cond_39

    const/4 v10, 0x1

    goto :goto_3a

    :cond_39
    const/4 v10, 0x0

    .line 182
    .local v10, "isVisibleThumbnail":Z
    :goto_3a
    iget-boolean v11, p1, Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan$Options;->onlyLoadPausedActivities:Z

    if-eqz v11, :cond_41

    if-eqz v7, :cond_41

    .line 183
    goto :goto_61

    .line 186
    :cond_41
    iget-boolean v11, p1, Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan$Options;->loadIcons:Z

    if-eqz v11, :cond_55

    if-nez v7, :cond_49

    if-eqz v8, :cond_55

    .line 187
    :cond_49
    iget-object v11, v5, Lcom/android/systemui/shared/recents/model/Task;->icon:Landroid/graphics/drawable/Drawable;

    if-nez v11, :cond_55

    .line 188
    iget-object v11, v5, Lcom/android/systemui/shared/recents/model/Task;->taskDescription:Landroid/app/ActivityManager$TaskDescription;

    invoke-virtual {p2, v6, v11, v9}, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->getAndUpdateActivityIcon(Lcom/android/systemui/shared/recents/model/Task$TaskKey;Landroid/app/ActivityManager$TaskDescription;Z)Landroid/graphics/drawable/Drawable;

    move-result-object v11

    iput-object v11, v5, Lcom/android/systemui/shared/recents/model/Task;->icon:Landroid/graphics/drawable/Drawable;

    .line 192
    :cond_55
    iget-boolean v11, p1, Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan$Options;->loadThumbnails:Z

    if-eqz v11, :cond_61

    if-eqz v10, :cond_61

    .line 193
    invoke-virtual {p2, v6, v9, v9}, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->getAndUpdateThumbnail(Lcom/android/systemui/shared/recents/model/Task$TaskKey;ZZ)Lcom/android/systemui/shared/recents/model/ThumbnailData;

    move-result-object v9

    iput-object v9, v5, Lcom/android/systemui/shared/recents/model/Task;->thumbnail:Lcom/android/systemui/shared/recents/model/ThumbnailData;

    .line 173
    .end local v5    # "task":Lcom/android/systemui/shared/recents/model/Task;
    .end local v6    # "taskKey":Lcom/android/systemui/shared/recents/model/Task$TaskKey;
    .end local v7    # "isRunningTask":Z
    .end local v8    # "isVisibleTask":Z
    .end local v10    # "isVisibleThumbnail":Z
    :cond_61
    :goto_61
    add-int/lit8 v4, v4, 0x1

    goto :goto_12

    .line 197
    .end local v4    # "i":I
    :cond_64
    return-void
.end method

.method public getTaskStack()Lcom/android/systemui/shared/recents/model/TaskStack;
    .registers 2

    .line 203
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan;->mStack:Lcom/android/systemui/shared/recents/model/TaskStack;

    return-object v0
.end method

.method public hasTasks()Z
    .registers 3

    .line 208
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan;->mStack:Lcom/android/systemui/shared/recents/model/TaskStack;

    const/4 v1, 0x0

    if-eqz v0, :cond_10

    .line 209
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan;->mStack:Lcom/android/systemui/shared/recents/model/TaskStack;

    invoke-virtual {v0}, Lcom/android/systemui/shared/recents/model/TaskStack;->getTaskCount()I

    move-result v0

    if-lez v0, :cond_f

    const/4 v1, 0x1

    nop

    :cond_f
    return v1

    .line 211
    :cond_10
    return v1
.end method

.method public preloadPlan(Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan$PreloadOptions;Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;II)V
    .registers 47
    .param p1, "opts"    # Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan$PreloadOptions;
    .param p2, "loader"    # Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;
    .param p3, "runningTaskId"    # I
    .param p4, "currentUserId"    # I

    move-object/from16 v0, p0

    move-object/from16 v1, p1

    move-object/from16 v2, p2

    .line 91
    iget-object v3, v0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan;->mContext:Landroid/content/Context;

    invoke-virtual {v3}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    move-result-object v3

    .line 92
    .local v3, "res":Landroid/content/res/Resources;
    new-instance v4, Ljava/util/ArrayList;

    invoke-direct {v4}, Ljava/util/ArrayList;-><init>()V

    .line 93
    .local v4, "allTasks":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Lcom/android/systemui/shared/recents/model/Task;>;"
    iget-object v5, v0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan;->mRawTasks:Ljava/util/List;

    if-nez v5, :cond_2b

    .line 94
    invoke-static {}, Lcom/android/systemui/shared/system/ActivityManagerWrapper;->getInstance()Lcom/android/systemui/shared/system/ActivityManagerWrapper;

    move-result-object v5

    .line 95
    invoke-static {}, Landroid/app/ActivityManager;->getMaxRecentTasksStatic()I

    move-result v6

    .line 94
    move/from16 v7, p4

    invoke-virtual {v5, v6, v7}, Lcom/android/systemui/shared/system/ActivityManagerWrapper;->getRecentTasks(II)Ljava/util/List;

    move-result-object v5

    iput-object v5, v0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan;->mRawTasks:Ljava/util/List;

    .line 98
    iget-object v5, v0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan;->mRawTasks:Ljava/util/List;

    invoke-static {v5}, Ljava/util/Collections;->reverse(Ljava/util/List;)V

    goto :goto_2d

    .line 101
    :cond_2b
    move/from16 v7, p4

    :goto_2d
    iget-object v5, v0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan;->mRawTasks:Ljava/util/List;

    invoke-interface {v5}, Ljava/util/List;->size()I

    move-result v5

    .line 102
    .local v5, "taskCount":I
    const/4 v8, 0x0

    .line 102
    .local v8, "i":I
    :goto_34
    if-ge v8, v5, :cond_13e

    .line 103
    iget-object v9, v0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan;->mRawTasks:Ljava/util/List;

    invoke-interface {v9, v8}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v9

    check-cast v9, Landroid/app/ActivityManager$RecentTaskInfo;

    .line 106
    .local v9, "t":Landroid/app/ActivityManager$RecentTaskInfo;
    iget-object v10, v9, Landroid/app/ActivityManager$RecentTaskInfo;->origActivity:Landroid/content/ComponentName;

    if-eqz v10, :cond_46

    .line 108
    iget-object v10, v9, Landroid/app/ActivityManager$RecentTaskInfo;->origActivity:Landroid/content/ComponentName;

    .line 110
    :goto_44
    move-object v15, v10

    goto :goto_49

    :cond_46
    iget-object v10, v9, Landroid/app/ActivityManager$RecentTaskInfo;->realActivity:Landroid/content/ComponentName;

    goto :goto_44

    .line 111
    .local v15, "sourceComponent":Landroid/content/ComponentName;
    :goto_49
    iget-object v10, v9, Landroid/app/ActivityManager$RecentTaskInfo;->configuration:Landroid/content/res/Configuration;

    iget-object v10, v10, Landroid/content/res/Configuration;->windowConfiguration:Landroid/app/WindowConfiguration;

    invoke-virtual {v10}, Landroid/app/WindowConfiguration;->getWindowingMode()I

    move-result v10

    .line 112
    .local v10, "windowingMode":I
    new-instance v19, Lcom/android/systemui/shared/recents/model/Task$TaskKey;

    iget v12, v9, Landroid/app/ActivityManager$RecentTaskInfo;->persistentId:I

    iget-object v14, v9, Landroid/app/ActivityManager$RecentTaskInfo;->baseIntent:Landroid/content/Intent;

    iget v13, v9, Landroid/app/ActivityManager$RecentTaskInfo;->userId:I

    iget-wide v6, v9, Landroid/app/ActivityManager$RecentTaskInfo;->lastActiveTime:J

    move-object/from16 v11, v19

    move/from16 v16, v13

    move v13, v10

    move-wide/from16 v17, v6

    invoke-direct/range {v11 .. v18}, Lcom/android/systemui/shared/recents/model/Task$TaskKey;-><init>(IILandroid/content/Intent;Landroid/content/ComponentName;IJ)V

    move-object/from16 v6, v19

    .line 115
    .local v6, "taskKey":Lcom/android/systemui/shared/recents/model/Task$TaskKey;
    const/4 v7, 0x5

    if-ne v10, v7, :cond_6c

    const/4 v7, 0x1

    goto :goto_6d

    :cond_6c
    const/4 v7, 0x0

    .line 116
    .local v7, "isFreeformTask":Z
    :goto_6d
    if-nez v7, :cond_71

    const/4 v12, 0x1

    goto :goto_72

    :cond_71
    const/4 v12, 0x0

    .line 117
    .local v12, "isStackTask":Z
    :goto_72
    iget v13, v6, Lcom/android/systemui/shared/recents/model/Task$TaskKey;->id:I

    move/from16 v14, p3

    if-ne v13, v14, :cond_7b

    const/16 v28, 0x1

    goto :goto_7d

    :cond_7b
    const/16 v28, 0x0

    .line 119
    .local v28, "isLaunchTarget":Z
    :goto_7d
    invoke-virtual {v2, v6}, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->getAndUpdateActivityInfo(Lcom/android/systemui/shared/recents/model/Task$TaskKey;)Landroid/content/pm/ActivityInfo;

    move-result-object v13

    .line 120
    .local v13, "info":Landroid/content/pm/ActivityInfo;
    if-nez v13, :cond_8a

    .line 121
    nop

    .line 102
    move-object/from16 v36, v3

    move/from16 v37, v5

    goto/16 :goto_130

    .line 125
    :cond_8a
    iget-boolean v11, v1, Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan$PreloadOptions;->loadTitles:Z

    if-eqz v11, :cond_97

    .line 126
    iget-object v11, v9, Landroid/app/ActivityManager$RecentTaskInfo;->taskDescription:Landroid/app/ActivityManager$TaskDescription;

    invoke-virtual {v2, v6, v11}, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->getAndUpdateActivityTitle(Lcom/android/systemui/shared/recents/model/Task$TaskKey;Landroid/app/ActivityManager$TaskDescription;)Ljava/lang/String;

    move-result-object v11

    .line 127
    :goto_94
    move-object/from16 v24, v11

    goto :goto_9a

    :cond_97
    const-string v11, ""

    goto :goto_94

    .line 128
    .local v24, "title":Ljava/lang/String;
    :goto_9a
    iget-boolean v11, v1, Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan$PreloadOptions;->loadTitles:Z

    if-eqz v11, :cond_a7

    .line 129
    iget-object v11, v9, Landroid/app/ActivityManager$RecentTaskInfo;->taskDescription:Landroid/app/ActivityManager$TaskDescription;

    invoke-virtual {v2, v6, v11}, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->getAndUpdateContentDescription(Lcom/android/systemui/shared/recents/model/Task$TaskKey;Landroid/app/ActivityManager$TaskDescription;)Ljava/lang/String;

    move-result-object v11

    .line 130
    :goto_a4
    move-object/from16 v25, v11

    goto :goto_aa

    :cond_a7
    const-string v11, ""

    goto :goto_a4

    .line 131
    .local v25, "titleDescription":Ljava/lang/String;
    :goto_aa
    if-eqz v12, :cond_b6

    .line 132
    iget-object v11, v9, Landroid/app/ActivityManager$RecentTaskInfo;->taskDescription:Landroid/app/ActivityManager$TaskDescription;

    const/4 v1, 0x0

    invoke-virtual {v2, v6, v11, v1}, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->getAndUpdateActivityIcon(Lcom/android/systemui/shared/recents/model/Task$TaskKey;Landroid/app/ActivityManager$TaskDescription;Z)Landroid/graphics/drawable/Drawable;

    move-result-object v11

    .line 133
    :goto_b3
    move-object/from16 v22, v11

    goto :goto_b9

    :cond_b6
    const/4 v1, 0x0

    const/4 v11, 0x0

    goto :goto_b3

    .line 134
    .local v22, "icon":Landroid/graphics/drawable/Drawable;
    :goto_b9
    invoke-virtual {v2, v6, v1, v1}, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->getAndUpdateThumbnail(Lcom/android/systemui/shared/recents/model/Task$TaskKey;ZZ)Lcom/android/systemui/shared/recents/model/ThumbnailData;

    move-result-object v11

    .line 136
    .local v11, "thumbnail":Lcom/android/systemui/shared/recents/model/ThumbnailData;
    iget-object v1, v9, Landroid/app/ActivityManager$RecentTaskInfo;->taskDescription:Landroid/app/ActivityManager$TaskDescription;

    invoke-virtual {v2, v1}, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->getActivityPrimaryColor(Landroid/app/ActivityManager$TaskDescription;)I

    move-result v1

    .line 137
    .local v1, "activityColor":I
    move-object/from16 v36, v3

    iget-object v3, v9, Landroid/app/ActivityManager$RecentTaskInfo;->taskDescription:Landroid/app/ActivityManager$TaskDescription;

    .line 137
    .end local v3    # "res":Landroid/content/res/Resources;
    .local v36, "res":Landroid/content/res/Resources;
    invoke-virtual {v2, v3}, Lcom/android/systemui/shared/recents/model/RecentsTaskLoader;->getActivityBackgroundColor(Landroid/app/ActivityManager$TaskDescription;)I

    move-result v3

    .line 138
    .local v3, "backgroundColor":I
    if-eqz v13, :cond_da

    iget-object v2, v13, Landroid/content/pm/ActivityInfo;->applicationInfo:Landroid/content/pm/ApplicationInfo;

    iget v2, v2, Landroid/content/pm/ApplicationInfo;->flags:I

    const/16 v16, 0x1

    and-int/lit8 v2, v2, 0x1

    if-eqz v2, :cond_da

    const/16 v30, 0x1

    goto :goto_dc

    :cond_da
    const/16 v30, 0x0

    .line 142
    .local v30, "isSystemApp":Z
    :goto_dc
    iget-object v2, v0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan;->mTmpLockedUsers:Landroid/util/SparseBooleanArray;

    move/from16 v37, v5

    iget v5, v9, Landroid/app/ActivityManager$RecentTaskInfo;->userId:I

    .line 142
    .end local v5    # "taskCount":I
    .local v37, "taskCount":I
    invoke-virtual {v2, v5}, Landroid/util/SparseBooleanArray;->indexOfKey(I)I

    move-result v2

    if-gez v2, :cond_fc

    .line 143
    iget-object v2, v0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan;->mTmpLockedUsers:Landroid/util/SparseBooleanArray;

    iget v5, v9, Landroid/app/ActivityManager$RecentTaskInfo;->userId:I

    move/from16 v38, v7

    iget-object v7, v0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan;->mKeyguardManager:Landroid/app/KeyguardManager;

    .line 143
    .end local v7    # "isFreeformTask":Z
    .local v38, "isFreeformTask":Z
    move/from16 v39, v10

    iget v10, v9, Landroid/app/ActivityManager$RecentTaskInfo;->userId:I

    .line 143
    .end local v10    # "windowingMode":I
    .local v39, "windowingMode":I
    invoke-virtual {v7, v10}, Landroid/app/KeyguardManager;->isDeviceLocked(I)Z

    move-result v7

    invoke-virtual {v2, v5, v7}, Landroid/util/SparseBooleanArray;->put(IZ)V

    goto :goto_100

    .line 145
    .end local v38    # "isFreeformTask":Z
    .end local v39    # "windowingMode":I
    .restart local v7    # "isFreeformTask":Z
    .restart local v10    # "windowingMode":I
    :cond_fc
    move/from16 v38, v7

    move/from16 v39, v10

    .line 145
    .end local v7    # "isFreeformTask":Z
    .end local v10    # "windowingMode":I
    .restart local v38    # "isFreeformTask":Z
    .restart local v39    # "windowingMode":I
    :goto_100
    iget-object v2, v0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan;->mTmpLockedUsers:Landroid/util/SparseBooleanArray;

    iget v5, v9, Landroid/app/ActivityManager$RecentTaskInfo;->userId:I

    invoke-virtual {v2, v5}, Landroid/util/SparseBooleanArray;->get(I)Z

    move-result v2

    .line 148
    .local v2, "isLocked":Z
    new-instance v5, Lcom/android/systemui/shared/recents/model/Task;

    iget-boolean v7, v9, Landroid/app/ActivityManager$RecentTaskInfo;->supportsSplitScreenMultiWindow:Z

    iget-object v10, v9, Landroid/app/ActivityManager$RecentTaskInfo;->taskDescription:Landroid/app/ActivityManager$TaskDescription;

    move-object/from16 v40, v13

    iget v13, v9, Landroid/app/ActivityManager$RecentTaskInfo;->resizeMode:I

    .line 148
    .end local v13    # "info":Landroid/content/pm/ActivityInfo;
    .local v40, "info":Landroid/content/pm/ActivityInfo;
    iget-object v14, v9, Landroid/app/ActivityManager$RecentTaskInfo;->topActivity:Landroid/content/ComponentName;

    move-object/from16 v20, v5

    move-object/from16 v21, v6

    move-object/from16 v23, v11

    move/from16 v26, v1

    move/from16 v27, v3

    move/from16 v29, v12

    move/from16 v31, v7

    move-object/from16 v32, v10

    move/from16 v33, v13

    move-object/from16 v34, v14

    move/from16 v35, v2

    invoke-direct/range {v20 .. v35}, Lcom/android/systemui/shared/recents/model/Task;-><init>(Lcom/android/systemui/shared/recents/model/Task$TaskKey;Landroid/graphics/drawable/Drawable;Lcom/android/systemui/shared/recents/model/ThumbnailData;Ljava/lang/String;Ljava/lang/String;IIZZZZLandroid/app/ActivityManager$TaskDescription;ILandroid/content/ComponentName;Z)V

    .line 153
    .local v5, "task":Lcom/android/systemui/shared/recents/model/Task;
    invoke-virtual {v4, v5}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    .line 102
    .end local v1    # "activityColor":I
    .end local v2    # "isLocked":Z
    .end local v3    # "backgroundColor":I
    .end local v5    # "task":Lcom/android/systemui/shared/recents/model/Task;
    .end local v6    # "taskKey":Lcom/android/systemui/shared/recents/model/Task$TaskKey;
    .end local v9    # "t":Landroid/app/ActivityManager$RecentTaskInfo;
    .end local v11    # "thumbnail":Lcom/android/systemui/shared/recents/model/ThumbnailData;
    .end local v12    # "isStackTask":Z
    .end local v15    # "sourceComponent":Landroid/content/ComponentName;
    .end local v22    # "icon":Landroid/graphics/drawable/Drawable;
    .end local v24    # "title":Ljava/lang/String;
    .end local v25    # "titleDescription":Ljava/lang/String;
    .end local v28    # "isLaunchTarget":Z
    .end local v30    # "isSystemApp":Z
    .end local v38    # "isFreeformTask":Z
    .end local v39    # "windowingMode":I
    .end local v40    # "info":Landroid/content/pm/ActivityInfo;
    :goto_130
    add-int/lit8 v8, v8, 0x1

    move-object/from16 v3, v36

    move/from16 v5, v37

    move-object/from16 v1, p1

    move-object/from16 v2, p2

    move/from16 v7, p4

    goto/16 :goto_34

    .line 157
    .end local v8    # "i":I
    .end local v36    # "res":Landroid/content/res/Resources;
    .end local v37    # "taskCount":I
    .local v3, "res":Landroid/content/res/Resources;
    .local v5, "taskCount":I
    :cond_13e
    move-object/from16 v36, v3

    move/from16 v37, v5

    .line 157
    .end local v3    # "res":Landroid/content/res/Resources;
    .end local v5    # "taskCount":I
    .restart local v36    # "res":Landroid/content/res/Resources;
    .restart local v37    # "taskCount":I
    new-instance v1, Lcom/android/systemui/shared/recents/model/TaskStack;

    invoke-direct {v1}, Lcom/android/systemui/shared/recents/model/TaskStack;-><init>()V

    iput-object v1, v0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan;->mStack:Lcom/android/systemui/shared/recents/model/TaskStack;

    .line 158
    iget-object v1, v0, Lcom/android/systemui/shared/recents/model/RecentsTaskLoadPlan;->mStack:Lcom/android/systemui/shared/recents/model/TaskStack;

    const/4 v2, 0x0

    invoke-virtual {v1, v4, v2}, Lcom/android/systemui/shared/recents/model/TaskStack;->setTasks(Ljava/util/List;Z)V

    .line 159
    return-void
.end method
