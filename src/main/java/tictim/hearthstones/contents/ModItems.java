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
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import tictim.hearthstones.config.ModCfg;
import tictim.hearthstones.contents.item.CompanionHearthstoneItem;
import tictim.hearthstones.contents.item.GuidTavernUpgradeItem;
import tictim.hearthstones.contents.item.HearthingGemItem;
import tictim.hearthstones.contents.item.HearthingPlanksItem;
import tictim.hearthstones.contents.item.MortarItem;
import tictim.hearthstones.contents.item.RegularHearthstoneItem;
import tictim.hearthstones.contents.item.RegularTavernUpgradeItem;
import tictim.hearthstones.contents.item.ShabbyTavernUpgradeItem;

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

	public static final RegistryObject<Item> HEARTHSTONE = REGISTER.register("hearthstone",
			() -> new RegularHearthstoneItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE).tab(TAB))
	);
	public static final RegistryObject<Item> HEARTHING_PLANKS = REGISTER.register("hearthing_planks",
			() -> new HearthingPlanksItem(new Item.Properties().stacksTo(1).tab(TAB))
	);
	public static final RegistryObject<Item> HEARTHING_GEM = REGISTER.register("hearthing_gem",
			() -> new HearthingGemItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).tab(TAB))
	);
	public static final RegistryObject<Item> COMPANION_HEARTHSTONE = REGISTER.register("companion_hearthstone",
			() -> new CompanionHearthstoneItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).tab(TAB))
	);
	public static final RegistryObject<Item> COMPANION_STONE = REGISTER.register("companion_stone",
			() -> new Item(new Item.Properties().stacksTo(1).tab(TAB)){
				@Override public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn){
					tooltip.add(new TranslatableComponent("info.hearthstones.companion_stone.tooltip"));
				}
			}
	);
	public static final RegistryObject<Item> MORTAR = REGISTER.register("mortar",
			() -> new MortarItem(new Item.Properties().durability(155).tab(TAB).setNoRepair())
	);

	public static final RegistryObject<Item> TATTERED_TAVERNCLOTH = REGISTER.register("tattered_taverncloth",
			() -> new ShabbyTavernUpgradeItem(new Item.Properties().tab(TAB))
	);
	public static final RegistryObject<Item> TAVERNCLOTH = REGISTER.register("taverncloth",
			() -> new RegularTavernUpgradeItem(new Item.Properties().tab(TAB))
	);
	public static final RegistryObject<Item> BLUE_TAVERNCLOTH = REGISTER.register("blue_taverncloth",
			() -> new GuidTavernUpgradeItem(new Item.Properties().rarity(Rarity.UNCOMMON).tab(TAB))
	);

	public static final RegistryObject<Item> AQUAMARINE = REGISTER.register("aquamarine",
			() -> new Item(new Item.Properties().tab(TAB))
	);
	public static final RegistryObject<Item> RED_LEATHER = REGISTER.register("red_leather",
			() -> new Item(new Item.Properties().tab(TAB))
	);
	public static final RegistryObject<Item> TATTERED_LEATHER = REGISTER.register("tattered_leather",
			() -> new Item(new Item.Properties().tab(TAB)){
				@Override public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items){
					if(this.allowdedIn(group)&&!ModCfg.easyMode()) items.add(new ItemStack(this));
				}
			}
	);
	public static final RegistryObject<Item> BLUE_LEATHER = REGISTER.register("blue_leather",
			() -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON).tab(TAB))
	);
	public static final RegistryObject<Item> DEEP_BLUE = REGISTER.register("deep_blue",
			() -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON).tab(TAB))
	);
	public static final RegistryObject<Item> AQUAMARINE_DUST = REGISTER.register("aquamarine_dust",
			() -> new Item(new Item.Properties().tab(TAB))
	);
	public static final RegistryObject<Item> DIAMOND_DUST = REGISTER.register("diamond_dust",
			() -> new Item(new Item.Properties().tab(TAB))
	);
	public static final RegistryObject<Item> LAPIS_DUST = REGISTER.register("lapis_dust",
			() -> new Item(new Item.Properties().tab(TAB))
	);

	public static final RegistryObject<Item> AQUAMARINE_ORE = REGISTER.register("aquamarine_ore",
			() -> new BlockItem(ModBlocks.AQUAMARINE_ORE.get(), new Item.Properties().tab(TAB))
	);
	public static final RegistryObject<Item> AQUAMARINE_BLOCK = REGISTER.register("aquamarine_block",
			() -> new BlockItem(ModBlocks.AQUAMARINE_BLOCK.get(), new Item.Properties().tab(TAB))
	);
	public static final RegistryObject<Item> TAVERN = REGISTER.register("tavern",
			() -> new BlockItem(ModBlocks.TAVERN.get(), new Item.Properties().tab(TAB))
	);
	public static final RegistryObject<Item> SHABBY_TAVERN = REGISTER.register("shabby_tavern",
			() -> new BlockItem(ModBlocks.SHABBY_TAVERN.get(), new Item.Properties().tab(TAB))
	);
	public static final RegistryObject<Item> GLOBAL_TAVERN = REGISTER.register("global_tavern",
			() -> new BlockItem(ModBlocks.GLOBAL_TAVERN.get(), new Item.Properties().rarity(Rarity.UNCOMMON).tab(TAB))
	);
}
