/*
 * Copyright (c) 2012 David Green
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package castledesigner;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

/**
 * Enumerations representing the different types of buildings we use. This is looking very busy now. At some point we should create a file to store the data in
 * here instead of embedded within this class.
 *
 * @author David Green
 */
public enum BuildingType
{
	// @formatter:off
	//Wood
	WOODEN_WALL(		new Color(150, 75, 0), 		new Dimension(1, 1), false, 	new Integer[] {0, 20, 0, 0},	225,	(short)0x0021,	new Point(0, 0)),
	WOODEN_GATEHOUSE(	new Color(100, 50, 0), 		new Dimension(3, 3), true, 		new Integer[] {0, 200, 0, 0}, 	3600,	(short)0x0027,	new Point(1, 1)),
	WOODEN_TOWER(		new Color(125, 58, 0), 		new Dimension(2, 2), false, 	new Integer[] {0, 200, 0, 0}, 	10800,	(short)0x0015,	new Point(0, 1)),
	
	//Stone
	STONE_WALL(			new Color(230, 230, 230), 	new Dimension(1, 1), false, 	new Integer[] {100, 0, 0, 0}, 	900,	(short)0x0022,	new Point(0, 0)),
	STONE_GATEHOUSE(	new Color(100, 100, 100), 	new Dimension(3, 3), true, 		new Integer[] {500, 0, 0, 0}, 	7200,	(short)0x0025,	new Point(1, 1)),
	LOOKOUT_TOWER(		new Color(200, 200, 200), 	new Dimension(2, 2), true, 		new Integer[] {300, 0, 0, 0}, 	14400,	(short)0x000b,	new Point(0, 1)),
	SMALL_TOWER(		new Color(200, 200, 200), 	new Dimension(3, 3), true, 		new Integer[] {800, 0, 0, 0}, 	28800,	(short)0x000c,	new Point(1, 1)),
	LARGE_TOWER(		new Color(200, 200, 200), 	new Dimension(4, 4), true, 		new Integer[] {1500, 0, 0, 0}, 	57600,	(short)0x000d,	new Point(1, 2)),
	GREAT_TOWER(		new Color(200, 200, 200), 	new Dimension(5, 5), true, 		new Integer[] {2500, 0, 0, 0}, 	86400,	(short)0x000e,	new Point(2, 2)),

	//Misc
	GUARD_HOUSE(		new Color(255, 200, 180), 	new Dimension(3, 3), false, 	new Integer[] {0, 400, 0, 0}, 	10800,	(short)0x001f,	new Point(1, 1)),
	BALLISTA_TOWER(		new Color(230, 200, 60), 	new Dimension(3, 3), false, 	new Integer[] {0, 10000, 0, 0}, 18000,	(short)0x002a,	new Point(1, 1)),
	TURRET(				new Color(0, 0, 80), 		new Dimension(2, 2), false, 	new Integer[] {2000, 0, 0, 0}, 	14400,	(short)0x0029,	new Point(0, 1)),
	SMELTER(			new Color(200, 30, 30), 	new Dimension(4, 4), false, 	new Integer[] {0, 0, 400, 0}, 	21600,	(short)0x0020,	new Point(1, 2)),
	MOAT(				new Color(0, 200, 255), 	new Dimension(1, 1), false, 	new Integer[] {0, 0, 0, 20}, 	900,	(short)0x0023,	new Point(0, 0)),
	
	//Keep
	KEEP(				new Color(0, 0, 0), 		new Dimension(8, 8), false, 	new Integer[] {0, 0, 0, 0}, 	0,		(short)0x0000,	null),
	KILLING_PIT(		new Color(120, 100, 0), 	new Dimension(1, 1), false, 	new Integer[] {0, 0, 100, 0}, 	3600,	(short)0x0024,	new Point(0, 0)),
	
	// additional building in age 4++
	BOMBARD(			new Color(49, 44, 33), 		new Dimension(3, 3), false, 	new Integer[] {0,0,1000, 0}, 	43200,	(short)0x002c,	new Point(1, 1));
	// @formatter:on
	
	private final 	Color 								colour;
	private 		Dimension 							dimension;
	private 		boolean 							gapRequired;
	private 		BufferedImage 						image;
	private 		BufferedImage 						validOverlay;
	private 		BufferedImage 						invalidOverlay;
	private 		Map<BuildingResource, Integer> 		buildingResources;
	private 		int 								buildTime;
	private			short								casFileCode;
	private			Point								casFileOffset;
	
