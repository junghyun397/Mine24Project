package junghyun.Shop24;

import static java.lang.Math.toIntExact;

import java.sql.ResultSet;
import java.sql.SQLException;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import junghyun.Main;
import junghyun.db.MysqlLib;

public class Shop24 {
	
	public MysqlLib mysqllib = new MysqlLib();

	public Main plugin;
	
	public Shop24 onEnable(Main plugin) {
		this.plugin = plugin;
		this.plugin.getLogger().info("상점 시스템 로딩중....");
		this.plugin.getLogger().info("상점 시스템 로딩완료....");
		return this;
	}
	
	public void wed_item_check_(Player player, String name) {
		String query = "SELECT * FROM itemorlder WHERE name = '" + name + "';";
		ResultSet result = this.mysqllib.executequery(query);
		int index = 0;
		String item_name = null;
		PlayerInventory inv = player.getInventory();
		player.sendMessage("§7----------------");
		try {
			while (result.next()) {
				index++;
				item_name = result.getString("item_name");
				int code = result.getInt("itemcode");
				int material = result.getInt("material");
				int amount = result.getInt("amount");
				int count = result.getInt("count");

				if (amount == 64) { // 1세트
					for (int i = 0; i >= count; i++) {
						inv.addItem(new Item(code, material, 64));
					}
					this.mysqllib.execute("DELETE FROM itemorlder WHERE num = '" + result.getInt("num") + "';");
					player.sendMessage("<§e웹 상점§f> 주문하신 아이템 " + item_name + " " + count + "세트가 인벤토리에 지급 되었습니다.");
				} else { // 개당
					count = result.getInt("count") * amount;
					if (count > 64) { // 1세트 초과
						int found = count % 64;
						int all_conut = 0;
						boolean loop = true;
						while (loop) {
							all_conut = all_conut + 64;
							if (all_conut + found == count) {
								loop = false;
							} else {
								inv.addItem(new Item(code, material, 64));
							}
						}
						this.mysqllib.execute("DELETE FROM itemorlder WHERE num = '" + result.getInt("num") + "';");
						player.sendMessage("<§e웹 상점§f> 주문하신 아이템 " + item_name + " " + count + "개가 인벤토리에 지급 되었습니다.");
					} else {
						inv.addItem(new Item(code, material, count));
						this.mysqllib.execute("DELETE FROM itemorlder WHERE num = '" + result.getInt("num") + "';");
						player.sendMessage("<§e웹 상점§f> 주문하신 아이템 " + item_name + " " + count + "개가 인벤토리에 지급 되었습니다.");
					}
				}
				 
				Item item = new Item(code, material, count*amount);
				
				if(inv.canAddItem(item)) {
					
				}
			}
			result.close();
		} catch (SQLException e) {
			return;
		}
		player.sendMessage("§7----------------");
		player.sendMessage("<§e웹 상점§f> 전체 " + index + " 개의 주문이 지급 처리 되었습니다. 확인 부탁드립니다. :D");
		player.sendMessage("§7----------------");
		return;
	}
	
	public void press_cmd(CommandSender sender, int type, String[] args) {
    	if (type == 1) {
    		this.wed_item_check(sender.getServer().getPlayer(sender.getName()), sender.getName().toLowerCase(), true);
    	}
    	return;
	}
	
	public void wed_item_check(Player player, String name, boolean sult) {
		String query = "SELECT * FROM itemorder WHERE name = '" + name + "';";
		ResultSet result = this.mysqllib.executequery(query);
		if (result == null) {
			return;
		}
		int index = 0;
		String item_name = null;
		PlayerInventory inv = null;
		if (sult) {
			player.sendMessage("§7----------------");
		}
		try {
			while (result.next()) {
				inv = player.getInventory();
				index++;
				item_name = result.getString("item_name");
				int code = result.getInt("itemcode");
				int material = result.getInt("material");
				int amount = result.getInt("amount");
				int count = result.getInt("count")*amount;
				String present = result.getString("present");
				Item item = new Item(code, material, count);
				if(inv.canAddItem(item)) {
					inv.addItem(item);
					if (present == null) {
						player.sendMessage("<§e웹 상점§f> 주문하신 아이템 " + item_name + " 이(가) " + count + "개가 인벤토리에 지급 되었습니다.");
						this.mysqllib.execute("DELETE FROM itemorder WHERE num = '" + result.getInt("num") + "';");
					} else {
						player.sendMessage("<§e웹 상점§f> "+present+" 님이 선물하신 아이템 " + item_name + " 이(가) " + count + "개가 인벤토리에 지급 되었습니다.");
						this.mysqllib.execute("DELETE FROM itemorder WHERE num = '" + result.getInt("num") + "';");
					}
				} else {
					player.sendMessage("§7----------------");
					player.sendMessage("<§e웹 상점§f> 인벤토리에 더이상 공간이 없습니다.");
					player.sendMessage("<§e웹 상점§f> 인벤토리를 비우고 다시 /수령 명령어 입력 부탁 드립니다.");
					player.sendMessage("§7----------------");
					index = index-1;
					break;
				}
			}
			if (index == 0) {
				return;
			}
			player.sendMessage("§7----------------");
			player.sendMessage("<§e웹 상점§f> 전체 " + index + " 개의 주문이 지급 처리 되었습니다. 확인 부탁드립니다. :D");
			player.sendMessage("§7----------------");
			result.close();
		} catch (SQLException e) {
			return;
		}
		return;
	}
	
