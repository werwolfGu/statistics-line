package com.flhs.common;

import java.util.ResourceBundle;

public class Constant {

	private static ResourceBundle res = ResourceBundle
			.getBundle("config.flhs");
	
	
	public final static String C_POSTFIX = ".c";                       //   .c     程序
	public final static String C_PLUS_POSTFIX = ".cpp";				   //   .cpp   程序
	public final static String JAVA_POSTFIX = ".java";				   //   .java  程序
	
	public final static String totalLine = "TOTAL_LINE";               //  物理总行数
	public final static String blankLine = "BLANK_LINE";               //  空行数
	public final static String annotationLine = "ANNOTATION_LINE";     //  注释行数
	public final static String validLine = "VALID_LINE";               //  有效行数
	
	
	
	public final static String THREAD_DISTRIBUTE_STRATEGY = res.getString("thread_distribute_strategy");
	public final static String FILE_HANDLE_THREAD_NUM = res.getString("file_handle_thread_num");
	public final static String CHECK_CODE_COMPILER = res.getString("check_code_compiler");
	public final static String TARGET_PROJ_JAR_PATH = res.getString("target_proj_jar_path");
	public final static String TARGET_PROJ_SOURCE_DIR = res.getString("target_proj_source_dir");
	public final static String TARGET_PROJ_COMPILER_DIR = res.getString("target_proj_compiler_dir");
	
	public static String getString(String key){
		String value = null;
		if(key != null){
			value = res.getString(key);
		}else{
			System.out.println("key is null!");
		}
		
		return value;
	}
}
