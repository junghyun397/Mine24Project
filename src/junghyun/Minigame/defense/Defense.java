package junghyun.Minigame.defense;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import junghyun.Minigame.wall.Wall;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import junghyun.Main;
import junghyun.Minigame.Minigame_base;
import junghyun.class_box.Mine24player;
import junghyun.class_box.defense_player;
import junghyun.task.defense_respawn_task;
import junghyun.timer.defense_count_down_task;

public class Defense {
	
	public Main plugin;
	
	public Minigame_base base;
	
	public static Map<String, defense_player> players = new HashMap<>();
	
	public static int top_score = 0;
	
	public static String top_player = "----";
	
	public final static Item sword = new Item(267, 0);
	public final static Item shield = new Item(341, 0);
	
	public static int map_type = 0;
	public final static Vector3[][] map_spawns = Defense.setup_spawns();
	
	public static boolean in_game = false;
	
	public static int player_count = 0;

	public static JSONObject players_all = null;

	public static JSONObject report_kd = null;;

	public static int kd_count = 0;

	public static String start_date = null;
	
	public Defense onEnable(Main plugin, Minigame_base base) {
		this.plugin = plugin;
		this.plugin.getLogger().info("미니게임 Defense 로딩중...");
		this.base = base;
		this.plugin.getLogger().info("미니게임 Defense 로딩완료....");
		return this;
	}
	
	public void join_game(Player player) {
		if (Defense.players.get(player.getName()) != null) {
			player.sendMessage("§7----------------");
			player.sendMessage("<§e미니게임§f> 이미 대기중 입니다.");
			player.sendMessage("§7----------------");
			return;
		}
		if (Defense.players.size() >= 20) {
			player.sendMessage("§7----------------");
			player.sendMessage("<§e미니게임§f> 미니게임에 입장할수 없습니다. 사람이 너무 많습니다.");
			player.sendMessage("§7----------------");
			return;
		}
		if (Defense.in_game) {
			player.sendMessage("§7----------------");
			player.sendMessage("<§e미니게임§f> 미니게임이 진행중 입니다. 잠시 기다려 주시기 바랍니다.");
			player.sendMessage("§7----------------");
			return;
		}
		defense_player def_player = new defense_player();
		def_player.player = player;
		Defense.players.put(player.getName(), def_player);
		this.update_sign_stay(Main.Mine24core.mainlevel);
		String msg_list = " ";
		for (Map.Entry<String, defense_player> entry: Defense.players.entrySet()) {
			msg_list = msg_list+(entry.getValue().getPlayer().getName())+" ";
		}
		player.sendMessage("§7----------------");
		player.sendMessage("<§e미니게임§f> 미니게임 Defense 에 참여 하셨습니다.");
		player.sendMessage("<§e미니게임§f> 현재 대기중인 인원 -" + msg_list);
		player.sendMessage("§7----------------");
		this.broadcastPopUP("§7"+player.getName()+"님이 참여 하셨습니다.");
		player.teleport(new Position(-65, 6, 1264, Main.Mine24core.mainlevel));
		this.setup_player(player);
		if (Defense.players.size() == 4) {
			this.pvp_count_down_start();
		}
		return;
	}
	
	public void setup_player(Player player) {
		Mine24player player_box = Main.players_.get(player.getName());
		player_box.is_minigame = true;
		player_box.minigame_type = 2; //Defense
		player_box.backup_inv(player);
		
		this.base.set_clear(player);
		
		Main.players_.put(player.getName(), player_box);
		Main.popup_task.off_popup(player.getName());
	}
	
	public void set_hotbar(Player player) {
		int[] hotbar = new int[player.getInventory().getHotbarSize()];
		int size_1 = (int) Math.floor(hotbar.length/2);
		
		player.getInventory().addItem(Defense.sword);
		player.getInventory().addItem(Defense.shield);
		
		for (int i = 0; i < size_1; i++) {
            player.getInventory().setHotbarSlotIndex(hotbar[i], 0);
        }
		
		for (int i = (size_1+1); i < hotbar.length; i++) {
            player.getInventory().setHotbarSlotIndex(hotbar[i], 1);
        }
	}
	
