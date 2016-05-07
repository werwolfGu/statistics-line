package com.flhs.service;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.flhs.common.Constant;
import com.flhs.file.FileReader;
import com.flhs.manager.DistributeManager;

public class FileHandleThreadPlus implements Runnable {

	private FileReader reader ;
	
	private BlockingQueue<Map<String,Integer>> queueCounter ;
	
	private BlockingQueue<String> fileQueue;
	
	public FileHandleThreadPlus(BlockingQueue<Map<String,Integer>> queueCounter,BlockingQueue<String> fileQueue){
		this.queueCounter = queueCounter;
		this.fileQueue = fileQueue;
		reader = new FileReader();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		String tmpFilePath = null;
		
			while(true){
				
				if(DistributeManager.isScannerPlusEndFlag() && fileQueue.size() <= 0){
					
					break;
				}
				
//				System.out.println("时间：" +new Date() + " " + Thread.currentThread().getName());
				try {
					
					tmpFilePath = fileQueue.poll(1,TimeUnit.SECONDS);
					if(tmpFilePath != null){
						
						File _file = new File(tmpFilePath);
						if(_file.exists() && _file.isFile()){
							
							fileLineCount(_file);
							tmpFilePath = null;
						}
					}
					
					Thread.sleep(10);
				
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
	}
	
	
	public void fileLineCount(File _file){
		try {
			
			String fileName = _file.getName();
			//判断文件是否以   .c   ||   .cpp   || .java  结尾的
			if(fileName.endsWith(Constant.C_POSTFIX) || fileName.endsWith(Constant.C_PLUS_POSTFIX) 
					|| fileName.endsWith(Constant.JAVA_POSTFIX)){
				Map<String,Integer> map = reader.getFileLineCount(_file);
				System.out.println("处理完文件：" + fileName + "；行数统计："+ map);
				queueCounter.add(map);
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
