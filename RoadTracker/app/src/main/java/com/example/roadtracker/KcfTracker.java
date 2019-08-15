package com.example.roadtracker;

import android.util.Log;

import org.opencv.bgsegm.Bgsegm;
import org.opencv.bgsegm.BackgroundSubtractorMOG;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static android.content.ContentValues.TAG;

public class KcfTracker {
    private final int min_area_size;//decrease from 8000
    private final int max_area_size;
//    private static final int max_tracking_duration = 30;//increase from 20

    private BackgroundSubtractorMOG mBackgroundSubtractor;
    private final Size screen_size;
    private volatile  Mat inputImage;
    private volatile Mat clean_foreground;
    private volatile Mat dilated_foreground;
    private volatile List<MatOfPoint> contours;
    private volatile List<Rect> filtered_bboxes;
    private List<CustomKcfTracker> trackers = new ArrayList<>();

    public KcfTracker(Size screenSize) {
        this.mBackgroundSubtractor = Bgsegm.createBackgroundSubtractorMOG(200, 5, 0.7, 0);
        this.screen_size = screenSize;
        min_area_size = (int)this.screen_size.area()/16;
        max_area_size = (int)this.screen_size.area()/4;
    }

    public void process(Mat img) {
        this.inputImage = img;
        this.clean_foreground = applySubtractor(this.inputImage);
        this.dilated_foreground = dilateForeground(this.clean_foreground);
        this.contours = getContours(this.dilated_foreground);
        this.filtered_bboxes = getFilteredBboxes(this.contours, this.inputImage);
//        this.updateTrackers(this.inputImage);
//        this.addTrackers(this.inputImage, this.filteredBoudingBoxes);
    }

    private List<Rect> getFilteredBboxes(List<MatOfPoint> contours, Mat frame) {
        List<Rect> bboxes = new ArrayList<>();
        for (MatOfPoint c: contours) {
            Rect bb = Imgproc.boundingRect(c);
            if(bb.area() >= min_area_size && bb.area() <= max_area_size && !(isIncluded(bb, bboxes))) {
                bboxes.add(bb);

//                Rect2d boundingBoxDouble = new Rect2d(bb.tl(), bb.size());
//
//                TrackerKCF tracker = TrackerKCF.create();
//                tracker.init(frame, boundingBoxDouble);
//                this.trackers.add(tracker);
            }
        }
        Log.d(TAG, "!!!!!!!!!! before" + contours.size());
        Log.d(TAG, "!!!!!!!!!! after" + bboxes.size());
        return bboxes;
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

    public void drawBoundingBoxes() {
        Mat contoursWithBoundingBoxesMat = Mat.zeros(inputImage.size(), CvType.CV_8UC3);
        Imgproc.drawContours(contoursWithBoundingBoxesMat, contours, -1, new Scalar(255, 255, 255));

        Mat inputImageWithBoundingBoxesMat = inputImage.clone();

        for (Rect boundingBox : filtered_bboxes) {
            Imgproc.rectangle(contoursWithBoundingBoxesMat, boundingBox.tl(), boundingBox.br(), new Scalar(0, 255, 0));
            Imgproc.rectangle(inputImageWithBoundingBoxesMat, boundingBox.tl(), boundingBox.br(), new Scalar(0, 255, 0));
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


//        Mat previewMat = generatePreviewMat(2, 4, showGrid, inputImage, rawForegroundMask, dilated_foreground, contoursWithBoundingBoxesMat, inputImageWithBoundingBoxesMat);
//        return previewMat;
        return inputImageWithBoundingBoxesMat;
    }

    private Mat generatePreviewMat(final int NUM_ROWS, final int NUM_COLS, boolean showGrid, Mat... mats) {

        int previewFrameWidth = (int) (screen_size.width / NUM_COLS);
        int previewFrameHeight = (int) (screen_size.height / NUM_ROWS);

        Mat previewMat = new Mat(screen_size, CvType.CV_8UC3);

        Queue<Mat> matList = new LinkedList<>();

        for (Mat previewFrame : mats) {
            previewFrame = convertMat(previewFrame);

            if (showGrid) {
                drawGrid(previewFrame, 100);
            }

            Imgproc.resize(previewFrame, previewFrame, new Size(previewFrameWidth, previewFrameHeight));
            matList.add(previewFrame);
        }

        Mat blank = new Mat(new Size(previewFrameWidth, previewFrameHeight), CvType.CV_8UC3, new Scalar(255, 255, 255));
        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_COLS; col++) {
                Mat previewSubMat = matList.poll();
                if (previewSubMat != null) {
                    previewSubMat.copyTo(previewMat.submat(row * previewFrameHeight, (row + 1) * previewFrameHeight, col * previewFrameWidth, (col + 1) * previewFrameWidth));
                } else {
                    blank.copyTo(previewMat.submat(row * previewFrameHeight, (row + 1) * previewFrameHeight, col * previewFrameWidth, (col + 1) * previewFrameWidth));
                }
            }
        }

//        //TODO more elelgant & Adaptable way to do labels
//        String[] labelStrings = {"Source", "Raw Bg", "Cleaned Bg", "Contours", "Bounding Boxes"};
//        for (int row = 0; row < NUM_ROWS; row++) {
//            for (int col = 0; col < NUM_COLS && labelStrings.length > row * NUM_COLS + col; col++) {
//                Imgproc.putText(previewMat, labelStrings[row * NUM_COLS + col],
//                        new Point(col * previewFrameWidth + 20, row * previewFrameHeight + 100),
//                        FONT_HERSHEY_SIMPLEX, 2.5, new Scalar(0, 0, 255), 5, Core.FILLED);
//            }
//        }



        return previewMat;
    }



