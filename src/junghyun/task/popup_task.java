package junghyun.task;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import cn.nukkit.Player;
import cn.nukkit.scheduler.PluginTask;
import junghyun.Main;

public class popup_task extends PluginTask<Main> {
	
	public int seconds = 0;
	
	public String[] popup = new String[20];
	
	public Map<String, int[]> players = new HashMap<String, int[]>();
	
	Main plugin;

	public popup_task(Main plugin) {
		super(plugin);
		this.plugin = plugin;
		this.popup[1] = "§e월드 재생성 중입니다. 잠시 기다려 주시기 바랍니다.";
		this.popup[2] = "§e/이동 상점§7 명령어로 상점으로 이동 하시기 바랍니다.";
		this.popup[3] = "§7인벤토리에 있는 밀을 꺼내 중앙의 회색 블럭을 터치 하시기 바랍니다.";
		this.popup[4] = "§e/경제 내돈§7 명령어로 자신의 돈을 확인 하시기 바랍니다.";
		this.popup[5] = "§e/월드 이동§7 명령어로 자신의 월드로 돌아 가보시기 바랍니다.";
		this.popup[6] = "§e/월드 확장 10§7 명령어로 자신의 월드를 확장 해보시기 바랍니다.";
		this.popup[7] = "§e/월드 스폰설정 또는 /건너뛰기§7 명령어로 월드의 스폰을 설정 해보시기 바랍니다.";
		this.popup[8] = "§e/가입 <원하는 비밀번호>§7 명령어로 가입을 해주시기 바랍니다.";
		this.popup[9] = "§e/로그인 <비밀번호>§7 로 로그인 해주시기 바랍니다.";
		this.popup[10] = "§e/가입 <비밀번호>§7 로 가입 하시면 더욱 많은 혜택을 누리실 수 있습니다.";
		this.popup[11] = "§7잠시 기다려 주시면 월드로 이동 됩니다...";
		this.popup[12] = "§7튜토리얼을 시작하는 중입니다... 10초만 기다려 주시기 바랍니다.";
		this.popup[13] = "§7블럭 설치가 안될경우 재접속 해보시기 바랍니다.";
	}
	
	/*
	 * 
	 */

	@Override
	public void onRun(int arg0) {
		Map<UUID, Player> server_players = this.plugin.getServer().getOnlinePlayers();
		for (Map.Entry<UUID, Player> entry: server_players.entrySet()) {
			Player player = entry.getValue();
			try {
				int[] no = this.players.get(player.getName());
				if (no[0] == 0) {
					player.sendTip("§7현재 §e" + no[1] + " OP§7 를 가지고 있습니다.");
				} else if (no[0] == -1) {
					return;
				} else {
					player.sendTip(this.popup[no[0]]);
				}
			} catch (NullPointerException e) {
			}
		}
		return;
	}
	
	public void add_new(String player, int money) {
		int[] arry_popup = new int[2];
		arry_popup[0] = 0;
		arry_popup[1] = money;
		this.players.put(player, arry_popup);
		return;
	}
	
	public void set_popup(String player, int code) {
		int[] popup = this.players.get(player);
		if (popup == null) {
			int[] arry_popup = new int[2];
			arry_popup[0] = code;
			arry_popup[1] = 0;
			this.players.put(player, arry_popup);
			return;
		}
		popup[0] = code;
		popup[1] = 0;
		this.players.put(player, popup);
	}
	
	public void del_popup(String player, int money) {
		int[] arry_popup = new int[2];
		arry_popup[0] = 0;
		arry_popup[1] = money;
		this.players.put(player, arry_popup);
		return;
	}
	
	public void update_money(String player, int money) {
		int[] arry_popup = this.players.get(player);
		if (arry_popup[0] != 0) {
			return;
		}
		arry_popup[1] = money;
		this.players.put(player, arry_popup);
		return;
	}
	
	public void off_popup(String player) {
		int[] arry_popup = new int[2];
		arry_popup[0] = -1;
		arry_popup[1] = 0;
		this.players.put(player, arry_popup);
	}
	
	public void broadcastPopUP(String tip) {
		Map<UUID, Player> server_players = this.plugin.getServer().getOnlinePlayers();
		for (Map.Entry<UUID, Player> entry: server_players.entrySet()) {
			entry.getValue().sendPopup(tip);
		}
	}

}
