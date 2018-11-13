package junghyun.Auth24;

import junghyun.Main;
import junghyun.class_box.Mine24player;
import junghyun.db.MysqlLib;
import cn.nukkit.command.CommandSender;
import cn.nukkit.Player;

import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class auth24 {
	
	public MysqlLib mysqllib = new MysqlLib();
	
	public Main plugin;
	
	private final static Pattern email_check = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE); //이메일 확인
	
	public auth24 onEnable(Main plugin) {
		this.plugin = plugin;
		this.plugin.getLogger().info("통합 로그인 시스템 로딩중.....");
		this.plugin.getLogger().info("통합 로그인 시스템 로딩완료.");
		return this;
	}
	
    public void press_cmd(CommandSender sender, int type, String[] args) {
    	if (type == 1) {
    		Player player = sender.getServer().getPlayer(sender.getName());
        	this.pressregister(player, args);
    	} else if (type == 2) {
    		Player player = sender.getServer().getPlayer(sender.getName());
        	this.presslogin(player, args);
    	} else if (type == 3) {
    		Player player = sender.getServer().getPlayer(sender.getName());
        	this.pwd_hub(player, args);
    	} else if (type == 4) {
        	this.press_email_check(sender, args);
    	}
    }
    
    private void pwd_hub(CommandSender player, String[] args) {
    	String cmd = null;
    	try {
    		cmd = args[0];
    	} catch (ArrayIndexOutOfBoundsException e){
    		player.sendMessage("§7----------------");
    		player.sendMessage("<§e계정보호§f> /비밀번호 초기화 또는 /비밀번호 변경 형태로 적어주시기 바랍니다.");
    		player.sendMessage("§7----------------");
    		return;
    	}

		switch (cmd) {
			case "초기화":
				if (!Main.players_.get(player.getName()).logined) {
					this.pressnewpasswd(player, args);
				} else {
					player.sendMessage("§7----------------");
					player.sendMessage("<§e계정보호§f> 이미 로그인 되어 있습니다. /비밀번호 초기화 명령어를 이용 해주시기 바랍니다.");
					player.sendMessage("§7----------------");
				}
				return;
			case "변경":
				if (Main.players_.get(player.getName()).logined) {
					this.pressdelpasswd(player, args);
				} else {
					player.sendMessage("§7----------------");
					player.sendMessage("<§e계정보호§f> 로그인 되어있지 않습니다. /비밀번호 변경 명령어를 이용 해주시기 바랍니다.");
					player.sendMessage("§7----------------");
				}
				return;
			default:
				player.sendMessage("§7----------------");
				player.sendMessage("<§e계정보호§f> /비밀번호 초기화 또는 /비밀번호 변경 형태로 적어주시기 바랍니다.");
				player.sendMessage("§7----------------");
		}
    }
    
    private void pressnewpasswd(CommandSender player, String[] args) {
    	String name = player.getName().toLowerCase();
    	String mail = null;
    	try {
    		mail = args[1];
    	} catch (ArrayIndexOutOfBoundsException e) {
    		player.sendMessage("§7----------------");
    		player.sendMessage("<§e계정보호§f> 이메일을 입력 해 주시기 바랍니다.");
    		player.sendMessage("<§e계정보호§f> /비밀번호 초기화 <이메일> 형태로 적어 주시기 바랍니다.");
    		player.sendMessage("§7----------------");
    		return;
    	}
    	
    	if (this.checkup_mail(name, mail)) {
    		this.send_mail_reset(mail, name);
    		player.sendMessage("§7----------------");
    		player.sendMessage("<§e계정보호§f> "+mail+"이메일로 초기화 페이지를 보내 드렸습니다.");
    		player.sendMessage("<§e계정보호§f> 이메일을 확인 해주시기 바랍니다.");
    		player.sendMessage("§7----------------");
    	} else {
    		player.sendMessage("§7----------------");
    		player.sendMessage("<§e계정보호§f> "+mail+"이메일은 유효하지 않은 이메일 입니다.");
    		player.sendMessage("<§e계정보호§f> 이메일 인증을 받지 않으셨거나 이메일이 일치하지 않습니다.");
    		player.sendMessage("<§e계정보호§f> 이메일을 다시 확인해 주시기 바랍니다.");
    		player.sendMessage("§7----------------");
    		return;
    	}
    	
    }
    
    private void pressdelpasswd(CommandSender player, String[] args) {
    	String passwd = null;
    	try {
    		passwd = args[1];
    	} catch (ArrayIndexOutOfBoundsException e) {
    		player.sendMessage("§7----------------");
    		player.sendMessage("<§e계정보호§f> 비밀번호를 입력 해 주시기 바랍니다.");
    		player.sendMessage("<§e계정보호§f> /비밀번호 변경 <원하는 비밀번호> 형태로 적어 주시기 바랍니다.");
    		player.sendMessage("§7----------------");
    		return;
    	}
    	this.resetpasswd(player.getName().toLowerCase(), passwd);
    	player.sendMessage("§7----------------");
    	player.sendMessage("<§e계정보호§f> 비밀번호 변경이 완료 되었습니다. §e비밀번호 " + passwd + " 를 확인 §f해 주시기 바랍니다. :D");
		player.sendMessage("§7----------------");
	}

	public void pressregister(Player player, String[] args) {
    	String name = player.getName().toLowerCase();
    	try {
    		String password = args[0]; //비밀번호 입력않는 오류시 경고 출력문으로 넘김
    		int index = this.chackregister_login(name);
    		if (index == 1) {
    			String hash = this.gethash(name, password);
        		String ip = player.getServer().getPlayer(name).getAddress();
        		Long uuid = this.getClientId(player.getServer().getPlayer(name));
        		this.registerplayer(name, hash, ip, uuid);
        		player.sendMessage("§7----------------");
            	player.sendMessage("<§e계정보호§f> 가입이 완료 되었습니다. §e비밀번호 " + password + " 를 확인 §f해 주시기 바랍니다. :D");
            	player.sendMessage("<§e계정보호§f> §e비밀번호가 잘못 되었다면 /비밀번호변경§f 으로 비밀번호를 바꾸어 주시기 바랍니다.");
            	player.sendMessage("<§e안내§f> 서버 §e공식카페 cafe.mine24.net 에 가입§f하시면 서버의 정보를 빠르게 받아 보실 수 있습니다.");
            	player.sendMessage("§7----------------");
            	
            	Mine24player player_box = Main.players_.get(player.getName());
            	player_box.logined = true;
            	player_box.registered = true;
            	player_box.pass_limt = true;
            	Main.players_.put(player.getName(), player_box);
                Main.playerlimit.start_startup(player);
    			return;
    		} else if (index == 0) {
    			player.sendMessage("§7----------------");
            	player.sendMessage("<§e계정보호§f> 가입이 완료 되었습니다. §e비밀번호 " + password + " 를 확인 §f해 주시기 바랍니다. :D");
            	player.sendMessage("<§e계정보호§f> §e비밀번호가 잘못 되었다면 /비밀번호변경§f 으로 비밀번호를 바꾸어 주시기 바랍니다.");
            	player.sendMessage("<§e안내§f> 서버 §e공식카페 cafe.mine24.net 에 가입§f하시면 서버의 정보를 빠르게 받아 보실 수 있습니다.");
            	player.sendMessage("§7----------------");
    			this.resetpasswd(name, password);
    			return;
    		} else {
    			player.sendMessage("§7----------------");
    			player.sendMessage("<§e계정보호§f> 이미 가입이 되어 있습니다. /로그인 명령어로 로그인을 해 주시기 바랍니다. :D");
    			player.sendMessage("§7----------------");
    		}
            return;
    	} catch (ArrayIndexOutOfBoundsException e) {
    		player.sendMessage("§7----------------");
    		player.sendMessage("<§e계정보호§f> 비밀번호를 입력 해 주시기 바랍니다.");
    		player.sendMessage("<§e계정보호§f> /가입 <원하는 비밀번호> 형태로 적어 주시기 바랍니다.");
    		player.sendMessage("§7----------------");
    		return;
    	}
    }
    
    public void presslogin(Player player, String[] args) {
    	String name = player.getName().toLowerCase();
    	
    	Mine24player player_box = Main.players_.get(player.getName());
    	
    	if (player_box.pass_limt) {
    		player.sendMessage("§7----------------");
    		player.sendMessage("<§e계정보호§f> 이미 로그인 되어 있습니다.");
    		player.sendMessage("§7----------------");
    		return;
    	}
    	try {
    		String password = args[0]; //오류나면 아래 블록으로
    		String hash = this.gethash(name, password);
        	if (this.chacklogin(name, hash)) {
        		
        		player_box.logined = true;
        		player_box.pass_limt = true;
        		Main.players_.put(player.getName(), player_box);
        		this.updateplayer(player);
        		player.sendMessage("§7----------------");
        		player.sendMessage("<§e계정보호§f> 로그인에 성공 하였습니다. 반갑습니다, " + name + " 님!");
        		player.sendMessage("<§e계정보호§f> Mine24 서버에서 즐거운 시간 보내시기 바랍니다.");
        		player.sendMessage("§7----------------");
        		Main.popup_task.del_popup(player.getName(), Main.Economy24.getmoney(player.getName()));
        		Main.Shop24.wed_item_check(player, name, false);
        		Main.playerlimit.start_startup(player);
        		return;
        	}
        	else {
        		player.sendMessage("§7----------------");
        		player.sendMessage("<§e계정보호§f> 로그인에 실패 하였습니다. 비밀번호가 틀립니다.");
        		player.sendMessage("<§e계정보호§f> 비밀번호를 잊으셨다면 /비밀번호 초기화 명령어를 이용 해주시기 바랍니다.");
        		player.sendMessage("§7----------------");
        		return;
        	}
    	}
    	catch (ArrayIndexOutOfBoundsException e) {
    		player.sendMessage("§7----------------");
        	player.sendMessage("<§e계정보호§f> /로그인 <가입했을때 입력했던 비밀번호> 형태로 적어 주시기 바랍니다.");
        	player.sendMessage("§7----------------");
    		return;
    	}
    }
    
    public boolean register_startup(Player player, String[] msg) {
    	String name = player.getName().toLowerCase();
    	try {
    		String password = msg[1];
    		int index = this.chackregister_login(name);
    		if (index == 1) {
    			String hash = this.gethash(name, password);
        		String ip = player.getServer().getPlayer(name).getAddress();
        		Long uuid = this.getClientId(player.getServer().getPlayer(name));
        		this.registerplayer(name, hash, ip, uuid);
        		player.sendMessage("§7----------------");
            	player.sendMessage("<§e계정보호§f> 가입이 완료 되었습니다. §e비밀번호 " + password + " 를 확인 §f해 주시기 바랍니다. :D");
            	player.sendMessage("<§e계정보호§f> §e비밀번호가 잘못 되었다면 /비밀번호변경§f 으로 비밀번호를 바꾸어 주시기 바랍니다.");
            	player.sendMessage("<§e안내§f> 서버 §e공식카페 cafe.mine24.net 에 가입§f하시면 서버의 정보를 빠르게 받아 보실 수 있습니다.");
            	player.sendMessage("§7----------------");
            	
            	Mine24player player_box = Main.players_.get(player.getName());
            	player_box.logined = true;
            	player_box.registered = true;
            	player_box.pass_limt = true;
            	Main.players_.put(player.getName(), player_box);
                Main.playerlimit.start_startup(player);
    			return true;
    		} else if (index == 0) {
    			player.sendMessage("§7----------------");
            	player.sendMessage("<§e계정보호§f> 가입이 완료 되었습니다. §e비밀번호 " + password + " 를 확인 §f해 주시기 바랍니다. :D");
            	player.sendMessage("<§e계정보호§f> §e비밀번호가 잘못 되었다면 /비밀번호변경§f 으로 비밀번호를 바꾸어 주시기 바랍니다.");
            	player.sendMessage("<§e안내§f> 서버 §e공식카페 cafe.mine24.net 에 가입§f하시면 서버의 정보를 빠르게 받아 보실 수 있습니다.");
            	player.sendMessage("§7----------------");
    			this.resetpasswd(name, password);
    			return true;
    		} else {
    			player.sendMessage("§7----------------");
    			player.sendMessage("<§e계정보호§f> 이미 가입이 되어 있습니다. /로그인 명령어로 로그인을 해 주시기 바랍니다. :D");
    			player.sendMessage("§7----------------");
    		}
            return false;
    	} catch (ArrayIndexOutOfBoundsException e) {
    		player.sendMessage("§7----------------");
    		player.sendMessage("<§e계정보호§f> 비밀번호를 입력 해 주시기 바랍니다.");
    		player.sendMessage("<§e계정보호§f> /가입 <원하는 비밀번호> 형태로 적어 주시기 바랍니다.");
    		player.sendMessage("§7----------------");
    		return false;
    	}
    }
    
    public void press_email_check(CommandSender sender, String[] args) {
    	String mail;
    	try {
    		mail = args[0];
    	} catch (ArrayIndexOutOfBoundsException e) {
    		sender.sendMessage("§7----------------");
    		sender.sendMessage("<§e계정인증§f> /인증 <이메일> 형태로 적어 주시기 바랍니다.");
    		sender.sendMessage("§7----------------");
    		return;
    	}
    	
    	if (mail.equals("초기화")) {
    		this.press_reset_email(sender);
    	} else {
    		int mail_check = this.check_email(mail, sender.getName().toLowerCase());
    		if (this.check_mail(mail)) {
    			sender.sendMessage("§7----------------");
        		sender.sendMessage("<§e계정인증§f> "+mail+"는 이메일이 아닙니다. 이메일 주소를 정확히 적어주세요.");
        		sender.sendMessage("<§e계정인증§f> example@example.com 형태로 적어 주시기 바랍니다.");
        		sender.sendMessage("§7----------------");
        		return;
    		} else if (mail_check != 0) {
    			if (mail_check == 3) {
    				sender.sendMessage("§7----------------");
    				sender.sendMessage("<§e계정인증§f> 이미 다른 메일로 인증을 받았습니다.");
    				sender.sendMessage("<§e계정인증§f> /인증 초기화 명령어를 통해 인증을 초기화 한 다음 다시 진행 해주시기 바랍니다.");
    				sender.sendMessage("§7----------------");
    			} else if (mail_check == 2) {
    				sender.sendMessage("§7----------------");
    				sender.sendMessage("<§e계정인증§f> "+mail+"으로 이미 인증을 받았습니다.");
    				sender.sendMessage("<§e계정인증§f> /인증 초기화 명령어를 통해 인증을 초기화 한 다음 다시 진행 해주시기 바랍니다.");
    				sender.sendMessage("§7----------------");
    			} else {
    				sender.sendMessage("§7----------------");
    				sender.sendMessage("<§e계정인증§f> "+mail+"은 이미 다른 플레이어가 인증을 완료한 메일입니다.");
    				sender.sendMessage("<§e계정인증§f> 다른 이메일로 다시 시도 해주시기 바랍니다.");
    				sender.sendMessage("§7----------------");
    			}
    		} else if (!this.send_mail_auth(mail, sender.getName())) {
    			sender.sendMessage("§7----------------");
        		sender.sendMessage("<§e계정인증§f> 이메일 전송에 실패 하였습니다. 다시 시도 해주시기 바랍니다.");
        		sender.sendMessage("§7----------------");
        		return;
    		} else {
    			sender.sendMessage("§7----------------");
    			sender.sendMessage("<§e계정인증§f> "+mail+"로 이메일을 보내 드렸습니다. 메일함을 확인 해주세요.");
    			sender.sendMessage("<§e계정인증§f> 메일이 도착하지 않았다면 스팸 메일함을 확인해 주시기 바랍니다.");
        		sender.sendMessage("§7----------------");
    			return;
    		}
    	}
    }
    
    public void press_reset_email(CommandSender sender) {
    	sender.sendMessage("§7----------------");
		sender.sendMessage("<§e계정인증§f> 이메일 인증을 초기화 하였습니다.");
		sender.sendMessage("<§e계정인증§f> /인증 <이메일> 명령어로 다시 인증해 주시기 바랍니다.");
		sender.sendMessage("§7----------------");
    }
    
    public boolean chacklogin(String name, String hash) {
		ResultSet result = this.getplayerdata(name);
		try {
			if (result.next()) {
				String info = result.getString("hash");
				if (hash.equals(info)) {
					return true;
				}
				else {
					return false;
				}
			}
		} catch (SQLException e) {
			return false;
		}
		return false;
	}
    
    public int checkautologin(Player player) {
    	return this.chackuuid(player.getName().toLowerCase(), this.getClientId(player));
    }
    
    public int chackregister_login(String name) {
    	ResultSet result = this.getplayerdata(name);
    	try {
			if (result.next()) {
				if (result.getString("uuid").equals(result.getString("hash"))) {
					return 0;
				}
				return 1;
			}
		} catch (SQLException e) {
			
			return 2;
		}
    	return 2;
    }
    
    public boolean chackregister(String name) {
    	ResultSet result = this.getplayerdata(name);
    	try {
			if (result.next()) {
				return true;
			}
		} catch (SQLException e) {
			return false;
		}
    	return false;
    }
    
    public boolean chackregister_uid(String name) {
    	ResultSet result = this.getplayerdata(name);
    	try {
			if (result.next()) {
				if (result.getString("hash").equals(result.getString("uuid"))) {
					return false;
				}
				return true;
			}
		} catch (SQLException e) {
			return false;
		}
    	return false;
    }
    
    public int chackuuid(String player, long uuid) {
    	ResultSet result = this.getplayerdata(player);
    	try {
			if (result.next()) {
				if(result.getString("uuid").equals(Long.toString(uuid))) {
					return 1;
				} else if (result.getString("hash").equals(Long.toString(uuid))) {
					return 2;
				}
			}
		} catch (SQLException e) {
			return 0;
		}
    	return 0;
    }
    
    public void resetpasswd(String name, String passwd) {
    	String query = "UPDATE simpleauth_players SET hash = '"+(this.gethash(name, passwd))+"' WHERE name ='"+name+"';";
    	this.mysqllib.executeupdate(query);
    }
    
    public void updateplayer(Player player) {
    	Long uuid = this.getClientId(player);
    	String ip = player.getAddress();
    	String timestamp_long = String.valueOf(Calendar.getInstance().getTime().getTime());
		String timestamp = timestamp_long.substring(0, 10);
		String name = player.getName().toLowerCase();
    	String query = "UPDATE simpleauth_players SET logindate='"+timestamp+"', lastip='"+ip+"', uuid='"+uuid+"' WHERE name ='"+name+"';";
		this.mysqllib.executeupdate(query);
    	return;
    }
    
    public void registerplayer_uuid(Player player) {
    	Long uuid = this.getClientId(player);
    	this.registerplayer(player.getName().toLowerCase(), Long.toString(uuid), player.getAddress(), uuid);
    	return;
    }
    
    public boolean check_mail(String address) {
    	Matcher match = auth24.email_check.matcher(address);
    	if (match.find()) {
    		return false;
    	}
    	return true;
    }
    
    public boolean send_mail_auth(String address, String player) {
    	Thread mail_sender = new Mailsender(address, player, this.gettoken_auth(player, address), 1);
    	mail_sender.start();
    	return true;
    }
    
    public boolean send_mail_reset(String address, String player) {
    	Thread mail_sender = new Mailsender(address, player, this.gettoken_reset(player, address), 2);
    	mail_sender.start();
    	return true;
    }
    
    public int check_email(String address, String player) {
    	player = player.toLowerCase();
    	String query = "SELECT * FROM simpleauth_players WHERE mail = '" + address + "';";
		ResultSet rs_addr = Main.mysqllib.executequery(query);
		ResultSet rs_player = this.getplayerdata(player);
		try {
			if (rs_player.next()) {
				if (rs_addr.getString("mail") != null) {
					return 3;
				}
			}
			if (rs_addr.next()) {
				if (rs_addr.getString("name").equals(player)) {
					return 2;
				}
				return 1;
			}
		} catch (SQLException e) {
			return 0;
		}
    	return 0;
    }
    
    public boolean checkup_mail(String player, String address) {
    	ResultSet rs = this.getplayerdata(player);
    	try {
			if (rs.next()) {
				if (rs.getString("mail_check").equals("1") && rs.getString("mail").equals(address)) {
					return true;
				}
			}
		} catch (SQLException e) {
			return false;
		}
    	return false;
    }
    
    public String gettoken_auth(String player, String address) {
    	String timestamp_long = String.valueOf(Calendar.getInstance().getTime().getTime());
    	String non_md_token = timestamp_long+"_"+player+"_"+address;
    	String token = this.md5(non_md_token);
    	String query = "UPDATE simpleauth_players SET mail = '"+address+"', mail_check = '"+token+"' WHERE name = '"+player.toLowerCase()+"';";
    	Main.mysqllib.executeupdate(query);
    	return token;
    }
    
    public String gettoken_reset(String player, String address) {
    	String timestamp_long = String.valueOf(Calendar.getInstance().getTime().getTime());
    	String non_md_token = timestamp_long+"_"+player+"_"+address;
    	String token = this.md5(non_md_token);
    	String query = "UPDATE simpleauth_players SET mail = '"+address+"', pwd_check = '"+token+"' WHERE name = '"+player.toLowerCase()+"';";
    	Main.mysqllib.executeupdate(query);
    	return token;
    }
    
    //플레이어 가입 처리
    public void registerplayer(String player, String hash, String ip, Long uuid) {
		String timestamp_long = String.valueOf(Calendar.getInstance().getTime().getTime());
		String timestamp = timestamp_long.substring(0, 10);
    	String query = "INSERT INTO simpleauth_players(name, registerdate, logindate, lastip, hash, uuid) VALUES('" + player + "', " + timestamp + ", " + timestamp + ", '" + ip + "', '" + hash + "', " + uuid + ");";
    	this.mysqllib.execute(query);
    	return;
    }
    
    //해시를 얻어옴
    public String gethash(String name,String password) {
        SimpleAuthHasher SimpleAuthHasher = new SimpleAuthHasher();
        String hash = SimpleAuthHasher.getHash(name, password);
        return hash;
    }
    
    //쿼리를 보내어 플레이어 데이터를 파악후 배열로 반환
    public ResultSet getplayerdata(String name) {
    	String query = "SELECT * FROM simpleauth_players WHERE name = '" + name + "';";
    	return this.mysqllib.executequery(query);
    }
    
    public long getClientId(Player player) {
		Class<? extends Player> reflect =  player.getClass();
		Field var;
		long clientId = 0;
		try {
			var = reflect.getDeclaredField("randomClientId");
			var.setAccessible(true);
			try {
				clientId = var.getLong(player);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				player.kick("불량 클라이언트");
				e.printStackTrace();
			}
		} catch (NoSuchFieldException | SecurityException e) {
			player.kick("불량 클라리언트");
			e.printStackTrace();
		}
		return clientId;
	}

    public String md5(String str){
    	String md5 = ""; 
    	try {
    		MessageDigest md = MessageDigest.getInstance("MD5"); 
    		md.update(str.getBytes()); 
    		byte byteData[] = md.digest();
    		StringBuffer sb = new StringBuffer(); 
    		for(int i = 0 ; i < byteData.length ; i++){
    			sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));
    		}
    		md5 = sb.toString();
    	} catch(NoSuchAlgorithmException e) {
    		e.printStackTrace(); 
    		md5 = null; 
    	}
    	return md5;
    }
}