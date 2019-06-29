.class public abstract Lcom/android/systemui/shared/recents/ISystemUiProxy$Stub;
.super Landroid/os/Binder;
.source "ISystemUiProxy.java"

# interfaces
.implements Lcom/android/systemui/shared/recents/ISystemUiProxy;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/android/systemui/shared/recents/ISystemUiProxy;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x409
    name = "Stub"
.end annotation

.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/android/systemui/shared/recents/ISystemUiProxy$Stub$Proxy;
    }
.end annotation


# static fields
.field private static final DESCRIPTOR:Ljava/lang/String; = "com.android.systemui.shared.recents.ISystemUiProxy"

.field static final TRANSACTION_getNonMinimizedSplitScreenSecondaryBounds:I = 0x8

.field static final TRANSACTION_onOverviewShown:I = 0x7

.field static final TRANSACTION_onSplitScreenInvoked:I = 0x6

.field static final TRANSACTION_screenshot:I = 0x1

.field static final TRANSACTION_setBackButtonAlpha:I = 0x9

.field static final TRANSACTION_setInteractionState:I = 0x5

.field static final TRANSACTION_startScreenPinning:I = 0x2


# direct methods
.method public constructor <init>()V
    .registers 2

    .line 70
    invoke-direct {p0}, Landroid/os/Binder;-><init>()V

    .line 71
    const-string v0, "com.android.systemui.shared.recents.ISystemUiProxy"

    invoke-virtual {p0, p0, v0}, Lcom/android/systemui/shared/recents/ISystemUiProxy$Stub;->attachInterface(Landroid/os/IInterface;Ljava/lang/String;)V

    .line 72
    return-void
.end method

.method public static asInterface(Landroid/os/IBinder;)Lcom/android/systemui/shared/recents/ISystemUiProxy;
    .registers 3
    .param p0, "obj"    # Landroid/os/IBinder;

    .line 79
    if-nez p0, :cond_4

    .line 80
    const/4 v0, 0x0

    return-object v0

    .line 82
    :cond_4
    const-string v0, "com.android.systemui.shared.recents.ISystemUiProxy"

    invoke-interface {p0, v0}, Landroid/os/IBinder;->queryLocalInterface(Ljava/lang/String;)Landroid/os/IInterface;

    move-result-object v0

    .line 83
    .local v0, "iin":Landroid/os/IInterface;
    if-eqz v0, :cond_14

    instance-of v1, v0, Lcom/android/systemui/shared/recents/ISystemUiProxy;

    if-eqz v1, :cond_14

    .line 84
    move-object v1, v0

    check-cast v1, Lcom/android/systemui/shared/recents/ISystemUiProxy;

    return-object v1

    .line 86
    :cond_14
    new-instance v1, Lcom/android/systemui/shared/recents/ISystemUiProxy$Stub$Proxy;

    invoke-direct {v1, p0}, Lcom/android/systemui/shared/recents/ISystemUiProxy$Stub$Proxy;-><init>(Landroid/os/IBinder;)V

    return-object v1
.end method

.method public static getDefaultImpl()Lcom/android/systemui/shared/recents/ISystemUiProxy;
    .registers 1

    .line 412
    sget-object v0, Lcom/android/systemui/shared/recents/ISystemUiProxy$Stub$Proxy;->sDefaultImpl:Lcom/android/systemui/shared/recents/ISystemUiProxy;

    return-object v0
.end method

.method public static setDefaultImpl(Lcom/android/systemui/shared/recents/ISystemUiProxy;)Z
    .registers 2
    .param p0, "impl"    # Lcom/android/systemui/shared/recents/ISystemUiProxy;

    .line 405
    sget-object v0, Lcom/android/systemui/shared/recents/ISystemUiProxy$Stub$Proxy;->sDefaultImpl:Lcom/android/systemui/shared/recents/ISystemUiProxy;

    if-nez v0, :cond_a

    if-eqz p0, :cond_a

    .line 406
    sput-object p0, Lcom/android/systemui/shared/recents/ISystemUiProxy$Stub$Proxy;->sDefaultImpl:Lcom/android/systemui/shared/recents/ISystemUiProxy;

    .line 407
    const/4 v0, 0x1

    return v0

    .line 409
    :cond_a
    const/4 v0, 0x0

    return v0
.end method


# virtual methods
.method public asBinder()Landroid/os/IBinder;
    .registers 1

    .line 90
    return-object p0
.end method