	public void start_game() {
		String msg_list = " ";
		this.start_report_game();
		
		Defense.map_type = Main.rand.nextInt(3)+1;
		Defense.in_game = true;
		
		for (Entry<String, defense_player> entry: Defense.players.entrySet()) {
			msg_list = msg_list+entry.getKey()+" ";
		}
		for (Map.Entry<String, defense_player> entry: Defense.players.entrySet()) {
			Player player = this.plugin.getServer().getPlayer(entry.getKey());
			Mine24player player_box = Main.players_.get(entry.getKey());
			player_box.can_pvp = true;
			player_box.bossbar_id = player.createBossBar("\n\n게임 종료까지 §e5분§f 남았습니다.", 100);
			Main.players_.put(entry.getKey(), player_box);
			this.set_hotbar(player);
			player.teleport(this.rand_spawn());
			
			player.sendMessage("§7----------------");
			player.sendMessage("<§e미니게임§f> 게임이 시작 되었습니다! 다른플레이어를 죽여 점수를 획득 하세요."
					+ "\n<§e미니게임§f> 참여 인원  : " + msg_list);
			player.sendMessage("§7----------------");
			
		}
		Defense.player_count = Defense.players.size();
		this.update_sign_life(Main.Mine24core.mainlevel);
		this.count_down_start();
	}

	public void reboot_exit() {
		if (Defense.in_game) {
			for (Map.Entry<String, defense_player> entry: Defense.players.entrySet()) {
				this.getoff_player(entry.getValue().getPlayer());
			}
		}
	}
	
	public void go_exit(Player player) {
		if (!Defense.in_game) {
			this.getoff_player(player);
			this.update_sign_stay(Main.Mine24core.mainlevel);
			return;
		}
		this.getoff_player(player);
		this.broadcastMsg("<§e미니게임§f> "+player.getName()+" 님이 탈주 하셨습니다."
				+ "\n<§e미니게임§f> 현재 "+ Defense.players.size()+"명의 플레이어가 남아 있습니다.");
		this.update_sign_life(Main.Mine24core.mainlevel);
		if (Defense.players.size() == 1) { //플레이어 부족 경기 취소
			Player winner = null;
			for (Map.Entry<String, defense_player> entry: Defense.players.entrySet()) {
				winner = entry.getValue().getPlayer();
			}
			winner.sendMessage("<§e미니게임§f> 플레이어 부족으로 진행이 불가능 합니다.");
			winner.sendMessage("<§e미니게임§f> 경기가 취소 되었습니다. 스폰으로 이동 됩니다...");
			winner.sendMessage("§7----------------");
			winner.teleport(Main.main_level_pos);
			this.getoff_player(winner);
			this.clear_game();
		}
	}
	
	public void clear_game() {
		Defense.in_game = false; //초기화
		Defense.top_player = "----";
		Defense.top_score = 0;
		Defense.player_count = 0;
		Defense.players.clear();
		this.clear_sign(Main.Mine24core.mainlevel);
	}

	public void move_pvp(Player player) {
//		if (player.getY() > 100) {
//			player.sendPopup("§7장난해요?");
//			player.teleport(this.rand_spawn());
//		} else if (player.getX() <= -262) {
//			player.sendPopup("§7제한 거리를 벗어 났습니다.");
//			player.teleport(this.rand_spawn());
//		} else if (player.getZ() <= 1383) {
//			player.sendPopup("§7제한 거리를 벗어 났습니다.");
//			player.teleport(this.rand_spawn());
//		} else if (player.getX() >= -208) {
//			player.sendPopup("§7제한 거리를 벗어 났습니다.");
//			player.teleport(this.rand_spawn());
//		} else if (player.getZ() >= 1437) {
//			player.sendPopup("§7제한 거리를 벗어 났습니다.");
//			player.teleport(this.rand_spawn());
//		}
		return;
	}
	
