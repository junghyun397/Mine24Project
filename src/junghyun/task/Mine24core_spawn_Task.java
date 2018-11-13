package junghyun.task;

import cn.nukkit.Player;
import cn.nukkit.level.Position;
import cn.nukkit.scheduler.PluginTask;
import junghyun.Main;
import junghyun.Mine24core.Mine24core;

public class Mine24core_spawn_Task extends PluginTask<Main> {
	
	private Player player;
	
	public Mine24core_spawn_Task(Mine24core plugin, Player player, Main main) {
		super(main);
		this.player = player;
	}

	@Override
	public void onRun(int arg0) {
		Position mainpoint = Main.main_level_pos;
		this.player.teleport(mainpoint);
		return;
	}

}
