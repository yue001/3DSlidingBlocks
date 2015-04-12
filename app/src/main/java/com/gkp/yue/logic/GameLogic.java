package com.gkp.yue.logic;

import com.gkp.yue.Constant;
import com.gkp.yue.GameData;

public class GameLogic {

	/**
	 * GameData.data的直接缓冲区
	 */
	private byte[][][] dataBuffer = null;
	private static GameLogic logic = null;
	private byte[] blocks = null;
	private byte[] activeBlock = null;

	private GameDataChangedListener gameDataChangedListener = null;
	private ActiveBlockCreateListener activeBlockCreateListener = null;

	private boolean dropping = false;
	/**
	 * 活动快移动方向
	 */
	public static final int MOVE_LEFT = 1;
	public static final int MOVE_RIGHT = 2;
	public static final int MOVE_FRONT = 3;
	public static final int MOVE_BACK = 4;

	private GameLogic() {
		dataBuffer = new byte[Constant.LAY][Constant.ROW][Constant.COL];
		byte x = Constant.COL / 2;
		byte y = Constant.LAY - 2;
		byte z = Constant.ROW / 2;
		blocks = new byte[] { x, y, z, (byte) (x + 1), y, z, x, (byte) (y + 1),
				z, x, y, (byte) (z + 1) };
		activeBlock = new byte[9];
	}

	public static GameLogic getInstance() {
		if (null == logic) {
			logic = new GameLogic();
		}
		return logic;
	}

	/**
	 * 生成新的活动块
	 */
	private void generateActiveBlock() {
		int index = ((int) (Math.random() * 2) + 2) * 3;
		// Log.i("tg", "GameLogic/generateActiveBlock/" + index);
		int offset = 0;
		for (int i = 0; i < 9; i++) {
			if (i == index) {
				offset += 3;
			}
			activeBlock[i] = blocks[i + offset];
		}
		// Log.i("tg", "GameLogic/generateActiveBlock/" +
		// activeBlockCreateListener);
		if (null != activeBlockCreateListener) {
			activeBlockCreateListener.onActiveBlockCreate();
		}
	}

	/**
	 * 新游戏开局
	 */
	public void newGame() {
		for (int lay = 0; lay < Constant.LAY; lay++) {
			for (int row = 0; row < Constant.ROW; row++) {
				for (int col = 0; col < Constant.COL; col++) {
					dataBuffer[lay][row][col] = 0;
				}
			}
		}
		this.generateActiveBlock();
		this.submitData();
	}

	/**
	 * 绕垂直方向逆时针旋转活动块
	 */
	public void rotateActiveBlock() {
		byte[] temp = new byte[9];
		System.arraycopy(this.activeBlock, 0, temp, 0, 9);
		for (int i = 1; i <= 2; i++) {

			if (activeBlock[i * 3 + 1] == activeBlock[1]) {
				if (activeBlock[i * 3] == activeBlock[0]) {
					if (activeBlock[i * 3 + 2] > activeBlock[2]) {
						temp[i * 3] = (byte) (activeBlock[0] + 1);
					} else if (activeBlock[i * 3 + 2] < temp[2]) {
						temp[i * 3] = (byte) (activeBlock[0] - 1);
					}
				} else {
					temp[i * 3] = activeBlock[0];
				}
				if (activeBlock[i * 3 + 2] == activeBlock[2]) {
					if (activeBlock[i * 3] > activeBlock[0]) {
						temp[i * 3 + 2] = (byte) (activeBlock[2] - 1);
					} else if (activeBlock[i * 3] < activeBlock[0]) {
						temp[i * 3 + 2] = (byte) (activeBlock[2] + 1);
					}
				} else {
					temp[i * 3 + 2] = activeBlock[2];
				}
			}
		}
		boolean flag = doCollisionDetection(temp);
		// Log.i("tg","GameLogic/rotateActiveBlock/" + flag);
		if (flag) {
			System.arraycopy(temp, 0, this.activeBlock, 0, 9);
		}
		submitData();
	}

