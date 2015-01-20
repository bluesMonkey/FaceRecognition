package org.opencv.samples.facedetect;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.objdetect.CascadeClassifier;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Property;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Properties;

public class FdActivity extends Activity implements CvCameraViewListener2 {

    private static final String    TAG                 = "OCVSample::Activity";
    private static final Scalar    FACE_RECT_COLOR     = new Scalar(0, 255, 0, 255);
    public static final int        JAVA_DETECTOR       = 0;
    public static final int        NATIVE_DETECTOR     = 1;

    private MenuItem               mItemFace50;
    private MenuItem               mItemFace40;
    private MenuItem               mItemFace30;
    private MenuItem               mItemFace20;
    private MenuItem               mItemFace10;
    private MenuItem               mItemType;

    private Mat                    mRgba;
    private Mat                    mGray;
    private File                   mCascadeFileFace;
    private File                   mCascadeFileEye;
    private File                   mCascadeFileNoze;

    private CascadeClassifier mJavaDetectorFace;
    private CascadeClassifier mJavaDetectorEye;
    private CascadeClassifier mJavaDetectorNoze;


    private DetectionBasedTracker  mNativeDetector;

    private int                    mDetectorType       = JAVA_DETECTOR;
    private String[]               mDetectorName;

    private float                  mRelativeFaceSize   = 0.2f;
    private int                    mAbsoluteFaceSize   = 0;

    static public Properties properties;

    private int count = 0;

    private CameraBridgeViewBase   mOpenCvCameraView;

    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");

                    // Load native library after(!) OpenCV initialization
                    System.loadLibrary("detection_based_tracker");

                    try {
                        // load cascade file from application resources
                        InputStream is_face = getResources().openRawResource(R.raw.lbpcascade_frontalface);

                        File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                        mCascadeFileFace = new File(cascadeDir, "lbpcascade_frontalface.xml");

                        FileOutputStream os = new FileOutputStream(mCascadeFileFace);

                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = is_face.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                        is_face.close();

                        os.close();

                        mJavaDetectorFace = new CascadeClassifier(mCascadeFileFace.getAbsolutePath());
                        if (mJavaDetectorFace.empty()) {
                            Log.e(TAG, "Failed to load cascade classifier");
                            mJavaDetectorFace = null;
                        } else
                            Log.i(TAG, "Loaded cascade classifier from " + mCascadeFileFace.getAbsolutePath());

                        mNativeDetector = new DetectionBasedTracker(mCascadeFileFace.getAbsolutePath(), 0);

                        cascadeDir.delete();



                            InputStream is_left_eye = getResources().openRawResource(R.raw.haarcascade_eye);
                            cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                            mCascadeFileEye = new File(cascadeDir, "haarcascade_eye.xml");

                             os = new FileOutputStream(mCascadeFileEye);

                            buffer = new byte[4096];
                            //int bytesRead;
                            while ((bytesRead = is_left_eye.read(buffer)) != -1) {
                                os.write(buffer, 0, bytesRead);
                            }
                            is_left_eye.close();

                            os.close();

                            mJavaDetectorEye = new CascadeClassifier(mCascadeFileEye.getAbsolutePath());
                            if (mJavaDetectorEye.empty()) {
                                Log.e(TAG, "Failed to load cascade classifier");
                                mJavaDetectorEye = null;
                            } else
                                Log.i(TAG, "Loaded cascade classifier from " + mCascadeFileEye.getAbsolutePath());

                            cascadeDir.delete();



                            InputStream is_noze = getResources().openRawResource(R.raw.haarcascade_mcs_nose);
                            cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                            mCascadeFileNoze = new File(cascadeDir, "haarcascade_mcs_nose.xml");

                            os = new FileOutputStream(mCascadeFileNoze);

                            buffer = new byte[4096];
                            //int bytesRead;
                            while ((bytesRead = is_noze.read(buffer)) != -1) {
                                os.write(buffer, 0, bytesRead);
                            }
                            is_noze.close();

                            os.close();

                            mJavaDetectorNoze = new CascadeClassifier(mCascadeFileNoze.getAbsolutePath());
                            if (mJavaDetectorNoze.empty()) {
                                Log.e(TAG, "Failed to load cascade classifier");
                                mJavaDetectorEye = null;
                            } else
                                Log.i(TAG, "Loaded cascade classifier from " + mCascadeFileNoze.getAbsolutePath());

                            cascadeDir.delete();




                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
                    }

                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
    private boolean simpleCamera=false;
    private boolean jeuTest = false;
    private boolean enregistrementID = false;
    private boolean identification = false;
    private MenuItem mItemFace05;
    private int h1;
    private int h2;

