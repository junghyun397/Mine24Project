package junghyun.Wildgame;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import junghyun.Wildgame_world.WorldManager;
import junghyun.class_box.Mine24world;
import junghyun.Wildgame_db.DatabaseManager;
import junghyun.task.Wildgame_Task;
import junghyun.Main;

public class Wildgame {

	/*
	 * DB 구조
	 */

	public Map<String, Mine24world> maps = new HashMap<String, Mine24world>();

	public Main plugin;

	public static WorldManager world;

	public static DatabaseManager db;

	public Wildgame onEnable(Main plugin) {
		this.plugin = plugin;
		this.plugin.getLogger().info("야생 시스템 로딩중....");
		this.plugin.getServer().getScheduler().scheduleRepeatingTask(new Wildgame_Task(plugin), 1); 
		
		Wildgame.world = new WorldManager(this); // 월드 관리자 불러오기
		Wildgame.db = new DatabaseManager(this); // 데이터베이스 관리자 불러오기
		
		this.plugin.getLogger().info("야생 시스템 로딩완료.");
		return this;
		// player.sendMessage("X " + event.getBlock().getX() + " Y " +
		// event.getBlock().getY() + " Z " + event.getBlock().getZ());
	}

	public void joinworld(String level, String player, Player player_class) {
		if (Wildgame.world.worldinfo(level) == 2) {
			this.setindex(level);
		} else {
			try {
				this.setindex(level);
			} catch (NullPointerException e) {
				this.plugin.log_info(level + " 월드 오류 정정");
				Wildgame.world.createworld(level);
				this.setindex(level);
			}
		}
		Wildgame.world.joinworld(player_class, level);
		return;
	}

	public void delworld(String level) {
		if (Wildgame.world.worldinfo(level) == 2) {
			Wildgame.db.delworlddata(level);
			Wildgame.world.delworld(level);
		} else {
			this.plugin.getServer().unloadLevel(this.plugin.getServer().getLevelByName(level));
			Wildgame.db.delworlddata(level);
			Wildgame.world.delworld(level);
		}
		return;
	}

	/*
	 * public void leaveworld(String level, String player, Player player_class)
	 * { int[] players = this.maps.get(level); players[4] = players[4]-1;
	 * this.maps.put(level, players); if (players[4] == 0) {
	 * this.getServer().getScheduler().scheduleDelayedTask(new
	 * Wildgame_unload_Task(this, level), 10); }
	 * //this.world.leaveworld(player_class, level); return; }
	 */

	public String getnowdate() {
		GregorianCalendar gc = new GregorianCalendar();
		String nowdate = gc.get(Calendar.YEAR) + "-" + gc.get(Calendar.DAY_OF_YEAR);
		return nowdate;
	}

	public int getnowday() {
		GregorianCalendar gc = new GregorianCalendar();
		return gc.get(Calendar.DAY_OF_YEAR);
	}

	public int getnowyear() {
		GregorianCalendar gc = new GregorianCalendar();
		return gc.get(Calendar.YEAR);
	}

	/**
	 * @param level 월드를 목록에 색인. 색인하지 않을시 월드 입장 에러
	 */
	public void setindex(String level) {
		Mine24world world_box = new Mine24world();
		world_box.set_all(level, Wildgame.db.getworldpoints(level), Wildgame.db.getworldspawn(level), Wildgame.db.getworldplayer(level));
		this.maps.put(level, world_box);
		return;
	}

//	// 위치 확인
//
//	/**
//	 * @param player
//	 * @return 위치 확인, 사용가능 true
//	 */
//	public boolean checkpos(String player, String level, double playerx, double playery) {
//		if (this.checkpos_other(player, level)) {
//			if (this.checkpos_world(level, playerx, playery)) {
//				return true;
//			}
//			return false;
//		}
//		return false;
//	}
//
//	/**
//	 * @param player
//	 * @return 월드 좌표 확인
//	 */
//	public boolean checkpos_world(String level, double playerx, double playery) {
//
//		// 서 있는 월드에서의 호환성 체크
//		int[] levelline = this.maps.get(level);
//		double pos1x = levelline[0];
//		double pos1y = levelline[1];
//		double pos2x = levelline[2];
//		double pos2y = levelline[3];
//		if (playerx <= pos1x) {
//			return false;
//		} else if (playery <= pos1y) {
//			return false;
//		} else if (playerx >= pos2x) {
//			return false;
//		} else if (playery >= pos2y) {
//			return false;
//		}
//		return true;
//	}
//
//	/**
//	 * @param player
//	 * @return 플레이어가 사용 가능한지 확인
//	 */
//	public boolean checkpos_other(String player, String level) {
//		String[] players = this.maps_player.get(level); // 플레이어 리스트를 얻어옴
//		int length = players.length; // 길이를 얻어옴
//		for (int i = 0; i <= length - 1; i++) { // 길이만큼 반복
//			if (players[i].equals(player)) { // 같은게 바온다면 바로 리턴
//				return true;
//			}
//		}
//		return false;
//	}

