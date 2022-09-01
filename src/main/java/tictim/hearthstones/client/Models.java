package tictim.hearthstones.client;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import tictim.hearthstones.contents.ModItems;
import tictim.hearthstones.tavern.TavernType;

import java.util.Objects;

import static tictim.hearthstones.Hearthstones.MODID;

@Mod.EventBusSubscriber(modid = MODID, value = Side.CLIENT)
public class Models{
	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event){
		simple(ModItems.HEARTHSTONE);
		simple(ModItems.SHABBY_HEARTHSTONE);
		simple(ModItems.HEARTHING_GEM);
		simple(ModItems.COMPANION_HEARTHSTONE);

		simple(ModItems.COMPANION_STONE);

		simple(ModItems.MORTAR);

		simple(ModItems.TATTERED_TAVERNCLOTH);
		simple(ModItems.TAVERNCLOTH);
		simple(ModItems.BLUE_TAVERNCLOTH);

		simple(ModItems.WAYPOINT);
		simple(ModItems.WAYPOINT_BINDER);
		simple(ModItems.INFINITE_WAYPOINT_BINDER);

		simple(ModItems.AQUAMARINE);
		simple(ModItems.RED_LEATHER);
		simple(ModItems.TATTERED_LEATHER);
		simple(ModItems.BLUE_LEATHER);
		simple(ModItems.PURPLE_LEATHER);
		simple(ModItems.DEEP_BLUE);
		simple(ModItems.DEEP_PURPLE);
		simple(ModItems.AQUAMARINE_DUST);
		simple(ModItems.DIAMOND_DUST);
		simple(ModItems.LAPIS_DUST);
		simple(ModItems.AMETHYST_SHARD);
		simple(ModItems.AMETHYST_DUST);

		simple(ModItems.AQUAMARINE_ORE);
		simple(ModItems.AQUAMARINE_BLOCK);

		simple(ModItems.AMETHYST_BLOCK);
		simple(ModItems.BUDDING_AMETHYST);
		simple(ModItems.AMETHYST_CLUSTER);
		simple(ModItems.SMALL_AMETHYST_BUD);
		simple(ModItems.MEDIUM_AMETHYST_BUD);
		simple(ModItems.LARGE_AMETHYST_BUD);

		for(TavernType tavernType : TavernType.values()){
			ModelLoader.setCustomModelResourceLocation(ModItems.TAVERN,
					tavernType.ordinal(),
					new ModelResourceLocation(new ResourceLocation(MODID, "tavern"), "inventory"));
		}
		simple(ModItems.BINDER_LECTERN);
	}

	private static void simple(Item item){
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(
				Objects.requireNonNull(item.getRegistryName()), "inventory"));
	}
}
