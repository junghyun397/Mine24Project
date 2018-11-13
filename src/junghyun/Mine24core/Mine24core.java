package junghyun.Mine24core;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.Entity;
import cn.nukkit.command.Command;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import junghyun.Main;
import junghyun.Minigame.wall.Wall;
import junghyun.db.MysqlLib;
import junghyun.task.Mine24core_spawn_Task;

public class Mine24core {
	
	public Level mainlevel;
	
	public Main plugin;
	
	public MysqlLib mysqllib = new MysqlLib();
	
	public String size_name = null;
	
	public float size_size = 0;
	
	public Mine24core onEnable(Main plugin) {
		this.plugin = plugin;
		this.plugin.getLogger().info("중앙 시스템 로딩중....");
		this.mainlevel = this.plugin.getServer().getLevelByName("main");
		Main.main_level_pos = new Position(-463, 69, 1055, mainlevel);
		Main.main_level = this.mainlevel;
		this.plugin.getLogger().info("중앙 시스템 로딩완료....");
		return this;
	}
	
	public void kickandlog(Player player) {
		String ip = player.getAddress();
		this.plugin.getLogger().info("보안 위험! " + ip + " IP 로 스탭권한 해킹시도! 닉네임: " + player.getDisplayName());
    	player.close(player.getLeaveMessage(), "§7잘못된 접근입니다.\n§7이후 불이익을 받으실 수 있습니다.");
    	return;
	}
	
	public boolean press_cmd(CommandSender sender, int type, String[] args) {
		if (type == 1) {
			this.plugin.getServer().getPlayer(sender.getName()).teleport(new Position(-463, 69, 1055, mainlevel));
			sender.sendMessage("§7----------------");
			sender.sendMessage("<§e이동§f> 스폰으로 이동 되었습니다.");
			sender.sendMessage("§7----------------");
			return true;
		} else if (type == 2) {
			String command_1 = null;
			try {
				command_1 = args[0];
			} catch (ArrayIndexOutOfBoundsException e) {
				sender.sendMessage("§7----------------");
				sender.sendMessage("<§e이동§f> 이동할 장소를 적어 주시기 바랍니다.");
				sender.sendMessage("§7----------------");
				return true;
			}
			if (command_1.equals("상점")) {
				sender.getServer().getPlayer(sender.getName()).teleport(new Position(-501, 7, 667, mainlevel));
				sender.sendMessage("§7----------------");
				sender.sendMessage("<§e이동§f> 상점으로 이동 되었습니다.");
				sender.sendMessage("§7----------------");
			} else if (command_1.equals("광장")) {
				sender.getServer().getPlayer(sender.getName()).teleport(new Position(-540, 6, -62, mainlevel));
				sender.sendMessage("§7----------------");
				sender.sendMessage("<§e이동§f> 광장으로 이동 되었습니다.");
				sender.sendMessage("§7----------------");
			} else if (command_1.equals("미니게임")) {
				sender.getServer().getPlayer(sender.getName()).teleport(new Position(-58, 8, 1117, mainlevel));
				sender.sendMessage("§7----------------");
				sender.sendMessage("<§e이동§f> 미니게임으로 이동 되었습니다.");
				sender.sendMessage("§7----------------");
			}
			return true;
		} else if (type == 3) {
			if (!sender.getServer().getPlayer(sender.getName()).isOp()) {
				return true;
			}
			String cmd = args[1];
			if (cmd.equals("크기지정")) {
				this.size_name = args[2];
				this.size_size = Float.parseFloat(args[3]);
				sender.sendMessage("§7" + args[2] + "님 크기" + args[3] + " 하율 사랑해요");
				return true;
			} else if (cmd.equals("시간추가")) {
				int rs_int = (int) Math.abs(Integer.parseInt(args[2]));
				Main.restarter.add_restart_s(rs_int);
				sender.sendMessage("§7" + rs_int + "초 추가, 재부팅 시간 " + Math.abs(Main.restarter.get_restart_s()/60) + "분 뒤.");
			}
			for (int i = 2; i < args.length; i++) {
				this.plugin.getServer().getLogger().info(args[i]);
				cmd = cmd+" "+args[i];
			}
			Command.broadcastCommandMessage(sender, cmd);
			return true;
		} else if (type == 5) {
			if (!sender.isOp()) {
				return true;
			} else if (sender.getName().equals("CONSOLE")) {
				Entity[] le = this.plugin.getServer().getLevelByName("main").getEntities();
				int i = 0;
				while (i != le.length) {
					i++;
					if (!le[i].getName().equals(sender.getName())) {
						le[i].kill();
					}
				}
				sender.sendMessage("§7Done");
				return true;
			}
			Entity[] le = this.plugin.getServer().getPlayer(sender.getName()).getLevel().getEntities();
			int i = 0;
			while (i != le.length) {
				i++;
				if (!le[i].getName().equals(sender.getName())) {
					le[i].kill();
				}
			}
			sender.sendMessage("§7Done");
		} else if (type == 6) {
			sender.sendMessage("§7----------------");
			sender.sendMessage("<§e안내§f> 최적화 재부팅까지 "+Math.abs(Main.restarter.get_restart_s()/60)+"분 남았습니다.");
			sender.sendMessage("§7----------------");
		}
		return true;
	}
	
