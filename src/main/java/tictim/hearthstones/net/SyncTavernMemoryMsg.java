package tictim.hearthstones.net;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import tictim.hearthstones.capability.TavernMemory;

import java.util.Objects;

public class SyncTavernMemoryMsg{
	public static SyncTavernMemoryMsg read(FriendlyByteBuf buf){
		return new SyncTavernMemoryMsg(Objects.requireNonNull(buf.readNbt()), Objects.requireNonNull(buf.readNbt()));
	}

	public final CompoundTag player;
	public final CompoundTag global;

	public SyncTavernMemoryMsg(Player player){
		this(TavernMemory.expectFromPlayer(player).write(), serializeAccessibleTaverns(player));
	}

	private static CompoundTag serializeAccessibleTaverns(Player player){
		return TavernMemory.expectServerGlobal().taverns().values().stream()
				.filter(tavernRecord -> tavernRecord.hasAccessPermission(player))
				.collect(TavernMemory::new, TavernMemory::addOrUpdate, (tavernMemory, tavernMemory2) -> {})
				.write();
	}

	public SyncTavernMemoryMsg(CompoundTag nbt, CompoundTag globalNbt){
		this.player = nbt;
		this.global = globalNbt;
	}

	public void write(FriendlyByteBuf buf){
		buf.writeNbt(player);
		buf.writeNbt(global);
	}
}
