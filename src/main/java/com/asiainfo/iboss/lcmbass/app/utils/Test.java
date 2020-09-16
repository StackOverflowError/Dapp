//package com.asiainfo.iboss.lcmbass.app.utils;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//
//
//import ch.ethz.ssh2.Connection;
//import ch.ethz.ssh2.SCPClient;
//
//
//public class Test {
//
//	public static void main(String[] args) throws IOException {
//		Connection conn = new Connection("47.96.225.104", 22);
//		conn.connect(); // 连接
//		boolean authenticate = conn.authenticateWithPassword("webapp", "Blzx@308"); // 认证
//		if(!authenticate) {
//			System.out.println("登陆失败，请检查登陆相关信息！！！退出！！！");
//		}
//		System.out.println("登陆成功，开始传输文件！！！");
//		String fileAbs = "E:\\logs\\aa.mp3";
//		File file=new File(fileAbs);
//		SCPClient scp = conn.createSCPClient();
//		try {
//			scp.put(file.getAbsolutePath(), "/home/webapp/temp");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}finally {
//
//		}
//	}
//
//}
