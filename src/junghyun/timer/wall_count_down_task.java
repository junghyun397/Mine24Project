package junghyun.timer;

import cn.nukkit.scheduler.PluginTask;
import junghyun.Main;
import junghyun.Minigame.Minigame_base;

public class wall_count_down_task extends PluginTask<Main> {
	
	public static int seconds = 0;
	
	public int count_type = 0;

	public wall_count_down_task(Main plugin) {
		super(plugin);
	}

	@Override
	public void onRun(int arg0) {
		wall_count_down_task.seconds++;
		//		this.seconds = this.seconds+5;
		if (this.count_type == 1)  { // 30초
			if (wall_count_down_task.seconds >= 30) {
				this.reset_task();
				Minigame_base.wall.start_game();
			} else {
				Minigame_base.wall.broadcastTitle("§e"+(30-wall_count_down_task.seconds)+"초§7 뒤 게임 시작..");
			}
		} else if (this.count_type == 2) { // 10초
			if (wall_count_down_task.seconds >= 10) {
				this.reset_task();
				Minigame_base.wall.start_pvp();
			} else {
				Minigame_base.wall.broadcastTitle("§e"+(10-wall_count_down_task.seconds)+"초§7 뒤 PVP 시작..");
			}
		} else if (this.count_type == 3) { // 10분
			if (wall_count_down_task.seconds >= 600) {
				this.reset_task();
				Minigame_base.wall.pvp_count_down_start();
			} else if ((wall_count_down_task.seconds % 60) == 0) {
				Minigame_base.wall.update_bossbar(10-(wall_count_down_task.seconds/60));
			}
		}
	}
	
	/*
	 * 
	 */
	
	public void count_down_10s() {
		this.count_type = 2;
		wall_count_down_task.seconds = 0;
	}
	
	public void count_down_30s() {
		this.count_type = 1;
		wall_count_down_task.seconds = 0;
	}
	
	public void count_down_10m() {
		this.count_type = 3;
		wall_count_down_task.seconds = 0;
	}
	
	public void reset_task() {
		this.count_type = 0;
		wall_count_down_task.seconds = 0;
	}
}
