package com.primeton.nexus.nexusList.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 通过拼接url去对页面进行解析从而获取nexus库中信息的工具类
 * 
 * @author angw@priemton.com
 *
 */
@SuppressWarnings("all")
@Component
public class ParseHtmlUtil {
	/**
	 * nexus服务所在ip地址（从配置文件中读取）
	 */
	@Value("${nexus.ip}")
	private String nexusIp;
	/**
	 * nexus服务所在的port（从配置文件中读取）
	 */
	@Value("${nexus.port}")
	private String nexusPort;
	/**
	 * 用于拼接url的字符串
	 */
	private String nexusUrl = null;

	/**
	 * 通过repositoryId，获取到具体库中所有依赖的groupId和artifactId 是一种迭代的方式（未完成）
	 * 
	 * @author angw@primeton.com
	 * @return List<String> 目标repository中现存的扩展名称列表
	 */
	public List parseHtmlBody(String repositoryId) {

		nexusUrl = String.format("http://%s:%s/nexus/content/repositories/%s/", nexusIp,nexusPort,repositoryId);
		List jarList = getList(nexusUrl);
		if (!jarList.get(0).equals("找不到此版本信息!")) {
			jarList.remove(0);
		}
		jarList.remove(jarList.size() - 1);
		return jarList;
	}

	/**
	 * 通过repositoryId和groupId来获取具体依赖的artifactdId
	 * 
	 * @author angw@primeton.com
	 * @param repositoryId repositoryId
	 * @param groupId      groupId
	 * @return List<String> artifactId
	 */
	public List<String> parseHtmlBody(String repositoryId, String groupId) {

		String groupIdPath = groupId.replace(".", "/");

		nexusUrl = String.format("http://%s:%s/nexus/content/repositories/%s/%s", nexusIp, nexusPort, repositoryId,
				groupIdPath);

		List<?> jarList = getList(nexusUrl);
		if (!jarList.get(0).equals("找不到此版本信息!")) {
			jarList.remove(0);
		}
		return (List<String>) jarList;
	}

	/**
	 * 通过repositoryId+groupId+artifactdId获取依赖所有版本号
	 * 
	 * @author angw@primeton.com
	 * @param repositoryId repositoryId
	 * @param groupId      groupId
	 * @param artifactId   artifactId
	 * @return List<String> 扩展的所有版本号versionCode
	 */
	public List<String> parseHtmlBody(String repositoryId, String groupId, String artifactId) {

		String groupIdPath = groupId.replace(".", "/");
		nexusUrl = String.format("http://%s:%s/nexus/content/repositories/%s/%s/%s", nexusIp, nexusPort, repositoryId,
				groupIdPath, artifactId);

		List<?> jarList = getList(nexusUrl);
		if (!jarList.get(0).equals("找不到此版本信息!")) {
			jarList.remove(0);
			for (int i = 0; i < jarList.size(); i++) {
				if (((String) jarList.get(i)).startsWith("maven")) {
					jarList.remove(i);
				}
			}
			jarList.remove(jarList.size() - 1);
		}
		return (List<String>) jarList;
	}

	/**
	 * 根据repositoryId+groupId+artifactId+version获取具体的jar包
	 * 
	 * @author angw@primeton.com
	 * @param repositoryId repositoId
	 * @param groupId      groupId
	 * @param artifactId   artifactId
	 * @param version
	 * @return List<String> 具体jar包名称
	 */
	public List<String> parseHtmlBody(String repositoryId, String groupId, String artifactId, String version) {

		String groupIdPath = groupId.replace(".", "/");
		nexusUrl = String.format("http://%s:%s/nexus/content/repositories/%s/%s/%s/%s", nexusIp, nexusPort,
				repositoryId, groupIdPath, artifactId, version);

		List<?> jarList = getList(nexusUrl);
		if (!jarList.get(0).equals("找不到此版本信息!")) {
			jarList.remove(0);
			List<String> subList = (List<String>) jarList.subList(0, 1);
			subList.set(0, subList.get(0) + "r");
			return subList;
		}

		return (List<String>) jarList;
	}

	/**
	 * 拼接后的url进行检索
	 * 
	 * @author angw@primeton.com
	 * @param url url
	 * @return
	 */
	public List<?> getList(String url) {
		Connection connect = Jsoup.connect(nexusUrl);
		Document document = null;
		List<String> jarList = new ArrayList<>();
		try {
			document = connect.get();
			Elements elements = document.select("a[href]");
			for (org.jsoup.nodes.Element element : elements) {
				String jarHtml = element.toString();
				String jarName = jarHtml.substring(jarHtml.indexOf("\">") + 2, jarHtml.indexOf("</a>") - 1);
				jarList.add(jarName);
			}
		} catch (IOException e) {
			jarList.add("找不到此版本信息!");
		}
		return jarList;
	}

}
