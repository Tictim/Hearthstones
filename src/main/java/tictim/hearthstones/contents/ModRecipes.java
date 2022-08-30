package tictim.hearthstones.contents;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;
import tictim.hearthstones.Hearthstones;
import tictim.hearthstones.contents.recipes.ChargeBinderRecipe;
import tictim.hearthstones.contents.recipes.EasyModeCondition;

import static tictim.hearthstones.Hearthstones.MODID;

@Mod.EventBusSubscriber(modid = Hearthstones.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModRecipes{

	public static final DeferredRegister<RecipeSerializer<?>> REGISTER = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);

	public static final RegistryObject<SimpleRecipeSerializer<?>> CHARGE_BINDER_RECIPE = REGISTER.register("charge_binder", () -> new SimpleRecipeSerializer<>(ChargeBinderRecipe::new));

	@SubscribeEvent
	public static void registerRecipes(RegisterEvent event){
		if(event.getRegistryKey().equals(ForgeRegistries.Keys.RECIPE_SERIALIZERS)){
			CraftingHelper.register(EasyModeCondition.Serializer.INSTANCE);
		}
	}
}
