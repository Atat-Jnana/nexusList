package com.primeton.nexus.nexusList.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class NexusInfoUtil {
	public static final String REPOSITORY_FORMAT = "http://%s:%s/nexus/content/repositories/%s";
	public static final String GROUP_FORMAT = REPOSITORY_FORMAT + "/%s";
	public static final String ARTIFACT_FORMAT = GROUP_FORMAT + "/%s";
	private static final String VERSION_FORMAT = ARTIFACT_FORMAT + "/%s";
	private static final String SELECT_OPTION = "a[href]";

	private Logger logger = LoggerFactory.getLogger(NexusInfoUtil.class);
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
	 * 通过repositoryId，获取到具体库中所有依赖的groupId和artifactId 是一种迭代的方式（未完成）
	 * 
	 * @author angw@primeton.com
	 * @return List<String> 目标repository中现存的扩展名称列表
	 */
	public List<String> parseRepositoryBody(String repositoryId) {
		logger.debug("start invoke NexusInfoUtil.parseRepositoryBody.");
		String nexusUrl = String.format(REPOSITORY_FORMAT, nexusIp, nexusPort, repositoryId);
		List<String> result = parseHtmlBody(nexusUrl);
		logger.debug("finish invoke ParseHtmlUtil.parseRepositoryBody.");
		return result;
	}

	/**
	 * 通过repositoryId和groupId来获取具体依赖的artifactdId
	 * 
	 * @author angw@primeton.com
	 * @param repositoryId repositoryId
	 * @param groupId      groupId
	 * @return List<String> artifactId
	 */
	public List<String> parseGroupBody(String repositoryId, String groupId) {
		logger.debug("start invoke NexusInfoUtil.parseGroupBody.");
		String groupIdPath = groupId.replace(".", "/");
		String nexusUrl = String.format(GROUP_FORMAT, nexusIp, nexusPort, repositoryId, groupIdPath);
		List<String> result = parseHtmlBody(nexusUrl);
		logger.debug("finish invoke NexusInfoUtil.parseGroupBody.");
		return result;
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
	public List<String> parseArtifactBody(String repositoryId, String groupId, String artifactId) {
		logger.debug("start invoke NexusInfoUtil.parseArtifactBody.");
		String groupIdPath = groupId.replace(".", "/");
		String nexusUrl = String.format(ARTIFACT_FORMAT, nexusIp, nexusPort, repositoryId, groupIdPath, artifactId);
		List<String> result = parseHtmlBody(nexusUrl);
		logger.debug("finish invoke NexusInfoUtil.parseArtifactBody.");
		return result;
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
	public List<String> parseVersionBody(String repositoryId, String groupId, String artifactId, String version) {

		logger.debug("start invoke NexusInfoUtil.parseVersionBody.");
		String groupIdPath = groupId.replace(".", "/");
		String nexusUrl = String.format(VERSION_FORMAT, nexusIp, nexusPort, repositoryId, groupIdPath, artifactId,
				version);
		List<String> result = parseHtmlBody(nexusUrl);
		logger.debug("finish invoke NexusInfoUtil.parseVersionBody.");
		return result;
	}

	/**
	 * 拼接后的url进行检索
	 * 
	 * @author angw@primeton.com
	 * @param url url
	 * @return
	 */
	private List<String> parseHtmlBody(String nexusUrl) {
		logger.debug("start invoke NexusInfoUtil.parseHtmlBody.");
		Connection connect = Jsoup.connect(nexusUrl);
		Document document = null;
		List<String> jarList = new ArrayList<>();
		try {
			document = connect.get();
			Elements elements = document.select(SELECT_OPTION);
			for (org.jsoup.nodes.Element element : elements) {
				String jarHtml = element.toString();
				if (jarHtml.contains("Parent") || jarHtml.contains("maven-") || jarHtml.contains(".md5")
						|| jarHtml.contains(".sha") || jarHtml.contains(".sha1") || jarHtml.contains(".pom")) {
					continue;
				}
				String name = jarHtml.substring(jarHtml.indexOf("\">") + 2, jarHtml.indexOf("</a>") - 1);
				if (jarHtml.toString().contains(".jar")) {
					name = jarHtml.substring(jarHtml.indexOf("\">") + 2, jarHtml.indexOf("</a>"));
				}
				jarList.add(name);
			}
			return jarList;
		} catch (IOException e) {
			String msg = "failure reading html message!";
			logger.error(msg);
			throw new RuntimeException(e);
		} finally {
			logger.debug("finish invoke NexusInfoUtil.parseHtmlBody.");
		}
	}

}
