package com.gkp.yue.element;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import com.gkp.yue.Constant;

public class Block {

	/** 顶点数量 */
	public static final int VERTEX_COUNT = 36;

	public static final float[] VERTEX_COORD = new float[]{
		0,1,0,	0,1,1,	1,1,0,	1,1,1,	0,1,1,	1,1,0,							
		
		0,0,0,	0,0,1,	0,1,0,	0,1,1,	0,0,1,	0,1,0,	
		
		0,0,0,	0,0,1,	1,0,0,	1,0,1,	0,0,1,	1,0,0,
		
		1,0,0,	1,0,1,	1,1,0,	1,1,1,	1,0,1,	1,1,0,
		
		0,0,1,	1,0,1,	0,1,1,	1,1,1,	1,0,1,	0,1,1,
		
		0,0,0,	1,0,0,	0,1,0,	1,1,0,	1,0,0,	0,1,0,
	};
	public static final float[] NORMALS_COORD = new float[]{
		0,1,0,	0,1,0,	0,1,0,	0,1,0,	0,1,0,	0,1,0,								//Y+
		-1,0,0,	-1,0,0,	-1,0,0,	-1,0,0,	-1,0,0,	-1,0,0,								//X-
		0,-1,0,	0,-1,0,	0,-1,0,	0,-1,0,	0,-1,0,	0,-1,0,								//Y-
		1,0,0,	1,0,0,	1,0,0,	1,0,0,	1,0,0,	1,0,0,								//X+
		0,0,1,	0,0,1,	0,0,1,	0,0,1,	0,0,1,	0,0,1,								//Z+
		0,0,-1,	0,0,-1,	0,0,-1,	0,0,-1,	0,0,-1,	0,0,-1,								//Z-
	};
	public static final float[] TEXTURE_COORD = new float[]{
		0,0,		0,1,		1,0,		1,1,		0,1,		1,0,
		0,0,		0,1,		1,0,		1,1,		0,1,		1,0,
		0,0,		0,1,		1,0,		1,1,		0,1,		1,0,
		0,0,		0,1,		1,0,		1,1,		0,1,		1,0,
		0,0,		0,1,		1,0,		1,1,		0,1,		1,0,
		0,0,		0,1,		1,0,		1,1,		0,1,		1,0,
	};
	//使用静态代码块对数据进行缩放更正；
	static{
		for(int i=0;i<VERTEX_COORD.length;i++){
			VERTEX_COORD[i] = VERTEX_COORD[i] * Constant.BOLOCK_LONG;
		}
	}
	
	private FloatBuffer mVertexBuffer = null;
	private FloatBuffer mNormalBuffer = null;
	private FloatBuffer mTextureBuffer = null;
	
	public Block(){
		// vertices.length*4是因为一个Float四个字节
		ByteBuffer vbb = ByteBuffer.allocateDirect(VERTEX_COORD.length * 4);
		vbb.order(ByteOrder.nativeOrder());// 设置字节顺序
		mVertexBuffer = vbb.asFloatBuffer();// 转换为float型缓冲
		mVertexBuffer.put(VERTEX_COORD);// 向缓冲区中放入顶点坐标数据
		mVertexBuffer.position(0);// 设置缓冲区起始位置
		
		ByteBuffer nbb = ByteBuffer.allocateDirect(NORMALS_COORD.length * 4);
		nbb.order(ByteOrder.nativeOrder());// 设置字节顺序
		mNormalBuffer = nbb.asFloatBuffer();// 转换为int型缓冲
		mNormalBuffer.put(NORMALS_COORD);// 向缓冲区中放入顶点着色数据
		mNormalBuffer.position(0);// 设置缓冲区起始位置
		
		ByteBuffer tbb = ByteBuffer.allocateDirect(TEXTURE_COORD.length * 4);
		tbb.order(ByteOrder.nativeOrder());// 设置字节顺序
		mTextureBuffer = tbb.asFloatBuffer();// 转换为int型缓冲
		mTextureBuffer.put(TEXTURE_COORD);// 向缓冲区中放入顶点着色数据
		mTextureBuffer.position(0);// 设置缓冲区起始位置
	}
	/**
	 * 在空间画出一个方块
	 * @param gl
	 * @param row	行
	 * @param col		列
	 * @param lay		层
	 */
	public void drawAt(GL10 gl, int row,int col,int lay ){
//		Log.i("tg","Block/drawAt");
		
		gl.glTranslatef(Constant.BOLOCK_LONG*col , Constant.BOLOCK_LONG*lay , Constant.BOLOCK_LONG*row );
		
		// 为画笔指定顶点坐标数据
		gl.glVertexPointer(3, 				// 每个顶点的坐标数量为3 xyz
				GL10.GL_FLOAT,			 	// 顶点坐标值的类型为 GL_FIXED
				0, 										// 连续顶点坐标数据之间的间隔
				mVertexBuffer 					// 顶点坐标数据
		);
		// 为画笔指定顶点法向量数据
		gl.glNormalPointer(GL10.GL_FLOAT, 0, mVertexBuffer);

		// 指定纹理ST坐标缓冲
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer);
		// 绑定当前纹理
		gl.glBindTexture(GL10.GL_TEXTURE_2D, TextureManager.getTextureId(TextureManager.TEXTURE_INDEX_BOX));
		
		// 绘制图形 , 以三角形方式填充
		gl.glDrawArrays(GL10.GL_TRIANGLES, 	0, 	VERTEX_COUNT );
		
		
	}
	
}

