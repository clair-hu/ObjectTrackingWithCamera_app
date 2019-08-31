# ObjectTrackingWithCamera_app

-   Sensor Tower

    -   on android platform

        -   implemented by java opencv

            -   github

                -   add README

        -   building opencv-android library

            -   it is no longer maintained by opencv

                -   last maintenance in August, 2018

                    -   <https://sourceforge.net/projects/opencvlibrary/files/opencv-android/>

            -   found workaround to build opencv-android library

                -   using opencv build python script

                    -   <https://answers.opencv.org/question/197296/android-object-tracking/>

        -   connect android application with opencv-android library as a
            module

            -   <https://heartbeat.fritz.ai/a-guide-to-preparing-opencv-for-android-4e9532677809>

            -   <https://android.jlelse.eu/a-beginners-guide-to-setting-up-opencv-android-library-on-android-studio-19794e220f3c>

        -   based on the development flow on Mac platform

            -   "translate" python opencv to android java opencv

                -   using MOG background subtractor

                -   using KCF tracker

        -   orientation of frame is wrong

            -   fix by doing matrix calculation

                -   describe in codes

        -   defined min and max area for bounding boxes

            -   for performance

                -   if the camera close to the cars, need to adjust min
                    of bounding box, increase the min area

        -   issue

            -   the speed of the application is still not quick enough

                -   usually it has 1 to 3 seconds delay

                    -   i think the issue is that the phone camera can
                        only process around 10 frame per minute, but the
                        algorithm wants to process around 25 to 30 frame
                        per second.

                        -   described as the synchronization issue
                            between kcf and camera

            -   todo

                -   sync phone camera capture speed with the speed of
                    kcf tracker algorithm

                    -   solution by Melvin

                        -   on android

                    -   clair no time to look at the synchronization
                        solution
