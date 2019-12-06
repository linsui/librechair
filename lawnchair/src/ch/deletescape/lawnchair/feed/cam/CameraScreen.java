/*
 * Copyright (c) 2019 oldosfan.
 * Copyright (c) 2019 the Lawnchair developers
 *
 *     This file is part of Librechair.
 *
 *     Librechair is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Librechair is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Librechair.  If not, see <https://www.gnu.org/licenses/>.
 */

package ch.deletescape.lawnchair.feed.cam;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;

import com.android.launcher3.R;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Objects;
import java.util.function.Consumer;

import ch.deletescape.lawnchair.BlankActivity;
import ch.deletescape.lawnchair.LawnchairUtilsKt;
import ch.deletescape.lawnchair.feed.FeedProvider;
import ch.deletescape.lawnchair.feed.ProviderScreen;
import kotlin.Unit;

public class CameraScreen extends ProviderScreen {
    private final Handler cameraHandler;
    private final Consumer<Bitmap> listener;
    private final CameraManager cameraManager;
    private String cameraId;
    private Size previewSize;
    private CameraDevice dev;
    private CameraCaptureSession session;
    private int facing = CameraCharacteristics.LENS_FACING_BACK;

    public CameraScreen(Context base, Consumer<Bitmap> listener) {
        super(base);
        this.listener = listener;
        this.cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        HandlerThread thread = new HandlerThread("camera-1");
        thread.start();
        cameraHandler = new Handler(thread.getLooper());
    }

    @Override
    protected View getView(ViewGroup parent) {
        return LawnchairUtilsKt.inflate(parent, R.layout.camera_screen);
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void bindView(View view) {
        TextureView cam = view.findViewById(R.id.camera);
        View takeImage = view.findViewById(R.id.take_foto);
        if (getPackageManager().checkPermission(Manifest.permission.CAMERA,
                getPackageName()) == PackageManager.PERMISSION_GRANTED) {
            takeImage.setOnClickListener(v -> {
                listener.accept(cam.getBitmap());
                finish();
            });
            cam.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surface, int width,
                                                      int height) {
                    setUpCamera(this, cam);
                    clearActions();
                    addAction(new FeedProvider.Action(getDrawable(R.drawable.ic_lawnstep),
                            getString(R.string.title_action_rotate), () -> {
                        if (session != null) {
                            dev.close();
                            session.close();
                        }
                        facing = facing ==
                                CameraMetadata.LENS_FACING_BACK ? CameraMetadata.LENS_FACING_FRONT : CameraMetadata.LENS_FACING_BACK;
                        setUpCamera(this, cam);
                    }));
                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width,
                                                        int height) {
                }

                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                    return true;
                }

                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture surface) {

                }
            });
        } else {
            if (getBoundFeed() != null) {
                getBoundFeed().closeScreen(this);
                BlankActivity.Companion.requestPermission(this, Manifest.permission.CAMERA,
                        Manifest.permission.CAMERA.hashCode(), b -> Unit.INSTANCE);
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    @RequiresPermission(Manifest.permission.CAMERA)
    private void setUpCamera(TextureView.SurfaceTextureListener listener, TextureView textureView) {
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics cameraCharacteristics =
                        cameraManager.getCameraCharacteristics(cameraId);
                Objects.requireNonNull(cameraCharacteristics);
                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) ==
                        facing) {
                    StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(
                            CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    this.previewSize = Objects.requireNonNull(
                            streamConfigurationMap).getOutputSizes(SurfaceTexture.class)[0];
                    this.cameraId = cameraId;

                    cameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {
                        @Override
                        public void onOpened(@NonNull CameraDevice camera) {
                            dev = camera;
                            createPreviewSession(textureView);
                        }

                        @Override
                        public void onDisconnected(@NonNull CameraDevice camera) {
                            dev = null;
                            if (session != null) {
                                session.close();
                            }
                        }

                        @Override
                        public void onError(@NonNull CameraDevice camera, int error) {
                            session = null;
                            dev = null;
                        }
                    }, cameraHandler);
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (session != null) {
            dev.close();
            session.close();
        }
        cameraHandler.getLooper().quit();
    }

    private void createPreviewSession(TextureView textureView) {
        try {
            SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
            surfaceTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
            Surface previewSurface = new Surface(surfaceTexture);
            CaptureRequest.Builder captureRequestBuilder = dev.createCaptureRequest(
                    CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(previewSurface);

            dev.createCaptureSession(Collections.singletonList(previewSurface),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(
                                @NotNull CameraCaptureSession cameraCaptureSession) {
                            if (dev == null) {
                                return;
                            }

                            try {
                                CaptureRequest captureRequest = captureRequestBuilder.build();
                                session = cameraCaptureSession;
                                session.setRepeatingRequest(captureRequest,
                                        null, cameraHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(
                                @NotNull CameraCaptureSession cameraCaptureSession) {

                        }
                    }, cameraHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
}
