package junghyun.task;

import cn.nukkit.Player;
import cn.nukkit.scheduler.PluginTask;
import junghyun.Main;
import junghyun.playerlimit.playerlimit;

public class Find_water_task extends PluginTask<Main> {
	
	private Player player;
	
	private playerlimit plugin;

	public Find_water_task(playerlimit plugin, Player player, Main main) {
		super(main);
		this.plugin = plugin;
		this.player = player;
	}

	@Override
	public void onRun(int arg0) {
		this.plugin.find_water(this.player);
		return;
	}

}
