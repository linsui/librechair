.class public Lcom/android/systemui/shared/recents/model/Task;
.super Ljava/lang/Object;
.source "Task.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/android/systemui/shared/recents/model/Task$TaskKey;,
        Lcom/android/systemui/shared/recents/model/Task$TaskCallbacks;
    }
.end annotation


# static fields
.field public static final TAG:Ljava/lang/String; = "Task"


# instance fields
.field public colorBackground:I
    .annotation runtime Landroid/view/ViewDebug$ExportedProperty;
        category = "recents"
    .end annotation
.end field

.field public colorPrimary:I
    .annotation runtime Landroid/view/ViewDebug$ExportedProperty;
        category = "recents"
    .end annotation
.end field

.field public icon:Landroid/graphics/drawable/Drawable;

.field public isDockable:Z
    .annotation runtime Landroid/view/ViewDebug$ExportedProperty;
        category = "recents"
    .end annotation
.end field

.field public isLaunchTarget:Z
    .annotation runtime Landroid/view/ViewDebug$ExportedProperty;
        category = "recents"
    .end annotation
.end field

.field public isLocked:Z
    .annotation runtime Landroid/view/ViewDebug$ExportedProperty;
        category = "recents"
    .end annotation
.end field

.field public isStackTask:Z
    .annotation runtime Landroid/view/ViewDebug$ExportedProperty;
        category = "recents"
    .end annotation
.end field

.field public isSystemApp:Z
    .annotation runtime Landroid/view/ViewDebug$ExportedProperty;
        category = "recents"
    .end annotation
.end field

.field public key:Lcom/android/systemui/shared/recents/model/Task$TaskKey;
    .annotation runtime Landroid/view/ViewDebug$ExportedProperty;
        deepExport = true
        prefix = "key_"
    .end annotation
.end field

.field private mCallbacks:Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList<",
            "Lcom/android/systemui/shared/recents/model/Task$TaskCallbacks;",
            ">;"
        }
    .end annotation
.end field

.field public resizeMode:I
    .annotation runtime Landroid/view/ViewDebug$ExportedProperty;
        category = "recents"
    .end annotation
.end field

.field public taskDescription:Landroid/app/ActivityManager$TaskDescription;

.field public temporarySortIndexInStack:I

.field public thumbnail:Lcom/android/systemui/shared/recents/model/ThumbnailData;

.field public title:Ljava/lang/String;
    .annotation runtime Landroid/view/ViewDebug$ExportedProperty;
        category = "recents"
    .end annotation
.end field

.field public titleDescription:Ljava/lang/String;
    .annotation runtime Landroid/view/ViewDebug$ExportedProperty;
        category = "recents"
    .end annotation
.end field

.field public topActivity:Landroid/content/ComponentName;
    .annotation runtime Landroid/view/ViewDebug$ExportedProperty;
        category = "recents"
    .end annotation
.end field

.field public useLightOnPrimaryColor:Z
    .annotation runtime Landroid/view/ViewDebug$ExportedProperty;
        category = "recents"
    .end annotation
.end field


# direct methods
.method public constructor <init>()V
    .registers 2

    .line 178
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 176
    new-instance v0, Ljava/util/ArrayList;

    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    iput-object v0, p0, Lcom/android/systemui/shared/recents/model/Task;->mCallbacks:Ljava/util/ArrayList;

    .line 180
    return-void
.end method

