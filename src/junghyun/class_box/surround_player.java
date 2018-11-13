package junghyun.class_box;

import cn.nukkit.Player;

public class surround_player {
	
	public Player player = null;

	public int kills = 0;
	public int deaths = 0;
	
	public boolean is_winner = false;
	
	public Player getPlayer() {
		return this.player;
	}
}