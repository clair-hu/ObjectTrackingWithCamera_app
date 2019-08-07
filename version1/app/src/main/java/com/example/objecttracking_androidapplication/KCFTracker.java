package com.example.objecttracking_androidapplication;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorKNN;
import org.opencv.video.Video;
import java.util.ArrayList;
import java.util.List;

public class KCFTracker {
    private final int mBoxThreshold = 8000;

    private BackgroundSubtractorKNN mBackgroundSubtractor;
    private final Size screen_size;
    private volatile  Mat inputImage;
    private volatile Mat rawForegroundMask;
    private volatile Mat cleanedForegroundMask;
    private volatile List<MatOfPoint> rawContours;
    private volatile List<Rect> filteredBoudingBoxes;
//    private List<CustomKCFTracker>

    public KCFTracker(Size screenSize) {
        this.mBackgroundSubtractor = Video.createBackgroundSubtractorKNN(500, 16, false);
        this.screen_size = screenSize;
    }

    public void process(Mat img) {
        this.inputImage = img;
        this.rawForegroundMask = findMask(this.inputImage);
        this.cleanedForegroundMask = cleanMask(this.rawForegroundMask);
        this.rawContours = findContours(this.cleanedForegroundMask);
        this.filteredBoudingBoxes = findFilteredBoudingBoxes(this.rawContours);
//        this.updateTrackers(this.inputImage);
//        this.addTrackers(this.inputImage, this.findFilteredBoudingBoxes());

    }

    private void updateTrackers(Mat img) {

    }

    private Mat findMask(Mat img) {
        img = img.clone();
        this.mBackgroundSubtractor.apply(img, img);
        return img;
    }

    private Mat cleanMask(Mat img) {
        img = img.clone();
        // TODO validate ksize and other parameters to be 75 or others
        Imgproc.medianBlur(img, img, 75);
        Imgproc.blur(img, img, new Size(5, 5));
        Imgproc.threshold(img, img, 125, 255, Imgproc.THRESH_BINARY);
//        Imgproc.dilate(img, img, Mat.ones(3, 3, CvType.CV_8UC1));
        return img;
    }

    private List<MatOfPoint> findContours(Mat img) {
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(img, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        return contours;
    }

    private List<Rect> findFilteredBoudingBoxes(List<MatOfPoint> contours) {
        List<Rect> bboxes = new ArrayList<>();
        for (MatOfPoint c: contours) {
            Rect bb = Imgproc.boundingRect(c);
            if(bb.area() >= mBoxThreshold) {
                bboxes.add(bb);
            }
        }
        return bboxes;
    }
}
