package junghyun.Economy24;

import java.sql.ResultSet;
import java.sql.SQLException;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;

import junghyun.Main;
import junghyun.db.MysqlLib;

public class Economy24 {

	public MysqlLib mysqllib = new MysqlLib();

	public Main plugin;

	public Economy24 onEnable(Main plugin) {
		this.plugin = plugin;
		this.plugin.getLogger().info("경제 시스템 로딩중....");
		this.plugin.getLogger().info("경제 시스템 로딩완료.");
		return this;
	}

	// API

	public void addmoney(String player, int cost) {
		int dmoney = this.getmoney(player);
		int amoney = dmoney + cost;
		this.mysqllib.executeupdate(
				"UPDATE player_economy SET money = " + amoney + " WHERE name = '" + player.toLowerCase() + "'");
		this.money_update(player, amoney);
		return;
	}

	public boolean delmoney(String player, int cost) {
		int dmoney = this.getmoney(player);
		int amoney = dmoney - cost;
		if (amoney < 0) {
			return false;
		}
		this.mysqllib.executeupdate(
				"UPDATE player_economy SET money = " + amoney + " WHERE name = '" + player.toLowerCase() + "'");
		this.money_update(player, amoney);
		return true;
	}

	public void setmoney(String player, int cost) {
		this.mysqllib.executeupdate(
				"UPDATE player_economy SET money = " + cost + " WHERE name = '" + player.toLowerCase() + "'");
		this.money_update(player, cost);
		return;
	}

	public int getmoney(String player) {
		ResultSet sqlresult = this.getplayerdata(player);
		try {
			if (sqlresult.next()) {
				int money = sqlresult.getInt("money");
				return money;
			}
		} catch (SQLException e) {
			return 0;
		}
		return 0;
	}

	public ResultSet getplayerdata(String player) {
		String query = "SELECT * FROM player_economy WHERE name = '" + player.toLowerCase() + "';";
		return this.mysqllib.executequery(query);
	}

	// END API
	
	public void money_update(String player, int money) {
		Player player_c = this.plugin.getServer().getPlayer(player);
		if (player_c == null) {
			return;
		} else if (!player_c.getName().equals(player)) {
			return;
		}
		Main.popup_task.update_money(player, money);
		return;
	}

	// NON-API

	public void addproflie(String player) {
		String query = "INSERT INTO player_economy(name, money) VALUES('" + player.toLowerCase() + "', 0)";
		this.mysqllib.execute(query);
		this.plugin.getLogger().info(player + "님 새 프로필 생성완료.");
		return;
	}

