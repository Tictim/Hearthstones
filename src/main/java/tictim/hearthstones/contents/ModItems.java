package tictim.hearthstones.contents;

import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import tictim.hearthstones.config.ModCfg;
import tictim.hearthstones.contents.item.MortarItem;
import tictim.hearthstones.contents.item.RareItem;
import tictim.hearthstones.contents.item.TavernBinderItem;
import tictim.hearthstones.contents.item.TavernItem;
import tictim.hearthstones.contents.item.TavernWaypointItem;
import tictim.hearthstones.contents.item.hearthstone.CompanionHearthstoneItem;
import tictim.hearthstones.contents.item.hearthstone.HearthingGemItem;
import tictim.hearthstones.contents.item.hearthstone.HearthingPlanksItem;
import tictim.hearthstones.contents.item.hearthstone.NormalHearthstoneItem;
import tictim.hearthstones.contents.item.tavernupgrade.GuildTavernUpgradeItem;
import tictim.hearthstones.contents.item.tavernupgrade.RegularTavernUpgradeItem;
import tictim.hearthstones.contents.item.tavernupgrade.ShabbyTavernUpgradeItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

import static tictim.hearthstones.Hearthstones.MODID;

@Mod.EventBusSubscriber(modid = MODID)
@GameRegistry.ObjectHolder(MODID)
public class ModItems{
	@SuppressWarnings("ConstantConditions") @Nonnull private static <T> T definitelyNotNull(){
		return null;
	}

	public static final Item HEARTHSTONE = definitelyNotNull();
	public static final Item SHABBY_HEARTHSTONE = definitelyNotNull();
	public static final Item HEARTHING_GEM = definitelyNotNull();
	public static final Item COMPANION_HEARTHSTONE = definitelyNotNull();

	public static final Item COMPANION_STONE = definitelyNotNull();

	public static final Item MORTAR = definitelyNotNull();

	public static final Item TATTERED_TAVERNCLOTH = definitelyNotNull();
	public static final Item TAVERNCLOTH = definitelyNotNull();
	public static final Item BLUE_TAVERNCLOTH = definitelyNotNull();

	public static final Item WAYPOINT = definitelyNotNull();
	public static final Item WAYPOINT_BINDER = definitelyNotNull();
	public static final Item INFINITE_WAYPOINT_BINDER = definitelyNotNull();

	public static final Item AQUAMARINE = definitelyNotNull();
	public static final Item RED_LEATHER = definitelyNotNull();
	public static final Item TATTERED_LEATHER = definitelyNotNull();
	public static final Item BLUE_LEATHER = definitelyNotNull();
	public static final Item PURPLE_LEATHER = definitelyNotNull();
	public static final Item DEEP_BLUE = definitelyNotNull();
	public static final Item DEEP_PURPLE = definitelyNotNull();
	public static final Item AQUAMARINE_DUST = definitelyNotNull();
	public static final Item DIAMOND_DUST = definitelyNotNull();
	public static final Item LAPIS_DUST = definitelyNotNull();
	public static final Item AMETHYST_SHARD = definitelyNotNull();
	public static final Item AMETHYST_DUST = definitelyNotNull();

	public static final Item AQUAMARINE_ORE = definitelyNotNull();
	public static final Item AQUAMARINE_BLOCK = definitelyNotNull();

	public static final Item AMETHYST_BLOCK = definitelyNotNull();
	public static final Item BUDDING_AMETHYST = definitelyNotNull();
	public static final Item AMETHYST_CLUSTER = definitelyNotNull();
	public static final Item SMALL_AMETHYST_BUD = definitelyNotNull();
	public static final Item MEDIUM_AMETHYST_BUD = definitelyNotNull();
	public static final Item LARGE_AMETHYST_BUD = definitelyNotNull();

	public static final Item TAVERN = definitelyNotNull();
	public static final Item BINDER_LECTERN = definitelyNotNull();

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event){
		IForgeRegistry<Item> registry = event.getRegistry();
		register(registry, "hearthstone", new NormalHearthstoneItem().setRarity(EnumRarity.RARE));
		register(registry, "shabby_hearthstone", new HearthingPlanksItem());
		register(registry, "hearthing_gem", new HearthingGemItem().setRarity(EnumRarity.UNCOMMON));
		register(registry, "companion_hearthstone", new CompanionHearthstoneItem().setRarity(EnumRarity.EPIC));

		register(registry, "companion_stone", new Item(){
			@Override public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn){
				tooltip.add(I18n.format("info.hearthstones.companion_stone.tooltip"));
			}
		}.setMaxStackSize(1));

		register(registry, "mortar", new MortarItem());

		register(registry, "tattered_taverncloth", new ShabbyTavernUpgradeItem());
		register(registry, "taverncloth", new RegularTavernUpgradeItem());
		register(registry, "blue_taverncloth", new GuildTavernUpgradeItem().setRarity(EnumRarity.UNCOMMON));

		register(registry, "waypoint", new TavernWaypointItem().setRarity(EnumRarity.UNCOMMON));
		register(registry, "waypoint_binder", new TavernBinderItem(false).setRarity(EnumRarity.UNCOMMON));
		register(registry, "infinite_waypoint_binder", new TavernBinderItem(true).setRarity(EnumRarity.EPIC));

		register(registry, "aquamarine", new Item());
		register(registry, "red_leather", new Item());
		register(registry, "tattered_leather", new Item(){
			@Override public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items){
				if(this.isInCreativeTab(tab)&&!ModCfg.easyMode) items.add(new ItemStack(this));
			}
		});
		register(registry, "blue_leather", new RareItem().setRarity(EnumRarity.UNCOMMON));
		register(registry, "purple_leather", new RareItem().setRarity(EnumRarity.UNCOMMON));
		register(registry, "deep_blue", new RareItem().setRarity(EnumRarity.UNCOMMON));
		register(registry, "deep_purple", new RareItem().setRarity(EnumRarity.UNCOMMON));
		register(registry, "aquamarine_dust", new Item());
		register(registry, "diamond_dust", new Item());
		register(registry, "lapis_dust", new Item());
		register(registry, "amethyst_shard", new Item());
		register(registry, "amethyst_dust", new Item());

		registerBlockItem(registry, ModBlocks.AQUAMARINE_ORE);
		registerBlockItem(registry, ModBlocks.AQUAMARINE_BLOCK);

		registerBlockItem(registry, ModBlocks.AMETHYST_BLOCK);
		registerBlockItem(registry, ModBlocks.BUDDING_AMETHYST);
		registerBlockItem(registry, ModBlocks.AMETHYST_CLUSTER);
		registerBlockItem(registry, ModBlocks.SMALL_AMETHYST_BUD);
		registerBlockItem(registry, ModBlocks.MEDIUM_AMETHYST_BUD);
		registerBlockItem(registry, ModBlocks.LARGE_AMETHYST_BUD);

		register(registry, "tavern", new TavernItem(ModBlocks.TAVERN));
		registerBlockItem(registry, ModBlocks.BINDER_LECTERN);
	}

	private static void registerBlockItem(IForgeRegistry<Item> registry, Block block){
		registry.register(new ItemBlock(block)
				.setRegistryName(Objects.requireNonNull(block.getRegistryName())));
	}

	private static void register(IForgeRegistry<Item> registry, String name, Item item){
		registry.register(item.setCreativeTab(HearthstoneTab.get())
				.setRegistryName(name)
				.setTranslationKey(MODID+"."+name));
	}
}
