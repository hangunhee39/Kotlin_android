package hgh.project.camera_x;

import java.lang.System;

@kotlin.Metadata(mv = {1, 6, 0}, k = 1, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u0000 \u00162\u00020\u0001:\u0001\u0016B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u000e\u001a\u00020\u000fH\u0002J\u0012\u0010\u0010\u001a\u00020\u000f2\b\u0010\u0011\u001a\u0004\u0018\u00010\u0012H\u0014J\u0010\u0010\u0013\u001a\u00020\u000f2\u0006\u0010\u0014\u001a\u00020\tH\u0002J\b\u0010\u0015\u001a\u00020\u000fH\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R!\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\f\u0010\r\u001a\u0004\b\n\u0010\u000b\u00a8\u0006\u0017"}, d2 = {"Lhgh/project/camera_x/ImageListActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "binding", "Lhgh/project/camera_x/databinding/ActivityImageListBinding;", "imageViewPagerAdapter", "Lhgh/project/camera_x/adapter/ImageViewPagerAdapter;", "uriList", "", "Landroid/net/Uri;", "getUriList", "()Ljava/util/List;", "uriList$delegate", "Lkotlin/Lazy;", "initViews", "", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "removeImage", "uri", "setupImageList", "Companion", "app_debug"})
public final class ImageListActivity extends androidx.appcompat.app.AppCompatActivity {
    @org.jetbrains.annotations.NotNull()
    public static final hgh.project.camera_x.ImageListActivity.Companion Companion = null;
    private static final java.lang.String URI_LIST_KEY = "uriList";
    private hgh.project.camera_x.databinding.ActivityImageListBinding binding;
    private hgh.project.camera_x.adapter.ImageViewPagerAdapter imageViewPagerAdapter;
    private final kotlin.Lazy uriList$delegate = null;
    
    public ImageListActivity() {
        super();
    }
    
    private final java.util.List<android.net.Uri> getUriList() {
        return null;
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void initViews() {
    }
    
    private final void setupImageList() {
    }
    
    private final void removeImage(android.net.Uri uri) {
    }
    
    @kotlin.Metadata(mv = {1, 6, 0}, k = 1, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u001c\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\nR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\f"}, d2 = {"Lhgh/project/camera_x/ImageListActivity$Companion;", "", "()V", "URI_LIST_KEY", "", "newIntent", "Landroid/content/Intent;", "activity", "Landroid/app/Activity;", "uriList", "", "Landroid/net/Uri;", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final android.content.Intent newIntent(@org.jetbrains.annotations.NotNull()
        android.app.Activity activity, @org.jetbrains.annotations.NotNull()
        java.util.List<? extends android.net.Uri> uriList) {
            return null;
        }
    }
}