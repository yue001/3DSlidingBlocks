package com.gkp.yue.element;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import com.gkp.yue.Constant;

public class Container {
	/** 顶点坐标数据缓冲 */
	private FloatBuffer mVertexBuffer;
	/** 顶点法向量数据缓冲 */
	private FloatBuffer mNormalBuffer;
	/** 顶点纹理数据缓冲，存储每个顶点在位图中的坐标 */
	private FloatBuffer mTextureBuffer;

	/** 色子类 */
	public Container() {
		initDataBuffer();
	}

	/** 初始化定点数据缓冲区 */
	private void initDataBuffer() {
		float cW = Constant.BOLOCK_LONG * Constant.ROW;
		float cL = Constant.BOLOCK_LONG * Constant.COL;
		float cH = Constant.BOLOCK_LONG * (Constant.LAY - 2);

		float[] vertices = { 0, cH, cW, 0, 0, cW, 0, 0, 0, 0, cH, cW, 0, 0, 0,
				0, cH, 0, // wall顶点集
				0, cH, 0, 0, 0, 0, cL, 0, 0, 0, cH, 0, cL, 0, 0, cL, cH, 0, // wood顶点集
				0, 0, 0, 0, 0, cW, cL, 0, cW, 0, 0, 0, cL, 0, cW, cL, 0, 0 // lawn顶点集
		};
		float[] normals = { 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0,
				0, // wall法向量
				0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, // wood法向量
				0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, // lawn法向量
		};
		// 逆时针画左下角，右上角。
		float[] texST = { 0, 0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1,
				0, 0, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0, }; // 要贴的图宽高须是2的n
																			// 次幂
		// vertices.length*4是因为一个Float四个字节
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());// 设置字节顺序
		mVertexBuffer = vbb.asFloatBuffer();// 转换为float型缓冲
		mVertexBuffer.put(vertices);// 向缓冲区中放入顶点坐标数据
		mVertexBuffer.position(0);// 设置缓冲区起始位置

		ByteBuffer nbb = ByteBuffer.allocateDirect(normals.length * 4);
		nbb.order(ByteOrder.nativeOrder());// 设置字节顺序
		mNormalBuffer = nbb.asFloatBuffer();// 转换为int型缓冲
		mNormalBuffer.put(normals);// 向缓冲区中放入顶点着色数据
		mNormalBuffer.position(0);// 设置缓冲区起始位置

		ByteBuffer tbb = ByteBuffer.allocateDirect(texST.length * 4);
		tbb.order(ByteOrder.nativeOrder());// 设置字节顺序
		mTextureBuffer = tbb.asFloatBuffer();// 转换为int型缓冲
		mTextureBuffer.put(texST);// 向缓冲区中放入顶点着色数据
		mTextureBuffer.position(0);// 设置缓冲区起始位置
	}

	/** 绘制容器 */
	public void drawSelf(GL10 gl) {
		// Log.i("tg","to draw dice..");

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);// 启用顶点坐标数组
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);// 打开法线数组
		// 开启纹理贴图
		gl.glEnable(GL10.GL_TEXTURE_2D);
		// 允许使用纹理ST坐标缓冲
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		// 为画笔指定顶点坐标数据
		gl.glVertexPointer(3, // 每个顶点的坐标数量为3 xyz
				GL10.GL_FLOAT, // 顶点坐标值的类型为 GL_FIXED
				0, // 连续顶点坐标数据之间的间隔
				mVertexBuffer // 顶点坐标数据
		);

		// 为画笔指定顶点法向量数据
		gl.glNormalPointer(GL10.GL_FLOAT, 0, mNormalBuffer);

		// 指定纹理ST坐标缓冲
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer);

		/*
		 * //允许忽略某个面 gl.glEnable(GL10.GL_CULL_FACE);
		 * //设置顺时针为前面，GL_CCW-逆时针（默认），GL_CW-顺时针 gl.glFrontFace(GL10.GL_CW);
		 * //忽略后面，GL_FRONT-正面，GL_BACK-反面。 gl.glCullFace(GL10.GL_BACK);
		 */

		/*
		 * for(int i=0;i<3;i++){ // 绑定当前纹理 gl.glBindTexture(GL10.GL_TEXTURE_2D,
		 * TextureManager.getTextureId( i+1 )); // 绘制图形 , 以三角形方式填充
		 * gl.glDrawArrays(GL10.GL_TRIANGLES, i*6, 6 );
		 * 
		 * }
		 */
		// 绑定当前纹理
		gl.glBindTexture(GL10.GL_TEXTURE_2D,
				TextureManager.getTextureId(TextureManager.TEXTURE_INDEX_WOOD));
		// 绘制图形 , 以三角形方式填充
		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 6);
		gl.glDrawArrays(GL10.GL_TRIANGLES, 6, 6);
		// 绑定当前纹理
		gl.glBindTexture(GL10.GL_TEXTURE_2D,
				TextureManager.getTextureId(TextureManager.TEXTURE_INDEX_LAWN));
		// 绘制图形 , 以三角形方式填充
		gl.glDrawArrays(GL10.GL_TRIANGLES, 12, 6);

		// 解绑纹理
		// gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);
		// 禁用忽略正面或反面
		// gl.glDisable(GL10.GL_CULL_FACE);

		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
	}
}
