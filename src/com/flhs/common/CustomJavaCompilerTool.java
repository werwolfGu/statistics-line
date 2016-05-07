package com.flhs.common;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class CustomJavaCompilerTool {
	
	public static String  jarLock = "lock";
	
	public static String libPath = "";
	
	public static boolean needJavaJar = true;
	
	private static  Iterable<String> options;
	
	private static CustomJavaCompilerTool instance;
	
	private static String extLibPath = Constant.TARGET_PROJ_JAR_PATH;
	
	private static String sourceDir = Constant.TARGET_PROJ_SOURCE_DIR;
	
	private static String targeteDir = Constant.TARGET_PROJ_COMPILER_DIR;

	private static DiagnosticCollector<JavaFileObject> diagnosticListener;
	
	private static List<String> compilterErrorInfoList = new ArrayList<String>();
	

	public static List<String> getCompilterErrorInfoList() {
		return compilterErrorInfoList;
	}

	public static void setCompilterErrorInfoList(List<String> compilterErrorInfoList) {
		CustomJavaCompilerTool.compilterErrorInfoList = compilterErrorInfoList;
	}
	
	static {
		
		if(needJavaJar){
			
			String osName = System.getProperty("os.name");
			
			//加载系统包
			String javaHome = System.getProperty("java.home");
			String baseJarPath = javaHome+File.separator+"lib";
			File baseJarFile = new File(baseJarPath);
			if(baseJarFile.exists() && baseJarFile.isDirectory()){
				File[] files = baseJarFile.listFiles();
				for(File jarFile:files){
					if(jarFile.isFile() && jarFile.getName().endsWith(".jar")){
						if(osName.indexOf("Windows")!=-1){
							libPath =libPath+";"+baseJarPath+File.separator+jarFile.getName();
						}else{
							libPath =libPath+":"+baseJarPath+File.separator+jarFile.getName();
						}
					}
				}
				if(libPath.startsWith(";")||libPath.startsWith(":")){
					libPath = libPath.substring(1);
				}
			}
			
			//加载目标工程jar包
			File extJarFile = new File(extLibPath);
			if(extJarFile.exists() && extJarFile.isDirectory()){
				File[] files = extJarFile.listFiles(new FileFilter(){
					@Override
					public boolean accept(File pathname) {
						// TODO Auto-generated method stub
						return  pathname.getName().endsWith(".jar");
					}
	            	
	            });
				
				for(File jarFile:files){
					
					if(osName.indexOf("Windows")!=-1){
						libPath =libPath+";"+extLibPath+File.separator+jarFile.getName();
					}else{
						libPath =libPath+":"+extLibPath+File.separator+jarFile.getName();
					}
					
				}
				

				if(libPath.startsWith(";")||libPath.startsWith(":")){
					libPath = libPath.substring(1);
				}
			}
		}
		File file = new File(targeteDir);
		if(!file.exists()){
			file.mkdirs();
		}else{
			options = Arrays.asList("-classpath",libPath,"-d",targeteDir,"-sourcepath",sourceDir);
		}
		
		diagnosticListener = new DiagnosticCollector<JavaFileObject>();
		
	}
	
	private CustomJavaCompilerTool(){
		
	}
	
	public synchronized static CustomJavaCompilerTool getInstatnce(){
		
		if(instance == null){
			instance = new CustomJavaCompilerTool();
		}
		return instance;
	}

	public static boolean complierJava(File... files) throws IOException{
		
	   JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
       StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

       Iterable<? extends JavaFileObject> compilationUnits =
           fileManager.getJavaFileObjectsFromFiles(Arrays.asList(files));
      
       boolean  flag = compiler.getTask(null, fileManager, diagnosticListener, options, null, compilationUnits).call();

       fileManager.close();
       recordErrorInfo();
       return flag;
	}
	
	public static void recordErrorInfo(){
		
		if(diagnosticListener != null && diagnosticListener.getDiagnostics().size() > 0){
			
			for (Diagnostic diagnostic : diagnosticListener.getDiagnostics()) {
				
				if("ERROR".equalsIgnoreCase(diagnostic.getKind().toString())){
					  String className = diagnostic.getSource().toString().replaceAll("\\\\", "/");
				      className  = className.substring(className.lastIndexOf("/") + 1, className.length() -1);
				      System.out.println("" + className + " 该类编译时出现错误，将不统计其行数！");
				      System.out.println("code:" + diagnostic.getCode());
				      System.out.println("kind:" + diagnostic.getKind());
//				      System.out.println("position:" + diagnostic.getPosition());
//				      System.out.println("start postition:" + diagnostic.getStartPosition());
//				      System.out.println("end position:" + diagnostic.getEndPosition());
//				      System.out.println("srouce:" + diagnostic.getSource());
				      System.out.println("message:" + diagnostic.getMessage(null));
					  compilterErrorInfoList.add(className);
				}
				
				  
			 }
		}
			
	}
	
	public static void loopDirFindJava(String dir ,List<File> list){
		File file = new File(dir);
		if(file.exists()){
			
			if(file.isFile() && file.getName().endsWith(".java")){
				list.add(file);
				return;
			}else if(file.isDirectory()){
				
				File[] files = file.listFiles();
				if(files != null && files.length > 0){
					
					for(File _file : files){
						
						loopDirFindJava(_file.getAbsolutePath(),list);
					}
				}
				
			}
			
		}else{
			return ;
		}
	}
	
	public static boolean isNeedJavaJar() {
		return needJavaJar;
	}

	public static void setNeedJavaJar(boolean needJavaJar) {
		CustomJavaCompilerTool.needJavaJar = needJavaJar;
	}

	public static String getExtLibPath() {
		return extLibPath;
	}

	public static void setExtLibPath(String extLibPath) {
		CustomJavaCompilerTool.extLibPath = extLibPath;
	}

	public static String getSourceDir() {
		return sourceDir;
	}

	public static void setSourceDir(String sourceDir) {
		CustomJavaCompilerTool.sourceDir = sourceDir;
	}

	public static String getTargeteDir() {
		return targeteDir;
	}

	public static void setTargeteDir(String targeteDir) {
		CustomJavaCompilerTool.targeteDir = targeteDir;
	}
	
	public static void main(String[] args) {
		
		File file = new File("D:\\doc\\workspace\\FLHS_TEST\\src\\com\\flhs\\Test.java");
//		boolean b = CustomJavaCompilerTool.complierJava(file);
		try {
			long s = System.currentTimeMillis();
			String str = "D:\\doc\\workspace\\FLHS_TEST";
			List<File> list = new ArrayList<File>();
			loopDirFindJava(str,list);
			File[] files = new File[list.size()];
			boolean b = CustomJavaCompilerTool.complierJava(list.toArray(files));
			 for (Diagnostic diagnostic : diagnosticListener.getDiagnostics()) {
				  System.out.println("code:" + diagnostic.getCode());
			      System.out.println("kind:" + diagnostic.getKind().toString());
			      System.out.println("position:" + diagnostic.getPosition());
			      System.out.println("start postition:" + diagnostic.getStartPosition());
			      System.out.println("end position:" + diagnostic.getEndPosition());
			      System.out.println("srouce:" + diagnostic.getSource().toString());
			      System.out.println("message:" + diagnostic.getMessage(null));
			      
			      String className = diagnostic.getSource().toString().replaceAll("\\\\", "/");
			      className  = className.substring(className.lastIndexOf("/") + 1, className.length() -1);
			      System.out.println("className :" + className);
			 }
			System.out.println("boolean :" + b + "  and cost time :" + (System.currentTimeMillis() -s) + ".ms");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
