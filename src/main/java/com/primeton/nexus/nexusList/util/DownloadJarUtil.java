package com.primeton.nexus.nexusList.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component 
public class DownloadJarUtil {
	/**
	 * 下载jar包到本地maven库中
	 * 
	 * @param groupId    groupId
	 * @param artifactId artifactId
	 * @param nexusUrl   resource link
	 * @author angw@primeton.com
	 */
	@Value("${mvn.path}")
	private String localRepoPath;

	public void saveJar(String groupId, String artifactId, String nexusUrl) throws Exception {


/*		// 假设本地已有这个jar包，拼接出应该有的路径，判断是否已有，而不重复下载。
		File existFile = new File(
				localRepoPath + groupId.replace(".", "\\") + "\\" + nexusUrl.substring(nexusUrl.lastIndexOf("/") + 1));
		if (existFile.exists()) {
			System.out.println(existFile.length());
			return;
		}
*/		
		//根据groupId拼接出路径在本地建立目录
		File file = new File(localRepoPath + groupId.replace(".", "\\"));
		if (!file.exists())
			file.mkdirs();
		URL url = new URL(nexusUrl);
		// 连接类的父类，抽象类
		URLConnection urlConnection = url.openConnection();
		// http的连接类
		HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
		// 设定请求的方法，默认是GET（对于知识库的附件服务器必须是GET，如果是POST会返回405。流程附件迁移功能里面必须是POST，有所区分。）
		httpURLConnection.setRequestMethod("GET");
		// 设置字符编码
		httpURLConnection.setRequestProperty("Charset", "UTF-8");
		// 打开到此 URL 引用的资源的通信链接（如果尚未建立这样的连接）。
		int code = httpURLConnection.getResponseCode();

		InputStream inputStream = httpURLConnection.getInputStream();
		OutputStream out = new FileOutputStream(file + "\\" + nexusUrl.substring(nexusUrl.lastIndexOf("/")));
		int size = 0;
		int lent = 0;
		byte[] buf = new byte[1024];
		while ((size = inputStream.read(buf)) != -1) {
			lent += size;
			out.write(buf, 0, size);
		}
		inputStream.close();
		out.close();
	}
}