    public FdActivity() {
        mDetectorName = new String[2];
        mDetectorName[JAVA_DETECTOR] = "Java";
        mDetectorName[NATIVE_DETECTOR] = "Native (tracking)";

        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.face_detect_surface_view);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.fd_activity_surface_view);
        mOpenCvCameraView.setCvCameraViewListener(this);

        properties = new Properties();
        InputStream is = null;
        File f = null;

            is = getResources().openRawResource(R.raw.proper);


        try {
            /*if ( is == null)
            {
                FdActivity fd_activity = new FdActivity();
                is = fd_activity.getClass().getResourceAsStream("server.properties");
            }*/

            properties.load(is);

        }catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
    }

    public void onDestroy() {
        super.onDestroy();
        mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat();
        mRgba = new Mat();
    }

    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();


        // Detection de id avec test
        if (!simpleCamera) {
            if (mAbsoluteFaceSize == 0) {
                int height = mGray.rows();
                if (Math.round(height * mRelativeFaceSize) > 0) {
                    mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
                }
                mNativeDetector.setMinFaceSize(mAbsoluteFaceSize);
            }

            MatOfRect faces = new MatOfRect();

            if (mDetectorType == JAVA_DETECTOR) {
                if (mJavaDetectorFace != null)
                    mJavaDetectorFace.detectMultiScale(mGray, faces, 1.1, 2, 2, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
                            new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
            }

        /*else {
            Log.e(TAG, "Detection method is not selected!");
        }
*/
            int x1, x2, y1, y2;
            x1 = x2 = y1 = y2 = 0;
            Rect[] facesArray = faces.toArray();
            for (int i = 0; i < facesArray.length; i++) {
                //Core.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3);
                x1 = (int) facesArray[i].tl().x;
                x2 = (int) facesArray[i].br().x;
                y1 = (int) facesArray[i].tl().y;
                y2 = (int) facesArray[i].br().y;


            }
            Log.d(" Point Face ", String.valueOf(x2 - x1)); //+ "  "+ x2);
            Mat roi = null;
            if ((x1 > 0) && (y1 > 0)) {

                roi = mRgba.submat(y1, y2, x1, x2);
                //roi = mRgba.submat(0, 500, 0, 500);
                Rect ROI = new Rect(roi.rows() * 4, roi.cols(), roi.cols(), roi.rows());
                //Highgui.imwrite("/data/tmp/1.png", roi);
                Core.putText(mRgba, "Face : " + x1 + ", " + x2, new Point(0, 50), Core.FONT_HERSHEY_PLAIN, 1, new Scalar(255));


            }

       /* if (x2 - x1 > 320 && x2 - x1 < 370)
        {
            Core.putText(mRgba, "Alex", new Point(x1,y2), Core.FONT_ITALIC,1,new  Scalar(255));
        }

        if (x2 - x1 > 380 && x2 - x1 < 420)
        {
            Core.putText(mRgba, "Kevin", new Point(x1,y2), Core.FONT_ITALIC,1,new  Scalar(255));
        }*/

            //roi.copyTo(mRgba);

            //////////////////////////////////////////




/*    MatOfRect eyes = new MatOfRect();

    if (mDetectorType == JAVA_DETECTOR) {
        if (mJavaDetectorEye != null)
            mJavaDetectorEye.detectMultiScale(mGray, eyes, 1.1, 2, 2, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
                    new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
    }*/


    /*x1=x2=y1=y2=0;
    Rect[] eyesArray = eyes.toArray();
    for (int i = 0; i < eyesArray.length; i++) {
        Core.rectangle(mRgba, eyesArray[i].tl(), eyesArray[i].br(), FACE_RECT_COLOR, 3);
        x1 = (int)eyesArray[i].tl().x;
        x2= (int)eyesArray[i].br().x;
        y1 = (int)eyesArray[i].tl().y;
        y2=  (int)eyesArray[i].br().y;


    }
    Log.d(" Point Eye",x1 + "  "+ x2);
    roi=null;
    if ((x1> 0 ) && (y1>0)) {

        roi = mRgba.submat(y1, y2, x1, x2);
        //roi = mRgba.submat(0, 500, 0, 500);
        Rect ROI = new Rect(roi.rows() * 4,roi.cols(),  roi.cols(),roi.rows());
        //ighgui.imwrite("/data/tmp/1.png", roi);


    }*/

            //////////////////////////////////////////


            MatOfRect nozes = new MatOfRect();

            if ((mDetectorType == JAVA_DETECTOR) && (roi != null)) {
                if (mJavaDetectorNoze != null)
                    mJavaDetectorNoze.detectMultiScale(roi, nozes, 1.1, 2, 2, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
                            new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
            }

            int x11, x22, y11, y22;
            x11 = x22 = y11 = y22 = 0;
            Rect[] nozesArray = nozes.toArray();
            for (int i = 0; i < nozesArray.length; i++) {
                // Core.rectangle(mRgba, nozesArray[i].tl(), nozesArray[i].br(), FACE_RECT_COLOR, 3);
                x11 = (int) nozesArray[i].tl().x;
                x22 = (int) nozesArray[i].br().x;
                y11 = (int) nozesArray[i].tl().y;
                y22 = (int) nozesArray[i].br().y;


            }

            // Log.d(" Point Noze",x1 + "  "+ x2);
            if (roi != null) {
                h1= (int) ((y11 * 1.0) / roi.rows() * 100);
                h2 = (int) (((roi.rows() - y22) * 1.0) / roi.rows() * 100);
                Core.putText(mRgba, "H1 : " + (int) ((y11 * 1.0) / roi.rows() * 100) + " | " + roi.rows(), new Point(x1, y1), Core.FONT_ITALIC, 1, new Scalar(255));
                Core.putText(mRgba, "H2 : " + (int) (((roi.rows() - y22) * 1.0) / roi.rows() * 100) + " | " + roi.rows(), new Point(x1, y1 + 50), Core.FONT_ITALIC, 1, new Scalar(255));
            }

            if (this.enregistrementID)
            {


                // Écriture dans le fichier
                if (roi != null) {

                   // Log.e(" __ID__ ", "H1 : " + (int) ((y11 * 1.0) / roi.rows() * 100) + " | H2 : " +
                     //       (int) (((roi.rows() - y22) * 1.0) / roi.rows() * 100));



                }
            }



       /* roi=null;
        if ((x1> 0 ) && (y1>0)) {

            roi = mRgba.submat(y1, y2, x1, x2);
            //roi = mRgba.submat(0, 500, 0, 500);
            Rect ROI = new Rect(roi.rows() * 4,roi.cols(),  roi.cols(),roi.rows());
            //ighgui.imwrite("/data/tmp/1.png", roi);
            Core.putText(mRgba, "Noze : " + x1 + ", " + x2, new Point(0, 100), Core.FONT_ITALIC,1,new  Scalar(255));


        }*/


            //Core.putText(mRgba, "Tutorialspoint.com", new Point(mRgba.rows()/2,mRgba.cols()/2), Core.FONT_ITALIC,1,new  Scalar(255));
            //Core.addWeighted(roi, 0.5, mRgba, 0.5,0, mRgba);


            //Core.addWeighted(mRgba.submat(ROI), 0.8, roi, 0.2, 1,  mRgba);

            if (this.jeuTest){

                int h11 = Integer.parseInt(properties.getProperty("user1.h1"));
                int h22 = Integer.parseInt(properties.getProperty("user1.h2"));
                if( (h11==h1) ){
                  Intent I1 = new Intent(Intent.ACTION_VIEW);
                    File F = new File         ("/sdcard/DCIM/Camera/Detect1-1.mp4"  );
                    I1.setDataAndType(Uri.fromFile(F),"video/*");
                    startActivity(I1);
                }

            }
            return mRgba;
        }


        return  inputFrame.rgba();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "called onCreateOptionsMenu");
        mItemFace50 = menu.add("Camera simple");
        mItemFace40 = menu.add("Identification");
        mItemFace30 = menu.add("Jeu test");
        mItemFace20 = menu.add("Fin jeu de test");
        mItemFace10 = menu.add("Enregistrement identité");
        mItemFace05 = menu.add("EditDB");
        mItemType   = menu.add(mDetectorName[mDetectorType]);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);

        if (item == mItemFace50)
        {
            this.simpleCamera= true;//setMinFaceSize(0.5f);
            this.jeuTest = false;
            this.enregistrementID = false;
        }

        else if (item == mItemFace40)
        {
            this.identification = true;
            this.simpleCamera= false;
            this.enregistrementID = false;
        }

        else if (item == mItemFace30)
        {
            this.jeuTest = true;
            this.simpleCamera = false;
            this.identification = false;
            this.enregistrementID = false;
        }

        else if (item == mItemFace20)
        {
            this.jeuTest = false;
            this.simpleCamera = true;
            this.identification = false;
            this.enregistrementID = false;
        }

        else if (item == mItemFace10) {
            this.enregistrementID = true;
            this.jeuTest = false;
            this.simpleCamera = false;
            this.identification = false;
        }
        else if (item == mItemFace05) {
            Intent intent = new Intent(this, EditDB.class);
            startActivity(intent);
        }

        else if (item == mItemType) {
            int tmpDetectorType = (mDetectorType + 1) % mDetectorName.length;
            item.setTitle(mDetectorName[tmpDetectorType]);
            setDetectorType(tmpDetectorType);
        }

        Log.e(" check ", "SC : " + this.simpleCamera +
                " | ID : " + this.identification +
                " | REQ_ID : " + this.enregistrementID +
                " | Test : " + this.jeuTest);

        return true;
    }

    private void setMinFaceSize(float faceSize) {
        mRelativeFaceSize = faceSize;
        mAbsoluteFaceSize = 0;
    }

    private void setDetectorType(int type) {
        if (mDetectorType != type) {
            mDetectorType = type;

            if (type == NATIVE_DETECTOR) {
                Log.i(TAG, "Detection Based Tracker enabled");
                mNativeDetector.start();
            } else {
                Log.i(TAG, "Cascade detector enabled");
                mNativeDetector.stop();
            }
        }
    }
}
