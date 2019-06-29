.class public Lcom/android/systemui/shared/system/InputConsumerController;
.super Ljava/lang/Object;
.source "InputConsumerController.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/android/systemui/shared/system/InputConsumerController$InputEventReceiver;,
        Lcom/android/systemui/shared/system/InputConsumerController$RegistrationListener;,
        Lcom/android/systemui/shared/system/InputConsumerController$TouchListener;
    }
.end annotation


# static fields
.field private static final TAG:Ljava/lang/String;


# instance fields
.field private mInputEventReceiver:Lcom/android/systemui/shared/system/InputConsumerController$InputEventReceiver;

.field private mListener:Lcom/android/systemui/shared/system/InputConsumerController$TouchListener;

.field private final mName:Ljava/lang/String;

.field private mRegistrationListener:Lcom/android/systemui/shared/system/InputConsumerController$RegistrationListener;

.field private final mToken:Landroid/os/IBinder;

.field private final mWindowManager:Landroid/view/IWindowManager;


# direct methods
.method static constructor <clinit>()V
    .registers 1

    .line 42
    const-class v0, Lcom/android/systemui/shared/system/InputConsumerController;

    invoke-virtual {v0}, Ljava/lang/Class;->getSimpleName()Ljava/lang/String;

    move-result-object v0

    sput-object v0, Lcom/android/systemui/shared/system/InputConsumerController;->TAG:Ljava/lang/String;

    return-void
.end method

.method public constructor <init>(Landroid/view/IWindowManager;Ljava/lang/String;)V
    .registers 4
    .param p1, "windowManager"    # Landroid/view/IWindowManager;
    .param p2, "name"    # Ljava/lang/String;

    .line 94
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 95
    iput-object p1, p0, Lcom/android/systemui/shared/system/InputConsumerController;->mWindowManager:Landroid/view/IWindowManager;

    .line 96
    new-instance v0, Landroid/os/Binder;

    invoke-direct {v0}, Landroid/os/Binder;-><init>()V

    iput-object v0, p0, Lcom/android/systemui/shared/system/InputConsumerController;->mToken:Landroid/os/IBinder;

    .line 97
    iput-object p2, p0, Lcom/android/systemui/shared/system/InputConsumerController;->mName:Ljava/lang/String;

    .line 98
    return-void
.end method

.method static synthetic access$000(Lcom/android/systemui/shared/system/InputConsumerController;)Lcom/android/systemui/shared/system/InputConsumerController$TouchListener;
    .registers 2
    .param p0, "x0"    # Lcom/android/systemui/shared/system/InputConsumerController;

    .line 40
    iget-object v0, p0, Lcom/android/systemui/shared/system/InputConsumerController;->mListener:Lcom/android/systemui/shared/system/InputConsumerController$TouchListener;

    return-object v0
.end method

.method public static getPipInputConsumer()Lcom/android/systemui/shared/system/InputConsumerController;
    .registers 3

    .line 104
    new-instance v0, Lcom/android/systemui/shared/system/InputConsumerController;

    invoke-static {}, Landroid/view/WindowManagerGlobal;->getWindowManagerService()Landroid/view/IWindowManager;

    move-result-object v1

    const-string v2, "pip_input_consumer"

    invoke-direct {v0, v1, v2}, Lcom/android/systemui/shared/system/InputConsumerController;-><init>(Landroid/view/IWindowManager;Ljava/lang/String;)V

    return-object v0
.end method

.method public static getRecentsAnimationInputConsumer()Lcom/android/systemui/shared/system/InputConsumerController;
    .registers 3

    .line 112
    new-instance v0, Lcom/android/systemui/shared/system/InputConsumerController;

    invoke-static {}, Landroid/view/WindowManagerGlobal;->getWindowManagerService()Landroid/view/IWindowManager;

    move-result-object v1

    const-string v2, "recents_animation_input_consumer"

    invoke-direct {v0, v1, v2}, Lcom/android/systemui/shared/system/InputConsumerController;-><init>(Landroid/view/IWindowManager;Ljava/lang/String;)V

    return-object v0
.end method