	// END 위치 확인

	// API

	/**
	 * @param level
	 *            월드 생성
	 */
	public void createworld(String level) {
		Wildgame.world.createworld(level);
		return;
	}

	// END API

	// 명령어 입력기

	public boolean press_cmd(CommandSender sender, int type, String[] args) {
		Player player = sender.getServer().getPlayer(sender.getName());
		String command_1 = null;
		try {
			command_1 = args[0];
		} catch (ArrayIndexOutOfBoundsException e) {
			player.sendMessage("§7----------------");
			player.sendMessage("<§e명령어 안내§f> /월드 명령어는 자신의 월드, 다른사람의 월드로 이동 등의 기능을 수행합니다.");
			player.sendMessage("<§e명령어 안내§f> /월드 이동, /월드 공유, /월드 공유초기화, /월드 연장, /월드 생성");
			player.sendMessage("<§e명령어 안내§f> /월드 정보, /월드 스폰설정, /월드 확장, /월드 추방, /월드 문제해결 명령어를 사용 가능합니다.");
			player.sendMessage("<§e명령어 안내§f> 인테넷 주소창에 §e명령어.메인.한국§f 을 적어 이동해 명령어 사용법을 알아 볼 수 있습니다.");
			player.sendMessage("§7----------------");
			return true;
		}
		if (command_1.equals("확장")) {
			this.pressaddsize(player, args);
			return true;
		} else if (command_1.equals("공유")) {
			this.pressshere(player, args);
			return true;
		} else if (command_1.equals("추방")) {
			this.presskick(player, args);
			return true;
		} else if (command_1.equals("생성")) {
			this.presscreate(player, args);
			return true;
		} else if (command_1.equals("연장")) {
			this.pressextend(player, args);
			return true;
		} else if (command_1.equals("이동")) {
			this.pressmove(player, args);
			return true;
		} else if (command_1.equals("공유초기화")) {
			this.presssherereset(player, args);
			return true;
		} else if (command_1.equals("정보")) {
			this.pressworldinfo(player, args);
			return true;
		} else if (command_1.equals("스폰설정")) {
			this.presssetspawn(player, args);
			return true;
		} else if (command_1.equals("축약")) {
			this.pressindexname(player, args);
			return true;
		} else if (command_1.equals("잠금")) {
			this.presslockworld(player, args);
			return true;
		} else if (command_1.equals("문제해결")) {
			this.pressworldcheck(player, args);
			return true;
		}
		return true;
	}
	
	private void pressworldcheck(Player player, String[] args) {
		String level = player.getName().toLowerCase();
		int info = Wildgame.world.worldinfo(level);
		if (info != 3) {
			if (!Wildgame.db.check_worlddata(level)) {
				Wildgame.world.delworld(level);
				player.sendMessage("§7----------------");
				player.sendMessage("<§e월드 관리§f> 월드 파일 결손이 발견 되었습니다.오류 해결 완료 되었습니다.");
				player.sendMessage("<§e월드 관리§f> /월드 생성 명령어를 이용해 다시 월드를 생성 해주시기 바랍니다. :D");
				player.sendMessage("§7----------------");
				return;
			}
		}
		player.sendMessage("§7----------------");
		player.sendMessage("<§e월드 관리§f> 발견된 오류가 없습니다.");
		player.sendMessage("§7----------------");
	}

