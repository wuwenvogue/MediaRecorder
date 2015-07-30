package com.example.videotest;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

public class VideoTest extends Activity implements Callback {

	public static final String TAG = "VideoTest";
	
	/**
	 *  初始化SurfaceView
	 */
	private SurfaceView mSurfaceView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		initCamera();
		
		setContentView(R.layout.videotest);
		
		initSurfaceView();
	}

	@SuppressWarnings("deprecation")
	private void initSurfaceView() {
		mSurfaceView = (SurfaceView) this.findViewById(R.id.surface_camera);
		SurfaceHolder holder = mSurfaceView.getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mSurfaceView.setVisibility(View.VISIBLE);
	}


	private SurfaceHolder mSurfaceHolder;
	
	private boolean mMediaRecorderRecording = false;

	public void surfaceCreated(SurfaceHolder holder) {
		mSurfaceHolder = holder;
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		mSurfaceHolder = holder;
		if (!mMediaRecorderRecording) {
			initializeVideo();
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {

	}



	// 初始化MediaRecorder
	private MediaRecorder mMediaRecorder = null;
	private Camera mCamera;

	@SuppressLint("InlinedApi")
	private void initializeVideo() {
		if (mSurfaceHolder == null) {
			return;
		}

		mMediaRecorderRecording = true;

		if (mMediaRecorder == null) {
			mMediaRecorder = new MediaRecorder();
			
		} else {
			long duration = System.currentTimeMillis();
			mMediaRecorder.reset();
			duration = System.currentTimeMillis() - duration;
			
			
			
			System.out.println("duration----------->>>>>>" + duration);
//			mMediaRecorder.release();
			mMediaRecorder = null;
			mMediaRecorder = new MediaRecorder();
		}

		if (mCamera != null) {
			// 解锁camera
			mCamera.unlock();
			mMediaRecorder.setCamera(mCamera);
			
			mMediaRecorder.setOnInfoListener(onInfoListener);
			mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
			mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
			mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
            File mRecAudioFile = new File(getVideoOutputPath());
			if(!mRecAudioFile.exists()) {
				try {
					mRecAudioFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			mMediaRecorder.setOutputFile(mRecAudioFile.toString());
			mMediaRecorder.setMaxDuration( 10 * 1000); //每隔5分钟
			
			try {
				mMediaRecorder.prepare();
				mMediaRecorder.start();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace(); 
				releaseMediaRecorder();
			}
		}
	}
	
	private OnInfoListener onInfoListener = new OnInfoListener() {
		
		@Override
		public void onInfo(MediaRecorder mr, int what, int extra) {
			if(what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
				mMediaRecorderRecording = false;
				initializeVideo();
				
			} else if(what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED) {
				
			} else if(what == MediaRecorder.MEDIA_RECORDER_INFO_UNKNOWN) {
				
			}
		}
	};
	
	@SuppressLint("NewApi")
	private void initCamera() {
		   mCamera = getCameraInstance();
//		   WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);// 得到窗口管理器
//         Display display = wm.getDefaultDisplay();// 得到当前屏幕
           Camera.Parameters parameters = mCamera.getParameters();// 得到摄像头的参数
//         parameters.setPreviewSize(display.getHeight(), display.getWidth());// 设置预览照片的大小
//         parameters.setPreviewFrameRate(3);// 设置每秒3帧
//         parameters.setPictureFormat(PixelFormat.JPEG);// 设置照片的格式
//         parameters.setJpegQuality(85);// 设置照片的质量
//         parameters.setPictureSize(display.getHeight(), display.getWidth());// 设置照片的大小，默认是和
//         parameters.set("orientation", "portrait"); // 屏幕一样大
//         mCamera.setDisplayOrientation(90);
//
//         try {
//        	 mCamera.setPreviewDisplay(mSurfaceHolder);
//         } catch (IOException e) {
//                 // TODO Auto-generated catch block
//                 e.printStackTrace();
//         }// 通过SurfaceView显示取景画面
         
		   parameters.setRecordingHint(true);
		   mCamera.setParameters(parameters);
           mCamera.startPreview();// 开始预览
//         isPreview = true;// 设置是否预览参数为真
	}
	
	public void  doTakePicture(View view) {
		if(mCamera != null) {
			 mCamera.takePicture(mShutterCallback, null, mJpegPictureCallback);  
		}
	}
	   /*为了实现拍照的快门声音及拍照保存照片需要下面三个回调变量*/  
    ShutterCallback mShutterCallback = new ShutterCallback()   
    //快门按下的回调，在这里我们可以设置类似播放“咔嚓”声之类的操作。默认的就是咔嚓。  
    {  
        public void onShutter() {  
            // TODO Auto-generated method stub  
            Log.i(TAG, "myShutterCallback:onShutter...");  
        }  
    };  
    
    PictureCallback mRawCallback = new PictureCallback()   
    // 拍摄的未压缩原数据的回调,可以为null  
    {  
        public void onPictureTaken(byte[] data, Camera camera) {  
            // TODO Auto-generated method stub  
            Log.i(TAG, "myRawCallback:onPictureTaken...");  
        }  
    };  
    
    PictureCallback mJpegPictureCallback = new PictureCallback()    
    //对jpeg图像数据的回调,最重要的一个回调  
    {  
        public void onPictureTaken(byte[] data, Camera camera) {  
            // TODO Auto-generated method stub  
            Log.i(TAG, "myJpegCallback:onPictureTaken...");  
            Bitmap b = null;  
            if(null != data) {  
                b = BitmapFactory.decodeByteArray(data, 0, data.length);//data是字节数据，将其解析成位图  
            }  
            //保存图片到sdcard  
            if(null != b)    {  
                //设置FOCUS_MODE_CONTINUOUS_VIDEO)之后，myParam.set("rotation", 90)失效。  
                //图片竟然不能旋转了，故这里要旋转下  
                Bitmap rotaBitmap = ImageUtil.getRotateBitmap(b, 90.0f);  
                FileUtil.saveBitmap(rotaBitmap);  
            }  
        }  
    };  

	
	/////////////////////////////////////////////////////////////////////////////////////
	////                          打开摄像头                                                                                                         //////
	/////////////////////////////////////////////////////////////////////////////////////
	public Camera getCameraInstance() {
		Camera c = null;
		try {
			c = openFacingBackCamera();
		} catch (Exception e) {
			// 打开摄像头错误
			Log.i("info", "打开摄像头错误");
		}
		return c;
	}

	@SuppressLint("NewApi")
	private Camera openFacingBackCamera() {
		Camera cam = null;
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();

		for (int camIdx = 0, cameraCount = Camera.getNumberOfCameras(); camIdx < cameraCount; camIdx++) {
			Camera.getCameraInfo(camIdx, cameraInfo);
			if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
				try {
					cam = Camera.open(camIdx);
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
			}
		}
		return cam;
	}

	/**
	 *  释放MediaRecorder资源
	 */
	private void releaseMediaRecorder() {
		if (mMediaRecorder != null) {
			if (mMediaRecorderRecording) {
				mMediaRecorder.stop();
				mMediaRecorderRecording = false;
			}
			mMediaRecorder.reset();
			mMediaRecorder.release();
			mMediaRecorder = null;
		}
	}

	private String getVideoOutputPath() {
		String path = Environment.getExternalStorageDirectory() + "/video_"
				+ getCurrentTime() + ".MP4";
		return path;
	}

	/**
	 * 得到当前时间
	 * 
	 * @return 返回当前的时间
	 */
	public static String getCurrentTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH_mm_ss", Locale.CHINESE);
		Calendar calendar = Calendar.getInstance();
		String timeNow = sdf.format(calendar.getTime());
		return timeNow;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mMediaRecorderRecording) {
			releaseMediaRecorder();
			if(mCamera != null) {
				mCamera.release();
			}
			mMediaRecorderRecording = false;
		}
		finish();
	}

}