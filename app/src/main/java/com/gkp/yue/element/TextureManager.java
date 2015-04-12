package com.gkp.yue.element;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import com.gkp.yue.R;


public class TextureManager {

	public static final int TEXTURE_INDEX_BOX = 0;
	public static final int TEXTURE_INDEX_WALL = 1;
	public static final int TEXTURE_INDEX_WOOD = 2;
	public static final int TEXTURE_INDEX_LAWN = 3;
	public static final int TEXTURE_INDEX_BACKGROUND = 4;
	public static int[] textureIds = new int[5];
	private static int[] textureSrcs = {R.drawable.box2,R.drawable.wall01,R.drawable.wood,R.drawable.lawn,R.drawable.bg5};
	
	public static int getTextureId(int index){
//		Log.i("tg","TextureManager/getTextureId/" + textureIds[index]);
		return textureIds[index];
	}
	/**初始化纹理*/
	public static void initTexture(GL10 gl,Resources res) {


		//获取未使用的纹理对象ID
		gl.glGenTextures(textureIds.length, textureIds, 0);

		for(int i=0;i<textureIds.length;i++){
//			Log.i("tg","TextureManager/initTexture/" + textureIds[i]);
			//绑定纹理对象
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIds[i]);
			//设置纹理控制，指定使用纹理时的处理方式
			//缩小过滤：一个像素代表多个纹素。
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, 	//纹理目标
					GL10.GL_TEXTURE_MIN_FILTER,			//纹理缩小过滤
					GL10.GL_NEAREST								//使用距离当前渲染像素中心最近的纹素
					);
			//放大过滤：一个像素是一个纹素的一部分。
			//放大过滤时，使用距离当前渲染像素中心，最近的4个纹素加权平均值，也叫双线性过滤。
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
					GL10.GL_LINEAR);		//
			//设置纹理贴图方式，指定对超出【0,1】的纹理坐标的处理方式
			//左下角是【0,0】，右上角是【1,1】，横向是S维，纵向是T维。android以左上角为原点
			//S维贴图方式：重复平铺
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
					GL10.GL_REPEAT);
			//T维贴图方式：重复平铺
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
					GL10.GL_REPEAT);
			bindBitmap(i,res);
		}

	}
	private static void bindBitmap(int index,Resources res){
		Bitmap bitmap = null;
		InputStream is = res.openRawResource(textureSrcs[index]);
		try {
			bitmap = BitmapFactory.decodeStream(is);
		} finally {
			if(is != null){
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		//为纹理对象指定位图
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
		bitmap.recycle();
	}
}

