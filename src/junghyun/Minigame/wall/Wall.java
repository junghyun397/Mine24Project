package junghyun.Minigame.wall;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import cn.nukkit.Player;
import cn.nukkit.block.BlockGlass;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import junghyun.Main;
import junghyun.Minigame.Minigame_base;
import junghyun.Wildgame.Wildgame;
import junghyun.class_box.Mine24player;
import junghyun.class_box.wall_player;
import junghyun.task.wall_clear_task;
import junghyun.task.wall_respawn_task;

public class Wall {
	
	public Main plugin;
	
	public Minigame_base base;
	
	public static Map<String, wall_player> players = new HashMap<>();
	
	public static JSONObject players_all = null;

	public static Level gamelevel;
	
	public static boolean in_game = false;
	
	public static boolean is_pvp = false;
	
	public static boolean end_setup = true;
	
	public static int player_count = 0;
	
	public static Position pos = null;
	
	public static int kd_count = 0;
	
	public static JSONObject report_kd = null;
	
	public static String start_date = null;
	
	public Wall onEnable(Main plugin, Minigame_base base) {
		this.plugin = plugin;
		this.plugin.getLogger().info("미니게임 Wall 로딩중...");
		this.base = base;
		this.plugin.getLogger().info("미니게임 Wall 로딩완료....");
		return this;
	}
	
	public Level create_tempworld() {
		int info = Wildgame.world.worldinfo("-Wall"); //확인
		if (info == 1) {
			Level level = this.plugin.getServer().getLevelByName("-Wall"); //지정
			this.plugin.getServer().unloadLevel(level); //언로드
			this.del_tempworld(); //삭제
			this.plugin.getServer().generateLevel("-Wall"); //생성
			return this.plugin.getServer().getLevelByName("-Wall"); //반환
		} else if (info == 2) {
			this.del_tempworld(); //삭제
			this.plugin.getServer().generateLevel("-Wall"); //생성
			return this.plugin.getServer().getLevelByName("-Wall");  //반환
		}
		this.plugin.getServer().generateLevel("-Wall"); //생성
		return this.plugin.getServer().getLevelByName("-Wall"); //반환
	}
	
	public void del_tempworld() {
		File reg_file = new File("/root/worlds/-Wall/region"); //하위 폴더를 확정
		String[] fnameList = reg_file.list();
		int reg_fCnt = fnameList.length;
		String childPath = null;
	    for(int i = 0; i < reg_fCnt; i++) {
	    	childPath = "/root/worlds/-Wall/region/"+fnameList[i];
	    	File f = new File(childPath);
	    	f.delete();
	    }
	    reg_file.delete(); //region 삭제
	    File dat_file = new File("/root/worlds/-Wall/level.dat"); //level.dat 삭제
	    dat_file.delete();
	    File file = new File("/root/worlds/-Wall"); // 폴더 삭제
	    file.delete();
	    this.del_tempworld_old();
	}
	
	public void del_tempworld_old() {
		try {
			File reg_file = new File("/root/worlds/-Wall.old/region"); // 하위
																			// 폴더를
																			// 확정
			String[] fnameList = reg_file.list();
			int reg_fCnt = fnameList.length;
			String childPath = null;
			for (int i = 0; i < reg_fCnt; i++) {
				childPath = "/root/worlds/-Wall.old/region/" + fnameList[i];
				File f = new File(childPath);
				f.delete();
			}
			reg_file.delete(); // region 삭제
			File dat_file = new File("/root/worlds/-Wall.old/level.dat.old"); // level.dat
																					// 삭제
			dat_file.delete();
			File file = new File("/root/worlds/-Wall.old"); // 폴더 삭제
			file.delete();
		} catch (Exception e) {
			return;
		}
		return;
	}
	
	public void killed_player(Player death) {
		if (Wall.is_pvp) {
			this.getoff_player(death);
			this.broadcastMsg("<§e미니게임§f> "+death.getName()+" 님이 죽었습니다."
					+ "\n<§e미니게임§f> 현재 "+ Wall.players.size()+"명의 플레이어가 남아 있습니다.");
			death.sendMessage("§7----------------");
			death.sendMessage("<§e미니게임§f> 죽었습니다! 탈락 되셨습니다. 다음에 다시 도전 해보세요. :D");
			death.sendMessage("§7----------------");
			this.update_sign_life(Main.Mine24core.mainlevel);
			
			if (Wall.players.size() == 1) {
				Player winner = null;
				for (Map.Entry<String, wall_player> entry: Wall.players.entrySet()) {
					winner = entry.getValue().getPlayer();
				}
				this.end_game(winner);
			}
		}
	}
	
