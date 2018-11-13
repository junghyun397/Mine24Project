package junghyun.task;

import cn.nukkit.Player;
import cn.nukkit.level.Position;
import cn.nukkit.scheduler.PluginTask;
import junghyun.Main;

public class tick_5_respawn_task extends PluginTask<Main> {
	
	private Player player;
	
	private Position pos;
	
	public tick_5_respawn_task(Player player, Position pos, Main main) {
		super(main);
		this.player = player;
		this.pos = pos;
	}

	@Override
	public void onRun(int arg0) {
		this.player.teleport(this.pos);
		return;
	}

}