	public void kill_player(Player killer, Player deather) {
		defense_player box_killer = Defense.players.get(killer.getName());
		defense_player box_deather = Defense.players.get(deather.getName());
		
		box_killer.onkill();
		box_deather.ondeath();
		
		if (Defense.top_score < box_killer.score) {
			Defense.top_player = killer.getName();
			Defense.top_score = box_killer.score;
		}
		Defense.players.put(killer.getName(), box_killer);
		Defense.players.put(deather.getName(), box_deather);
		
		killer.sendTitle("§e"+deather.getName()+"님§7 사살!");
		killer.sendTip("§e"+box_killer.kill_chain+"§7연속 사살중");
		
		deather.sendTitle("§e"+killer.getName()+"님§7 에게 죽었습니다!");
		deather.sendTip("§e"+box_deather.death_chain+"§7연속 사망중");
		
		this.broadcast_pvp_Msg("§6"+killer.getName()+"님§7이 §5"+deather.getName()+"님§7을 사살 하였습니다.");
		
		if (box_killer.kill_chain == 3) {
			this.broadcast_pvp_Msg("§6"+killer.getName()+"님§7이 §63연속 사살§7을 달성 하였습니다.");
			killer.sendTip("§7연승 §e추가체력 20§7을 받았습니다.");
			killer.setHealth(20);
		} else if (box_killer.kill_chain == 10) {
			this.broadcast_pvp_Msg("§6"+killer.getName()+"님§7이 §610연속 사살§7을 달성 하였습니다.");
			killer.sendTip("§7연승 §e추가체력 40§7을 받았습니다.");
			killer.setHealth(40);
		}
		
		if (box_deather.death_chain == 3) {
			this.broadcast_pvp_Msg("§5"+deather.getName()+"님§7이 3연속으로 죽고 있습니다.");
		}
		
		this.update_bossbar(this.getbossbar_present());
	}
	
	public void hit_player(Player dmg_hit, Player dmg_in) {
		defense_player box_in = Defense.players.get(dmg_in.getName());
		box_in.onhit(dmg_hit);
		Defense.players.put(dmg_in.getName(), box_in);
	}
	
	public void drop_death(Player player) {
		defense_player box_drop = Defense.players.get(player.getName());
		if (box_drop.last_hit == null) {
			return;
		}
		this.kill_player(box_drop.last_hit, player);
	}
	
	public void respawn_player(Player player) {
		this.plugin.getServer().getScheduler().scheduleDelayedTask(new defense_respawn_task(this.find_spawn(), player, this.plugin), 5);
	}
	
	public Position find_spawn() {
		return this.rand_spawn();
	}

	public int getbossbar_present() {
		return (int) (5.0-(Math.floor(defense_count_down_task.seconds/60)));
	}
	
	public void getoff_player(Player player) {
		defense_player defense_player = Defense.players.get(player.getName());
		if (Defense.players.size() == 1) {
			defense_player.is_winner = true;
		}
		if (Defense.in_game) {
			this.report_player_e(defense_player);
		}
		Defense.players.remove(player.getName());
		this.base.getoff_minigame(player);
	}
	
	public Position rand_spawn() {
//		Random random = new Random();
//		int cut_x = Math.abs(random.nextInt(46));
//		int cut_y = Math.abs(random.nextInt(46));
//		Position pos = new Position(-258+cut_x, 9, 1387+cut_y, Main.main_level.getLevel());
		Vector3 rand_pos = Defense.map_spawns[Defense.map_type][Math.abs(Main.rand.nextInt(29))];
		return new Position(rand_pos.getX(), rand_pos.getY(), rand_pos.getZ(), Main.main_level);
	}
	
	
	public void broadcastPopUP(String msg) {
		for (Map.Entry<String, defense_player> entry: Defense.players.entrySet()) {
			entry.getValue().getPlayer().sendTip(msg);
		}
	}
	