	public void join_game(Player player) {
		if (Wall.players.get(player.getName()) != null) {
			player.sendMessage("§7----------------");
			player.sendMessage("<§e미니게임§f> 이미 대기중 입니다.");
			player.sendMessage("§7----------------");
			return;
		}
		if (Wall.players.size() >= 20) {
			player.sendMessage("§7----------------");
			player.sendMessage("<§e미니게임§f> 미니게임에 입장할수 없습니다. 사람이 너무 많습니다.");
			player.sendMessage("§7----------------");
			return;
		}
		if (Wall.in_game) {
			player.sendMessage("§7----------------");
			player.sendMessage("<§e미니게임§f> 미니게임이 진행중 입니다. 잠시 기다려 주시기 바랍니다.");
			player.sendMessage("§7----------------");
			return;
		}
		
		wall_player wall_player = new wall_player();
		wall_player.player = player;
		
		Wall.players.put(player.getName(), wall_player);
		this.update_sign_stay(Main.Mine24core.mainlevel);
		String msg_list = " ";
		for (Map.Entry<String, wall_player> entry: Wall.players.entrySet()) {
			msg_list = msg_list+(entry.getValue().getPlayer().getName())+" ";
		}
		player.sendMessage("§7----------------");
		player.sendMessage("<§e미니게임§f> 미니게임 Wall 에 참여 하셨습니다.");
		player.sendMessage("<§e미니게임§f> 현재 대기중인 인원 -" + msg_list);
		player.sendMessage("§7----------------");
		
		this.broadcastPopUP("§7"+player.getName()+"님이 참여 하셨습니다.");
		player.teleport(new Position(-470, 8, 1756, Main.Mine24core.mainlevel));
		this.setup_player(player);
		if (Wall.players.size() == 4) {
			this.setup_game();
		}
		return;
	}
	
	public void setup_game() {
		Wall.gamelevel = this.create_tempworld();
		Wall.gamelevel.generateChunk(128 >> 4, 128 >> 4);
		this.count_down_start();
		this.broadcastMsg("<§e미니게임§f> 30초 뒤에 게임이 시작 됩니다. 준비 해주세요!");
		this.count_down_start();
	}
	
	@SuppressWarnings("unchecked")
	public void start_game() {
		Position spawn = this.find_spawn();
		String msg_list = " ";
		
		this.start_report_game();
		
		int index_count = 0;
		for (Map.Entry<String, wall_player> entry: Wall.players.entrySet()) {
			msg_list = msg_list+(entry.getValue().getPlayer().getName())+" ";
			Wall.players_all.put(index_count, entry.getValue().getPlayer().getName());
			index_count++;
		}
		Wall.players_all.put("index", index_count);
		
		for (Map.Entry<String, wall_player> entry: Wall.players.entrySet()) {
			Player player = this.plugin.getServer().getPlayer(entry.getKey());
			
			Mine24player player_box = Main.players_.get(entry.getKey());
			player_box.bossbar_id = player.createBossBar("\n\n데스매치 시작까지 §e10분§f 남았습니다.", 100);
			Main.players_.put(entry.getKey(), player_box);
			
			player.teleport(spawn);
			player.sendMessage("§7----------------");
			player.sendMessage("<§e미니게임§f> 재료 수집이 시작 되었습니다! 10분간 전투에 참여할 재료를 모아 보세요."
					+ "\n<§e미니게임§f> 참여 인원  : " + msg_list);
			player.sendMessage("§7----------------");
		}
		
		this.update_sign_life(Main.Mine24core.mainlevel);
		
		this.pvp_start_count();
		Wall.in_game = true;
		Wall.player_count = Wall.players.size();
	}
	
	public void update_bossbar(int time) {
		for (Map.Entry<String, wall_player> entry: Wall.players.entrySet()) {
			Player player = this.plugin.getServer().getPlayer(entry.getKey());
			Mine24player player_box = Main.players_.get(entry.getKey());
			player.updateBossBar("\n\n§7데스매치 시작까지 §e"+time+"분§7 남았습니다.", (10*time), player_box.bossbar_id);
		}
	}
	