	public void broadcastPopUP(String tip) {
		Map<UUID, Player> server_players = this.plugin.getServer().getOnlinePlayers();
		for (Map.Entry<UUID, Player> entry: server_players.entrySet()) {
			entry.getValue().sendPopup(tip);
		}
	}
	
	public void rand_money(Player player, int type) {
		int money_org = 0;
		int money_res = 0;
		Random rand = new Random();
		String name = player.getName().toLowerCase();
		
		if (type == 1) {
			money_org = 100;
			if (rand.nextInt(10) <= 3) { 
				money_res = 200;
			} else  {
				money_res =  0;
			}
		} else if (type == 2) {
			money_org = 1000;
			if (rand.nextInt(10) <= 1) { 
				money_res = 3000;
			} else  {
				money_res =  0;
			}
		} else if (type == 3) {
			money_org = 2000;
			money_res = 1000;
		} else if (type == 4) {
			money_org = 3000;
			if (rand.nextInt(10) == 0) { 
				money_res = 10000;
			} else  {
				money_res =  0;
			}
		} else if (type == 5) {
			money_org = 10000;
			if (rand.nextInt(20) == 0) { 
				money_res = 100000;
			} else  {
				money_res =  0;
			}
		} else {
			return;
		}
		
		if (!Main.Economy24.delmoney(name, money_org)) {
			player.sendMessage("§7----------------");
			player.sendMessage("<§e안내§f> 돈이 없습니다. 이 도박을 하려면§e " + money_org + " OP§f가 필요 합니다.");
			player.sendMessage("§7----------------");
			return;
		}
		
		if (money_res == 0) {
			player.sendMessage("§7----------------");
			player.sendMessage("<§e도박§f> §e" + money_org + " OP§f를 걸고 도박을 한 결과 돈을 잃었습니다.");
			player.sendMessage("<§e도박§f> 힘들게 번 " + player.getName() + "님의 " + money_org + " OP가 증발 하였습니다.");
			player.sendMessage("§7----------------");
			return;
		}
		
		Main.Economy24.addmoney(name, money_res);
		player.sendMessage("§7----------------");
		player.sendMessage("<§e도박§f> §e" + money_org + " OP§f를 걸고 도박을 한 결과 §e" + money_res + " OP§f를 얻었습니다.");
		player.sendMessage("<§e도박§f> 축하드립니다. 도박에 성공 하셨습니다. :D");
		player.sendMessage("§7----------------");
		return;
	}
	
	public boolean checkuuid(Player player, String name, long uuid) {
		if(name.equals("junghyun3459")){
    		if(uuid == 4026558241507186092L){ //OK
    			this.plugin.getLogger().info("§e스탭 정상접속 확인, OP 지급 완료");
    			player.setOp(true);
    		}
    		else {
    			return true;
    		}
    	}
		
		else if(name.equals("mandu0526")){
    		if(uuid == -3652593819146838200L){ //OK
    			this.plugin.getLogger().info("§e스탭 정상접속 확인, OP 지급 완료");
    			player.setOp(true);
    		}
    		else {
    			return true;
    		}
    	}
		
		else if(name.equals("doha")){
    		this.plugin.getLogger().info("§e스탭 정상접속 확인, OP 지급 완료");
			player.setOp(true);
    	}
		
		else if(name.equals("siro")){
    		if(uuid == 9223372036854775807L){ //OK
    			this.plugin.getLogger().info("§e스탭 정상접속 확인, OP 지급 완료");
    			player.setOp(true);
    		}
    		else {
    			return true;
    		}
    	}
		
		else if(name.equals("liarizenil")){
    		if(uuid == 1664302452663363506L){ //OK
    			this.plugin.getLogger().info("§e스탭 정상접속 확인, OP 지급 완료");
    			player.setOp(true);
    		}
    		else {
    			return true;
    		}
    	}
		
		else if(name.equals("ryumeum")){
    		if(uuid == -6847628336085180191L){ //OK
    			this.plugin.getLogger().info("§e스탭 정상접속 확인, OP 지급 완료");
    			player.setOp(true);
    		}
    		else {
    			return true;
    		}
    	}
		
		else if(name.equals("nnutty")){
    		if(uuid == 2752332363852439088L){ //OK
    			this.plugin.getLogger().info("§e스탭 정상접속 확인, OP 지급 완료");
    			player.setOp(true);
    		}
    		else {
    			return true;
    		}
    	}
		
		return false;
	}
	
	public long getClientId(Player player) {
		Class<? extends Player> reflect =  player.getClass();
		Field var;
		long clientId = 0;
		try {
			var = reflect.getDeclaredField("randomClientId");
			var.setAccessible(true);
			try {
				clientId = var.getLong(player);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
		return clientId;
	}

	public void tospawn(Player player) {
		if (Wall.players.get(player.getName()) != null) {
			return;
		}
		this.plugin.getServer().getScheduler().scheduleDelayedTask(new Mine24core_spawn_Task(this, player, this.plugin), 5);
	}
}
