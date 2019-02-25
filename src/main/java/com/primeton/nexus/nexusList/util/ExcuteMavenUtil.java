package com.primeton.nexus.nexusList.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ExcuteMavenUtil {
	
	@Value("${mvn.path}")
	private String localRepoPath;

	public String mavenCompile(String groupId) {
		String result = "编译失败!";
		return result;
		
	}

}
