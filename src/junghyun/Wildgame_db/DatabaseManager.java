package junghyun.Wildgame_db;

import java.sql.ResultSet;
import java.sql.SQLException;
import static java.lang.Math.toIntExact;

import org.json.simple.*;

import junghyun.db.MysqlLib;
import junghyun.Wildgame.Wildgame;

/**
 * @author junghyun
 *
 */

public class DatabaseManager {

	Wildgame plugin;

	public MysqlLib mysqllib = new MysqlLib();

	public DatabaseManager(Wildgame plugin) {
		this.plugin = plugin;
		this.chackdelworld();
	}

	public String addworldextend(int weeks, String level) {
		String old_time = this.getworlddeltime(level);
		int lenght = old_time.length();
		
		int day = Integer.parseInt(old_time.substring(5, lenght)); // 2014-200
		int year = Integer.parseInt(old_time.substring(0, 4));
		if ((day+(7*weeks)) > 365) {
			String del_time = year+1 + "-" + ((day+(7*weeks))-365);
			String query = "UPDATE worlds_info SET del_time='"+del_time+"' WHERE level_name = '"+level.toLowerCase()+"';";
			this.mysqllib.execute(query);
			return del_time;
		}
		String del_time = year + "-" + (day+(7*weeks));
		String query = "UPDATE worlds_info SET del_time='"+del_time+"' WHERE level_name = '"+level.toLowerCase()+"';";
		this.mysqllib.execute(query);
		return del_time;
	}
	
	public boolean getplayercanjoin(String player, String level) {
		ResultSet result = this.getworlddata(level);
		int type = 0;
		try {
			if (result.next()) {
				type = result.getInt("locked");
			}
		} catch (SQLException e) {
			return false;
		}
		if (type == 0) {
			return true;
		}
		if (type == 2) {
			if (level.equals(player)) {
				return true;
			}
			return false;
		}
		String[] players = this.getworldplayer(level); // 플레이어 리스트를 얻어옴
		int length = players.length; // 길이를 얻어옴
		for (int i = 0; i <= length - 1; i++) { // 길이만큼 반복
			if (players[i].equals(player)) { // 같은게 바온다면 바로 리턴
				return true;
			}
		}
		return false;
	}
	
	public void setworldlocked(String level, int type) {
		String query = null;
		if (type == 0) {
			query = "UPDATE worlds_info SET locked = null WHERE level_name = '"+level+"';";
		} else if (type == 1) {
			query = "UPDATE worlds_info SET locked = 1 WHERE level_name = '"+level+"';";
		} else if (type == 2) {
			query = "UPDATE worlds_info SET locked = 2 WHERE level_name = '"+level+"';";
		}
		this.mysqllib.executeupdate(query);
	}
	
	@SuppressWarnings("unchecked")
	public void setworldspawn(String level, double x, double y, double z) {
		JSONObject spawn_js = new JSONObject();
		spawn_js.put("x", x);
		spawn_js.put("y", y);
		spawn_js.put("z", z);
		String query = "UPDATE worlds_info SET spawn='"+spawn_js.toJSONString()+"' WHERE level_name = '"+level.toLowerCase()+"';";
		this.mysqllib.execute(query);
		return;
	}
	
	public String getworlddeltime(String level) {
		ResultSet result = this.getworlddata(level);
		try {
			if (result.next()) {
				return result.getString("del_time");
			}
		} catch (SQLException e) {
		}
		return null;
	}

	public ResultSet getplayerdata(String player) {
		String query = "SELECT * FROM player_worlds WHERE level_name = '" + player.toLowerCase() + "';";
		return this.mysqllib.executequery(query);
	}

	public ResultSet getworlddata(String level) {
		String query = "SELECT * FROM worlds_info WHERE level_name = '" + level.toLowerCase() + "';";
		return this.mysqllib.executequery(query);
	}

	/**
	 * @param level
	 * @return 월드의 끝 지점을을 반환
	 */
	public int[] getworldpoints(String level) {
		int size = this.getworldsize(level);
		int[] pointion = new int[5];
		pointion[0] = 128 - size; // x
		pointion[1] = 128 - size; // y
		pointion[2] = 128 + size; // x2
		pointion[3] = 128 + size; // y2
		pointion[4] = 0;
		return pointion;
	}