.method public constructor <init>(Lcom/android/systemui/shared/recents/model/Task$TaskKey;Landroid/graphics/drawable/Drawable;Lcom/android/systemui/shared/recents/model/ThumbnailData;Ljava/lang/String;Ljava/lang/String;IIZZZZLandroid/app/ActivityManager$TaskDescription;ILandroid/content/ComponentName;Z)V
    .registers 32
    .param p1, "key"    # Lcom/android/systemui/shared/recents/model/Task$TaskKey;
    .param p2, "icon"    # Landroid/graphics/drawable/Drawable;
    .param p3, "thumbnail"    # Lcom/android/systemui/shared/recents/model/ThumbnailData;
    .param p4, "title"    # Ljava/lang/String;
    .param p5, "titleDescription"    # Ljava/lang/String;
    .param p6, "colorPrimary"    # I
    .param p7, "colorBackground"    # I
    .param p8, "isLaunchTarget"    # Z
    .param p9, "isStackTask"    # Z
    .param p10, "isSystemApp"    # Z
    .param p11, "isDockable"    # Z
    .param p12, "taskDescription"    # Landroid/app/ActivityManager$TaskDescription;
    .param p13, "resizeMode"    # I
    .param p14, "topActivity"    # Landroid/content/ComponentName;
    .param p15, "isLocked"    # Z

    move-object/from16 v0, p0

    .line 186
    invoke-direct/range {p0 .. p0}, Ljava/lang/Object;-><init>()V

    .line 176
    new-instance v1, Ljava/util/ArrayList;

    invoke-direct {v1}, Ljava/util/ArrayList;-><init>()V

    iput-object v1, v0, Lcom/android/systemui/shared/recents/model/Task;->mCallbacks:Ljava/util/ArrayList;

    .line 187
    move-object/from16 v1, p1

    iput-object v1, v0, Lcom/android/systemui/shared/recents/model/Task;->key:Lcom/android/systemui/shared/recents/model/Task$TaskKey;

    .line 188
    move-object/from16 v2, p2

    iput-object v2, v0, Lcom/android/systemui/shared/recents/model/Task;->icon:Landroid/graphics/drawable/Drawable;

    .line 189
    move-object/from16 v3, p3

    iput-object v3, v0, Lcom/android/systemui/shared/recents/model/Task;->thumbnail:Lcom/android/systemui/shared/recents/model/ThumbnailData;

    .line 190
    move-object/from16 v4, p4

    iput-object v4, v0, Lcom/android/systemui/shared/recents/model/Task;->title:Ljava/lang/String;

    .line 191
    move-object/from16 v5, p5

    iput-object v5, v0, Lcom/android/systemui/shared/recents/model/Task;->titleDescription:Ljava/lang/String;

    .line 192
    move/from16 v6, p6

    iput v6, v0, Lcom/android/systemui/shared/recents/model/Task;->colorPrimary:I

    .line 193
    move/from16 v7, p7

    iput v7, v0, Lcom/android/systemui/shared/recents/model/Task;->colorBackground:I

    .line 194
    iget v8, v0, Lcom/android/systemui/shared/recents/model/Task;->colorPrimary:I

    const/4 v9, -0x1

    invoke-static {v8, v9}, Lcom/android/systemui/shared/recents/utilities/Utilities;->computeContrastBetweenColors(II)F

    move-result v8

    const/high16 v9, 0x40400000    # 3.0f

    cmpl-float v8, v8, v9

    if-lez v8, :cond_37

    const/4 v8, 0x1

    goto :goto_38

    :cond_37
    const/4 v8, 0x0

    :goto_38
    iput-boolean v8, v0, Lcom/android/systemui/shared/recents/model/Task;->useLightOnPrimaryColor:Z

    .line 196
    move-object/from16 v8, p12

    iput-object v8, v0, Lcom/android/systemui/shared/recents/model/Task;->taskDescription:Landroid/app/ActivityManager$TaskDescription;

    .line 197
    move/from16 v9, p8

    iput-boolean v9, v0, Lcom/android/systemui/shared/recents/model/Task;->isLaunchTarget:Z

    .line 198
    move/from16 v10, p9

    iput-boolean v10, v0, Lcom/android/systemui/shared/recents/model/Task;->isStackTask:Z

    .line 199
    move/from16 v11, p10

    iput-boolean v11, v0, Lcom/android/systemui/shared/recents/model/Task;->isSystemApp:Z

    .line 200
    move/from16 v12, p11

    iput-boolean v12, v0, Lcom/android/systemui/shared/recents/model/Task;->isDockable:Z

    .line 201
    move/from16 v13, p13

    iput v13, v0, Lcom/android/systemui/shared/recents/model/Task;->resizeMode:I

    .line 202
    move-object/from16 v14, p14

    iput-object v14, v0, Lcom/android/systemui/shared/recents/model/Task;->topActivity:Landroid/content/ComponentName;

    .line 203
    move/from16 v15, p15

    iput-boolean v15, v0, Lcom/android/systemui/shared/recents/model/Task;->isLocked:Z

    .line 204
    return-void
.end method


# virtual methods
.method public addCallback(Lcom/android/systemui/shared/recents/model/Task$TaskCallbacks;)V
    .registers 3
    .param p1, "cb"    # Lcom/android/systemui/shared/recents/model/Task$TaskCallbacks;

    .line 232
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/Task;->mCallbacks:Ljava/util/ArrayList;

    invoke-virtual {v0, p1}, Ljava/util/ArrayList;->contains(Ljava/lang/Object;)Z

    move-result v0

    if-nez v0, :cond_d

    .line 233
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/Task;->mCallbacks:Ljava/util/ArrayList;

    invoke-virtual {v0, p1}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    .line 235
    :cond_d
    return-void
