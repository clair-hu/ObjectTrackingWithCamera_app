//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.tracking;

// C++: class TrackerCSRT
//javadoc: TrackerCSRT

public class TrackerCSRT extends Tracker {

    protected TrackerCSRT(long addr) {
        super(addr);
    }

    // internal usage only
    public static TrackerCSRT __fromPtr__(long addr) {
        return new TrackerCSRT(addr);
    }

    //
    // C++: static Ptr_TrackerCSRT cv::TrackerCSRT::create()
    //

    //javadoc: TrackerCSRT::create()
    public static TrackerCSRT create() {

        TrackerCSRT retVal = TrackerCSRT.__fromPtr__(create_0());

        return retVal;
    }

    // C++: static Ptr_TrackerCSRT cv::TrackerCSRT::create()
    private static native long create_0();

    // native support for java finalize()
    private static native void delete(long nativeObj);

    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }

}
