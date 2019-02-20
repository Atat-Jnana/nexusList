package com.primeton.nexus.nexusList.bean;

import java.util.List;
import java.util.Map;

/**
 * artifact的实体类，存放扩展信息
 * 
 * @author angw@primeton.com
 *
 */
public class Artifact {

	private String groupId;

	private String artifactId;

	private String versionCode;

	private List<String> versionList;

	private Map<String, String> artifactPaths;

	private String repositoryId;

	private String repositoryGroupId;

	private String artifactDomain;

	private String artifactApplication;

	private String artifactExtension;

	public Artifact() {
	}

	public Artifact(String groupId, String artifactId, String repositoryId) {
		super();
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.repositoryId = repositoryId;
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

	public List<String> getVersionList() {
		return versionList;
	}

	public void setVersionList(List<String> versionList) {
		this.versionList = versionList;
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

	public Map<String, String> getArtifactPaths() {
		return artifactPaths;
	}

	public void setArtifactPaths(Map<String, String> artifactPaths) {
		this.artifactPaths = artifactPaths;
	}

	public String getArtifactDomain() {
		return artifactDomain;
	}

	public void setArtifactDomain(String artifactDomain) {
		this.artifactDomain = artifactDomain;
	}

	public String getArtifactApplication() {
		return artifactApplication;
	}

	public void setArtifactApplication(String artifactApplication) {
		this.artifactApplication = artifactApplication;
	}

	public String getArtifactExtension() {
		return artifactExtension;
	}

	public void setArtifactExtension(String artifactExtension) {
		this.artifactExtension = artifactExtension;
	}

	public String getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}

	@Override
	public String toString() {
		return "Artifact [groupId=" + groupId + ", artifactId=" + artifactId + ", versionCode=" + versionCode
				+ ", versionList=" + versionList + ", artifactPaths=" + artifactPaths + ", repositoryId=" + repositoryId
				+ ", repositoryGroupId=" + repositoryGroupId + ", artifactDomain=" + artifactDomain
				+ ", artifactApplication=" + artifactApplication + ", artifactExtension=" + artifactExtension + "]";
	}

}
