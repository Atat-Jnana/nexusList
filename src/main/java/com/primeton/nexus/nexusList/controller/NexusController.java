package com.primeton.nexus.nexusList.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.primeton.nexus.nexusList.bean.Artifact;
import com.primeton.nexus.nexusList.util.ExcuteMavenUtil;
import com.primeton.nexus.nexusList.util.ParseHtmlUtil;
import com.primeton.nexus.nexusList.util.ParseJarUtil;

/**
 * 对nexus进行操作的接口
 * 
 * @author angw@primeton.com
 *
 */
@SuppressWarnings("all")
@RestController
@RequestMapping("/nexus")
public class NexusController {

	@Autowired
	private ParseHtmlUtil parseHtmlUtil;

	@Autowired
	private ParseJarUtil parseJarUtil;
	
	@Autowired
	private ExcuteMavenUtil excuteMavenUtil;

	/**
	 * @author angw@primmeton.com
	 * @param artifact 前端传来的artifact实体类
	 * @return List<String> repository中已有的扩展名称列表
	 */
	@PostMapping("/getAllFromRepository")
	public List<String> getAllFromRepository(@RequestBody Artifact artifact) {
		return parseHtmlUtil.parseHtmlBody(artifact.getRepositoryId());
	}

	/**
	 * 传入一个Artifact实体对象，通过其中的字段的详略去调用具体的查询方法
	 * 
	 * @author angw@primeton.com
	 * @param artifact 前端传来的artifact实体对象
	 * @return List<?>
	 */
	@PostMapping("/getByCondition")
	public List<?> getArtifactByCondition(@RequestBody Artifact artifact) {
		List<?> artifacts = null;
		// 通过repositoryId+groupId获取所有artifactId(通过)
		if (artifact.getRepositoryId() != null && artifact.getGroupId() != null && artifact.getArtifactId() == null
				&& artifact.getVersionCode() == null) {
			artifacts = parseHtmlUtil.parseHtmlBody(artifact.getRepositoryId(), artifact.getGroupId());
			return artifacts;
		}
		// repositoryId+groupId+artifactId获取所有version(通过)
		if (artifact.getRepositoryId() != null && artifact.getGroupId() != null && artifact.getArtifactId() != null
				&& artifact.getVersionCode() == null) {
			artifacts = parseHtmlUtil.parseHtmlBody(artifact.getRepositoryId(), artifact.getGroupId(),
					artifact.getArtifactId());
			return artifacts;
		}
		// repositoryId+groupId+artifactId+version获取具体jar包（通过）
		if (artifact.getRepositoryId() != null && artifact.getGroupId() != null && artifact.getArtifactId() != null
				&& artifact.getVersionCode() != null) {
			artifacts = parseHtmlUtil.parseHtmlBody(artifact.getRepositoryId(), artifact.getGroupId(),
					artifact.getArtifactId(), artifact.getVersionCode());
			return artifacts;
		}

		return artifacts;

	}

	/**
	 * 根据groupId+artifactId+version+repositoryId获取具体的jar包，对其中的扩展信息进行解析
	 * 
	 * @author angw@primeton.com
	 * @param artifact Artifact实例
	 * @return 携带扩展信息的List集合
	 */
	@PostMapping("/getSpecificArtifact")
	public List getSpecificArtifact(@RequestBody Artifact artifact) {
		List result = new ArrayList<>();
		// 先判断前端传来的字段是否完整
		if (artifact.getGroupId() == null || artifact.getArtifactId() == null || artifact.getVersionCode() == null
				|| artifact.getRepositoryId() == null) {
			result.add("参数不完整！");
			return result;
		}
		// result中存储有jar包的文件名
		result = parseHtmlUtil.parseHtmlBody(artifact.getRepositoryId(), artifact.getGroupId(),
				artifact.getArtifactId(), artifact.getVersionCode());
		// 如果字段虽然完整，但其中有填错的信息
		if (result.get(0).toString().equals("找不到此版本信息!"))
			return result;
		result = parseJarUtil.getJarInfo(artifact, result.get(0).toString());
		return result;

	}

	/**
	 * 1、给出一个pom.xml文件路径解析出所有dependency(只取groupId为com.gdrcu.gmf开头的jar包同时判断artifactId)
	 * 2、从步骤1中获取到的特定的jar包保存到本地（通过g,v,a从nexus去获取，下载到本地maven库中）
	 * 3、理想的下载方案，通过java调用maven的下载方式，让maven去nexus的仓库指定位置获取资源jar包
	 * 
	 * @author angw@primeton.com
	 * @param pomPath pomPath(给出的pom文件的路径)
	 * @return
	 */
	@PostMapping("/getDependency")
	public List getDependencyFromPom(@RequestParam String pomPath) {
		List<Object> jarInfo = parseJarUtil.getJarInfo(pomPath);
		return jarInfo;
	}

	/**
	 * 往总工程pom.xml下添加<moudle>
	 * 
	 * @author angw@primeton.com
	 * @param pomPath    pom文件的路径
	 * @param moduleName 所要添加的module名称
	 * @return String "添加失败！"(失败) "添加成功！"(成功)
	 */
	@PostMapping("/addMoudle")
	public String addModuleToPom(@RequestParam String pomPath, @RequestParam String moduleName) {
		String result = "添加失败!";
		result = parseJarUtil.addMoudle(pomPath, moduleName);
		return result;
	}
	

	/**
	 * 往指定pom.xml中添加<dependency>
	 * @author angw@primeton.com
	 * @param pom_artifact 携带groupId,artifactId,version,pom文件路径的HashMap
	 * @return String
	 */
	@PostMapping("/addDependency")
	public String addDependencyToPom(@RequestBody HashMap<Object, Object> pom_artifact) {
		String result = "添加失败！";
		result = parseJarUtil.addDependency(pom_artifact);
		return result;
	}
	/**
	 * 在给定路径的pom.xml中修改<dependency>的信息
	 * @author angw@primeton.com
	 * @param pom_artifact 携带groupId,artifactId,version,pom文件路径的HashMap
	 * @return String
	 */
	@PostMapping("/updateDependency")
	public String updateDependencyToPom(@RequestBody HashMap<Object, Object> pom_artifact) {
		String result = "修改失败!";
		result = parseJarUtil.updateDependency(pom_artifact);
		return result;
	}
	/**
	 * 通过给定的pom文件路径，运行maven命令对其进行编译
	 * @author angw@primeton.com
	 * @param pomPath pomPath
	 * @return String
	 */
	@PostMapping("/compilePom")
	public String excuteMavenCompile(@RequestParam String pomPath) {
		String result = "编译失败!";
		result = excuteMavenUtil.mavenCompile(pomPath);
		return result;
		
	}
}
