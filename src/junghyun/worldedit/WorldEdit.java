package junghyun.worldedit;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import junghyun.Main;
import junghyun.class_box.Mine24player;

public class WorldEdit {
	
	public Main plugin;
	
	public WorldEdit onEnable(Main plugin) {
		this.plugin = plugin;
		this.plugin.getLogger().info("월드에딧 로딩중...");
		this.plugin.getLogger().info("월드에딧 로딩완료...");
		return this;
	}
	
	public int set(Position pos1, Position pos2, Block b) {
        int minX = (int) Math.min(pos1.x, pos2.x);
        int minY = (int) Math.min(pos1.y, pos2.y);
        int minZ = (int) Math.min(pos1.z, pos2.z);
        int maxX = (int) Math.max(pos1.x, pos2.x);
        int maxY = (int) Math.max(pos1.y, pos2.y);
        int maxZ = (int) Math.max(pos1.z, pos2.z);
        Vector3 pos = new Vector3();
        int blocks = 0;
        
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    pos1.getLevel().setBlock(pos.setComponents(x, y, z), b, true, false);
                    blocks++;
                }
            }
        }
        return blocks;
    }
	
	public boolean press_cmd(CommandSender sender, int type, String[] args) {
		if (!sender.isOp()) {
			return true;
		}
		if (type == 1) {
			if (args[0].equals("1")) {
				this.set_pos1(this.plugin.getServer().getPlayer(sender.getName()), null, false);
			} else if (args[0].equals("2")) {
				this.set_pos2(this.plugin.getServer().getPlayer(sender.getName()), null, false);
			}
		} else if (type == 2) {
			Item item = new Item(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
			this.start_set(this.plugin.getServer().getPlayer(sender.getName()), item);
		}
		return true;
	}
	
	public void set_pos1(Player player, Position pos, boolean type) {
		Mine24player player_box = Main.players_.get(player.getName());
		if (type) {
			if ((pos == null) && (this.check_fucking_000_trolling(pos))) {
				player.sendMessage("§7씹새끼야");
				return;
			}
			player_box.worlded_pos1 = pos;
			Main.players_.put(player.getName(), player_box);
			player.sendMessage("§7좌표 1번" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "좌표 지정됨 (블록 터치)");
			return;
		}
		player_box.worlded_pos1 = new Position(Math.round(player.getX()), Math.round(player.getY()), Math.round(player.getZ()), player.getLevel());
		Main.players_.put(player.getName(), player_box);
		player.sendMessage("§7좌표 1번" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "좌표 지정됨 (서있는 위치)");
		return;
	}
	
	public void set_pos2(Player player, Position pos, boolean type) {
		Mine24player player_box = Main.players_.get(player.getName());
		if (type) {
			if ((pos == null) && (this.check_fucking_000_trolling(pos))) {
				player.sendMessage("§7씹새끼야");
				return;
			}
			player_box.worlded_pos2 = pos;
			Main.players_.put(player.getName(), player_box);
			player.sendMessage("§7좌표 2번" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "좌표 지정됨 (블록 터치)");
			return;
		}
		player_box.worlded_pos2 = new Position(Math.round(player.getX()), Math.round(player.getY()), Math.round(player.getZ()), player.getLevel());
		Main.players_.put(player.getName(), player_box);
		player.sendMessage("§7좌표 2번" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "좌표 지정됨 (서있는 위치)");
		return;
	}
	
	public void start_set(Player player, Item item) {
		Mine24player player_box = Main.players_.get(player.getName());
		if (player_box.worlded_pos1 == null) {
			if (player_box.worlded_pos2 == null) {
			}
			player.sendMessage("§7좌표 지정오류");
			return;
		}
		
		if (this.check_fucking_000_trolling(player_box.worlded_pos1)) {
			player.sendMessage("§7씹새끼야");
			return;
		} else if (this.check_fucking_000_trolling(player_box.worlded_pos2)) {
			player.sendMessage("§7씹새끼야");
			return;
		}
		
		if (!(player_box.worlded_pos1.getLevel().getName().equals(player_box.worlded_pos2.getLevel().getName()))) {
			return;
		}
		this.plugin.getServer().getLogger().info(" ID " + item.getId());
		Block block = Block.get(item.getId(), item.getDamage());
		int blocks = this.set(player_box.worlded_pos1, player_box.worlded_pos2, block);
		player.sendMessage("§7" + blocks + "개의 블록을 설치 하였습니다. 아이디 " + item.getId() + " 재질" + item.getDamage() + ".");
		player_box.worlded_pos1 = null;
		player_box.worlded_pos2 = null;
		Main.players_.put(player.getName(), player_box);
		return;
	}
	
	public boolean check_fucking_000_trolling(Position pos) {
		if ((pos.getX() == 0) && (pos.getY() == 0) && (pos.getZ() == 0)) {
			return true;
		}
		return false;
	}
	
}