	public void start_pvp() {
		Wall.is_pvp = true;
		Position spawn = this.find_spawn();
		String msg_list = " ";
		
		this.update_sign_life(Main.Mine24core.mainlevel);
		
		for (Map.Entry<String, wall_player> entry: Wall.players.entrySet()) {
			msg_list = msg_list+(entry.getValue().getPlayer().getName())+" ";
			
			Mine24player player_box = Main.players_.get(entry.getKey());
			player_box.can_pvp = true;
			entry.getValue().getPlayer().removeBossBar(player_box.bossbar_id);
			
			player_box.bossbar_id = 0;
			Main.players_.put(entry.getKey(), player_box);
		}
		for (Map.Entry<String, wall_player> entry: Wall.players.entrySet()) {
			Player player = this.plugin.getServer().getPlayer(entry.getKey());
			player.teleport(spawn);
			entry.getValue().getPlayer().sendMessage("§7----------------");
			entry.getValue().getPlayer().sendMessage("<§e미니게임§f> PVP가 시작 되었습니다! 서로를 죽여 최후의  1인이 되어 보세요."
					+ "\n<§e미니게임§f> 대전 상대  : " + msg_list);
			entry.getValue().getPlayer().sendMessage("§7----------------");
		}
	}
	
	public void end_game(Player winner) {
		winner.sendMessage("§7----------------");
		winner.sendMessage("<§e미니게임§f> 축하드립니다! 최후의 1인으로 우승 하셨습니다.");
		winner.sendMessage("<§e미니게임§f> "+ Wall.player_count+"명과 경기를 벌여 우승 하셨습니다. 총 "+ Wall.player_count*100+" OP 의 상금을 얻었습니다.");
		winner.sendMessage("§7----------------");
		winner.teleport(Main.main_level_pos);
		
		this.end_report_game(winner.getName(), 1);
//		this.plugin.getServer().broadcastMessage("<§e안내§f> Wall 미니게임에서 ");
		this.getoff_player(winner);
		this.base.pvp_off(winner.getName());
		Main.Economy24.addmoney(winner.getName().toLowerCase(), Wall.player_count*100);
		this.clear_game();
		return;
	}
	
	public void end_game_out(Player winner) {
		winner.sendMessage("§7----------------");
		winner.sendMessage("<§e미니게임§f> 인원이 부족해 미니게임을 더이상 진행할수 없습니다.");
		winner.sendMessage("<§e미니게임§f> 스폰으로 이동 됩니다...");
		winner.sendMessage("§7----------------");
		winner.teleport(Main.main_level_pos);
		this.getoff_player(winner);
		this.base.pvp_off(winner.getName());
		this.clear_game();
		return;
	}
	
	public void setup_player(Player player) {
		Mine24player player_box = Main.players_.get(player.getName());
		player_box.is_minigame = true;
		player_box.minigame_type = 1; //Wall
		player_box.backup_inv(player);
		
		Main.players_.put(player.getName(), player_box);
		Main.popup_task.off_popup(player.getName());
	}
	
	public Player get_player(String name) {
		return this.plugin.getServer().getPlayer(name);
	}
	
	public void go_exit(Player player) {
		if (!Wall.in_game) {
			this.getoff_player(player);
			this.update_sign_stay(Main.Mine24core.mainlevel);
			return;
		}
		this.getoff_player(player);
		
		this.broadcastMsg("<§e미니게임§f> "+player.getName()+" 님이 탈주 하셨습니다."
				+ "\n<§e미니게임§f> 현재 "+ Wall.players.size()+"명의 플레이어가 남아 있습니다.");
		this.update_sign_life(Main.Mine24core.mainlevel);
		if (Wall.players.size() == 0) {
			this.clear_game();
		} else if (Wall.players.size() == 1) {
			Player winner = null;
			for (Map.Entry<String, wall_player> entry: Wall.players.entrySet()) {
				winner = entry.getValue().getPlayer();
			}
			if (Wall.is_pvp) {
				this.end_game(winner);
			} else {
				winner.sendMessage("<§e미니게임§f> 플레이어 부족으로 진행이 불가능 합니다.");
				winner.sendMessage("<§e미니게임§f> 경기가 취소 되었습니다. 스폰으로 이동 됩니다...");
				winner.sendMessage("§7----------------");
				winner.teleport(Main.main_level_pos);
				this.getoff_player(winner);
				this.end_report_game(null, 2);
				this.clear_count_down();
				this.clear_game();
			}
		}
	}
	
