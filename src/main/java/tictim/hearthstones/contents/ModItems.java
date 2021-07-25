package tictim.hearthstones.contents;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import tictim.hearthstones.config.ModCfg;
import tictim.hearthstones.item.CompanionHearthstoneItem;
import tictim.hearthstones.item.GuidTavernUpgradeItem;
import tictim.hearthstones.item.HearthingGemItem;
import tictim.hearthstones.item.HearthingPlanksItem;
import tictim.hearthstones.item.MortarItem;
import tictim.hearthstones.item.RegularHearthstoneItem;
import tictim.hearthstones.item.RegularTavernUpgradeItem;
import tictim.hearthstones.item.ShabbyTavernUpgradeItem;

import javax.annotation.Nullable;
import java.util.List;

import static tictim.hearthstones.Hearthstones.MODID;

public final class ModItems{
	private ModItems(){}

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

	public static final ItemGroup TAB = new ItemGroup("hearthstones"){
		@Override public ItemStack makeIcon(){
			return new ItemStack(ModItems.HEARTHSTONE.get());
		}
	}.setEnchantmentCategories(ModEnchantments.HEARTHSTONE);

	public static final RegistryObject<Item> HEARTHSTONE = ITEMS.register("hearthstone",
			() -> new RegularHearthstoneItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE).tab(TAB))
	);
	public static final RegistryObject<Item> HEARTHING_PLANKS = ITEMS.register("hearthing_planks",
			() -> new HearthingPlanksItem(new Item.Properties().stacksTo(1).tab(TAB))
	);
	public static final RegistryObject<Item> HEARTHING_GEM = ITEMS.register("hearthing_gem",
			() -> new HearthingGemItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).tab(TAB))
	);
	public static final RegistryObject<Item> COMPANION_HEARTHSTONE = ITEMS.register("companion_hearthstone",
			() -> new CompanionHearthstoneItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).tab(TAB))
	);
	public static final RegistryObject<Item> COMPANION_STONE = ITEMS.register("companion_stone",
			() -> new Item(new Item.Properties().stacksTo(1).tab(TAB)){
				@Override public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
					tooltip.add(new TranslationTextComponent("info.hearthstones.companion_stone.tooltip"));
				}
			}
	);
	public static final RegistryObject<Item> MORTAR = ITEMS.register("mortar",
			() -> new MortarItem(new Item.Properties().durability(155).tab(TAB).setNoRepair())
	);

	public static final RegistryObject<Item> TATTERED_TAVERNCLOTH = ITEMS.register("tattered_taverncloth",
			() -> new ShabbyTavernUpgradeItem(new Item.Properties().tab(TAB))
	);
	public static final RegistryObject<Item> TAVERNCLOTH = ITEMS.register("taverncloth",
			() -> new RegularTavernUpgradeItem(new Item.Properties().tab(TAB))
	);
	public static final RegistryObject<Item> BLUE_TAVERNCLOTH = ITEMS.register("blue_taverncloth",
			() -> new GuidTavernUpgradeItem(new Item.Properties().rarity(Rarity.UNCOMMON).tab(TAB))
	);

	public static final RegistryObject<Item> AQUAMARINE = ITEMS.register("aquamarine",
			() -> new Item(new Item.Properties().tab(TAB))
	);
	public static final RegistryObject<Item> RED_LEATHER = ITEMS.register("red_leather",
			() -> new Item(new Item.Properties().tab(TAB))
	);
	public static final RegistryObject<Item> TATTERED_LEATHER = ITEMS.register("tattered_leather",
			() -> new Item(new Item.Properties().tab(TAB)){
				@Override public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items){
					if(this.allowdedIn(group)&&!ModCfg.easyMode()) items.add(new ItemStack(this));
				}
			}
	);
	public static final RegistryObject<Item> BLUE_LEATHER = ITEMS.register("blue_leather",
			() -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON).tab(TAB))
	);
	public static final RegistryObject<Item> DEEP_BLUE = ITEMS.register("deep_blue",
			() -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON).tab(TAB))
	);
	public static final RegistryObject<Item> AQUAMARINE_DUST = ITEMS.register("aquamarine_dust",
			() -> new Item(new Item.Properties().tab(TAB))
	);
	public static final RegistryObject<Item> DIAMOND_DUST = ITEMS.register("diamond_dust",
			() -> new Item(new Item.Properties().tab(TAB))
	);
	public static final RegistryObject<Item> LAPIS_DUST = ITEMS.register("lapis_dust",
			() -> new Item(new Item.Properties().tab(TAB))
	);

	public static final RegistryObject<Item> AQUAMARINE_ORE = ITEMS.register("aquamarine_ore",
			() -> new BlockItem(ModBlocks.AQUAMARINE_ORE.get(), new Item.Properties().tab(TAB))
	);
	public static final RegistryObject<Item> AQUAMARINE_BLOCK = ITEMS.register("aquamarine_block",
			() -> new BlockItem(ModBlocks.AQUAMARINE_BLOCK.get(), new Item.Properties().tab(TAB))
	);
	public static final RegistryObject<Item> TAVERN = ITEMS.register("tavern",
			() -> new BlockItem(ModBlocks.TAVERN.get(), new Item.Properties().tab(TAB))
	);
	public static final RegistryObject<Item> SHABBY_TAVERN = ITEMS.register("shabby_tavern",
			() -> new BlockItem(ModBlocks.SHABBY_TAVERN.get(), new Item.Properties().tab(TAB))
	);
	public static final RegistryObject<Item> GLOBAL_TAVERN = ITEMS.register("global_tavern",
			() -> new BlockItem(ModBlocks.GLOBAL_TAVERN.get(), new Item.Properties().rarity(Rarity.UNCOMMON).tab(TAB))
	);
}
