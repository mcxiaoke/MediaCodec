package com.mcxiaoke.media;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaCodecInfo;
import android.media.MediaCodecInfo.CodecCapabilities;
import android.media.MediaCodecList;
import android.util.Log;
import com.mcxiaoke.media.ocodec.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: mcxiaoke
 * Date: 2017/7/7
 * Time: 16:43
 */

public class VRecorder {
    public static final String TAG = "VRecorder";
    public static final boolean DEBUG = true;

    public static final String CODEC_VIDEO_H264 = Config.CODEC_VIDEO_H264;
    public static final String CODEC_AUDIO_AAC = Config.CODEC_AUDIO_AAC;
    public static final String[] H264_ACC = new String[]{CODEC_VIDEO_H264, CODEC_AUDIO_AAC};

    private static List<MediaCodecInfo> sSupportedEncoders;
    //    private static List<String> sSupportedTypes;
    private static boolean sSupported;

    static {
        int total = MediaCodecList.getCodecCount();
        List<MediaCodecInfo> codecs = new ArrayList<>();
        List<String> types = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
            if (!codecInfo.isEncoder()) {
                continue;
            }
            codecs.add(codecInfo);
            String[] supportedTypes = codecInfo.getSupportedTypes();
            if (supportedTypes.length > 0) {
                types.addAll(Arrays.asList(supportedTypes));
            }
        }
        sSupportedEncoders = codecs;
//        sSupportedTypes = types;
        sSupported = types.containsAll(Arrays.asList(H264_ACC));
    }

    public static void showSupportedTypes() {
        int total = MediaCodecList.getCodecCount();
        for (int i = 0; i < total; i++) {
            MediaCodecInfo codec = MediaCodecList.getCodecInfoAt(i);
            if (!codec.isEncoder()) {
                continue;
            }
            String[] types = codec.getSupportedTypes();
            Log.v(TAG, Arrays.toString(types)
                    + " name" + codec.getName());
            for (String type : types) {
                CodecCapabilities caps = codec.getCapabilitiesForType(type);
                Log.v(TAG, caps.getMimeType() + " colorFormats:" + Arrays.toString(caps.colorFormats));
            }
        }
    }

    public static MediaCodecInfo selectCodec(String mimeType) {
        for (MediaCodecInfo codec : sSupportedEncoders) {
            String[] types = codec.getSupportedTypes();
            for (final String type : types) {
                if (type.equalsIgnoreCase(mimeType)) {
                    return codec;
                }
            }
        }
        return null;
    }

    public static boolean hasCamera(Context ctx) {
        return ctx.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public static boolean isSupported(Context context) {
        return sSupported && hasCamera(context);
    }
}
