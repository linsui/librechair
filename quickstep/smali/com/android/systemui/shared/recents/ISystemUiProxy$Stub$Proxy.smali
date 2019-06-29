.class Lcom/android/systemui/shared/recents/ISystemUiProxy$Stub$Proxy;
.super Ljava/lang/Object;
.source "ISystemUiProxy.java"

# interfaces
.implements Lcom/android/systemui/shared/recents/ISystemUiProxy;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/android/systemui/shared/recents/ISystemUiProxy$Stub;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0xa
    name = "Proxy"
.end annotation


# static fields
.field public static sDefaultImpl:Lcom/android/systemui/shared/recents/ISystemUiProxy;


# instance fields
.field private mRemote:Landroid/os/IBinder;


# direct methods
.method static constructor <clinit>()V
    .registers 1

    .line 395
    const/4 v0, 0x0

    sput-object v0, Lcom/android/systemui/shared/recents/ISystemUiProxy$Stub$Proxy;->sDefaultImpl:Lcom/android/systemui/shared/recents/ISystemUiProxy;

    return-void
.end method

.method constructor <init>(Landroid/os/IBinder;)V
    .registers 2
    .param p1, "remote"    # Landroid/os/IBinder;

    .line 204
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 205
    iput-object p1, p0, Lcom/android/systemui/shared/recents/ISystemUiProxy$Stub$Proxy;->mRemote:Landroid/os/IBinder;

    .line 206
    return-void
.end method


# virtual methods
.method public asBinder()Landroid/os/IBinder;
    .registers 2

    .line 209
    iget-object v0, p0, Lcom/android/systemui/shared/recents/ISystemUiProxy$Stub$Proxy;->mRemote:Landroid/os/IBinder;

    return-object v0
.end method

.method public getInterfaceDescriptor()Ljava/lang/String;
    .registers 2

    .line 213
    const-string v0, "com.android.systemui.shared.recents.ISystemUiProxy"

    return-object v0
.end method

.method public getNonMinimizedSplitScreenSecondaryBounds()Landroid/graphics/Rect;
    .registers 6
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Landroid/os/RemoteException;
        }
    .end annotation

    .line 348
    invoke-static {}, Landroid/os/Parcel;->obtain()Landroid/os/Parcel;

    move-result-object v0

    .line 349
    .local v0, "_data":Landroid/os/Parcel;
    invoke-static {}, Landroid/os/Parcel;->obtain()Landroid/os/Parcel;

    move-result-object v1

    .line 352
    .local v1, "_reply":Landroid/os/Parcel;
    :try_start_8
    const-string v2, "com.android.systemui.shared.recents.ISystemUiProxy"

    invoke-virtual {v0, v2}, Landroid/os/Parcel;->writeInterfaceToken(Ljava/lang/String;)V

    .line 353
    iget-object v2, p0, Lcom/android/systemui/shared/recents/ISystemUiProxy$Stub$Proxy;->mRemote:Landroid/os/IBinder;

    const/16 v3, 0x8

    const/4 v4, 0x0

    invoke-interface {v2, v3, v0, v1, v4}, Landroid/os/IBinder;->transact(ILandroid/os/Parcel;Landroid/os/Parcel;I)Z

    move-result v2

    .line 354
    .local v2, "_status":Z
    if-nez v2, :cond_2d

    invoke-static {}, Lcom/android/systemui/shared/recents/ISystemUiProxy$Stub;->getDefaultImpl()Lcom/android/systemui/shared/recents/ISystemUiProxy;

    move-result-object v3

    if-eqz v3, :cond_2d

    .line 355
    invoke-static {}, Lcom/android/systemui/shared/recents/ISystemUiProxy$Stub;->getDefaultImpl()Lcom/android/systemui/shared/recents/ISystemUiProxy;

    move-result-object v3

    invoke-interface {v3}, Lcom/android/systemui/shared/recents/ISystemUiProxy;->getNonMinimizedSplitScreenSecondaryBounds()Landroid/graphics/Rect;

    move-result-object v3
    :try_end_26
    .catchall {:try_start_8 .. :try_end_26} :catchall_4a

    .line 366
    invoke-virtual {v1}, Landroid/os/Parcel;->recycle()V

    .line 367
    invoke-virtual {v0}, Landroid/os/Parcel;->recycle()V

    .line 355
    return-object v3

    .line 357
    :cond_2d
    :try_start_2d
    invoke-virtual {v1}, Landroid/os/Parcel;->readException()V

    .line 358
    invoke-virtual {v1}, Landroid/os/Parcel;->readInt()I

    move-result v3

    if-eqz v3, :cond_3f

    .line 359
    sget-object v3, Landroid/graphics/Rect;->CREATOR:Landroid/os/Parcelable$Creator;

    invoke-interface {v3, v1}, Landroid/os/Parcelable$Creator;->createFromParcel(Landroid/os/Parcel;)Ljava/lang/Object;

    move-result-object v3

    check-cast v3, Landroid/graphics/Rect;
    :try_end_3e
    .catchall {:try_start_2d .. :try_end_3e} :catchall_4a

    .line 359
    .local v3, "_result":Landroid/graphics/Rect;
    goto :goto_40

    .line 362
    .end local v3    # "_result":Landroid/graphics/Rect;
    :cond_3f
    const/4 v3, 0x0

    .line 362
    .end local v2    # "_status":Z
    .restart local v3    # "_result":Landroid/graphics/Rect;
    :goto_40
    move-object v2, v3

    .line 366
    .end local v3    # "_result":Landroid/graphics/Rect;
    .local v2, "_result":Landroid/graphics/Rect;
    invoke-virtual {v1}, Landroid/os/Parcel;->recycle()V

    .line 367
    invoke-virtual {v0}, Landroid/os/Parcel;->recycle()V

    .line 368
    nop

    .line 367
    nop

    .line 369
    return-object v2

    .line 366
    .end local v2    # "_result":Landroid/graphics/Rect;
    :catchall_4a
    move-exception v2

    invoke-virtual {v1}, Landroid/os/Parcel;->recycle()V

    .line 367
    invoke-virtual {v0}, Landroid/os/Parcel;->recycle()V

    throw v2
