package tictim.hearthstones.tavern;

import net.minecraft.item.EnumRarity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import static tictim.hearthstones.Hearthstones.MODID;

public enum TavernType implements IStringSerializable{
	NORMAL("normal", "tile.hearthstones.tavern", new ResourceLocation(MODID, "textures/screen/tavern/normal.png"), TextFormatting.RED+"Normal"),
	SHABBY("shabby", "tile.hearthstones.shabby_tavern", new ResourceLocation(MODID, "textures/screen/tavern/shabby.png"), TextFormatting.GRAY+"Shabby"),
	GLOBAL("global", "tile.hearthstones.global_tavern", new ResourceLocation(MODID, "textures/screen/tavern/global.png"), TextFormatting.BLUE+"Global");

	private final String name;
	private final String blockNameTranslationKey;
	public final ResourceLocation guiTexture;
	public final String commandAppearance;

	TavernType(String name, String blockNameTranslationKey, ResourceLocation guiTexture, String commandAppearance){
		this.name = name;
		this.blockNameTranslationKey = blockNameTranslationKey;
		this.guiTexture = guiTexture;
		this.commandAppearance = commandAppearance;
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
