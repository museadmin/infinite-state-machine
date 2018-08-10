package com.github.museadmin.infinite_state_machine.data.access.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipDirectory {

  public static void zipDirectory(String sourceDirectoryPath, String zipPath) throws IOException {
    Path zipFilePath = Files.createFile(Paths.get(zipPath));

    try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(zipFilePath))) {
      Path sourceDirPath = Paths.get(sourceDirectoryPath);

      Files.walk(sourceDirPath).filter(path -> !Files.isDirectory(path))
        .forEach(path -> {
          ZipEntry zipEntry = new ZipEntry(sourceDirPath.relativize(path).toString());
          try {
            zipOutputStream.putNextEntry(zipEntry);
            zipOutputStream.write(Files.readAllBytes(path));
            zipOutputStream.closeEntry();
          } catch (Exception e) {
            System.err.println(e);
          }
        });
    }
  }
}
