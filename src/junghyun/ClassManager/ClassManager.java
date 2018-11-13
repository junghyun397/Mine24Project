package junghyun.ClassManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import junghyun.Main;

public class ClassManager {
	
	public static Map<String, Integer> players = new HashMap<String, Integer>();
	
	public Main plugin;
	
	public ClassManager onEnable(Main plugin) {
		this.plugin = plugin;
		this.plugin.getLogger().info("칭호 관리체계 로딩중.....");
		this.plugin.getLogger().info("칭호 관리체계 로딩완료...");
		return this;
	}

	public String setindex(String player) {
		ResultSet result = this.getplayerdata(player);
		String format = null;
		if (!this.chackregister(player)) {
			ClassManager.players.put(player, 0);
			format = this.getformat(player);
			return format;
		}
		try {
			if (result.next()) {
				int info = result.getInt("code");
				ClassManager.players.put(player, info);
				format = this.getformat(player);
				return format;
			}
		} catch (SQLException e) {
			this.plugin.getServer().getPlayer(player).kick("불량 클라이언트");
			e.printStackTrace();
			return null;
		}
		return null;
	}
	
	public boolean chackregister(String name) {
    	ResultSet result = this.getplayerdata(name);
    	try {
			if (result.next()) {
				return true;
			}
		} catch (SQLException e) {
			return false;
		}
    	return false;
    }

	public String getformat(String player) {
		int info = this.getclass(player);
		if (info == 0) {
			return "§2[유저] §f" + player;
		}
		else if (info == 1) {
			return "§b[Staff] §f" + player;
		}
		else if (info == 2) {
			return "§9[Staff] §f"+ player;
		}
		else if (info >= 20) {
			return "§6[Premium"+(info-10)+"] §f"+ player;
		}
		else {
			if ((info-10) == 1) {
				return "§5[Premium1] §f" + player;
			}
			return "§4[§6P§er§ae§2m§3i§bu§1m§5" + (info - 10) + "] §f" + player;
		}
	}
	
	public String gettag(String player) {
		int info = this.getclass(player);
		if (info == 0) {
			return "§f" + player;
		}
		else if (info == 1) {
			return "§b" + player;
		}
		else if (info == 2) {
			return "§1" + player;
		}
		else if (info >= 20) {
			return "§6[Premium"+(info-10)+"]§e"+ player;
		}
		else {
			if ((info -10) == 1) {
				return "§5" + player;
			}
			return "§4[§6P§er§ae§2m§3i§bu§1m§5"+(info-10)+"]§e"+player;
		}
	}
	
	public int getclass(String player) {
		return ClassManager.players.get(player);
	}
	
	public ResultSet getplayerdata(String player) {
		String query = "SELECT * FROM player_group WHERE name = '" + player.toLowerCase() + "';";
    	return Main.mysqllib.executequery(query);
	}

	public void setgroup(String player, int code) {
		if (!this.chackregister(player)) {
			String query = "INSERT INTO player_group(name, code) VALUES('"+player.toLowerCase()+"', '"+code+"');";
			Main.mysqllib.execute(query);
			return;
		}
		String query = "UPDATE player_group SET code='"+code+"' WHERE name ='"+player.toLowerCase()+"';";
		Main.mysqllib.executeupdate(query);
		return;
	}
	
	public void broadcastPopUP(CommandSender player, String msg, boolean type, String target) {
		if (type) {
			String massage = "§7" + player.getName() + " 님의 전체 메시지: " + msg;
			Map<UUID, Player> server_players = this.plugin.getServer().getOnlinePlayers();
			for (Map.Entry<UUID, Player> entry: server_players.entrySet()) {
				entry.getValue().sendPopup(massage);
			}
			player.sendMessage("§7----------------");
			player.sendMessage("<§e팝업 메시지§f> §e젠체§f에게 팝업을 전달하였습니다. 내용: " + msg);
			player.sendMessage("§7----------------");
			return;
		} else {
			String message = "§7" + player.getName() + " 님의 개인 메시지: " + msg;
			Player target_player = this.plugin.getServer().getPlayer(target);
			if (target_player != null) {
				target_player.sendTip(message);
				player.sendMessage("§7----------------");
				player.sendMessage("<§e팝업 메시지§f> §e"+target+" 님§f에게 팝업을 전달하였습니다. 내용: " + msg);
				player.sendMessage("§7----------------");
				return;
			}
			player.sendMessage("§7----------------");
			player.sendMessage("<§e팝업 메시지§f> §e"+target+" 님§f은 접속해 있지 않습니다.");
			player.sendMessage("§7----------------");
			return;
		}
	}
	
