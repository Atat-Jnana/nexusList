package com.primeton.nexus.nexusList.util;

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
	/**拼接jar包在本地仓库中的路径，获取jar包中pom.xml中信息
	 * @author angw@primeton.com
	 * @param artifact Artifact实例
	 * @param jarName jar包文件名
	 * @return 带有Jar包内容的List
	 */
	public List getJarInfo(Artifact artifact, String jarName) {
		List result = new ArrayList<>();
		Map jarInfo = new HashMap<>();
		// 拼接出类似上边这个路径
		String path = "jar:file:/" + localRepoPath.replace("\\", "/") + artifact.getGroupId().replace(".", "/") + "/"
				+ jarName + "!/META-INF/maven/" + artifact.getGroupId() + "/" + artifact.getArtifactId() + "/pom.xml";
		MavenXpp3Reader reader = new MavenXpp3Reader();
		try {
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
			return result;
		}
		return result;
	}
}