	public void move_pvp(Player player) {
		if (player.getX() <= 78) {
			player.sendPopup("§7PVP 제한 거리를 벗어 났습니다.");
			player.teleport(this.find_spawn());
		} else if (player.getZ() <= 78) {
			player.sendPopup("§7PVP 제한 거리를 벗어 났습니다.");
			player.teleport(this.find_spawn());
		} else if (player.getX() >= 178) {
			player.sendPopup("§7PVP 제한 거리를 벗어 났습니다.");
			player.teleport(this.find_spawn());
		} else if (player.getZ() >= 178) {
			player.sendPopup("§7PVP 제한 거리를 벗어 났습니다.");
			player.teleport(this.find_spawn());
		}
		return;
	}
	
	public void clear_sign(Level level) {
		BlockEntitySign sign = (BlockEntitySign) level.getBlockEntity(new Vector3(-30, 8, 1117));
		sign.setText("", "§e§lWall", "§7이 표지판을 눌러 참여", "");
	}
	
	public void update_sign_stay(Level level) {
		BlockEntitySign sign = (BlockEntitySign) level.getBlockEntity(new Vector3(-30, 8, 1117));
		if (Wall.players.size() == 0) {
			this.clear_sign(level);
			return;
		}
		String line4 = "§74명 이상이 모이면 시작합니다..";
		if (Wall.players.size() >= 4) {
			line4 = "§7잠시 뒤 경기가 시작됩니다..";
		}
		sign.setText("§e§lWall", "§7이 표지판을 눌러 참여", "§7현재 " + Wall.players.size()+ "명 대기중..", line4);
	}
	
	public void update_sign_life(Level level) {
		BlockEntitySign sign = (BlockEntitySign) level.getBlockEntity(new Vector3(-30, 8, 1117));
		String line4 = "§7재료수집 진행중...";
		if (Wall.is_pvp) {
			line4 = "§7PVP 진행중...";
		}
		sign.setText("§e§lWall", "§7경기 진행중...", "§7현재 " + Wall.players.size()+ "명 생존", line4);
	}
	
	public void update_sign(Level level) {
		BlockEntitySign sign = (BlockEntitySign) level.getBlockEntity(new Vector3(-30, 8, 1117));
		sign.setText("", "", "", "");
	}
	
	public void getoff_player(Player player) {
		wall_player wall_player = Wall.players.get(player.getName());
		if (Wall.players.size() == 1) {
			wall_player.is_winner = true;
		}
		if (Wall.in_game) {
			this.report_player_e(wall_player);
		}
		Wall.players.remove(player.getName());
		this.base.getoff_minigame(player);
	}
	
	public void respawn_player(Player player) {
		if (Wall.is_pvp) {
			return;
		}
		this.plugin.getServer().getScheduler().scheduleDelayedTask(new wall_respawn_task(this.find_spawn(), player, this.plugin), 5);
	}
	
	public Position find_spawn() {
		Wall.gamelevel = this.plugin.getServer().getLevelByName("-Wall");
		Position pos = Wildgame.world.findspawn_no_water(Wall.gamelevel);
		Position pos_2 = new Position(128, pos.getY()-2, 128, Wall.gamelevel);
		int id = Wall.gamelevel.getBlock(pos_2).getId();
		if((id == 8) || (id == 9)) {
			 pos_2.getLevel().setBlock(pos_2, new BlockGlass(), true, false);
		}
		Wall.pos = pos;
		return pos;
	}
	
	public void broadcastPopUP(String msg) {
		for (Map.Entry<String, wall_player> entry: Wall.players.entrySet()) {
			entry.getValue().getPlayer().sendTip(msg);
		}
	}
	
	public void broadcastTitle(String msg) {
		for (Map.Entry<String, wall_player> entry: Wall.players.entrySet()) {
			entry.getValue().getPlayer().sendTitle(msg);
		}
	}
	
	public void broadcastMsg(String msg) {
		for (Map.Entry<String, wall_player> entry: Wall.players.entrySet()) {
			entry.getValue().getPlayer().sendMessage("§7----------------");
			entry.getValue().getPlayer().sendMessage(msg);
			entry.getValue().getPlayer().sendMessage("§7----------------");
		}
	}
	
	public void clear_game() {
		this.clear_sign(Main.Mine24core.mainlevel);
		Wall.players.clear();
		Wall.is_pvp = false;
		Wall.player_count = 0;
		this.plugin.getServer().getScheduler().scheduleDelayedTask(new wall_clear_task(this, this.plugin), 5);
	}
	
	public void clear_5_game() {
		Wall.gamelevel = this.plugin.getServer().getLevelByName("-Wall");
		this.plugin.getServer().unloadLevel(Wall.gamelevel);
		Wall.in_game = false;
		Minigame_base.hot_join = 0;
		this.del_tempworld();
	}
	