	/**
	 * @param level
	 * @param player
	 * 월드에 플레이어를 추가
	 */
	@SuppressWarnings("unchecked")
	public boolean addworldplayer(String level, String player) {
		JSONObject worldjson = this.getworldplayer_js(level);
		if (worldjson.containsValue(player)) {
			return false;
		}
		int index = toIntExact((long) worldjson.get("index"));
		worldjson.put("index", index + 1);
		worldjson.put(index + 1, player);
		String query = "UPDATE worlds_info SET players = '" + worldjson.toJSONString() + "' WHERE level_name = '" + level.toLowerCase() + "'";
		this.mysqllib.executeupdate(query);
		return true;
	}
	
	public String getindexname(String name) {
		String query = "SELECT * FROM worlds_info WHERE index_name = '"+name+"';";
		ResultSet rs = this.mysqllib.executequery(query);
		try {
			if (rs.next()) {
				String level_name = rs.getString("level_name");
				return level_name;
			}
		} catch (SQLException e) {
			return null;
		}
		return null;
	}
	
	public void setindexname(String level, String name) {
		String query = "UPDATE worlds_info SET index_name = '"+name+"' WHERE level_name = '"+level+"';";
		this.mysqllib.executeupdate(query);
	}
	
	
	
	/**
	 * @param level
	 * @param player
	 * 월드에 플레이어를 제거
	 */
	@SuppressWarnings("unchecked")
	public boolean delworldplayer(String level, String player) {
		JSONObject worldjson = this.getworldplayer_js(level); // 플레이어 목록을 불러옴
		if (!worldjson.containsValue(player)) {
			return false;
		}
		int index = toIntExact((long) worldjson.get("index")); // 색인 지정
		int del_players = 0;
		int new_players = 0;
		String target_index;
		JSONObject players_js = new JSONObject();
		for (int i = 1; i <= index; i++) {
			target_index = (String) worldjson.get(Integer.toString(i));
			if (target_index.equals(player)) {
				del_players++;
				worldjson.remove(Integer.toString(i));
			} else {
				new_players++;
				players_js.put(new_players, target_index);
			}
		}
		if (del_players == 0) {
			return false;
		}
		players_js.put("index", new_players);
		String query = "UPDATE worlds_info SET players = '" + players_js.toJSONString() + "' WHERE level_name = '" + level.toLowerCase() + "'";
		this.mysqllib.executeupdate(query);
		return true;
	}
	
	/**
	 * @param level
	 * @return 월드를 사용 가능한 플레이어 목록
	 */
	public String[] getworldplayer(String level) {
		JSONObject result = this.getworldplayer_js(level);
		int index = toIntExact((long) result.get("index"));
		String[] players = new String[index];
		for (int i = 0; i <= index-1; i++) {
			players[i] = (String) result.get(Integer.toString(i+1));
		}
		return players;
	}

