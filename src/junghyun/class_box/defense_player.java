package junghyun.class_box;

import cn.nukkit.Player;

public class defense_player {
	
	public Player player = null;
	
	public int score = 0;
	
	public int kills = 0;
	
	public int deaths = 0;
	
	public int kill_chain = 0;
	
	public int death_chain = 0;
	
	public boolean is_winner = false;
	
	public Player last_hit = null;
	
	public Player getPlayer() {
		return this.player;
	}
	
	public void onhit(Player dmger) {
		this.last_hit = dmger;
	}
	
	public void onkill() {
		this.kills++;
		this.score++;
		
		this.death_chain = 0;
		
		this.kill_chain++;
	}
	
	public void ondeath() {
		this.deaths++;
		
		this.kill_chain = 0;
		
		this.death_chain++;
	}
}