	private void presssetspawn(Player player, String[] args) {
		String level = player.getName().toLowerCase();
		if (!level.equals(player.getLevel().getName())) {
			player.sendMessage("§7----------------");
			player.sendMessage("<§e월드 관리§f> 자신의 월드에서만 지정 가능합니다.");
			player.sendMessage("§7----------------");
			return;
		}
		if (Wildgame.world.worldinfo(level) == 3) {
			player.sendMessage("§7----------------");
			player.sendMessage("<§e월드 관리§f> 월드가 삭제 되어 있습니다. /월드 생성 명령어로 월드를 재성성 해주시기 바랍니다.");
			player.sendMessage("§7----------------");
			return;
		}
		double x = Math.round(player.getX());
		double y = Math.round(player.getY());
		double z = Math.round(player.getZ());
		Wildgame.db.setworldspawn(player.getName(), x, y, z);
		player.sendMessage("§7----------------");
		player.sendMessage("<§e월드 관리§f> 소지하고 계신 월드의 스폰을 서계신 위치로 지정 했습니다.");
		player.sendMessage("§7----------------");
	}

	public void move_slef_world(Player player) {
		String level = player.getName().toLowerCase();
		if (Wildgame.world.worldinfo(level) == 3) {
			this.createworld(player.getName());
			player.sendMessage("§7----------------");
			player.sendMessage("<§e월드 관리§f> 월드가 생성 되었습니다. /월드 이동 명령어로 자신의 월드로 이동 할 수 있습니다.");
			player.sendMessage("<§e월드 관리§f> 사면이 바다거나 비 정상적인 지형에서 스폰 되셨다면 카카오톡 @Mine24 로 문의 부탁드립니다.");
			player.sendMessage("<§e월드 관리§f> 주어진 잔디, 뼈가루, 묘목은 나무 생성용 으로 사용하시기 바랍니다.");
			player.sendMessage("§7----------------");
			player.getInventory().addItem(new Item(274, 0, 1, "채굴용 돌 곡괭이"));
			player.getInventory().addItem(new Item(351, 15, 2, "나무 생성용 뼈가루"));
			player.getInventory().addItem(new Item(4, 0, 64, "동굴 탈출용 조약돌"));
			player.getInventory().addItem(new Item(2, 0, 2, "나무 생성용 잔디"));
			player.getInventory().addItem(new Item(6, 0, 2, "나무 생성용  묘목"));
			this.joinworld(level, level, player);
			return;
		}
		this.joinworld(level, level, player);
		String del_time = Wildgame.db.getworlddeltime(level);
		int lenght = del_time.length();
		int day = Integer.parseInt(del_time.substring(5, lenght)); // 2014-200
		int year = Integer.parseInt(del_time.substring(0, 4));
		int del_day = 0;
		if (year != this.getnowyear()) {
			del_day = day + (365 - this.getnowday()); // 2016-360 2017-1
		} else {
			del_day = day - this.getnowday();
		}
		player.sendMessage("§7----------------");
		player.sendMessage("<§e이동§f> 자신의 월드로 이동 완료되었습니다.");
		player.sendMessage("<§e안내§f> 소유 하고 계신 월드는 " + del_day + "일 뒤 삭제됩니다.");
		player.sendMessage("<§e안내§f> /월드 연장 명령어를 통해 월드의 삭제일을 연장 가능합니다.");
		player.sendMessage("§7----------------");
		return;
	}

