package com.gkp.yue;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.gkp.yue.logic.GameLogic;
import com.gkp.yue.logic.GameLogic.GameDataChangedListener;

public class GameSurfaceView extends GLSurfaceView {

	private static final int MSG_WHAT_DATADIRTY = 1;
	private GameRenderer mRenderer = null;
	private GameLogic logic = null;
	private Handler mHandler = null;

	private float touchStartX = 0;
	private float touchStartY = 0;

	public GameSurfaceView(Context context) {
		super(context);
		init();
	}

	public GameSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
//		this.setZOrderOnTop(true);
//		this.getHolder().setFormat(PixelFormat.RGBA_8888);
//		this.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		mRenderer = new GameRenderer(this);
		setRenderer(mRenderer);
		// 设置描绘方式，
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		// this.requestRender();
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if (msg.what == MSG_WHAT_DATADIRTY) {
					requestRender();
				}
			}
		};

		logic = GameLogic.getInstance();
		logic.setGameDataChangedListener(new GameDataChangedListener() {
			@Override
			public void onGameDataChanged() {
				mHandler.sendEmptyMessage(MSG_WHAT_DATADIRTY);
			}
		});
		logic.newGame();

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		// Log.i("tg","GameSurfaceView/onTouchEvent");
		// this.requestRender();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			touchStartX = event.getRawX();
			touchStartY = event.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			float dx = event.getRawX() - touchStartX;
			float dy = event.getRawY() - touchStartY;
			double radian = Math.PI / 6;
			float spX = (float) (dx * Math.sin(radian) + dy * Math.cos(radian));
			float spZ = (float) (dy * Math.cos(radian) - dx * Math.sin(radian));
			boolean flag = false;
			if (spX > 20.0f) {
				logic.moveActiveBlock(GameLogic.MOVE_RIGHT);
				flag = true;
			} else if (spX < -20.f) {
				logic.moveActiveBlock(GameLogic.MOVE_LEFT);
				flag = true;
			}
			if (spZ > 20.0f) {
				logic.moveActiveBlock(GameLogic.MOVE_BACK);
				flag = true;
			} else if (spZ < -20.f) {
				logic.moveActiveBlock(GameLogic.MOVE_FRONT);
				flag = true;
			}
			if (flag) {
				touchStartX = event.getRawX();
				touchStartY = event.getRawY();
			}
			break;
		case MotionEvent.ACTION_UP:
			// float dx2 = event.getRawX() - touchStartX;
			// float dy2 = event.getRawY() - touchStartY;
			// double radian = Math.PI/6;
			// float spX = (float) (dx2*Math.sin(radian) +
			// dy2*Math.cos(radian));
			// float spZ = (float) (dy2*Math.cos(radian) -
			// dx2*Math.sin(radian));
			// Log.i("tg","GameSurfaceView/onTouchEvent/dx2:" + dx2 + "/dy2:" +
			// dy2);
			// Log.i("tg","GameSurfaceView/onTouchEvent/spX:" + spX + "/spZ:" +
			// spZ);
			break;
		}
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Log.i("tg","GameSurfaceView/onKeyDown" + event);
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_LEFT:
			logic.moveActiveBlock(GameLogic.MOVE_LEFT);
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			logic.moveActiveBlock(GameLogic.MOVE_RIGHT);
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			logic.moveActiveBlock(GameLogic.MOVE_FRONT);
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			logic.moveActiveBlock(GameLogic.MOVE_BACK);
			break;
		case KeyEvent.KEYCODE_N:
			logic.newGame();
			break;
		case KeyEvent.KEYCODE_R:
			logic.rotateActiveBlock();
			break;
		case KeyEvent.KEYCODE_D:
			logic.dropActiveBlock();
			break;
		case KeyEvent.KEYCODE_SPACE:
			logic.dropActiveBlock();
			break;
		case KeyEvent.KEYCODE_C:
			logic.testClearLayer();
			break;
		}
		return true;
	}

}

