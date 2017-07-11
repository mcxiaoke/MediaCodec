package com.mcxiaoke.media.recoder;

import android.app.Activity;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.util.Log;
import com.mcxiaoke.media.CameraUtils;
import com.mcxiaoke.media.Const;
import com.mcxiaoke.media.ocodec.CodecUtils;

/**
 * User: mcxiaoke
 * Date: 2017/7/5
 * Time: 16:33
 */

public class CameraProxy {
    public static final String TAG = Const.TAG;

    private Activity activity;

    private int orientation;
    private Camera.Size videoSize;
    private Camera.Size previewSize;

    public CameraProxy(Activity activity) {
        this.activity = activity;
    }


    public Camera openCamera() {
        Camera c = null;
        try {
            c = Camera.open(CameraInfo.CAMERA_FACING_BACK); // attempt to get a Camera instance
            // get Camera parameters
            Camera.Parameters params = c.getParameters();
            orientation = CodecUtils.getDisplayOrientation(activity, CameraInfo.CAMERA_FACING_BACK);
            Point p = new Point();
            activity.getWindowManager().getDefaultDisplay().getSize(p);
            Log.d(TAG, "() orientation=" + orientation);
            Log.d(TAG, "() screen size=" + p);
            videoSize = CameraUtils.getOptimalVideoSize(720, 480, params);
            previewSize = CameraUtils.getOptimalPreviewSize(videoSize, params);
            Log.d(TAG, "() video size=" + videoSize.width + "x" + videoSize.height);
            Log.d(TAG, "() preview size=" + previewSize.width + "x" + previewSize.height);
            params.setPreviewSize(previewSize.width, previewSize.height);
            params.setRotation(orientation);
            params.setRecordingHint(true);
            params.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            params.setFlashMode(Parameters.FLASH_MODE_OFF);
            params.setSceneMode(Parameters.SCENE_MODE_AUTO);
            c.setParameters(params);
            c.setDisplayOrientation(orientation);

            CodecUtils.showInfo(activity, c);
//            Log.v(TAG, "Camera Parameters: " + params.flatten());
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
            e.printStackTrace();
        }
        return c; // returns null if camera is unavailable
    }
}
