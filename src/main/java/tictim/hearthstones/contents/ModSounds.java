package tictim.hearthstones.contents;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import static tictim.hearthstones.Hearthstones.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class ModSounds{
	public static final SoundEvent AMETHYST_BLOCK_BREAK = soundEvent("block.amethyst_block.break");
	public static final SoundEvent AMETHYST_BLOCK_CHIME = soundEvent("block.amethyst_block.chime");
	public static final SoundEvent AMETHYST_BLOCK_FALL = soundEvent("block.amethyst_block.fall");
	public static final SoundEvent AMETHYST_BLOCK_HIT = soundEvent("block.amethyst_block.hit");
	public static final SoundEvent AMETHYST_BLOCK_PLACE = soundEvent("block.amethyst_block.place");
	public static final SoundEvent AMETHYST_BLOCK_STEP = soundEvent("block.amethyst_block.step");

	public static final SoundEvent AMETHYST_CLUSTER_BREAK = soundEvent("block.amethyst_cluster.break");
	public static final SoundEvent AMETHYST_CLUSTER_FALL = soundEvent("block.amethyst_cluster.fall");
	public static final SoundEvent AMETHYST_CLUSTER_HIT = soundEvent("block.amethyst_cluster.hit");
	public static final SoundEvent AMETHYST_CLUSTER_PLACE = soundEvent("block.amethyst_cluster.place");
	public static final SoundEvent AMETHYST_CLUSTER_STEP = soundEvent("block.amethyst_cluster.step");

	public static final SoundEvent SMALL_AMETHYST_BUD_BREAK = soundEvent("block.small_amethyst_bud.break");
	public static final SoundEvent SMALL_AMETHYST_BUD_PLACE = soundEvent("block.small_amethyst_bud.place");
	public static final SoundEvent MEDIUM_AMETHYST_BUD_BREAK = soundEvent("block.medium_amethyst_bud.break");
	public static final SoundEvent MEDIUM_AMETHYST_BUD_PLACE = soundEvent("block.medium_amethyst_bud.place");
	public static final SoundEvent LARGE_AMETHYST_BUD_BREAK = soundEvent("block.large_amethyst_bud.break");
	public static final SoundEvent LARGE_AMETHYST_BUD_PLACE = soundEvent("block.large_amethyst_bud.place");

	@SubscribeEvent
	public static void registerSounds(RegistryEvent.Register<SoundEvent> event){
		IForgeRegistry<SoundEvent> r = event.getRegistry();
		r.register(AMETHYST_BLOCK_BREAK);
		r.register(AMETHYST_BLOCK_CHIME);
		r.register(AMETHYST_BLOCK_FALL);
		r.register(AMETHYST_BLOCK_HIT);
		r.register(AMETHYST_BLOCK_PLACE);
		r.register(AMETHYST_BLOCK_STEP);

		r.register(AMETHYST_CLUSTER_BREAK);
		r.register(AMETHYST_CLUSTER_FALL);
		r.register(AMETHYST_CLUSTER_HIT);
		r.register(AMETHYST_CLUSTER_PLACE);
		r.register(AMETHYST_CLUSTER_STEP);
	}

	private static SoundEvent soundEvent(String path){
		ResourceLocation id = new ResourceLocation(MODID, path);
		return new SoundEvent(id).setRegistryName(id);
	}
}
