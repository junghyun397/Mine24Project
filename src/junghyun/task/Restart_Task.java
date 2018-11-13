package junghyun.task;

import cn.nukkit.scheduler.PluginTask;
import junghyun.Main;
import junghyun.Minigame.Minigame_base;

public class Restart_Task extends PluginTask<Main> {
	
	private static int seconds = 0;
	
	Main plugin;

	public Restart_Task(Main plugin) {
		super(plugin);
		this.plugin = plugin;
	}
	
	public void add_restart_s(int s) {
		Restart_Task.seconds += s;
	}
	
	public int get_restart_s() {
		return (10800- Restart_Task.seconds);
	}

	@Override
	public void onRun(int arg0) {
		Restart_Task.seconds++;
		Restart_Task.seconds = Math.abs(Restart_Task.seconds);
		if (Restart_Task.seconds == 10200) {
			this.plugin.getServer().broadcastMessage("<§e재부팅 안내§f> 서버가 쾌적한 환경 유지를 위해 10분뒤 재부팅 됩니다.");
			return;
		} else if (Restart_Task.seconds > 10790) {
			this.plugin.getServer().broadcastMessage("<§e재부팅 안내§f> "+(10-(Restart_Task.seconds-10790))+"초 뒤에 재부팅 됩니다.");
		}
		if (Restart_Task.seconds == 10799) {
			Minigame_base.defense.reboot_exit();
		}
		if (Restart_Task.seconds == 10800) {
			this.plugin.getServer().broadcastMessage("<§e재부팅 안내§f> 서버가 쾌적한 환경 유지를 위해 지금 재부팅 됩니다.");
			this.plugin.getServer().shutdown();
			return;
		}
		return;
	}

	private void end_minigame() {

	}

}
