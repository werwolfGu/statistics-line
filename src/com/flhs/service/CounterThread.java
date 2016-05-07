package com.flhs.service;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.flhs.common.Constant;
import com.flhs.manager.DistributeManager;

public class CounterThread implements Runnable {

	private BlockingQueue<Map<String,Integer>> queueCounter ;

	private int totalLine = 0;
	private int blankLine = 0;
	private int validLine = 0;
	private int annotationLine = 0;
	
	public CounterThread(BlockingQueue<Map<String,Integer>> queueCounter){
		this.queueCounter = queueCounter;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		/*�߳������Ƿ����̴߳��*/
		while(DistributeManager.getGroup().activeCount() > 0){
			
			try {
				
//				System.out.println("ִ���߳�δ�����꣡");
				Map<String, Integer> map  = queueCounter.poll(5,TimeUnit.SECONDS);
				if(map != null && map.size() > 0){
					countLines(map);
				}
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		/*ͳ�ƶ�����ʣ��δͳ�Ƶ��ļ�*/
		while(queueCounter.size() > 0){
			
			try {
				 Map<String, Integer> map = queueCounter.poll(5,TimeUnit.SECONDS);
				 countLines(map);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			  
		}
		
		printProjLines();
	}
	
	
	public void countLines(Map<String, Integer> map){
		if(map != null && map.size() > 0){
			
			for(Map.Entry<String, Integer> entry : map.entrySet()){
				
				if(Constant.totalLine.equals(entry.getKey())){
					totalLine += entry.getValue();
					continue;
				}
				if(Constant.blankLine.equals(entry.getKey())){
					blankLine +=entry.getValue();
					continue;
				}
				
				if(Constant.validLine.equals(entry.getKey())){
					validLine += entry.getValue();
					continue;
				}
				if(Constant.annotationLine.equals(entry.getKey())){
					annotationLine += entry.getValue();
					continue;
				}
			}
		}
	}
	public void printProjLines(){
		System.out.println(">>>>>>>>>ͳ�Ƴ���������ϸ��");
		System.out.println("����������     : " + totalLine);
		System.out.println("��������          : " + blankLine);
		System.out.println("��Ч�������� : " + validLine);
		System.out.println("ע���������� : " + annotationLine);
	}
	
}
