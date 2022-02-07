package tictim.hearthstones.tavern;

import net.minecraft.item.EnumRarity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;

import static tictim.hearthstones.Hearthstones.MODID;

public enum TavernType implements IStringSerializable{
	NORMAL("normal", "tile.hearthstones.tavern", new ResourceLocation(MODID, "textures/screen/tavern/normal.png")),
	SHABBY("shabby", "tile.hearthstones.shabby_tavern", new ResourceLocation(MODID, "textures/screen/tavern/shabby.png")),
	GLOBAL("global", "tile.hearthstones.global_tavern", new ResourceLocation(MODID, "textures/screen/tavern/global.png"));

	private final String name;
	private final String blockNameTranslationKey;
	public final ResourceLocation guiTexture;

	TavernType(String name, String blockNameTranslationKey, ResourceLocation guiTexture){
		this.name = name;
		this.blockNameTranslationKey = blockNameTranslationKey;
		this.guiTexture = guiTexture;
	}

	@Override public String getName(){
		return name;
	}
	public String getBlockNameTranslationKey(){
		return blockNameTranslationKey;
	}
	public EnumRarity getRarity(){
		return this==TavernType.GLOBAL ? EnumRarity.UNCOMMON : EnumRarity.COMMON;
	}

	public static TavernType of(int meta){
		TavernType[] values = TavernType.values();
		return values[meta<0||meta>=values.length ? 0 : meta];
	}
}
