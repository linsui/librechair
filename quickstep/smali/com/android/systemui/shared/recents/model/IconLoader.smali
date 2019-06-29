.class public abstract Lcom/android/systemui/shared/recents/model/IconLoader;
.super Ljava/lang/Object;
.source "IconLoader.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/android/systemui/shared/recents/model/IconLoader$DefaultIconLoader;
    }
.end annotation


# static fields
.field private static final TAG:Ljava/lang/String; = "IconLoader"


# instance fields
.field protected final mActivityInfoCache:Landroid/util/LruCache;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Landroid/util/LruCache<",
            "Landroid/content/ComponentName;",
            "Landroid/content/pm/ActivityInfo;",
            ">;"
        }
    .end annotation
.end field

.field protected final mContext:Landroid/content/Context;

.field protected final mIconCache:Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Lcom/android/systemui/shared/recents/model/TaskKeyLruCache<",
            "Landroid/graphics/drawable/Drawable;",
            ">;"
        }
    .end annotation
.end field


# direct methods
.method public constructor <init>(Landroid/content/Context;Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;Landroid/util/LruCache;)V
    .registers 4
    .param p1, "context"    # Landroid/content/Context;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/content/Context;",
            "Lcom/android/systemui/shared/recents/model/TaskKeyLruCache<",
            "Landroid/graphics/drawable/Drawable;",
            ">;",
            "Landroid/util/LruCache<",
            "Landroid/content/ComponentName;",
            "Landroid/content/pm/ActivityInfo;",
            ">;)V"
        }
    .end annotation

    .line 46
    .local p2, "iconCache":Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;, "Lcom/android/systemui/shared/recents/model/TaskKeyLruCache<Landroid/graphics/drawable/Drawable;>;"
    .local p3, "activityInfoCache":Landroid/util/LruCache;, "Landroid/util/LruCache<Landroid/content/ComponentName;Landroid/content/pm/ActivityInfo;>;"
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 47
    iput-object p1, p0, Lcom/android/systemui/shared/recents/model/IconLoader;->mContext:Landroid/content/Context;

    .line 48
    iput-object p2, p0, Lcom/android/systemui/shared/recents/model/IconLoader;->mIconCache:Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;

    .line 49
    iput-object p3, p0, Lcom/android/systemui/shared/recents/model/IconLoader;->mActivityInfoCache:Landroid/util/LruCache;

    .line 50
    return-void
.end method


# virtual methods
.method protected abstract createBadgedDrawable(Landroid/graphics/drawable/Drawable;ILandroid/app/ActivityManager$TaskDescription;)Landroid/graphics/drawable/Drawable;
.end method

.method protected createDrawableFromBitmap(Landroid/graphics/Bitmap;ILandroid/app/ActivityManager$TaskDescription;)Landroid/graphics/drawable/Drawable;
    .registers 6
    .param p1, "icon"    # Landroid/graphics/Bitmap;
    .param p2, "userId"    # I
    .param p3, "desc"    # Landroid/app/ActivityManager$TaskDescription;

    .line 148
    new-instance v0, Landroid/graphics/drawable/BitmapDrawable;

    iget-object v1, p0, Lcom/android/systemui/shared/recents/model/IconLoader;->mContext:Landroid/content/Context;

    .line 149
    invoke-virtual {v1}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    move-result-object v1

    invoke-direct {v0, v1, p1}, Landroid/graphics/drawable/BitmapDrawable;-><init>(Landroid/content/res/Resources;Landroid/graphics/Bitmap;)V

    .line 148
    invoke-virtual {p0, v0, p2, p3}, Lcom/android/systemui/shared/recents/model/IconLoader;->createBadgedDrawable(Landroid/graphics/drawable/Drawable;ILandroid/app/ActivityManager$TaskDescription;)Landroid/graphics/drawable/Drawable;

    move-result-object v0

    return-object v0
.end method

