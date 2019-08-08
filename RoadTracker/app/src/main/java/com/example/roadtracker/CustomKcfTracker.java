package com.example.roadtracker;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Rect2d;
import org.opencv.core.Size;
import org.opencv.tracking.TrackerKCF;

public class CustomKcfTracker {
    //TODO Remove magic Number velocity
    private static final int VELOCITY = 50;
    protected int age = 0;
    protected boolean trackingLost = false;
    private TrackerKCF tracker = null;
    private Rect2d boundingBox = null;
    //    private ExitDirection exitDirection = ExitDirection.UNKNOWN;
    private Point center = null;
    private Size frameSize = null;

    protected CustomKcfTracker(Rect boundingBox, Mat frame) {
        Rect2d boundingBoxDouble = new Rect2d(boundingBox.tl(), boundingBox.size());

        this.tracker = TrackerKCF.create();
        this.tracker.init(frame, boundingBoxDouble);

        this.boundingBox = boundingBoxDouble;
//        this.exitDirection = exitZone;

        center = calculateCenter(boundingBoxDouble);
        frameSize = frame.size();
    }


    private Point calculateCenter(Rect2d boundingBox) {
        double x = boundingBox.x + boundingBox.width / 2;
        double y = boundingBox.y + boundingBox.height / 2;
        return new Point(x, y);
    }

//    private void predictTrackerLocation() {
//        switch (exitDirection) {
//            case ZONE1:
//                this.boundingBox.x -= VELOCITY;
//                break;
//
//            case ZONE2:
//                this.boundingBox.x += VELOCITY;
//                break;
//
//            case UNKNOWN:
//                break;
//
//            default:
//                break;
//        }
//    }

    protected void update(Mat frame) {
        if (trackingLost) {
//            this.predictTrackerLocation();
        } else {
            Rect2d newBoundingBox = new Rect2d();
            boolean foundTracker = this.tracker.update(frame, newBoundingBox);
            if (foundTracker) {
                this.boundingBox = newBoundingBox;

            } else {
                this.trackingLost = true;
//                this.predictTrackerLocation();
            }
        }
        this.center = calculateCenter(this.boundingBox);
        this.age++;
    }

    protected void reinitialize(Mat frame, Rect boundingBox) {
        Rect2d boundingBoxDouble = new Rect2d(boundingBox.tl(), boundingBox.size());

        this.tracker.clear();
        this.tracker = TrackerKCF.create();
        this.tracker.init(frame, boundingBoxDouble);

        this.boundingBox = boundingBoxDouble;
        this.trackingLost = false;
    }

    protected boolean insideBoundingBox(Rect boundingBox) {
        if (this.center.inside(boundingBox)) {
            return true;
        }
        return false;
    }

    protected boolean insideImage() {
        return insideBoundingBox(new Rect(new Point(0, 0), this.frameSize));
    }
}
