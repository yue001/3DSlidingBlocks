package com.gkp.yue;

public class GameData {

	public static byte[][][] data = null;
	static{
		data = new byte[Constant.LAY][Constant.ROW][Constant.COL];
		
/*		for(int lay = 0; lay<GameData.LAY; lay++){
			for(int row = 0; row<GameData.ROW; row++){
				for(int col = 0; col<GameData.COL; col++){
					if(Math.random()>0.5f){
						GameData.data[lay][row][col] = 1;
					}
				}
			}
		}*/
		
	}

}
