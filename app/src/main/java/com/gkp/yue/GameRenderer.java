package com.gkp.yue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.res.Resources;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.view.View;

import com.gkp.yue.element.BackWall;
import com.gkp.yue.element.BlockStack;
import com.gkp.yue.element.Container;
import com.gkp.yue.element.CoordinateLine;
import com.gkp.yue.element.TextureManager;


public class GameRenderer implements Renderer {

	private BackWall backWall = null;
	private CoordinateLine coordLine = null;
	private Container container = null;
	private BlockStack blockStack = null;
	
	private Resources res = null;
	private static int count = 0;

	public GameRenderer(View view) {
		backWall = new BackWall();
		coordLine = new CoordinateLine();
		container = new Container();
		blockStack = new BlockStack();
		res = view.getResources();
		
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// Set the background frame color
		gl.glClearColor(0.3f, 0.3f, 0.4f, 0.2f);
		// 启用深度测试, 不启用时，不管远近，后画的会覆盖之前画的，
		gl.glEnable(GL10.GL_DEPTH_TEST);
//		gl.glEnable(GL10.GL_BLEND);
//		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		TextureManager.initTexture(gl, res);
		initLight(gl);
		initMaterial(gl);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// Log.i("tg","Surface changed.。");
		// 设置视窗
		gl.glViewport(0, 0, width, height);
		// 适应屏幕比例
		float ratio = (float) width / height;
		// 设置矩阵为投射模式
		gl.glMatrixMode(GL10.GL_PROJECTION); // set matrix to projection mode
		// 重置矩阵
		gl.glLoadIdentity(); // reset the matrix to its default state
		// 设置投射椎体 // apply the projection matrix
		gl.glFrustumf(-ratio, ratio, -1, 1, 4, 10);

		// 设置 GL_MODELVIEW(模型观察) 转换模式
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		// 重置矩阵，设置当前矩阵为单位矩阵，相当于渲染之前清屏
		gl.glLoadIdentity();
		// 每次使用GL_MODELVIEW 模式时, 必须重新设置视点, center[x,y,z]定义屏幕中心点在空间坐标的位置
		GLU.gluLookAt(gl, 4, 4f, 4, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
		gl.glTranslatef(0, -0.6f, 0f);
		
//		GLU.gluLookAt(gl, 0, 7f, 0, 0.5f, 0f, 0.5f, 1f, 0f, 0.0f);


	}

	@Override
	public void onDrawFrame(GL10 gl) {
		// 重画背景
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
//		gl.glEnable(GL10.GL_COLOR_MATERIAL);

		gl.glPushMatrix();
		gl.glRotatef(45f, 0, 1, 0);
		gl.glRotatef(40f, -1, 0, 0);		//真机
//		gl.glTranslatef(0, 0.5f, -1f);	//虚拟机
		gl.glTranslatef(0, 0.5f, 0f);
		backWall.drawSelf(gl);
		gl.glPopMatrix();
		
		gl.glPushMatrix();
		coordLine.drawSelf(gl);
		gl.glPopMatrix();
		
		gl.glPushMatrix();
//		gl.glColor4f(0.5f, 0.5f, 0.5f, 0.2f);
		container.drawSelf(gl);
		gl.glPopMatrix();
		
		gl.glPushMatrix();
//		gl.glColor4f(0.99f, 0.99f, 0.99f, 0.7f);
		blockStack.drawSelf(gl);
		gl.glPopMatrix();

	}

	/** 初始化灯光
	 * 定义各种类型光的光谱
	 * */
	private void initLight(GL10 gl) {
		gl.glEnable(GL10.GL_LIGHTING);		//打开照明总开关
		gl.glEnable(GL10.GL_LIGHT1);		// 打开1号灯

		// 环境光设置
		float[] ambientParams = { 0.7f, 0.7f, 0.7f, 1.0f };// 光参数 RGBA
		gl.glLightfv(GL10.GL_LIGHT1,		//光源序号
				GL10.GL_AMBIENT, 			//光照参数名-环境光
				ambientParams, 				//参数值
				0							//偏移
				);
		// 散射光设置
		float[] diffuseParams = { 0.9f, 0.9f, 0.9f, 1.0f };// 光参数 RGBA
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_DIFFUSE, diffuseParams, 0);
		// 反射光设置
		float[] specularParams = { 1f, 1f, 1f, 1.0f };// 光参数 RGBA
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_SPECULAR, specularParams, 0);

		//模拟器，虚拟机，正常
/*		//光源位置
		float[] positionParams = { 4, 4, 4,1 };
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_POSITION, positionParams, 0);
		//聚光灯方向矢量
		float[] directionParams = {-1,-1,-1};
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_SPOT_DIRECTION , directionParams, 0);
*/		
		//???真机测试光源位置和方向矢量都在Z轴上时灯光有效。
		//光源位置
		float[] positionParams = { 0, 0, 7,1 };
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_POSITION, positionParams, 0);
		//聚光灯方向矢量
		float[] directionParams = {0,0,-1};
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_SPOT_DIRECTION , directionParams, 0);
		
		//聚光角度（0-90）度
		gl.glLightf(GL10.GL_LIGHT1, GL10.GL_SPOT_CUTOFF , 10);
		//聚光程度（0-128）实现聚焦
		gl.glLightf(GL10.GL_LIGHT1, GL10.GL_SPOT_EXPONENT  , 30);
	}

	//初始化材质
	private void initMaterial(GL10 gl)
	{//材质为白色时什么颜色的光照在上面就将体现出什么颜色
        //环境光为白色材质
        float ambientMaterial[] = {1f, 1f, 1f, 1.0f};
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, ambientMaterial,0);
        //散射光为白色材质
        float diffuseMaterial[] = {1f, 1f, 1f, 1.0f};
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, diffuseMaterial,0);
        //高光材质为白色
        float specularMaterial[] = {1f, 1f, 1f, 1.0f};
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, specularMaterial,0);
        gl.glMaterialf(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, 120f);
	}

}

