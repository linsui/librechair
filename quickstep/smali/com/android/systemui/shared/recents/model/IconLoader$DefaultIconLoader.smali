.class public Lcom/android/systemui/shared/recents/model/IconLoader$DefaultIconLoader;
.super Lcom/android/systemui/shared/recents/model/IconLoader;
.source "IconLoader.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/android/systemui/shared/recents/model/IconLoader;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x9
    name = "DefaultIconLoader"
.end annotation


# instance fields
.field private final mDefaultIcon:Landroid/graphics/drawable/BitmapDrawable;

.field private final mDrawableFactory:Landroid/util/IconDrawableFactory;


# direct methods
.method public constructor <init>(Landroid/content/Context;Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;Landroid/util/LruCache;)V
    .registers 7
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

    .line 168
    .local p2, "iconCache":Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;, "Lcom/android/systemui/shared/recents/model/TaskKeyLruCache<Landroid/graphics/drawable/Drawable;>;"
    .local p3, "activityInfoCache":Landroid/util/LruCache;, "Landroid/util/LruCache<Landroid/content/ComponentName;Landroid/content/pm/ActivityInfo;>;"
    invoke-direct {p0, p1, p2, p3}, Lcom/android/systemui/shared/recents/model/IconLoader;-><init>(Landroid/content/Context;Lcom/android/systemui/shared/recents/model/TaskKeyLruCache;Landroid/util/LruCache;)V

    .line 171
    sget-object v0, Landroid/graphics/Bitmap$Config;->ALPHA_8:Landroid/graphics/Bitmap$Config;

    const/4 v1, 0x1

    invoke-static {v1, v1, v0}, Landroid/graphics/Bitmap;->createBitmap(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;

    move-result-object v0

    .line 172
    .local v0, "icon":Landroid/graphics/Bitmap;
    const/4 v1, 0x0

    invoke-virtual {v0, v1}, Landroid/graphics/Bitmap;->eraseColor(I)V

    .line 173
    new-instance v1, Landroid/graphics/drawable/BitmapDrawable;

    invoke-virtual {p1}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    move-result-object v2

    invoke-direct {v1, v2, v0}, Landroid/graphics/drawable/BitmapDrawable;-><init>(Landroid/content/res/Resources;Landroid/graphics/Bitmap;)V

    iput-object v1, p0, Lcom/android/systemui/shared/recents/model/IconLoader$DefaultIconLoader;->mDefaultIcon:Landroid/graphics/drawable/BitmapDrawable;

    .line 174
    invoke-static {p1}, Landroid/util/IconDrawableFactory;->newInstance(Landroid/content/Context;)Landroid/util/IconDrawableFactory;

    move-result-object v1

    iput-object v1, p0, Lcom/android/systemui/shared/recents/model/IconLoader$DefaultIconLoader;->mDrawableFactory:Landroid/util/IconDrawableFactory;

    .line 175
    return-void
.end method


# virtual methods
.method protected createBadgedDrawable(Landroid/graphics/drawable/Drawable;ILandroid/app/ActivityManager$TaskDescription;)Landroid/graphics/drawable/Drawable;
    .registers 6
    .param p1, "icon"    # Landroid/graphics/drawable/Drawable;
    .param p2, "userId"    # I
    .param p3, "desc"    # Landroid/app/ActivityManager$TaskDescription;

    .line 185
    invoke-static {}, Landroid/os/UserHandle;->myUserId()I

    move-result v0

    if-eq p2, v0, :cond_15

    .line 186
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/IconLoader$DefaultIconLoader;->mContext:Landroid/content/Context;

    invoke-virtual {v0}, Landroid/content/Context;->getPackageManager()Landroid/content/pm/PackageManager;

    move-result-object v0

    new-instance v1, Landroid/os/UserHandle;

    invoke-direct {v1, p2}, Landroid/os/UserHandle;-><init>(I)V

    invoke-virtual {v0, p1, v1}, Landroid/content/pm/PackageManager;->getUserBadgedIcon(Landroid/graphics/drawable/Drawable;Landroid/os/UserHandle;)Landroid/graphics/drawable/Drawable;

    move-result-object p1

    .line 188
    :cond_15
    return-object p1
.end method

.method protected getBadgedActivityIcon(Landroid/content/pm/ActivityInfo;ILandroid/app/ActivityManager$TaskDescription;)Landroid/graphics/drawable/Drawable;
    .registers 6
    .param p1, "info"    # Landroid/content/pm/ActivityInfo;
    .param p2, "userId"    # I
    .param p3, "desc"    # Landroid/app/ActivityManager$TaskDescription;

    .line 194
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/IconLoader$DefaultIconLoader;->mDrawableFactory:Landroid/util/IconDrawableFactory;

    iget-object v1, p1, Landroid/content/pm/ActivityInfo;->applicationInfo:Landroid/content/pm/ApplicationInfo;

    invoke-virtual {v0, p1, v1, p2}, Landroid/util/IconDrawableFactory;->getBadgedIcon(Landroid/content/pm/PackageItemInfo;Landroid/content/pm/ApplicationInfo;I)Landroid/graphics/drawable/Drawable;

    move-result-object v0

    return-object v0
.end method

.method public getDefaultIcon(I)Landroid/graphics/drawable/Drawable;
    .registers 3
    .param p1, "userId"    # I

    .line 179
    iget-object v0, p0, Lcom/android/systemui/shared/recents/model/IconLoader$DefaultIconLoader;->mDefaultIcon:Landroid/graphics/drawable/BitmapDrawable;

    return-object v0
.end method
