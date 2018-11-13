package junghyun.class_box;

import java.util.HashMap;
import java.util.Map;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;

public class Mine24player {
	
	public String name = null; //닉네임
	
	public String low_name = null; //소문자 닉네임
	
	public String chatformat = null;
	
	public boolean logined = true; //로그인 여부
	
	public boolean registered = true;
	
	public boolean is_startup = false; //튜토리얼 진행중 여부
	
	public boolean is_minigame = false; //미니게임 진행여부
	
	public boolean can_pvp = false;
	
	public boolean can_move = true;
	
	public int startup_step = 0; //튜토리얼 단계
	
	public int minigame_type = 0; //미니게임 종류
	
	public long bossbar_id = 0; //보스바 아이디
	
	public int group = 0; //그룹
	
	public long uuid = 0; //UUID
	
	public boolean pass_limt = false;
	
	public Map<Integer, Item> items = null;
	
	public int[] hotbar = null;
	
	public Position worlded_pos1 = null;
	public Position worlded_pos2 = null;
	
	public void set_all(Player player, String chatformat) {
		this.name = player.getName();
		this.low_name = player.getName().toLowerCase();
		this.chatformat = chatformat;
	}
	
	public void backup_inv(Player player) {
		this.items = new HashMap<>(player.getInventory().getContents());
		
		this.hotbar = new int[player.getInventory().getHotbarSize()];

        for (int i = 0; i < this.hotbar.length; i++) {
            this.hotbar[i] = player.getInventory().getHotbarSlotIndex(i);
        }
        
        player.getInventory().clearAll();
		return;
	}
	
	public void set_inv(Player player) {
		player.getInventory().clearAll();
        player.getInventory().setContents(this.items);

        for (int i = 0; i < hotbar.length; i++) {
            player.getInventory().setHotbarSlotIndex(this.hotbar[i], i);
        }

        player.getInventory().sendContents(player);
        player.getInventory().sendArmorContents(player);
        
        this.items = null;
        this.hotbar = null;
        
		return;
	}
}
