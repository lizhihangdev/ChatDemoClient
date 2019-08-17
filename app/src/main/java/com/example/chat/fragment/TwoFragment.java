package com.example.chat.fragment;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chat.listener.OnDoubleClickListener;
import com.example.chat.R;
import com.example.chat.activity.MainActivity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by chen on 2019/5/16.
 */

public class TwoFragment extends Fragment implements View.OnClickListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        SeekBar.OnSeekBarChangeListener {

    private Button bt_pause,bt_maxsize,bt_minisize;
    private TextView tv_currentprogress,tv_totalprogress,title;
    //private RelativeLayout rLayout_video;
    private ImageView iv_clickplay,iv_clickplay_moren;
    private MediaPlayer mediaPlayer;//多媒体播放器
    private static final String STATE_CONTINUE = "继续";
    private static final String STATE_PAUSE = "暂停";
    private SurfaceView sv;//****************SurfaceView是一个在其他线程中显示、更新画面的组件，专门用来完成在单位时间内大量画面变化的需求
    private SurfaceHolder holder;//****************SurfaceHolder接口为一个显示界面内容的容器
    private SeekBar seekBar;//===============进度条
    private static int savedPosition;//===============记录当前播放文件播放的进度
    private static String savedFilepath;//===============记录当前播放文件的位置
    private Timer timer;//===============定义一个计时器，每隔100ms更新一次进度条
    private TimerTask task;//===============计时器所执行的任务

    private RelativeLayout rLayout_video;
    private int screenWidth;
    private int screenHeight;

    private MainActivity mainActivity;

    public TwoFragment(){

    }


    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_two_fragment,container,false);


        mainActivity= (MainActivity)getActivity();
        DisplayMetrics dm = new DisplayMetrics();
        mainActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth= dm.widthPixels;
        screenHeight= dm.heightPixels;

        tv_currentprogress=(TextView)view.findViewById(R.id.tv_currentProgress);
        tv_totalprogress=(TextView) view.findViewById(R.id.tv_totalProgress);
        title = (TextView) view.findViewById(R.id.title);


        rLayout_video=(RelativeLayout) view.findViewById(R.id.rlayout_video);
        LinearLayout.LayoutParams params_1= (LinearLayout.LayoutParams) rLayout_video.getLayoutParams();
        params_1.height=screenHeight/3;
        rLayout_video.setLayoutParams(params_1);

        bt_pause = (Button) view.findViewById(R.id.bt_pause);
        bt_maxsize=(Button) view.findViewById(R.id.bt_maxsize);
        bt_minisize=(Button) view.findViewById(R.id.bt_minisize);
        iv_clickplay=(ImageView) view.findViewById(R.id.image_clickplay);
        iv_clickplay_moren=(ImageView) view.findViewById(R.id.image_clickplay_moren);

        bt_pause.setOnClickListener(this);
        bt_maxsize.setOnClickListener(this);
        bt_minisize.setOnClickListener(this);
        iv_clickplay.setOnClickListener(this);
        iv_clickplay_moren.setOnClickListener(this);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnErrorListener(this);
        sv = (SurfaceView) view.findViewById(R.id.sv);//****************
        sv.setOnTouchListener(new OnDoubleClickListener(new OnDoubleClickListener.DoubleClickCallback() {

            @Override
            public void onDoubleClick() {
                // TODO Auto-generated method stub
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    //bt_pause.setText(STATE_CONTINUE);
                    videoPlayStateImageRefresh(true);
                    bt_pause.setBackgroundResource(R.drawable.videoplayer_pause);
                } else {
                    mediaPlayer.start();
                    //bt_pause.setText(STATE_PAUSE);
                    videoPlayStateImageRefresh(false);
                    bt_pause.setBackgroundResource(R.drawable.videoplayer_play);
                    return;
                }
            }
        }));
        holder = sv.getHolder();//****************得到显示界面内容的容器
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); //设置surfaceView自己不管理缓存区。虽然提示过时，但最好还是设置下
        seekBar = (SeekBar) view.findViewById(R.id.seekBar);//===============
        seekBar.setOnSeekBarChangeListener(this);//===============

        //在界面【最小化】时暂停播放，并记录holder播放的位置——————————————————————————————
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {//holder被销毁时回调。最小化时都会回调
                if (mediaPlayer != null) {
                    Log.i("bqt", "销毁了--surfaceDestroyed" + "--" + mediaPlayer.getCurrentPosition());
                    savedPosition = mediaPlayer.getCurrentPosition();//当前播放位置
                    mediaPlayer.stop();
                    if(timer!=null)
                    {
                        timer.cancel();
                        task.cancel();
                    }


                    timer = null;
                    task = null;
                }
            }
            @Override
            public void surfaceCreated(SurfaceHolder holder) {//holder被创建时回调
                Log.i("bqt", "创建了--" + savedPosition + "--" + savedFilepath);
                title.setText("余罪演技炸裂，与局长精彩对峙，为了父亲、为了自己不想当卧底！");
//                if (savedPosition > 0) {//如果记录的数据有播放进度。
//                    try {
//                        iv_clickplay_moren.setVisibility(View.GONE);
//                        bt_pause.setBackgroundResource(R.drawable.videoplayer_play);
//                        mediaPlayer.reset();
//                        mediaPlayer.setDataSource(savedFilepath);
//                        mediaPlayer.setDisplay(holder);
//                        mediaPlayer.prepare();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
            }
            //holder宽高发生变化（横竖屏切换）时回调
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                //Toast.makeText(MainVideoActivity.this, "横竖屏切换", Toast.LENGTH_SHORT).show();
            }
        });






        return view;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
