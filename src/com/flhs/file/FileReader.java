package com.flhs.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.flhs.common.Constant;

public class FileReader {

	public Map<String,Integer> getFileLineCount(File file) throws IOException{
		
		if(!file.exists()){
			return null;
		}
		
		Map<String,Integer> result = new HashMap<String,Integer>();
		int totalLine = 0;   //总行数
		int blankLine = 0;   //空格行数
		int validLine = 0;   //有效行数
		int annotationLine = 0;   //注释行数
		boolean asteriskAnnotationFlag = false;  //带星号的多行注释
		int asteriskLine = 0;
		
		try {
			
			InputStream input = new FileInputStream(file);
			
			if(input != null){
				
				BufferedReader br = new BufferedReader(new InputStreamReader(input));
				String tmpLine = null;
				while((tmpLine = br.readLine()) != null ){
					tmpLine = tmpLine.trim();    //过滤空格
					//总行数
					totalLine++;
					
					if(asteriskAnnotationFlag  && !tmpLine.contains("*/")){
						asteriskLine++;
						continue;
					}
					
					//空个判断
					if("".equals(tmpLine) || tmpLine.length() == 0){
						blankLine++;
						continue;
					}else if(tmpLine.startsWith("//")){                             // 注释判断
						annotationLine++;
						continue;
					}else if(tmpLine.startsWith("/*") && tmpLine.endsWith("*/")){   //星号注释判断
						annotationLine++;
						continue;
					}else if(tmpLine.startsWith("/*")){
						asteriskAnnotationFlag = true;
						asteriskLine++;
						continue;
					}else if(tmpLine.endsWith("*/")){
						asteriskLine++;
						annotationLine += asteriskLine;
						asteriskAnnotationFlag = false;
						asteriskLine = 0;
						continue;
					}else{                                                           //有效代码
						validLine++;
						
						if(tmpLine.contains("/*")){
							asteriskAnnotationFlag = true;
						}else if(tmpLine.contains("*/")){
							asteriskAnnotationFlag = false;
							annotationLine += asteriskLine;
							asteriskLine = 0;
						}
					}
					
				}
				
				result.put(Constant.totalLine, totalLine);
				result.put(Constant.annotationLine, annotationLine);
				result.put(Constant.blankLine, blankLine);
				result.put(Constant.validLine, validLine);
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static void main(String[] args) throws IOException {
		
		String str = "D:\\doc\\workspace\\FLHS_TEST\\src\\com\\flhs\\Test.java";
		File file = new File(str);
		FileReader fr = new FileReader();
		Map map = fr.getFileLineCount(file);
		System.out.println("map :" + map );
		if(str.startsWith("/*") && str.endsWith("*/")){
			System.out.println(str);
		}
		
	}
}