	public void broadcastTitle(String msg) {
		for (Map.Entry<String, defense_player> entry: Defense.players.entrySet()) {
			entry.getValue().getPlayer().sendTitle(msg);
		}
	}
	
	public void broadcastMsg(String msg) {
		for (Map.Entry<String, defense_player> entry: Defense.players.entrySet()) {
			entry.getValue().getPlayer().sendMessage("§7----------------");
			entry.getValue().getPlayer().sendMessage(msg);
			entry.getValue().getPlayer().sendMessage("§7----------------");
		}
	}
	
	public void broadcast_pvp_Msg(String msg) {
		for (Map.Entry<String, defense_player> entry: Defense.players.entrySet()) {
			entry.getValue().getPlayer().sendMessage(msg);
		}
	}
	
	public void update_bossbar(int time) {
		for (Map.Entry<String, defense_player> entry: Defense.players.entrySet()) {
			Player player = this.plugin.getServer().getPlayer(entry.getKey());
			Mine24player player_box = Main.players_.get(entry.getKey());
			player.updateBossBar("\n\n§7게임 종료까지 §e"+time+"분§7 남았습니다.\n"
					+ "§7현재 획득 포인트 : §e" + entry.getValue().score + "점\n"
					+ "§71위와의 격차 : §e" + (Defense.top_score-entry.getValue().score) + "점\n"
					+ "§7현재 1위  : §e" + Defense.top_player,
					(20*time), player_box.bossbar_id);
		}
	}
	
	public void end_game() {
		Player winner = null;
		for (Map.Entry<String, defense_player> entry: Defense.players.entrySet()) {
			String name = entry.getValue().getPlayer().getName();
			Player player = entry.getValue().getPlayer();
			if (Defense.top_player.equals(name)) {
				winner = player;
				winner.sendMessage("§7----------------");
				winner.sendMessage("<§e미니게임§f> 축하드립니다! 최고 득점자로 우승 하셨습니다.");
				winner.sendMessage("<§e미니게임§f> 총 " + entry.getValue().score + "점을 획득 하셨습니다." );
				winner.sendMessage("<§e미니게임§f> "+ Defense.player_count+"명과 경기를 벌여 우승 하셨습니다. 총 "+ Defense.player_count*200+" OP 의 상금을 얻었습니다.");
				winner.sendMessage("§7----------------");
				winner.teleport(Main.main_level_pos);
				Main.Economy24.addmoney(winner.getName().toLowerCase(), (Defense.player_count*200));
			} else {
				player.sendMessage("§7----------------");
				player.sendMessage("<§e미니게임§f> "+ Defense.top_player+" 님이 경기에서 우승 하셨습니다.");
				player.sendMessage("<§e미니게임§f> 경기에서 지셨습니다. 다음에 다시 도전 해보세요. :D");
				player.sendMessage("<§e미니게임§f> 총 " + entry.getValue().score + "점을 획득 하셨습니다.");
				player.sendMessage("<§e미니게임§f> 우승자와 " + (Defense.top_score-entry.getValue().score) + "점의 차이로 지셨습니다.");
				player.sendMessage("§7----------------");
				player.teleport(Main.main_level_pos);
			}
			this.base.getoff_minigame(player);
		}
		
		this.clear_game();
	}
	
	public void clear_sign(Level level) {
		BlockEntitySign sign = (BlockEntitySign) level.getBlockEntity(new Vector3(-83, 8, 1117));
		sign.setText("", "§e§lDefense", "§7이 표지판을 눌러 참여", "");
	}
	
