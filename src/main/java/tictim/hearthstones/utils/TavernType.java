package tictim.hearthstones.utils;

import it.unimi.dsi.fastutil.bytes.Byte2ObjectArrayMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import tictim.hearthstones.contents.ModItems;

import java.util.Objects;
import java.util.function.Supplier;

import static tictim.hearthstones.Hearthstones.MODID;

public final class TavernType{
	private static final Byte2ObjectArrayMap<TavernType> types = new Byte2ObjectArrayMap<>();

	public static TavernType of(int id){
		return of((byte)id);
	}
	public static TavernType of(byte id){
		TavernType type = types.get(id);
		return type==null ? NORMAL : type;
	}

	public static final TavernType NORMAL = new TavernType(0, "normal", new ResourceLocation(MODID, "textures/screen/tavern/normal.png"), () -> new ItemStack(ModItems.TAVERN.get()));
	public static final TavernType SHABBY = new TavernType(1, "shabby", new ResourceLocation(MODID, "textures/screen/tavern/shabby.png"), () -> new ItemStack(ModItems.SHABBY_TAVERN.get()));
	public static final TavernType GLOBAL = new TavernType(2, "global", new ResourceLocation(MODID, "textures/screen/tavern/guild.png"), () -> new ItemStack(ModItems.GLOBAL_TAVERN.get()));

	public final byte id;
	public final String name;
	public final ResourceLocation tavernUITexture;
	private final Supplier<ItemStack> renderStackSupplier;

	public TavernType(int id, String name, ResourceLocation tavernUITexture, Supplier<ItemStack> renderStackSupplier){
		byte bid = (byte)id;
		if(types.containsKey(bid)) throw new IllegalStateException("TavernType with id "+id+" already exists.");
		else types.put(bid, this);
		this.id = bid;
		this.name = name;
		this.tavernUITexture = Objects.requireNonNull(tavernUITexture);
		this.renderStackSupplier = Objects.requireNonNull(renderStackSupplier);
	}

	private ItemStack stackForRender;

	public ItemStack stackForRender(){
		if(stackForRender==null) stackForRender = renderStackSupplier.get();
		return stackForRender;
	}

	public void resetRenderStack(){
		stackForRender = null;
	}

	/*
	public TETavern createTileEntity(){
		switch(this){
		case SHABBY: return new TETavernShabby();
		case GLOBAL: return new TETavernGlobal();
		case NORMAL: return new TETavern();
		default: throw new IllegalStateException("Fix this shithead");
		}
	}

	public Rarity getRarity(){
		return this==TavernType.GLOBAL ? Rarity.UNCOMMON : Rarity.COMMON;
	}

	public Block getTavernBlock(){
		switch(this){
		case NORMAL: return ModBlocks.TAVERN.get();
		case SHABBY: return ModBlocks.SHABBY_TAVERN.get();
		case GLOBAL: return ModBlocks.GLOBAL_TAVERN.get();
		default: throw new IllegalStateException("Fix this shithead");
		}
	}

	public Item getTavernBlockItem(){
		switch(this){
		case NORMAL: return ModItems.TAVERN.get();
		case SHABBY: return ModItems.SHABBY_TAVERN.get();
		case GLOBAL: return ModItems.GLOBAL_TAVERN.get();
		default: throw new IllegalStateException("Fix this shithead");
		}
	}

	public Item getTavernclothItem(){
		switch(this){
		case NORMAL: return ModItems.TAVERNCLOTH.get();
		case SHABBY: return ModItems.TATTERED_TAVERNCLOTH.get();
		case GLOBAL: return ModItems.BLUE_TAVERNCLOTH.get();
		default: throw new IllegalStateException("Fix this shithead");
		}
	}
*/
}
