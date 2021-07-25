package tictim.hearthstones.contents;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import tictim.hearthstones.tileentity.GlobalTavernTileEntity;
import tictim.hearthstones.tileentity.NormalTavernTileEntity;
import tictim.hearthstones.tileentity.ShabbyTavernTileEntity;

import static tictim.hearthstones.Hearthstones.MODID;

public final class ModTileEntities{
	private ModTileEntities(){}

	public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, MODID);

	public static final RegistryObject<TileEntityType<NormalTavernTileEntity>> TAVERN = TILE_ENTITIES.register("tavern",
			() -> TileEntityType.Builder.of(NormalTavernTileEntity::new, ModBlocks.TAVERN.get()).build(null)
	);
	public static final RegistryObject<TileEntityType<ShabbyTavernTileEntity>> SHABBY_TAVERN = TILE_ENTITIES.register("shabby_tavern",
			() -> TileEntityType.Builder.of(ShabbyTavernTileEntity::new, ModBlocks.SHABBY_TAVERN.get()).build(null)
	);
	public static final RegistryObject<TileEntityType<GlobalTavernTileEntity>> GLOBAL_TAVERN = TILE_ENTITIES.register("global_tavern",
			() -> TileEntityType.Builder.of(GlobalTavernTileEntity::new, ModBlocks.GLOBAL_TAVERN.get()).build(null)
	);
}
