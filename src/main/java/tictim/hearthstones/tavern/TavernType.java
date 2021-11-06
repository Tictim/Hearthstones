package tictim.hearthstones.tavern;

import it.unimi.dsi.fastutil.bytes.Byte2ObjectArrayMap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
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

	public static final TavernType NORMAL = new TavernType(0, "normal", new ResourceLocation(MODID, "textures/screen/tavern/normal.png"), () -> new ItemStack(ModItems.TAVERN.get()), new TextComponent("Normal").withStyle(ChatFormatting.RED));
	public static final TavernType SHABBY = new TavernType(1, "shabby", new ResourceLocation(MODID, "textures/screen/tavern/shabby.png"), () -> new ItemStack(ModItems.SHABBY_TAVERN.get()), new TextComponent("Shabby").withStyle(ChatFormatting.GRAY));
	public static final TavernType GLOBAL = new TavernType(2, "global", new ResourceLocation(MODID, "textures/screen/tavern/guild.png"), () -> new ItemStack(ModItems.GLOBAL_TAVERN.get()), new TextComponent("Global").withStyle(ChatFormatting.BLUE));

	public final byte id;
	public final String name;
	public final ResourceLocation tavernUITexture;
	private final Supplier<ItemStack> renderStackSupplier;
	public final Component commandAppearance;

	public TavernType(int id, String name, ResourceLocation tavernUITexture, Supplier<ItemStack> renderStackSupplier, Component commandAppearance){
		byte bid = (byte)id;
		if(types.containsKey(bid)) throw new IllegalStateException("TavernType with id "+id+" already exists.");
		else types.put(bid, this);
		this.id = bid;
		this.name = name;
		this.tavernUITexture = Objects.requireNonNull(tavernUITexture);
		this.renderStackSupplier = Objects.requireNonNull(renderStackSupplier);
		this.commandAppearance = commandAppearance;
	}

	private ItemStack stackForRender;

	public ItemStack stackForRender(){
		if(stackForRender==null) stackForRender = renderStackSupplier.get();
		return stackForRender;
	}

	public void resetRenderStack(){
		stackForRender = null;
	}
}
