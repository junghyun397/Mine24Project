package junghyun.Wildgame_world;

import java.io.File;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import junghyun.Wildgame.Wildgame;
import junghyun.class_box.Mine24world;

public class WorldManager {
	
	Wildgame plugin;
	 
	public WorldManager(Wildgame plugin) {
		this.plugin = plugin;
	}
	
	/**
	 * @param level
	 * @return 월드 생성 시도, 성공 true
	 */
	public boolean createworld(String level) {
		if(this.plugin.plugin.getServer().generateLevel(level.toLowerCase())) {
			Wildgame.db.createworlddata(level.toLowerCase());
			return true;
		}
		return false;
	}
	
	public Position findspawn(Level level) {
		Position pos = new Position(128, 127, 128, level);
		int id = 0;
		for (int i = 0; i < 127; i++) {
			int pos_y = 128-i;
			id = level.getBlock(new Position(128, pos_y, 128, level)).getId();
			if (id != 0) {
				if ((id == 8) || (id == 9)) {
					return null;
				}
				pos = new Position(128, pos_y+2, 128, level);
				return pos;
			}
		}
		return null;
	}
	
	public Position findspawn_no_water(Level level) {
		Position pos = new Position(128, 127, 128, level);
		int id = 0;
		for (int i = 0; i < 127; i++) {
			int pos_y = 128-i;
			id = level.getBlock(new Position(128, pos_y, 128, level)).getId();
			if (id != 0) {
				pos = new Position(128, pos_y+2, 128, level);
				return pos;
			}
		}
		return null;
	}
	

	/**
	 * @param level
	 * 월드 삭제
	 */
	public void delworld(String level_big) {
		String level = level_big.toLowerCase();
		File reg_file = new File("/root/worlds/"+level+"/region"); //하위 폴더를 확정
		String[] fnameList = reg_file.list();
		int reg_fCnt = fnameList.length;
		String childPath = null;
	    for(int i = 0; i < reg_fCnt; i++) {
	    	childPath = "/root/worlds/"+level+"/region/"+fnameList[i];
	    	File f = new File(childPath);
	    	f.delete();
	    }
	    reg_file.delete(); //region 삭제
	    File dat_file = new File("/root/worlds/"+level+"/level.dat"); //level.dat 삭제
	    dat_file.delete();
	    File file = new File("/root/worlds/"+level); // 폴더 삭제
	    file.delete();
	    this.delworld_old(level_big);
		return;
	}
	
	public void repair_world() {
		File reg_file = new File("/root/worlds");
		String[] fnameList = reg_file.list();
		int reg_fCnt = fnameList.length;
		String world_path = null;
	    for(int i = 0; i < reg_fCnt; i++) { //월드 복구 시작
	    	world_path = fnameList[i];
	    	if (world_path != null) {
	    		if (!world_path.contains(".old")) {
		    		this.plugin.plugin.log_info(world_path + " DB 생성 완료");
		    		Wildgame.db.createworlddata(world_path);
		    	}
	    	}
	    }
	    
	}
	
	public void delworld_old(String level_big) {
		try {
			String level = level_big.toLowerCase() + ".old";
			File reg_file = new File("/root/worlds/" + level + "/region"); // 하위
																			// 폴더를
																			// 확정
			String[] fnameList = reg_file.list();
			int reg_fCnt = fnameList.length;
			String childPath = null;
			for (int i = 0; i < reg_fCnt; i++) {
				childPath = "/root/worlds/" + level + "/region/" + fnameList[i];
				File f = new File(childPath);
				f.delete();
			}
			reg_file.delete(); // region 삭제
			File dat_file = new File("/root/worlds/" + level + "/level.dat.old"); // level.dat
																					// 삭제
			dat_file.delete();
			File file = new File("/root/worlds/" + level); // 폴더 삭제
			file.delete();
		} catch (Exception e) {
			return;
		}
		return;
	}
	
	/**
	 * @param level
	 * @return 월드의 저장소 크기 (킬로바이트) 를 출력
	 */
	public int getworldsize(String level) {
		return 0;
	}
	
	/**
	 * @param level
	 * 월드 로딩
	 */
	public void loadworld(String level) {
		this.plugin.plugin.getServer().loadLevel(level);
		return;
	}
	
	/**
	 * @param name
	 * 월드 로딩해제
	 */
	public void unloadworld(String name_big) {
		String name = name_big.toLowerCase();
		Level level = this.plugin.plugin.getServer().getLevelByName(name);
		this.plugin.plugin.getServer().unloadLevel(level);
		this.plugin.maps.remove(name);
		return;
	}
	/**
	 * @param level
	 * @return 월드 로딩됨 1
	 * 로딩안됨 2
	 * 생성안됨 3
	 */
	public int worldinfo(String level_big) {
		String level = level_big.toLowerCase();
		if (!this.plugin.plugin.getServer().isLevelGenerated(level)) {
			
			return 3;
		}
		if (!this.plugin.plugin.getServer().isLevelLoaded(level)) {
			return 2;
		}
		return 1;
	}
	
	public boolean get_able_unload(Level level) {
		Entity[] le = level.getEntities();
		int i = 0;
		while (i != le.length) {
			i++;
			if (le[i] instanceof Player) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * @param player
	 * @param level
	 * @return 플레이어를 월드로 이동시킴, 실패시 false
	 */
	public boolean joinworld(Player player, String big_level) {
		String level = big_level.toLowerCase();
		int worldinfo = this.worldinfo(level);
		if (worldinfo == 3) {
			return false;
		}
		else if (worldinfo == 2) {
			Mine24world world_box = this.plugin.maps.get(level);
			double[] pos_spawn = world_box.spawn;
			this.loadworld(level);
			if (pos_spawn == null) {
				player.teleport(this.plugin.plugin.getServer().getLevelByName(level).getSafeSpawn());
				return true;
			}
			Position pos = new Position(pos_spawn[0], pos_spawn[1], pos_spawn[2], this.plugin.plugin.getServer().getLevelByName(level));
			player.teleport(pos);
		}
		else if (worldinfo == 1) {
			Mine24world world_box = this.plugin.maps.get(level);
			double[] pos_spawn = world_box.spawn;
			if (pos_spawn == null) {
				player.teleport(this.plugin.plugin.getServer().getLevelByName(level).getSafeSpawn());
				return true;
			}
			Position pos = new Position(pos_spawn[0], pos_spawn[1], pos_spawn[2], this.plugin.plugin.getServer().getLevelByName(level));
			player.teleport(pos);
		}
		return true;
	}
	
	public int check_level_remove(String level) {
		Level class_level = this.plugin.plugin.getServer().getLevelByName(level);
		Position pos = class_level.getSafeSpawn();
		pos.y = (pos.y-1);
		return class_level.getBlock(pos).getId();
	}
	
	/**
	 * @param player
	 * @param level
	 * 월드를 언로드하고 플레이어를 스폰으로 이동시킴
	 */
	public void leaveworld(Player player, String level) {
		return;
	}

}