	public JSONObject getworldplayer_js(String level) {
		ResultSet result = this.getworlddata(level);
		String json_string = null;
		try {
			if (result.next()) {
				json_string = result.getString("players");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		JSONObject object = (JSONObject) JSONValue.parse(json_string);
		return object;
	}

	/**
	 * @param level
	 * 월드를 생성
	 */
	@SuppressWarnings("unchecked")
	public void createworlddata(String level) {
		String now_time = this.plugin.getnowdate();
		String del_time = this.plugin.getnowyear() + "-" + (this.plugin.getnowday()+7);
		JSONObject players_js = new JSONObject();
		players_js.put("index", 1);
		players_js.put(1, level);
		String players = players_js.toJSONString();
		String query = "INSERT INTO worlds_info(level_name, add_time, del_time, size, players) "
				+ "VALUES('"+level.toLowerCase()+"', '"+now_time+"', '"+del_time+"', 50, '"+players+"');";
		this.plugin.plugin.getLogger().info("새 월드 생성, 쿼리문 " + query);
		this.mysqllib.execute(query);
		return;
	}
	
	@SuppressWarnings("unchecked")
	public void sharereset(String level) {
		JSONObject players_js = new JSONObject();
		players_js.put("index", 1);
		players_js.put(1, level);
		String players = players_js.toJSONString();
		this.mysqllib.executeupdate("UPDATE worlds_info SET players='"+players+"' WHERE level_name = '"+level+"';");
		return;
	}

	/**
	 * 삭제할 월드 검색
	 */
	public void chackdelworld() {
		
		String del_time = this.plugin.getnowdate();
		String query = "SELECT * FROM worlds_info WHERE del_time = '"+del_time+"';";
		ResultSet result = this.mysqllib.executequery(query);
		this.plugin.plugin.log_info("기준일 " + del_time + "삭제 준비...");
		int i = 0;
		try {
			while (result.next()) {
				i++;
				this.delworlddata(result.getString("level_name"));
				this.plugin.plugin.getLogger().info("월드 기한만료 " + result.getString("level_name") + "님의 월드 삭제 준비...");
				Wildgame.world.delworld(result.getString("level_name"));
				this.plugin.plugin.getLogger().info("월드 기한만료 " + result.getString("level_name") + "님의 월드 삭제 완료.");
			}
			result.close();
		} catch (SQLException e) {
		}
		this.plugin.plugin.getLogger().info("총 " +i+ "개의 월드를 삭제 처리 하였습니다, 기준 날짜 " + del_time);
		return;
	}

	public void delworlddata(String level) {
		this.mysqllib.execute("DELETE FROM worlds_info WHERE level_name = '" + level.toLowerCase() + "';");
		return;
	}

	/**
	 * @param level
	 * @return 월드의 크기를 늘림
	 */
	public void addworldsize(String level, int size) {
		String query = "UPDATE worlds_info SET size="+size+" WHERE level_name = '"+level.toLowerCase()+"';";
		this.mysqllib.executeupdate(query);
		return;
	}

	/**
	 * @param level
	 * @return 월드의 크기
	 */
	public int getworldsize(String level) {
		String query = "SELECT * FROM worlds_info WHERE level_name = '"+level.toLowerCase()+"';";
		ResultSet result = this.mysqllib.executequery(query);
		try {
			if (result.next()) {
				return result.getInt("size");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public double[] getworldspawn(String level) {
		String query = "SELECT * FROM worlds_info WHERE level_name = '"+level.toLowerCase()+"';";
		ResultSet result = this.mysqllib.executequery(query);
		try {
			if (result.next()) {
				String json_spawn = result.getString("spawn");
				if (json_spawn == null) {
					return null;
				}
				JSONObject object = (JSONObject) JSONValue.parse(json_spawn);
				double[] pos = new double[3];
				pos[0] = (double) object.get("x");
				pos[1] = (double) object.get("y");
				pos[2] = (double) object.get("z");
				return pos;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean check_worlddata(String level) {
    	ResultSet result = this.getworlddata(level);
    	try {
			if (result.next()) {
				return true;
			}
		} catch (SQLException e) {
			return false;
		}
    	return false;
    }
	
//	public void addsizeworld() {
//	String query = "SELECT * FROM worlds_info WHERE 1;";
//	ResultSet result = this.mysqllib.executequery(query);
//	int i = 0;
//	try {
//		while (result.next()) {
//			i++;
//			String old_time = result.getString("add_time");
//			int lenght = old_time.length();
//			int day = Integer.parseInt(old_time.substring(5, lenght)); // 2014-200
//			int year = Integer.parseInt(old_time.substring(0, 4));
//			if ( year == 2016) {
//				this.addworldsize(result.getString("level_name"), result.getInt("size")+30);
//				this.plugin.plugin.getLogger().info(result.getString("level_name") + "크기 30칸 확장 완료");
//			}
//		}
//		result.close();
//	} catch (SQLException e) {
//	}
//	this.plugin.plugin.getLogger().info("총 " +i+ "개 월드 확장 처리");
//	return;
//}
}
