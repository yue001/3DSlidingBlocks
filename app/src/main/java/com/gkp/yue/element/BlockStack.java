package com.gkp.yue.element;

import javax.microedition.khronos.opengles.GL10;

import com.gkp.yue.Constant;
import com.gkp.yue.GameData;

public class BlockStack {

	private Block block = null;

	public BlockStack() {
		this.block = new Block();
	}

	public void drawSelf(GL10 gl) {

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);// 启用顶点坐标数组
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);// 打开法线数组
		// 开启纹理贴图
		gl.glEnable(GL10.GL_TEXTURE_2D);
		// 允许使用纹理ST坐标缓冲
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		for (int lay = 0; lay < Constant.LAY; lay++) {
			for (int row = 0; row < Constant.ROW; row++) {
				for (int col = 0; col < Constant.COL; col++) {
					if (1 == GameData.data[lay][row][col]) {
						gl.glPushMatrix();
						block.drawAt(gl, row, col, lay);
						gl.glPopMatrix();
					}
				}
			}
		}

		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
	}
}