.end method

.method public onOverviewShown(Z)V
    .registers 7
    .param p1, "fromHome"    # Z
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Landroid/os/RemoteException;
        }
    .end annotation

    .line 326
    invoke-static {}, Landroid/os/Parcel;->obtain()Landroid/os/Parcel;

    move-result-object v0

    .line 327
    .local v0, "_data":Landroid/os/Parcel;
    invoke-static {}, Landroid/os/Parcel;->obtain()Landroid/os/Parcel;

    move-result-object v1

    .line 329
    .local v1, "_reply":Landroid/os/Parcel;
    :try_start_8
    const-string v2, "com.android.systemui.shared.recents.ISystemUiProxy"

    invoke-virtual {v0, v2}, Landroid/os/Parcel;->writeInterfaceToken(Ljava/lang/String;)V

    .line 330
    invoke-virtual {v0, p1}, Landroid/os/Parcel;->writeInt(I)V

    .line 331
    iget-object v2, p0, Lcom/android/systemui/shared/recents/ISystemUiProxy$Stub$Proxy;->mRemote:Landroid/os/IBinder;

    const/4 v3, 0x7

    const/4 v4, 0x0

    invoke-interface {v2, v3, v0, v1, v4}, Landroid/os/IBinder;->transact(ILandroid/os/Parcel;Landroid/os/Parcel;I)Z

    move-result v2

    .line 332
    .local v2, "_status":Z
    if-nez v2, :cond_2e

    invoke-static {}, Lcom/android/systemui/shared/recents/ISystemUiProxy$Stub;->getDefaultImpl()Lcom/android/systemui/shared/recents/ISystemUiProxy;

    move-result-object v3

    if-eqz v3, :cond_2e

    .line 333
    invoke-static {}, Lcom/android/systemui/shared/recents/ISystemUiProxy$Stub;->getDefaultImpl()Lcom/android/systemui/shared/recents/ISystemUiProxy;

    move-result-object v3

    invoke-interface {v3, p1}, Lcom/android/systemui/shared/recents/ISystemUiProxy;->onOverviewShown(Z)V
    :try_end_27
    .catchall {:try_start_8 .. :try_end_27} :catchall_39

    .line 339
    invoke-virtual {v1}, Landroid/os/Parcel;->recycle()V

    .line 340
    invoke-virtual {v0}, Landroid/os/Parcel;->recycle()V

    .line 334
    return-void

    .line 336
    :cond_2e
    :try_start_2e
    invoke-virtual {v1}, Landroid/os/Parcel;->readException()V
    :try_end_31
    .catchall {:try_start_2e .. :try_end_31} :catchall_39

    .line 339
    .end local v2    # "_status":Z
    invoke-virtual {v1}, Landroid/os/Parcel;->recycle()V

    .line 340
    invoke-virtual {v0}, Landroid/os/Parcel;->recycle()V

    .line 341
    nop

    .line 342
    return-void

    .line 339
    :catchall_39
    move-exception v2

    invoke-virtual {v1}, Landroid/os/Parcel;->recycle()V

    .line 340
    invoke-virtual {v0}, Landroid/os/Parcel;->recycle()V

    throw v2
