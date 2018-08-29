package app.TreeViewWatchService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.Callable;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class ServiceWaitUpdate  extends Service<Boolean> implements Callable<Boolean>{

	private FileAlterationListenerImpl fileAlterationListenerImpl;
	
	public ServiceWaitUpdate(FileAlterationListenerImpl fileAlterationListenerImpl) {
		
	}

	public void ServiceWaitForUpdate() {
		
	}

	@Override
	protected void succeeded() {
		reset();
	}
	
	@Override
	protected void cancelled() {
		reset();
	}
	
	@Override
	protected void failed() {
		reset();
	}
	
	@Override
	protected Task<Boolean> createTask() {
		
		return new Task<Boolean>() {
            @Override
            protected Boolean call() {
            	try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return true;              
            }
		};
		
//		try {
//			Thread.sleep(5000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return null;
//		
	}

	@Override
	public Boolean call() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}



}
