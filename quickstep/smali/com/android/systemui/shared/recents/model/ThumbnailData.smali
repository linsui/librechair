.class public Lcom/android/systemui/shared/recents/model/ThumbnailData;
.super Ljava/lang/Object;
.source "ThumbnailData.java"


# instance fields
.field public insets:Landroid/graphics/Rect;

.field public isRealSnapshot:Z

.field public isTranslucent:Z

.field public orientation:I

.field public reducedResolution:Z

.field public scale:F

.field public systemUiVisibility:I

.field public final thumbnail:Landroid/graphics/Bitmap;

.field public windowingMode:I


# direct methods
.method public constructor <init>()V
    .registers 3

    .line 41
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 42
    const/4 v0, 0x0

    iput-object v0, p0, Lcom/android/systemui/shared/recents/model/ThumbnailData;->thumbnail:Landroid/graphics/Bitmap;

    .line 43
    const/4 v0, 0x0

    iput v0, p0, Lcom/android/systemui/shared/recents/model/ThumbnailData;->orientation:I

    .line 44
    new-instance v1, Landroid/graphics/Rect;

    invoke-direct {v1}, Landroid/graphics/Rect;-><init>()V

    iput-object v1, p0, Lcom/android/systemui/shared/recents/model/ThumbnailData;->insets:Landroid/graphics/Rect;

    .line 45
    iput-boolean v0, p0, Lcom/android/systemui/shared/recents/model/ThumbnailData;->reducedResolution:Z

    .line 46
    const/high16 v1, 0x3f800000    # 1.0f

    iput v1, p0, Lcom/android/systemui/shared/recents/model/ThumbnailData;->scale:F

    .line 47
    const/4 v1, 0x1

    iput-boolean v1, p0, Lcom/android/systemui/shared/recents/model/ThumbnailData;->isRealSnapshot:Z

    .line 48
    iput-boolean v0, p0, Lcom/android/systemui/shared/recents/model/ThumbnailData;->isTranslucent:Z

    .line 49
    iput v0, p0, Lcom/android/systemui/shared/recents/model/ThumbnailData;->windowingMode:I

    .line 50
    iput v0, p0, Lcom/android/systemui/shared/recents/model/ThumbnailData;->systemUiVisibility:I

    .line 51
    return-void
.end method

.method public constructor <init>(Landroid/app/ActivityManager$TaskSnapshot;)V
    .registers 4
    .param p1, "snapshot"    # Landroid/app/ActivityManager$TaskSnapshot;

    .line 53
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 54
    invoke-virtual {p1}, Landroid/app/ActivityManager$TaskSnapshot;->getSnapshot()Landroid/graphics/GraphicBuffer;

    move-result-object v0

    invoke-static {v0}, Landroid/graphics/Bitmap;->createHardwareBitmap(Landroid/graphics/GraphicBuffer;)Landroid/graphics/Bitmap;

    move-result-object v0

    iput-object v0, p0, Lcom/android/systemui/shared/recents/model/ThumbnailData;->thumbnail:Landroid/graphics/Bitmap;

    .line 55
    new-instance v0, Landroid/graphics/Rect;

    invoke-virtual {p1}, Landroid/app/ActivityManager$TaskSnapshot;->getContentInsets()Landroid/graphics/Rect;

    move-result-object v1

    invoke-direct {v0, v1}, Landroid/graphics/Rect;-><init>(Landroid/graphics/Rect;)V

    iput-object v0, p0, Lcom/android/systemui/shared/recents/model/ThumbnailData;->insets:Landroid/graphics/Rect;

    .line 56
    invoke-virtual {p1}, Landroid/app/ActivityManager$TaskSnapshot;->getOrientation()I

    move-result v0

    iput v0, p0, Lcom/android/systemui/shared/recents/model/ThumbnailData;->orientation:I

    .line 57
    invoke-virtual {p1}, Landroid/app/ActivityManager$TaskSnapshot;->isReducedResolution()Z

    move-result v0

    iput-boolean v0, p0, Lcom/android/systemui/shared/recents/model/ThumbnailData;->reducedResolution:Z

    .line 58
    invoke-virtual {p1}, Landroid/app/ActivityManager$TaskSnapshot;->getScale()F

    move-result v0

    iput v0, p0, Lcom/android/systemui/shared/recents/model/ThumbnailData;->scale:F

    .line 59
    invoke-virtual {p1}, Landroid/app/ActivityManager$TaskSnapshot;->isRealSnapshot()Z

    move-result v0

    iput-boolean v0, p0, Lcom/android/systemui/shared/recents/model/ThumbnailData;->isRealSnapshot:Z

    .line 60
    invoke-virtual {p1}, Landroid/app/ActivityManager$TaskSnapshot;->isTranslucent()Z

    move-result v0

    iput-boolean v0, p0, Lcom/android/systemui/shared/recents/model/ThumbnailData;->isTranslucent:Z

    .line 61
    invoke-virtual {p1}, Landroid/app/ActivityManager$TaskSnapshot;->getWindowingMode()I

    move-result v0

    iput v0, p0, Lcom/android/systemui/shared/recents/model/ThumbnailData;->windowingMode:I

    .line 62
    invoke-virtual {p1}, Landroid/app/ActivityManager$TaskSnapshot;->getSystemUiVisibility()I

    move-result v0

    iput v0, p0, Lcom/android/systemui/shared/recents/model/ThumbnailData;->systemUiVisibility:I

    .line 63
    return-void
.end method
