package com.primeton.nexus.nexusList.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.primeton.nexus.nexusList.bean.Artifact;
import com.primeton.nexus.nexusList.util.ParseHtmlUtil;
import com.primeton.nexus.nexusList.util.ParseJarUtil;

/**
 * 对nexus进行操作的接口
 * 
 * @author angw@primeton.com
 *
 */
@RestController
@RequestMapping("/nexus")
public class NexusController {

	@Autowired
	private ParseHtmlUtil parseHtmlUtil;
	
	@Autowired
	private ParseJarUtil parseJarUtil;

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
	@PostMapping("/getSpecificArtifact")
	public List getSpecificArtifact(@RequestBody Artifact artifact){
		List result = new ArrayList<>();
		//先判断前端传来的字段是否完整
		if(artifact.getGroupId()==null||artifact.getArtifactId()==null||artifact.getVersionCode()==null||artifact.getRepositoryId()==null) {
			result.add("参数不完整！");
			return result;
		}
		//result中存储有jar包的文件名
		result = parseHtmlUtil.parseHtmlBody(artifact.getRepositoryId(), artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersionCode());
		//如果字段虽然完整，但其中有填错的信息
		if(result.get(0).toString().equals("找不到此版本信息!"))
			return result;
		result = parseJarUtil.getJarInfo(artifact, result.get(0).toString());
		return result;
		
	}
}
