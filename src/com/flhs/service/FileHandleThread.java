package com.flhs.service;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.flhs.common.Constant;
import com.flhs.common.CustomJavaCompilerTool;
import com.flhs.file.FileReader;
import com.flhs.manager.DistributeManager;

public class FileHandleThread implements Runnable {

	private String  filePath;
	
	private FileReader reader ;
	
	private boolean compilerFlag = false;
	
	private BlockingQueue<Map<String,Integer>> queueCounter ;
	
	public void init(String filePath ,BlockingQueue<Map<String,Integer>> queueCounter){
		this.filePath = filePath;
		this.queueCounter = queueCounter;
		reader = new FileReader();
		
		if("Y".equals(Constant.CHECK_CODE_COMPILER)){
			compilerFlag = true;
		}
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		File file = new File(filePath);
		if(file.exists()){
			
			if(file.isDirectory()){
				File[] files = file.listFiles();
				if(files != null && files.length >0){
					
					for(File _file :files){
						
						if(_file.isFile()){
							fileLineCount(_file);
							
						}
					}
				}
			}else if(file.isFile()){
				fileLineCount(file);
			}
			
		}
		
	}

	public void fileLineCount(File _file){
		try {
			
			String fileName = _file.getName();
			//�ж��ļ��Ƿ���   .c   ||   .cpp   || .java  ��β��
			if(fileName.endsWith(Constant.C_POSTFIX) || fileName.endsWith(Constant.C_PLUS_POSTFIX) 
					|| fileName.endsWith(Constant.JAVA_POSTFIX)){
				
				if(compilerFlag && fileName.endsWith(Constant.JAVA_POSTFIX)){
					//�������Ƿ�ͨ��
					if(!CustomJavaCompilerTool.getCompilterErrorInfoList().contains(_file.getName())){
						Map<String,Integer> map = reader.getFileLineCount(_file);
						System.out.println("�������ļ���" + fileName + "������ͳ�ƣ�"+ map);
						queueCounter.add(map);
					}else{
						System.out.println(fileName + " ���ļ�����ʱ����   ����ͳ����������");
					}
					
				}else{
					
					Map<String,Integer> map = reader.getFileLineCount(_file);
					System.out.println("�������ļ���" + fileName + "������ͳ�ƣ�"+ map);
					queueCounter.add(map);
				}
				
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public FileReader getReader() {
		return reader;
	}
	public void setReader(FileReader reader) {
		this.reader = reader;
	}
	
	
}
