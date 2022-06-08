package hgh.project.camera_x;

import java.lang.System;

@kotlin.Metadata(mv = {1, 6, 0}, k = 1, d1 = {"\u0000\u0090\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0015\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u0000 =2\u00020\u0001:\u0001=B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010)\u001a\u00020$H\u0002J\b\u0010*\u001a\u00020+H\u0002J\b\u0010,\u001a\u00020+H\u0002J\b\u0010-\u001a\u00020+H\u0002J\u0012\u0010.\u001a\u00020+2\b\u0010/\u001a\u0004\u0018\u000100H\u0014J-\u00101\u001a\u00020+2\u0006\u00102\u001a\u00020\u00192\u000e\u00103\u001a\n\u0012\u0006\b\u0001\u0012\u000205042\u0006\u00106\u001a\u000207H\u0016\u00a2\u0006\u0002\u00108J\u0010\u00109\u001a\u00020+2\u0006\u0010:\u001a\u00020;H\u0002J\b\u0010<\u001a\u00020+H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000R\u001b\u0010\t\u001a\u00020\n8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\r\u0010\u000e\u001a\u0004\b\u000b\u0010\fR)\u0010\u000f\u001a\u0010\u0012\f\u0012\n \u0012*\u0004\u0018\u00010\u00110\u00110\u00108BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0015\u0010\u000e\u001a\u0004\b\u0013\u0010\u0014R\u0010\u0010\u0016\u001a\u0004\u0018\u00010\u0017X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u0019X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001a\u001a\u00020\u001bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u001c\u001a\u00020\u001d8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b \u0010\u000e\u001a\u0004\b\u001e\u0010\u001fR\u000e\u0010!\u001a\u00020\"X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010#\u001a\u00020$X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010%\u001a\u0004\u0018\u00010&X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\'\u001a\b\u0012\u0004\u0012\u00020\u00170(X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006>"}, d2 = {"Lhgh/project/camera_x/MainActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "binding", "Lhgh/project/camera_x/databinding/ActivityMainBinding;", "camera", "Landroidx/camera/core/Camera;", "cameraExecutor", "Ljava/util/concurrent/ExecutorService;", "cameraMainExecutor", "Ljava/util/concurrent/Executor;", "getCameraMainExecutor", "()Ljava/util/concurrent/Executor;", "cameraMainExecutor$delegate", "Lkotlin/Lazy;", "cameraProviderFuture", "Lcom/google/common/util/concurrent/ListenableFuture;", "Landroidx/camera/lifecycle/ProcessCameraProvider;", "kotlin.jvm.PlatformType", "getCameraProviderFuture", "()Lcom/google/common/util/concurrent/ListenableFuture;", "cameraProviderFuture$delegate", "contentUri", "Landroid/net/Uri;", "displayId", "", "displayListener", "Landroid/hardware/display/DisplayManager$DisplayListener;", "displayManager", "Landroid/hardware/display/DisplayManager;", "getDisplayManager", "()Landroid/hardware/display/DisplayManager;", "displayManager$delegate", "imageCapture", "Landroidx/camera/core/ImageCapture;", "isCapturing", "", "root", "Landroid/view/View;", "uriList", "", "allPermissionsGranted", "bindCameraUseCase", "", "bindCaptureListener", "captureCamera", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onRequestPermissionsResult", "requestCode", "permissions", "", "", "grantResults", "", "(I[Ljava/lang/String;[I)V", "startCamera", "viewFinder", "Landroidx/camera/view/PreviewView;", "updateSavedImageContent", "Companion", "app_debug"})
public final class MainActivity extends androidx.appcompat.app.AppCompatActivity {
    private hgh.project.camera_x.databinding.ActivityMainBinding binding;
    private java.util.concurrent.ExecutorService cameraExecutor;
    private final kotlin.Lazy cameraMainExecutor$delegate = null;
    private androidx.camera.core.ImageCapture imageCapture;
    private final kotlin.Lazy cameraProviderFuture$delegate = null;
    private androidx.camera.core.Camera camera;
    private android.view.View root;
    private boolean isCapturing = false;
    private android.net.Uri contentUri;
    private java.util.List<android.net.Uri> uriList;
    private final kotlin.Lazy displayManager$delegate = null;
    private int displayId = -1;
    private final android.hardware.display.DisplayManager.DisplayListener displayListener = null;
    @org.jetbrains.annotations.NotNull()
    public static final hgh.project.camera_x.MainActivity.Companion Companion = null;
    private static final int REQUEST_CODE_PERMISSIONS = 101;
    private static final java.lang.String[] REQUEST_PERMISSIONS = {"android.permission.CAMERA"};
    private static final int LENS_FACING = androidx.camera.core.CameraSelector.LENS_FACING_BACK;
    private static final java.lang.String FILENAME_FORMAT = "yyyy-MM-dd-HH--mm-ss-SS";
    
    public MainActivity() {
        super();
    }
    
    private final java.util.concurrent.Executor getCameraMainExecutor() {
        return null;
    }
    
    private final com.google.common.util.concurrent.ListenableFuture<androidx.camera.lifecycle.ProcessCameraProvider> getCameraProviderFuture() {
        return null;
    }
    
    private final android.hardware.display.DisplayManager getDisplayManager() {
        return null;
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void startCamera(androidx.camera.view.PreviewView viewFinder) {
    }
    
    private final void bindCameraUseCase() {
    }
    
    private final void bindCaptureListener() {
    }
    
    private final void updateSavedImageContent() {
    }
    
    private final void captureCamera() {
    }
    
    private final boolean allPermissionsGranted() {
        return false;
    }
    
    @java.lang.Override()
    public void onRequestPermissionsResult(int requestCode, @org.jetbrains.annotations.NotNull()
    java.lang.String[] permissions, @org.jetbrains.annotations.NotNull()
    int[] grantResults) {
    }
    
    @kotlin.Metadata(mv = {1, 6, 0}, k = 1, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000R\u0016\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00040\tX\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\n\u00a8\u0006\u000b"}, d2 = {"Lhgh/project/camera_x/MainActivity$Companion;", "", "()V", "FILENAME_FORMAT", "", "LENS_FACING", "", "REQUEST_CODE_PERMISSIONS", "REQUEST_PERMISSIONS", "", "[Ljava/lang/String;", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}