package com.primeton.nexus.nexusList.bean;

import java.util.List;

/**
 * artifact的实体类，存放扩展信息
 * 
 * @author angw@primeton.com
 *
 */
public class Artifact {
	/**
	 * 扩展的groupId
	 */
	private String groupId;
	/**
	 * 扩展的artifactId
	 */
	private String artifactId;
	/**
	 * 扩展的版本号version
	 */
	private String versionCode;

	/**
	 * 扩展在本地储存的路径
	 */
	private String artifactPaths;
	/**
	 * 扩展所在库的Id
	 */
	private String repositoryId;
	/**
	 * 扩展所在库的租Id
	 */
	private String repositoryGroupId;
	/**
	 * 扩展中所包含的依赖，集合
	 */
	private List<?> dependencies;
	/**
	 * 扩展的描述性息
	 */
	private String description;

	public Artifact() {
	}

	public Artifact(String groupId, String artifactId, String versionCode, String artifactPaths, String repositoryId,
			String repositoryGroupId, List<?> dependencies, String description) {
		super();
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.versionCode = versionCode;
		this.artifactPaths = artifactPaths;
		this.repositoryId = repositoryId;
		this.repositoryGroupId = repositoryGroupId;
		this.dependencies = dependencies;
		this.description = description;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	public String getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}

	public String getArtifactPaths() {
		return artifactPaths;
	}

	public void setArtifactPaths(String artifactPaths) {
		this.artifactPaths = artifactPaths;
	}

	public String getRepositoryId() {
		return repositoryId;
	}

	public void setRepositoryId(String repositoryId) {
		this.repositoryId = repositoryId;
	}

	public String getRepositoryGroupId() {
		return repositoryGroupId;
	}

	public void setRepositoryGroupId(String repositoryGroupId) {
		this.repositoryGroupId = repositoryGroupId;
	}

	public List<?> getDependencies() {
		return dependencies;
	}

	public void setDependencies(List<?> dependencies) {
		this.dependencies = dependencies;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "Artifact [groupId=" + groupId + ", artifactId=" + artifactId + ", versionCode=" + versionCode
				+ ", artifactPaths=" + artifactPaths + ", repositoryId=" + repositoryId + ", repositoryGroupId="
				+ repositoryGroupId + ", dependencies=" + dependencies + ", description=" + description + "]";
	}

}
