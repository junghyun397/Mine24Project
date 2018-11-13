package junghyun.task;

import cn.nukkit.Player;
import cn.nukkit.scheduler.PluginTask;
import junghyun.Main;
import junghyun.playerlimit.playerlimit;

public class World_ReCreate_task extends PluginTask<Main> {
	
	private Player player;
	
	private playerlimit plugin;

	public World_ReCreate_task(playerlimit plugin, Player player, Main main) {
		super(main);
		this.plugin = plugin;
		this.player = player;
	}

	@Override
	public void onRun(int arg0) {
		this.plugin._recreate_world(this.player);
		return;
	}

}