	public void update_sign_stay(Level level) {
		BlockEntitySign sign = (BlockEntitySign) level.getBlockEntity(new Vector3(-83, 8, 1117));
		if (Defense.players.size() == 0) {
			this.clear_sign(level);
			return;
		}
		String line4 = "§74명 이상이 모이면 시작합니다..";
		if (Defense.players.size() >= 4) {
			line4 = "§7잠시 뒤 경기가 시작됩니다..";
		}
		sign.setText("§e§lDefense", "§7이 표지판을 눌러 참여", "§7현재 " + Defense.players.size()+ "명 대기중..", line4);
	}
	
	public void update_sign_life(Level level) {
		BlockEntitySign sign = (BlockEntitySign) level.getBlockEntity(new Vector3(-83, 8, 1117));
		sign.setText("§e§lDefense", "§7경기 진행중...", "§7현재 " + Defense.players.size()+ "명 플레이중", "");
	}
	
	public void update_sign(Level level) {
		BlockEntitySign sign = (BlockEntitySign) level.getBlockEntity(new Vector3(-83, 6, 1117));
		sign.setText("", "", "", "");
	}
	
	public void clear_count_down() { // clear
		Minigame_base.defense_count_down_task.reset_task();
	}
	
	public void pvp_count_down_start() { // 30s 
		Minigame_base.defense_count_down_task.count_down_30s();
	}
	
	public void count_down_start() { // 5m
		Minigame_base.defense_count_down_task.count_down_5m();
	}
	
	public void start_report_game() {
		Defense.start_date = this.plugin.get_timestamp();
		Defense.players_all = new JSONObject();
		Defense.report_kd = new JSONObject();
	}
	
	@SuppressWarnings("unchecked")
	public void end_report_game(String winner, int type) {
		Defense.report_kd.put("index", Defense.kd_count);
		this.upload_report_game(winner, type);
		Defense.report_kd = null;
		Defense.players_all = null;
		Defense.kd_count = 0;
		Defense.start_date = null;
	}
	
	public void upload_report_game(String winner, int type) {
		String query = "INSERT INTO game_report(game, date, kill_death, winner, players, player_count, type) "
				+ "VALUES(2, "+ Defense.start_date+", '"+ Defense.report_kd.toJSONString()+"', '"+winner+"', '"+ Defense.players_all.toJSONString()+"', '"+ Defense.player_count+"', '"+type+"');";
		Main.mysqllib.execute(query);
	}
	
	@SuppressWarnings("unchecked")
	public void add_kill_line(String killer, String deather) {
		Defense.report_kd.put(Defense.kd_count, killer+"."+deather);
		Defense.kd_count++;
	}
	
	public void report_player_e(defense_player defense_player) {
		try {
			this.report_player(defense_player);
		} catch (SQLException e) {
			return;
		}
	}
	