.method public onTransact(ILandroid/os/Parcel;Landroid/os/Parcel;I)Z
    .registers 25
    .param p1, "code"    # I
    .param p2, "data"    # Landroid/os/Parcel;
    .param p3, "reply"    # Landroid/os/Parcel;
    .param p4, "flags"    # I
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Landroid/os/RemoteException;
        }
    .end annotation

    move-object/from16 v8, p0

    move/from16 v9, p1

    move-object/from16 v10, p2

    move-object/from16 v11, p3

    .line 94
    const-string v12, "com.android.systemui.shared.recents.ISystemUiProxy"

    .line 95
    .local v12, "descriptor":Ljava/lang/String;
    const v0, 0x5f4e5446

    const/4 v13, 0x1

    if-eq v9, v0, :cond_d3

    const/4 v14, 0x0

    packed-switch v9, :pswitch_data_d8

    packed-switch v9, :pswitch_data_e0

    .line 196
    invoke-super/range {p0 .. p4}, Landroid/os/Binder;->onTransact(ILandroid/os/Parcel;Landroid/os/Parcel;I)Z

    move-result v0

    return v0

    .line 185
    :pswitch_1c
    invoke-virtual {v10, v12}, Landroid/os/Parcel;->enforceInterface(Ljava/lang/String;)V

    .line 187
    invoke-virtual/range {p2 .. p2}, Landroid/os/Parcel;->readFloat()F

    move-result v0

    .line 189
    .local v0, "_arg0":F
    invoke-virtual/range {p2 .. p2}, Landroid/os/Parcel;->readInt()I

    move-result v1

    if-eqz v1, :cond_2b

    const/4 v14, 0x1

    nop

    :cond_2b
    move v1, v14

    .line 190
    .local v1, "_arg1":Z
    invoke-virtual {v8, v0, v1}, Lcom/android/systemui/shared/recents/ISystemUiProxy$Stub;->setBackButtonAlpha(FZ)V

    .line 191
    invoke-virtual/range {p3 .. p3}, Landroid/os/Parcel;->writeNoException()V

    .line 192
    return v13

    .line 171
    .end local v0    # "_arg0":F
    .end local v1    # "_arg1":Z
    :pswitch_33
    invoke-virtual {v10, v12}, Landroid/os/Parcel;->enforceInterface(Ljava/lang/String;)V

    .line 172
    invoke-virtual/range {p0 .. p0}, Lcom/android/systemui/shared/recents/ISystemUiProxy$Stub;->getNonMinimizedSplitScreenSecondaryBounds()Landroid/graphics/Rect;

    move-result-object v0

    .line 173
    .local v0, "_result":Landroid/graphics/Rect;
    invoke-virtual/range {p3 .. p3}, Landroid/os/Parcel;->writeNoException()V

    .line 174
    if-eqz v0, :cond_46

    .line 175
    invoke-virtual {v11, v13}, Landroid/os/Parcel;->writeInt(I)V

    .line 176
    invoke-virtual {v0, v11, v13}, Landroid/graphics/Rect;->writeToParcel(Landroid/os/Parcel;I)V

    goto :goto_49

    .line 179
    :cond_46
    invoke-virtual {v11, v14}, Landroid/os/Parcel;->writeInt(I)V

    .line 181
    :goto_49
    return v13

    .line 162
    .end local v0    # "_result":Landroid/graphics/Rect;
    :pswitch_4a
    invoke-virtual {v10, v12}, Landroid/os/Parcel;->enforceInterface(Ljava/lang/String;)V

    .line 164
    invoke-virtual/range {p2 .. p2}, Landroid/os/Parcel;->readInt()I

    move-result v0

    if-eqz v0, :cond_55

    const/4 v14, 0x1

    nop

    :cond_55
    move v0, v14

    .line 165
    .local v0, "_arg0":Z
    invoke-virtual {v8, v0}, Lcom/android/systemui/shared/recents/ISystemUiProxy$Stub;->onOverviewShown(Z)V

    .line 166
    invoke-virtual/range {p3 .. p3}, Landroid/os/Parcel;->writeNoException()V

    .line 167
    return v13

    .line 155
    .end local v0    # "_arg0":Z
    :pswitch_5d
    invoke-virtual {v10, v12}, Landroid/os/Parcel;->enforceInterface(Ljava/lang/String;)V

    .line 156
    invoke-virtual/range {p0 .. p0}, Lcom/android/systemui/shared/recents/ISystemUiProxy$Stub;->onSplitScreenInvoked()V

    .line 157
    invoke-virtual/range {p3 .. p3}, Landroid/os/Parcel;->writeNoException()V

    .line 158
    return v13

    .line 146
    :pswitch_67
    invoke-virtual {v10, v12}, Landroid/os/Parcel;->enforceInterface(Ljava/lang/String;)V

    .line 148
    invoke-virtual/range {p2 .. p2}, Landroid/os/Parcel;->readInt()I

    move-result v0

    .line 149
    .local v0, "_arg0":I
    invoke-virtual {v8, v0}, Lcom/android/systemui/shared/recents/ISystemUiProxy$Stub;->setInteractionState(I)V

    .line 150
    invoke-virtual/range {p3 .. p3}, Landroid/os/Parcel;->writeNoException()V

    .line 151
    return v13

    .line 137
    .end local v0    # "_arg0":I
    :pswitch_75
    invoke-virtual {v10, v12}, Landroid/os/Parcel;->enforceInterface(Ljava/lang/String;)V

    .line 139
    invoke-virtual/range {p2 .. p2}, Landroid/os/Parcel;->readInt()I

    move-result v0

    .line 140
    .restart local v0    # "_arg0":I
    invoke-virtual {v8, v0}, Lcom/android/systemui/shared/recents/ISystemUiProxy$Stub;->startScreenPinning(I)V

    .line 141
    invoke-virtual/range {p3 .. p3}, Landroid/os/Parcel;->writeNoException()V

    .line 142
    return v13

    .line 104
    .end local v0    # "_arg0":I
    :pswitch_83
    invoke-virtual {v10, v12}, Landroid/os/Parcel;->enforceInterface(Ljava/lang/String;)V

    .line 106
    invoke-virtual/range {p2 .. p2}, Landroid/os/Parcel;->readInt()I

    move-result v0

    if-eqz v0, :cond_96

    .line 107
    sget-object v0, Landroid/graphics/Rect;->CREATOR:Landroid/os/Parcelable$Creator;

    invoke-interface {v0, v10}, Landroid/os/Parcelable$Creator;->createFromParcel(Landroid/os/Parcel;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Landroid/graphics/Rect;

    .line 110
    .local v1, "_arg0":Landroid/graphics/Rect;
    :goto_94
    move-object v1, v0

    goto :goto_98

    .line 110
    .end local v1    # "_arg0":Landroid/graphics/Rect;
    :cond_96
    const/4 v0, 0x0

    goto :goto_94

    .line 113
    .restart local v1    # "_arg0":Landroid/graphics/Rect;
    :goto_98
    invoke-virtual/range {p2 .. p2}, Landroid/os/Parcel;->readInt()I

    move-result v15

    .line 115
    .local v15, "_arg1":I
    invoke-virtual/range {p2 .. p2}, Landroid/os/Parcel;->readInt()I

    move-result v16

    .line 117
    .local v16, "_arg2":I
    invoke-virtual/range {p2 .. p2}, Landroid/os/Parcel;->readInt()I

    move-result v17

    .line 119
    .local v17, "_arg3":I
    invoke-virtual/range {p2 .. p2}, Landroid/os/Parcel;->readInt()I

    move-result v18

    .line 121
    .local v18, "_arg4":I
    invoke-virtual/range {p2 .. p2}, Landroid/os/Parcel;->readInt()I

    move-result v0

    if-eqz v0, :cond_b0

    const/4 v6, 0x1

    goto :goto_b1

    :cond_b0
    const/4 v6, 0x0

    .line 123
    .local v6, "_arg5":Z
    :goto_b1
    invoke-virtual/range {p2 .. p2}, Landroid/os/Parcel;->readInt()I

    move-result v19

    .line 124
    .local v19, "_arg6":I
    move-object v0, v8

    move v2, v15

    move/from16 v3, v16

    move/from16 v4, v17

    move/from16 v5, v18

    move/from16 v7, v19

    invoke-virtual/range {v0 .. v7}, Lcom/android/systemui/shared/recents/ISystemUiProxy$Stub;->screenshot(Landroid/graphics/Rect;IIIIZI)Lcom/android/systemui/shared/system/GraphicBufferCompat;

    move-result-object v0

    .line 125
    .local v0, "_result":Lcom/android/systemui/shared/system/GraphicBufferCompat;
    invoke-virtual/range {p3 .. p3}, Landroid/os/Parcel;->writeNoException()V

    .line 126
    if-eqz v0, :cond_cf

    .line 127
    invoke-virtual {v11, v13}, Landroid/os/Parcel;->writeInt(I)V

    .line 128
    invoke-virtual {v0, v11, v13}, Lcom/android/systemui/shared/system/GraphicBufferCompat;->writeToParcel(Landroid/os/Parcel;I)V

    goto :goto_d2

    .line 131
    :cond_cf
    invoke-virtual {v11, v14}, Landroid/os/Parcel;->writeInt(I)V

    .line 133
    :goto_d2
    return v13

    .line 99
    .end local v0    # "_result":Lcom/android/systemui/shared/system/GraphicBufferCompat;
    .end local v1    # "_arg0":Landroid/graphics/Rect;
    .end local v6    # "_arg5":Z
    .end local v15    # "_arg1":I
    .end local v16    # "_arg2":I
    .end local v17    # "_arg3":I
    .end local v18    # "_arg4":I
    .end local v19    # "_arg6":I
    :cond_d3
    invoke-virtual {v11, v12}, Landroid/os/Parcel;->writeString(Ljava/lang/String;)V

    .line 100
    return v13

    nop

    :pswitch_data_d8
    .packed-switch 0x1
        :pswitch_83
        :pswitch_75
    .end packed-switch

    :pswitch_data_e0
    .packed-switch 0x5
        :pswitch_67
        :pswitch_5d
        :pswitch_4a
        :pswitch_33
        :pswitch_1c
    .end packed-switch
.end method
