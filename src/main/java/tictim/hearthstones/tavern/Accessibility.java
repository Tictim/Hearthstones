package tictim.hearthstones.tavern;

public enum Accessibility{
	MODIFIABLE,
	PARTIALLY_MODIFIABLE,
	READ_ONLY;

	public boolean isModifiable(){
		return this!=READ_ONLY;
	}
	public boolean isAccessibilityModifiable(){
		return this==MODIFIABLE;
	}

	public int getMeta(){
		return switch(this){
			case MODIFIABLE -> 0;
			case PARTIALLY_MODIFIABLE -> 1;
			case READ_ONLY -> -1;
		};
	}

	public static Accessibility fromMeta(int meta){
		return switch(meta){
			case 0 -> MODIFIABLE;
			case 1 -> PARTIALLY_MODIFIABLE;
			default -> READ_ONLY;
		};
	}
}