	public void delproflie(String player) {
		String query = "DELETE FROM WHERE name = '" + player.toLowerCase() + "'";
		this.mysqllib.execute(query);
		this.plugin.getLogger().info(player + "님 새 프로필 삭제완료.");
		return;
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

	public boolean press_cmd(CommandSender sender, int type, String[] args) {
		@SuppressWarnings("unused")
		String command_1;
		try {
			command_1 = args[0];
		} catch (ArrayIndexOutOfBoundsException e) {
			sender.sendMessage("§7----------------");
			sender.sendMessage("<§e명령어 안내§f> /경제 명령어는 자신의 소유 OP 관리, 타인의 OP 조회 등의 기능을 수행합니다.");
			sender.sendMessage("<§e명령어 안내§f> /경제 내돈, /경제 송금, /경제 조회 명령어를 사용 가능합니다.");
			sender.sendMessage("<§e명령어 안내§f> 인테넷 주소창에 §e명령어.메인.한국§f 을 적어 이동해 명령어 사용법을 알아 볼 수 있습니다.");
			sender.sendMessage("§7----------------");
			return true;
		}
		if (args[0].equals("내돈")) {
			String name = sender.getName().toString();
			String money = Integer.toString(this.getmoney(name));
			sender.sendMessage("§7----------------");
			sender.sendMessage("<§e잔고 안내§f> 현재 소유하고 계신 OP는 §e" + money + "OP§f 입니다.");
			sender.sendMessage("§7----------------");
			return true;
		} else if (args[0].equals("송금")) {
			this.presssendmoney(sender, args);
			return true;
		} else if (args[0].equals("조회")) {
			String player = null;
			try {
				player = args[1].toLowerCase();
			} catch (ArrayIndexOutOfBoundsException e) {
				sender.sendMessage("§7----------------");
				sender.sendMessage("<§e안내§f> 조회하실 플레이어의 닉네임을 적어 주시기 바랍니다.");
				sender.sendMessage("§7----------------");
				return true;
			}
			if (!this.chackregister(player)) {
				sender.sendMessage("§7----------------");
				sender.sendMessage("<§e안내§f> 조회에 실패 하였습니다. 조회하실 플레이어 정보가 존재하지 않습니다.");
				sender.sendMessage("§7----------------");
				return true;
			}
			sender.sendMessage("§7----------------");
			sender.sendMessage("<§e잔고 안내§f> " + player + "님이 현재 소유하고 계신 OP는 §e" + this.getmoney(player) + " OP§f 입니다.");
			sender.sendMessage("§7----------------");
			return true;
		}
		return true;
	}

	private void presssendmoney(CommandSender sender, String[] args) {
		String name = sender.getName();
		String target;
		try {
			target = args[1];
		} catch (ArrayIndexOutOfBoundsException e) {
			sender.sendMessage("§7----------------");
			sender.sendMessage("<§e안내§f> 송금에 실패 하였습니다. 송금받을 플레이어 이름을 적지 않으셨습니다.");
			sender.sendMessage("§7----------------");
			return;
		}
		if (!this.chackregister(target)) {
			sender.sendMessage("§7----------------");
			sender.sendMessage("<§e송금§f> 송금에 실패 하였습니다. 송금을 받는 플레이어 정보가 존재하지 않습니다.");
			sender.sendMessage("§7----------------");
			return;
		}
		int cost = 0;
		try {
			cost = Integer.parseInt(args[2]);
		} catch (NumberFormatException e) {
			sender.sendMessage("§7----------------");
			sender.sendMessage("<§e송금§f> 송금에 실패 하였습니다. 송금할 금액은 숫자로 입력 바랍니다.");
			sender.sendMessage("§7----------------");
			return;
		} catch (ArrayIndexOutOfBoundsException e) {
			sender.sendMessage("§7----------------");
			sender.sendMessage("<§e송금§f> 송금에 실패 하였습니다. 송금할 금액을 적어 주시기 바랍니다.");
			sender.sendMessage("§7----------------");
		}
		if (cost < 0) {
			sender.sendMessage("§7----------------");
			sender.sendMessage("<§e송금§f> 송금에 실패 하였습니다. 음수로 송금 불가능 합니다.");
			sender.sendMessage("§7----------------");
			return;
		}
		if (!this.delmoney(name, cost)) {
			sender.sendMessage("§7----------------");
			sender.sendMessage("<§e송금§f> 송금에 실패 하였습니다. 잔고가 부족합니다.");
			sender.sendMessage("§7----------------");
			return;
		}
		this.addmoney(target, cost);
		sender.sendMessage("§7----------------");
		sender.sendMessage("<§e송금§f> 송금 성공하였습니다. §e" + target + "님에게 " + cost + "OP §f를 송금 하였습니다.");
		sender.sendMessage("§7----------------");
		Player target_player = this.plugin.getServer().getPlayer(target);
		if (target_player == null) {
			return;
		}
		if (!target_player.getName().equals(target)) {
			return;
		}
		target_player.sendMessage("§7----------------");
		target_player.sendMessage("<§e송금§f> §e" + sender.getName() + "님에게 " + cost + "OP §f를 송급 받았습니다.");
		target_player.sendMessage("§7----------------");
		return;
	}
}