	/**
	 * @param player
	 * @param args
	 * 
	 */
	private void pressextend(Player player, String[] args) {
		if (Wildgame.world.worldinfo(player.getName()) == 3) {
			player.sendMessage("§7----------------");
			player.sendMessage("<§e월드 관리§f> 월드가 존재하지 않거나 삭제 되어 있습니다.");
			player.sendMessage("§7----------------");
			return;
		}
		int weeks = 0;
		try {
			weeks = Integer.parseInt(args[1]);
		} catch (ArrayIndexOutOfBoundsException e) {
			weeks = 1;
		}
		if (weeks < 0) {
			player.sendMessage("§7----------------");
			player.sendMessage("<§e월드 관리§f> 음수의 영역으로 연장 불가능 합니다.");
			player.sendMessage("§7----------------");
			return;
		}
		if (weeks > 10) {
			player.sendMessage("§7----------------");
			player.sendMessage("<§e월드 관리§f> 한방에 10주 이상 연장 불가능 합니다.");
			player.sendMessage("§7----------------");
			return;
		}
		int size = Wildgame.db.getworldsize(player.getName());
		int cost = 0;
		if (size <= 75) {
			cost = Math.round(((size * 2) * (size * 2) * weeks) / 80);
		} else {
			cost = Math.round(((size * 2) * (size * 2) * weeks) / 100);
		}
		if (!Main.Economy24.delmoney(player.getName(), cost)) {
			player.sendMessage("§7----------------");
			player.sendMessage("<§e월드 관리§f> 연장을 진행하실 돈이 부족합니다. " + weeks + "주 연장 비용: " + cost + "OP");
			player.sendMessage("§7----------------");
			return;
		}
		String del_time = Wildgame.db.addworldextend(weeks, player.getName());
		player.sendMessage("§7----------------");
		player.sendMessage(
				"<§e월드 관리§f> 사용 기간 연쟝을 완료 하였습니다. " + weeks + "주 연장, 비용: " + cost + "OP, 사용 가능기간  " + del_time);
		player.sendMessage("§7----------------");
		return;
	}

	private void presscreate(Player player, String[] args) {
		String name = player.getName().toLowerCase();
		if (Wildgame.world.worldinfo(name) == 3) {
			this.createworld(name);
			player.sendMessage("§7----------------");
			player.sendMessage("<§e월드 관리§f> 월드가 생성 되었습니다. /월드 이동 명령어로 자신의 월드로 이동 할 수 있습니다.");
			player.sendMessage("<§e월드 관리§f> 사면이 바다거나 비 정상적인 지형에서 스폰 되셨다면 카카오톡 @Mine24 로 문의 부탁드립니다.");
			player.sendMessage("<§e월드 관리§f> 주어진 잔디, 뼈가루, 묘목은 나무 생성용 으로 사용하시기 바랍니다.");
			player.sendMessage("§7----------------");
			player.getInventory().addItem(new Item(274, 0, 1, "채굴용 돌 곡괭이"));
			player.getInventory().addItem(new Item(351, 15, 2, "나무 생성용 뼈가루"));
			player.getInventory().addItem(new Item(4, 0, 64, "동굴 탈출용 조약돌"));
			player.getInventory().addItem(new Item(2, 0, 2, "나무 생성용 잔디"));
			player.getInventory().addItem(new Item(6, 0, 2, "나무 생성용  묘목"));
			this.joinworld(name, name, player);
			return;
		}
		player.sendMessage("§7----------------");
		player.sendMessage("<§e월드 관리§f> 이미 월드가 존재 합니다.");
		player.sendMessage("§7----------------");
		return;
	}
	
	private void pressindexname(Player player, String[] args) {
		String name = null;
		try {
			name = args[1];
		} catch (ArrayIndexOutOfBoundsException e) {
			player.sendMessage("§7----------------");
			player.sendMessage("<§e월드 관리§f> /월드 축약 <축약할 이름> 형태로 적어 주시기 바랍니다.");
			player.sendMessage("§7----------------");
			return;
		}
		if (Wildgame.world.worldinfo(player.getName().toLowerCase()) == 3) {
			player.sendMessage("§7----------------");
			player.sendMessage("<§e월드 관리§f> 월드가 존재하지 않거나 삭제 되어 있습니다.");
			player.sendMessage("§7----------------");
			return;
		}
		String owner = Wildgame.db.getindexname(name);
		if (owner != null) {
			player.sendMessage("§7----------------");
			player.sendMessage("<§e월드 관리§f> " + name + " 은 이미 다른사람이 사용하고 있는 축약자 입니다.");
			player.sendMessage("<§e월드 관리§f> " + name + " 으로는 " + owner + "님의 월드로 이동 가능합니다.");
			player.sendMessage("§7----------------");
			return;
		}
		Wildgame.db.setindexname(player.getName().toLowerCase(), name);
		player.sendMessage("§7----------------");
		player.sendMessage("<§e월드 관리§f> " + name + " 으로 월드의 축약자가 설정 되었습니다.");
		player.sendMessage("<§e월드 관리§f> 이제부터 §e/월드 이동 @" + name + "§f 으로 "+player.getName()+"님의 월드로 이동 가능합니다. :D");
		player.sendMessage("§7----------------");
		
	}

