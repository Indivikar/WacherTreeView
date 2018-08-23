package app.TreeViewWatchService;

import java.io.File;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class ServiceRegister extends Task<Void>{

	private File directory;
	
	public ServiceRegister(File directory) {
		this.directory = directory;
	}

	@Override
	protected void succeeded() {
		System.out.println("task ende: " + directory);
	}
	
	@Override
	protected void cancelled() {

	}
	
	@Override
	protected void failed() {

	}
	



	@Override
	protected Void call() throws Exception {		
		try {
			Thread.sleep(2000);
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

}
