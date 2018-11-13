package junghyun.playerlimit;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Random;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import junghyun.db.MysqlLib;
import junghyun.task.Find_water_task;
import junghyun.task.World_ReCreate_task;
import junghyun.task.delay_create_task;
import junghyun.Main;
import junghyun.ClassManager.ClassManager;
import junghyun.Wildgame.Wildgame;
import junghyun.class_box.Mine24player;

public class playerlimit {
	
	public MysqlLib mysqllib = new MysqlLib();
	
	public Position mainpoint;
	
	public Main plugin;
	
	public Level mainlevel;
	
	public playerlimit onEnable(Main plugin) {
		this.plugin = plugin;
		this.plugin.getLogger().info("경고/ 차단/ 튜토리얼 처리 로딩중...");
		this.mainlevel = this.plugin.getServer().getLevelByName("main");
		this.mainpoint = new Position(-463, 69, 1055, (this.plugin.getServer().getLevelByName("main")));
		this.plugin.getLogger().info("경고/ 차단/ 튜토리얼 처리 로딩완료...");
		return this;
	}
	
	public void onplayerjoin(Player player, String name) {
		ResultSet sqlresult = this.getplayerdata(name);
		if (this.chackregister(sqlresult)) {
			String result = this.getcanjoin(player, sqlresult);
			if (result != null) {
				player.close(player.getLeaveMessage(), result);
				return;
			}
		} else {
			this.createusrdata(name);
			ResultSet sqlresult_ip = this.getipban(player.getAddress());
			if (sqlresult_ip != null) {
				player.close(player.getLeaveMessage(), this.getbanmsg(player, sqlresult_ip, 2));
			}
			return;
		}
	}
	
	
	//튜토리얼
	
	public void create_world(Player player) {
		String name = player.getName().toLowerCase();
		Main.Wildgame.joinworld(name, name, player);
		this.find_water(player);
	}
	
	public void find_water(Player player) {
		try {
			String name = player.getName().toLowerCase();
			Level level = this.plugin.getServer().getLevelByName(name);
			Position pos = Wildgame.world.findspawn(level);
			if (pos == null) { // 스폰 아래가 물일경우
				pos = Wildgame.world.findspawn_no_water(level);
				Block block = Block.get(2, 0);
				Vector3 pos_s = new Vector3();
				for (int i = 0; i > 3; i++) {
					for (int ii = 0; ii > 3; ii++) {
						pos.getLevel().setBlock(pos_s.setComponents(127+i, pos.y, 127+ii), block, true, false);
					}
				}
				pos = new Position(128, pos.getY()+2, 128, level);
			}
			Main.popup_task.set_popup(player.getName(), 2);
			Wildgame.db.setworldspawn(name, 128, pos.y, 128);
			player.teleport(pos);
			Main.playerlimit.pressmsg(player, 3, 11);
			Main.playerlimit.pressmsg(player, 4, 11);
			Main.playerlimit.update_type(name.toLowerCase(), 4);
			Mine24player player_box = Main.players_.get(player.getName());
			player_box.startup_step = 4;
			Main.players_.put(name, player_box);
			player.getInventory().addItem(new Item(296, 0, 16, "밀"));
		} catch (NullPointerException e){
			this.plugin.getServer().getScheduler().scheduleDelayedTask(new Find_water_task(this, player, this.plugin), 40);
		}
	}
	
