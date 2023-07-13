package com.github.truthanytime.bootforum.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Adapt from the following site:
 * 		https://www.baeldung.com/java-compress-and-uncompress
 */
public class ZipUtil {
	
	public static boolean zipDirectory(File directoryToZip, File destinationFile) {
		
		try {
			FileOutputStream fos = new FileOutputStream(destinationFile);
			ZipOutputStream zipOut = new ZipOutputStream(fos);

			zipFile(directoryToZip, directoryToZip.getName(), zipOut);
			zipOut.close();
			fos.close();
		} 
		catch (IOException e) {
			return false;
		}
		return true;
	}
	
    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {

    	if (fileToZip.isHidden()) {
            return;
        }
        
    	if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
    	
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        
        int length;
        
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }
}