.end method

.method public copyFrom(Lcom/android/systemui/shared/recents/model/Task;)V
    .registers 3
    .param p1, "o"    # Lcom/android/systemui/shared/recents/model/Task;

    .line 210
    iget-object v0, p1, Lcom/android/systemui/shared/recents/model/Task;->key:Lcom/android/systemui/shared/recents/model/Task$TaskKey;

    iput-object v0, p0, Lcom/android/systemui/shared/recents/model/Task;->key:Lcom/android/systemui/shared/recents/model/Task$TaskKey;

    .line 211
    iget-object v0, p1, Lcom/android/systemui/shared/recents/model/Task;->icon:Landroid/graphics/drawable/Drawable;

    iput-object v0, p0, Lcom/android/systemui/shared/recents/model/Task;->icon:Landroid/graphics/drawable/Drawable;

    .line 212
    iget-object v0, p1, Lcom/android/systemui/shared/recents/model/Task;->thumbnail:Lcom/android/systemui/shared/recents/model/ThumbnailData;

    iput-object v0, p0, Lcom/android/systemui/shared/recents/model/Task;->thumbnail:Lcom/android/systemui/shared/recents/model/ThumbnailData;

    .line 213
    iget-object v0, p1, Lcom/android/systemui/shared/recents/model/Task;->title:Ljava/lang/String;

    iput-object v0, p0, Lcom/android/systemui/shared/recents/model/Task;->title:Ljava/lang/String;

    .line 214
    iget-object v0, p1, Lcom/android/systemui/shared/recents/model/Task;->titleDescription:Ljava/lang/String;

    iput-object v0, p0, Lcom/android/systemui/shared/recents/model/Task;->titleDescription:Ljava/lang/String;

    .line 215
    iget v0, p1, Lcom/android/systemui/shared/recents/model/Task;->colorPrimary:I

    iput v0, p0, Lcom/android/systemui/shared/recents/model/Task;->colorPrimary:I

    .line 216
    iget v0, p1, Lcom/android/systemui/shared/recents/model/Task;->colorBackground:I

    iput v0, p0, Lcom/android/systemui/shared/recents/model/Task;->colorBackground:I

    .line 217
    iget-boolean v0, p1, Lcom/android/systemui/shared/recents/model/Task;->useLightOnPrimaryColor:Z

    iput-boolean v0, p0, Lcom/android/systemui/shared/recents/model/Task;->useLightOnPrimaryColor:Z

    .line 218
    iget-object v0, p1, Lcom/android/systemui/shared/recents/model/Task;->taskDescription:Landroid/app/ActivityManager$TaskDescription;

    iput-object v0, p0, Lcom/android/systemui/shared/recents/model/Task;->taskDescription:Landroid/app/ActivityManager$TaskDescription;

    .line 219
    iget-boolean v0, p1, Lcom/android/systemui/shared/recents/model/Task;->isLaunchTarget:Z

    iput-boolean v0, p0, Lcom/android/systemui/shared/recents/model/Task;->isLaunchTarget:Z

    .line 220
    iget-boolean v0, p1, Lcom/android/systemui/shared/recents/model/Task;->isStackTask:Z

    iput-boolean v0, p0, Lcom/android/systemui/shared/recents/model/Task;->isStackTask:Z

    .line 221
    iget-boolean v0, p1, Lcom/android/systemui/shared/recents/model/Task;->isSystemApp:Z

    iput-boolean v0, p0, Lcom/android/systemui/shared/recents/model/Task;->isSystemApp:Z

    .line 222
    iget-boolean v0, p1, Lcom/android/systemui/shared/recents/model/Task;->isDockable:Z

    iput-boolean v0, p0, Lcom/android/systemui/shared/recents/model/Task;->isDockable:Z

    .line 223
    iget v0, p1, Lcom/android/systemui/shared/recents/model/Task;->resizeMode:I

    iput v0, p0, Lcom/android/systemui/shared/recents/model/Task;->resizeMode:I

    .line 224
    iget-boolean v0, p1, Lcom/android/systemui/shared/recents/model/Task;->isLocked:Z

    iput-boolean v0, p0, Lcom/android/systemui/shared/recents/model/Task;->isLocked:Z

    .line 225
    iget-object v0, p1, Lcom/android/systemui/shared/recents/model/Task;->topActivity:Landroid/content/ComponentName;

    iput-object v0, p0, Lcom/android/systemui/shared/recents/model/Task;->topActivity:Landroid/content/ComponentName;

    .line 226
    return-void