//		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
//			//竖屏显示
//			LayoutParams lp = (LayoutParams) sv.getLayoutParams();
//	        lp.width = lp.MATCH_PARENT;
//	        lp.height = 200;
//	        sv.setLayoutParams(lp);
//	    } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//	    	//横屏显示
//	        LayoutParams lp = (LayoutParams) sv.getLayoutParams();
//	        lp.width = lp.MATCH_PARENT;
//	        lp.height =lp.MATCH_PARENT;
//	        sv.setLayoutParams(lp);
//	    }
    }

    public void videoPlayStateImageRefresh(boolean b) {
        if (b) {
            iv_clickplay.setVisibility(View.VISIBLE);
        }
        else {
            iv_clickplay.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_clickplay_moren:
                playUrl();
                iv_clickplay_moren.setVisibility(View.GONE);
                bt_pause.setBackgroundResource(R.drawable.videoplayer_play);
                break;
            case R.id.image_clickplay:
                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        //bt_pause.setText(STATE_CONTINUE);
                        videoPlayStateImageRefresh(true);
                        bt_pause.setBackgroundResource(R.drawable.videoplayer_pause);
                    } else {
                        mediaPlayer.start();
                        //bt_pause.setText(STATE_PAUSE);
                        videoPlayStateImageRefresh(false);
                        bt_pause.setBackgroundResource(R.drawable.videoplayer_play);
                        return;
                    }
                }
                break;
            case R.id.bt_pause:
                pause();
                break;
            case R.id.bt_maxsize:
                mainActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                LinearLayout.LayoutParams params= (LinearLayout.LayoutParams) rLayout_video.getLayoutParams();
                params.height=screenWidth-200;
                rLayout_video.setLayoutParams(params);
                bt_maxsize.setVisibility(View.GONE);
                bt_minisize.setVisibility(View.VISIBLE);
                break;
            case R.id.bt_minisize:
                mainActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                LinearLayout.LayoutParams params_1= (LinearLayout.LayoutParams) rLayout_video.getLayoutParams();
                params_1.height=screenHeight/3;
                rLayout_video.setLayoutParams(params_1);
                bt_minisize.setVisibility(View.GONE);
                bt_maxsize.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }
    /**
     * 播放网络多媒体
     */
    public void playUrl() {

        String filepath = "http://39.96.18.40/video/oldyuzui.mp4";

        // String filepath = "http://cloud.video.taobao.com/play/u/2577498496/p/1/e/6/t/1/50016620184.mp4";
        if (!TextUtils.isEmpty(filepath)) {
            try {
                savedFilepath = filepath;
                mediaPlayer.setDataSource(filepath);
                mediaPlayer.setDisplay(holder);//****************
                mediaPlayer.prepareAsync();//异步准备
                Toast.makeText(mainActivity, "准备中，可能需要点时间……", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(mainActivity, "播放失败，请检查是否有网络权限", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mainActivity, "路径不能为空", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * 暂停
     */
    public void pause() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                //bt_pause.setText(STATE_CONTINUE);
                videoPlayStateImageRefresh(true);
                bt_pause.setBackgroundResource(R.drawable.videoplayer_pause);
            } else {
                mediaPlayer.start();
                //bt_pause.setText(STATE_PAUSE);
                videoPlayStateImageRefresh(false);
                bt_pause.setBackgroundResource(R.drawable.videoplayer_play);
                return;
            }
        }
    }
    /**
     * 停止
     */
    public void stop() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.reset();
        bt_pause.setText("暂停");
    }
    /**
     * 重播
     */
    public void replay() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            mediaPlayer.seekTo(0);
        }
        bt_pause.setText("暂停");
    }
    //********************************************************************************************************************
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        //Toast.makeText(MainVideoActivity.this, "报错了--" + what + "--" + extra, 0).show();
        return false;
    }
    @Override
    public void onPrepared(MediaPlayer mp) {//只有准备好以后才能处理很多逻辑
        mediaPlayer.start();
        //=============
        mediaPlayer.seekTo(savedPosition);//开始时是从0开始播放，恢复时是从指定位置开始播放
        seekBar.setMax(mediaPlayer.getDuration());//将进度条的最大值设为文件的总时长
        tv_currentprogress.setText("00:05");
        tv_totalprogress.setText(String.valueOf(msConvertToMinute(mediaPlayer.getDuration())));
        timer = new Timer();
        task = new TimerTask() {
            public void run() {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());//将媒体播放器当前播放的位置赋值给进度条的进度
                //tv_currentprogress.setText("00:05");
            }
        };
        timer.schedule(task, 0, 100);//0秒后执行，每隔100ms执行一次
        //Toast.makeText(MainVideoActivity.this, "开始播放！", 0).show();
    }

    public String msConvertToMinute(int ms) {
        String partA="00";
        String partB="00";
        int totalsecond=ms/1000;
        int minute=totalsecond/60;
        int second=totalsecond-minute*60;
        if (minute<10) {
            partA="0"+minute;
        }
        else {
            partA=String.valueOf(minute);
        }
        if (second<10) {
            partB="0"+second;
        }
        else {
            partB=String.valueOf(second);
        }
        return (partA+":"+partB);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
      //  Toast.makeText(mainActivity, "本视频播放完毕！", Toast.LENGTH_SHORT).show();
        videoPlayStateImageRefresh(true);
        bt_pause.setBackgroundResource(R.drawable.videoplayer_pause);
        //mediaPlayer.reset();
        //bt_playUrl.setEnabled(true);
    }
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {//进度发生变化时
        tv_currentprogress.setText(String.valueOf(msConvertToMinute(progress)));
    }
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {//停止拖拽时回调
        mediaPlayer.seekTo(seekBar.getProgress());//停止拖拽时进度条的进度
        tv_currentprogress.setText(String.valueOf(msConvertToMinute(seekBar.getProgress())));
    }
}