# virtual methods
.method public dump(Ljava/io/PrintWriter;Ljava/lang/String;)V
    .registers 6
    .param p1, "pw"    # Ljava/io/PrintWriter;
    .param p2, "prefix"    # Ljava/lang/String;

    .line 180
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v0, p2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string v1, "  "

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    .line 181
    .local v0, "innerPrefix":Ljava/lang/String;
    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v1, p2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    sget-object v2, Lcom/android/systemui/shared/system/InputConsumerController;->TAG:Ljava/lang/String;

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-virtual {p1, v1}, Ljava/io/PrintWriter;->println(Ljava/lang/String;)V

    .line 182
    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v1, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string v2, "registered="

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v2, p0, Lcom/android/systemui/shared/system/InputConsumerController;->mInputEventReceiver:Lcom/android/systemui/shared/system/InputConsumerController$InputEventReceiver;

    if-eqz v2, :cond_38

    const/4 v2, 0x1

    goto :goto_39

    :cond_38
    const/4 v2, 0x0

    :goto_39
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-virtual {p1, v1}, Ljava/io/PrintWriter;->println(Ljava/lang/String;)V

    .line 183
    return-void
.end method

.method public isRegistered()Z
    .registers 2

    .line 139
    iget-object v0, p0, Lcom/android/systemui/shared/system/InputConsumerController;->mInputEventReceiver:Lcom/android/systemui/shared/system/InputConsumerController$InputEventReceiver;

    if-eqz v0, :cond_6

    const/4 v0, 0x1

    goto :goto_7

    :cond_6
    const/4 v0, 0x0

    :goto_7
    return v0
.end method

.method public registerInputConsumer()V
    .registers 5

    .line 146
    iget-object v0, p0, Lcom/android/systemui/shared/system/InputConsumerController;->mInputEventReceiver:Lcom/android/systemui/shared/system/InputConsumerController$InputEventReceiver;

    if-nez v0, :cond_37

    .line 147
    new-instance v0, Landroid/view/InputChannel;

    invoke-direct {v0}, Landroid/view/InputChannel;-><init>()V

    .line 149
    .local v0, "inputChannel":Landroid/view/InputChannel;
    :try_start_9
    iget-object v1, p0, Lcom/android/systemui/shared/system/InputConsumerController;->mWindowManager:Landroid/view/IWindowManager;

    iget-object v2, p0, Lcom/android/systemui/shared/system/InputConsumerController;->mName:Ljava/lang/String;

    invoke-interface {v1, v2}, Landroid/view/IWindowManager;->destroyInputConsumer(Ljava/lang/String;)Z

    .line 150
    iget-object v1, p0, Lcom/android/systemui/shared/system/InputConsumerController;->mWindowManager:Landroid/view/IWindowManager;

    iget-object v2, p0, Lcom/android/systemui/shared/system/InputConsumerController;->mToken:Landroid/os/IBinder;

    iget-object v3, p0, Lcom/android/systemui/shared/system/InputConsumerController;->mName:Ljava/lang/String;

    invoke-interface {v1, v2, v3, v0}, Landroid/view/IWindowManager;->createInputConsumer(Landroid/os/IBinder;Ljava/lang/String;Landroid/view/InputChannel;)V
    :try_end_19
    .catch Landroid/os/RemoteException; {:try_start_9 .. :try_end_19} :catch_1a

    .line 153
    goto :goto_22

    .line 151
    :catch_1a
    move-exception v1

    .line 152
    .local v1, "e":Landroid/os/RemoteException;
    sget-object v2, Lcom/android/systemui/shared/system/InputConsumerController;->TAG:Ljava/lang/String;

    const-string v3, "Failed to create input consumer"

    invoke-static {v2, v3, v1}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    .line 154
    .end local v1    # "e":Landroid/os/RemoteException;
    :goto_22
    new-instance v1, Lcom/android/systemui/shared/system/InputConsumerController$InputEventReceiver;

    invoke-static {}, Landroid/os/Looper;->myLooper()Landroid/os/Looper;

    move-result-object v2

    invoke-direct {v1, p0, v0, v2}, Lcom/android/systemui/shared/system/InputConsumerController$InputEventReceiver;-><init>(Lcom/android/systemui/shared/system/InputConsumerController;Landroid/view/InputChannel;Landroid/os/Looper;)V

    iput-object v1, p0, Lcom/android/systemui/shared/system/InputConsumerController;->mInputEventReceiver:Lcom/android/systemui/shared/system/InputConsumerController$InputEventReceiver;

    .line 155
    iget-object v1, p0, Lcom/android/systemui/shared/system/InputConsumerController;->mRegistrationListener:Lcom/android/systemui/shared/system/InputConsumerController$RegistrationListener;

    if-eqz v1, :cond_37

    .line 156
    iget-object v1, p0, Lcom/android/systemui/shared/system/InputConsumerController;->mRegistrationListener:Lcom/android/systemui/shared/system/InputConsumerController$RegistrationListener;

    const/4 v2, 0x1

    invoke-interface {v1, v2}, Lcom/android/systemui/shared/system/InputConsumerController$RegistrationListener;->onRegistrationChanged(Z)V

    .line 159
    .end local v0    # "inputChannel":Landroid/view/InputChannel;
    :cond_37
    return-void
