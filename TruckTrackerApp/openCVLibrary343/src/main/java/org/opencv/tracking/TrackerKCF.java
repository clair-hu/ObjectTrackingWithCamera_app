//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.tracking;

// C++: class TrackerKCF
//javadoc: TrackerKCF

public class TrackerKCF extends Tracker {

    public static final int
            GRAY = (1 << 0),
            CN = (1 << 1),
            CUSTOM = (1 << 2);

    protected TrackerKCF(long addr) {
        super(addr);
    }

    // internal usage only
    public static TrackerKCF __fromPtr__(long addr) {
        return new TrackerKCF(addr);
    }


    //
    // C++: static Ptr_TrackerKCF cv::TrackerKCF::create()
    //

    //javadoc: TrackerKCF::create()
    public static TrackerKCF create() {

        TrackerKCF retVal = TrackerKCF.__fromPtr__(create_0());

        return retVal;
    }

    // C++: static Ptr_TrackerKCF cv::TrackerKCF::create()
    private static native long create_0();

    // native support for java finalize()
    private static native void delete(long nativeObj);

    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }

}
