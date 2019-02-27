package com.primeton.nexus.nexusList.utils;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.primeton.nexus.nexusList.bean.Artifact;

/**
 * 通过maven model去解析jar包中的pom.xml文件来获取配置信息
 * 
 * @author angw@primeton.com
 *
 */
@Component
public class ParseJarUtil {
	/**
	 * 本地maven库的路径
	 */
	@Value("${mvn.path}")
	private String localRepoPath;
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
	 * 用于解析pom文件的一个 实例对象
	 */
	private MavenXpp3Reader reader = null;

	

	/**
	 * 拼接jar包在本地仓库中的路径，获取jar包中pom.xml中信息
	 * 
	 * @author angw@primeton.com
	 * @param artifact Artifact实例
	 * @param jarName  jar包文件名
	 * @return 带有Jar包内容的List
	 */
	public List<Object> getJarInfo(Artifact artifact, String jarName) {
		List<Object> result = new ArrayList<>();
		Map<Object, Object> jarInfo = new HashMap<Object, Object>();
		// 拼接出jar文件中pom.xml文件的路径
		String path = "jar:file:/" + localRepoPath.replace("\\", "/") + artifact.getGroupId().replace(".", "/") + "/"
				+ jarName + "!/META-INF/maven/" + artifact.getGroupId() + "/" + artifact.getArtifactId() + "/pom.xml";
		try {
			reader = new MavenXpp3Reader();
			URL url = new URL(path);
			Model model = reader.read(url.openStream());
			jarInfo.put("name", model.getName());
			jarInfo.put("groupId", model.getGroupId());
			jarInfo.put("artifactId", model.getArtifactId());
			jarInfo.put("version", model.getVersion());
			jarInfo.put("description", model.getDescription());
			jarInfo.put("dependencies", model.getDependencies());
			jarInfo.put("parents", model.getParent());
			jarInfo.put("packaging", model.getPackaging());
			jarInfo.put("pluginRepositories", model.getPluginRepositories());
			result.add(jarInfo);

		} catch (Exception e) {
			result.add("jar包不完整！");
		}
		return result;
	}

	
}