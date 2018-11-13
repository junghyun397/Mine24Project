package junghyun.Minigame.surround;

import cn.nukkit.Player;
import cn.nukkit.level.Level;
import junghyun.Main;
import junghyun.Minigame.Minigame_base;
import junghyun.Minigame.wall.Wall;
import junghyun.Wildgame.Wildgame;
import junghyun.class_box.surround_player;
import junghyun.class_box.wall_player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Surround {

    public Main plugin;
    public Minigame_base base;

    public boolean is_pvp = false;

    public static Map<String, surround_player> players = new HashMap<>();

    public Surround onEnable() {
        this.plugin = plugin;
        this.plugin.getLogger().info("미니게임 Surround 로딩중...");
        this.base = base;
        this.plugin.getLogger().info("미니게임 Surround 로딩완료....");
        return this;
    }

    //월드 관리

    public Level create_tempworld() {
        int info = Wildgame.world.worldinfo("-Surround"); //확인
        if (info == 1) {
            Level level = this.plugin.getServer().getLevelByName("-Surround"); //지정
            this.plugin.getServer().unloadLevel(level); //언로드
            this.del_tempworld(); //삭제
            this.plugin.getServer().generateLevel("-Surround"); //생성
            return this.plugin.getServer().getLevelByName("-Surround"); //반환
        } else if (info == 2) {
            this.del_tempworld(); //삭제
            this.plugin.getServer().generateLevel("-Surround"); //생성
            return this.plugin.getServer().getLevelByName("-Surround");  //반환
        }
        this.plugin.getServer().generateLevel("-Surround"); //생성
        return this.plugin.getServer().getLevelByName("-Surround"); //반환
    }

    public void del_tempworld() {
        File reg_file = new File("/root/worlds/-Surround/region"); //하위 폴더를 확정
        String[] fnameList = reg_file.list();
        int reg_fCnt = fnameList.length;
        String childPath = null;
        for(int i = 0; i < reg_fCnt; i++) {
            childPath = "/root/worlds/-Surround/region/"+fnameList[i];
            File f = new File(childPath);
            f.delete();
        }
        reg_file.delete(); //region 삭제
        File dat_file = new File("/root/worlds/-Surround/level.dat"); //level.dat 삭제
        dat_file.delete();
        File file = new File("/root/worlds/-Surround"); // 폴더 삭제
        file.delete();
        this.del_tempworld_old();
    }

    public void del_tempworld_old() {
        try {
            File reg_file = new File("/root/worlds/-Surround.old/region"); // 하위
            // 폴더를
            // 확정
            String[] fnameList = reg_file.list();
            int reg_fCnt = fnameList.length;
            String childPath = null;
            for (int i = 0; i < reg_fCnt; i++) {
                childPath = "/root/worlds/-Surround.old/region/" + fnameList[i];
                File f = new File(childPath);
                f.delete();
            }
            reg_file.delete(); // region 삭제
            File dat_file = new File("/root/worlds/-Surround.old/level.dat.old"); // level.dat
            // 삭제
            dat_file.delete();
            File file = new File("/root/worlds/-Surround.old"); // 폴더 삭제
            file.delete();
        } catch (Exception e) {
            return;
        }
        return;
    }

    //배열 관리

    public static int get_alive() {
        return Surround.players.size();
    }

    //입장, 퇴장

    public void join_game(Player player) {
        if (Wall.players.get(player.getName()) != null) {
            player.sendMessage("§7----------------");
            player.sendMessage("<§e미니게임§f> 이미 대기중 입니다.");
            player.sendMessage("§7----------------");
            return;
        }
        if (Wall.players.size() >= 40) {
            player.sendMessage("§7----------------");
            player.sendMessage("<§e미니게임§f> 미니게임에 입장할수 없습니다. 사람이 너무 많습니다.");
            player.sendMessage("§7----------------");
            return;
        }
        if (Wall.in_game) {
            player.sendMessage("§7----------------");
            player.sendMessage("<§e미니게임§f> 미니게임이 진행중 입니다. 잠시 기다려 주시기 바랍니다.");
            player.sendMessage("§7----------------");
            return;
        }

        surround_player surround_player = new surround_player();
        surround_player.player = player;

        Surround.players.put(player.getName(), surround_player);
        this.update_sign_stay(Main.Mine24core.mainlevel);
        String msg_list = " ";
        for (Map.Entry<String, wall_player> entry: Wall.players.entrySet()) {
            msg_list = msg_list+(entry.getValue().getPlayer().getName())+" ";
        }
        player.sendMessage("§7----------------");
        player.sendMessage("<§e미니게임§f> 미니게임 Wall 에 참여 하셨습니다.");
        player.sendMessage("<§e미니게임§f> 현재 대기중인 인원 -" + msg_list);
        player.sendMessage("§7----------------");

        this.broadcastPopUP("§7"+player.getName()+"님이 참여 하셨습니다.");
//        player.teleport(new Position(-470, 8, 1756, Main.Mine24core.mainlevel));
//        this.setup_player(player);
//        if (Wall.players.size() == 4) {
//            this.setup_game();
//        }
        return;
    }

    //표지판

    private void update_sign_stay(Level mainlevel) {
    }

    //메시징

    public void broadcastPopUP(String msg) {
        for (Map.Entry<String, surround_player> entry: Surround.players.entrySet()) {
            entry.getValue().getPlayer().sendTip(msg);
        }
    }

    public void broadcastTitle(String msg) {
        for (Map.Entry<String, surround_player> entry: Surround.players.entrySet()) {
            entry.getValue().getPlayer().sendTitle(msg);
        }
    }

    public void broadcastMsg(String msg) {
        for (Map.Entry<String, surround_player> entry: Surround.players.entrySet()) {
            entry.getValue().getPlayer().sendMessage("§7----------------");
            entry.getValue().getPlayer().sendMessage(msg);
            entry.getValue().getPlayer().sendMessage("§7----------------");
        }
    }
}
