package com.unique.bullet.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.UUID;

public class Constants {
	public final static int CPUS = Runtime.getRuntime().availableProcessors();
	public final static int CAPACITY = 5 * CPUS;
	public final static int CODEC_FST = 1;
	public final static int CODEC_JDK = 2;
	public final static String CODEC_TAG = "x-message-codec";
	public final static int COREPOOLSIZE = 2;
	public final static int DEFAULT_CODEC = 1;
	public final static String DEFAULT_PASSWORD = "guest";
	public final static String DEFAULT_ROUTINGKEY = "DEFAULT";
	public final static int DEFAULT_TTL = 24 * 60 * 60;
	public final static String DEFAULT_USERNAME = "guest";
	public final static String DEFAULT_VIRTUALHOST = "/";
	public final static boolean DEFAULT_PEER_TO_PEER = false;
	private final static Logger log = LoggerFactory.getLogger(Constants.class);
	public final static int MAXIMUMCOREPOOLSIZE = 5 * CPUS;
	public final static int NEED_ZIP = 1024;
	public static String PEER_TO_PEER_INFO = "";
	public final static String PEER_TO_PEER_TAG = "x-message-forward";
	public final static String RESPONSE_SUFFIX = "#response";
	//
	public final static int TIMEOUT_MILLISECONDS = 30 * 1000;
	public final static int UPZIP = 0;
	public final static String X_MESSAGE_TTL = "x-message-ttl";
	public final static int ZIP = 1;
	public final static String ZIP_TAG = "x-message-zip";
	static {
		FileReader fr = null;
		BufferedReader br = null;
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			String path = Constants.class.getClassLoader().getResource("").getPath();
			File file = new File(path + "if-bullet.info");
			if (file.exists()) {
				fr = new FileReader(file);
				br = new BufferedReader(fr);
				String info = br.readLine();
				if (info == null || info.trim().length() <= 0) {
					fw = new FileWriter(file);
					bw = new BufferedWriter(fw);
					PEER_TO_PEER_INFO = UUID.randomUUID().toString();
					bw.write(PEER_TO_PEER_INFO);
					bw.flush();
				} else {
					PEER_TO_PEER_INFO = info;
				}
			} else {
				file.createNewFile();
				fw = new FileWriter(file);
				bw = new BufferedWriter(fw);
				PEER_TO_PEER_INFO = UUID.randomUUID().toString();
				bw.write(PEER_TO_PEER_INFO);
				bw.flush();
			}
		} catch (Exception e) {
			log.error(">>>>>get peer-to-peer info error", e);
			System.exit(0);
		} finally {
			try {
				if (fr != null) {
					fr.close();
				}
			} catch (Exception e) {
			}
			try {
				if (br != null) {
					br.close();
				}
			} catch (Exception e) {
			}
			try {
				if (fw != null) {
					fw.close();
				}
			} catch (Exception e) {
			}
			try {
				if (bw != null) {
					bw.close();
				}
			} catch (Exception e) {
			}
		}
	}
}
