package junghyun;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.plugin.PluginBase;

import junghyun.Auth24.auth24;
import junghyun.ClassManager.ClassManager;
import junghyun.Economy24.Economy24;
import junghyun.Mine24core.Mine24core;
import junghyun.Minigame.Minigame_base;
import junghyun.task.Wildgame_Task;
import junghyun.task.popup_task;
import junghyun.task.Restart_Task;
import junghyun.task.tick_5_respawn_task;
import junghyun.worldedit.WorldEdit;
import junghyun.Shop24.Shop24;
import junghyun.Wildgame.Wildgame;
import junghyun.class_box.Mine24player;
import junghyun.db.MysqlLib;
import junghyun.event.EventListener;
import junghyun.playerlimit.playerlimit;

public class Main extends PluginBase {

	public static Economy24 Economy24;
	public static Wildgame Wildgame;
	public static Shop24 Shop24;
	public static playerlimit playerlimit;
	public static Mine24core Mine24core;
	public static auth24 auth24;
	public static ClassManager ClassManager;
	public static WorldEdit WorldEdit;
	public static popup_task popup_task;
	public static Minigame_base minigame_base;
	public static MysqlLib mysqllib;
	public static Restart_Task restarter;
	
	public static Map<String, Mine24player> players_ = new HashMap<>();
	
	public static Position main_level_pos = null;
	public static Level main_level = null;
	
	public static Random rand = new Random();

	@Override
	public void onEnable() {
		
		this.getLogger().info("Mine24 를 시작합니다. 통합플러그인 메인서버.");
		
		this.getLogger().info("기능 모듈 로딩 시작");
		// 플러그인 로딩 시작
		Main.mysqllib = new MysqlLib().onEnable(this);
		Main.WorldEdit = new WorldEdit().onEnable(this);
		Main.Economy24 = new Economy24().onEnable(this);
		Main.Wildgame = new Wildgame().onEnable(this);
		Main.ClassManager = new ClassManager().onEnable(this);
		Main.Shop24 = new Shop24().onEnable(this);
		Main.auth24 = new auth24().onEnable(this);
		Main.playerlimit = new playerlimit().onEnable(this);
		Main.Mine24core = new Mine24core().onEnable(this);
		Main.minigame_base = new Minigame_base().onEnable(this);
		
		Main.popup_task = new popup_task(this);
		
		Main.restarter = new Restart_Task(this);
		
		this.getLogger().info("이벤트 리스너 로딩");
		
		//이벤트 리스너 불러오기
		this.getServer().getPluginManager().registerEvents(new EventListener(this), this);
		
		this.getLogger().info("스케줄러 로딩");

		// 재부팅 장치 불러오기

		this.getServer().getScheduler().scheduleRepeatingTask(Main.restarter, 20);
		
		// 1/2 큐 불러오기
		
		this.getServer().getScheduler().scheduleRepeatingTask(new Wildgame_Task(this), 1);
		
		//메시지 연속출력장치 불러오기
		
		this.getServer().getScheduler().scheduleRepeatingTask(Main.popup_task, 10);
		
		this.getLogger().info("젠체 로딩완료.");
	}

	//커멘드 리스너
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		String name = command.getName();
		switch (name) {
			case "가입":
				Main.auth24.press_cmd(sender, 1, args);
				return true;
			case "로그인":
				Main.auth24.press_cmd(sender, 2, args);
				return true;
			case "비밀번호":
				Main.auth24.press_cmd(sender, 3, args);
				return true;
			case "월드":
				Main.Wildgame.press_cmd(sender, 0, args);
				return true;
			case "이동":
				Main.Mine24core.press_cmd(sender, 2, args);
				return true;
			case "스폰":
				Main.Mine24core.press_cmd(sender, 1, args);
				return true;
			case "메시지":
				Main.ClassManager.press_cmd(sender, 2, args);
				return true;
			case "인원":
				Main.ClassManager.press_cmd(sender, 3, args);
				return true;
			case "칭호지정":
				Main.ClassManager.press_cmd(sender, 1, args);
				return true;
			case "경제":
				Main.Economy24.press_cmd(sender, 0, args);
				return true;
			case "b":
				Main.playerlimit.press_cmd(sender, 1, args);
				return true;
			case "k":
				Main.playerlimit.press_cmd(sender, 2, args);
				return true;
			case "i":
				Main.playerlimit.press_cmd(sender, 3, args);
				return true;
			case "bi":
				Main.playerlimit.press_cmd(sender, 4, args);
				return true;
			case "l":
				Main.playerlimit.press_cmd(sender, 5, args);
				return true;
			case "we":
				Main.WorldEdit.press_cmd(sender, 1, args);
				return true;
			case "wes":
				Main.WorldEdit.press_cmd(sender, 2, args);
				return true;
			case "/":
				Main.Mine24core.press_cmd(sender, 3, args);
				return true;
			case "삭제":
				Main.Mine24core.press_cmd(sender, 5, args);
				return true;
			case "수령":
				Main.Shop24.press_cmd(sender, 1, args);
				return true;
			case "참여":
				Main.minigame_base.press_cmd(sender, 1, args);
				return true;
			case "인증":
				Main.auth24.press_cmd(sender, 4, args);
				return true;
			case "재부팅":
				Main.Mine24core.press_cmd(sender, 6, args);
				return true;
		}
		return true;
	}
	
	public void tick_5_tp(Player player, Position pos) {
		this.getServer().getScheduler().scheduleDelayedTask(new tick_5_respawn_task(player, pos, this), 5);
	}
	
	public String get_timestamp() {
		String timestamp_long = String.valueOf(Calendar.getInstance().getTime().getTime());
		return timestamp_long.substring(0, 10);
	}
	
	public void log_info(String msg) {
		this.getServer().getLogger().info(msg);
	}

}
