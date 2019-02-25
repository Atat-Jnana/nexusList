package com.primeton.nexus.nexusList.bean;

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
	 * 扩展所在库的Id
	 */
	private String repositoryId;
	/**
	 * 扩展的描述性息
	 */
	private String description;

	public Artifact() {
	}

	public Artifact(String groupId, String artifactId, String versionCode, String repositoryId, String description) {
		super();
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.versionCode = versionCode;
		this.repositoryId = repositoryId;
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

	public String getRepositoryId() {
		return repositoryId;
	}

	public void setRepositoryId(String repositoryId) {
		this.repositoryId = repositoryId;
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
				+ ", repositoryId=" + repositoryId + ", description=" + description + "]";
	}
	

}