package com.primeton.nexus.nexusList.util;

import java.io.File;
import java.util.Collections;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ExcuteMavenUtil {
	
	@Value("${mvn.path}")
	private String localRepoPath;
	
	@Value("${mvn.home}")
	private String mavenHome;

	public String mavenCompile(String pathname) {
		String result = "编译失败!";
		InvocationRequest request = new DefaultInvocationRequest();
		request.setPomFile(new File(pathname));
		request.setGoals(Collections.singletonList("compile"));
		
		
		DefaultInvoker invoker = new DefaultInvoker();
		invoker.setMavenHome(new File(mavenHome));
		try {
			invoker.execute(request);
			result = "编译成功";
		} catch (Exception e) {
			result = "编译失败";
			e.printStackTrace();
		}
		return result;
		
	}

}