.end method

.method public onSplitScreenInvoked()V
    .registers 6
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Landroid/os/RemoteException;
        }
    .end annotation

    .line 305
    invoke-static {}, Landroid/os/Parcel;->obtain()Landroid/os/Parcel;

    move-result-object v0

    .line 306
    .local v0, "_data":Landroid/os/Parcel;
    invoke-static {}, Landroid/os/Parcel;->obtain()Landroid/os/Parcel;

    move-result-object v1

    .line 308
    .local v1, "_reply":Landroid/os/Parcel;
    :try_start_8
    const-string v2, "com.android.systemui.shared.recents.ISystemUiProxy"

    invoke-virtual {v0, v2}, Landroid/os/Parcel;->writeInterfaceToken(Ljava/lang/String;)V

    .line 309
    iget-object v2, p0, Lcom/android/systemui/shared/recents/ISystemUiProxy$Stub$Proxy;->mRemote:Landroid/os/IBinder;

    const/4 v3, 0x6

    const/4 v4, 0x0

    invoke-interface {v2, v3, v0, v1, v4}, Landroid/os/IBinder;->transact(ILandroid/os/Parcel;Landroid/os/Parcel;I)Z

    move-result v2

    .line 310
    .local v2, "_status":Z
    if-nez v2, :cond_2b

    invoke-static {}, Lcom/android/systemui/shared/recents/ISystemUiProxy$Stub;->getDefaultImpl()Lcom/android/systemui/shared/recents/ISystemUiProxy;

    move-result-object v3

    if-eqz v3, :cond_2b

    .line 311
    invoke-static {}, Lcom/android/systemui/shared/recents/ISystemUiProxy$Stub;->getDefaultImpl()Lcom/android/systemui/shared/recents/ISystemUiProxy;

    move-result-object v3

    invoke-interface {v3}, Lcom/android/systemui/shared/recents/ISystemUiProxy;->onSplitScreenInvoked()V
    :try_end_24
    .catchall {:try_start_8 .. :try_end_24} :catchall_36

    .line 317
    invoke-virtual {v1}, Landroid/os/Parcel;->recycle()V

    .line 318
    invoke-virtual {v0}, Landroid/os/Parcel;->recycle()V

    .line 312
    return-void

    .line 314
    :cond_2b
    :try_start_2b
    invoke-virtual {v1}, Landroid/os/Parcel;->readException()V
    :try_end_2e
    .catchall {:try_start_2b .. :try_end_2e} :catchall_36

    .line 317
    .end local v2    # "_status":Z
    invoke-virtual {v1}, Landroid/os/Parcel;->recycle()V

    .line 318
    invoke-virtual {v0}, Landroid/os/Parcel;->recycle()V

    .line 319
    nop

    .line 320
    return-void

    .line 317
    :catchall_36
    move-exception v2

    invoke-virtual {v1}, Landroid/os/Parcel;->recycle()V

    .line 318
    invoke-virtual {v0}, Landroid/os/Parcel;->recycle()V

    throw v2
.end method

