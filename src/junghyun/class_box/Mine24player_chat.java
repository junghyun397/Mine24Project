package junghyun.class_box;

import cn.nukkit.Player;

public class Mine24player_chat {
	
	public String name = null; //닉네임
	
	public String low_name = null; //소문자 닉네임
	
	public boolean can_chat = false; //로그인 여부
	
	public String title = null;
	
	public String tag = null;
	
	public int minigame_type = 0; //미니게임 종류
	
	public void set_all(Player player, String title, String tag) {
		this.name = player.getName();
		this.low_name = player.getName().toLowerCase();
		this.tag = tag;
		this.title = title;
	}
	
	public void can_chat() {
		this.can_chat = true;
	}
	
	public boolean able_allchat() {
		if (!can_chat) {
			return false;
		}
		return true;
	}
	
	public String getchat(String msg) {
		return "<"+this.title+"> "+msg;
	}
}
