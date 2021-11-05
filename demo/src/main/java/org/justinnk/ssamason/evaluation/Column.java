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

import java.util.Comparator;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.BiFunction;

public class Column {
  public HashMap<Integer, Object> data = new HashMap<>();
  public String name;

  public Column() {
    this.name = this.toString();
  }

  public Column(String name) {
    this.name = name;
  }

  public void addEntry(Object value) {
    Optional<Integer> last = data.keySet().stream().max(Comparator.naturalOrder());
    if (last.isPresent()) {
      data.put(last.get() + 1, value);
    } else {
      data.put(0, value);
    }
  }

  public void addEntries(HashMap<Integer, Object> values) {
    Optional<Integer> last = data.keySet().stream().max(Comparator.naturalOrder());
    if (last.isPresent()) {
      int i = 0;
      for (Object value : values.keySet().stream().sorted().toArray()) {
        data.put(last.get() + i + 1, values.get(value));
        i++;
      }
    } else {
      data.putAll(values);
    }
  }

  public void compute(int index, BiFunction<Object, Object, Object> func) {
    data.compute(index, func);
  }

  public void replaceAll(BiFunction<Object, Object, Object> func) {
    data.replaceAll(func);
  }
}