	private void pressmove(Player player, String[] args) {
		String level = null;
		try {
			level = args[1].toLowerCase();
		} catch (ArrayIndexOutOfBoundsException e) {
			level = player.getName().toLowerCase();
		}
		String check = level.substring(0, 1);
		String check1 = level.substring(1, level.length());
		if (check.equals("@")) {
			String target = Wildgame.db.getindexname(check1);
			if (target != null) {
				if (!Wildgame.db.getplayercanjoin(player.getName().toLowerCase(), target)) {
					player.sendMessage("§7----------------");
					player.sendMessage("<§e이동§f> " + target + "님의 월드는 잠겨 있습니다.");
					player.sendMessage("<§e이동§f> 잠겨있는 월드로 이동은 불가능 합니다.");
					player.sendMessage("§7----------------");
					return;
				}
				this.joinworld(target, player.getName().toLowerCase(), player);
				player.sendMessage("§7----------------");
				player.sendMessage("<§e이동§f> " + target + "님 월드로 이동 완료 하였습니다.");
				player.sendMessage("§7----------------");
				return;
			}
			player.sendMessage("§7----------------");
			player.sendMessage("<§e안내§f> " + check1 + " 로 등록된 월드는 존재하지 않습니다.");
			player.sendMessage("§7----------------");
			return;
		}
		if (Wildgame.world.worldinfo(level) == 3) {
			player.sendMessage("§7----------------");
			player.sendMessage("<§e이동§f> 이동 하실 월드가 존재하지 않습니다.");
			player.sendMessage("§7----------------");
			return;
		}
		if (!Wildgame.db.getplayercanjoin(player.getName().toLowerCase(), level)) {
			player.sendMessage("§7----------------");
			player.sendMessage("<§e이동§f> " + level + "님의 월드는 잠겨 있습니다.");
			player.sendMessage("<§e이동§f> 잠겨있는 월드로 이동은 불가능 합니다.");
			player.sendMessage("§7----------------");
			return;
		}
		this.joinworld(level, player.getName().toLowerCase(), player);
		if (player.getName().toLowerCase().equals(level)) {
			String del_time = Wildgame.db.getworlddeltime(level);
			int lenght = del_time.length();
			int day = Integer.parseInt(del_time.substring(5, lenght)); // 2014-200
			int year = Integer.parseInt(del_time.substring(0, 4));
			int del_day = 0;
			if (year != this.getnowyear()) {
				del_day = day + (365 - this.getnowday()); // 2016-360 2017-1
			} else {
				del_day = day - this.getnowday();
			}
			player.sendMessage("§7----------------");
			player.sendMessage("<§e안내§f> 소유 하고 계신 월드는 " + del_day + "일 뒤 삭제됩니다.");
			player.sendMessage("<§e안내§f> /월드 연장 명령어를 통해 월드의 삭제일을 연장 가능합니다.");
		}
		player.sendMessage("§7----------------");
		player.sendMessage("<§e이동§f> " + level + "님 월드로 이동 완료 하였습니다.");
		player.sendMessage("§7----------------");
		return;
	}

