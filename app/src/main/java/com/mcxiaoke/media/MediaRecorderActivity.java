package com.mcxiaoke.media;

import android.app.Activity;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioEncoder;
import android.media.MediaRecorder.AudioSource;
import android.media.MediaRecorder.OutputFormat;
import android.media.MediaRecorder.VideoEncoder;
import android.media.MediaRecorder.VideoSource;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.mcxiaoke.media.ocodec.CodecUtils;
import com.mcxiaoke.media.support.CameraPreview;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * User: mcxiaoke
 * Date: 2017/6/29
 * Time: 15:22
 */

public class MediaRecorderActivity extends Activity {
    public static final String TAG = Const.TAG;

    private Camera mCamera;
    private int mOrientation;
    private Camera.Size mVideoSize;
    private Camera.Size mPreviewSize;
    private MediaRecorder mMediaRecorder;
    private File mOutputFile;
    private CameraPreview mPreview;
    private boolean isRecording;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_recorder);
        ButterKnife.bind(this);
        new Thread() {
            @Override
            public void run() {
                mCamera = openCamera();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Create our Preview view and set it as the content of our activity.
                        mPreview = new CameraPreview(MediaRecorderActivity.this, mCamera);
                        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
                        preview.addView(mPreview);
                    }
                });
            }
        }.start();
        VRecorder.showSupportedTypes();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaRecorder();
        releaseCamera();
    }

    @OnClick(R.id.start)
    void onStart(View v) {
        if (!isRecording) {
            // initialize video camera
            if (prepareVideoRecorder()) {
                // Camera is available and unlocked, MediaRecorder is prepared,
                // now you can start recording
                mMediaRecorder.start();

                // inform the user that recording has started
                isRecording = true;
                Log.d(TAG, "started, file=" + mOutputFile);
                Toast.makeText(this, "Start recording!", Toast.LENGTH_LONG).show();
            } else {
                // prepare didn't work, release the camera
                releaseMediaRecorder();
                // inform user
            }
        }
    }

    @OnClick(R.id.stop)
    void onStop(View v) {
        if (isRecording) {
            // stop recording and release camera
            mMediaRecorder.stop();  // stop the recording
            releaseMediaRecorder(); // release the MediaRecorder object
            mCamera.lock();         // take camera access back from MediaRecorder

            // inform the user that recording has stopped
            isRecording = false;
            Log.d(TAG, "stopped, file=" + mOutputFile);
            Toast.makeText(this, "Stop recording!", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public Camera openCamera() {
        Camera c = null;
        try {
            c = Camera.open(CameraInfo.CAMERA_FACING_BACK); // attempt to get a Camera instance
            // get Camera parameters
            Camera.Parameters params = c.getParameters();
            mOrientation = CodecUtils.getDisplayOrientation(this, CameraInfo.CAMERA_FACING_BACK);
            Point p = new Point();
            getWindowManager().getDefaultDisplay().getSize(p);
            Log.d(TAG, "() orientation=" + mOrientation);
            Log.d(TAG, "() screen size=" + p);
            mVideoSize = CameraUtils.getOptimalVideoSize(720, 480, params);
            mPreviewSize = CameraUtils.getOptimalPreviewSize(mVideoSize, params);
            Log.d(TAG, "() video size=" + mVideoSize.width + "x" + mVideoSize.height);
            Log.d(TAG, "() preview size=" + mPreviewSize.width + "x" + mPreviewSize.height);
            params.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
            params.setRotation(mOrientation);
            params.setRecordingHint(true);
            params.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            params.setFlashMode(Parameters.FLASH_MODE_OFF);
            params.setSceneMode(Parameters.SCENE_MODE_AUTO);
            c.setParameters(params);
            c.setDisplayOrientation(mOrientation);

            CodecUtils.showInfo(this, c);
//            Log.v(TAG, "Camera Parameters: " + params.flatten());
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
            e.printStackTrace();
        }
        return c; // returns null if camera is unavailable
    }

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    /**
     * Create a file Uri for saving an image or video
     */
    private static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * Create a File for saving an image or video
     */
    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "test");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MediaCodec", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir, "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir, "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    private boolean prepareVideoRecorder() {

        mMediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        // Step 2: Set sources
        mMediaRecorder.setAudioSource(AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(VideoSource.CAMERA);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
//        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));

        CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_LOW);
        profile.audioCodec = AudioEncoder.AAC;
        profile.audioSampleRate = 44100;
        profile.audioSampleRate = 64 * 1024;
        profile.audioChannels = 2;

        profile.videoCodec = VideoEncoder.H264;
        profile.videoFrameRate = 24;
        profile.videoFrameWidth = mVideoSize.width;
        profile.videoFrameHeight = mVideoSize.height;
        profile.videoBitRate = mVideoSize.width * mVideoSize.height * 3;

        profile.fileFormat = OutputFormat.MPEG_4;

        mMediaRecorder.setProfile(profile);

//        mMediaRecorder.setOutputFormat(OutputFormat.MPEG_4);
//        mMediaRecorder.setAudioEncoder(AudioEncoder.AAC);
//        mMediaRecorder.setAudioSamplingRate(44100);
//        mMediaRecorder.setAudioEncodingBitRate(64 * 1024);
//        mMediaRecorder.setAudioChannels(2);
//
//        mMediaRecorder.setVideoEncoder(VideoEncoder.H264);
//        mMediaRecorder.setVideoFrameRate(24);
//        mMediaRecorder.setVideoEncodingBitRate(mSize.width * mSize.height * 4);
//        mMediaRecorder.setVideoSize(mSize.width, mSize.height);

        // Step 4: Set output file
        mOutputFile = getOutputMediaFile(MEDIA_TYPE_VIDEO);
        mMediaRecorder.setOutputFile(mOutputFile.getPath());

        // Step 5: Set the preview output
//        mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());

        mMediaRecorder.setOrientationHint(mOrientation);

        // Step 6: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
            return true;
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaRecorder();       // if you are using MediaRecorder, release it first
        releaseCamera();              // release the camera immediately on pause event
    }

    private void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock();           // lock camera for later use
        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }


}
