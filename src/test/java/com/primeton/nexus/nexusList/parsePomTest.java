package com.primeton.nexus.nexusList;

import java.net.URL;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

public class parsePomTest {
	public static void main(String[] args) {
		String pomPath = "C:\\Maven\\apache-maven-3.3.9-bin\\apache-maven-3.3.9\\repository\\com\\primeton\\RelationshipManagement\\META-INF\\maven\\com.primeton.RelationshipManagement\\RelationshipManagement\\pom.xml";
		String jarPath = "jar:file:/C:/Maven/apache-maven-3.3.9-bin/apache-maven-3.3.9/repository/com/primeton/RelationshipManagement/RelationshipManagement-1.0.0.jar!/META-INF/maven/com.primeton.RelationshipManagement/RelationshipManagement/pom.xml";
		MavenXpp3Reader reader = new MavenXpp3Reader();
		try {
			URL url = new URL(jarPath);
			Model model = reader.read(url.openStream());
			
			System.out.println("name:" + model.getName());
			System.out.println("groupId:" + model.getGroupId());
			System.out.println("artifactId:" + model.getArtifactId());
			System.out.println("version:" + model.getVersion());
			System.out.println("url:" + model.getUrl());
			System.out.println("repositories:" + model.getRepositories());
			System.out.println("dependencies:" + model.getDependencies());
			System.out.println("description:" + model.getDescription());
			System.out.println("id:" + model.getId());
			System.out.println("encoding:" + model.getModelEncoding());
			System.out.println("packaging:" + model.getPackaging());
			System.out.println("reports:" + model.getReports());
			System.out.println("ChildInheritAppendPath:" + model.getChildInheritAppendPath());
			System.out.println("InceptionYear:" + model.getInceptionYear());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
