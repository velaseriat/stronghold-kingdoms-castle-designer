package castledesigner;

import java.awt.Point;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class CasFileWriter {

	TileBuilding[][] gridData;
	File[] casFiles;

	private HashMap<CastlePoint, BuildingType> castleLayoutMap;
	private HashMap<Point, BuildingType> preWriteMap;

	public void writeToCasFiles(){
		castleLayoutMap = convertGridData(gridData);
		preWriteMap = addCasFileOffset(castleLayoutMap);
	}

	private HashMap<Point, BuildingType> addCasFileOffset(HashMap<CastlePoint, BuildingType> clm) {
		HashMap<Point, BuildingType> output = new HashMap<Point, BuildingType>();
		
		for(CastlePoint cp : clm.keySet()){
			BuildingType bt = clm.get(cp);
			Point offset = bt.getCasFilePointOffset();
			Point p = new Point(cp.x + offset.x, cp.y + offset.y);
			output.put(p, bt);
		}
		System.out.println(output);
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