.method public screenshot(Landroid/graphics/Rect;IIIIZI)Lcom/android/systemui/shared/system/GraphicBufferCompat;
    .registers 25
    .param p1, "sourceCrop"    # Landroid/graphics/Rect;
    .param p2, "width"    # I
    .param p3, "height"    # I
    .param p4, "minLayer"    # I
    .param p5, "maxLayer"    # I
    .param p6, "useIdentityTransform"    # Z
    .param p7, "rotation"    # I
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Landroid/os/RemoteException;
        }
    .end annotation

    move-object/from16 v9, p1

    .line 220
    invoke-static {}, Landroid/os/Parcel;->obtain()Landroid/os/Parcel;

    move-result-object v10

    .line 221
    .local v10, "_data":Landroid/os/Parcel;
    invoke-static {}, Landroid/os/Parcel;->obtain()Landroid/os/Parcel;

    move-result-object v1

    move-object v11, v1

    .line 224
    .local v11, "_reply":Landroid/os/Parcel;
    :try_start_b
    const-string v1, "com.android.systemui.shared.recents.ISystemUiProxy"

    invoke-virtual {v10, v1}, Landroid/os/Parcel;->writeInterfaceToken(Ljava/lang/String;)V

    .line 225
    const/4 v1, 0x1

    const/4 v2, 0x0

    if-eqz v9, :cond_1b

    .line 226
    invoke-virtual {v10, v1}, Landroid/os/Parcel;->writeInt(I)V

    .line 227
    invoke-virtual {v9, v10, v2}, Landroid/graphics/Rect;->writeToParcel(Landroid/os/Parcel;I)V

    goto :goto_1e

    .line 230
    :cond_1b
    invoke-virtual {v10, v2}, Landroid/os/Parcel;->writeInt(I)V
    :try_end_1e
    .catchall {:try_start_b .. :try_end_1e} :catchall_89

    .line 232
    :goto_1e
    move/from16 v12, p2

    :try_start_20
    invoke-virtual {v10, v12}, Landroid/os/Parcel;->writeInt(I)V
    :try_end_23
    .catchall {:try_start_20 .. :try_end_23} :catchall_87

    .line 233
    move/from16 v13, p3

    :try_start_25
    invoke-virtual {v10, v13}, Landroid/os/Parcel;->writeInt(I)V
    :try_end_28
    .catchall {:try_start_25 .. :try_end_28} :catchall_85

    .line 234
    move/from16 v14, p4

    :try_start_2a
    invoke-virtual {v10, v14}, Landroid/os/Parcel;->writeInt(I)V
    :try_end_2d
    .catchall {:try_start_2a .. :try_end_2d} :catchall_83

    .line 235
    move/from16 v15, p5

    :try_start_2f
    invoke-virtual {v10, v15}, Landroid/os/Parcel;->writeInt(I)V

    .line 236
    move/from16 v8, p6

    invoke-virtual {v10, v8}, Landroid/os/Parcel;->writeInt(I)V

    .line 237
    move/from16 v7, p7

    invoke-virtual {v10, v7}, Landroid/os/Parcel;->writeInt(I)V

    .line 238
    move-object/from16 v6, p0

    iget-object v3, v6, Lcom/android/systemui/shared/recents/ISystemUiProxy$Stub$Proxy;->mRemote:Landroid/os/IBinder;

    invoke-interface {v3, v1, v10, v11, v2}, Landroid/os/IBinder;->transact(ILandroid/os/Parcel;Landroid/os/Parcel;I)Z

    move-result v1

    move/from16 v16, v1

    .line 239
    .local v16, "_status":Z
    if-nez v16, :cond_65

    invoke-static {}, Lcom/android/systemui/shared/recents/ISystemUiProxy$Stub;->getDefaultImpl()Lcom/android/systemui/shared/recents/ISystemUiProxy;

    move-result-object v1

    if-eqz v1, :cond_65

    .line 240
    invoke-static {}, Lcom/android/systemui/shared/recents/ISystemUiProxy$Stub;->getDefaultImpl()Lcom/android/systemui/shared/recents/ISystemUiProxy;

    move-result-object v1

    move-object v2, v9

    move v3, v12

    move v4, v13

    move v5, v14

    move v6, v15

    move v7, v8

    move/from16 v8, p7

    invoke-interface/range {v1 .. v8}, Lcom/android/systemui/shared/recents/ISystemUiProxy;->screenshot(Landroid/graphics/Rect;IIIIZI)Lcom/android/systemui/shared/system/GraphicBufferCompat;

    move-result-object v1
    :try_end_5e
    .catchall {:try_start_2f .. :try_end_5e} :catchall_81

    .line 251
    invoke-virtual {v11}, Landroid/os/Parcel;->recycle()V

    .line 252
    invoke-virtual {v10}, Landroid/os/Parcel;->recycle()V

    .line 240
    return-object v1

    .line 242
    :cond_65
    :try_start_65
    invoke-virtual {v11}, Landroid/os/Parcel;->readException()V

    .line 243
    invoke-virtual {v11}, Landroid/os/Parcel;->readInt()I

    move-result v1

    if-eqz v1, :cond_77

    .line 244
    sget-object v1, Lcom/android/systemui/shared/system/GraphicBufferCompat;->CREATOR:Landroid/os/Parcelable$Creator;

    invoke-interface {v1, v11}, Landroid/os/Parcelable$Creator;->createFromParcel(Landroid/os/Parcel;)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Lcom/android/systemui/shared/system/GraphicBufferCompat;
    :try_end_76
    .catchall {:try_start_65 .. :try_end_76} :catchall_81

    .line 244
    .local v1, "_result":Lcom/android/systemui/shared/system/GraphicBufferCompat;
    goto :goto_78

    .line 247
    .end local v1    # "_result":Lcom/android/systemui/shared/system/GraphicBufferCompat;
    :cond_77
    const/4 v1, 0x0

    .line 251
    .end local v16    # "_status":Z
    .restart local v1    # "_result":Lcom/android/systemui/shared/system/GraphicBufferCompat;
    :goto_78
    invoke-virtual {v11}, Landroid/os/Parcel;->recycle()V

    .line 252
    invoke-virtual {v10}, Landroid/os/Parcel;->recycle()V

    .line 253
    nop

    .line 252
    nop

    .line 254
    return-object v1

    .line 251
    .end local v1    # "_result":Lcom/android/systemui/shared/system/GraphicBufferCompat;
    :catchall_81
    move-exception v0

    goto :goto_92

    :catchall_83
    move-exception v0

    goto :goto_90

    :catchall_85
    move-exception v0

    goto :goto_8e

    :catchall_87
    move-exception v0

    goto :goto_8c

    :catchall_89
    move-exception v0

    move/from16 v12, p2

    :goto_8c
    move/from16 v13, p3

    :goto_8e
    move/from16 v14, p4

    :goto_90
    move/from16 v15, p5

    :goto_92
    move-object v1, v0

    invoke-virtual {v11}, Landroid/os/Parcel;->recycle()V

    .line 252
    invoke-virtual {v10}, Landroid/os/Parcel;->recycle()V

    throw v1
