package datagen;

import com.mojang.datafixers.util.Pair;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.RegistryObject;
import tictim.hearthstones.contents.ModBlocks;
import tictim.hearthstones.contents.ModItems;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class LootTableGen extends LootTableProvider{
	public LootTableGen(DataGenerator dataGeneratorIn){
		super(dataGeneratorIn);
	}

	@Override protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables(){
		return Collections.singletonList(Pair.of(BlockTables::new, LootContextParamSets.BLOCK));
	}

	@Override protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker){}


	public static class BlockTables extends BlockLoot{
		@Override protected void addTables(){
			Function<Block, LootTable.Builder> aquamarineLoot = b -> createSilkTouchDispatchTable(b,
					applyExplosionDecay(b,
							LootItem.lootTableItem(ModItems.AQUAMARINE.get())
									.apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
									.apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))));
			add(ModBlocks.AQUAMARINE_ORE.get(), aquamarineLoot);
			add(ModBlocks.DEEPSLATE_AQUAMARINE_ORE.get(), aquamarineLoot);

			dropSelf(ModBlocks.AQUAMARINE_BLOCK.get());

			registerTavernLootTable(ModBlocks.TAVERN.get());
			registerTavernLootTable(ModBlocks.SHABBY_TAVERN.get());
			registerTavernLootTable(ModBlocks.GLOBAL_TAVERN.get());

			dropOther(ModBlocks.BINDER_LECTERN.get(), Items.LECTERN);
		}

		// ew
		private void registerTavernLootTable(Block block){
			add(block, LootTable.lootTable().withPool(
					applyExplosionCondition(block,
							LootPool.lootPool()
									.setRolls(ConstantValue.exactly(1))
									.add(LootItem.lootTableItem(block)
											.apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
											.apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
													.copy("owner", "BlockEntityTag.owner"))))));
		}

		@Override protected Iterable<Block> getKnownBlocks(){
			return ModBlocks.REGISTER.getEntries().stream().map(RegistryObject::get).collect(Collectors.toList());
		}
	}
}
