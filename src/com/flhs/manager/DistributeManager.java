package com.flhs.manager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.flhs.common.Constant;
import com.flhs.common.CustomJavaCompilerTool;
import com.flhs.service.CounterThread;
import com.flhs.service.FileHandleThread;
import com.flhs.service.FileHandleThreadPlus;

public class DistributeManager {
	
	private BlockingQueue<Map<String,Integer>> queueCounter = new LinkedBlockingQueue<Map<String,Integer>>();
	
	private BlockingQueue<String> fileQueue = new LinkedBlockingQueue<String>();
	
	private static String  dispatchStrategy = Constant.THREAD_DISTRIBUTE_STRATEGY;
	
	private static int handleThreadNum = Integer.valueOf(Constant.FILE_HANDLE_THREAD_NUM);
	
	private static boolean scannerPlusEndFlag = false;
	
	private boolean  compilerFlag = false ;

	private String basePath;
	
	private static DistributeManager instance;
		
	private static ThreadGroup group = new ThreadGroup("group");   //ִ���̷߳���ͬһ���߳������
	

	private DistributeManager(){
		
	}
	
	public static synchronized DistributeManager getInstance(){
		
		if(instance == null){
			instance = new DistributeManager();
		}
		return instance;
	}

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}
	
	public void init(String basePath) throws IOException{
		
		
		
		this.basePath = basePath;
		
		if("Y".equals(Constant.CHECK_CODE_COMPILER)){
			
			System.out.println(">>>>>>>>>>>��ʼ����Ŀ�깤��<<<<<<<<<<<");
			long s = System.currentTimeMillis();
			List<File> list = new ArrayList<File>();
			CustomJavaCompilerTool.loopDirFindJava(basePath,list);
			boolean b = CustomJavaCompilerTool.complierJava(list.toArray(new File[list.size()]));
			System.out.println(">>>>>>>>>����Ŀ�깤�̻���ʱ�䣺" + (System.currentTimeMillis() - s) + " ms.");
			
			compilerFlag = true;
		}
		
		if("1".equals(dispatchStrategy)){
			//����ɨ���߳�
			System.out.println("��Ŀ¼�����������ļ������̣߳�");
			Scanner scanner = new Scanner();
			Thread th = new Thread(group,scanner);
			th.start();
			
		}else{
			
			System.out.println("�Զ����ļ������̸߳�����");
			//����ɨ���߳�
			ScannerPlus plus = new ScannerPlus();
			Thread th = new Thread(group,plus);
			th.start();
			
			//���������߳�
			for(int i = 0 ; i < handleThreadNum ; i++){
				FileHandleThreadPlus handlePlus = new FileHandleThreadPlus(queueCounter,fileQueue);
				Thread handleThread = new Thread(group,handlePlus);
				handleThread.setName("handle-plus->" + i);
				handleThread.start();
			}
			
			
		}
		
		//����ͳ���߳�
		CounterThread counter = new CounterThread(queueCounter);
		Thread cTh = new Thread(counter);
		cTh.start();
		
	}
	
	/**
	 * ɨ���߳�     ����Ŀ¼ɨ�����Ŀ¼�µ���Ŀ¼  ͬʱ��ÿ����Ŀ¼��������Ŀ¼�µ��ļ������߳�
	 * @author gucheng_en
	 *
	 */
	class Scanner implements Runnable{

		@Override
		public void run() {
			
			if(basePath != null && !"".equals(basePath)){
				
				//��ÿ��Ŀ¼������һ���ļ������߳�   ʹ�õݹ鷽�������߳�
				File file = new File(basePath);
				
				if(file.exists()){
					if(file.isDirectory()){
						createFileHandleThread(basePath);
					}else if(file.isFile()){
						startupFileHandleThread(basePath);
					}
				}
				
				
			}
		}
		
	}
	
	
	/**
	 * ʹ�õݹ鴴��Ŀ¼�Ĵ����߳�
	 * @param dirPath
	 */
	public void createFileHandleThread(String dirPath){
		
		File filePath = new File(dirPath);
		if(!filePath.exists()){
			return ;
		}
		if(filePath.isFile()){
			
			return;
		}else if(filePath.isDirectory()){
			//��������Ŀ¼�ļ������߳�
			startupFileHandleThread(dirPath);
			
			File[] files = filePath.listFiles();

			if( files != null && files.length > 0 ){
				
				for(File tmpFile :files){
					
					if(tmpFile.isDirectory()){
						//���Ҹ�tmpFileĿ¼�»���û��Ŀ¼
						createFileHandleThread(tmpFile.getAbsolutePath());
					}else{
						continue;
					}
				}
				
			}else{
				return ;
			}
		}
	}
	
	/**
	 * ���������߳�   ͬʱ�������̷߳���ͬһ���߳�����
	 * @param dirPath
	 */
	public void startupFileHandleThread(String dirPath){
		
		FileHandleThread handle = new FileHandleThread();
		handle.init(dirPath,queueCounter);
		Thread th = new Thread(group,handle);
		th.start();
	}
	
	
	class ScannerPlus implements Runnable{

		@Override
		public void run() {

			try{
				if(basePath != null && !"".equals(basePath)){
					obtainFilePath(basePath);
				}
				scannerPlusEndFlag  = true;
			}catch(Exception ex){
				
			}
			
		}
		
	}
	
	public void obtainFilePath(String basePath) throws IOException{
		File file = new File(basePath);
		
		if(!file.exists()){
			return ;
		}
		
		if(file.isFile()){
			
			String fileName = file.getAbsolutePath();
			if(fileName.endsWith(Constant.C_POSTFIX) || fileName.endsWith(Constant.C_PLUS_POSTFIX) 
					|| fileName.endsWith(Constant.JAVA_POSTFIX)){
				
				if(compilerFlag && fileName.endsWith(Constant.JAVA_POSTFIX)){
					//�������Ƿ�ͨ��
					if(!CustomJavaCompilerTool.getCompilterErrorInfoList().contains(file.getName())){
						fileQueue.add(file.getAbsolutePath());
					}else{
						System.out.println(file.getAbsolutePath() + " ���ļ�����ʱ����   ����ͳ����������");
					}
					
				}else{
					
					fileQueue.add(file.getAbsolutePath());
				}
				
			}
			
			return;
		}else if(file.isDirectory()){
			
			File[] files = file.listFiles();
			if(files != null && files.length > 0){
				
				for(File _file:files){
					
					obtainFilePath(_file.getAbsolutePath());
				}
			}
			
		}
	}
	
	public static ThreadGroup getGroup() {
		return group;
	}

	public static void setGroup(ThreadGroup group) {
		DistributeManager.group = group;
	}
	
	public static boolean isScannerPlusEndFlag() {
		return scannerPlusEndFlag;
	}

	public static void setScannerPlusEndFlag(boolean scannerPlusEndFlag) {
		DistributeManager.scannerPlusEndFlag = scannerPlusEndFlag;
	}
	
	public BlockingQueue<String> getFileQueue() {
		return fileQueue;
	}

	public void setFileQueue(BlockingQueue<String> fileQueue) {
		this.fileQueue = fileQueue;
	}
	
	
}