.method public createNewIconForTask(Lcom/android/systemui/shared/recents/model/Task$TaskKey;Landroid/app/ActivityManager$TaskDescription;Z)Landroid/graphics/drawable/Drawable;
    .registers 11
    .param p1, "taskKey"    # Lcom/android/systemui/shared/recents/model/Task$TaskKey;
    .param p2, "desc"    # Landroid/app/ActivityManager$TaskDescription;
    .param p3, "returnDefault"    # Z

    .line 107
    iget v0, p1, Lcom/android/systemui/shared/recents/model/Task$TaskKey;->userId:I

    .line 108
    .local v0, "userId":I
    invoke-virtual {p2}, Landroid/app/ActivityManager$TaskDescription;->getInMemoryIcon()Landroid/graphics/Bitmap;

    move-result-object v1

    .line 109
    .local v1, "tdIcon":Landroid/graphics/Bitmap;
    if-eqz v1, :cond_d

    .line 110
    invoke-virtual {p0, v1, v0, p2}, Lcom/android/systemui/shared/recents/model/IconLoader;->createDrawableFromBitmap(Landroid/graphics/Bitmap;ILandroid/app/ActivityManager$TaskDescription;)Landroid/graphics/drawable/Drawable;

    move-result-object v2

    return-object v2

    .line 112
    :cond_d
    invoke-virtual {p2}, Landroid/app/ActivityManager$TaskDescription;->getIconResource()I

    move-result v2

    const/4 v3, 0x0

    if-eqz v2, :cond_3d

    .line 114
    :try_start_14
    iget-object v2, p0, Lcom/android/systemui/shared/recents/model/IconLoader;->mContext:Landroid/content/Context;

    invoke-virtual {v2}, Landroid/content/Context;->getPackageManager()Landroid/content/pm/PackageManager;

    move-result-object v2

    .line 115
    .local v2, "pm":Landroid/content/pm/PackageManager;
    invoke-virtual {p1}, Lcom/android/systemui/shared/recents/model/Task$TaskKey;->getPackageName()Ljava/lang/String;

    move-result-object v4

    const/high16 v5, 0x400000

    invoke-virtual {v2, v4, v5}, Landroid/content/pm/PackageManager;->getApplicationInfo(Ljava/lang/String;I)Landroid/content/pm/ApplicationInfo;

    move-result-object v4

    .line 117
    .local v4, "appInfo":Landroid/content/pm/ApplicationInfo;
    invoke-virtual {v2, v4}, Landroid/content/pm/PackageManager;->getResourcesForApplication(Landroid/content/pm/ApplicationInfo;)Landroid/content/res/Resources;

    move-result-object v5

    .line 118
    .local v5, "res":Landroid/content/res/Resources;
    invoke-virtual {p2}, Landroid/app/ActivityManager$TaskDescription;->getIconResource()I

    move-result v6

    invoke-virtual {v5, v6, v3}, Landroid/content/res/Resources;->getDrawable(ILandroid/content/res/Resources$Theme;)Landroid/graphics/drawable/Drawable;

    move-result-object v6

    invoke-virtual {p0, v6, v0, p2}, Lcom/android/systemui/shared/recents/model/IconLoader;->createBadgedDrawable(Landroid/graphics/drawable/Drawable;ILandroid/app/ActivityManager$TaskDescription;)Landroid/graphics/drawable/Drawable;

    move-result-object v6
    :try_end_34
    .catch Landroid/content/res/Resources$NotFoundException; {:try_start_14 .. :try_end_34} :catch_35
    .catch Landroid/content/pm/PackageManager$NameNotFoundException; {:try_start_14 .. :try_end_34} :catch_35

    return-object v6

    .line 120
    .end local v2    # "pm":Landroid/content/pm/PackageManager;
    .end local v4    # "appInfo":Landroid/content/pm/ApplicationInfo;
    .end local v5    # "res":Landroid/content/res/Resources;
    :catch_35
    move-exception v2

    .line 121
    .local v2, "e":Ljava/lang/Exception;
    const-string v4, "IconLoader"

    const-string v5, "Could not find icon drawable from resource"

    invoke-static {v4, v5, v2}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    .line 125
    .end local v2    # "e":Ljava/lang/Exception;
    :cond_3d
    nop

    .line 126
    invoke-virtual {p2}, Landroid/app/ActivityManager$TaskDescription;->getIconFilename()Ljava/lang/String;

    move-result-object v2

    .line 125
    invoke-static {v2, v0}, Landroid/app/ActivityManager$TaskDescription;->loadTaskDescriptionIcon(Ljava/lang/String;I)Landroid/graphics/Bitmap;

    move-result-object v1

    .line 127
    if-eqz v1, :cond_4d

    .line 128
    invoke-virtual {p0, v1, v0, p2}, Lcom/android/systemui/shared/recents/model/IconLoader;->createDrawableFromBitmap(Landroid/graphics/Bitmap;ILandroid/app/ActivityManager$TaskDescription;)Landroid/graphics/drawable/Drawable;

    move-result-object v2

    return-object v2

    .line 132
    :cond_4d
    invoke-virtual {p0, p1}, Lcom/android/systemui/shared/recents/model/IconLoader;->getAndUpdateActivityInfo(Lcom/android/systemui/shared/recents/model/Task$TaskKey;)Landroid/content/pm/ActivityInfo;

    move-result-object v2

    .line 133
    .local v2, "activityInfo":Landroid/content/pm/ActivityInfo;
    if-eqz v2, :cond_5a

    .line 134
    invoke-virtual {p0, v2, v0, p2}, Lcom/android/systemui/shared/recents/model/IconLoader;->getBadgedActivityIcon(Landroid/content/pm/ActivityInfo;ILandroid/app/ActivityManager$TaskDescription;)Landroid/graphics/drawable/Drawable;

    move-result-object v4

    .line 135
    .local v4, "icon":Landroid/graphics/drawable/Drawable;
    if-eqz v4, :cond_5a

    .line 136
    return-object v4

    .line 141
    .end local v4    # "icon":Landroid/graphics/drawable/Drawable;
    :cond_5a
    if-eqz p3, :cond_61

    invoke-virtual {p0, v0}, Lcom/android/systemui/shared/recents/model/IconLoader;->getDefaultIcon(I)Landroid/graphics/drawable/Drawable;

    move-result-object v3

    nop

    :cond_61
    return-object v3
