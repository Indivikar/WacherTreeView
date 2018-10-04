package app.TreeViewWatchService;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import app.controller.CTree;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;

public class ServiceWaitForUpdate  extends Service<Boolean> implements Callable<Boolean>{

	private CTree cTree;
	private FileAlterationListenerImpl fileAlterationListenerImpl;
	private PathTreeCell cell;
	
	public ServiceWaitForUpdate(FileAlterationListenerImpl fileAlterationListenerImpl, CTree cTree) {
		this.fileAlterationListenerImpl = fileAlterationListenerImpl;
		this.cTree = cTree;
	}

	public void ServiceWaitForUpdate() {
		
	}

	@Override
	protected void succeeded() {
		fileAlterationListenerImpl.getData().clear();
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
            		           		
					getMainDirectory();

					if (FileAlterationListenerImpl.isInternalChange) {
						internalChange();		
					} else {
						externalChange();
					}
										
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	
            	System.out.println("ServiceWaitForUpdate Ende");
				return true;              
            }
		};
	}

	
	private void internalChange() throws InterruptedException {
		System.err.println("internalChange()");
//		openAllExpandedItems();
		fileAlterationListenerImpl.actionChange();
		System.out.println("internalChange() -> 1");
//		openAllExpandedItems();
		System.out.println("internalChange() -> 2");

		Thread.sleep(10000);	
		getMainDirectory();		

		FileAlterationListenerImpl.isInternalChange = false;
		System.out.println("internalChange() -> 3");
	}
	
	private void externalChange() throws InterruptedException {
//		System.out.println("show List");
		System.err.println("externalChange()");
		Thread.sleep(1000);
		fileAlterationListenerImpl.getvBoxMessage().setVisible(true);
		System.out.println("ServiceWaitForUpdate 4");
//		startUpdate = false;

	}
	

	@Override
	public Boolean call() throws Exception {
		return null;
	}
	

	

	public void getMainDirectory() {
		
		System.out.println("data.size(): " + fileAlterationListenerImpl.getData().size());
					
			ModelFileChanges res = null;
			Optional<ModelFileChanges> resDirectory = fileAlterationListenerImpl.getData().stream()		
				.filter(p -> IsPathDirectory( p.getFileString()))	
				.min((p1, p2) -> Integer.compare(p1.getFileString().length(), p2.getFileString().length()));
			
			if (!fileAlterationListenerImpl.getData().isEmpty()) {
				if (resDirectory.isPresent()) {
					res = resDirectory.get();
					
				} else {
					System.err.println("ergOptional not present");
					Optional<ModelFileChanges> resFiles = fileAlterationListenerImpl.getData().stream()		
							.filter(p -> !IsPathDirectory( p.getFileString()))
							.findFirst();	
					res = resFiles.get();
				}
			} else {
				System.err.println("list data is empty");
				return;
			}


			System.err.println("der kleinste: " + res.getFileString());
			removeItem(res);
			
			if (!existItem(res) && !existMainDirectoryFromItem(res)) {
				cTree.getListSaveChanges().add(res);	
				sortList(cTree.getListSaveChanges());				
			}
			fileAlterationListenerImpl.getData().remove(res);

			getMainDirectory();
	}

	private boolean IsPathDirectory(String path) {
	    File file = new File(path);

	    // check if the file/directory is already there
	    if (!file.exists()) {
	        // see if the file portion it doesn't have an extension
	        return file.getName().lastIndexOf('.') == -1;
	    } else {
	        // see if the path that's already in place is a file or directory
	        return true;
	    }
	}
	
	private void removeItem(ModelFileChanges erg) {
		
		for (int i = 0; i < fileAlterationListenerImpl.getData().size(); i++) {
			if (fileAlterationListenerImpl.getData().get(i).getFile().isDirectory() && fileAlterationListenerImpl.getData().get(i).getFileString().contains(erg.getFileString())) {				
				for (String parents : checkParents(fileAlterationListenerImpl.getData().get(i))) {
					if (erg.getFileString().equalsIgnoreCase(parents)) {
//						System.out.println("remove: " + data.get(i).getFile());
						fileAlterationListenerImpl.getData().remove(i);
					}
				}					
			}
		}
	}
	
	private ObservableList<String> checkParents(ModelFileChanges modelFileChanges) {
		ObservableList<String> list = FXCollections.observableArrayList();
		
		File file = modelFileChanges.getFile();
		
		while (true) {
			file = file.getParentFile();

			if (file == null) {
				break;
			} else {
				list.add(file.getAbsolutePath());
			}	
		}
		return list;
	}
	
	private boolean existItem(ModelFileChanges erg) {
		for (ModelFileChanges item : cTree.getListSaveChanges()) {
			if (item.equals(erg)) {
				return true;
			}
		}
		return false;		
	}
	
	private boolean existMainDirectoryFromItem(ModelFileChanges res) {

		for (ModelFileChanges item : cTree.getListSaveChanges()) {
			if (item.getAction().equalsIgnoreCase(res.getAction())) {
				for (String parents : getAllParents(res.getFile())) {
					if (parents.equalsIgnoreCase(item.getFileString())) {
						return true;
					}
				}	
			}			
		}
		
		return false;
	}
	
	private ObservableList<String> getAllParents(File file) {
		ObservableList<String> parents = FXCollections.observableArrayList();
		File parentFile = file;

		while (parentFile != null) {
			parents.add(parentFile.getAbsolutePath());
			parentFile = parentFile.getParentFile();
		}
		
		return parents;
	}
	
	public ObservableList<ModelFileChanges> sortList(ObservableList<ModelFileChanges> list) {
		list.sort((a, b) -> Long.compare(a.getTimeInMilli(), b.getTimeInMilli()));
		return list;
	}
	
}