	public void press_shop(Player player, double x, double y, double z) {
		String item_name = "";
		int item_id = 0;
		int item_dm = 0;
		int cost = 0;
		int count = 0;
		if (x == -498) {
			if (z == 658) {
				item_name = "홍당무";
				item_id = 457;
				count = 16;
				cost = 1;
			} else if (z == 661) {
				item_name = "수박";
				item_id = 360;
				count = 16;
				cost = 2;
			} else if (z == 664) {
				item_name = "호박";
				item_id = 86;
				count = 3;
				cost = 1;
			} else if (z == 667) {
				item_name = "잔디";
				item_id = 2;
				count = 4;
				cost = 1;
			} else {
				return;
			}
		} else if (z == 670) {
			if (x == -501) {
				item_name = "참나무 묘목";
				item_id = 6;
				count = 2;
				cost = 1;
			} else if (x == -504) {
				item_name = "뼈가루";
				item_id = 351;
				item_dm = 15;
				count = 2;
				cost = 1;
			} else if (x == -507) {
				item_name = "밀 씨앗";
				item_id = 295;
				count = 1;
				cost = 1;
			} else if (x == -510) {
				item_name = "홍당무 씨앗";
				item_id = 458;
				count = 1;
				cost = 1;
			} else {
				return;
			}
		} else if (x == -513) {
			if (z == 658) {
				item_name = "석탄";
				item_id = 263;
				count = 4;
				cost = 1;
			} else if (z == 661) {
				item_name = "철괴";
				item_id = 265;
				count = 1;
				cost = 4;
			} else if (z == 664) {
				item_name = "금괴";
				item_id = 266;
				count = 1;
				cost = 12;
			} else if (z == 667) {
				item_name = "다이아몬드";
				item_id = 264;
				count = 1;
				cost = 80;
			} else {
				return;
			}
		} else if (z == 655) {
			if (x == -510) {
				item_name = "레드스톤";
				item_id = 331;
				count = 4;
				cost = 5;
			} else if (x == -507) {
				item_name = "밀";
				item_id = 296;
				count = 16;
				cost = 2;
			} else if (x == -504) {
				item_name = "감자";
				item_id = 392;
				count = 16;
				cost = 2;
			} else if (x == -501) {
				item_name = "당근";
				item_id = 391;
				count = 16;
				cost = 2;
			} else {
				return;
			}
		} else {
			return;
		}
		PlayerInventory inv = player.getInventory();
		Item item = new Item(item_id, item_dm, count);
		if (!Main.Economy24.delmoney(player.getName(), cost)) {
			player.sendMessage("§7----------------");
			player.sendMessage("<§e아이템 구매§f> 아이템 구매를 진행하실 돈이 부족합니다.");
			player.sendMessage("§7----------------");
			return;
		}
		if (!inv.canAddItem(item)) {
			player.sendMessage("§7----------------");
			player.sendMessage("<§e아이템 구매§f> 인벤토리가 전부 차 있습니다. 인벤토리를 비워주시기 바랍니다.");
			player.sendMessage("§7----------------");
			return;
		}
		player.sendMessage("§7----------------");
		player.sendMessage("<§e아이템 구매§f> " + item_name + " " + count + " 개를 " + cost + " OP 에 구매 하셨습니다.");
		player.sendMessage("§7----------------");
		inv.addItem(new Item(item_id, item_dm, count));
		return;
	}
	
	public void press_sell(Player player) {
		Item inv = player.getInventory().getContents().get(player.getInventory().getHeldItemSlot());
		int id = 0;
		try {
			id = inv.getId();
		} catch (NullPointerException e) {
			return;
		}
		int dmg = inv.getDamage();
		double count =(double) inv.getCount();
		double cost = 0;
		String target = null;
		if (id == 264) { //이하 광뮬
			target = "다이아몬드";
			cost = count*80;
		} else if (id == 263) {
			if (dmg == 1) {
				player.sendMessage("§7----------------");
				player.sendMessage("<§e아이템 판매§f> 목탄은 판매 불가능 합니다.");
				player.sendMessage("§7----------------");
				return;
			}
			target = "석탄";
			count = Math.floor(count/4)*4;
			cost = count/4;
		} else if (id == 331) {
			target = "레드스톤";
			count = Math.floor(count/4)*4;
			cost = (count/4)*5;
		} else if (id == 266) {
			target = "금";
			cost = count*12;
		} else if (id == 265) {
			target = "철";
			cost = count*4;
		} else if (id == 296) { //이하 농작물
			target = "밀";
			count = Math.floor(count/16)*16;
			cost = count/8;
		} else if (id == 392) {
			target = "감자";
			count = Math.floor(count/16)*16;
			cost = count/8;
		} else if (id == 391) {
			target = "당근";
			count = Math.floor(count/16)*16;
			cost = count/8;
		} else if (id == 457) {
			target = "홍당무";
			count = Math.floor(count/16)*16;
			cost = count/8;
		} else if (id == 360) {
			target = "수박";
			count = Math.floor(count/16)*16;
			cost = count/8;
		} else if (id == 86) {
			target = "호박";
			count = Math.floor(count/3)*3;
			cost = count/3;
		} else {
			player.sendMessage("§7----------------");
			player.sendMessage("<§e아이템 판매§f> 판매 불가능한 아이템 입니다.");
			player.sendMessage("§7----------------");
			return;
		}
		if (count == 0) {
			player.sendMessage("§7----------------");
			player.sendMessage("<§e아이템 판매§f> 판매할 아이템 수량이 충분하지 않습니다.");
			player.sendMessage("§7----------------");
			return;
		}
		Main.Economy24.addmoney(player.getName(), toIntExact((long) cost));
		player.getInventory().removeItem(inv);
		player.sendMessage("§7----------------");
		player.sendMessage("<§e아이템 판매§f> "+target+" "+toIntExact((long) count)+"개를 "+toIntExact((long) cost)+" OP 에 판매 하였습니다.");
		player.sendMessage("§7----------------");
		return;
	}
	
	// 폐기구역

}
