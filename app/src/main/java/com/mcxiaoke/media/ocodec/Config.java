package com.mcxiaoke.media.ocodec;

import android.media.AudioFormat;
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

public class Config {
    public static final String CODEC_VIDEO_H264 = "video/avc";
    public static final String CODEC_AUDIO_AAC = "audio/aac";
    public static final int IFRAME_INTERVAL = 5;
    public static final String FILE_MP4 = ".mp4";
    public static final int FORMAT_MP4 = OutputFormat.MPEG_4;
    public static final int ENC_H264 = VideoEncoder.H264;
    public static final int ENC_AAC = AudioEncoder.AAC;

    public static final int VIDEO_IN = VideoSource.CAMERA;
    public static final int AUDIO_IN = AudioSource.CAMCORDER;

    public static final int V480P_W = 720;
    public static final int V480P_H = 480;

    public int outputFormat = FORMAT_MP4;
    public File outputFile;

    public int videoEncoder = ENC_H264;
    public int audioEncoder = ENC_AAC;
    public int videoSource = VIDEO_IN;
    public int audioSource = AUDIO_IN;
    public String videoType = CODEC_VIDEO_H264;
    public String audioType = CODEC_AUDIO_AAC;
    // 480p 480*720
    public int videoWidth = V480P_W;
    public int videoHeight = V480P_H;
    public int videoFrameRate = 25;
    public int videoBitRate = videoWidth * videoHeight * 3;

    public int audioSamplingRate = 44100;
    public int audioChannels = 2;
    public int audioBitRate = 64 * 1024;
    public int audioChannelMask = AudioFormat.CHANNEL_IN_STEREO;


}
