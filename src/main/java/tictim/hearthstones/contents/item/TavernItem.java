package tictim.hearthstones.contents.item;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import tictim.hearthstones.config.ModCfg;
import tictim.hearthstones.contents.ModItems;
import tictim.hearthstones.tavern.TavernType;

public class TavernItem extends ItemBlock{
	public static ItemStack normal(){
		return stack(TavernType.NORMAL);
	}
	public static ItemStack normal(int amount){
		return stack(amount, TavernType.NORMAL);
	}
	public static ItemStack shabby(){
		return stack(TavernType.SHABBY);
	}
	public static ItemStack shabby(int amount){
		return stack(amount, TavernType.SHABBY);
	}
	public static ItemStack global(){
		return stack(TavernType.GLOBAL);
	}
	public static ItemStack global(int amount){
		return stack(amount, TavernType.GLOBAL);
	}
	public static ItemStack stack(TavernType type){
		return stack(1, type);
	}
	public static ItemStack stack(int amount, TavernType type){
		return new ItemStack(ModItems.TAVERN, amount, type.ordinal());
	}

	public static TavernType type(ItemStack stack){
		return TavernType.of(stack.getItemDamage());
	}

	public TavernItem(Block block){
		super(block);
		setHasSubtypes(true);
	}

	@SuppressWarnings("deprecation") @Override public EnumRarity getRarity(ItemStack stack){
		EnumRarity rarity = type(stack).getRarity();
		return stack.isItemEnchanted() ? RareItem.upper(rarity) : rarity;
	}

	@Override public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items){
		if(isInCreativeTab(tab)){
			items.add(normal());
			if(!ModCfg.easyMode()) items.add(shabby());
			items.add(global());
		}
	}

	@Override public String getTranslationKey(ItemStack stack){
		return type(stack).getBlockNameTranslationKey();
	}

	@Override public int getMetadata(int damage){
		return damage*4;
	}
}