    private Mat convertMat(Mat img) {
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
        Imgproc.resize(returnImg,returnImg,new Size(480,360));
        return returnImg;
    }

    private void drawGrid(Mat mat, int size) {
        for (int row = 0; (row += size) < mat.rows(); ) {
            Imgproc.line(mat, new Point(0, row), new Point(mat.cols(), row), new Scalar(255, 170, 0));
        }
        for (int col = 0; (col += size) < mat.cols(); ) {
            Imgproc.line(mat, new Point(col, 0), new Point(col, mat.rows()), new Scalar(255, 170, 0));
        }
        Imgproc.rectangle(mat, new Point(0, 0), new Point(mat.cols() - 1, mat.rows() - 1), new Scalar(0, 0, 255));
    }

//    private void updateTrackers(Mat img) {
//        Iterator<CustomKcfTracker> iterator = trackers.iterator();
//        while(iterator.hasNext()) {
//            CustomKcfTracker tracker = iterator.next();
//
//            if(tracker.age < max_tracking_duration) {
//                tracker.update(img);
//                if(!tracker.insideImage()) {
//                    iterator.remove();
//                }
//            }
//            else {
//                iterator.remove();
//            }
//        }
//    }
//
//    private void addTrackers(Mat img, List<Rect> bboxes) {
//        for(Rect bb : bboxes) {
////            CustomKcfTracker tracker = new CustomKcfTracker(bb, img);
////            trackers.add(tracker);
//            Rect wholeFrame = new Rect(new Point(-1,-1), new Point(this.inputImage.width()+1, this.inputImage.height()+1));
//            if(!wholeFrame.contains(bb.tl()) && !wholeFrame.contains(bb.br())) {
//                boolean isFound = false;
//                CustomKcfTracker trackerToAdd = null;
//                for(CustomKcfTracker tracker : this.trackers) {
//                    if(tracker.insideBoundingBox(bb)) {
//                        if(tracker.trackingLost) {
//                            trackerToAdd = tracker;
//                        }
//                        else {
//                            isFound = true;
//                            break;
//                        }
//                    }
//                }
//                if(!isFound) {
//                    if(trackerToAdd != null) {
//                        trackerToAdd.reinitialize(img, bb);
//                    }
//                    else {
//                        CustomKcfTracker tracker = new CustomKcfTracker(bb, img);
//                        trackers.add(tracker);
//
//                    }
//                }
//            }
//        }
//        Log.d(TAG, "!!!!!!!!!! tracker" + trackers.size());
//    }
}