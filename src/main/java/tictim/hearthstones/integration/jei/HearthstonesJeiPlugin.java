package tictim.hearthstones.integration.jei;
/*
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.resources.ResourceLocation;
import tictim.hearthstones.Hearthstones;
import tictim.hearthstones.contents.ModTags;

import java.util.Arrays;

@JeiPlugin
public final class HearthstonesJeiPlugin implements IModPlugin{
	*/
/*@Override
	public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration){
		registration.getCraftingCategory().addCategoryExtension(TavernSkinChangeRecipe.class, tavernSkinChangeRecipe -> HearthstonesJeiPlugin::setIngredients);
	}
	*//*

	private static void setIngredients(IIngredients ingredients){
		ingredients.setInputIngredients(Arrays.asList(Ingredient.of(Items.OAK_LOG), Ingredient.of(ModTags.HEARTHSTONE_MATERIAL)));
	}
	*/
/*
	@Override
	public void register(IModRegistry registry){
		if(HearthstoneConfig.easyMode()){
			IIngredientBlacklist blacklist = registry.getJeiHelpers().getIngredientBlacklist();
			blacklist.addIngredientToBlacklist(new ItemStack(HearthstoneItems.SHABBY_HEARTHSTONE, 1, OreDictionary.WILDCARD_VALUE));
			blacklist.addIngredientToBlacklist(new ItemStack(HearthstoneItems.TATTERED_TAVERNCLOTH, 1, OreDictionary.WILDCARD_VALUE));
			blacklist.addIngredientToBlacklist(new ItemStack(HearthstoneItems.TATTERED_LEATHER, 1, OreDictionary.WILDCARD_VALUE));
			blacklist.addIngredientToBlacklist(new ItemStack(HearthstoneItems.SHABBY_TAVERN, 1));
		}
	}
	*//*

	@Override
	public ResourceLocation getPluginUid(){
		return new ResourceLocation(Hearthstones.MODID, "hearthstones");
	}
}
*/
