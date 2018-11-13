package junghyun.event;

import cn.nukkit.Player;
import cn.nukkit.block.BlockPotato;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.block.SignChangeEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.event.server.DataPacketReceiveEvent;

import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.LoginPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import junghyun.Main;
import junghyun.Minigame.Minigame_base;
import junghyun.Minigame.wall.Wall;
import junghyun.Wildgame.Wildgame;
import junghyun.class_box.Mine24player;
import junghyun.class_box.Mine24world;
import junghyun.task.Wildgame_Task;

import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerCommandPreprocessEvent;
import cn.nukkit.event.player.PlayerDeathEvent;
import cn.nukkit.event.player.PlayerDropItemEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerInvalidMoveEvent;
import cn.nukkit.event.player.PlayerRespawnEvent;
import cn.nukkit.event.player.PlayerTeleportEvent;

public class EventListener implements Listener {

	Main plugin;
	
	Level mainlevel;

	public EventListener(Main plugin) {
		this.plugin = plugin;
		this.mainlevel = this.plugin.getServer().getLevelByName("main");
	}
	
	@EventHandler(ignoreCancelled = false, priority = EventPriority.HIGHEST)
	public void onTouch(PlayerInteractEvent event) {
		
		Player player = event.getPlayer();
		
		double x = event.getBlock().getX();
		double y = event.getBlock().getY();
		double z = event.getBlock().getZ();
		
		int id = player.getInventory().getItemInHand().getId();
		
		String level = player.getLevel().getName();
		
		if (level.equals("main")) {
			// Mine24core
			if (x == -463 && y == 70 && z == 1011) { // warp
				player.teleport(new Position(-540, 6, -62, mainlevel));
				player.sendMessage("§7----------------");
				player.sendMessage("<§e이동§f> 광장으로 이동 되었습니다.");
				player.sendMessage("§7----------------");
				return;
			} else if (x == -463 && y == 70 && z == 1099) { // shop
				player.teleport(new Position(-501, 7, 667, mainlevel));
				player.sendMessage("§7----------------");
				player.sendMessage("<§e이동§f> 상점으로 이동 되었습니다.");
				player.sendMessage("§7----------------");
				return;
			} else if (x == -419 && y == 70 && z == 1055) { // play
				player.teleport(new Position(-58, 8, 1117, mainlevel));
				player.sendMessage("§7----------------");
				player.sendMessage("<§e이동§f> 미니게임으로 이동 되었습니다.");
				player.sendMessage("§7----------------");
				return;
			} else if (x == -30 && y == 8 && z == 1117) {
				Minigame_base.wall.join_game(player);
				return;
			} else if (x == -83 && y == 8 && z == 1117) {
				Minigame_base.defense.join_game(player);
				return;
			}
			
			if ((y == 5) && (x == -1461)) {
				if (z == 1104)  {
					Main.Mine24core.rand_money(player, 1);
				} else if (z == 1102) {
					Main.Mine24core.rand_money(player, 2);
				} else if (z == 1100) {
					Main.Mine24core.rand_money(player, 3);
				} else if (z == 1098) {
					Main.Mine24core.rand_money(player, 4);
				} else if (z == 1096) {
					Main.Mine24core.rand_money(player, 5);
				}
			}
			
			int block_id = event.getBlock().getId();
			
			if (block_id == 199) {
				if (!player.isOp()) {
					event.setCancelled();
					return;
				}
			}
			
			//playerlimit
			Mine24player player_box = Main.players_.get(event.getPlayer().getName());
			
			if (player_box.startup_step == 5) {
				if (x == -506 && z == 662) { //END 5
					String name = player.getName().toLowerCase();
					Main.Shop24.press_sell(player);
					Main.playerlimit.pressmsg(player, 6, 11);
					Main.playerlimit.update_type(name, 6);
					
					player_box.startup_step = 6;
					Main.players_.put(name, player_box);
					Main.Economy24.addmoney(name, 2);
					Main.popup_task.set_popup(event.getPlayer().getName(), 4);
					event.setCancelled();
				} else if (x == -505 && z == 662) {
					String name = player.getName().toLowerCase();
					Main.Shop24.press_sell(player);
					Main.playerlimit.pressmsg(player, 6, 11);
					Main.playerlimit.update_type(name, 6);
					
					player_box.startup_step = 6;
					Main.players_.put(name, player_box);
					Main.Economy24.addmoney(name, 2);
					Main.popup_task.set_popup(event.getPlayer().getName(), 4);
					event.setCancelled();
				} else if (x == -506 && z == 663) {
					String name = player.getName().toLowerCase();
					Main.Shop24.press_sell(player);
					Main.playerlimit.pressmsg(player, 6, 11);
					Main.playerlimit.update_type(name, 6);
					
					player_box.startup_step = 6;
					Main.players_.put(name, player_box);
					Main.Economy24.addmoney(name, 2);
					Main.popup_task.set_popup(event.getPlayer().getName(), 4);
					event.setCancelled();
				} else if (x == -505 && z == 663) {
					String name = player.getName().toLowerCase();
					Main.Shop24.press_sell(player);
					Main.playerlimit.pressmsg(player, 6, 11);
					Main.playerlimit.update_type(name, 6);
					
					player_box.startup_step = 6;
					Main.players_.put(name, player_box);
					Main.Economy24.addmoney(name, 2);
					Main.popup_task.set_popup(event.getPlayer().getName(), 4);
					event.setCancelled();
				} else {
					Main.playerlimit.pressmsg(player, 6, 10);
				}
				return;
			}
			
			//shop 24
			
			if (((y == 7) || (y == 6) || (y == 5))) {
				if (x == -506 && z == 662) {
					Main.Shop24.press_sell(player);
				} else if (x == -505 && z == 662) {
					Main.Shop24.press_sell(player);
				} else if (x == -506 && z == 663) {
					Main.Shop24.press_sell(player);
				} else if (x == -505 && z == 663) {
					Main.Shop24.press_sell(player);
				} else {
					Main.Shop24.press_shop(player, x, y, z);
				}
			}
			
			if (x == -507 && event.getBlock().getY() == 70 && z == 1055) { //wild
				Main.Wildgame.move_slef_world(player);
				return;
			}
			
			if (player.isOp()) {
				if (id == 290) {
					Main.WorldEdit.set_pos1(player, new Position(x, y, z, player.getLevel()), true);
				} else if (id == 291) {
					Main.WorldEdit.set_pos2(player, new Position(x, y, z, player.getLevel()), true);
				} else if (id == 294) {
					player.sendMessage("§7X "+ event.getBlock().getX() + " Y " +event.getBlock().getY() + " Z " + event.getBlock().getZ() + "ID " + id + ":" + (event.getBlock().getId()));
				}
				return;
			}
			
			if (id == 259 || id == 325 || id == 199) {
				if (player.isOp()) {
					return;
				}
				player.close(player.getLeaveMessage(), (String) "§e잘못된 아이템§f 사용 시도");
				event.setCancelled();
				return;
			}
			return;
		} else if (level.equals("-Wall")) {
			return;
		}

		//Wildgame
		if (player.isOp()) {
			if (id == 290) {
				Main.WorldEdit.set_pos1(player, new Position(x, y, z, player.getLevel()), true);
			} else if (id == 291) {
				Main.WorldEdit.set_pos2(player, new Position(x, y, z, player.getLevel()), true);
			} else if (id == 294) {
				player.sendMessage("§7X "+ event.getBlock().getX() + " Y " +event.getBlock().getY() + " Z " + event.getBlock().getZ() + "ID " + id + ":" + (event.getBlock().getId()));
			}
			return;
		}
		Mine24world world_box = Main.Wildgame.maps.get(level);
		if (id == 328 || id == 333) {
			player.close(player.getLeaveMessage(), (String) "§e잘못된 아이템§f 사용 시도");
			event.setCancelled();
			return;
		}
		if (!world_box.checkpos_world(x, z)) { // 월드 안에서 사용 불가하면 아래로
			event.setCancelled(true);
			return;
		}
		
		
		String name = player.getName().toLowerCase();
		if (name.equals(player.getLevel().getName())) {
			
			//감자 설치
			if ((player.getInventory().getItemInHand().getId() == 392) && (event.getBlock().getId() == 60)) {
				event.setCancelled(true);
				int count = player.getInventory().getItemInHand().getCount()-1;
				Location loc = event.getBlock().getLocation();
				loc.y = loc.y+1;
				event.getBlock().getLevel().setBlock(loc, new BlockPotato(), true, false);
				if (count == 0) {
					player.getInventory().removeItem(player.getInventory().getItemInHand());
				} else {
					Item hand_item = player.getInventory().getItemInHand();
					hand_item.setCount(count);
					player.getInventory().setItemInHand(hand_item);
				}
			}
			return;
		}
		
		if (!world_box.checkpos_other(name)) {
			event.setCancelled(true);
			return;
		}
		//감자 설치
		if ((player.getInventory().getItemInHand().getId() == 392) && (event.getBlock().getId() == 60)) {
			event.setCancelled(true);
			int count = player.getInventory().getItemInHand().getCount()-1;
			Location loc = event.getBlock().getLocation();
			loc.y = loc.y+1;
			event.getBlock().getLevel().setBlock(loc, new BlockPotato(), true, false);
			if (count == 0) {
				player.getInventory().removeItem(player.getInventory().getItemInHand());
			} else {
				Item hand_item = player.getInventory().getItemInHand();
				hand_item.setCount(count);
				player.getInventory().setItemInHand(hand_item);
			}
		}
	}

