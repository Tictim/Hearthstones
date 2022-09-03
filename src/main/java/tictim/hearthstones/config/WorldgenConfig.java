package tictim.hearthstones.config;

import net.minecraftforge.common.config.Config;

public class WorldgenConfig{
	public boolean enable = true;
	public int minY;
	public int maxY;
	@Config.RangeInt(min = 0)
	public int oreSize;
	@Config.RangeInt(min = 0)
	public int countInChunk;

	public WorldgenConfig(int minY, int maxY, int oreSize, int countInChunk){
		this.minY = minY;
		this.maxY = maxY;
		this.oreSize = oreSize;
		this.countInChunk = countInChunk;
	}
}
