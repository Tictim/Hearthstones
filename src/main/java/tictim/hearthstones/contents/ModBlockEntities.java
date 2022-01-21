package tictim.hearthstones.contents;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import tictim.hearthstones.contents.blockentity.GlobalTavernBlockEntity;
import tictim.hearthstones.contents.blockentity.NormalTavernBlockEntity;
import tictim.hearthstones.contents.blockentity.ShabbyTavernBlockEntity;

import static tictim.hearthstones.Hearthstones.MODID;

public final class ModBlockEntities{
	private ModBlockEntities(){}

	public static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, MODID);

	public static final RegistryObject<BlockEntityType<NormalTavernBlockEntity>> TAVERN = REGISTER.register("tavern",
			() -> BlockEntityType.Builder.of(NormalTavernBlockEntity::new, ModBlocks.TAVERN.get()).build(null)
	);
	public static final RegistryObject<BlockEntityType<ShabbyTavernBlockEntity>> SHABBY_TAVERN = REGISTER.register("shabby_tavern",
			() -> BlockEntityType.Builder.of(ShabbyTavernBlockEntity::new, ModBlocks.SHABBY_TAVERN.get()).build(null)
	);
	public static final RegistryObject<BlockEntityType<GlobalTavernBlockEntity>> GLOBAL_TAVERN = REGISTER.register("global_tavern",
			() -> BlockEntityType.Builder.of(GlobalTavernBlockEntity::new, ModBlocks.GLOBAL_TAVERN.get()).build(null)
	);
}
