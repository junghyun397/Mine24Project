package junghyun.timer;

import cn.nukkit.scheduler.PluginTask;
import junghyun.Main;
import junghyun.Minigame.Minigame_base;

public class defense_count_down_task extends PluginTask<Main> {
	
	public static int seconds = 0;
	
	public int count_type = 0;
	
	public int min_time = 0;

	public defense_count_down_task(Main plugin) {
		super(plugin);
	}
	
	@Override
	public void onRun(int arg0) {
		defense_count_down_task.seconds++;
		//		this.seconds = this.seconds+5;
		if (this.count_type == 1)  { // 30초
			if (defense_count_down_task.seconds >= 30) {
				this.reset_task();
				Minigame_base.defense.start_game();
			} else {
				Minigame_base.defense.broadcastTitle("§e"+(30-defense_count_down_task.seconds)+"초§7 뒤 게임 시작..");
			}
		} else if (this.count_type == 2) { // 5분
			if (defense_count_down_task.seconds >= 300) {
				this.reset_task();
				Minigame_base.defense.end_game();
			} else if ((defense_count_down_task.seconds % 60) == 0) {
				this.min_time++;
				Minigame_base.defense.update_bossbar(5-(defense_count_down_task.seconds/60));
			}
		}
	}
	
	/*
	 * 
	 */
	
	public void count_down_30s() {
		this.count_type = 1;
		defense_count_down_task.seconds = 0;
	}
	
	public void count_down_5m() {
		this.count_type = 2;
		defense_count_down_task.seconds = 0;
	}
	
	public void reset_task() {
		this.count_type = 0;
		defense_count_down_task.seconds = 0;
	}
}
