package hgh.project.camera_x.util;

import java.lang.System;

@kotlin.Metadata(mv = {1, 6, 0}, k = 1, d1 = {"\u0000T\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J;\u0010\u0003\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u0005\u001a\u00020\u00062\b\u0010\u0007\u001a\u0004\u0018\u00010\b2\b\u0010\t\u001a\u0004\u0018\u00010\u00042\u000e\u0010\n\u001a\n\u0012\u0004\u0012\u00020\u0004\u0018\u00010\u000bH\u0002\u00a2\u0006\u0002\u0010\fJ\u000e\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0010J,\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0007\u001a\u00020\b2\u0012\u0010\u0015\u001a\u000e\u0012\u0004\u0012\u00020\u0017\u0012\u0004\u0012\u00020\u00120\u0016H\u0007J\u0018\u0010\u0018\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bJ\u0010\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u0007\u001a\u00020\bH\u0002J\u0010\u0010\u001b\u001a\u00020\u001a2\u0006\u0010\u0007\u001a\u00020\bH\u0002J\u0010\u0010\u001c\u001a\u00020\u001a2\u0006\u0010\u0007\u001a\u00020\bH\u0002\u00a8\u0006\u001d"}, d2 = {"Lhgh/project/camera_x/util/PathUtil;", "", "()V", "getDataColumn", "", "context", "Landroid/content/Context;", "uri", "Landroid/net/Uri;", "selection", "selectionArgs", "", "(Landroid/content/Context;Landroid/net/Uri;Ljava/lang/String;[Ljava/lang/String;)Ljava/lang/String;", "getOutputDirectory", "Ljava/io/File;", "activity", "Landroid/app/Activity;", "getOutputDirectoryAndWrite", "", "resolver", "Landroid/content/ContentResolver;", "write", "Lkotlin/Function1;", "Ljava/io/FileOutputStream;", "getPath", "isDownloadsDocument", "", "isExternalStorageDocument", "isMediaDocument", "app_debug"})
public final class PathUtil {
    @org.jetbrains.annotations.NotNull()
    public static final hgh.project.camera_x.util.PathUtil INSTANCE = null;
    
    private PathUtil() {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getPath(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    android.net.Uri uri) {
        return null;
    }
    
    private final java.lang.String getDataColumn(android.content.Context context, android.net.Uri uri, java.lang.String selection, java.lang.String[] selectionArgs) {
        return null;
    }
    
    private final boolean isExternalStorageDocument(android.net.Uri uri) {
        return false;
    }
    
    private final boolean isDownloadsDocument(android.net.Uri uri) {
        return false;
    }
    
    private final boolean isMediaDocument(android.net.Uri uri) {
        return false;
    }
    
    @android.annotation.TargetApi(value = android.os.Build.VERSION_CODES.Q)
    public final void getOutputDirectoryAndWrite(@org.jetbrains.annotations.NotNull()
    android.content.ContentResolver resolver, @org.jetbrains.annotations.NotNull()
    android.net.Uri uri, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.io.FileOutputStream, kotlin.Unit> write) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.io.File getOutputDirectory(@org.jetbrains.annotations.NotNull()
    android.app.Activity activity) {
        return null;
    }
}