	@SuppressWarnings("unchecked")
	public void report_player(defense_player defense_player) throws SQLException {
		String name = defense_player.getPlayer().getName().toLowerCase();
		String yaw1 = "2."+ Wall.start_date+"."+defense_player.is_winner;
		
		ResultSet sql_rs = this.base.get_player_report_data(name);
		
		int win_num = 0;
		int lose_num = 0;
		
		if (defense_player.is_winner) {
			win_num++;
		} else {
			lose_num++;
		}
		
		if (sql_rs != null) {
			
			JSONObject js_player = (JSONObject) JSONValue.parse(sql_rs.getString("last_game"));
			
			int kills = sql_rs.getInt("defense_kill")+defense_player.kills;
			int death = sql_rs.getInt("defense_death")+defense_player.deaths;
			int wins = sql_rs.getInt("defense_win")+win_num;
			int loses = sql_rs.getInt("defense_lose")+lose_num;
			
			int all_kills = sql_rs.getInt("kills")+defense_player.kills;
			int all_death = sql_rs.getInt("deaths")+defense_player.deaths;
			int all_win = sql_rs.getInt("win")+win_num;
			int all_lose = sql_rs.getInt("lose")+lose_num;
			
			String yaw2 = (String) js_player.get("1");
			String yaw3 = (String) js_player.get("2");
			
			JSONObject new_js_player = new JSONObject();
			new_js_player.put(1, yaw1);
			new_js_player.put(2, yaw2);
			new_js_player.put(3, yaw3);
			
			String query = "UPDATE player_game_report SET "
					+ "win="+all_win+", lose="+all_lose+", kills="+all_kills+", deaths="+all_death+", defense_kill="+kills+", defense_death="+death+", defense_win="+wins+", defense_lose="+loses+", last_game='"+new_js_player.toJSONString()+"' "
					+ "WHERE player='"+name+"';";
			Main.mysqllib.executeupdate(query);
		} else {
			JSONObject new_js_player = new JSONObject();
			new_js_player.put(1, yaw1);
			new_js_player.put(2, "NULL");
			new_js_player.put(3, "NULL");
			
			String query = "INSERT INTO player_game_report(player, win, lose, kills, deaths, defense_kill, defense_death, defense_win, defense_lose, last_game) "
					+ "VALUES ('"+name+"', "+win_num+", "+lose_num+", "+defense_player.kills+", "+defense_player.deaths+", "+defense_player.kills+", "+defense_player.deaths+", "+win_num+", "+lose_num+", '"+new_js_player.toJSONString()+"');";
			Main.mysqllib.execute(query);
		}
		
	}
	
