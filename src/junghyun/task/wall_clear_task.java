package junghyun.task;

import cn.nukkit.scheduler.PluginTask;
import junghyun.Main;
import junghyun.Minigame.wall.Wall;

public class wall_clear_task extends PluginTask<Main> {
	
	private Wall plugin;

	public wall_clear_task(Wall plugin, Main main) {
		super(main);
		this.plugin = plugin;
	}

	@Override
	public void onRun(int arg0) {
		this.plugin.clear_5_game();
		return;
	}

}
