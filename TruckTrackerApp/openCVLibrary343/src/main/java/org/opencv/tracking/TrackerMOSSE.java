//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.tracking;

// C++: class TrackerMOSSE
//javadoc: TrackerMOSSE

public class TrackerMOSSE extends Tracker {

    protected TrackerMOSSE(long addr) {
        super(addr);
    }

    // internal usage only
    public static TrackerMOSSE __fromPtr__(long addr) {
        return new TrackerMOSSE(addr);
    }

    //
    // C++: static Ptr_TrackerMOSSE cv::TrackerMOSSE::create()
    //

    //javadoc: TrackerMOSSE::create()
    public static TrackerMOSSE create() {

        TrackerMOSSE retVal = TrackerMOSSE.__fromPtr__(create_0());

        return retVal;
    }

    // C++: static Ptr_TrackerMOSSE cv::TrackerMOSSE::create()
    private static native long create_0();

    // native support for java finalize()
    private static native void delete(long nativeObj);

    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }

}
