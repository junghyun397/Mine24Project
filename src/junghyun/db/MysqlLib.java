package junghyun.db;

import junghyun.Main;

import java.sql.*;

public class MysqlLib {
	
	public static Connection connect;
	
	public Main plugin;
	
    public MysqlLib onEnable(Main plugin) {
    	this.plugin = plugin;
		this.plugin.getLogger().info("MYSQL 라이브러리 로딩중");
		//MySql 데이터베이스 연결
		String url = "jdbc:mysql://localhost:3306/server?characterEncoding=utf8";
		String user = "root";
		String passwd = "pwd!";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			this.plugin.getLogger().info("드라이버 검색 완료");
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
			this.plugin.getLogger().info("드라이버 검색 실패");
			return null;
		}
		try {
			MysqlLib.connect = DriverManager.getConnection(url, user, passwd);
			this.plugin.getLogger().info("드라이버 로딩완료");
		} catch (SQLException e) {
			e.printStackTrace();
			this.plugin.getLogger().info("드라이버 로딩 실패");
			return null;
		}
		return this;
	}
	
	//실제 코드 실행구역
	public ResultSet executequery(String query) {
		try {
			PreparedStatement pstmt = MysqlLib.connect.prepareStatement(query);
			ResultSet result = pstmt.executeQuery(query);
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public int executeupdate(String query) {
		try {
			PreparedStatement pstmt = MysqlLib.connect.prepareStatement(query);
			int result = pstmt.executeUpdate(query);
			pstmt.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public boolean execute(String query) {
		try {
			PreparedStatement pstmt = MysqlLib.connect.prepareStatement(query);
			boolean result = pstmt.execute(query);
			pstmt.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	

}
