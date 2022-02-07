package tictim.hearthstones.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IIngredientBlacklist;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import tictim.hearthstones.config.ModCfg;
import tictim.hearthstones.contents.ModItems;
import tictim.hearthstones.contents.item.TavernItem;

import javax.annotation.Nonnull;

@JEIPlugin
public class HearthstonesJeiPlugin implements IModPlugin{
	@Override public void register(@Nonnull IModRegistry registry){
		if(ModCfg.easyMode){
			IIngredientBlacklist blacklist = registry.getJeiHelpers().getIngredientBlacklist();
			blacklist.addIngredientToBlacklist(new ItemStack(ModItems.SHABBY_HEARTHSTONE, 1, OreDictionary.WILDCARD_VALUE));
			blacklist.addIngredientToBlacklist(new ItemStack(ModItems.TATTERED_TAVERNCLOTH, 1, OreDictionary.WILDCARD_VALUE));
			blacklist.addIngredientToBlacklist(new ItemStack(ModItems.TATTERED_LEATHER, 1, OreDictionary.WILDCARD_VALUE));
			blacklist.addIngredientToBlacklist(TavernItem.shabby());
		}
	}
}