.end method

.method public setBackButtonAlpha(FZ)V
    .registers 8
    .param p1, "alpha"    # F
    .param p2, "animate"    # Z
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Landroid/os/RemoteException;
        }
    .end annotation

    .line 377
    invoke-static {}, Landroid/os/Parcel;->obtain()Landroid/os/Parcel;

    move-result-object v0

    .line 378
    .local v0, "_data":Landroid/os/Parcel;
    invoke-static {}, Landroid/os/Parcel;->obtain()Landroid/os/Parcel;

    move-result-object v1

    .line 380
    .local v1, "_reply":Landroid/os/Parcel;
    :try_start_8
    const-string v2, "com.android.systemui.shared.recents.ISystemUiProxy"

    invoke-virtual {v0, v2}, Landroid/os/Parcel;->writeInterfaceToken(Ljava/lang/String;)V

    .line 381
    invoke-virtual {v0, p1}, Landroid/os/Parcel;->writeFloat(F)V

    .line 382
    invoke-virtual {v0, p2}, Landroid/os/Parcel;->writeInt(I)V

    .line 383
    iget-object v2, p0, Lcom/android/systemui/shared/recents/ISystemUiProxy$Stub$Proxy;->mRemote:Landroid/os/IBinder;

    const/16 v3, 0x9

    const/4 v4, 0x0

    invoke-interface {v2, v3, v0, v1, v4}, Landroid/os/IBinder;->transact(ILandroid/os/Parcel;Landroid/os/Parcel;I)Z

    move-result v2

    .line 384
    .local v2, "_status":Z
    if-nez v2, :cond_32

    invoke-static {}, Lcom/android/systemui/shared/recents/ISystemUiProxy$Stub;->getDefaultImpl()Lcom/android/systemui/shared/recents/ISystemUiProxy;

    move-result-object v3

    if-eqz v3, :cond_32

    .line 385
    invoke-static {}, Lcom/android/systemui/shared/recents/ISystemUiProxy$Stub;->getDefaultImpl()Lcom/android/systemui/shared/recents/ISystemUiProxy;

    move-result-object v3

    invoke-interface {v3, p1, p2}, Lcom/android/systemui/shared/recents/ISystemUiProxy;->setBackButtonAlpha(FZ)V
    :try_end_2b
    .catchall {:try_start_8 .. :try_end_2b} :catchall_3d

    .line 391
    invoke-virtual {v1}, Landroid/os/Parcel;->recycle()V

    .line 392
    invoke-virtual {v0}, Landroid/os/Parcel;->recycle()V

    .line 386
    return-void

    .line 388
    :cond_32
    :try_start_32
    invoke-virtual {v1}, Landroid/os/Parcel;->readException()V
    :try_end_35
    .catchall {:try_start_32 .. :try_end_35} :catchall_3d

    .line 391
    .end local v2    # "_status":Z
    invoke-virtual {v1}, Landroid/os/Parcel;->recycle()V

    .line 392
    invoke-virtual {v0}, Landroid/os/Parcel;->recycle()V

    .line 393
    nop

    .line 394
    return-void

    .line 391
    :catchall_3d
    move-exception v2

    invoke-virtual {v1}, Landroid/os/Parcel;->recycle()V

    .line 392
    invoke-virtual {v0}, Landroid/os/Parcel;->recycle()V

    throw v2
