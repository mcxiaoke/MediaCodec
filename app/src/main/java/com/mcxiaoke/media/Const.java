package com.mcxiaoke.media;

import android.os.Environment;

import java.io.File;

/**
 * User: mcxiaoke
 * Date: 2017/6/29
 * Time: 14:15
 */

public class Const {
    public static final String TAG = "MediaCodec";

    // parameters for the encoder
    private static final String MIME_TYPE = "video/avc";    // H.264 Advanced Video Coding
    private static final int FRAME_RATE = 24;               // 24fps
    private static final int I_FRAME_INTERVAL = 10;          // 10 seconds between I-frames
    private static final File OUTPUT_DIR = new File(Environment.getExternalStorageDirectory(), "test");
}