	/**
	 * 使活动快加速下落
	 */
	public void dropActiveBlock() {
		if (!dropping) {
			new Thread() {
				@Override
				public void run() {
					byte[] temp = new byte[9];
					System.arraycopy(activeBlock, 0, temp, 0, 9);
					dropping = true;
					while (dropping) {
						for (int i = 0; i < 3; i++) {
							temp[i * 3 + 1] -= 1;
						}
						dropping = doCollisionDetection(temp);
						// Log.i("tg","GameLogic/dropActiveBlock/" + flag);
						if (dropping) {
							System.arraycopy(temp, 0, activeBlock, 0, 9);
						}
						submitData();
						try {
							Thread.sleep(70);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					int[] filledLays = findFilledLayer();
					for (int i = 0; i < filledLays.length; i++) {
						if (1 == filledLays[i]) {
							clearFilledLayer(i);
						}
					}
					generateActiveBlock();
					submitData();
				}
			}.start();
		}

	}

	/**
	 * 移动活动块
	 * 
	 * @param dir
	 *            方向，
	 */
	public void moveActiveBlock(int dir) {
		byte[] temp = new byte[9];
		System.arraycopy(this.activeBlock, 0, temp, 0, 9);
		switch (dir) {
		case MOVE_LEFT:
			for (int i = 0; i < 3; i++) {
				temp[i * 3] -= 1;
			}
			break;
		case MOVE_RIGHT:
			for (int i = 0; i < 3; i++) {
				temp[i * 3] += 1;
			}
			break;
		case MOVE_FRONT:
			for (int i = 0; i < 3; i++) {
				temp[i * 3 + 2] -= 1;
			}
			break;
		case MOVE_BACK:
			for (int i = 0; i < 3; i++) {
				temp[i * 3 + 2] += 1;
			}
			break;
		}

		boolean flag = doCollisionDetection(temp);
		// Log.i("tg","GameLogic/moveActiveBlock/" + flag);
		if (flag) {
			System.arraycopy(temp, 0, this.activeBlock, 0, 9);
		}
		submitData();
	}

	/**
	 * 活动块碰撞检测
	 * 
	 * @param tempBlock
	 *            活动块副本
	 * @return true-没有碰撞；false-有碰撞；
	 */
	private boolean doCollisionDetection(byte[] tempBlock) {
		this.wipeActiveBlockFromDataBuffer();
		for (int i = 0; i < 3; i++) {
			// Log.i("tg","GameLogic/doCollisionDetection/" );
			if (tempBlock[i * 3] < 0
					|| tempBlock[i * 3] >= Constant.COL
					|| tempBlock[i * 3 + 1] < 0
					|| tempBlock[i * 3 + 1] >= Constant.LAY
					|| tempBlock[i * 3 + 2] < 0
					|| tempBlock[i * 3 + 2] >= Constant.ROW
					|| dataBuffer[tempBlock[i * 3 + 1]][tempBlock[i * 3 + 2]][tempBlock[i * 3]] != 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 查找完全堆满的层，
	 * 
	 * @return 层数组，为1的索引层全满，为0的索引层不满。
	 */
	private int[] findFilledLayer() {
		int[] resultLays = new int[Constant.LAY - 2];
		for (int lay = 0; lay < Constant.LAY - 2; lay++) {
			resultLays[lay] = 1;
			for (int row = 0; row < Constant.ROW; row++) {
				for (int col = 0; col < Constant.COL; col++) {
					if (0 == dataBuffer[lay][row][col]) {
						resultLays[lay] = 0;
					}
				}
			}
		}
		return resultLays;
	}

	/**
	 * 清除完全放满的层，高层自动下落。
	 * 
	 * @param lay
	 *            要清除的层。
	 */
	private void clearFilledLayer(int lay) {
		for (; lay < Constant.LAY - 3; lay++) {
			for (int row = 0; row < Constant.ROW; row++) {
				for (int col = 0; col < Constant.COL; col++) {
					dataBuffer[lay][row][col] = dataBuffer[lay + 1][row][col];
				}
			}
		}
	}

	/**
	 * 为方便测试的快捷操作
	 */
	public void testClearLayer() {
		int lay = 0, row = 0, col = 0;
		for (; row < Constant.ROW; row++) {
			for (col = 0; col < Constant.COL; col++) {
				dataBuffer[lay][row][col] = 1;
			}
		}
		dataBuffer[lay][row - 1][col - 1] = 0;
		dataBuffer[lay][row - 1][col - 2] = 0;
		submitData();
	}

	/**
	 * 擦除活动块
	 */
	private void wipeActiveBlockFromDataBuffer() {
		for (int i = 0; i < 3; i++) {
			dataBuffer[activeBlock[i * 3 + 1]][activeBlock[i * 3 + 2]][activeBlock[i * 3]] = 0;
		}
	}

	/**
	 * 填充活动块到游戏数据缓冲
	 */
	private void fillActiveBlockToDataBuffer() {
		for (int i = 0; i < 3; i++) {
			dataBuffer[activeBlock[i * 3 + 1]][activeBlock[i * 3 + 2]][activeBlock[i * 3]] = 1;
		}
	}

	/**
	 * 加载游戏数据
	 */
	private void loadData() {
		for (int lay = 0; lay < Constant.LAY; lay++) {
			for (int row = 0; row < Constant.ROW; row++) {
				System.arraycopy(GameData.data[lay][row], 0,
						this.dataBuffer[lay][row], 0, Constant.COL);
			}
		}
	}

	/**
	 * 提交更新游戏数据
	 */
	private void submitData() {
		// int length = GameData.ROW*GameData.COL*GameData.LAY;
		// Log.i("tg","GameLogic/" + data.length + "/" + GameData.data.length +
		// "/" + length);
		fillActiveBlockToDataBuffer();
		for (int lay = 0; lay < Constant.LAY; lay++) {
			for (int row = 0; row < Constant.ROW; row++) {
				System.arraycopy(this.dataBuffer[lay][row], 0,
						GameData.data[lay][row], 0, Constant.COL);
			}
		}
		if (null != gameDataChangedListener) {
			gameDataChangedListener.onGameDataChanged();
		}
	}

	/**
	 * 设置游戏数据改变监听器
	 */
	public void setGameDataChangedListener(GameDataChangedListener listener) {
		this.gameDataChangedListener = listener;
	}

	/**
	 * 设置创建活动块监听器
	 */
	public void setActiveBlockCreateListener(ActiveBlockCreateListener listener) {
		this.activeBlockCreateListener = listener;
	}

	/**
	 * 游戏数据改变监听接口
	 */
	public interface GameDataChangedListener {
		public void onGameDataChanged();
	}

	public interface ActiveBlockCreateListener {
		public void onActiveBlockCreate();
	}
}
