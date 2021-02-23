package tictim.hearthstones.datagen;

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.LootTableProvider;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.ConstantRange;
import net.minecraft.world.storage.loot.ItemLootEntry;
import net.minecraft.world.storage.loot.LootParameterSet;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.ValidationTracker;
import net.minecraft.world.storage.loot.functions.ApplyBonus;
import net.minecraft.world.storage.loot.functions.CopyName;
import net.minecraft.world.storage.loot.functions.CopyNbt;
import net.minecraft.world.storage.loot.functions.SetCount;
import net.minecraftforge.fml.RegistryObject;
import tictim.hearthstones.contents.ModBlocks;
import tictim.hearthstones.contents.ModItems;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class LootTableGen extends LootTableProvider{
	public LootTableGen(DataGenerator dataGeneratorIn){
		super(dataGeneratorIn);
	}

	@Override protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables(){
		return Collections.singletonList(Pair.of(BlockTables::new, LootParameterSets.BLOCK));
	}

	@Override protected void validate(Map<ResourceLocation, LootTable> map, ValidationTracker validationtracker){}


	public static class BlockTables extends BlockLootTables{
		@Override protected void addTables(){
			registerLootTable(ModBlocks.AQUAMARINE_ORE.get(),
					b -> droppingWithSilkTouch(b,
							withExplosionDecay(b,
									ItemLootEntry.builder(ModItems.AQUAMARINE_ORE.get())
											.acceptFunction(SetCount.builder(RandomValueRange.of(1, 2)))
											.acceptFunction(ApplyBonus.oreDrops(Enchantments.FORTUNE)))));

			registerDropSelfLootTable(ModBlocks.AQUAMARINE_BLOCK.get());

			registerTavernLootTable(ModBlocks.TAVERN.get());
			registerTavernLootTable(ModBlocks.SHABBY_TAVERN.get());
			registerTavernLootTable(ModBlocks.GLOBAL_TAVERN.get());
		}

		// ew
		private void registerTavernLootTable(Block block){
			registerLootTable(block, LootTable.builder().addLootPool(
					withSurvivesExplosion(block,
							LootPool.builder()
									.rolls(ConstantRange.of(1))
									.addEntry(ItemLootEntry.builder(block)
											.acceptFunction(CopyName.builder(CopyName.Source.BLOCK_ENTITY))
											.acceptFunction(CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY)
													.replaceOperation("owner", "BlockEntityTag.owner"))))));
		}

		@Override protected Iterable<Block> getKnownBlocks(){
			return ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get).collect(Collectors.toList());
		}
	}
}
