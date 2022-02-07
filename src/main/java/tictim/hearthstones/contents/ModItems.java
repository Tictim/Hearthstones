package tictim.hearthstones.contents;

import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import tictim.hearthstones.config.ModCfg;
import tictim.hearthstones.contents.item.MortarItem;
import tictim.hearthstones.contents.item.hearthstone.CompanionHearthstoneItem;
import tictim.hearthstones.contents.item.hearthstone.HearthingGemItem;
import tictim.hearthstones.contents.item.hearthstone.HearthingPlanksItem;
import tictim.hearthstones.contents.item.hearthstone.NormalHearthstoneItem;
import tictim.hearthstones.contents.item.TavernWaypointBinderItem;
import tictim.hearthstones.contents.item.TavernWaypointItem;
import tictim.hearthstones.contents.item.tavernupgrade.GuildTavernUpgradeItem;
import tictim.hearthstones.contents.item.tavernupgrade.RegularTavernUpgradeItem;
import tictim.hearthstones.contents.item.tavernupgrade.ShabbyTavernUpgradeItem;

import javax.annotation.Nullable;
import java.util.List;

import static tictim.hearthstones.Hearthstones.MODID;

public final class ModItems{
	private ModItems(){}

	public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

	public static final CreativeModeTab TAB = new CreativeModeTab("hearthstones"){
		@Override public ItemStack makeIcon(){
			return new ItemStack(ModItems.HEARTHSTONE.get());
		}
	}.setEnchantmentCategories(ModEnchantments.HEARTHSTONE);

	public static final RegistryObject<Item> HEARTHSTONE = REGISTER.register("hearthstone", () -> new NormalHearthstoneItem(p(Rarity.RARE).stacksTo(1)));
	public static final RegistryObject<Item> HEARTHING_PLANKS = REGISTER.register("hearthing_planks", () -> new HearthingPlanksItem(p().stacksTo(1)));
	public static final RegistryObject<Item> HEARTHING_GEM = REGISTER.register("hearthing_gem", () -> new HearthingGemItem(p(Rarity.UNCOMMON).stacksTo(1)));
	public static final RegistryObject<Item> COMPANION_HEARTHSTONE = REGISTER.register("companion_hearthstone", () -> new CompanionHearthstoneItem(p(Rarity.EPIC).stacksTo(1)));

	public static final RegistryObject<Item> COMPANION_STONE = REGISTER.register("companion_stone", () -> new Item(p().stacksTo(1)){
		@Override public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag){
			tooltip.add(new TranslatableComponent("info.hearthstones.companion_stone.tooltip"));
		}
	});
	public static final RegistryObject<Item> MORTAR = REGISTER.register("mortar", () -> new MortarItem(p().durability(155).setNoRepair()));

	public static final RegistryObject<Item> TATTERED_TAVERNCLOTH = REGISTER.register("tattered_taverncloth", () -> new ShabbyTavernUpgradeItem(p()));
	public static final RegistryObject<Item> TAVERNCLOTH = REGISTER.register("taverncloth", () -> new RegularTavernUpgradeItem(p()));
	public static final RegistryObject<Item> BLUE_TAVERNCLOTH = REGISTER.register("blue_taverncloth", () -> new GuildTavernUpgradeItem(p(Rarity.UNCOMMON)));

	public static final RegistryObject<Item> WAYPOINT = REGISTER.register("waypoint", () -> new TavernWaypointItem(p(Rarity.UNCOMMON)));
	public static final RegistryObject<Item> WAYPOINT_BINDER = REGISTER.register("waypoint_binder", () -> new TavernWaypointBinderItem(p(Rarity.UNCOMMON).stacksTo(1)));

	public static final RegistryObject<Item> AQUAMARINE = REGISTER.register("aquamarine", () -> new Item(p()));
	public static final RegistryObject<Item> RED_LEATHER = REGISTER.register("red_leather", () -> new Item(p()));
	public static final RegistryObject<Item> TATTERED_LEATHER = REGISTER.register("tattered_leather", () -> new Item(p()){
		@Override public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items){
			if(this.allowdedIn(group)&&!ModCfg.easyMode()) items.add(new ItemStack(this));
		}
	});
	public static final RegistryObject<Item> BLUE_LEATHER = REGISTER.register("blue_leather", () -> new Item(p(Rarity.UNCOMMON)));
	public static final RegistryObject<Item> DEEP_BLUE = REGISTER.register("deep_blue", () -> new Item(p(Rarity.UNCOMMON)));
	public static final RegistryObject<Item> DEEP_PURPLE = REGISTER.register("deep_purple", () -> new Item(p(Rarity.UNCOMMON)));
	public static final RegistryObject<Item> AQUAMARINE_DUST = REGISTER.register("aquamarine_dust", () -> new Item(p()));
	public static final RegistryObject<Item> DIAMOND_DUST = REGISTER.register("diamond_dust", () -> new Item(p()));
	public static final RegistryObject<Item> LAPIS_DUST = REGISTER.register("lapis_dust", () -> new Item(p()));
	public static final RegistryObject<Item> AMETHYST_DUST = REGISTER.register("amethyst_dust", () -> new Item(p()));

	public static final RegistryObject<Item> AQUAMARINE_ORE = REGISTER.register("aquamarine_ore", () -> new BlockItem(ModBlocks.AQUAMARINE_ORE.get(), p()));
	public static final RegistryObject<Item> DEEPSLATE_AQUAMARINE_ORE = REGISTER.register("deepslate_aquamarine_ore", () -> new BlockItem(ModBlocks.DEEPSLATE_AQUAMARINE_ORE.get(), p()));
	public static final RegistryObject<Item> AQUAMARINE_BLOCK = REGISTER.register("aquamarine_block", () -> new BlockItem(ModBlocks.AQUAMARINE_BLOCK.get(), p()));
	public static final RegistryObject<Item> TAVERN = REGISTER.register("tavern", () -> new BlockItem(ModBlocks.TAVERN.get(), p()));
	public static final RegistryObject<Item> SHABBY_TAVERN = REGISTER.register("shabby_tavern", () -> new BlockItem(ModBlocks.SHABBY_TAVERN.get(), p()));
	public static final RegistryObject<Item> GLOBAL_TAVERN = REGISTER.register("global_tavern", () -> new BlockItem(ModBlocks.GLOBAL_TAVERN.get(), p(Rarity.UNCOMMON)));

	private static Item.Properties p(){
		return new Item.Properties().tab(TAB);
	}
	private static Item.Properties p(Rarity rarity){
		return p().rarity(rarity);
	}
}