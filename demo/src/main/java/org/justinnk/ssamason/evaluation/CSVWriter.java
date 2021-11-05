/*
 * MIT License
 *
 * Copyright (c) 2021 Justin Kreikemeyer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.justinnk.ssamason.evaluation;

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
