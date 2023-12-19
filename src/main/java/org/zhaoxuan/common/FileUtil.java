package org.zhaoxuan.common;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
public class FileUtil {

    public static void saveToLocalDisk(byte[] fileBytes, String dirName, String fileName) {

        boolean dirExist;

        File tempFile = new File(dirName);
        if (!tempFile.exists()) {
            dirExist = tempFile.mkdirs();
        } else {
            dirExist = true;
        }

        if (!dirExist) {
            log.error("CreateImageDirFailure.");
            return;
        }

        try (OutputStream os = Files.newOutputStream(Paths.get(tempFile + fileName))) {

            os.write(fileBytes, 0, fileBytes.length);

        } catch (Exception ex) {
            log.warn("SaveToLocalDiskFailure.", ex);
        }

    }

}
