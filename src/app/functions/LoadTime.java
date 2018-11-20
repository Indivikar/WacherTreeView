package app.functions;

import java.util.Date;

import app.controller.CTree;
import app.loadTime.LoadTime.LoadTimeOperation;

public class LoadTime {

	public static long start1;
	
	public static void Start() {
		start1 = new Date().getTime();
	}
	
	public static void Stop(String operation, String comment) {
		long runningTime1 = new Date().getTime() - start1;			
		CTree.listLoadTime.add(new LoadTimeOperation(operation, runningTime1 + "", comment));
	}
	
}
