package tictim.hearthstones.contents;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public final class HearthstoneTab extends CreativeTabs{
	private static final HearthstoneTab instance = new HearthstoneTab();

	public static CreativeTabs get(){
		return instance;
	}

	private HearthstoneTab(){
		super("hearthstones");
		this.setRelevantEnchantmentTypes(ModEnchantments.HEARTHSTONE);
	}

	@Override public ItemStack createIcon(){
		return new ItemStack(ModItems.HEARTHSTONE);
	}
}