.end method

.method public setInteractionState(I)V
    .registers 7
    .param p1, "flags"    # I
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Landroid/os/RemoteException;
        }
    .end annotation

    .line 283
    invoke-static {}, Landroid/os/Parcel;->obtain()Landroid/os/Parcel;

    move-result-object v0

    .line 284
    .local v0, "_data":Landroid/os/Parcel;
    invoke-static {}, Landroid/os/Parcel;->obtain()Landroid/os/Parcel;

    move-result-object v1

    .line 286
    .local v1, "_reply":Landroid/os/Parcel;
    :try_start_8
    const-string v2, "com.android.systemui.shared.recents.ISystemUiProxy"

    invoke-virtual {v0, v2}, Landroid/os/Parcel;->writeInterfaceToken(Ljava/lang/String;)V

    .line 287
    invoke-virtual {v0, p1}, Landroid/os/Parcel;->writeInt(I)V

    .line 288
    iget-object v2, p0, Lcom/android/systemui/shared/recents/ISystemUiProxy$Stub$Proxy;->mRemote:Landroid/os/IBinder;

    const/4 v3, 0x5

    const/4 v4, 0x0

    invoke-interface {v2, v3, v0, v1, v4}, Landroid/os/IBinder;->transact(ILandroid/os/Parcel;Landroid/os/Parcel;I)Z

    move-result v2

    .line 289
    .local v2, "_status":Z
    if-nez v2, :cond_2e

    invoke-static {}, Lcom/android/systemui/shared/recents/ISystemUiProxy$Stub;->getDefaultImpl()Lcom/android/systemui/shared/recents/ISystemUiProxy;

    move-result-object v3

    if-eqz v3, :cond_2e

    .line 290
    invoke-static {}, Lcom/android/systemui/shared/recents/ISystemUiProxy$Stub;->getDefaultImpl()Lcom/android/systemui/shared/recents/ISystemUiProxy;

    move-result-object v3

    invoke-interface {v3, p1}, Lcom/android/systemui/shared/recents/ISystemUiProxy;->setInteractionState(I)V
    :try_end_27
    .catchall {:try_start_8 .. :try_end_27} :catchall_39

    .line 296
    invoke-virtual {v1}, Landroid/os/Parcel;->recycle()V

    .line 297
    invoke-virtual {v0}, Landroid/os/Parcel;->recycle()V

    .line 291
    return-void

    .line 293
    :cond_2e
    :try_start_2e
    invoke-virtual {v1}, Landroid/os/Parcel;->readException()V
    :try_end_31
    .catchall {:try_start_2e .. :try_end_31} :catchall_39

    .line 296
    .end local v2    # "_status":Z
    invoke-virtual {v1}, Landroid/os/Parcel;->recycle()V

    .line 297
    invoke-virtual {v0}, Landroid/os/Parcel;->recycle()V

    .line 298
    nop

    .line 299
    return-void

    .line 296
    :catchall_39
    move-exception v2

    invoke-virtual {v1}, Landroid/os/Parcel;->recycle()V

    .line 297
    invoke-virtual {v0}, Landroid/os/Parcel;->recycle()V

    throw v2
