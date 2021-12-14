/*
 * Copyright 2021 Justin Kreikemeyer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.justinnk.masonssa.evaluation;

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
