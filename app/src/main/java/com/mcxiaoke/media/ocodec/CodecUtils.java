package com.mcxiaoke.media.ocodec;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.Surface;
import com.mcxiaoke.media.Const;

import java.util.Arrays;

/**
 * User: mcxiaoke
 * Date: 2017/6/29
 * Time: 13:35
 */

public class CodecUtils {

    public static int getDisplayOrientation(Activity activity,
                                            int cameraId) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int orientation = activity.getResources().getConfiguration().orientation;
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        Log.v(Const.TAG, "getDisplayOrientation() orientation=" + orientation);
        Log.v(Const.TAG, "getDisplayOrientation() rotation=" + rotation);
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        Log.d(Const.TAG, "getDisplayOrientation() = " + result);
        return result;
    }

    public static int getOrientation(Context context) {
        final int orientation = context.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return 180;
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            return 90;
        }
        return 0;
    }

    public static void fixOrientation(Context context, Camera camera) {
        final int orientation = context.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            camera.setDisplayOrientation(180);
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            camera.setDisplayOrientation(90);
        }
    }

    public static void showInfo(Context context, Camera camera) {
        int don = context.getResources().getConfiguration().orientation;
        Log.v(Const.TAG, "Device Orientation = " + don);
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                Log.v(Const.TAG, i + "Camera Orientation=" + info.orientation);
            }
        }
        Camera.Parameters ps = camera.getParameters();
        for (Size s : ps.getSupportedPictureSizes()) {
//            Log.v(Const.TAG, "PictureSize: " + s.width + "x" + s.height);
        }
        Log.v(Const.TAG, "PictureFormats=" + ps.getSupportedPictureFormats());
        Log.v(Const.TAG, "PreviewFormats=" + ps.getSupportedPreviewFormats());
        for (int[] fs : ps.getSupportedPreviewFpsRange()) {
            Log.v(Const.TAG, "PreviewFpsRange: " + Arrays.toString(fs));
        }
        for (Size s : ps.getSupportedPreviewSizes()) {
            Log.v(Const.TAG, "PreviewSize: " + s.width + "x" + s.height);
        }
        for (Size s : ps.getSupportedVideoSizes()) {
            Log.v(Const.TAG, "VideoSize: " + s.width + "x" + s.height);
        }
    }

    public static void checkArgument(final boolean b) {
        // TODO: 2017/7/3  
    }
}
