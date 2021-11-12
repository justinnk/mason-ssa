/*
 * This repository might have licensing issues, which are in the process of being resolved, so no use is permitted at this time.
 */

package org.justinnk.masonssa.evaluation;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Static class to write a DataFrame table to a csv file. */
public class CSVWriter {
  public static void writeAndClose(DataFrame data, Path path, boolean writeIndex) {
    try {
      /* If file does not exist, create it. */
      if (!Files.exists(path)) {
        Files.createDirectories(path.getParent());
        Files.createFile(path);
      }
      FileWriter csvWriter = new FileWriter(path.toString());
      if (!data.isEmpty()) {
        /* header */
        if (writeIndex) {
          csvWriter.write("index,");
        }
        for (int i = 0; i < data.columns.size(); i++) {
          csvWriter.write(data.columns.get(i).name + ",");
        }
        csvWriter.write("\n");
        /* data */
        for (int i = 0; i < data.columns.get(0).data.size(); i++) {
          if (writeIndex) {
            csvWriter.write(i + ",");
          }
          for (int j = 0; j < data.columns.size(); j++) {
            csvWriter.write(data.columns.get(j).data.get(i) + ",");
          }
          csvWriter.write("\n");
        }
      }
      csvWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
