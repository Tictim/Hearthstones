package tictim.hearthstones.contents.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class SoundBlock extends Block{
	public SoundBlock(Material material, MapColor mapColor, SoundType soundType){
		super(material, mapColor);
		this.setSoundType(soundType);
	}
	public SoundBlock(Material material, SoundType soundType){
		super(material);
		this.setSoundType(soundType);
	}
}