.end method

.method public startScreenPinning(I)V
    .registers 7
    .param p1, "taskId"    # I
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Landroid/os/RemoteException;
        }
    .end annotation

    .line 261
    invoke-static {}, Landroid/os/Parcel;->obtain()Landroid/os/Parcel;

    move-result-object v0

    .line 262
    .local v0, "_data":Landroid/os/Parcel;
    invoke-static {}, Landroid/os/Parcel;->obtain()Landroid/os/Parcel;

    move-result-object v1

    .line 264
    .local v1, "_reply":Landroid/os/Parcel;
    :try_start_8
    const-string v2, "com.android.systemui.shared.recents.ISystemUiProxy"

    invoke-virtual {v0, v2}, Landroid/os/Parcel;->writeInterfaceToken(Ljava/lang/String;)V

    .line 265
    invoke-virtual {v0, p1}, Landroid/os/Parcel;->writeInt(I)V

    .line 266
    iget-object v2, p0, Lcom/android/systemui/shared/recents/ISystemUiProxy$Stub$Proxy;->mRemote:Landroid/os/IBinder;

    const/4 v3, 0x2

    const/4 v4, 0x0

    invoke-interface {v2, v3, v0, v1, v4}, Landroid/os/IBinder;->transact(ILandroid/os/Parcel;Landroid/os/Parcel;I)Z

    move-result v2

    .line 267
    .local v2, "_status":Z
    if-nez v2, :cond_2e

    invoke-static {}, Lcom/android/systemui/shared/recents/ISystemUiProxy$Stub;->getDefaultImpl()Lcom/android/systemui/shared/recents/ISystemUiProxy;

    move-result-object v3

    if-eqz v3, :cond_2e

    .line 268
    invoke-static {}, Lcom/android/systemui/shared/recents/ISystemUiProxy$Stub;->getDefaultImpl()Lcom/android/systemui/shared/recents/ISystemUiProxy;

    move-result-object v3

    invoke-interface {v3, p1}, Lcom/android/systemui/shared/recents/ISystemUiProxy;->startScreenPinning(I)V
    :try_end_27
    .catchall {:try_start_8 .. :try_end_27} :catchall_39

    .line 274
    invoke-virtual {v1}, Landroid/os/Parcel;->recycle()V

    .line 275
    invoke-virtual {v0}, Landroid/os/Parcel;->recycle()V

    .line 269
    return-void

    .line 271
    :cond_2e
    :try_start_2e
    invoke-virtual {v1}, Landroid/os/Parcel;->readException()V
    :try_end_31
    .catchall {:try_start_2e .. :try_end_31} :catchall_39

    .line 274
    .end local v2    # "_status":Z
    invoke-virtual {v1}, Landroid/os/Parcel;->recycle()V

    .line 275
    invoke-virtual {v0}, Landroid/os/Parcel;->recycle()V

    .line 276
    nop

    .line 277
    return-void

    .line 274
    :catchall_39
    move-exception v2

    invoke-virtual {v1}, Landroid/os/Parcel;->recycle()V

    .line 275
    invoke-virtual {v0}, Landroid/os/Parcel;->recycle()V

    throw v2
.end method