	public void start_startup(Player player) {
		String name = player.getName().toLowerCase();
		ResultSet result_sql = this.getplayerdata(name);
		Mine24player player_box = Main.players_.get(player.getName());
		int type = 0;
		try {
			if (result_sql.next()) {
				type = result_sql.getInt("step");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			player.close(player.getLeaveMessage(), "불량 클라이언트");
			return;
		}
		if (type == 100) {
			player_box.pass_limt = true;
			Main.players_.put(name, player_box);
		} else if (type == 0) { //튜토리얼 시작
			this.pressmsg(player, 1, 11);
			this.pressmsg(player, 2, 11);
			player_box.startup_step = 5;
			player_box.pass_limt = false;
			player_box.registered = true;
			
			this.update_type(name, 2);
			Main.players_.put(name, player_box);
			Main.popup_task.set_popup(player.getName(), 12);
			
			Wildgame.db.createworlddata(name);
			this.plugin.getServer().generateLevel(name);
			
			this.plugin.getServer().getLevelByName(name).generateChunk(128 >> 4, 128 >> 4);
			
			this.plugin.getServer().getScheduler().scheduleDelayedTask(new delay_create_task(this, player, this.plugin), 100);
		} else {
			this.move2startup(player, type, name, player_box);
		}
	}
	
	public void move2startup(Player player, int step, String name, Mine24player player_box) {
		player.sendMessage("<§e튜토리얼§f> 튜토리얼을 이어서 진행합니다.");
		player_box.startup_step = step;
		player_box.pass_limt = false;
		player_box.registered = true;
		if (step == 2) {
			this.create_world(player);
		}
		if ((step == 3) || (step == 4) || (step == 9) || (step == 10)) { //본인 월드로 이동
			Main.Wildgame.joinworld(name, name, player);
		}
		if (step == 3) {
			Main.playerlimit.pressmsg(player, 3, 11);
		} else if (step == 4) {
			player.teleport(new Position(-501, 7, 667, mainlevel)); //상점으로 이동
			Main.playerlimit.pressmsg(player, 4, 11);
		} else if (step == 5) {
			Main.popup_task.set_popup(player.getName(), 3);
			Main.playerlimit.pressmsg(player, 5, 11);
		} else if (step == 6) {
			Main.popup_task.set_popup(player.getName(), 4);
			Main.playerlimit.pressmsg(player, 6, 11);
		} else if (step == 7) {
			Main.popup_task.set_popup(player.getName(), 5);
			Main.playerlimit.pressmsg(player, 7, 11);
		} else if (step == 9) {
			Main.popup_task.set_popup(player.getName(), 6);
			Main.playerlimit.pressmsg(player, 9, 11);
		} else if (step == 10) {
			Main.popup_task.set_popup(player.getName(), 7);
			Main.playerlimit.pressmsg(player, 10, 11);
		} else if (step == 11) {
			Main.popup_task.set_popup(player.getName(), 8);
			Main.playerlimit.pressmsg(player, 11, 11);
		}
		Main.players_.put(name, player_box);
		return;
	}
	
	/**
	 * 메시지 전송 , step 1~10
	 * type 10 오작동 메시지
	 * type 11 아무행동 취하지 않음
	 * @param player
	 * @param step
	 * @param type
	 */
	public void pressmsg(Player player, int step, int type) {
		if (step == 1) {
			player.sendMessage("§7----------------");
			player.sendMessage("<§e튜토리얼§f> Mine24서버에 오신 것을 환영합니다. "+player.getName()+"님!");
			player.sendMessage("<§e튜토리얼§f> 서버를 본격적으로 진행하기에 앞서 튜토리얼을 진행합니다.");
			player.sendMessage("<§e튜토리얼§f> 튜토리얼이 끝나기 전까지는 채팅을 치실 수 없습니다.");
			return;
		} else if (step == 2) {
			player.sendMessage("<§e튜토리얼§f> 저희 Mine24는 각각의 플레이어들에게 개인 월드를 제공하고 있습니다.");
			player.sendMessage("<§e튜토리얼§f> "+player.getName()+"님도 자신의 월드를 생성해 발전시켜 나가기 바랍니다. :)");
			player.sendMessage("<§e튜토리얼§f> "+player.getName()+"님의 월드가 준비되었습니다. 지금 "+player.getName()+"님만의 월드로 이동합니다.");
			player.sendMessage("§7----------------");
			return;
		} else if (step == 3) {
			if (type == 1) {
				player.sendMessage("<§e튜토리얼§f> "+player.getName()+"님의 월드로 이동이 완료되었습니다. 현재 "+player.getName()+"님 월드의 스폰 아래가 물입니다.");
				player.sendMessage("<§e튜토리얼§f> 월드를 다시 생성 하겠습니다. 잠시만 기다려 주세요. :D");
				player.sendMessage("§7----------------");
				return;
			} else if (type == 10) {
				player.sendMessage("§7----------------");
				player.sendMessage("<§e튜토리얼§f> 월드를 다시 생성 중입니다. 잠시 기다려 주시기 바랍니다.");
				player.sendMessage("§7----------------");
				return;
			}
			player.sendMessage("<§e튜토리얼§f> 여기는 "+player.getName()+"님만의 월드입니다. 정해진 영역 안에서 자유롭게 수정 가능합니다.");
			player.sendMessage("<§e튜토리얼§f> 자신의 월드 또는 다른 사람의 월드에서 채굴, 농사 등을 이용해 돈을 벌수 있습니다.");
			player.sendMessage("<§e튜토리얼§f> 땅 속에 끼이거나 하늘에 스폰 되었을 경우 잠시 기다려 주시면 정상적인 위치로 이동됩니다.");
			player.sendMessage("§7----------------");
			return;
		} else if (step == 4) {
			player.sendMessage("<§e튜토리얼§f> 지금은 밀 16개를 드리겠습니다. 상점으로 이동해 밀을 팔아 보시기 바랍니다. : D");
			player.sendMessage("<§e튜토리얼§f> 수확한 수확물을 팔아 돈을 벌어봅시다. §e/이동 상점§f 명령어를 쳐서 상점으로 이동해 봅시다.");
			player.sendMessage("<§e튜토리얼§f> 상점으로 이동하시면 다음 단계로 진행됩니다. : D");
			player.sendMessage("§7----------------");
			return;
		} else if (step == 5) {
			if (type == 10) {
				player.sendMessage("§7----------------");
				player.sendMessage("<§e튜토리얼§f> §e/이동 상점§f 명령어로 상점으로 이동 해주시기 바랍니다.");
				player.sendMessage("§7----------------");
				return;
			}
			player.sendMessage("§7----------------");
			player.sendMessage("<§e튜토리얼§f> 여기는 상점입니다. 방금 얻으신 밀을 팔아 돈을 벌어봅시다.");
			player.sendMessage("<§e튜토리얼§f> 중간에 보이는 회색 블럭이 보이시나요? §e밀을 들고 회색 블럭을 터치§f해주시기 바랍니다.");
			player.sendMessage("<§e튜토리얼§f> 판매할 들고 중앙의 회색 블럭을 터치하시면 아이템이 판매됩니다. : D");
			player.sendMessage("<§e튜토리얼§f> 가지고 계신 아이템을 판매하시면 다음 단계로 진행됩니다. : D");
			player.sendMessage("§7----------------");
			return;
		} else if (step == 6) {
			if (type == 10) {
				player.sendMessage("§7----------------");
				player.sendMessage("<§e튜토리얼§f> §e밀을 들고 상점 중앙에 회색 블럭을 터치§f 해주시기 바랍니다.");
				player.sendMessage("§7----------------");
				return;
			}
			player.sendMessage("§7----------------");
			player.sendMessage("<§e튜토리얼§f> 아이템을 성공적으로 판매하셨습니다.");
			player.sendMessage("<§e튜토리얼§f> §e/경제 내돈§f 명령어로 자신의 돈을 확인하실 수 있습니다.");
			player.sendMessage("<§e튜토리얼§f> /경제 내돈 명령어로 "+player.getName()+"님의 돈을 확인하시면 다음 단계로 진행됩니다. : D");
			player.sendMessage("§7----------------");
			return;
		} else if (step == 7) {
			if (type == 10) {
				player.sendMessage("§7----------------");
				player.sendMessage("<§e튜토리얼§f> §e/경제 내돈§f 명령어로 자신의 돈을 확인해 보시기 바랍니다.");
				player.sendMessage("<§e튜토리얼§f> /경제 내돈 명령어로 "+player.getName()+"님의 돈을 확인하시면 다음 단계로 진행됩니다. : D");
				player.sendMessage("§7----------------");
				return;
			}
			player.sendMessage("§7----------------");
			player.sendMessage("<§e튜토리얼§f> 아이템을 팔아서 돈을 벌었습니다. 이제 아까 밀을 수확하셨던 님의 월드로 돌아가 봅시다.");
			player.sendMessage("<§e튜토리얼§f> §e/월드 이동§f 명령어를 쳐서 자신의 월드로 이동하시면 다음 단계로 진행됩니다. : D");
			player.sendMessage("§7----------------");
			return;
		} else if (step == 8) {
			if (type == 10) {
				player.sendMessage("§7----------------");
				player.sendMessage("<§e튜토리얼§f> §e/월드 이동§f 명령어로 자신의 월드로 이동 해주시기 바랍니다.");
				player.sendMessage("<§e튜토리얼§f> /월드 이동 명령어로 "+player.getName()+"님의 월드로 이동 하시면 다음 단계로 진행됩니다. : D");
				player.sendMessage("§7----------------");
				return;
			}
			player.sendMessage("§7----------------");
			player.sendMessage("<§e튜토리얼§f> 다시 "+player.getName()+"님의 월드로 돌아왔습니다.");
			player.sendMessage("§7----------------");
			return;
		} else if (step == 9) {
			player.sendMessage("<§e튜토리얼§f> "+player.getName()+"님의 월드 크기 제한은 80*80칸입니다. 돈을 모아서 크기를 확장할 수 있습니다.");
			player.sendMessage("<§e튜토리얼§f> 지금은 무료로 100*100칸까지 확장할 기회를 드립니다. :)");
			player.sendMessage("<§e튜토리얼§f> §e/월드 확장 10§f 명령어로 "+player.getName()+"님의 월드를 100칸까지 확장해봅시다.");
			player.sendMessage("<§e튜토리얼§f> /월드 확장 10 명령어를 쳐서 자신의 월드를 확장하시면 다음 단계로 진행됩니다. : D");
			player.sendMessage("§7----------------");
			return;
		} else if (step == 10) {
			if (type == 10) {
				player.sendMessage("§7----------------");
				player.sendMessage("<§e튜토리얼§f> §e/월드 확장 10§f 명령어로 자신의 월드로 확장 해주시기 바랍니다.");
				player.sendMessage("<§e튜토리얼§f> /월드 확장 10 명령어로 "+player.getName()+"님의 월드의 사용 가능 넓이를 확장 하시면 다음 단계로 진행됩니다. : D");
				player.sendMessage("§7----------------");
				return;
			}
			player.sendMessage("§7----------------");
			player.sendMessage("<§e튜토리얼§f> 잘 하셨습니다. :P 이제 "+player.getName()+"님의 월드는 100*100칸까지 사용 가능합니다.");
			player.sendMessage("<§e튜토리얼§f> 다시 앞으로 쭉 나가신다면 전보다 멀리 나가실 수 있을 것입니다.");
			player.sendMessage("<§e튜토리얼§f> 넓어진 월드에서 마음껏 건축을 하시기 전에 원하시는 위치로 스폰을 설정해주시기 바랍니다.");
			player.sendMessage("<§e튜토리얼§f> 마음에 드시는 위치로 이동한 뒤에 §e/월드 스폰설정§f 명령어로 스폰을 설정해주시기 바랍니다.");
			player.sendMessage("<§e튜토리얼§f> 현재의 스폰 위치가 마음에 드신다면 §e/건너뛰기§f 명령어를 이용해주시기 바랍니다.");
			player.sendMessage("§7----------------");
			return;
		} else if (step == 11) {
			if (type == 1) {
				player.sendMessage("§7----------------");
				player.sendMessage("<§e튜토리얼§f> 월드의 새로운 스폰을 지정하셨습니다. 다음번에 자신의 월드로 이동하시면 여기서 스폰 됩니다.");
				player.sendMessage("§7----------------");
				return;
			} else if (type == 2) {
				player.sendMessage("§7----------------");
				player.sendMessage("<§e튜토리얼§f> 월드의 새로운 스폰 지정을 건너뛰셨습니다. 다음번에 자신의 월드로 이동하시면 여기서 스폰 됩니다.");
				player.sendMessage("§7----------------");
				return;
			} else if (type == 10) {
				player.sendMessage("§7----------------");
				player.sendMessage("<§e튜토리얼§f> "+player.getName()+"님의 월드의 새로운 스폰을 지정 해주시기 바랍니다.");
				player.sendMessage("<§e튜토리얼§f> §e/건너뛰기§f 명령어를 이용 해주시면 스폰 설정을 건너 뛰실 수 있습니다.");
				player.sendMessage("<§e튜토리얼§f> 현재 스폰 위치가 마음에 들지 않으신다면 원하는 위치로 이동한 다음 §e/월드 스폰설정§f 명령어를 이용 해주시기 바랍니다.");
				player.sendMessage("§7----------------");
				return;
			}
			player.sendMessage("<§e튜토리얼§f> 언제나 소지하고 계신 월드의 스폰을 바꾸고 싶다면 §e/월드 스폰설정§f 명령어를 이용해주시면 됩니다.");
			player.sendMessage("<§e튜토리얼§f> 지금 안내드린 내용 외에도 여러 가지 명령어들이 존재합니다.");
			player.sendMessage("<§e튜토리얼§f> 언제나 §e도움말.메인.한국§f을 인터넷 주소창에 넣어 이동하시면 명령어 안내를 띄워 드리겠습니다.");
			player.sendMessage("<§e튜토리얼§f> 언제나 궁금한 내용이 있다면 §e카카오톡 @Mine24§f로 연락 주시면 답변드리겠습니다.");
			player.sendMessage("<§e튜토리얼§f> 월드를 다른 플레이어와 같이 사용하는 법, 개인 메시지 사용법 등이 적혀 있으니 시간 나실 때 꼭 한번 봐주시기 바랍니다.");
			player.sendMessage("<§e튜토리얼§f> §e/가입 <원하는 비밀번호>§f 로 가입을 하시면 정상적인 채팅/서버 활동이 가능합니다. : D");
			player.sendMessage("§7----------------");
			return;
		} else if (step == 12) {
			if (type == 10) {
				player.sendMessage("§7----------------");
				player.sendMessage("<§e튜토리얼§f> §e/가입 <원하는 비밀번호>§f 로 가입을 해주시기 바랍니다. : D");
				player.sendMessage("<§e튜토리얼§f> 예시 - /가입 fa"+new Random().nextInt(10000)+"r");
				player.sendMessage("§7----------------");
				return;
			}
			player.sendMessage("§7----------------");
			player.sendMessage("<§e튜토리얼§f> 튜토리얼을 성공적으로 마쳤습니다.");
			player.sendMessage("<§e튜토리얼§f> 이제 채팅, 아이템 수집 등 정상적인 서버 활동이 가능합니다.");
			player.sendMessage("<§e튜토리얼§f> 여러가지 미니게임, 이벤트 등이 준비 되어 있습니다.");
			player.sendMessage("<§e튜토리얼§f> /인증 <이메일> 로 이메일 인증을 받으시면 더 다양한 혜택을 누리실 수 있습니단.");
			player.sendMessage("<§e튜토리얼§f> 언제나 즐거운 시간 보내시기 바랍니다. §e"+player.getName()+"님! 감사합니다.§f : D");
			player.sendMessage("§7----------------");
			return;
		}
		return;
	}

	
	public void recreate_world(Player player) {
		player.teleport(this.mainpoint);
		this.plugin.getServer().getScheduler().scheduleDelayedTask(new World_ReCreate_task(this, player, this.plugin), 5);
	}
	
	public void _recreate_world(Player player) {
		this.create_world(player);
		return;
	}
	
	public void update_type(String player, int type) {
		this.mysqllib.execute("UPDATE player_info SET step="+type+" WHERE name='"+player+"';");
	}
	
	public void end_startup(String player) {
		this.mysqllib.execute("UPDATE player_info SET step=100 WHERE name='"+player.toLowerCase()+"';");
	}
	
	
	//차단
	

    public boolean press_cmd(CommandSender sender, int type, String[] args) {
		if (type == 1) { //경고 3회 + 차단 
//        	Player player = sender.getServer().getPlayer(sender.getName());
        	this.pressban(sender, args);
        	return true;
        } else if (type == 2) { //경고 1회 + 추방
//        	Player player = sender.getServer().getPlayer(sender.getName());
        	this.pressw(sender, args);
        	return true;
        } else if (type == 3) { //IP 차단
//        	Player player = sender.getServer().getPlayer(sender.getName());
        	this.pressipban(sender, args);
        	return true;
        } else if (type == 4) { //경고 3회 + IP차단 + 닉네임 차단
//        	Player player = sender.getServer().getPlayer(sender.getName());
        	this.pressallban(sender, args);
        	return true;
        } else if (type == 5) { //일시 추방조치
//        	Player player = sender.getServer().getPlayer(sender.getName());
        	this.presskick(sender, args);
        	return true;
        }
		return true;
	}

	public boolean checkpms(String name) {
		name = name.toLowerCase();
		if (name.equals("console")) {
			return false;
		} else if (ClassManager.players.get(name) == 1) {
			return false;
		}
		return true;
	}
	
	public Player findplayer(String hint) {
		return this.plugin.getServer().getPlayer(hint);
	}
	
	public void presskick(CommandSender player, String[] args) {
		if (this.checkpms(player.getName())) {
			return;
		}
		Player target = null;
		String target_name = null;
		String reason = null;
		try {
			target = this.findplayer(args[0]);
			target_name = target.getName().toLowerCase();
			reason = args[1];
		} catch (NullPointerException e) {
			player.sendMessage("§7해당 플레이어는 오프라인 입니다. 일시 추방처리 불가능");
			return;
		} catch (ArrayIndexOutOfBoundsException e) {
			player.sendMessage("§7 적지 않으신 요소가 있습니다.");
			return;
		}
		target.close(target.getLeaveMessage(), reason);
		player.sendMessage("§7"+ target_name + "님 일시 추방 완료. 출력 메시지 " + reason);
	}
	
	private void pressallban(CommandSender player, String[] args) {
		if (this.checkpms(player.getName())) {
			return;
		}
		Player target = null;
		String target_name = null;
		String target_ip = null;
		String reason = null;
		try {
			target = this.findplayer(args[0]);
			target_name = target.getName().toLowerCase();
			target_ip = target.getAddress();
			reason = args[1];
		} catch (NullPointerException e) {
			String ip = this.find_ip(args[0]);
			if (ip == null) {
				player.sendMessage("§7"+args[0]+" 플레이어 추적 실패. 전체 차단 불가능");
				return;
			}
			this.setban(args[1], reason, player.getName());
			this.setban_ip(ip, args[0], reason, player.getName());
			player.sendMessage("§7"+ args[0] + "님 " + ip + "IP 및 닉네임 차단 완료. 사유 " + args[1] + " (오프라인 플레이어)");
			return;
		} catch (ArrayIndexOutOfBoundsException e) {
			player.sendMessage("§7 적지 않으신 요소가 있습니다.");
			return;
		}
		this.setban(target_name, reason, player.getName());
		this.setban_ip(target_ip, target_name, reason, player.getName());
		target.close(target.getLeaveMessage(), "§e" + target_name + "님 계정은 차단§f 되셨습니다.\n§f사유: §e" + reason + "\n§f차단 해제 요청/ 이의 제기 -§e Mine24.net/ban");
		player.sendMessage("§7"+ target_name + "님 " + target_ip + "IP 및 닉네임 차단 완료. 사유 " + reason);
	}

	private void pressipban(CommandSender player, String[] args) {
		if (this.checkpms(player.getName())) {
			return;
		}
		Player target = null;
		String target_name = null;
		String target_ip = null;
		String reason = null;
		try {
			reason = args[1];
			target = this.findplayer(args[0]);
			target_name = target.getName().toLowerCase();
			target_ip = target.getAddress();
		} catch (NullPointerException e) {
			player.sendMessage("§7해당 플레이어는 오프라인 입니다. IP 불가능");
			return;
		} catch (ArrayIndexOutOfBoundsException e) {
			player.sendMessage("§7 적지 않으신 요소가 있습니다.");
			return;
		}
		this.setban_ip(target_ip, target_name, reason, player.getName());
		target.close(target.getLeaveMessage(), "§e" + target_name + "님 IP는 차단§f 되셨습니다.\n§f사유: §e" + reason + "\n§f차단 해제 요청/ 이의 제기 -§e Mine24.net/ban");
		player.sendMessage("§7"+ target_name + "님 " + target_ip + "IP 차단 완료. 사유 " + reason);
	}

	private void pressw(CommandSender player, String[] args) {
		if (this.checkpms(player.getName())) {
			return;
		}
		Player target = null;
		String target_name = null;
		String reason = null;
		int count = 1;
		try {
			reason = args[1];
			target = this.findplayer(args[0]);
			target_name = target.getName().toLowerCase();
		} catch (NullPointerException e) {
			target_name = args[0];
			int warning = this.getwarning(target_name);
			this.setwarning(target_name, count+warning, reason, player.getName());
			player.sendMessage("§7"+ target_name + "님 경고" + count + "회 처리 완료. 사유 " + reason + " 누적경고 " + (count+warning) + "회 (오프라인 플레이어 경고처리)");
			return;
		} catch (ArrayIndexOutOfBoundsException e) {
			player.sendMessage("§7 적지 않으신 요소가 있습니다.");
			return;
		}
		int warning = this.getwarning(target_name);
		this.setwarning(target_name, count+warning, reason, player.getName());
		target.close(target.getLeaveMessage(), "§e" + target_name + "님은 경고 "+count+"회를 §f받았습니다.\n§f사유: §e" + reason + "\n§f경고 철회 요청/ 이의 제기 -§e Mine24.net/ban");
		player.sendMessage("§7"+ target_name + "님 경고" + count + "회 처리 완료. 사유 " + reason + " 누적경고 " + (count+warning) + "회");
		if ((count+warning) > 2) {
			this.setban(target_name, "경고_3회_누적_차단처리", player.getName());
			player.sendMessage("§7"+ target_name + "님 경고누적 차단처리 완료");
		}
	}

	private void pressban(CommandSender player, String[] args) {
		if (this.checkpms(player.getName())) {
			return;
		}
		Player target = null;
		String target_name = null;
		String reason = null;
		try {
			reason = args[1];
			target = this.findplayer(args[0]);
			target_name = target.getName().toLowerCase();
		} catch (NullPointerException e) {
			target_name = args[0];
			this.setban(target_name, reason, player.getName());
			player.sendMessage("§7"+ target_name + "님 닉네임 차단 처리 완료. (오프라인 플레이어 차단처리) 사유 " + reason);
			return;
		} catch (ArrayIndexOutOfBoundsException e) {
			player.sendMessage("§7 적지 않으신 요소가 있습니다.");
			return;
		}
		this.setban(target_name, reason, player.getName());
		target.close(target.getLeaveMessage(), "§e" + target_name + "님 닉네임은 차단§f 되셨습니다.\n§f사유: §e" + reason + "\n§f차단 해제 요청/ 이의 제기 -§e ban.Mine24.net/ban");
		player.sendMessage("§7"+ target_name + "님 닉네임 차단 처리 완료. 사유 " + reason);
	}
	
	public boolean chackregister(ResultSet result) {
    	try {
			if (result.next()) {
				return true;
			}
		} catch (SQLException e) {
			return false;
		}
    	return false;
    }
	
	public String getcanjoin(Player player, ResultSet result) {
		int type = 0;
		try {
			type = result.getInt("type");
		} catch (SQLException e) {
			return null;
		}
		if (type == 6) {
			return this.getbanmsg(player, result, 1);
		} else {
			ResultSet sqlresult_ip = this.getipban(player.getAddress());
			if (sqlresult_ip != null) {
				return this.getbanmsg(player, sqlresult_ip, 2);
			}
			return null;
		}
	}
	
	public void setban(String name, String reason, String staff) {
		this.mysqllib.execute("UPDATE player_info SET type=6, reason='"+reason+"', staff='"+staff+"', date='"+((String.valueOf(Calendar.getInstance().getTime().getTime())).substring(0, 10))+"' WHERE name='"+name+"';");
	}
	
	public void setban_ip(String ip, String name, String reason, String staff) {
		this.mysqllib.execute("INSERT INTO player_info(name, ip, type, date, reason, staff) VALUES('"+ip+"', 1, NULL, '"+((String.valueOf(Calendar.getInstance().getTime().getTime())).substring(0, 10))+"', '"+reason+"', '"+staff+"');");
	}
	
	public void setwarning(String name, int count, String reason, String staff) {
		this.mysqllib.execute("UPDATE player_info SET type="+count+", reason='"+reason+"', staff='"+staff+"', date='"+((String.valueOf(Calendar.getInstance().getTime().getTime())).substring(0, 10))+"' WHERE name='"+name+"';");
		return;
	}
	
	public String find_ip(String name) {
		ResultSet result = this.mysqllib.executequery("SELECT * FROM simpleauth_players WHERE name = '"+name+"';");
		try {
			if (result.next()) {
				return result.getString("lastip");
			}
			return null;
		} catch (SQLException e) {
			return null;
		}
	}
	
	public int getwarning(String name) {
		ResultSet result = this.mysqllib.executequery("SELECT * FROM player_info WHERE name='"+name+"';");
		try {
			if (result.next()) {
				int count = result.getInt("type");
				if (count > 3) {
					return 0;
				} else {
					return count;
				}
			}
		} catch (SQLException e) {
			return 0;
		}
		return 0;
	}
	
	public void createusrdata(String name) {
		this.mysqllib.execute("INSERT INTO player_info(name, ip, type, date, reason, staff, step) VALUES('"+name+"', NULL, 0, NULL, NULL, NULL, 0);");
		return;
	}
	
	public String getbanmsg(Player player, ResultSet result, int type) {
		String date = null;
		String reason = null;
		try {
			date = result.getString("date");
			reason = result.getString("reason");
		} catch (SQLException e) {
			return null;
		}
		if (type == 1) {
			return "§e" + player.getName() + " 닉네임은 차단 §f상태입니다.\n사유: §e" + reason + "\n§f차단날짜: §e" + ((new Timestamp(Long.parseLong(date)*1000)).toString());
		} else if (type == 2) {
			return "§e" + player.getAddress() + " IP는 차단 §f상태입니다.\n사유: §e" + reason + "\n§f차단날짜: §e" + ((new Timestamp(Long.parseLong(date)*1000)).toString());
		}
		return null;
	}
	
	public ResultSet getplayerdata(String player) {
		String query = "SELECT * FROM player_info WHERE name = '" + player.toLowerCase() + "';";
    	return this.mysqllib.executequery(query);
	}
	
	public ResultSet getipban(String ip) {
		ResultSet result = this.getplayerdata(ip);
    	try {
			if (result.next()) {
				return result;
			} else {
				return null;
			}
		} catch (SQLException e) {
			return null;
		}
    }
}
