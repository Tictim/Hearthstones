package tictim.hearthstones.client.screen;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import tictim.hearthstones.client.screen.TavernButton.TavernProperty;
import tictim.hearthstones.hearthstone.HearthingGemHearthstone;
import tictim.hearthstones.net.ModNet;
import tictim.hearthstones.net.OpenHearthstoneScreenMsg;
import tictim.hearthstones.net.TavernMemoryOperationMsg;
import tictim.hearthstones.tavern.PlayerTavernMemory;
import tictim.hearthstones.tavern.Tavern;
import tictim.hearthstones.tavern.TavernMemory;
import tictim.hearthstones.tavern.TavernRecord;
import tictim.hearthstones.tavern.TavernType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class HearthstoneScreen extends TavernMemoryScreen{
	public PlayerTavernMemory playerMemory;
	public TavernMemory globalMemory;
	public boolean hearthingGem;

	public HearthstoneScreen(){
		this.playerMemory = new PlayerTavernMemory();
		this.globalMemory = new TavernMemory();
	}

	public void updateData(OpenHearthstoneScreenMsg packet){
		this.playerMemory = packet.playerMemory();
		this.globalMemory = packet.globalMemory();
		this.hearthingGem = packet.isHearthingGem();
		if(isInitialized()) refreshTavernButtons();
	}

	@Override protected List<TavernButton> createTavernButtons(){
		List<TavernButton> buttons = new ArrayList<>();
		for(TavernRecord t : playerMemory.taverns().values()){
			TavernButton b = new TavernButton(this, t);
			b.canSelect = true;
			b.canDelete = true;
			addProperties(b);
			buttons.add(b);
		}
		for(TavernRecord t : globalMemory.taverns().values()){
			if(playerMemory.has(t.pos())) continue;
			TavernButton b = new TavernButton(this, t);
			b.canSelect = true;
			b.properties.add(TavernProperty.GLOBAL);
			addProperties(b);
			buttons.add(b);
		}
		buttons.sort((b1, b2) -> {
			// substitute for comparing between player memory / global memory
			if(b1.canDelete!=b2.canDelete)
				return b1.canDelete ? -1 : 1;
			Tavern o1 = b1.tavern, o2 = b2.tavern;

			int i;

			// home
			Tavern homeTavern = this.playerMemory.getHomeTavern();
			i = Boolean.compare(homeTavern==o2, homeTavern==o1);
			if(i!=0) return i;

			// (Optional) distance
			ResourceLocation d1 = o1.pos().dim();
			ResourceLocation d2 = o2.pos().dim();
			ResourceKey<Level> dim = minecraft.level.dimension();
			if(d1.equals(d2)&&dim.location().equals(d1)){
				double s1 = minecraft.player.distanceToSqr(o1.blockPos().getX()+0.5, o1.blockPos().getY()+0.5, o1.blockPos().getZ()+0.5);
				double s2 = minecraft.player.distanceToSqr(o2.blockPos().getX()+0.5, o2.blockPos().getY()+0.5, o2.blockPos().getZ()+0.5);
				i = Double.compare(s1, s2);
				if(i!=0) return i;
			}

			// missing
			i = Boolean.compare(o1.isMissing(), o2.isMissing());
			if(i!=0) return i;

			// dimension
			i = o1.pos().dim().compareTo(o2.pos().dim());
			if(i!=0) return i;

			// name
			String n1 = o1.name();
			String n2 = o2.name();
			if(n1==null){
				if(n2!=null) return -1;
			}else if(n2==null) return 1;
			else{
				i = n1.compareTo(n2);
				if(i!=0) return i;
			}

			// static position
			return o1.blockPos().compareTo(o2.blockPos());
		});
		return buttons;
	}

	@Override protected void select(Tavern tavern){
		ModNet.CHANNEL.sendToServer(new TavernMemoryOperationMsg(tavern.pos(), TavernMemoryOperationMsg.SELECT));
		playerMemory.select(tavern.pos());
		for(TavernButton b : tavernButtons)
			b.selected = b.tavern.pos().equals(tavern.pos());
	}
	@Override protected void delete(Tavern tavern){
		ModNet.CHANNEL.sendToServer(new TavernMemoryOperationMsg(tavern.pos(), TavernMemoryOperationMsg.DELETE));
		playerMemory.delete(tavern.pos());
		refreshTavernButtons();
	}

	@SuppressWarnings("ConstantConditions")
	private void addProperties(TavernButton button){
		if(button.tavern.isMissing())
			button.properties.add(TavernProperty.MISSING);
		if(button.tavern.pos().equals(playerMemory.getHomePos()))
			button.properties.add(TavernProperty.HOME);
		if(button.tavern.type()==TavernType.SHABBY)
			button.properties.add(TavernProperty.SHABBY);
		if(hearthingGem&&HearthingGemHearthstone.isTooFar(minecraft.player, button.tavern.pos()))
			button.properties.add(TavernProperty.TOO_FAR);
		if(button.tavern.pos().equals(playerMemory.getSelectedPos()))
			button.selected = true;
	}

	@Nullable @Override protected Component getEmptyScreenMessage(){
		return new TranslatableComponent("info.hearthstones.screen.empty");
	}
}
