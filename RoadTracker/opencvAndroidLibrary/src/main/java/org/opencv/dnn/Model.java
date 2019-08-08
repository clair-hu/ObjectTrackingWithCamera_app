//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.dnn;

import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.dnn.Net;
import org.opencv.utils.Converters;

// C++: class Model
/**
 * This class is presented high-level API for neural networks.
 *
 * Model allows to set params for preprocessing input image.
 * Model creates net from file with trained weights and config,
 * sets preprocessing input and runs forward pass.
 */
public class Model extends Net {

    protected Model(long addr) { super(addr); }

    // internal usage only
    public static Model __fromPtr__(long addr) { return new Model(addr); }

    //
    // C++:   cv::dnn::Model::Model(Net network)
    //

    /**
     * Create model from deep learning network.
     * @param network Net object.
     */
    public Model(Net network) {
        super(Model_0(network.nativeObj));
    }


    //
    // C++:   cv::dnn::Model::Model(String model, String config = "")
    //

    /**
     * Create model from deep learning network represented in one of the supported formats.
     * An order of {@code model} and {@code config} arguments does not matter.
     * @param model Binary file contains trained weights.
     * @param config Text file contains network configuration.
     */
    public Model(String model, String config) {
        super(Model_1(model, config));
    }

    /**
     * Create model from deep learning network represented in one of the supported formats.
     * An order of {@code model} and {@code config} arguments does not matter.
     * @param model Binary file contains trained weights.
     */
    public Model(String model) {
        super(Model_2(model));
    }


    //
    // C++:  void cv::dnn::Model::predict(Mat frame, vector_Mat& outs)
    //

    /**
     * Given the {@code input} frame, create input blob, run net and return the output {@code blobs}.
     * @param outs Allocated output blobs, which will store results of the computation.
     * @param frame automatically generated
     */
    public void predict(Mat frame, List<Mat> outs) {
        Mat outs_mat = new Mat();
        predict_0(nativeObj, frame.nativeObj, outs_mat.nativeObj);
        Converters.Mat_to_vector_Mat(outs_mat, outs);
        outs_mat.release();
    }


    //
    // C++:  void cv::dnn::Model::setInputParams(double scale = 1.0, Size size = Size(), Scalar mean = Scalar(), bool swapRB = false, bool crop = false)
    //

    /**
     * Set preprocessing parameters for frame.
     * @param size New input size.
     * @param mean Scalar with mean values which are subtracted from channels.
     * @param scale Multiplier for frame values.
     * @param swapRB Flag which indicates that swap first and last channels.
     * @param crop Flag which indicates whether image will be cropped after resize or not.
     * blob(n, c, y, x) = scale * resize( frame(y, x, c) ) - mean(c) )
     */
    public void setInputParams(double scale, Size size, Scalar mean, boolean swapRB, boolean crop) {
        setInputParams_0(nativeObj, scale, size.width, size.height, mean.val[0], mean.val[1], mean.val[2], mean.val[3], swapRB, crop);
    }

    /**
     * Set preprocessing parameters for frame.
     * @param size New input size.
     * @param mean Scalar with mean values which are subtracted from channels.
     * @param scale Multiplier for frame values.
     * @param swapRB Flag which indicates that swap first and last channels.
     * blob(n, c, y, x) = scale * resize( frame(y, x, c) ) - mean(c) )
     */
    public void setInputParams(double scale, Size size, Scalar mean, boolean swapRB) {
        setInputParams_1(nativeObj, scale, size.width, size.height, mean.val[0], mean.val[1], mean.val[2], mean.val[3], swapRB);
    }

    /**
     * Set preprocessing parameters for frame.
     * @param size New input size.
     * @param mean Scalar with mean values which are subtracted from channels.
     * @param scale Multiplier for frame values.
     * blob(n, c, y, x) = scale * resize( frame(y, x, c) ) - mean(c) )
     */
    public void setInputParams(double scale, Size size, Scalar mean) {
        setInputParams_2(nativeObj, scale, size.width, size.height, mean.val[0], mean.val[1], mean.val[2], mean.val[3]);
    }

    /**
     * Set preprocessing parameters for frame.
     * @param size New input size.
     * @param scale Multiplier for frame values.
     * blob(n, c, y, x) = scale * resize( frame(y, x, c) ) - mean(c) )
     */
    public void setInputParams(double scale, Size size) {
        setInputParams_3(nativeObj, scale, size.width, size.height);
    }

    /**
     * Set preprocessing parameters for frame.
     * @param scale Multiplier for frame values.
     * blob(n, c, y, x) = scale * resize( frame(y, x, c) ) - mean(c) )
     */
    public void setInputParams(double scale) {
        setInputParams_4(nativeObj, scale);
    }

    /**
     * Set preprocessing parameters for frame.
     * blob(n, c, y, x) = scale * resize( frame(y, x, c) ) - mean(c) )
     */
    public void setInputParams() {
        setInputParams_5(nativeObj);
    }


    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }



    // C++:   cv::dnn::Model::Model(Net network)
    private static native long Model_0(long network_nativeObj);

    // C++:   cv::dnn::Model::Model(String model, String config = "")
    private static native long Model_1(String model, String config);
    private static native long Model_2(String model);

    // C++:  void cv::dnn::Model::predict(Mat frame, vector_Mat& outs)
    private static native void predict_0(long nativeObj, long frame_nativeObj, long outs_mat_nativeObj);

    // C++:  void cv::dnn::Model::setInputParams(double scale = 1.0, Size size = Size(), Scalar mean = Scalar(), bool swapRB = false, bool crop = false)
    private static native void setInputParams_0(long nativeObj, double scale, double size_width, double size_height, double mean_val0, double mean_val1, double mean_val2, double mean_val3, boolean swapRB, boolean crop);
    private static native void setInputParams_1(long nativeObj, double scale, double size_width, double size_height, double mean_val0, double mean_val1, double mean_val2, double mean_val3, boolean swapRB);
    private static native void setInputParams_2(long nativeObj, double scale, double size_width, double size_height, double mean_val0, double mean_val1, double mean_val2, double mean_val3);
    private static native void setInputParams_3(long nativeObj, double scale, double size_width, double size_height);
    private static native void setInputParams_4(long nativeObj, double scale);
    private static native void setInputParams_5(long nativeObj);

    // native support for java finalize()
    private static native void delete(long nativeObj);

}