.end method

.method public getAndInvalidateIfModified(Lcom/android/systemui/shared/recents/model/Task$TaskKey;Landroid/app/ActivityManager$TaskDescription;Z)Landroid/graphics/drawable/Drawable;
    .registers 6
    .param p1, "taskKey"    # Lcom/android/systemui/shared/recents/model/Task$TaskKey;
    .param p2, "td"    # Landroid/app/ActivityManager$TaskDescription;
    .param p3, "loadIfNotCached"    # Z

    .line 88
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/IconLoader;->mIconCache:Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;

    invoke-virtual {v0, p1}, Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;->getAndInvalidateIfModified(Lcom/android/systemui/shared/recents/model/Task$TaskKey;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Landroid/graphics/drawable/Drawable;

    .line 89
    .local v0, "icon":Landroid/graphics/drawable/Drawable;
    if-eqz v0, :cond_b

    .line 90
    return-object v0

    .line 93
    :cond_b
    if-eqz p3, :cond_1a

    .line 94
    const/4 v1, 0x0

    invoke-virtual {p0, p1, p2, v1}, Lcom/android/systemui/shared/recents/model/IconLoader;->createNewIconForTask(Lcom/android/systemui/shared/recents/model/Task$TaskKey;Landroid/app/ActivityManager$TaskDescription;Z)Landroid/graphics/drawable/Drawable;

    move-result-object v0

    .line 95
    if-eqz v0, :cond_1a

    .line 96
    iget-object v1, p0, Lcom/android/systemui/shared/recents/model/IconLoader;->mIconCache:Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;

    invoke-virtual {v1, p1, v0}, Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;->put(Lcom/android/systemui/shared/recents/model/Task$TaskKey;Ljava/lang/Object;)V

    .line 97
    return-object v0

    .line 102
    :cond_1a
    const/4 v1, 0x0

    return-object v1
.end method

.method public getAndUpdateActivityInfo(Lcom/android/systemui/shared/recents/model/Task$TaskKey;)Landroid/content/pm/ActivityInfo;
    .registers 7
    .param p1, "taskKey"    # Lcom/android/systemui/shared/recents/model/Task$TaskKey;

    .line 59
    invoke-virtual {p1}, Lcom/android/systemui/shared/recents/model/Task$TaskKey;->getComponent()Landroid/content/ComponentName;

    move-result-object v0

    .line 60
    .local v0, "cn":Landroid/content/ComponentName;
    iget-object v1, p0, Lcom/android/systemui/shared/recents/model/IconLoader;->mActivityInfoCache:Landroid/util/LruCache;

    invoke-virtual {v1, v0}, Landroid/util/LruCache;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Landroid/content/pm/ActivityInfo;

    .line 61
    .local v1, "activityInfo":Landroid/content/pm/ActivityInfo;
    if-nez v1, :cond_43

    .line 62
    invoke-static {}, Lcom/android/systemui/shared/system/PackageManagerWrapper;->getInstance()Lcom/android/systemui/shared/system/PackageManagerWrapper;

    move-result-object v2

    iget v3, p1, Lcom/android/systemui/shared/recents/model/Task$TaskKey;->userId:I

    invoke-virtual {v2, v0, v3}, Lcom/android/systemui/shared/system/PackageManagerWrapper;->getActivityInfo(Landroid/content/ComponentName;I)Landroid/content/pm/ActivityInfo;

    move-result-object v1

    .line 63
    if-eqz v0, :cond_23

    if-nez v1, :cond_1d

    goto :goto_23

    .line 68
    :cond_1d
    iget-object v2, p0, Lcom/android/systemui/shared/recents/model/IconLoader;->mActivityInfoCache:Landroid/util/LruCache;

    invoke-virtual {v2, v0, v1}, Landroid/util/LruCache;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    goto :goto_43

    .line 64
    :cond_23
    :goto_23
    const-string v2, "IconLoader"

    new-instance v3, Ljava/lang/StringBuilder;

    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V

    const-string v4, "Unexpected null component name or activity info: "

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v3, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    const-string v4, ", "

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v3, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v3

    invoke-static {v2, v3}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    .line 66
    const/4 v2, 0x0

    return-object v2

    .line 70
    :cond_43
    :goto_43
    return-object v1
.end method

.method protected abstract getBadgedActivityIcon(Landroid/content/pm/ActivityInfo;ILandroid/app/ActivityManager$TaskDescription;)Landroid/graphics/drawable/Drawable;
.end method

.method public abstract getDefaultIcon(I)Landroid/graphics/drawable/Drawable;
.end method

.method public getIcon(Lcom/android/systemui/shared/recents/model/Task;)Landroid/graphics/drawable/Drawable;
    .registers 6
    .param p1, "t"    # Lcom/android/systemui/shared/recents/model/Task;

    .line 74
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/IconLoader;->mIconCache:Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;

    iget-object v1, p1, Lcom/android/systemui/shared/recents/model/Task;->key:Lcom/android/systemui/shared/recents/model/Task$TaskKey;

    invoke-virtual {v0, v1}, Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;->get(Lcom/android/systemui/shared/recents/model/Task$TaskKey;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Landroid/graphics/drawable/Drawable;

    .line 75
    .local v0, "cachedIcon":Landroid/graphics/drawable/Drawable;
    if-nez v0, :cond_1c

    .line 76
    iget-object v1, p1, Lcom/android/systemui/shared/recents/model/Task;->key:Lcom/android/systemui/shared/recents/model/Task$TaskKey;

    iget-object v2, p1, Lcom/android/systemui/shared/recents/model/Task;->taskDescription:Landroid/app/ActivityManager$TaskDescription;

    const/4 v3, 0x1

    invoke-virtual {p0, v1, v2, v3}, Lcom/android/systemui/shared/recents/model/IconLoader;->createNewIconForTask(Lcom/android/systemui/shared/recents/model/Task$TaskKey;Landroid/app/ActivityManager$TaskDescription;Z)Landroid/graphics/drawable/Drawable;

    move-result-object v0

    .line 77
    iget-object v1, p0, Lcom/android/systemui/shared/recents/model/IconLoader;->mIconCache:Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;

    iget-object v2, p1, Lcom/android/systemui/shared/recents/model/Task;->key:Lcom/android/systemui/shared/recents/model/Task$TaskKey;

    invoke-virtual {v1, v2, v0}, Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;->put(Lcom/android/systemui/shared/recents/model/Task$TaskKey;Ljava/lang/Object;)V

    .line 79
    :cond_1c
    return-object v0
.end method