	public static Vector3[][] setup_spawns() {
		Vector3[][] spawns = new Vector3[5][30];
		
		// 0, 무명
		
		spawns[0][0] = new Vector3(-294, 6, 1425);
		spawns[0][1] = new Vector3(0, 0, 0);
		spawns[0][2] = new Vector3(0, 0, 0);
		spawns[0][3] = new Vector3(0, 0, 0);
		spawns[0][4] = new Vector3(0, 0, 0);
		spawns[0][5] = new Vector3(0, 0, 0);
		spawns[0][6] = new Vector3(0, 0, 0);
		spawns[0][7] = new Vector3(0, 0, 0);
		spawns[0][8] = new Vector3(0, 0, 0);
		spawns[0][9] = new Vector3(0, 0, 0);
		spawns[0][10] = new Vector3(0, 0, 0);
		spawns[0][11] = new Vector3(0, 0, 0);
		spawns[0][12] = new Vector3(0, 0, 0);
		spawns[0][13] = new Vector3(0, 0, 0);
		spawns[0][14] = new Vector3(0, 0, 0);
		spawns[0][15] = new Vector3(0, 0, 0);
		spawns[0][16] = new Vector3(0, 0, 0);
		spawns[0][17] = new Vector3(0, 0, 0);
		spawns[0][18] = new Vector3(0, 0, 0);
		spawns[0][19] = new Vector3(0, 0, 0);
		spawns[0][20] = new Vector3(0, 0, 0);
		spawns[0][21] = new Vector3(0, 0, 0);
		spawns[0][22] = new Vector3(0, 0, 0);
		spawns[0][23] = new Vector3(0, 0, 0);
		spawns[0][24] = new Vector3(0, 0, 0);
		spawns[0][25] = new Vector3(0, 0, 0);
		spawns[0][26] = new Vector3(0, 0, 0);
		spawns[0][27] = new Vector3(0, 0, 0);
		spawns[0][28] = new Vector3(0, 0, 0);
		spawns[0][29] = new Vector3(0, 0, 0);
		
		//1, 놀이터
		
		spawns[1][0] = new Vector3(-257, 8, 1431 );
		spawns[1][1] = new Vector3(-256, 8, 1418 );
		spawns[1][2] = new Vector3(-258, 8, 1409 );
		spawns[1][3] = new Vector3(-258, 8, 1401 );
		spawns[1][4] = new Vector3(-251, 8, 1398 );
		spawns[1][5] = new Vector3(-255, 8, 1387 );
		spawns[1][6] = new Vector3(-251, 13, 1390 );
		spawns[1][7] = new Vector3(-244, 13, 1392 );
		spawns[1][8] = new Vector3(-251, 13, 1405 );
		spawns[1][9] = new Vector3(-246, 13, 1410 );
		spawns[1][10] = new Vector3(-236, 13, 1408 );
		spawns[1][11] = new Vector3(-226, 13, 1406 );
		spawns[1][12] = new Vector3(-218, 13, 1410 );
		spawns[1][13] = new Vector3(-218, 15, 1413 );
		spawns[1][14] = new Vector3(-213, 13, 1415 );
		spawns[1][15] = new Vector3(-220, 20, 1429 );
		spawns[1][16] = new Vector3(-227, 20, 1422 );
		spawns[1][17] = new Vector3(-225, 20, 1415 );
		spawns[1][18] = new Vector3(-232, 21, 1408 );
		spawns[1][19] = new Vector3(-244, 21, 1410 );
		spawns[1][20] = new Vector3(-251, 21, 1412 );
		spawns[1][21] = new Vector3(-250, 19, 1423 );
		spawns[1][22] = new Vector3(-243, 19, 1430 );
		spawns[1][23] = new Vector3(-236, 20, 1428 );
		spawns[1][24] = new Vector3(-215, 8, 1415 );
		spawns[1][25] = new Vector3(-228, 8, 1420 );
		spawns[1][26] = new Vector3(-226, 13, 1428 );
		spawns[1][27] = new Vector3(-243, 13, 1429 );
		spawns[1][28] = new Vector3(-246, 8, 1418 );
		spawns[1][29] = new Vector3(-233, 8, 1431 );
		
		//2, 해저
		
		spawns[2][0] = new Vector3(-294, 6, 1425 );
		spawns[2][1] = new Vector3(-300, 7, 1418 );
		spawns[2][2] = new Vector3(-301, 7, 1428 );
		spawns[2][3] = new Vector3(-310, 6, 1424 );
		spawns[2][4] = new Vector3(-310, 6, 1434 );
		spawns[2][5] = new Vector3(-315, 7, 1427 );
		spawns[2][6] = new Vector3(-322, 6, 1433 );
		spawns[2][7] = new Vector3(-329, 7, 1426 );
		spawns[2][8] = new Vector3(-323, 6, 1418 );
		spawns[2][9] = new Vector3(-316, 13, 1426 );
		spawns[2][10] = new Vector3(-317, 6, 1410 );
		spawns[2][11] = new Vector3(-307, 6, 1400 );
		spawns[2][12] = new Vector3(-307, 10, 1415 );
		spawns[2][13] = new Vector3(-298, 6, 1410 );
		spawns[2][14] = new Vector3(-298, 7, 1404 );
		spawns[2][15] = new Vector3(-297, 13, 1392 );
		spawns[2][16] = new Vector3(-306, 9, 1393 );
		spawns[2][17] = new Vector3(-312, 9, 1386 );
		spawns[2][18] = new Vector3(-320, 6, 1391 );
		spawns[2][19] = new Vector3(-323, 6, 1399 );
		spawns[2][20] = new Vector3(-328, 9, 1403 );
		spawns[2][21] = new Vector3(-329, 10, 1408 );
		spawns[2][22] = new Vector3(-337, 6, 1402 );
		spawns[2][23] = new Vector3(-333, 6, 1388 );
		spawns[2][24] = new Vector3(-337, 21, 1418 );
		spawns[2][25] = new Vector3(-341, 29, 1426 );
		spawns[2][26] = new Vector3(-329, 22, 1426 );
		spawns[2][27] = new Vector3(-337, 8, 1420 );
		spawns[2][28] = new Vector3(-335, 8, 1432 );
		spawns[2][29] = new Vector3(-335, 22, 1432);
		
		//3, 얼음
		
		spawns[3][0] = new Vector3( -131, 15, 1391);
		spawns[3][1] = new Vector3(-135, 19, 1393);
		spawns[3][2] = new Vector3(-141, 24, 1388);
		spawns[3][3] = new Vector3(-152, 19, 1389);
		spawns[3][4] = new Vector3(-158, 21, 1398);
		spawns[3][5] = new Vector3(-126, 19, 1386);
		spawns[3][6] = new Vector3(-126, 19, 1414);
		spawns[3][7] = new Vector3(-131, 11, 1418);
		spawns[3][8] = new Vector3(-132, 14, 1429);
		spawns[3][9] = new Vector3(-133, 19, 1430);
		spawns[3][10] = new Vector3(-140, 21, 1427);
		spawns[3][11] = new Vector3(-138, 19, 1415);
		spawns[3][12] = new Vector3(-142, 23, 1411);
		spawns[3][13] = new Vector3(-142, 21, 1404);
		spawns[3][14] = new Vector3(-148, 19, 1406);
		spawns[3][15] = new Vector3(-149, 12, 1395);
		spawns[3][16] = new Vector3(-149, 12, 1407);
		spawns[3][17] = new Vector3(-144, 12, 1418);
		spawns[3][18] = new Vector3(-135, 12, 1404);
		spawns[3][19] = new Vector3(-150, 20, 1434);
		spawns[3][20] = new Vector3(-150, 12, 1422);
		spawns[3][21] = new Vector3(-154, 21, 1421);
		spawns[3][22] = new Vector3(-156, 12, 1428);
		spawns[3][23] = new Vector3(-165, 20, 1430);
		spawns[3][24] = new Vector3(-164, 19, 1418);
		spawns[3][25] = new Vector3(-163, 12, 1416);
		spawns[3][26] = new Vector3(-174, 22, 1412);
		spawns[3][27] = new Vector3(-168, 22, 1406);
		spawns[3][28] = new Vector3(-168, 21, 1399);
		spawns[3][29] = new Vector3(-156, 22, 1403);
		
		//4, 무명
		
		spawns[4][0] = new Vector3(-294, 6, 1425);
		spawns[4][1] = new Vector3(0, 0, 0);
		spawns[4][2] = new Vector3(0, 0, 0);
		spawns[4][3] = new Vector3(0, 0, 0);
		spawns[4][4] = new Vector3(0, 0, 0);
		spawns[4][5] = new Vector3(0, 0, 0);
		spawns[4][6] = new Vector3(0, 0, 0);
		spawns[4][7] = new Vector3(0, 0, 0);
		spawns[4][8] = new Vector3(0, 0, 0);
		spawns[4][9] = new Vector3(0, 0, 0);
		spawns[4][10] = new Vector3(0, 0, 0);
		spawns[4][11] = new Vector3(0, 0, 0);
		spawns[4][12] = new Vector3(0, 0, 0);
		spawns[4][13] = new Vector3(0, 0, 0);
		spawns[4][14] = new Vector3(0, 0, 0);
		spawns[4][15] = new Vector3(0, 0, 0);
		spawns[4][16] = new Vector3(0, 0, 0);
		spawns[4][17] = new Vector3(0, 0, 0);
		spawns[4][18] = new Vector3(0, 0, 0);
		spawns[4][19] = new Vector3(0, 0, 0);
		spawns[4][20] = new Vector3(0, 0, 0);
		spawns[4][21] = new Vector3(0, 0, 0);
		spawns[4][22] = new Vector3(0, 0, 0);
		spawns[4][23] = new Vector3(0, 0, 0);
		spawns[4][24] = new Vector3(0, 0, 0);
		spawns[4][25] = new Vector3(0, 0, 0);
		spawns[4][26] = new Vector3(0, 0, 0);
		spawns[4][27] = new Vector3(0, 0, 0);
		spawns[4][28] = new Vector3(0, 0, 0);
		spawns[4][29] = new Vector3(0, 0, 0);
		
		return spawns;
	}
	
}
