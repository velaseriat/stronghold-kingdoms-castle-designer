package castledesigner;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.TreeSet;

public class CasFileWriter {

	TileBuilding[][] gridData;
	File[] casFiles;

	private HashMap<CastlePoint, BuildingType> castleLayoutMap;
	private HashMap<Point, BuildingType> preWriteMap;

	public void writeToCasFiles(){
		castleLayoutMap = convertGridData(gridData);
		preWriteMap = addCasFileOffset(castleLayoutMap);
		//maybe someone wants to prioritize centered structures? in hex, coords of keep is (36, 36).
		outputToFiles(preWriteMap);
	}

	private void outputToFiles(HashMap<Point, BuildingType> pwm) {
		int totalStructures = pwm.size();
		for (Point p : pwm.keySet()){
			pointToCasFileShortConverter(p);
		}
	}
	
	
	private short pointToCasFileShortConverter(Point p){
		File f = new File("HERO.cas");
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(f);
			short[] payload = {0x2122, 0x2324, 0x2122, 0x2324};
			ByteBuffer myByteBuffer = ByteBuffer.allocate(8);
			myByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
			
			ShortBuffer myShortBuffer = myByteBuffer.asShortBuffer();
			myShortBuffer.put(payload);
			
			FileChannel out = fos.getChannel();

			
			out.write(myByteBuffer);

			
			out.close();
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return 0x0000;
	}

	private HashMap<Point, BuildingType> addCasFileOffset(HashMap<CastlePoint, BuildingType> clm) {
		HashMap<Point, BuildingType> output = new HashMap<Point, BuildingType>();
		
		for(CastlePoint cp : clm.keySet()){
			BuildingType bt = clm.get(cp);
			Point offset = bt.getCasFilePointOffset();
			Point p = new Point(cp.x + offset.x, cp.y + offset.y);
			output.put(p, bt);
		}
		return output;
	}

	/**
	 * Converting the gridData into something that can be put into an actual .cas file.
	 * 
	 * @param TileBuilding[][] gd
	 * @return TreeMap<CastlePoint, BuildingType> clm
	 */
	private HashMap<CastlePoint, BuildingType> convertGridData(TileBuilding[][] gd) {
		//HashMap<BuildingId, TreeSet<CastlePoint>> to filter same building IDs
		HashMap<Integer, TreeSet<CastlePoint>> structureMap = new HashMap<Integer, TreeSet<CastlePoint>>();
		for (int i = 0; i < gridData.length; i++)
			for (int j = 0; j < gridData[i].length; j++){
				//System.out.println("("+ i + "," + j + ")" + (gridData[i][j] != null ? gridData[i][j].getBuildingType().name() + " ID:" + gridData[i][j].getBuildingId() : "Empty spot"));
				if (gridData[i][j] != null)
					if (gridData[i][j].getBuildingType() != BuildingType.KEEP){
						
						if (!structureMap.containsKey(gridData[i][j].getBuildingId())){
							structureMap.put(gridData[i][j].getBuildingId(), new TreeSet<CastlePoint>());
							//System.out.println("("+ i + "," + j + ")" + (gridData[i][j] != null ? gridData[i][j].getBuildingType().name() + " ID:" + gridData[i][j].getBuildingId() : "Empty spot"));
						}
						structureMap.get(gridData[i][j].getBuildingId()).add(new CastlePoint(i, j));
					}
			}
		
		//take the top of each list so that we can get the bottom-right corner of each structure.
		HashMap<CastlePoint, BuildingType> clm = new HashMap<CastlePoint, BuildingType>();
		for (Integer i : structureMap.keySet()){
			//simply take the first value in each entry
			clm.put(structureMap.get(i).first(), getBuildingId(structureMap.get(i).first()));
		}
		return clm;
	}

	private BuildingType getBuildingId(CastlePoint point) {
		return gridData[point.x][point.y].getBuildingType();
	}

	public TileBuilding[][] getGridData() {
		return gridData;
	}
	public void setGridData(TileBuilding[][] gridData) {
		this.gridData = gridData;
	}
	public File[] getCasFiles() {
		return casFiles;
	}
	public void setCasFiles(File[] casFiles) {
		this.casFiles = casFiles;
	}

	//for the sake of getting the correct point in the TreeSet. Not good programming but it gets it done.
	class CastlePoint extends Point implements Comparable<CastlePoint>{
		public CastlePoint(int x, int y){
			super (x, y);
		}
		
		public int compareTo(CastlePoint other) {
			if (other.x + other.y < this.x + this.y){
				return 1;
			}
			else if (other.x + other.y > this.x + this.y){
				return -1;
			}
			return 0;
		}
	}
}
