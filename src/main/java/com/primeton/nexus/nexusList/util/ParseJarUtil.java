package com.primeton.nexus.nexusList.util;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
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
	 * 用于写入内容到pom.xml的对象
	 */
	private MavenXpp3Writer writer = null;

	public List<Object> getJarInfo(String pomPath) {
		List<Object> result = new ArrayList<>();
		InputStream openStream = null;
		try {

			reader = new MavenXpp3Reader();
			URL url = new URL("file:///" + pomPath.replace("\\", "/"));
			openStream = url.openStream();
			Model model = reader.read(openStream);
			// 获取pom中包含依赖的List集合
			List<Dependency> dependencies = model.getDependencies();
			DependencyManagement dependencyManagement = model.getDependencyManagement();
			// 没有<dependency>标签的情况下，去<dependencyManagement>标签下查找依赖
			if (dependencies.size() == 0) {
				dependencies = dependencyManagement.getDependencies();
			}
			// 将相关的依赖放入result集合中作为结果返回（按照名称排除第三方的jar包）
			for (Dependency dependency : dependencies) {
				if (dependency.getGroupId().contains("spring")) {
					result.add(dependency);
				}
			}
			
		} catch (Exception e) {
			result.add("系統找不到指定文件！");
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(openStream);
		}

		return result;
	}

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
		Map<Object, Object> jarInfo = new HashMap<>();
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

	/**
	 * 在总工程下的pom.xml文件中添加module
	 * 
	 * @author angw@primeton.com
	 * @param pomPath    pom文件路径
	 * @param moudleName 所添加Moudle的名称
	 * @return
	 */
	public String addMoudle(String pomPath, String moudleName) {
		String result = "添加成功!";
		FileWriter pomFile = null;
		try {
			reader = new MavenXpp3Reader();
			writer = new MavenXpp3Writer();
			URL url = new URL("file:///" + pomPath.replace("\\", "/"));
			Model model = reader.read(url.openStream());
			List<String> modules = model.getModules();
			if (modules.contains(moudleName)) {
				return "已存在名为：" + moudleName + "的module！";
			}
			modules.add(moudleName);
			model.setModules(modules);
			pomFile = new FileWriter(pomPath.replace("\\", "/"));
			writer.write(pomFile, model);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			result = "系统找不到指定路径！";
		} catch (Exception e) {
			e.printStackTrace();
			result = "添加失败";
		} finally {
			IOUtils.closeQuietly(pomFile);
		}
		return "result";
	}

	/**
	 * 往boot工程添加<dependency>
	 * 
	 * @author angw@primeton.com
	 * @param param 携带g a v 和pom文件路径的的HashMap
	 * @return
	 */
	public String addDependency(HashMap<Object, Object> param) {
		String result = "添加成功!";
		FileWriter pomFile = null;
		try {
			reader = new MavenXpp3Reader();
			writer = new MavenXpp3Writer();

			URL url = new URL("file:///" + param.get("pomPath").toString().replace("\\", "/"));
			Model model = reader.read(url.openStream());
			List<Dependency> dependencies = model.getDependencies();
			// 先检索pom.xml中是否已经存在这一个依赖,如果存在，返回提示信息
			for (Dependency dependency : dependencies) {
				if (dependency.getGroupId().equals(param.get("groupId").toString())) {
					return "已存在groupId为：" + dependency.getGroupId() + "的依赖！";
				}
			}

			Dependency depen = new Dependency();
			depen.setGroupId(param.get("groupId").toString());
			depen.setArtifactId(param.get("artifactId").toString());
			depen.setVersion(param.get("versionCode").toString());
			dependencies.add(depen);

			model.setDependencies(dependencies);

			pomFile = new FileWriter(param.get("pomPath").toString().replace("\\", "/"));
			writer.write(pomFile, model);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			result = "系统找不到指定路径";
		} catch (Exception e) {
			e.printStackTrace();
			result = "添加失败";
		} finally {
			IOUtils.closeQuietly(pomFile);
		}
		return result;
	}
	/**
	 * 在指定pom文件中找到并修改对应依赖的a,v
	 * @author angw@primmeton.com
	 * @param param param 携带g a v 和pom文件路径的的HashMap
	 * @return String
	 */
	public String updateDependency(HashMap<Object, Object> param) {
		String result = "修改成功!";
		boolean containDependency = false;
		FileWriter pomFile = null;
		try {
			reader = new MavenXpp3Reader();
			writer = new MavenXpp3Writer();

			URL url = new URL("file:///" + param.get("pomPath").toString().replace("\\", "/"));
			Model model = reader.read(url.openStream());
			List<Dependency> dependencies = model.getDependencies();
			//在pom文件中搜索要修改的那一条<dependency>
			for (Dependency dependency : dependencies) {
				if(dependency.getGroupId().equals(param.get("groupId").toString())) {
					containDependency = true;
					dependency.setArtifactId(param.get("artifactId").toString());
					dependency.setVersion(param.get("versionCode").toString());
				}
			}
			if(containDependency==false) {
				result = "pom中不包含所需的dependency！";
				return result;
			}
			model.setDependencies(dependencies);

			pomFile = new FileWriter(param.get("pomPath").toString().replace("\\", "/"));
			writer.write(pomFile, model);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			result = "系统找不到指定路径";
		} catch (Exception e) {
			e.printStackTrace();
			result = "修改失败"; 
		} finally {
			IOUtils.closeQuietly(pomFile);
		}
		return result;
	}
}