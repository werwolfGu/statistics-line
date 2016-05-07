package com.flhs;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.locks.ReentrantLock;

public class Test {

	private int a;
	
//	private Map map = new HashMap();/*
	public void init(){
		System.out.println("a"+a );
	}
	/*
	 * 
	 /*   /*
	 *   
	 * 
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		String javaHome = System.getProperty("java.home") + File.separator + "lib";
		File file = new File(javaHome);
		if(file.exists() && file.isDirectory()){
			File[] files = file.listFiles(new FileFilter(){/**
			/*hjkjnjk
			hjnjk
			
			
				*/@Override
				public boolean accept(File pathname) {
					// TODO Auto-generated method stub
					return pathname.getName().endsWith(".jar");
				}
				
			});
			
			System.out.println("length:" + files.length);
			for(File _file: files){
				System.out.println("fileanme:" + _file.getName());
			}
		}
	}
}
