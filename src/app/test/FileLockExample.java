package app.test;
import java.io.*;
import java.nio.channels.*;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.Set;

public class FileLockExample {
	
		static String name = "H:\\Test\\ordner_36\\1\\hwb-2_720x600.jpg";
		static String name2 = "H:\\Test\\ordner_36";
	
        public static void main(String[] args) throws Exception {


        	
                try {
                        RandomAccessFile file = new RandomAccessFile(name, "rw");
                        FileChannel fileChannel = file.getChannel();
                        FileLock fileLock = fileChannel.tryLock();
                        if (fileLock != null) {
                                System.out.println("File is locked");
                                accessFile();
                        }
                } catch (Exception e) {
                }
        }

        
        

        
        public static void accessFile() {
                try {
                        String line = "";
                        BufferedReader br = new BufferedReader(
                                        new FileReader(name));
                        while ((line = br.readLine()) != null) {
                                System.out.println(line);
                        }
                } catch (Exception e) {
                        System.out.println(e);
                }
        }
}