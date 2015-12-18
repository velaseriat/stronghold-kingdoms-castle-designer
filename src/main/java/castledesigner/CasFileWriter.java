package castledesigner;

import java.awt.Point;
import java.io.File;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;

public class CasFileWriter {

	TileBuilding[][] gridData;
	File[] casFiles;

	TreeMap<CastlePoint, BuildingType> castleLayoutMap;

	public void writeToCasFiles(){
		castleLayoutMap = convertGridData(gridData);
	}

	/**
	 * Converting the gridData into something that can be put into an actual .cas file.
	 * 
	 * @param TileBuilding[][] gd
	 * @return TreeMap<CastlePoint, BuildingType> clm
	 */
	private TreeMap<CastlePoint, BuildingType> convertGridData(TileBuilding[][] gd) {
		//HashMap<BuildingId, TreeSet<CastlePoint>> to filter same building IDs
		HashMap<Integer, TreeSet<CastlePoint>> structureMap = new HashMap<Integer, TreeSet<CastlePoint>>();
		for (int i = 0; i < gridData.length; i++)
			for (int j = 0; j < gridData[i].length; j++){
				//System.out.println("("+ i + "," + j + ")" + (gridData[i][j] != null ? gridData[i][j].getBuildingType().name() + " ID:" + gridData[i][j].getBuildingId() : "Empty spot"));
				if (gridData[i][j] != null)
					if (!structureMap.containsKey(gridData[i][j].getBuildingId()))
						structureMap.put(gridData[i][j].getBuildingId(), new TreeSet<CastlePoint>());
					else
						structureMap.get(gridData[i][j].getBuildingId()).add(new CastlePoint(i, j));
			}
		TreeMap<CastlePoint, BuildingType> clm = new TreeMap<CastlePoint, BuildingType>();
		for (int i : structureMap.keySet()){
			//simply take the first value in each entry
			clm.put(structureMap.get(i).first(), getBuildingId(structureMap.get(i).first()));
		}

		return clm;
	}

	private BuildingType getBuildingId(CastlePoint point) {
		return gridData[(int) point.getX()][(int) point.getY()].getBuildingType();
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
	class CastlePoint extends Point implements Comparable{
		public CastlePoint(int x, int y){
			super (x, y);
		}

		public int compareTo(Object other) {
			if (other instanceof Point){
				if (((Point) other).getX() + ((Point) other).getY() < this.getX() + this.getY()){
					return 1;
				}
				else if (((Point) other).getX() + ((Point) other).getY() > this.getX() + this.getY()){
					return -1;
				}
			}
			return 0;
		}
	}
}
