package com.example.roadtracker;

import org.opencv.bgsegm.Bgsegm;
import org.opencv.bgsegm.BackgroundSubtractorMOG;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Rect2d;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.tracking.TrackerKCF;

import java.util.ArrayList;
import java.util.List;


public class TrackingActivity {

    private final int min_area_size;//decrease from 8000
    private final int max_area_size;
    private static final int max_tracking_duration = 30;//increase from 20

    private BackgroundSubtractorMOG mBackgroundSubtractor;
    private final Size screen_size;
    private volatile  Mat inputImage;
    private volatile Mat clean_foreground;
    private volatile Mat dilated_foreground;
    private volatile List<MatOfPoint> contours = new ArrayList<>();
    private volatile List<Rect> filtered_bboxes = new ArrayList<>();
    private List<TrackerKCF> trackers = new ArrayList<>();

    public TrackingActivity(Size screenSize) {
        this.mBackgroundSubtractor = Bgsegm.createBackgroundSubtractorMOG(200, 5, 0.7, 0);
        this.screen_size = screenSize;
        min_area_size = (int)this.screen_size.area()/16;
        max_area_size = (int)this.screen_size.area()/4;
    }

    public void process(Mat img) {
//        this.inputImage = img;
        this.inputImage = convertChannels(img);
        this.clean_foreground = applySubtractor(this.inputImage);
        this.dilated_foreground = dilateForeground(this.clean_foreground);
        this.contours = getContours(this.dilated_foreground);
        this.filtered_bboxes = createTrackersAndBboxes(this.contours, this.inputImage);
        this.updateAndFilterTrackers(this.inputImage);
    }

    private Mat convertChannels(Mat img) {
        Mat returnImg = img.clone();
        if (returnImg.channels() == 4) {
            Imgproc.cvtColor(returnImg, returnImg, Imgproc.COLOR_BGRA2BGR);
        } else if (returnImg.channels() == 3) {
            //already proper colour-space config
        } else if (returnImg.channels() == 1) {
            Imgproc.cvtColor(returnImg, returnImg, Imgproc.COLOR_GRAY2BGR);
        } else {
            //TODO output error Invalid image
        }
        return returnImg;
    }

    private List<Rect> createTrackersAndBboxes(List<MatOfPoint> contours, Mat frame) {
        for (MatOfPoint c: contours) {
            Rect bb = Imgproc.boundingRect(c);
            if(bb.area() >= min_area_size && bb.area() <= max_area_size && !(isIncluded(bb, this.filtered_bboxes))) {
                this.filtered_bboxes.add(bb);

                Rect2d boundingBoxDouble = new Rect2d(bb.tl(), bb.size());

                TrackerKCF tracker = TrackerKCF.create();
                tracker.init(frame, boundingBoxDouble);
                this.trackers.add(tracker);
                assert(this.trackers.size() == this.filtered_bboxes.size());
            }
        }
        return this.filtered_bboxes;
    }

    private void updateAndFilterTrackers(Mat frame) {
//        Log.i(TAG, "!!  size of bboxes " + this.filtered_bboxes.size());
        List<Rect> tempBboxes = new ArrayList<>();
        List<TrackerKCF> tempTrackers = new ArrayList<>();
        for (TrackerKCF k : this.trackers) {
            tempTrackers.add(k);
        }
        if (this.filtered_bboxes.size() >= 1) {
//            Log.i(TAG, "!! before for loop, tracker size is " + this.trackers.size());
//            Log.i(TAG, "!! before for loop, bboxes size is " + this.filtered_bboxes.size());
            for (int i = this.filtered_bboxes.size(); i-- > 0; ) {
                Rect bb = this.filtered_bboxes.get(i);
                Rect2d updated_bbox = new Rect2d();
//                Log.i(TAG, "channel is " + frame.channels());
//                Log.i(TAG, "!! after size of trackers " + this.trackers.size());

                Boolean success = this.trackers.get(i).update(frame, updated_bbox);
                if (success == true) {
                    tempBboxes.add(bb);
                }
                else {
                    tempTrackers.remove(i);
//                    Log.i(TAG, "while removing temptracker, trakcers size is " + this.trackers.size());
                }
            }
        }
        this.filtered_bboxes = tempBboxes;
        this.trackers = tempTrackers;
    }

    private Mat applySubtractor(Mat img) {
        img = img.clone();
        Imgproc.cvtColor(img, img, Imgproc.COLOR_RGBA2RGB);
        this.mBackgroundSubtractor.apply(img, img);
        return img;
    }

    private Mat dilateForeground(Mat img) {
        img = img.clone();
        // TODO blur/thresdhold
        Imgproc.dilate(img, img, Mat.ones(3, 3, CvType.CV_8UC1), new Point(-1,-1), 2);
        return img;
    }

    private List<MatOfPoint> getContours(Mat img) {
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(img, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        contours = grabContours(contours);

        return contours;
    }

    private List<MatOfPoint> grabContours(List<MatOfPoint> cnts) {
        if (cnts.size() == 2) {
            MatOfPoint temp = cnts.get(0);
            cnts = new ArrayList<>();
            cnts.add(temp);
        }
        else if (cnts.size() == 3) {
            MatOfPoint temp = cnts.get(1);
            cnts = new ArrayList<>();
            cnts.add(temp);
        }
        return cnts;
    }

    private Boolean isIncluded(Rect bb, List<Rect> bboxes) {
        if (bboxes.size() == 0) {
            return false;
        }
        for (Rect boxIncluded : bboxes) {
            if (isInOtherBoundingBoxes(bb, boxIncluded)) {
                return true;
            }
        }
        return false;
    }

    // return whether rect a is inside rect b
    private Boolean isInOtherBoundingBoxes(Rect a, Rect b) {
        Point tl = new Point(a.x, a.y);
        Point br = new Point(a.x + a.width, a.y + a.height);
        if (b.contains(tl) && b.contains(br)) {
            return true;
        }
        else {
            return false;
        }
    }


    public Mat getPreviewMat(boolean showGrid) {
        Mat contoursWithBoundingBoxesMat = Mat.zeros(inputImage.size(), CvType.CV_8UC3);
        Imgproc.drawContours(contoursWithBoundingBoxesMat, contours, -1, new Scalar(255, 255, 255));

        Mat inputImageWithBoundingBoxesMat = inputImage.clone();

        for (Rect boundingBox : filtered_bboxes) {
            Imgproc.rectangle(contoursWithBoundingBoxesMat, boundingBox.tl(), boundingBox.br(), new Scalar(0, 255, 0));
            Imgproc.rectangle(inputImageWithBoundingBoxesMat, boundingBox.tl(), boundingBox.br(), new Scalar(0, 255, 0));
        }

        return inputImageWithBoundingBoxesMat;
    }

}