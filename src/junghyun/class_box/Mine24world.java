package junghyun.class_box;

public class Mine24world {
	
	public String name = null;
	
	public int player_cont = 0;
	
	public double[] spawn = null;
	
	public String[] players = null;
	
	public double pos1x = 0;
	public double pos1y = 0;
	public double pos2x = 0;
	public double pos2y = 0;
	
	public void set_all(String level, int[] levelline, double[] spawn, String[] players) {
		this.name = level;
		this.pos1x = levelline[0];
		this.pos1y = levelline[1];
		this.pos2x = levelline[2];
		this.pos2y = levelline[3];
		this.players = players;
		this.spawn = spawn;
	}
	
	public boolean checkpos(String player, double playerx, double playery) {
		if (this.checkpos_other(player)) {
			if (this.checkpos_world(playerx, playery)) {
				return true;
			}
			return false;
		}
		return false;
	}
	
	public boolean checkpos_world(double playerx, double playery) {
		if (playerx <= this.pos1x) {
			return false;
		} else if (playery <= this.pos1y) {
			return false;
		} else if (playerx >= this.pos2x) {
			return false;
		} else if (playery >= this.pos2y) {
			return false;
		}
		return true;
	}
	
	public boolean checkpos_other(String player) {
		int length = this.players.length; // 길이를 얻어옴
		for (int i = 0; i <= length - 1; i++) { // 길이만큼 반복
			if (this.players[i].equals(player)) { // 같은게 바온다면 바로 리턴
				return true;
			}
		}
		return false;
	}
	
}
