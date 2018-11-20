package app.test.CopyFilesApp;

import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.FileVisitResult;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;


/*
 * Class has one static method.
 * Creates a ZIP (compressed archive) file for the supplied directory with
 * files. This directory is the same as the target directory as a result of
 * copy action in Copy Dialog.
 * The ZIP file created is in the same directory in which the supplied directory
 * is present.
 */
public class ZipFileCreater {

    
    public static String zip(Path input)
            throws IOException {

        String targetFileNameStr = input.getFileName().toString() + ".zip";
        Path targetPath =
                Paths.get(input.getParent().toString(), targetFileNameStr);
        ZipOutputStream zipOutputStream =
                new ZipOutputStream(new FileOutputStream(targetPath.toString()));

        Files.walkFileTree(input, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                
                Path targetfile = input.relativize(file);
                ZipEntry zipEntry = new ZipEntry(targetfile.toString());
                zipOutputStream.putNextEntry(zipEntry);
                
                try(FileInputStream fileInputStream =
                        new FileInputStream(file.toString())) {
                
                    byte [] buf = new byte [512];
                    int bytesRead;
                
                    while ((bytesRead = fileInputStream.read(buf)) > 0) {
                
                        zipOutputStream.write(buf, 0, bytesRead);
                    }
                }
                
                zipOutputStream.closeEntry();
                return FileVisitResult.CONTINUE;
            }
        });
        
        zipOutputStream.close();
        return targetPath.toString();
    }
}
