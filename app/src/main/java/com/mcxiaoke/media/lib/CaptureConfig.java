package com.mcxiaoke.media.lib;

import android.media.MediaRecorder.AudioEncoder;
import android.media.MediaRecorder.AudioSource;
import android.media.MediaRecorder.OutputFormat;
import android.media.MediaRecorder.VideoEncoder;
import android.media.MediaRecorder.VideoSource;

import java.io.File;

/**
 * User: mcxiaoke
 * Date: 2017/7/3
 * Time: 16:17
 */

public class CaptureConfig {
    public static final int FORMAT_MP4 = OutputFormat.MPEG_4;
    public static final int ENC_H264 = VideoEncoder.H264;
    public static final int ENC_AAC = AudioEncoder.AAC;

    public static final int VIDEO_IN = VideoSource.CAMERA;
    public static final int AUDIO_IN = AudioSource.CAMCORDER;

    public static final int V480P_W = 640;
    public static final int V480P_H = 480;

    // 480p 640*480
    public int videoWidth = V480P_W;
    public int videoHeight = V480P_H;
    public int videoFrameRate = 24;
    public int videoBitRate = videoWidth * videoHeight * 3;

    public int audioSamplingRate = 44100;
    public int audioChannels = 2;
    public int audioBitRate = 64 * 1024;

    public File outputFile;
}