	public void printplayer(CommandSender player) {
		Map<UUID, Player> server_players = this.plugin.getServer().getOnlinePlayers();
		int i = 0;
		String msg = "<§e접속자 안내§f> 현재 접속자:";
		for (Map.Entry<UUID, Player> entry: server_players.entrySet()) {
			i++;
			String va;
			try {
				va = entry.getValue().getName();
				int result = ClassManager.players.get(va);
				if (result == 0) {
					msg = msg + " §2" + va;
				} else if (result == 1) {
					msg = msg + " §b" + va;
				} else if (result == 2) {
					msg = msg + " §1" + va;
				} else if (result == 11) {
					msg = msg + " §5" + va;
				} else {
					msg = msg + " §6" + va;
				}
			} catch (NullPointerException e){
				
			}
		}
		player.sendMessage("§7----------------");
		player.sendMessage(msg + "§f\n<§e접속자 안내§f> 전체 접속자수 총 §e" + i + "§f 명");
		player.sendMessage("§7----------------");
		return;
	}
	
	public void printplayer(Player player) {
		Map<UUID, Player> server_players = this.plugin.getServer().getOnlinePlayers();
		int i = 0;
		String msg = "<§e접속자 안내§f> 현재 접속자:";
		for (Map.Entry<UUID, Player> entry: server_players.entrySet()) {
			i++;
			String va;
			try {
				va = entry.getValue().getName();
				int result = ClassManager.players.get(va);
				if (result == 0) {
					msg = msg + " §2" + va;
				} else if (result == 1) {
					msg = msg + " §b" + va;
				} else if (result == 2) {
					msg = msg + " §1" + va;
				} else if (result == 11) {
					msg = msg + " §5" + va;
				} else {
					msg = msg + " §6" + va;
				}
			} catch (NullPointerException e){
				
			}
		}
		player.sendMessage("§7----------------");
		player.sendMessage(msg + "§f\n<§e접속자 안내§f> 전체 접속자수 총 §e" + i + "§f 명");
		player.sendMessage("§7----------------");
		return;
	}
	

	public void press_cmd(CommandSender sender, int type, String[] args) {
		 if (type == 1) {
			 if (!sender.isOp()) {
				 return;
			 }
			 String target = args[0];
			 int code = Integer.parseInt(args[1]);
			 this.setgroup(target, code);
			 this.setindex(target);
			 sender.sendMessage(target + "님 code" + code + "로 지정완료.");
			 return;
		 } else if (type == 2) {
			 if (ClassManager.players.get(sender.getName()) != 0) {
				 boolean type1 = false;
				 String msg = null;
				 String target = null;
				 try {
					 if (args[0].equals("전체")) {
						 type1 = true;
						 msg = args[1];
					 } else {
						 type1 = false;
						 target = args[1];
						 msg = args[2];
					 }
				 } catch (ArrayIndexOutOfBoundsException e) {
					 sender.sendMessage("§7----------------");
					 sender.sendMessage("<§e안내§f> /메시지 전체 <전달할 메시지> 또는 /메시지 개인 <전달할 플레이어> <전달할 내용> 과 같이 적어주시기 바랍니다.");
					 sender.sendMessage("§7----------------");
					 return;
				 }
				 this.broadcastPopUP(sender, msg, type1, target);
				 return;
			 }
			 sender.sendMessage("§7----------------");
			 sender.sendMessage("<§e안내§f> 일반 유저는 사용 불가능한 기능입니다. (Premium 유저 사용가능)");
			 sender.sendMessage("§7----------------");
			 return;
		 } else if (type == 3) {
			 this.printplayer(sender);
			 return;
		 }
		 return;
	 }
	
}
