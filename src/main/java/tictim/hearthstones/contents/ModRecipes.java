package tictim.hearthstones.contents;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tictim.hearthstones.Hearthstones;
import tictim.hearthstones.contents.recipes.EasyModeCondition;

@Mod.EventBusSubscriber(modid = Hearthstones.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModRecipes{
	@SubscribeEvent
	public static void registerRecipes(RegistryEvent.Register<RecipeSerializer<?>> event){
		CraftingHelper.register(EasyModeCondition.Serializer.INSTANCE);
	}
}
