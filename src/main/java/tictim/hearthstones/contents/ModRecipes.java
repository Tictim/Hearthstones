package tictim.hearthstones.contents;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tictim.hearthstones.Hearthstones;
import tictim.hearthstones.recipes.EasyModeCondition;

@Mod.EventBusSubscriber(modid = Hearthstones.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModRecipes{
	@SubscribeEvent
	public static void registerRecipes(RegistryEvent.Register<IRecipeSerializer<?>> event){
		CraftingHelper.register(EasyModeCondition.Serializer.INSTANCE);
	}
}
