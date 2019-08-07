//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.tracking;

import org.opencv.core.Algorithm;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect2d;
import org.opencv.core.Rect2d;

// C++: class MultiTracker
//javadoc: MultiTracker

public class MultiTracker extends Algorithm {

    protected MultiTracker(long addr) {
        super(addr);
    }

    //javadoc: MultiTracker::MultiTracker()
    public MultiTracker() {

        super(MultiTracker_0());

        return;
    }

    //
    // C++:   cv::MultiTracker::MultiTracker()
    //

    // internal usage only
    public static MultiTracker __fromPtr__(long addr) {
        return new MultiTracker(addr);
    }


    //
    // C++: static Ptr_MultiTracker cv::MultiTracker::create()
    //

    //javadoc: MultiTracker::create()
    public static MultiTracker create() {

        MultiTracker retVal = MultiTracker.__fromPtr__(create_0());

        return retVal;
    }


    //
    // C++:  bool cv::MultiTracker::add(Ptr_Tracker newTracker, Mat image, Rect2d boundingBox)
    //

    // C++:   cv::MultiTracker::MultiTracker()
    private static native long MultiTracker_0();


    //
    // C++:  bool cv::MultiTracker::update(Mat image, vector_Rect2d& boundingBox)
    //

    // C++: static Ptr_MultiTracker cv::MultiTracker::create()
    private static native long create_0();


    //
    // C++:  vector_Rect2d cv::MultiTracker::getObjects()
    //

    // C++:  bool cv::MultiTracker::add(Ptr_Tracker newTracker, Mat image, Rect2d boundingBox)
    private static native boolean add_0(long nativeObj, long newTracker_nativeObj, long image_nativeObj, double boundingBox_x, double boundingBox_y, double boundingBox_width, double boundingBox_height);

    // C++:  bool cv::MultiTracker::update(Mat image, vector_Rect2d& boundingBox)
    private static native boolean update_0(long nativeObj, long image_nativeObj, long boundingBox_mat_nativeObj);

    // C++:  vector_Rect2d cv::MultiTracker::getObjects()
    private static native long getObjects_0(long nativeObj);

    // native support for java finalize()
    private static native void delete(long nativeObj);

    //javadoc: MultiTracker::add(newTracker, image, boundingBox)
    public boolean add(Tracker newTracker, Mat image, Rect2d boundingBox) {

        boolean retVal = add_0(nativeObj, newTracker.getNativeObjAddr(), image.nativeObj, boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);

        return retVal;
    }

    //javadoc: MultiTracker::update(image, boundingBox)
    public boolean update(Mat image, MatOfRect2d boundingBox) {
        Mat boundingBox_mat = boundingBox;
        boolean retVal = update_0(nativeObj, image.nativeObj, boundingBox_mat.nativeObj);

        return retVal;
    }

    //javadoc: MultiTracker::getObjects()
    public MatOfRect2d getObjects() {

        MatOfRect2d retVal = MatOfRect2d.fromNativeAddr(getObjects_0(nativeObj));

        return retVal;
    }

    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }

}