	private void presskick(Player player, String[] args) {
		if (Wildgame.world.worldinfo(player.getName().toLowerCase()) == 3) {
			player.sendMessage("§7----------------");
			player.sendMessage("<§e월드 관리§f> 월드가 존재하지 않거나 삭제 되어 있습니다.");
			player.sendMessage("§7----------------");
			return;
		}
		String target = null;
		try {
			target = args[1].toLowerCase();
		} catch (ArrayIndexOutOfBoundsException e) {
			player.sendMessage("§7----------------");
			player.sendMessage("<§e월드 관리§f> 월드 공유를 취소할 플레이어의 닉네임을 적어 주시기 바랍니다.");
			player.sendMessage("§7----------------");
			return;
		}
		if (!Wildgame.db.delworldplayer(player.getName(), target)) {
			player.sendMessage("§7----------------");
			player.sendMessage("<§e월드 관리§f> "+target+"님은 이미 공유 되어있지 않는 플레이어 입니다.");
			player.sendMessage("§7----------------");
			return;
		}
		player.sendMessage("§7----------------");
		player.sendMessage("<§e월드 관리§f> "+target+"님의 공유를 해제 완료 하였습니다.");
		player.sendMessage("§7----------------");
		return;
	}

	private void presssherereset(Player player, String[] args) {
		if (Wildgame.world.worldinfo(player.getName().toLowerCase()) == 3) {
			player.sendMessage("§7----------------");
			player.sendMessage("<§e월드 관리§f> 월드가 존재하지 않거나 삭제 되어 있습니다.");
			player.sendMessage("§7----------------");
			return;
		}
		Wildgame.db.sharereset(player.getName().toLowerCase());
		player.sendMessage("§7----------------");
		player.sendMessage("<§e월드 관리§f> 공유된 플레이어 목록이 초기화 되었습니다.");
		player.sendMessage("§7----------------");
		return;
	}

	private void pressshere(Player player, String[] args) {
		if (Wildgame.world.worldinfo(player.getName().toLowerCase()) == 3) {
			player.sendMessage("§7----------------");
			player.sendMessage("<§e월드 관리§f> 월드가 존재하지 않거나 삭제 되어 있습니다.");
			player.sendMessage("§7----------------");
			return;
		}
		String target = null;
		try {
			target = args[1].toLowerCase();
		} catch (ArrayIndexOutOfBoundsException e) {
			player.sendMessage("§7----------------");
			player.sendMessage("<§e월드 관리§f> 공유할 플레이어의 닉네임을 적어 주시기 바랍니다.");
			player.sendMessage("§7----------------");
			return;
		}
		if (!Wildgame.db.addworldplayer(player.getName(), target)) {
			player.sendMessage("§7----------------");
			player.sendMessage("<§e월드 관리§f> " + target + "님과 월드를 공유할 수 없습니다. 이미 초대 되어 있습니다.");
			player.sendMessage("§7----------------");
			return;
		}
		player.sendMessage("§7----------------");
		player.sendMessage("<§e월드 관리§f> " + target + "님과 월드를 공유 하였습니다.");
		player.sendMessage("§7----------------");
		return;
	}
	
	private void presslockworld(Player player, String[] args) {
		if (Wildgame.world.worldinfo(player.getName().toLowerCase()) == 3) {
			player.sendMessage("§7----------------");
			player.sendMessage("<§e월드 관리§f> 월드가 존재하지 않거나 삭제 되어 있습니다.");
			player.sendMessage("§7----------------");
			return;
		}
		String type = null;
		try {
			type = args[1]; 
		} catch (ArrayIndexOutOfBoundsException e) {
			player.sendMessage("§7----------------");
			player.sendMessage("<§e안내§f> /월드 잠금 <완전/공유/해제> 와 같이 적어주시기 바랍니다.");
			player.sendMessage("§7----------------");
			return;
		}
		if (type.equals("완전")) {
			Wildgame.db.setworldlocked(player.getName().toLowerCase(), 2);
			player.sendMessage("§7----------------");
			player.sendMessage("<§e안내§f> 월드가 완전 잠금 처리 되었습니다. " +player.getName()+ "님을 제외한 모든 플레이어의 접근을 막습니다.");
			player.sendMessage("§7----------------");
			return;
		} else if (type.equals("공유")) {
			Wildgame.db.setworldlocked(player.getName().toLowerCase(), 1);
			player.sendMessage("§7----------------");
			player.sendMessage("<§e안내§f> 월드가 잠금 처리 되었습니다. 공유 처리되지 않은 모든 플레이어의 접근을 막습니다.");
			player.sendMessage("§7----------------");
		} else if (type.equals("해제")) {
			Wildgame.db.setworldlocked(player.getName().toLowerCase(), 0);
			player.sendMessage("§7----------------");
			player.sendMessage("<§e안내§f> 월드가 잠금 해제 처리 되었습니다. 모든 플레이어의 접근이 허용 됩니다.");
			player.sendMessage("§7----------------");
		}
		return;
	}