.end method

.method public dump(Ljava/lang/String;Ljava/io/PrintWriter;)V
    .registers 4
    .param p1, "prefix"    # Ljava/lang/String;
    .param p2, "writer"    # Ljava/io/PrintWriter;

    .line 294
    invoke-virtual {p2, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/Task;->key:Lcom/android/systemui/shared/recents/model/Task$TaskKey;

    invoke-virtual {p2, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/Object;)V

    .line 295
    iget-boolean v0, p0, Lcom/android/systemui/shared/recents/model/Task;->isDockable:Z

    if-nez v0, :cond_11

    .line 296
    const-string v0, " dockable=N"

    invoke-virtual {p2, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    .line 298
    :cond_11
    iget-boolean v0, p0, Lcom/android/systemui/shared/recents/model/Task;->isLaunchTarget:Z

    if-eqz v0, :cond_1a

    .line 299
    const-string v0, " launchTarget=Y"

    invoke-virtual {p2, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    .line 301
    :cond_1a
    iget-boolean v0, p0, Lcom/android/systemui/shared/recents/model/Task;->isLocked:Z

    if-eqz v0, :cond_23

    .line 302
    const-string v0, " locked=Y"

    invoke-virtual {p2, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    .line 304
    :cond_23
    const-string v0, " "

    invoke-virtual {p2, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/Task;->title:Ljava/lang/String;

    invoke-virtual {p2, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    .line 305
    invoke-virtual {p2}, Ljava/io/PrintWriter;->println()V

    .line 306
    return-void
.end method

.method public equals(Ljava/lang/Object;)Z
    .registers 5
    .param p1, "o"    # Ljava/lang/Object;

    .line 284
    move-object v0, p1

    check-cast v0, Lcom/android/systemui/shared/recents/model/Task;

    .line 285
    .local v0, "t":Lcom/android/systemui/shared/recents/model/Task;
    iget-object v1, p0, Lcom/android/systemui/shared/recents/model/Task;->key:Lcom/android/systemui/shared/recents/model/Task$TaskKey;

    iget-object v2, v0, Lcom/android/systemui/shared/recents/model/Task;->key:Lcom/android/systemui/shared/recents/model/Task$TaskKey;

    invoke-virtual {v1, v2}, Lcom/android/systemui/shared/recents/model/Task$TaskKey;->equals(Ljava/lang/Object;)Z

    move-result v1

    return v1
.end method

.method public getTopComponent()Landroid/content/ComponentName;
    .registers 2

    .line 276
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/Task;->topActivity:Landroid/content/ComponentName;

    if-eqz v0, :cond_7

    .line 277
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/Task;->topActivity:Landroid/content/ComponentName;

    goto :goto_f

    .line 278
    :cond_7
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/Task;->key:Lcom/android/systemui/shared/recents/model/Task$TaskKey;

    iget-object v0, v0, Lcom/android/systemui/shared/recents/model/Task$TaskKey;->baseIntent:Landroid/content/Intent;

    invoke-virtual {v0}, Landroid/content/Intent;->getComponent()Landroid/content/ComponentName;

    move-result-object v0

    .line 276
    :goto_f
    return-object v0
.end method

.method public notifyTaskDataLoaded(Lcom/android/systemui/shared/recents/model/ThumbnailData;Landroid/graphics/drawable/Drawable;)V
    .registers 6
    .param p1, "thumbnailData"    # Lcom/android/systemui/shared/recents/model/ThumbnailData;
    .param p2, "applicationIcon"    # Landroid/graphics/drawable/Drawable;

    .line 255
    iput-object p2, p0, Lcom/android/systemui/shared/recents/model/Task;->icon:Landroid/graphics/drawable/Drawable;

    .line 256
    iput-object p1, p0, Lcom/android/systemui/shared/recents/model/Task;->thumbnail:Lcom/android/systemui/shared/recents/model/ThumbnailData;

    .line 257
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/Task;->mCallbacks:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v0

    .line 258
    .local v0, "callbackCount":I
    const/4 v1, 0x0

    .line 258
    .local v1, "i":I
    :goto_b
    if-ge v1, v0, :cond_1b

    .line 259
    iget-object v2, p0, Lcom/android/systemui/shared/recents/model/Task;->mCallbacks:Ljava/util/ArrayList;

    invoke-virtual {v2, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lcom/android/systemui/shared/recents/model/Task$TaskCallbacks;

    invoke-interface {v2, p0, p1}, Lcom/android/systemui/shared/recents/model/Task$TaskCallbacks;->onTaskDataLoaded(Lcom/android/systemui/shared/recents/model/Task;Lcom/android/systemui/shared/recents/model/ThumbnailData;)V

    .line 258
    add-int/lit8 v1, v1, 0x1

    goto :goto_b

    .line 261
    .end local v1    # "i":I
    :cond_1b
    return-void
.end method

.method public notifyTaskDataUnloaded(Landroid/graphics/drawable/Drawable;)V
    .registers 4
    .param p1, "defaultApplicationIcon"    # Landroid/graphics/drawable/Drawable;

    .line 265
    iput-object p1, p0, Lcom/android/systemui/shared/recents/model/Task;->icon:Landroid/graphics/drawable/Drawable;

    .line 266
    const/4 v0, 0x0

    iput-object v0, p0, Lcom/android/systemui/shared/recents/model/Task;->thumbnail:Lcom/android/systemui/shared/recents/model/ThumbnailData;

    .line 267
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/Task;->mCallbacks:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v0

    add-int/lit8 v0, v0, -0x1

    .line 267
    .local v0, "i":I
    :goto_d
    if-ltz v0, :cond_1d

    .line 268
    iget-object v1, p0, Lcom/android/systemui/shared/recents/model/Task;->mCallbacks:Ljava/util/ArrayList;

    invoke-virtual {v1, v0}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/android/systemui/shared/recents/model/Task$TaskCallbacks;

    invoke-interface {v1}, Lcom/android/systemui/shared/recents/model/Task$TaskCallbacks;->onTaskDataUnloaded()V

    .line 267
    add-int/lit8 v0, v0, -0x1

    goto :goto_d

    .line 270
    .end local v0    # "i":I
    :cond_1d
    return-void
.end method

.method public removeCallback(Lcom/android/systemui/shared/recents/model/Task$TaskCallbacks;)V
    .registers 3
    .param p1, "cb"    # Lcom/android/systemui/shared/recents/model/Task$TaskCallbacks;

    .line 241
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/Task;->mCallbacks:Ljava/util/ArrayList;

    invoke-virtual {v0, p1}, Ljava/util/ArrayList;->remove(Ljava/lang/Object;)Z

    .line 242
    return-void
.end method

.method public setWindowingMode(I)V
    .registers 5
    .param p1, "windowingMode"    # I

    .line 246
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/Task;->key:Lcom/android/systemui/shared/recents/model/Task$TaskKey;

    invoke-virtual {v0, p1}, Lcom/android/systemui/shared/recents/model/Task$TaskKey;->setWindowingMode(I)V

    .line 247
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/Task;->mCallbacks:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v0

    .line 248
    .local v0, "callbackCount":I
    const/4 v1, 0x0

    .line 248
    .local v1, "i":I
    :goto_c
    if-ge v1, v0, :cond_1c

    .line 249
    iget-object v2, p0, Lcom/android/systemui/shared/recents/model/Task;->mCallbacks:Ljava/util/ArrayList;

    invoke-virtual {v2, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Lcom/android/systemui/shared/recents/model/Task$TaskCallbacks;

    invoke-interface {v2}, Lcom/android/systemui/shared/recents/model/Task$TaskCallbacks;->onTaskWindowingModeChanged()V

    .line 248
    add-int/lit8 v1, v1, 0x1

    goto :goto_c

    .line 251
    .end local v1    # "i":I
    :cond_1c
    return-void
.end method

.method public toString()Ljava/lang/String;
    .registers 3

    .line 290
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v1, "["

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v1, p0, Lcom/android/systemui/shared/recents/model/Task;->key:Lcom/android/systemui/shared/recents/model/Task$TaskKey;

    invoke-virtual {v1}, Lcom/android/systemui/shared/recents/model/Task$TaskKey;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string v1, "] "

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v1, p0, Lcom/android/systemui/shared/recents/model/Task;->title:Ljava/lang/String;

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method