	@EventHandler(ignoreCancelled = false, priority = EventPriority.HIGHEST)
	public void BlockPlaceEvent(BlockPlaceEvent event) {
		
		//Auth24
		Mine24player player_box = Main.players_.get(event.getPlayer().getName());
		
		if (!player_box.logined) {
			event.getPlayer().sendMessage("§7----------------");
			event.getPlayer().sendMessage("<§e계정보호§f> 로그인을 진행 해 주시기 바랍니다.\n<§e계정보호§f> /로그인 <가입하셨을때 설정했던 비밀번호> 명령어로 로그인 가능합니다.");
			event.getPlayer().sendMessage("§7----------------");
			event.setCancelled(true);
			return;
		} else if (!player_box.registered) {
			event.getPlayer().sendMessage("§7----------------");
			event.getPlayer().sendMessage("<§e계정보호§f> 가입을 진행 해 주시기 바랍니다.\n<§e계정보호§f> /가입 <원하시는 비밀번호> 명령어로 가입하실 수 있습니다.");
			event.getPlayer().sendMessage("§7----------------");
			event.setCancelled(true);
			return;
		}
		
		//Wildgame
		Player player = event.getPlayer();
		String level = player.getLevel().getName();
		if (player.isOp()) {
			return;
		}
		if (level.equals("main")) {
			event.setCancelled(true);
			return;
		} else if (level.equals("-Wall")) {
			if (Wall.is_pvp) {
				event.setCancelled();
				player.sendPopup("§7PVP 진행중에는 블럭을 설치할수 없습니다.");
			}
			return;
		}
		
		Mine24world world_box = Main.Wildgame.maps.get(level);
		if (!world_box.checkpos_world(event.getBlock().getX(), event.getBlock().getZ())) {
			player.sendPopup("§7해당 지역은  §e구매하지 않은 영역§7으로 블럭 임의 설치가 불가능한 공간입니다.");
			event.setCancelled(true);
			return;
		}
		String name = player.getName().toLowerCase();
		if (name.equals(player.getLevel().getName())) {
			return;
		}
		if (!world_box.checkpos_other(name)) {
			player.sendPopup("§7해당 지역은  §e공유되지 않은 영역§7으로 블럭 임의 설치가 불가능한 공간입니다.");
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = false, priority = EventPriority.HIGHEST)
	public void PlayerDropItemEvent(PlayerDropItemEvent event) {
		
		Mine24player player_box = Main.players_.get(event.getPlayer().getName());
		
		//Auth24
		if (!player_box.logined) {
			event.getPlayer().sendMessage("§7----------------");
			event.getPlayer().sendMessage("<§e계정보호§f> 로그인을 진행 해 주시기 바랍니다.\n<§e계정보호§f> /로그인 <가입하셨을때 설정했던 비밀번호> 명령어로 로그인 가능합니다.");
			event.getPlayer().sendMessage("§7----------------");
			event.setCancelled(true);
			return;
		}
		
		if (!player_box.pass_limt) {
			if (player_box.startup_step == 3) {
				Main.playerlimit.pressmsg(event.getPlayer(), 3, 10);
			} else if (player_box.startup_step == 4) {
				Main.playerlimit.pressmsg(event.getPlayer(), 5, 10);
			} else if (player_box.startup_step == 5) {
				Main.playerlimit.pressmsg(event.getPlayer(), 6, 10);
			} else if (player_box.startup_step == 6) {
				Main.playerlimit.pressmsg(event.getPlayer(), 7, 10);
			} else if (player_box.startup_step == 7) {
				Main.playerlimit.pressmsg(event.getPlayer(), 8, 10);
			} else if (player_box.startup_step == 9) {
				Main.playerlimit.pressmsg(event.getPlayer(), 10, 10);
			} else if (player_box.startup_step == 10) {
				Main.playerlimit.pressmsg(event.getPlayer(), 11, 10);
			} else if (player_box.startup_step == 11) {
				Main.playerlimit.pressmsg(event.getPlayer(), 12, 10);
			}
			return;
		}
		
	}

	@EventHandler(ignoreCancelled = false, priority = EventPriority.HIGHEST)
	public void BlockBreakEvent(BlockBreakEvent event) {
		
		//Auth24
		Mine24player player_box = Main.players_.get(event.getPlayer().getName());
		if (!player_box.logined) {
			event.getPlayer().sendMessage("§7----------------");
			event.getPlayer().sendMessage("<§e계정보호§f> 로그인을 진행 해 주시기 바랍니다.\n<§e계정보호§f> /로그인 <가입하셨을때 설정했던 비밀번호> 명령어로 로그인 가능합니다.");
			event.getPlayer().sendMessage("§7----------------");
			event.setCancelled(true);
			return;
		}
		
		//Wildgame
		Player player = event.getPlayer();
		String level = player.getLevel().getName();
		if (player.isOp()) {
			return;
		}
		if (level.equals("main")) {
			event.setCancelled(true);
			return;
		} else if (level.equals("-Wall")) {
			if (Wall.is_pvp) {
				event.setCancelled();
				player.sendPopup("§7PVP 진행중에는 블럭을 파괴할수 없습니다.");
			}
			return;
		}
		Mine24world world_box = Main.Wildgame.maps.get(level);
		if (!world_box.checkpos_world(event.getBlock().getX(), event.getBlock().getZ())) { // 월드 안에서 사용 불가하면 아래로
			player.sendTip("§7해당 지역은  §e구매하지 않은 영역§7으로 블럭 임의 제거가 불가능한 공간입니다.");
			event.setCancelled(true);
			return;
		}
		String name = player.getName().toLowerCase();
		if (name.equals(player.getLevel().getName())) {
			return;
		}
		if (!world_box.checkpos_other(name)) {
			player.sendPopup("§7해당 지역은  §e공유되지 않은 영역§7으로 블럭 임의 제거가 불가능한 공간입니다.");
			event.setCancelled(true);
		}

	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		
		event.setJoinMessage("");
		
		Player player = event.getPlayer();
		
		if (!player.getName().equals((player.getName().replaceAll("\\p{Space}","")))) {
			player.close(player.getLeaveMessage(), (String) "§e닉네임에 띄어쓰기는 허용되지 않습니다.");
			return;
		} else if (player.getName().equals("Steve")) {
			player.close(player.getLeaveMessage(), (String) "§eSteve 는 기본 닉네임 입니다. 닉네임을 바꿔 주시기 바랍니다.");
			return;
		} else if (player.getName().equals("main")) {
			player.close(player.getLeaveMessage(), (String) "§e닉네임을 바꿔 주시기 바랍니다.");
			return;
		}
		
		Mine24player player_box = new Mine24player();
		player_box.set_all(event.getPlayer(), Main.ClassManager.setindex(event.getPlayer().getName())); //칭호 등 설정
		
		String name = player_box.low_name;
		
		Main.Mine24core.broadcastPopUP("§7+ "+event.getPlayer().getName());
		
		//playerlimit
		Main.playerlimit.onplayerjoin(event.getPlayer(), name);
		
		//ClassManager
		String format = "§7<<§l"+Main.ClassManager.gettag(event.getPlayer().getName())+"§r§7>>";
		event.getPlayer().setNameTag(format);
		
		//Economy24
		
		if(!Main.Economy24.chackregister(name)) {
			Main.Economy24.addproflie(name);
		}
		
		//Mine24core
		
		if (player.getName().equals(Main.Mine24core.size_name)) {
			player.setScale(Main.Mine24core.size_size);
		}
		
		Main.Mine24core.broadcastPopUP("§7+ "+player.getName());
		long uuid = Main.Mine24core.getClientId(player);
		this.plugin.getLogger().info("플레이어 로딩감지, 좌표 " + player + ", 닉네임 " + player.getName() + ", uuid " + uuid);
		if (Main.Mine24core.checkuuid(player, name, uuid)) {
			Main.Mine24core.kickandlog(player);
			return;
		}
		
		Main.ClassManager.printplayer(player);
		
		Main.Mine24core.tospawn(event.getPlayer());
		
		player.setFoodEnabled(false);
		
//		if (!event.getPlayer().isOp()) {
//			event.getPlayer().close(player.getLeaveMessage(), (String) "§e서버 점검중§f 입니다.\n점검 일정 -8월 8일 §e오후 10시 30분 까지");
//		}
					
		//Auth24
		if (Main.auth24.chackregister(name)) {
			int type = Main.auth24.checkautologin(player);
			if (type == 1) {
				player.sendTip("§7자동 로그인 되었습니다.");
				
				player_box.pass_limt = true;
				Main.auth24.updateplayer(player);
				Main.popup_task.add_new(player.getName(), Main.Economy24.getmoney(player.getName()));
				Main.Shop24.wed_item_check(player, name, false);
				
				Main.players_.put(player.getName(), player_box);
				Main.playerlimit.start_startup(player);
				return;
			} else if (type == 2) {
				event.getPlayer().sendMessage("§7----------------");
				event.getPlayer().sendMessage("<§e계정보호§f> 현재 기기 인증으로 임시 가입 상태입니다.");
				event.getPlayer().sendMessage("<§e계정보호§f> 더 많은 기능를 이용 하시려면 가입을 진행 해 주시기 바랍니다.\n<§e계정보호§f> /가입 <원하시는 비밀번호> 명령어로 가입하실 수 있습니다.");
				event.getPlayer().sendMessage("§7----------------");
				player_box.registered = false;
				Main.popup_task.set_popup(player.getName(), 10);
				
				Main.players_.put(player.getName(), player_box);
				Main.playerlimit.start_startup(player);
				return;
			}
			player_box.logined = false;
			Main.popup_task.set_popup(player.getName(), 9);
			
			Main.players_.put(player.getName(), player_box);
			return;
		}
		player_box.registered = false;
		Main.auth24.registerplayer_uuid(player);
//		event.getPlayer().sendMessage("§7----------------");
//		event.getPlayer().sendMessage("<§e계정보호§f> Mine24 서버에 오신것을 환영합니다. : D");
//		event.getPlayer().sendMessage("<§e계정보호§f> 기기 인증으로 임시 가입 완료 되었습니다.");
//		event.getPlayer().sendMessage("<§e계정보호§f> /가입 명령어를 통해 비밀번호로 가입 해 주시면 보다 많은 혜택을 누리실 수 있습니다.");
//		event.getPlayer().sendMessage("§7----------------");
		Main.popup_task.set_popup(player.getName(), 10);
		
		Main.players_.put(player.getName(), player_box);
		Main.playerlimit.start_startup(player);
	}

	@EventHandler(ignoreCancelled = false, priority = EventPriority.HIGHEST)
	public void onMove(PlayerMoveEvent event) {
		
		Mine24player player_box = Main.players_.get(event.getPlayer().getName());
		
		//Auth24
		if (!player_box.logined) {
			event.getPlayer().sendMessage("§7----------------");
			event.getPlayer().sendMessage("<§e계정보호§f> 로그인을 진행 해 주시기 바랍니다.\n<§e계정보호§f> /로그인 <가입하셨을때 설정했던 비밀번호> 명령어로 로그인 가능합니다.");
			event.getPlayer().sendMessage("§7----------------");
			event.setCancelled(true);
			return;
		}
		
		//Wildgame
		if (Wildgame_Task.move_cue) {
			return;
		}
		
		Player player = event.getPlayer();
		String level = player.getLevel().getName();
		
		if (!player_box.can_move) {
			player.teleport(player.getLevel().getSafeSpawn());
		}
		
		if (player_box.minigame_type == 1) {
			if (Wall.is_pvp) {
				Minigame_base.wall.move_pvp(event.getPlayer());
				return;
			}
		} else if (player_box.minigame_type == 2) {
			if (player_box.can_pvp) {
				Minigame_base.defense.move_pvp(event.getPlayer());
			}
			return;
		}
		
		if (player.isOp()) {
			return;
		}
		if (level.equals("main")) {
			return;
		} else if (level.equals("-Wall")) {
			return;
		}
		Mine24world world_box = Main.Wildgame.maps.get(level);
		if (!world_box.checkpos_world(player.getX(), player.getZ())) {
			player.sendPopup("§7해당 지역은  §e구매하지 않은 영역§7으로 진입이 불가능한 공간입니다.");
			player.teleport(player.getLevel().getSafeSpawn());
			return;
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onChat(PlayerChatEvent event) {
		
		Mine24player player_box = Main.players_.get(event.getPlayer().getName());
		
		//Auth24
		if (!player_box.logined) {
			event.getPlayer().sendMessage("§7----------------");
			event.getPlayer().sendMessage("<§e계정보호§f> 로그인을 진행 해 주시기 바랍니다.\n<§e계정보호§f> /로그인 <가입하셨을때 설정했던 비밀번호> 명령어로 로그인 가능합니다.");
			event.getPlayer().sendMessage("§7----------------");
			event.setCancelled(true);
			return;
		}
		
		//playerlimit
		if (!player_box.pass_limt) {
			if (player_box.startup_step == 3) {
				Main.playerlimit.pressmsg(event.getPlayer(), 3, 10);
			} else if (player_box.startup_step == 4) {
				Main.playerlimit.pressmsg(event.getPlayer(), 5, 10);
			} else if (player_box.startup_step == 5) {
				Main.playerlimit.pressmsg(event.getPlayer(), 6, 10);
			} else if (player_box.startup_step == 6) {
				Main.playerlimit.pressmsg(event.getPlayer(), 7, 10);
			} else if (player_box.startup_step == 7) {
				Main.playerlimit.pressmsg(event.getPlayer(), 8, 10);
			} else if (player_box.startup_step == 9) {
				Main.playerlimit.pressmsg(event.getPlayer(), 10, 10);
			} else if (player_box.startup_step == 10) {
				Main.playerlimit.pressmsg(event.getPlayer(), 11, 10);
			} else if (player_box.startup_step == 11) {
				Main.playerlimit.pressmsg(event.getPlayer(), 12, 10);
			}
			event.setCancelled();
			return;
		}
		
		//ClassManager
		String format = "<"+player_box.chatformat+"> "+event.getMessage();
		event.setFormat(format);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
		
		this.plugin.getLogger().info(event.getPlayer().getName() + " : " + event.getMessage());
		
		Mine24player player_box = Main.players_.get(event.getPlayer().getName());
		
		//Auth24
		if (!player_box.logined) {
			String command = event.getMessage().split(" ")[0];
			if (command.equals("/로그인") || command.equals("/비밀번호")) {
				return;
			}
			event.getPlayer().sendMessage("§7----------------");
			event.getPlayer().sendMessage("<§e계정보호§f> 로그인을 진행 해 주시기 바랍니다.\n<§e계정보호§f> /로그인 <가입하셨을때 설정했던 비밀번호> 명령어로 로그인 가능합니다.");
			event.getPlayer().sendMessage("§7----------------");
			event.setCancelled(true);
			return;
		} else if (!player_box.registered) {
			event.getPlayer().sendMessage("§7----------------");
			event.getPlayer().sendMessage("<§e계정보호§f> 현재 기기 인증으로 임시 가입 상태입니다.");
			event.getPlayer().sendMessage("<§e계정보호§f> 더 많은 기능를 이용 하시려면 가입을 진행 해 주시기 바랍니다.\n<§e계정보호§f> /가입 <원하시는 비밀번호> 명령어로 가입하실 수 있습니다.");
			event.getPlayer().sendMessage("§7----------------");
			return;
		}
		
		//ClassManager
		String command = event.getMessage().split(" ")[0];
		if (command.equals("/help") || command.equals("/me") || command.equals("/?")) {
			 event.setCancelled(true);
		}
		
		if (player_box.is_minigame) {
			event.getPlayer().sendMessage("§7----------------");
			event.getPlayer().sendMessage("<§e안내§f> 미니게임 대기/진행중 입니다.");
			event.getPlayer().sendMessage("§7----------------");
			event.setCancelled(true);
		}

		//playerlimit
		String name = event.getPlayer().getName();
		if (!player_box.pass_limt) {
			if (player_box.startup_step == 3) { //월드 선택 
				String msg = event.getMessage();
				if (msg.equals("/재생성")) {
					Main.playerlimit.recreate_world(event.getPlayer());
					event.setCancelled();
					return;
				} else if (msg.equals("/선택")) { //END 2
					Main.playerlimit.pressmsg(event.getPlayer(), 3, 11);
					Main.playerlimit.pressmsg(event.getPlayer(), 4, 11);
					Main.playerlimit.update_type(name.toLowerCase(), 4);
					player_box.startup_step = 4;
					Main.players_.put(name, player_box);
					event.getPlayer().getInventory().addItem(new Item(296, 0, 16, "밀"));
					event.setCancelled();
					return;
				}
				Main.playerlimit.pressmsg(event.getPlayer(), 3, 10);
			} else if (player_box.startup_step == 4) { // /이동 상점
				String msg = event.getMessage();
				if (msg.equals("/이동 상점")) { // END 4
					player_box.startup_step = 5;
					Main.players_.put(name, player_box);
					event.getPlayer().teleport(new Position(-501, 7, 667, mainlevel));
					Main.playerlimit.update_type(name.toLowerCase(), 5);
					Main.playerlimit.pressmsg(event.getPlayer(), 5, 11);
					Main.popup_task.set_popup(event.getPlayer().getName(), 3);
					event.setCancelled();
					return;
				}
				Main.playerlimit.pressmsg(event.getPlayer(), 5, 10);
			} else if (player_box.startup_step == 6) { // /경제 내돈
				String msg = event.getMessage();
				if (msg.equals("/경제 내돈")) { //END 6
					player_box.startup_step = 7;
					Main.players_.put(name, player_box);
					event.getPlayer().sendMessage("§7----------------");
					event.getPlayer().sendMessage("<§e잔고 안내§f> " + event.getPlayer().getName() + "님이 현재 소유하고 계신 OP는 §e2 OP§f 입니다.");
					event.getPlayer().sendMessage("§7----------------");
					Main.playerlimit.update_type(name.toLowerCase(), 7);
					Main.playerlimit.pressmsg(event.getPlayer(), 7, 11);
					Main.popup_task.set_popup(event.getPlayer().getName(), 5);
					event.setCancelled();
					return;
				}
				Main.playerlimit.pressmsg(event.getPlayer(), 7, 10);
			} else if (player_box.startup_step == 7) { // /월드 이동
				String msg = event.getMessage();
				if (msg.equals("/월드 이동")) { //END 7
					String lname = name.toLowerCase();
					Main.Wildgame.joinworld(lname, lname, event.getPlayer());
					Main.playerlimit.update_type(lname, 9);
					player_box.startup_step = 9;
					Main.players_.put(name, player_box);
					Main.playerlimit.pressmsg(event.getPlayer(), 9, 11);
					Main.popup_task.set_popup(event.getPlayer().getName(), 6);
					event.setCancelled();
					return;
				}
				Main.playerlimit.pressmsg(event.getPlayer(), 8, 10); 
			} else if (player_box.startup_step == 9) { // /월드 확장
				String msg = event.getMessage();
				if (msg.equals("/월드 확장 10")) { //END 9
					player_box.startup_step = 10;
					Main.players_.put(name, player_box);
					Main.playerlimit.update_type(name.toLowerCase(), 10);
					Main.playerlimit.pressmsg(event.getPlayer(), 10, 11);
					Wildgame.db.addworldsize(name.toLowerCase(), 50);
					Main.popup_task.set_popup(event.getPlayer().getName(), 7);
					event.setCancelled();
					return;
				}
				Main.playerlimit.pressmsg(event.getPlayer(), 10, 10);
			} else if (player_box.startup_step == 10) { // /월드 스폰지정
				String msg = event.getMessage();
				if (msg.equals("/월드 스폰설정")) { //END 10
					player_box.startup_step = 11;
					Main.players_.put(name, player_box);
					Main.playerlimit.update_type(name.toLowerCase(), 11);
					Main.playerlimit.pressmsg(event.getPlayer(), 11, 1);
					Main.playerlimit.pressmsg(event.getPlayer(), 11, 11);
					Main.popup_task.set_popup(event.getPlayer().getName(), 8);
					Wildgame.db.setworldspawn(name.toLowerCase(), event.getPlayer().getX(), event.getPlayer().getY(), event.getPlayer().getZ());
					event.setCancelled();
					return;
				} else if (msg.equals("/건너뛰기")) { //END 10
					player_box.startup_step = 11;
					Main.players_.put(name, player_box);
					Main.playerlimit.update_type(name.toLowerCase(), 11);
					Main.playerlimit.pressmsg(event.getPlayer(), 11, 2);
					Main.playerlimit.pressmsg(event.getPlayer(), 11, 11);
					Main.popup_task.set_popup(event.getPlayer().getName(), 8);
					event.setCancelled();
					return;
				}
				Main.playerlimit.pressmsg(event.getPlayer(), 11, 10);
			} else if (player_box.startup_step == 11) { // 튜토리얼 완료
				String msg = command;
				if (msg.equals("/가입")) { //END 11
					event.setCancelled();
					if (!Main.auth24.register_startup(event.getPlayer(), event.getMessage().split(" "))) {
						return;
					}
					player_box.pass_limt = true;
					Main.players_.put(name, player_box);
					Main.playerlimit.update_type(name.toLowerCase(), 100);
					Main.playerlimit.pressmsg(event.getPlayer(), 12, 11);
					event.getPlayer().getInventory().addItem(new Item(274, 0, 1, "채굴용 돌 곡괭이"));
					event.getPlayer().getInventory().addItem(new Item(351, 15, 2, "나무 생성용 뼈가루"));
					event.getPlayer().getInventory().addItem(new Item(4, 0, 64, "조약돌"));
					event.getPlayer().getInventory().addItem(new Item(2, 0, 2, "나무 생성용 잔디"));
					event.getPlayer().getInventory().addItem(new Item(6, 0, 2, "나무 생성용  묘목"));
					Main.popup_task.set_popup(event.getPlayer().getName(), 13);
					return;
				}
				Main.playerlimit.pressmsg(event.getPlayer(), 12, 10);
			}
			event.setCancelled();
		}
	}
	
	@EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
		event.setQuitMessage("");
		Main.Mine24core.broadcastPopUP("§7- "+event.getPlayer().getName());
		
		Mine24player player_box = Main.players_.get(event.getPlayer().getName());
		
		if (player_box == null) {
			return;
		}
		
		if (player_box.is_minigame) {
			if (player_box.minigame_type == 1) {
				Minigame_base.wall.go_exit(event.getPlayer());
			} else if (player_box.minigame_type == 2) {
				Minigame_base.defense.go_exit(event.getPlayer());
			}
		}
		
		String level = event.getPlayer().getLevel().getName();
		if (level.equals("main") || level.equals("-Wall")) {
			// 레벨 언로드 체크
		}
		
		if (event.getPlayer().isOp()) {
			event.getPlayer().setOp(false);
		}
		return;
	}
	
	@EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onkill(PlayerDeathEvent event) {
		String level = event.getEntity().getLevel().getName();
		if (level.equals("main") || level.equals("-Wall")) {
			Mine24player player_box = Main.players_.get(event.getEntity().getName());
			if (player_box.is_minigame) {
				if (player_box.minigame_type == 1) {
					Minigame_base.wall.killed_player(event.getEntity());
				} else if (player_box.minigame_type == 2) {
					if (event.getDeathMessage().getText().equals("death.fell.accident.generic") || event.getDeathMessage().getText().equals("death.attack.fall")) {
						this.plugin.log_info("떨어짐?");
						Minigame_base.defense.drop_death(event.getEntity().getPlayer()); //낙사?
					}
				}
			}
			// 레벨 언로드 체크
		}
		event.setDeathMessage("");
		event.setKeepInventory(true);
		return;
	}
	
	@EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		Entity player_e = event.getEntity();
		Entity attacker_e = event.getDamager();
		if (player_e instanceof Player && attacker_e instanceof Player){
			Player player = (Player) player_e;
			Player attacker = (Player) attacker_e;
			Mine24player player_box = Main.players_.get(player.getName());
			if (!player_box.can_pvp) {
				event.setCancelled();
				return;
			} else if (player_box.is_minigame) {
				if (player_box.minigame_type == 1) { //Wall
					if (player.getHealth() <= event.getDamage()) {
						Minigame_base.wall.add_kill_line(attacker.getName(), player.getName());
					}
				} else if (player_box.minigame_type == 2) { //Defense
					int attacker_id = attacker.getInventory().getItemInHand().getId();
					int player_id = player.getInventory().getItemInHand().getId();
					
					if (attacker_id == 267) { //칼
						if (player_id != 341) { //칼 외 키타
							event.setDamage(10);
						} else { //방패
							event.setDamage(1);
						}
						Minigame_base.defense.hit_player(attacker, player);
					} else { //방패 및 기타
						event.setCancelled();
					}
					
					if (player.getHealth() <= event.getDamage()) {
						Minigame_base.defense.kill_player(attacker, player);
						Minigame_base.defense.add_kill_line(attacker.getName(), player.getName());
						
						player.setHealth(20);
						player.teleport(Minigame_base.defense.find_spawn());
						event.setCancelled();
					}
					
				}
			}
		}
	}
	
	@EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void PlayerInvalidMoveEvent(PlayerInvalidMoveEvent event) {
		event.setCancelled(true);
		return;
	}
	
	 @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onSignChange(SignChangeEvent event) {
		 String[] text = event.getLines();
		 this.plugin.getLogger().info(event.getPlayer().getName() + " 표지판 작성: " +text[0] + " || " + text[1] + " || " + text[2] + " || " + text[3] + " || ");
		 if (event.getPlayer().isOp()) {
			 text[0] = text[0].replace("☆", "§");
			 text[1] = text[1].replace("☆", "§");
			 text[2] = text[2].replace("☆", "§");
			 text[3] = text[3].replace("☆", "§");
			 event.setLine(0, text[0]);
			 event.setLine(1, text[1]);
			 event.setLine(2, text[2]);
			 event.setLine(3, text[3]);
		 }
	 }
	 
	 @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
	 public void onRespawn(PlayerRespawnEvent event) {
		 Mine24player player_box = Main.players_.get(event.getPlayer().getName());
		 if (player_box == null) {
			 return;
		 }
		 if (player_box.is_minigame) {
			 if (player_box.minigame_type == 1) {
				 Minigame_base.wall.respawn_player(event.getPlayer());
			 } else if (player_box.minigame_type == 2) {
				 this.plugin.tick_5_tp(event.getPlayer(), Minigame_base.defense.find_spawn());
			 } 
		 }
		 return;
	 }
	 
	 @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
	 public void onPlayerTeleport(PlayerTeleportEvent event) {
		 if (!event.getFrom().getLevel().getName().equals(event.getTo().getLevel().getName())) {
			//레벨 언로드 체크
			 return;
		 }
		 return;
	 }
	
	 @EventHandler (ignoreCancelled = true, priority = EventPriority.LOW)
	 public void onLoginPacket(DataPacketReceiveEvent event){
		 DataPacket packet = event.getPacket();
		 if (packet.pid() != ProtocolInfo.LOGIN_PACKET) return;
		 LoginPacket loginPacket = (LoginPacket) packet;

		 if (loginPacket.protocol != 131) {
			 if (loginPacket.protocol >= 131) {
				 loginPacket.protocol = 131;
				 
				 Player online_player = this.plugin.getServer().getPlayer(loginPacket.username);
				 if (online_player == null) {
					 return;
				 }
				 if (online_player.getName().equals(loginPacket.username)) {
					 event.getPlayer().close(event.getPlayer().getLeaveMessage(), "§e같은 이름의 플레이어§f가 온라인 입니다.");
					 return;
				 }
				 
				 return;
			 }
			 event.getPlayer().close(event.getPlayer().getLeaveMessage(), "§e1.1.0 버전으로 업데이트§f 바랍니다. : D");
			 return;
		 }
		 
		 Player online_player = this.plugin.getServer().getPlayer(loginPacket.username);
		 if (online_player == null) {
			 return;
		 }
		 if (online_player.getName().equals(loginPacket.username)) {
			 event.getPlayer().close(event.getPlayer().getLeaveMessage(), "§e같은 이름의 플레이어§f가 온라인 입니다.");
			 return;
		 }
	 }

//		@EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
//		public void onDamage(EntityDamageEvent event) {
//			Entity entity = event.getEntity();
//			Player player;
	//
//			if (entity instanceof Player) {
//				player = (Player) entity;
//				if (event.getFinalDamage() >= player.getHealth()) {
//					if (this.plugin.minigame_base.Wall.players.get(player.getName()) != null) {
//						this.plugin.minigame_base.Wall.killed_player(player);
//					}
//				}
//			} else {
//				return;
//			}
//		}
		
//		@EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
//		public void onEntityDamageByEntity(EntityDamageEvent event){
//			Player victim = event.getEntity();
//			Player attacker = event.get;
//			if(victim instanceof Player && attacker instanceof Player){
//				
//			}
//		}
}