	public void clear_count_down() {
		Minigame_base.wall_count_down_task.reset_task();
	}
	
	public void count_down_start() {
		Minigame_base.wall_count_down_task.count_down_30s();
	}
	
	public void pvp_count_down_start() {
		Minigame_base.wall_count_down_task.count_down_10s();
	}
	
	public void pvp_start_count() {
		Minigame_base.wall_count_down_task.count_down_10m();
	}
	
	public void start_report_game() {
		Wall.start_date = this.plugin.get_timestamp();
		Wall.players_all = new JSONObject();
		Wall.report_kd = new JSONObject();
	}
	
	@SuppressWarnings("unchecked")
	public void end_report_game(String winner, int type) {
		Wall.report_kd.put("index", Wall.kd_count);
		this.upload_report_game(winner, type);
		Wall.report_kd = null;
		Wall.players_all = null;
		Wall.kd_count = 0;
		Wall.start_date = null;
	}
	
	public void upload_report_game(String winner, int type) {
		String query = "INSERT INTO game_report(game, date, kill_death, winner, players, player_count, type) "
				+ "VALUES(1, "+ Wall.start_date+", '"+ Wall.report_kd.toJSONString()+"', '"+winner+"', '"+ Wall.players_all.toJSONString()+"', '"+ Wall.player_count+"', '"+type+"');";
		Main.mysqllib.execute(query);
	}
	
	@SuppressWarnings("unchecked")
	public void add_kill_line(String killer, String deather) {
		Wall.report_kd.put(Wall.kd_count, killer+"."+deather);
		Wall.kd_count++;
	}
	
	public void report_player_e(wall_player wall_player) {
		try {
			this.report_player(wall_player);
		} catch (SQLException e) {
			return;
		}
	}
	
	@SuppressWarnings("unchecked")
	public void report_player(wall_player wall_player) throws SQLException {
		String name = wall_player.getPlayer().getName().toLowerCase();
		String yaw1 = "1."+ Wall.start_date+"."+wall_player.is_winner;
		
		ResultSet sql_rs = this.base.get_player_report_data(name);
		
		int win_num = 0;
		int lose_num = 0;
		
		if (wall_player.is_winner) {
			win_num++;
		} else {
			lose_num++;
		}
		
		if (sql_rs != null) {
			
			JSONObject js_player = (JSONObject) JSONValue.parse(sql_rs.getString("last_game"));
			
			int kills = sql_rs.getInt("wall_kill")+wall_player.kills;
			int death = sql_rs.getInt("wall_death")+wall_player.deaths;
			int wins = sql_rs.getInt("wall_win")+win_num;
			int loses = sql_rs.getInt("wall_lose")+lose_num;
			
			int all_kills = sql_rs.getInt("kills")+wall_player.kills;
			int all_death = sql_rs.getInt("deaths")+wall_player.deaths;
			int all_win = sql_rs.getInt("win")+win_num;
			int all_lose = sql_rs.getInt("lose")+lose_num;
			
			String yaw2 = (String) js_player.get("1");
			String yaw3 = (String) js_player.get("2");
			
			JSONObject new_js_player = new JSONObject();
			new_js_player.put(1, yaw1);
			new_js_player.put(2, yaw2);
			new_js_player.put(3, yaw3);
			
			String query = "UPDATE player_game_report SET "
					+ "win="+all_win+", lose="+all_lose+", kills="+all_kills+", deaths="+all_death+", wall_kill="+kills+", wall_death="+death+", wall_win="+wins+", wall_lose="+loses+", last_game='"+new_js_player.toJSONString()+"' "
					+ "WHERE player='"+name+"';";
			Main.mysqllib.executeupdate(query);
		} else {
			JSONObject new_js_player = new JSONObject();
			new_js_player.put(1, yaw1);
			new_js_player.put(2, "NULL");
			new_js_player.put(3, "NULL");
			
			String query = "INSERT INTO player_game_report(player, win, lose, kills, deaths, wall_kill, wall_death, wall_win, wall_lose, last_game) "
					+ "VALUES ('"+name+"', "+win_num+", "+lose_num+", "+wall_player.kills+", "+wall_player.deaths+", "+wall_player.kills+", "+wall_player.deaths+", "+win_num+", "+lose_num+", '"+new_js_player.toJSONString()+"');";
			Main.mysqllib.execute(query);
		}
		
	}
}
