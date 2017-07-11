/***
 Copyright (c) 2013 CommonsWare, LLC

 Licensed under the Apache License, Version 2.0 (the "License"); you may
 not use this file except in compliance with the License. You may obtain
 a copy of the License at
 http://www.apache.org/licenses/LICENSE-2.0
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.mcxiaoke.media;

import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CameraUtils {

    private static final double ASPECT_TOLERANCE = 0.1;

    public static Size getOptimalVideoSize(int width,
                                           int height,
                                           Camera.Parameters parameters) {
        return getOptimalPreviewSize(width, height,
                parameters.getSupportedVideoSizes());
    }

    public static Size getOptimalPreviewSize(Camera.Size videoSize, Camera.Parameters parameters) {
        return getBestAspectPreviewSize(videoSize.width * 2, videoSize.height * 2, parameters);
    }

    private static Size getOptimalPreviewSize(int width,
                                              int height,
                                              List<Size> sizes) {
        double targetRatio = (double) width / height;
        Size optimalSize = null;
        int minDiff = Integer.MAX_VALUE;
        int targetHeight = height;
//        if (displayOrientation == 90 || displayOrientation == 270) {
//            targetRatio = (double) height / width;
//        }
        Log.v(Const.TAG, "checkSize: targetRatio=" + targetRatio + " targetHeight=" + targetHeight);
        // Try to find an size match aspect ratio and size

        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) <= ASPECT_TOLERANCE) {
                Log.v(Const.TAG, "checkSize: matched w=" + size.width + " h=" + size.height);
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }

        // Cannot find the one match the aspect ratio, ignore
        // the requirement

        if (optimalSize == null) {
            minDiff = Integer.MAX_VALUE;

            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    Log.v(Const.TAG, "checkSize2: matched w=" + size.width + " h=" + size.height);
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }

        return (optimalSize);
    }

    public static Size getBestAspectPreviewSize(int width,
                                                int height,
                                                Camera.Parameters parameters) {
        return (getBestAspectPreviewSize(width, height, parameters, 0.01d));
    }

    public static Size getBestAspectPreviewSize(int width,
                                                int height,
                                                Camera.Parameters parameters,
                                                double closeEnough) {
        double targetRatio = (double) width / height;
        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        List<Size> sizes = parameters.getSupportedPreviewSizes();

        Collections.sort(sizes,
                Collections.reverseOrder(new SizeComparator()));

        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;

            Log.v(Const.TAG, "check best ratio w=" + size.width + " h=" + size.height);

            if (Math.abs(ratio - targetRatio) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(ratio - targetRatio);
            }

            if (minDiff < closeEnough) {
                break;
            }
        }

        return (optimalSize);
    }

    private static class SizeComparator implements
            Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            int left = lhs.width * lhs.height;
            int right = rhs.width * rhs.height;

            if (left < right) {
                return (-1);
            } else if (left > right) {
                return (1);
            }

            return (0);
        }
    }
}
