.class public abstract Lcom/android/systemui/shared/recents/IOverviewProxy$Stub;
.super Landroid/os/Binder;
.source "IOverviewProxy.java"

# interfaces
.implements Lcom/android/systemui/shared/recents/IOverviewProxy;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/android/systemui/shared/recents/IOverviewProxy;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x409
    name = "Stub"
.end annotation

.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/android/systemui/shared/recents/IOverviewProxy$Stub$Proxy;
    }
.end annotation


# static fields
.field private static final DESCRIPTOR:Ljava/lang/String; = "com.android.systemui.shared.recents.IOverviewProxy"

.field static final TRANSACTION_onBind:I = 0x1

.field static final TRANSACTION_onMotionEvent:I = 0x3

.field static final TRANSACTION_onOverviewHidden:I = 0x9

.field static final TRANSACTION_onOverviewShown:I = 0x8

.field static final TRANSACTION_onOverviewToggle:I = 0x7

.field static final TRANSACTION_onPreMotionEvent:I = 0x2

.field static final TRANSACTION_onQuickScrubEnd:I = 0x5

.field static final TRANSACTION_onQuickScrubProgress:I = 0x6

.field static final TRANSACTION_onQuickScrubStart:I = 0x4

.field static final TRANSACTION_onQuickStep:I = 0xa

.field static final TRANSACTION_onTip:I = 0xb


# direct methods
.method public constructor <init>()V
    .registers 2

    .line 104
    invoke-direct {p0}, Landroid/os/Binder;-><init>()V

    .line 105
    const-string v0, "com.android.systemui.shared.recents.IOverviewProxy"

    invoke-virtual {p0, p0, v0}, Lcom/android/systemui/shared/recents/IOverviewProxy$Stub;->attachInterface(Landroid/os/IInterface;Ljava/lang/String;)V

    .line 106
    return-void
.end method

.method public static asInterface(Landroid/os/IBinder;)Lcom/android/systemui/shared/recents/IOverviewProxy;
    .registers 3
    .param p0, "obj"    # Landroid/os/IBinder;

    .line 113
    if-nez p0, :cond_4

    .line 114
    const/4 v0, 0x0

    return-object v0

    .line 116
    :cond_4
    const-string v0, "com.android.systemui.shared.recents.IOverviewProxy"

    invoke-interface {p0, v0}, Landroid/os/IBinder;->queryLocalInterface(Ljava/lang/String;)Landroid/os/IInterface;

    move-result-object v0

    .line 117
    .local v0, "iin":Landroid/os/IInterface;
    if-eqz v0, :cond_14

    instance-of v1, v0, Lcom/android/systemui/shared/recents/IOverviewProxy;

    if-eqz v1, :cond_14

    .line 118
    move-object v1, v0

    check-cast v1, Lcom/android/systemui/shared/recents/IOverviewProxy;

    return-object v1

    .line 120
    :cond_14
    new-instance v1, Lcom/android/systemui/shared/recents/IOverviewProxy$Stub$Proxy;

    invoke-direct {v1, p0}, Lcom/android/systemui/shared/recents/IOverviewProxy$Stub$Proxy;-><init>(Landroid/os/IBinder;)V

    return-object v1
.end method

.method public static getDefaultImpl()Lcom/android/systemui/shared/recents/IOverviewProxy;
    .registers 1

    .line 510
    sget-object v0, Lcom/android/systemui/shared/recents/IOverviewProxy$Stub$Proxy;->sDefaultImpl:Lcom/android/systemui/shared/recents/IOverviewProxy;

    return-object v0
.end method

.method public static setDefaultImpl(Lcom/android/systemui/shared/recents/IOverviewProxy;)Z
    .registers 2
    .param p0, "impl"    # Lcom/android/systemui/shared/recents/IOverviewProxy;

    .line 503
    sget-object v0, Lcom/android/systemui/shared/recents/IOverviewProxy$Stub$Proxy;->sDefaultImpl:Lcom/android/systemui/shared/recents/IOverviewProxy;

    if-nez v0, :cond_a

    if-eqz p0, :cond_a

    .line 504
    sput-object p0, Lcom/android/systemui/shared/recents/IOverviewProxy$Stub$Proxy;->sDefaultImpl:Lcom/android/systemui/shared/recents/IOverviewProxy;

    .line 505
    const/4 v0, 0x1

    return v0

    .line 507
    :cond_a
    const/4 v0, 0x0

    return v0
.end method


# virtual methods
.method public asBinder()Landroid/os/IBinder;
    .registers 1

    .line 124
    return-object p0
.end method

