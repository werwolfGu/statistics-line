package com.flhs;

import java.io.IOException;
import java.util.Scanner;

import com.flhs.manager.DistributeManager;

public class ServerMain {

	private static Scanner input;

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.out.println("请输入统计目标工程路径：");
		input = new Scanner(System.in);
		String basePath = input.next();
		System.out.println("basePath:" + basePath);
		DistributeManager manager = DistributeManager.getInstance();
		try {
			manager.init(basePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
