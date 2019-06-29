.class public Lcom/android/systemui/shared/system/ActivityCompat;
.super Ljava/lang/Object;
.source "ActivityCompat.java"


# instance fields
.field private final mWrapped:Landroid/app/Activity;


# direct methods
.method public constructor <init>(Landroid/app/Activity;)V
    .registers 2
    .param p1, "activity"    # Landroid/app/Activity;

    .line 28
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 29
    iput-object p1, p0, Lcom/android/systemui/shared/system/ActivityCompat;->mWrapped:Landroid/app/Activity;

    .line 30
    return-void
.end method


# virtual methods
.method public encodeViewHierarchy(Ljava/io/ByteArrayOutputStream;)Z
    .registers 8
    .param p1, "out"    # Ljava/io/ByteArrayOutputStream;

    .line 43
    const/4 v0, 0x0

    .line 44
    .local v0, "view":Landroid/view/View;
    iget-object v1, p0, Lcom/android/systemui/shared/system/ActivityCompat;->mWrapped:Landroid/app/Activity;

    invoke-virtual {v1}, Landroid/app/Activity;->getWindow()Landroid/view/Window;

    move-result-object v1

    if-eqz v1, :cond_37

    iget-object v1, p0, Lcom/android/systemui/shared/system/ActivityCompat;->mWrapped:Landroid/app/Activity;

    .line 45
    invoke-virtual {v1}, Landroid/app/Activity;->getWindow()Landroid/view/Window;

    move-result-object v1

    invoke-virtual {v1}, Landroid/view/Window;->peekDecorView()Landroid/view/View;

    move-result-object v1

    if-eqz v1, :cond_37

    iget-object v1, p0, Lcom/android/systemui/shared/system/ActivityCompat;->mWrapped:Landroid/app/Activity;

    .line 46
    invoke-virtual {v1}, Landroid/app/Activity;->getWindow()Landroid/view/Window;

    move-result-object v1

    invoke-virtual {v1}, Landroid/view/Window;->peekDecorView()Landroid/view/View;

    move-result-object v1

    invoke-virtual {v1}, Landroid/view/View;->getViewRootImpl()Landroid/view/ViewRootImpl;

    move-result-object v1

    if-eqz v1, :cond_37

    .line 47
    iget-object v1, p0, Lcom/android/systemui/shared/system/ActivityCompat;->mWrapped:Landroid/app/Activity;

    invoke-virtual {v1}, Landroid/app/Activity;->getWindow()Landroid/view/Window;

    move-result-object v1

    invoke-virtual {v1}, Landroid/view/Window;->peekDecorView()Landroid/view/View;

    move-result-object v1

    invoke-virtual {v1}, Landroid/view/View;->getViewRootImpl()Landroid/view/ViewRootImpl;

    move-result-object v1

    invoke-virtual {v1}, Landroid/view/ViewRootImpl;->getView()Landroid/view/View;

    move-result-object v0

    .line 49
    :cond_37
    const/4 v1, 0x0

    if-nez v0, :cond_3b

    .line 50
    return v1

    .line 53
    :cond_3b
    new-instance v2, Landroid/view/ViewHierarchyEncoder;

    invoke-direct {v2, p1}, Landroid/view/ViewHierarchyEncoder;-><init>(Ljava/io/ByteArrayOutputStream;)V

    .line 54
    .local v2, "encoder":Landroid/view/ViewHierarchyEncoder;
    invoke-virtual {v0}, Landroid/view/View;->getLocationOnScreen()[I

    move-result-object v3

    .line 55
    .local v3, "location":[I
    const-string v4, "window:left"

    aget v1, v3, v1

    invoke-virtual {v2, v4, v1}, Landroid/view/ViewHierarchyEncoder;->addProperty(Ljava/lang/String;I)V

    .line 56
    const-string v1, "window:top"

    const/4 v4, 0x1

    aget v5, v3, v4

    invoke-virtual {v2, v1, v5}, Landroid/view/ViewHierarchyEncoder;->addProperty(Ljava/lang/String;I)V

    .line 57
    invoke-virtual {v0, v2}, Landroid/view/View;->encode(Landroid/view/ViewHierarchyEncoder;)V

    .line 58
    invoke-virtual {v2}, Landroid/view/ViewHierarchyEncoder;->endStream()V

    .line 59
    return v4
.end method

.method public registerRemoteAnimations(Lcom/android/systemui/shared/system/RemoteAnimationDefinitionCompat;)V
    .registers 4
    .param p1, "definition"    # Lcom/android/systemui/shared/system/RemoteAnimationDefinitionCompat;

    .line 36
    iget-object v0, p0, Lcom/android/systemui/shared/system/ActivityCompat;->mWrapped:Landroid/app/Activity;

    invoke-virtual {p1}, Lcom/android/systemui/shared/system/RemoteAnimationDefinitionCompat;->getWrapped()Landroid/view/RemoteAnimationDefinition;

    move-result-object v1

    invoke-virtual {v0, v1}, Landroid/app/Activity;->registerRemoteAnimations(Landroid/view/RemoteAnimationDefinition;)V

    .line 37
    return-void
.end method