.method public onTransact(ILandroid/os/Parcel;Landroid/os/Parcel;I)Z
    .registers 10
    .param p1, "code"    # I
    .param p2, "data"    # Landroid/os/Parcel;
    .param p3, "reply"    # Landroid/os/Parcel;
    .param p4, "flags"    # I
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Landroid/os/RemoteException;
        }
    .end annotation

    .line 128
    const-string v0, "com.android.systemui.shared.recents.IOverviewProxy"

    .line 129
    .local v0, "descriptor":Ljava/lang/String;
    const v1, 0x5f4e5446

    const/4 v2, 0x1

    if-eq p1, v1, :cond_b1

    const/4 v1, 0x0

    const/4 v3, 0x0

    packed-switch p1, :pswitch_data_b6

    .line 234
    invoke-super {p0, p1, p2, p3, p4}, Landroid/os/Binder;->onTransact(ILandroid/os/Parcel;Landroid/os/Parcel;I)Z

    move-result v1

    return v1

    .line 224
    :pswitch_12
    invoke-virtual {p2, v0}, Landroid/os/Parcel;->enforceInterface(Ljava/lang/String;)V

    .line 226
    invoke-virtual {p2}, Landroid/os/Parcel;->readInt()I

    move-result v1

    .line 228
    .local v1, "_arg0":I
    invoke-virtual {p2}, Landroid/os/Parcel;->readInt()I

    move-result v3

    .line 229
    .local v3, "_arg1":I
    invoke-virtual {p0, v1, v3}, Lcom/android/systemui/shared/recents/IOverviewProxy$Stub;->onTip(II)V

    .line 230
    return v2

    .line 211
    .end local v1    # "_arg0":I
    .end local v3    # "_arg1":I
    :pswitch_21
    invoke-virtual {p2, v0}, Landroid/os/Parcel;->enforceInterface(Ljava/lang/String;)V

    .line 213
    invoke-virtual {p2}, Landroid/os/Parcel;->readInt()I

    move-result v3

    if-eqz v3, :cond_33

    .line 214
    sget-object v1, Landroid/view/MotionEvent;->CREATOR:Landroid/os/Parcelable$Creator;

    invoke-interface {v1, p2}, Landroid/os/Parcelable$Creator;->createFromParcel(Landroid/os/Parcel;)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Landroid/view/MotionEvent;

    .line 214
    .local v1, "_arg0":Landroid/view/MotionEvent;
    goto :goto_34

    .line 217
    .end local v1    # "_arg0":Landroid/view/MotionEvent;
    :cond_33
    nop

    .line 219
    .restart local v1    # "_arg0":Landroid/view/MotionEvent;
    :goto_34
    invoke-virtual {p0, v1}, Lcom/android/systemui/shared/recents/IOverviewProxy$Stub;->onQuickStep(Landroid/view/MotionEvent;)V

    .line 220
    return v2

    .line 201
    .end local v1    # "_arg0":Landroid/view/MotionEvent;
    :pswitch_38
    invoke-virtual {p2, v0}, Landroid/os/Parcel;->enforceInterface(Ljava/lang/String;)V

    .line 203
    invoke-virtual {p2}, Landroid/os/Parcel;->readInt()I

    move-result v1

    if-eqz v1, :cond_43

    const/4 v1, 0x1

    goto :goto_44

    :cond_43
    const/4 v1, 0x0

    .line 205
    .local v1, "_arg0":Z
    :goto_44
    invoke-virtual {p2}, Landroid/os/Parcel;->readInt()I

    move-result v4

    if-eqz v4, :cond_4c

    const/4 v3, 0x1

    nop

    .line 206
    .local v3, "_arg1":Z
    :cond_4c
    invoke-virtual {p0, v1, v3}, Lcom/android/systemui/shared/recents/IOverviewProxy$Stub;->onOverviewHidden(ZZ)V

    .line 207
    return v2

    .line 193
    .end local v1    # "_arg0":Z
    .end local v3    # "_arg1":Z
    :pswitch_50
    invoke-virtual {p2, v0}, Landroid/os/Parcel;->enforceInterface(Ljava/lang/String;)V

    .line 195
    invoke-virtual {p2}, Landroid/os/Parcel;->readInt()I

    move-result v1

    if-eqz v1, :cond_5b

    const/4 v3, 0x1

    nop

    :cond_5b
    move v1, v3

    .line 196
    .restart local v1    # "_arg0":Z
    invoke-virtual {p0, v1}, Lcom/android/systemui/shared/recents/IOverviewProxy$Stub;->onOverviewShown(Z)V

    .line 197
    return v2

    .line 187
    .end local v1    # "_arg0":Z
    :pswitch_60
    invoke-virtual {p2, v0}, Landroid/os/Parcel;->enforceInterface(Ljava/lang/String;)V

    .line 188
    invoke-virtual {p0}, Lcom/android/systemui/shared/recents/IOverviewProxy$Stub;->onOverviewToggle()V

    .line 189
    return v2

    .line 179
    :pswitch_67
    invoke-virtual {p2, v0}, Landroid/os/Parcel;->enforceInterface(Ljava/lang/String;)V

    .line 181
    invoke-virtual {p2}, Landroid/os/Parcel;->readFloat()F

    move-result v1

    .line 182
    .local v1, "_arg0":F
    invoke-virtual {p0, v1}, Lcom/android/systemui/shared/recents/IOverviewProxy$Stub;->onQuickScrubProgress(F)V

    .line 183
    return v2

    .line 173
    .end local v1    # "_arg0":F
    :pswitch_72
    invoke-virtual {p2, v0}, Landroid/os/Parcel;->enforceInterface(Ljava/lang/String;)V

    .line 174
    invoke-virtual {p0}, Lcom/android/systemui/shared/recents/IOverviewProxy$Stub;->onQuickScrubEnd()V

    .line 175
    return v2

    .line 167
    :pswitch_79
    invoke-virtual {p2, v0}, Landroid/os/Parcel;->enforceInterface(Ljava/lang/String;)V

    .line 168
    invoke-virtual {p0}, Lcom/android/systemui/shared/recents/IOverviewProxy$Stub;->onQuickScrubStart()V

    .line 169
    return v2

    .line 154
    :pswitch_80
    invoke-virtual {p2, v0}, Landroid/os/Parcel;->enforceInterface(Ljava/lang/String;)V

    .line 156
    invoke-virtual {p2}, Landroid/os/Parcel;->readInt()I

    move-result v3

    if-eqz v3, :cond_92

    .line 157
    sget-object v1, Landroid/view/MotionEvent;->CREATOR:Landroid/os/Parcelable$Creator;

    invoke-interface {v1, p2}, Landroid/os/Parcelable$Creator;->createFromParcel(Landroid/os/Parcel;)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Landroid/view/MotionEvent;

    .line 157
    .local v1, "_arg0":Landroid/view/MotionEvent;
    goto :goto_93

    .line 160
    .end local v1    # "_arg0":Landroid/view/MotionEvent;
    :cond_92
    nop

    .line 162
    .restart local v1    # "_arg0":Landroid/view/MotionEvent;
    :goto_93
    invoke-virtual {p0, v1}, Lcom/android/systemui/shared/recents/IOverviewProxy$Stub;->onMotionEvent(Landroid/view/MotionEvent;)V

    .line 163
    return v2

    .line 146
    .end local v1    # "_arg0":Landroid/view/MotionEvent;
    :pswitch_97
    invoke-virtual {p2, v0}, Landroid/os/Parcel;->enforceInterface(Ljava/lang/String;)V

    .line 148
    invoke-virtual {p2}, Landroid/os/Parcel;->readInt()I

    move-result v1

    .line 149
    .local v1, "_arg0":I
    invoke-virtual {p0, v1}, Lcom/android/systemui/shared/recents/IOverviewProxy$Stub;->onPreMotionEvent(I)V

    .line 150
    return v2

    .line 138
    .end local v1    # "_arg0":I
    :pswitch_a2
    invoke-virtual {p2, v0}, Landroid/os/Parcel;->enforceInterface(Ljava/lang/String;)V

    .line 140
    invoke-virtual {p2}, Landroid/os/Parcel;->readStrongBinder()Landroid/os/IBinder;

    move-result-object v1

    invoke-static {v1}, Lcom/android/systemui/shared/recents/ISystemUiProxy$Stub;->asInterface(Landroid/os/IBinder;)Lcom/android/systemui/shared/recents/ISystemUiProxy;

    move-result-object v1

    .line 141
    .local v1, "_arg0":Lcom/android/systemui/shared/recents/ISystemUiProxy;
    invoke-virtual {p0, v1}, Lcom/android/systemui/shared/recents/IOverviewProxy$Stub;->onBind(Lcom/android/systemui/shared/recents/ISystemUiProxy;)V

    .line 142
    return v2

    .line 133
    .end local v1    # "_arg0":Lcom/android/systemui/shared/recents/ISystemUiProxy;
    :cond_b1
    invoke-virtual {p3, v0}, Landroid/os/Parcel;->writeString(Ljava/lang/String;)V

    .line 134
    return v2

    nop

    :pswitch_data_b6
    .packed-switch 0x1
        :pswitch_a2
        :pswitch_97
        :pswitch_80
        :pswitch_79
        :pswitch_72
        :pswitch_67
        :pswitch_60
        :pswitch_50
        :pswitch_38
        :pswitch_21
        :pswitch_12
    .end packed-switch
.end method
