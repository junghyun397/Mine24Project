package junghyun.task;

import cn.nukkit.scheduler.PluginTask;
import junghyun.Main;

public class Wildgame_Task extends PluginTask<Main> {
	
	public static boolean move_cue;
	
	public byte ticks;

	public Wildgame_Task(Main plugin) {
		super(plugin);
	}

	@Override
	public void onRun(int arg0) {
		if (this.ticks == 10) {
			Wildgame_Task.move_cue = false;
			this.ticks = 0;
			return;
		}
		this.ticks++;
		Wildgame_Task.move_cue = true;
		return;
	}

}
