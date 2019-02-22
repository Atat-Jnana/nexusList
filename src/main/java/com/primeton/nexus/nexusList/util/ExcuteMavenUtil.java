package com.primeton.nexus.nexusList.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ExcuteMavenUtil {
	
	@Value("${mvn.path}")
	private String localRepoPath;

	public void mavenInstall(String groupId, String artifactId, String nexusUrl) throws Exception {

		
	}

}
