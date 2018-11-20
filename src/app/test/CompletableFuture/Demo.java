package app.test.CompletableFuture;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class Demo {

	public static void main(String[] args) {
		System.out.println("Demo");
		ExecutorService executor = Executors.newFixedThreadPool(5);
		
		
		FirstLineService firstLineService = new FirstLineService();
		SecondLineService secondLineService = new SecondLineService();
		
		Thread firstLineThread = new Thread(firstLineService);
		Thread secondLineThread = new Thread(secondLineService);
		

//        executor.execute(firstLineThread);
//        executor.execute(secondLineThread);

        executor.submit(firstLineService);
        executor.submit(secondLineService);
        
//        firstLineService.start();
//        secondLineService.start();
        
        while (!executor.isTerminated()) {
        	
        }
        System.out.println("Finished all threads");
		
	}

	private static class FirstLineService extends Task<Void> {

		
		
		@Override
		protected Void call() throws Exception {
					System.out.println("Start first");
			                   try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                   System.out.println("First ende");
			return null;
		}
    }
	
	private static class SecondLineService extends Task<Void> {
		
		
		@Override
		protected Void call() throws Exception {	
					System.out.println("Start Second");
			        try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                   System.out.println("Second ende");
			return null;
		}
    }
	
}
