.class Lcom/android/systemui/shared/recents/utilities/Utilities$2;
.super Landroid/util/Property;
.source "Utilities.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/android/systemui/shared/recents/utilities/Utilities;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "Landroid/util/Property<",
        "Landroid/graphics/drawable/Drawable;",
        "Landroid/graphics/Rect;",
        ">;"
    }
.end annotation


# direct methods
.method constructor <init>(Ljava/lang/Class;Ljava/lang/String;)V
    .registers 3
    .param p2, "x1"    # Ljava/lang/String;

    .line 66
    .local p1, "x0":Ljava/lang/Class;, "Ljava/lang/Class<Landroid/graphics/Rect;>;"
    invoke-direct {p0, p1, p2}, Landroid/util/Property;-><init>(Ljava/lang/Class;Ljava/lang/String;)V

    return-void
.end method


# virtual methods
.method public get(Landroid/graphics/drawable/Drawable;)Landroid/graphics/Rect;
    .registers 3
    .param p1, "object"    # Landroid/graphics/drawable/Drawable;

    .line 74
    invoke-virtual {p1}, Landroid/graphics/drawable/Drawable;->getBounds()Landroid/graphics/Rect;

    move-result-object v0

    return-object v0
.end method

.method public bridge synthetic get(Ljava/lang/Object;)Ljava/lang/Object;
    .registers 2

    .line 66
    check-cast p1, Landroid/graphics/drawable/Drawable;

    invoke-virtual {p0, p1}, Lcom/android/systemui/shared/recents/utilities/Utilities$2;->get(Landroid/graphics/drawable/Drawable;)Landroid/graphics/Rect;

    move-result-object p1

    return-object p1
.end method

.method public set(Landroid/graphics/drawable/Drawable;Landroid/graphics/Rect;)V
    .registers 3
    .param p1, "object"    # Landroid/graphics/drawable/Drawable;
    .param p2, "bounds"    # Landroid/graphics/Rect;

    .line 69
    invoke-virtual {p1, p2}, Landroid/graphics/drawable/Drawable;->setBounds(Landroid/graphics/Rect;)V

    .line 70
    return-void
.end method

.method public bridge synthetic set(Ljava/lang/Object;Ljava/lang/Object;)V
    .registers 3

    .line 66
    check-cast p1, Landroid/graphics/drawable/Drawable;

    check-cast p2, Landroid/graphics/Rect;

    invoke-virtual {p0, p1, p2}, Lcom/android/systemui/shared/recents/utilities/Utilities$2;->set(Landroid/graphics/drawable/Drawable;Landroid/graphics/Rect;)V

    return-void
.end method
