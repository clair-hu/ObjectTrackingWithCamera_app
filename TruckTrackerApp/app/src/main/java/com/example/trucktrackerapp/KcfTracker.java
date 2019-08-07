package com.example.trucktrackerapp;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.tracking.TrackerKCF;
import org.opencv.video.BackgroundSubtractorKNN;
import org.opencv.video.Video;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class KcfTracker {
    private final int mBoxThreshold = 8000;
    private static final int MAX_TRACKER_AGE = 30;//increase from 20

    private BackgroundSubtractorKNN mBackgroundSubtractor;
    private final Size screen_size;
    private volatile  Mat inputImage;
    private volatile Mat rawForegroundMask;
    private volatile Mat cleanedForegroundMask;
    private volatile List<MatOfPoint> rawContours;
    private volatile List<Rect> filteredBoudingBoxes;
    private List<CustomKcfTracker> trackers = new ArrayList<>();

    public KcfTracker(Size screenSize) {
        this.mBackgroundSubtractor = Video.createBackgroundSubtractorKNN(500, 16, false);
        this.screen_size = screenSize;
    }

    public void process(Mat img) {
        this.inputImage = img;
        this.rawForegroundMask = findMask(this.inputImage);
        this.cleanedForegroundMask = cleanMask(this.rawForegroundMask);
        this.rawContours = findContours(this.cleanedForegroundMask);
        this.filteredBoudingBoxes = findFilteredBoudingBoxes(this.rawContours);
        this.updateTrackers(this.inputImage);
        this.addTrackers(this.inputImage, this.filteredBoudingBoxes);

    }

    private void updateTrackers(Mat img) {
        Iterator<CustomKcfTracker> iterator = trackers.iterator();
        while(iterator.hasNext()) {
            CustomKcfTracker tracker = iterator.next();

            if(tracker.age < MAX_TRACKER_AGE) {
                tracker.update(img);
                if(!tracker.insideImage()) {
                    iterator.remove();
                }
            }
            else {
                iterator.remove();
            }
        }
    }

    private void addTrackers(Mat img, List<Rect> bboxes) {
        for(Rect bb : bboxes) {
            Rect wholeFrame = new Rect(new Point(-1,-1), new Point(this.inputImage.width()+1, this.inputImage.height()+1));
            if(!wholeFrame.contains(bb.tl()) && !wholeFrame.contains(bb.br())) {
                boolean isFound = false;
                CustomKcfTracker trackerToAdd = null;
                for(CustomKcfTracker tracker : this.trackers) {
                    if(tracker.insideBoundingBox(bb)) {
                        if(tracker.trackingLost) {
                            trackerToAdd = tracker;
                        }
                        else {
                            isFound = true;
                            break;
                        }
                    }
                }
                if(!isFound) {
                    if(trackerToAdd != null) {
                        trackerToAdd.reinitialize(img, bb);
                    }
                    else {
                        CustomKcfTracker tracker = new CustomKcfTracker(bb, img);
                        trackers.add(tracker);

                    }
                }
            }
        }
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

    public void drawBoundingBoxes() {
        Mat contoursWithBoundingBoxesMat = Mat.zeros(inputImage.size(), CvType.CV_8UC3);
        Imgproc.drawContours(contoursWithBoundingBoxesMat, rawContours, -1, new Scalar(255, 255, 255));

        Mat inputImageWithBoundingBoxesMat = inputImage.clone();

        for (Rect boundingBox : filteredBoudingBoxes) {
            Imgproc.rectangle(contoursWithBoundingBoxesMat, boundingBox.tl(), boundingBox.br(), new Scalar(0, 255, 0));
            Imgproc.rectangle(inputImageWithBoundingBoxesMat, boundingBox.tl(), boundingBox.br(), new Scalar(0, 255, 0));
        }
    }
}