.end method

.method public setRegistrationListener(Lcom/android/systemui/shared/system/InputConsumerController$RegistrationListener;)V
    .registers 4
    .param p1, "listener"    # Lcom/android/systemui/shared/system/InputConsumerController$RegistrationListener;

    .line 127
    iput-object p1, p0, Lcom/android/systemui/shared/system/InputConsumerController;->mRegistrationListener:Lcom/android/systemui/shared/system/InputConsumerController$RegistrationListener;

    .line 128
    iget-object v0, p0, Lcom/android/systemui/shared/system/InputConsumerController;->mRegistrationListener:Lcom/android/systemui/shared/system/InputConsumerController$RegistrationListener;

    if-eqz v0, :cond_12

    .line 129
    iget-object v0, p0, Lcom/android/systemui/shared/system/InputConsumerController;->mRegistrationListener:Lcom/android/systemui/shared/system/InputConsumerController$RegistrationListener;

    iget-object v1, p0, Lcom/android/systemui/shared/system/InputConsumerController;->mInputEventReceiver:Lcom/android/systemui/shared/system/InputConsumerController$InputEventReceiver;

    if-eqz v1, :cond_e

    const/4 v1, 0x1

    goto :goto_f

    :cond_e
    const/4 v1, 0x0

    :goto_f
    invoke-interface {v0, v1}, Lcom/android/systemui/shared/system/InputConsumerController$RegistrationListener;->onRegistrationChanged(Z)V

    .line 131
    :cond_12
    return-void
.end method

.method public setTouchListener(Lcom/android/systemui/shared/system/InputConsumerController$TouchListener;)V
    .registers 2
    .param p1, "listener"    # Lcom/android/systemui/shared/system/InputConsumerController$TouchListener;

    .line 120
    iput-object p1, p0, Lcom/android/systemui/shared/system/InputConsumerController;->mListener:Lcom/android/systemui/shared/system/InputConsumerController$TouchListener;

    .line 121
    return-void
.end method

.method public unregisterInputConsumer()V
    .registers 4

    .line 165
    iget-object v0, p0, Lcom/android/systemui/shared/system/InputConsumerController;->mInputEventReceiver:Lcom/android/systemui/shared/system/InputConsumerController$InputEventReceiver;

    if-eqz v0, :cond_26

    .line 167
    :try_start_4
    iget-object v0, p0, Lcom/android/systemui/shared/system/InputConsumerController;->mWindowManager:Landroid/view/IWindowManager;

    iget-object v1, p0, Lcom/android/systemui/shared/system/InputConsumerController;->mName:Ljava/lang/String;

    invoke-interface {v0, v1}, Landroid/view/IWindowManager;->destroyInputConsumer(Ljava/lang/String;)Z
    :try_end_b
    .catch Landroid/os/RemoteException; {:try_start_4 .. :try_end_b} :catch_c

    .line 170
    goto :goto_14

    .line 168
    :catch_c
    move-exception v0

    .line 169
    .local v0, "e":Landroid/os/RemoteException;
    sget-object v1, Lcom/android/systemui/shared/system/InputConsumerController;->TAG:Ljava/lang/String;

    const-string v2, "Failed to destroy input consumer"

    invoke-static {v1, v2, v0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    .line 171
    .end local v0    # "e":Landroid/os/RemoteException;
    :goto_14
    iget-object v0, p0, Lcom/android/systemui/shared/system/InputConsumerController;->mInputEventReceiver:Lcom/android/systemui/shared/system/InputConsumerController$InputEventReceiver;

    invoke-virtual {v0}, Lcom/android/systemui/shared/system/InputConsumerController$InputEventReceiver;->dispose()V

    .line 172
    const/4 v0, 0x0

    iput-object v0, p0, Lcom/android/systemui/shared/system/InputConsumerController;->mInputEventReceiver:Lcom/android/systemui/shared/system/InputConsumerController$InputEventReceiver;

    .line 173
    iget-object v0, p0, Lcom/android/systemui/shared/system/InputConsumerController;->mRegistrationListener:Lcom/android/systemui/shared/system/InputConsumerController$RegistrationListener;

    if-eqz v0, :cond_26

    .line 174
    iget-object v0, p0, Lcom/android/systemui/shared/system/InputConsumerController;->mRegistrationListener:Lcom/android/systemui/shared/system/InputConsumerController$RegistrationListener;

    const/4 v1, 0x0

    invoke-interface {v0, v1}, Lcom/android/systemui/shared/system/InputConsumerController$RegistrationListener;->onRegistrationChanged(Z)V

    .line 177
    :cond_26
    return-void
.end method
