package com.gkp.yue.element;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import com.gkp.yue.Constant;

public class CoordinateLine {
	private FloatBuffer mVertexBuffer = null;

	public CoordinateLine() {

		float containerH = Constant.BOLOCK_LONG * (Constant.LAY - 2);

		float[] vertices = {
				0,
				0,
				0, // 原点
				0.2f, 0, 0, 0.4f, 0, 0, 0.6f, 0, 0, 0.8f, 0, 0, 1.0f, 0, 0,
				1.2f, 0, 0, 1.4f, 0, 0,
				1.9f,
				0,
				0, // X轴上点
				0, 0.2f, 0, 0, 0.4f, 0, 0, 0.6f, 0, 0, 0.8f, 0, 0, 1.0f, 0,
				0,
				2,
				0, // Y轴上点
				0, 0, 0.2f, 0, 0, 0.4f, 0, 0, 0.6f, 0, 0, 0.8f, 0, 0, 1.0f, 0,
				0,
				1.5f, // Z轴上点
				1.4f, 0,
				1.0f, // 地面近角点
				1.4f, containerH, 0f, 0f, containerH, 1.0f, 1.4f, containerH,
				1.0f // 顶面三个顶点
		};

		// vertices.length*4是因为一个Float四个字节
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());// 设置字节顺序
		mVertexBuffer = vbb.asFloatBuffer();// 转换为float型缓冲
		mVertexBuffer.put(vertices);// 向缓冲区中放入顶点坐标数据
		mVertexBuffer.position(0);// 设置缓冲区起始位置

	}

	public void drawSelf(GL10 gl) {
		// Log.i("tg","CoordinateLine/drawSelf");
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);// 启用顶点坐标数组
		// gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);// 打开法线数组
		// 为画笔指定顶点坐标数据
		gl.glVertexPointer(3, // 每个顶点的坐标数量为3 xyz
				GL10.GL_FLOAT, // 顶点坐标值的类型为 GL_FIXED
				0, // 连续顶点坐标数据之间的间隔
				mVertexBuffer // 顶点坐标数据
		);
		// 为画笔指定顶点法向量数据
		// gl.glNormalPointer(GL10.GL_FLOAT, 0, mVertexBuffer);

		// gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 4 );
		// X轴线
		gl.glColor4f(1, 0, 0, 1);
		gl.glDrawElements(GL10.GL_LINE_STRIP, 2, GL10.GL_UNSIGNED_BYTE,
				ByteBuffer.wrap(new byte[] { 0, 8 }));
		// Y轴线
		gl.glColor4f(0, 1, 0, 1);
		gl.glDrawElements(GL10.GL_LINE_STRIP, 2, GL10.GL_UNSIGNED_BYTE,
				ByteBuffer.wrap(new byte[] { 0, 14 }));
		// Z轴线
		gl.glColor4f(0.5f, 0.5f, 0, 1);
		gl.glDrawElements(GL10.GL_LINE_STRIP, 2, GL10.GL_UNSIGNED_BYTE,
				ByteBuffer.wrap(new byte[] { 0, 20 }));

		// 边界线
		// 环境光为蓝色材质
		float ambientMaterial[] = { 0f, 0f, 1f, 1.0f };
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT,
				ambientMaterial, 0);
		gl.glLineWidth(2);
		gl.glDrawElements(GL10.GL_LINE_STRIP, 2, GL10.GL_UNSIGNED_BYTE,
				ByteBuffer.wrap(new byte[] { 24, 21 }));
		gl.glDrawElements(GL10.GL_LINE_STRIP, 2, GL10.GL_UNSIGNED_BYTE,
				ByteBuffer.wrap(new byte[] { 24, 22 }));
		gl.glDrawElements(GL10.GL_LINE_STRIP, 2, GL10.GL_UNSIGNED_BYTE,
				ByteBuffer.wrap(new byte[] { 24, 23 }));

		// 轴线上画点
		gl.glPointSize(3f);
		gl.glTranslatef(0, Constant.BOLOCK_LONG * (Constant.LAY - 2), 0);
		gl.glPushMatrix();
		int cZ = (int) (Constant.BOLOCK_LONG * Constant.ROW / 0.05f);
		for (int f = 0; f < cZ; f++) {
			gl.glDrawElements(GL10.GL_POINTS, 6, GL10.GL_UNSIGNED_BYTE,
					ByteBuffer.wrap(new byte[] { 1, 2, 3, 4, 5, 6 }));
			gl.glTranslatef(0, 0, 0.05f);
		}
		gl.glPopMatrix();
		gl.glPushMatrix();
		int cX = (int) (Constant.BOLOCK_LONG * Constant.COL / 0.05f);
		for (int f = 0; f < cX; f++) {
			// gl.glDrawElements(GL10.GL_POINTS, 7, GL10.GL_UNSIGNED_BYTE,
			// ByteBuffer.wrap(new byte[]{1,2,3,4,5,6,7}));
			gl.glDrawElements(GL10.GL_POINTS, 4, GL10.GL_UNSIGNED_BYTE,
					ByteBuffer.wrap(new byte[] { 15, 16, 17, 18 }));
			gl.glTranslatef(0.05f, 0, 0);
		}
		gl.glPopMatrix();

		// 环境光为白色材质
		float ambientMaterial2[] = { 1f, 1f, 1f, 1.0f };
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT,
				ambientMaterial2, 0);
		// 底面
		// gl.glColor4f(0.7f, 0.7f, 0.7f, 0.2f);
		// gl.glDrawElements(GL10.GL_TRIANGLES, 6, GL10.GL_UNSIGNED_BYTE,
		// ByteBuffer.wrap(new byte[]{0,7,21,0,21,19}));

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

	}
}
