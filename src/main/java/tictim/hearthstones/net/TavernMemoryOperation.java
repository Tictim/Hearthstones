package tictim.hearthstones.net;

import tictim.hearthstones.data.TavernPos;

import javax.annotation.Nullable;

public class TavernMemoryOperation{
	public static final byte SELECT = 1;
	public static final byte DELETE = 2;
	public static final byte SET_HOME = 3;

	@Nullable public final TavernPos pos;
	public final byte operation;

	public TavernMemoryOperation(@Nullable TavernPos pos, byte operation){
		this.pos = pos;
		this.operation = operation;
	}
}
