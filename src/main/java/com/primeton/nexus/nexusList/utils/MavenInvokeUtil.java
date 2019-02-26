package com.primeton.nexus.nexusList.utils;

import java.io.File;
import java.util.Collections;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class MavenInvokeUtil {
	
	public static final String COMPILE = "compile";

	private Logger logger = LoggerFactory.getLogger(MavenInvokeUtil.class);
	
	@Value("${mvn.path}")
	private String localRepoPath;
	
	@Value("${mvn.home}")
	private String mavenHome;

	/**
	 *  对指定pom文件执行maven的compile命令
	 * @param filePath pom文件路径
	 * @return
	 */
	public int compile(String filePath) {
		logger.debug("start invoke MavenInvokeUtil.compile.");
		int result = execute(COMPILE, filePath);
		logger.debug("finish invoke MavenInvokeUtil.compile.");
		return result;
	}

	/**
	 *  对指定pom文件执行maven的命令
	 * @param commond maven命令名称
	 * @param filePath pom文件路径
	 * @return 非0即失败
	 */
	public int execute(String commond, String filePath) {
		logger.debug("start invoke MavenInvokeUtil.excute.");
		if(StringUtils.isBlank(filePath)) {
			String msg = "the maven file path is null!";
			logger.error(msg);
			throw new RuntimeException(msg);
		}
		InvocationRequest request = new DefaultInvocationRequest();
		request.setPomFile(new File(filePath));
		request.setGoals(Collections.singletonList(commond));
		DefaultInvoker invoker = new DefaultInvoker();
		invoker.setMavenHome(new File(mavenHome));
		try {
			InvocationResult result = invoker.execute(request);
			if(result != null && result.getExitCode() != 0) {
				String msg = "failure building!";
				logger.error(msg);
				throw new RuntimeException(result.getExecutionException());
			}
			return result.getExitCode();//正确执行必须走到该方法
		} catch (Exception e) {
			String msg = String.format("failure building with exception %s!", e.getMessage());
			logger.error(msg);
			throw new RuntimeException(e);
		} finally {
			logger.debug("finish invoke MavenInvokeUtil.excute.");
		}
	}
}