	/**
	 * Constructor
	 * 
	 * @param colour
	 *            the colour represented on the grid and button
	 * @param dimension
	 *            the dimension of the building (in tiles)
	 * @param gapRequired
	 *            true if this building cannot be placed next to other buildings that also have this set to true
	 * @param resourceCosts
	 * 			  cost of resources
	 * @param buildTime
	 * 			  the buildtime of the structure
	 * @param offset
	 * 			  the point offset when writing data into .cas files. Unfortunately, it is not from the bottom-left corner, but from the center
	 */
	private BuildingType(
			Color 		colour,
			Dimension 	dimension,
			boolean 	gapRequired,
			Integer[] 	resourceCosts,
			int 		buildTime,
			short		casFileCode,
			Point 		casFileOffset)
	{
		this.colour = colour;
		this.dimension = dimension;
		this.gapRequired = gapRequired;
		
		String urlPath = "/buildings/" + name() + ".png";
		URL url = getClass().getResource(urlPath.toLowerCase());
		
		if (url != null)
		{
			try
			{
				image = ImageIO.read(url);
				validOverlay = createOverlay(true);
				invalidOverlay = createOverlay(false);
			} catch (IOException ex)
			{
				Logger.getLogger(BuildingType.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		
		buildingResources = new HashMap<BuildingResource, Integer>();
		buildingResources.put(BuildingResource.STONE, resourceCosts[0]);
		buildingResources.put(BuildingResource.WOOD, resourceCosts[1]);
		buildingResources.put(BuildingResource.IRON, resourceCosts[2]);
		buildingResources.put(BuildingResource.GOLD, resourceCosts[3]);
		
		this.buildTime = buildTime;
		this.casFileCode = casFileCode;
		this.casFileOffset = casFileOffset;
	}
	
	private BufferedImage createOverlay(boolean valid)
	{
		BufferedImage overlay = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TRANSLUCENT);
		Graphics2D g = overlay.createGraphics();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
		g.drawImage(image, 0, 0, null);
		g.dispose();
		
		if (valid == false)
		{
			for (int i = 0; i < overlay.getWidth(); i++)
			{
				for (int j = 0; j < overlay.getHeight(); j++)
				{
					int rgb = overlay.getRGB(i, j) & 0xFFFF4444 | 0xFF0000;
					overlay.setRGB(i, j, rgb);
				}
			}
		}
		return overlay;
	}
	
	
	public int[] getHotspot()
	{
		return new int[]
		{ (dimension.width - 1) / 2, (dimension.height - 1) / 2 };
	}
	
	//Convenience methods for .cas file output
	public short getCaseFileCode(){
		return this.casFileCode;
	}
	public Point getCasFilePointOffset(){
		return this.casFileOffset;
	}
	
	public int xOffset(){
		return this.casFileOffset.x;
	}
	
	public int yOffset(){
		return this.casFileOffset.y;
	}
	
	/**
	 * @Deprecated Images should be used instead of colours @see #getImage()
	 * 			
	 *             The colour represented by the building
	 * 			
	 * @return the colour represented by the building
	 */
	public Color getColour()
	{
		return colour;
	}
	
	/**
	 * The dimension of the building (in tiles).
	 *
	 * @return the dimension of the building (in tiles)
	 */
	public Dimension getDimension()
	{
		return dimension;
	}
	
	/**
	 * Returns true if this building cannot be placed next to other buildings that also have this value set to true.
	 * 
	 * @return true if required, false otherwise
	 */
	public boolean isGapRequired()
	{
		return gapRequired;
	}
	
	/**
	 * Returns the amount of specified resource needed to construct this building.
	 *
	 * @param buildingResource
	 *            the resource wanted
	 * @return the number of resources needed for the whole building
	 */
	public int getCost(BuildingResource buildingResource)
	{
		return buildingResources.get(buildingResource);
	}
	
	/**
	 * Returns the time to construct this building (in seconds). No bonuses from research is assumed.
	 *
	 * @return the number of seconds to construct this building.
	 */
	public int getBuildTime()
	{
		return buildTime;
	}
	
	/**
	 * Returns a visual image of this building type. Note that null may be returned.
	 *
	 * @return An Image representing this building type
	 */
	public Image getImage()
	{
		return image;
	}
	
	/**
	 * Returns a visual translucent image of this building type. Note that null may be returned.
	 * 
	 * @return A translucent BufferedImage representing this building type
	 */
	public BufferedImage getValidOverlay()
	{
		return validOverlay;
	}
	
	/**
	 * Returns a visual translucent red-tint image of this building type. Note that null may be returned.
	 * 
	 * @return A translucent red-tinted BufferedImage representing this building type
	 */
	public BufferedImage getInvalidOverlay()
	{
		return invalidOverlay;
	}
	
	@Override
	public String toString()
	{
		String[] words = name().toLowerCase().split("_");
		
		StringBuffer name = new StringBuffer();
		for (String word : words)
		{
			if (name.length() > 0)
				name.append(' ');
				
			name.append(Character.toUpperCase(word.charAt(0)));
			if (word.length() > 1)
				name.append(word.substring(1));
		}
		
		return name.toString();
	}
}