	private void pressaddsize(Player player, String[] args) {
		if (Wildgame.world.worldinfo(player.getName().toLowerCase()) == 3) {
			player.sendMessage("§7----------------");
			player.sendMessage("<§e월드 관리§f> 월드가 존재하지 않거나 삭제 되어 있습니다.");
			player.sendMessage("§7----------------");
			return;
		}
		int desize = 0;
		try {
			desize = Integer.parseInt(args[1]);
		} catch (ArrayIndexOutOfBoundsException e) {
			desize = 1;
		} catch (NumberFormatException e) {
			return;
		}
		if (desize < 0) {
			player.sendMessage("§7----------------");
			player.sendMessage("<§e월드 관리§f> 음수의 영역으로 확장 불가능 합니다.");
			player.sendMessage("§7----------------");
			return;
		}
		if (desize > 10) {
			player.sendMessage("§7----------------");
			player.sendMessage("<§e월드 관리§f> 한방에 10칸 이상 확장 불가능 합니다.");
			player.sendMessage("§7----------------");
			return;
		}
		String level = player.getName().toLowerCase();
		int size = Wildgame.db.getworldsize(level);
		int cost;
		if (size <= 75) {
			cost = (((size+desize)*2)*((size+desize)*2))-((size*2)*(size*2))/5;
		} else {
			cost = (((size+desize)*2)*((size+desize)*2))-((size*2)*(size*2))/8;
		}
		if (!Main.Economy24.delmoney(player.getName(), cost)) {
			player.sendMessage("§7----------------");
			player.sendMessage("<§e월드 관리§f> 월드를 확장할 돈이 부족합니다. 확장 비용 : " + cost + " OP");
			player.sendMessage("§7----------------");
			return;
		}
		Wildgame.db.addworldsize(level, (size + (desize * 2)));
		player.sendMessage("§7----------------");
		player.sendMessage("<§e월드 관리§f> 월드를 확장 하였습니다. 총 사용 가능 크기 : " + ((size + desize)*2) + "칸, 확장 비용 " + cost + " OP");
		player.sendMessage("§7----------------");
		return;
	}

	private void pressworldinfo(Player player, String[] args) {
		String level = null;
		try {
			level = args[1].toLowerCase();
		} catch (ArrayIndexOutOfBoundsException e) {
			level = player.getName().toLowerCase();
		}
		if (Wildgame.world.worldinfo(level.toLowerCase()) == 3) {
			player.sendMessage("§7----------------");
			player.sendMessage("<§e월드 조회§f> 조회하실 월드가 존재하지 않습니다.");
			player.sendMessage("§7----------------");
			return;
		}
		String players = " ";
		String[] worldplayers = Wildgame.db.getworldplayer(level);
		for (int i = 0; i <= (worldplayers.length - 1); i++) {
			players = players + (", " + worldplayers[i]);
		}
		player.sendMessage("§7----------------");
		player.sendMessage("<§e월드 조회§f> 소유주 : " + level + "님, 사용가능 크기 : " + Wildgame.db.getworldsize(level)*2
				+ ", 월드 삭제 예정일  : " + Wildgame.db.getworlddeltime(level));
		player.sendMessage("<§e월드 조회§f> 공유된 플레이어 목록 :" + players);
		player.sendMessage("§7----------------");
		return;
	}
	
	public void exit_world(Player player) {
		
	}

}
