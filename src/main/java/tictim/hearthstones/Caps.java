package tictim.hearthstones;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import tictim.hearthstones.contents.item.TavernBinderItem;
import tictim.hearthstones.contents.item.hearthstone.HearthstoneItem;
import tictim.hearthstones.tavern.TavernBinderData;
import tictim.hearthstones.tavern.TavernMemories;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Caps{
	@SuppressWarnings("ConstantConditions") @Nonnull private static <T> T definitelyNotNull(){
		return null;
	}

	@CapabilityInject(TavernMemories.class)
	public static final Capability<TavernMemories> TAVERN_MEMORIES = definitelyNotNull();
	@CapabilityInject(HearthstoneItem.Data.class)
	public static final Capability<HearthstoneItem.Data> HEARTHSTONE_DATA = definitelyNotNull();
	@CapabilityInject(TavernBinderData.class)
	public static final Capability<TavernBinderData> BINDER_DATA = definitelyNotNull();

	public static void register(){
		registerCapability(TavernMemories.class);
		registerCapability(HearthstoneItem.Data.class);
		registerCapability(TavernBinderData.class);
	}

	private static <T> void registerCapability(Class<T> clazz){
		CapabilityManager.INSTANCE.register(clazz, new Capability.IStorage<T>(){
			@Nullable @Override public NBTBase writeNBT(Capability<T> capability, T instance, EnumFacing side){
				return null;
			}
			@Override public void readNBT(Capability<T> capability, T instance, EnumFacing side, NBTBase nbt){}
		}, () -> {
			throw new UnsupportedOperationException();
		});
	}
}
