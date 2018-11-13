package junghyun.Minigame;

import java.sql.ResultSet;
import java.sql.SQLException;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import junghyun.Main;
import junghyun.Minigame.defense.Defense;
import junghyun.Minigame.wall.Wall;
import junghyun.class_box.Mine24player;
import junghyun.timer.wall_count_down_task;
import junghyun.timer.defense_count_down_task;

public class Minigame_base {
	
	/*
	 * 1 Wall
	 * 2 Defense
	 * 3 pvp_10
	 * 4 colors
	 */
	
	public Main plugin;
	
	public static Wall wall;
	public static wall_count_down_task wall_count_down_task;
	
	public static Defense defense;
	public static defense_count_down_task defense_count_down_task;
	
	public static int hot_join = 0;

	public Minigame_base onEnable(Main plugin) {
		this.plugin = plugin;
		this.plugin.getLogger().info("미니게임 Base 로딩중...");
		
		Minigame_base.wall = new Wall().onEnable(plugin, this); //Wall 로딩
		Minigame_base.wall_count_down_task = new wall_count_down_task(plugin);
		this.plugin.getServer().getScheduler().scheduleRepeatingTask(Minigame_base.wall_count_down_task, 20);
		
		Minigame_base.defense = new Defense().onEnable(plugin, this); //Defense 로딩
		Minigame_base.defense_count_down_task = new defense_count_down_task(plugin);
		this.plugin.getServer().getScheduler().scheduleRepeatingTask(Minigame_base.defense_count_down_task, 20);
		
		
		this.plugin.getLogger().info("미니게임 Base 로딩완료....");
		return this;
	}
	
	public void press_cmd(CommandSender sender, int i, String[] args) {
		/*
		if (i == 1) {
			
		}
		*/
	}
	
	public void getoff_minigame(Player player) {
		Mine24player player_box = Main.players_.get(player.getName());
		player.removeBossBar(player_box.bossbar_id);
		player_box.minigame_type = 0;
		player_box.can_pvp = false;
		player_box.is_minigame = false;
		player_box.bossbar_id = 0;
		player_box.set_inv(player);
		Main.players_.put(player.getName(), player_box);
		Main.popup_task.del_popup(player.getName(), Main.Economy24.getmoney(player.getName().toLowerCase()));
	}
	
	public ResultSet get_player_report_data(String player) {
		String query = "SELECT * FROM player_game_report WHERE player = '"+player+"';";
		ResultSet rs = Main.mysqllib.executequery(query);
		try {
			if (rs.next()) {
				return rs;
			}
		} catch (SQLException e) {
			return null;
		}
		return null;
	}
	
	public void set_clear(Player player) {
		player.setHealth(20);
		player.getFoodData().setLevel(player.getFoodData().getMaxLevel());
	}
	
	public void pvp_off(String name) {
		Mine24player player_box = Main.players_.get(name);
		player_box.can_pvp = false;
		Main.players_.put(name, player_box);
	}

}
