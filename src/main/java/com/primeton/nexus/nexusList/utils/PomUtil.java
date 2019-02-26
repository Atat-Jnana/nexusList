package com.primeton.nexus.nexusList.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 通过maven model去解析jar包中的pom.xml文件来获取配置信息
 * 
 * @author angw@primeton.com
 *
 */
@Component
public class PomUtil {
	
	private Logger logger = LoggerFactory.getLogger(PomUtil.class);
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
	 * 根据pom文件路径，解析pom中的依赖列表。
	 * @param pomFilePath
	 * @return
	 */
	public List<Dependency> getAllDependenciesFromPom(String pomFilePath) {
		logger.debug("start invoke PomUtil.getAllDependenciesFromPom");
		if(StringUtils.isBlank(pomFilePath)) {
			String msg = "the pom file path is null!";
			logger.error(msg);
			throw new RuntimeException(msg);  
		}
		
		MavenXpp3Reader reader = null;
		
		List<Dependency> result = new ArrayList<>();
		InputStream fis = null;
		try {

			reader = new MavenXpp3Reader();

			fis = new FileInputStream(pomFilePath.replace("\\", "/"));

			Model model = reader.read(fis);
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
			String msg = String.format("File Not Found %s!", e.getMessage());
			logger.error(msg);
		} finally {
			IOUtils.closeQuietly(fis);
			logger.debug("finish invoke PomUtil.getAllDependenciesFromPom");
		}

		return result;
	}

	/**
	 * 在总工程下的pom.xml文件中添加module
	 * 
	 * @author angw@primeton.com
	 * @param pomFilePath    pom文件路径
	 * @param moduleName 所添加Moudle的名称
	 * @return int   1:找不到文件   2:pom中已存在该module  0:添加成功
	 */
	public int addModuleToPom(String pomFilePath, String moduleName) {
		logger.debug("start invoke PomUtil.addModuleToPom");
		int result = 0;
		if(StringUtils.isBlank(pomFilePath)) {
			String msg = "the pom file path is null!";
			logger.error(msg);
			throw new RuntimeException(msg);  
		}
		if(StringUtils.isBlank(moduleName)) {
			String msg = "the module name is null!";
			logger.error(msg);
			throw new RuntimeException(msg);  
		}
		
		FileWriter pomFile = null;
		FileInputStream fis = null;
		
		MavenXpp3Reader reader = null;
		MavenXpp3Writer writer = null;
		
		try {
			reader = new MavenXpp3Reader();
			writer = new MavenXpp3Writer();
			fis = new FileInputStream(pomFilePath.replace("\\", "/"));
			Model model = reader.read(fis);
			List<String> modules = model.getModules();
			if (modules.contains(moduleName)) {
				logger.info("module already exists in pom!");
				return 2;
			}
			modules.add(moduleName);
			model.setModules(modules);
			pomFile = new FileWriter(pomFilePath.replace("\\", "/"));
			writer.write(pomFile, model);

		} catch (Exception e) {
			String msg = String.format("File Not Found %s!", e.getMessage());
			logger.error(msg);
			result = 1;
		} finally {
			IOUtils.closeQuietly(pomFile);
			IOUtils.closeQuietly(fis);
			logger.debug("finish invoke PomUtil.addModuleToPom");
		}
		return result;
	}

	/**
	 * 往boot工程添加<dependency>
	 * 
	 * @author angw@primeton.com
	 * @param param 携带g a v 和pom文件路径的的HashMap
	 * @return int   1:找不到文件   2:pom中已存在该module  0:添加成功  3:其他错误
	 */
	public int addDependencyToPom(String pomPath,String groupId,String artifactId,String versionCode) {
		logger.debug("start invoke PomUtil.addDependencyToPom");
		int result = 0;
		FileWriter pomFile = null;
		FileInputStream fis = null;
		
		MavenXpp3Reader reader = null;
		MavenXpp3Writer writer = null;
		
		try {
			reader = new MavenXpp3Reader();
			writer = new MavenXpp3Writer();

			fis = new FileInputStream(pomPath.replace("\\", "/"));
			Model model = reader.read(fis);
			List<Dependency> dependencies = model.getDependencies();
			// 先检索pom.xml中是否已经存在这一个依赖,如果存在，返回提示信息
			for (Dependency dependency : dependencies) {
				if (dependency.getGroupId().equals(groupId)) {
					return 2;
				}
			}

			Dependency depen = new Dependency();
			depen.setGroupId(groupId);
			depen.setArtifactId(artifactId);
			depen.setVersion(versionCode);
			dependencies.add(depen);

			model.setDependencies(dependencies);

			pomFile = new FileWriter(pomPath.replace("\\", "/"));
			writer.write(pomFile, model);

		} catch (FileNotFoundException e) {
			String msg = String.format("File Not Found %s!", e.getMessage());
			logger.error(msg);
			result = 1;
		} catch (Exception e) {
			e.printStackTrace();
			result = 3;
		} finally {
			IOUtils.closeQuietly(pomFile);
			IOUtils.closeQuietly(fis);
			logger.debug("finish invoke PomUtil.addDependencyToPom");
		}
		return result;
	}

	/**
	 * 在指定pom文件中找到并修改对应依赖的a,v
	 * 
	 * @author angw@primmeton.com
	 * @param param param 携带g a v 和pom文件路径的的HashMap
	 * @return int   1:找不到文件   2:pom中不包含此dependency  0:添加成功  3:其他错误
	 */
	public int updateDependencyFromPom(String pomPath,String groupId,String artifactId,String versionCode) {
		logger.debug("start invoke PomUtil.updateDependencyFromPom");
		int result = 0;
		boolean containDependency = false;
		FileWriter pomFile = null;
		FileInputStream fis = null;
		
		MavenXpp3Reader reader = null;
		MavenXpp3Writer writer = null;
		
		try {
			reader = new MavenXpp3Reader();
			writer = new MavenXpp3Writer();
			fis = new FileInputStream(pomPath.replace("\\", "/"));
			Model model = reader.read(fis);
			List<Dependency> dependencies = model.getDependencies();
			// 在pom文件中搜索要修改的那一条<dependency>
			for (Dependency dependency : dependencies) {
				if (dependency.getGroupId().equals(groupId.toString())) {
					containDependency = true;
					dependency.setArtifactId(artifactId.toString());
					dependency.setVersion(versionCode.toString());
				}
			}

			if (containDependency == false) {
				result = 2;
				return result;
			}
			model.setDependencies(dependencies);

			pomFile = new FileWriter(pomPath.replace("\\", "/"));
			writer.write(pomFile, model);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			result = 1;
		} catch (Exception e) {
			e.printStackTrace();
			result = 3;
		} finally {
			IOUtils.closeQuietly(pomFile);
			IOUtils.closeQuietly(fis);
			logger.debug("finish invoke PomUtil.updateDependencyFromPom");
		}
		return result;
	}

}