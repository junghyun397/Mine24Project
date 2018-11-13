package junghyun.task;

import cn.nukkit.Player;
import cn.nukkit.level.Position;
import cn.nukkit.scheduler.PluginTask;
import junghyun.Main;

public class defense_respawn_task extends PluginTask<Main> {
	
	private Player player;
	
	private Position pos;

	public defense_respawn_task(Position pos, Player player, Main main) {
		super(main);
		this.player = player;
		this.pos = pos;
	}

	@Override
	public void onRun(int arg0) {
		this.player.teleport(pos);
		return;
	}

}
