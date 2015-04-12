package com.gkp.yue;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.gkp.yue.logic.GameLogic;
import com.gkp.yue.logic.GameLogic.ActiveBlockCreateListener;

public class SlidingBlocksAct extends Activity {

	private static final int COUNT_DOWN_TIME = 5000;
	private static final int MSG_WHAT_TIMEOUT = 2;
	private static final int MSG_WHAT_TIME_COUNT = 3;
	private static final int IMAGE_KEY_ROTATE= KeyEvent.KEYCODE_R;
	private static final int IMAGE_KEY_DROP= KeyEvent.KEYCODE_D;
	private static final int IMAGE_KEY_NEWGAME= KeyEvent.KEYCODE_N;
	
	private GameSurfaceView gameSurfaceView = null;
	private TextView downTimeText = null;
	private static int timeCount = COUNT_DOWN_TIME;
	
	private Handler mHandler = null;
	private boolean gameRunning = true;
	private boolean countEnable = false;
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		gameSurfaceView = new GameSurfaceView(this);
//		setContentView(gameSurfaceView);

		 setContentView(R.layout.main);
		 gameSurfaceView = (GameSurfaceView)this.findViewById(R.id.game_surface);
		 
		 downTimeText = (TextView)this.findViewById(R.id.count_down);
		 mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				switch(msg.what){
				case MSG_WHAT_TIMEOUT:
					GameLogic.getInstance().dropActiveBlock();
					Log.i("tg","SlidingBlocksAct/onCreate" + timeCount);
				case MSG_WHAT_TIME_COUNT:
					downTimeText.setText("" + timeCount/1000 + "." + timeCount%1000/100);
					break;
				}
			}
		 };
		 GameLogic.getInstance().setActiveBlockCreateListener(new ActiveBlockCreateListener(){
			@Override
			public void onActiveBlockCreate() {
				if(!countEnable){
					return;
				}
				mHandler.removeCallbacksAndMessages(null);
				timeCount = COUNT_DOWN_TIME;
				mHandler.sendEmptyMessage(MSG_WHAT_TIME_COUNT);
				mHandler.postDelayed(new CountDownTask(), 100);
			}
		 });
		 //控制是否允许倒计时
		 ImageView timer = (ImageView)this.findViewById(R.id.timer);
		 timer.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				countEnable = !countEnable;
				mHandler.removeCallbacksAndMessages(null);
				timeCount = COUNT_DOWN_TIME;
				mHandler.sendEmptyMessage(MSG_WHAT_TIME_COUNT);
				if(countEnable){
					mHandler.postDelayed(new CountDownTask(), 100);
				}
			}
		 });
		 
		 //控制点击逆时针旋转
		 ImageView rotate = (ImageView)this.findViewById(R.id.rotate);
//		 Log.i("tg","SlidingBlocksAct/onCreate/" + rotate);
		 rotate.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(gameRunning){
					gameSurfaceView.onKeyDown(IMAGE_KEY_ROTATE, null);
				}
			}
		 });  
		 //控制点击下降
		 ImageView drop = (ImageView)this.findViewById(R.id.drop);
		 drop.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(gameRunning){
					gameSurfaceView.onKeyDown(IMAGE_KEY_DROP, null);
				}
			}
		 });
		 //控制点击新建游戏
		 ImageView newGame = (ImageView)this.findViewById(R.id.new_game);
		 newGame.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				gameRunning = true;
				gameSurfaceView.onKeyDown(IMAGE_KEY_NEWGAME, null);
			}
		 });
		
	}
/**
 * 返回键虚拟机退出
 */
	@Override
	public void onBackPressed() {
		Log.i("tg", "SlidingBlocksAct/onBackPressed");
		super.onBackPressed();
		System.exit(0);
	}
/**
 * 传递按键事件
 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
//		Log.i("tg", "SlidingBlocksAct/onKeyDown/" + event);
		gameSurfaceView.onKeyDown(keyCode, event);

		return super.onKeyDown(keyCode, event); // 须有此调用，否则返回键不能退出。
	}
/**
 * 倒计时任务
 *
 */
	class CountDownTask implements Runnable{
		@Override
		public void run() {
//			Log.i("tg","SlidingBlocksAct/CountDownTask/run");
			timeCount -= 100;
			if(timeCount <= 0){
				mHandler.sendEmptyMessage(MSG_WHAT_TIMEOUT);
			}else{
				mHandler.sendEmptyMessage(MSG_WHAT_TIME_COUNT);
				mHandler.postDelayed(this, 100);
			}
			
		}
	}
	
}
