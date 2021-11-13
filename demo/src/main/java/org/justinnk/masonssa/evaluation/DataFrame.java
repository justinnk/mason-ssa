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

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

/** A simple table consisting of columns indexed by a double. */
public class DataFrame {
  public String name;
  public ArrayList<Column> columns = new ArrayList<>();

  public DataFrame() {
    this.name = "<DataFrame" + this + ">";
  }

  /** Returns whether this table has any non-empty columns. */
  public boolean isEmpty() {
    if (!columns.isEmpty()) {
      return columns.stream().allMatch(c -> c.data.isEmpty());
    }
    return true;
  }

  /** Tries to retrieve the column with the given name. */
  public Optional<Column> get(String column) {
    return columns.stream().filter(c -> Objects.equals(c.name, column)).findFirst();
  }

  public Column getOrCreate(String column) {
    Optional<Column> col = columns.stream().filter(c -> Objects.equals(c.name, column)).findFirst();
    if (col.isPresent()) {
      return col.get();
    }
    Column newCol = new Column(column);
    this.columns.add(newCol);
    return newCol;
  }

  /**
   * Computes a function on an existing value at the index of the given column (or null, if there is
   * no value associated with the index).
   */
  public void compute(String column, int index, BiFunction<Object, Object, Object> func) {
    Optional<Column> c = get(column);
    if (c.isPresent()) {
      c.get().compute(index, func);
    } else {
      Column nC = new Column(column);
      nC.compute(index, func);
      columns.add(nC);
    }
  }

  /**
   * Computes a function on an existing value at the index of all columns (or null, if there is no
   * value associated with the index).
   */
  public void compute(int index, BiFunction<Object, Object, Object> func) {
    for (Column c : columns) {
      c.compute(index, func);
    }
  }

  /** Replaces all values in the specified column with the result of func. */
  public void replaceAll(String column, BiFunction<Object, Object, Object> func) {
    Optional<Column> c = get(column);
    if (c.isPresent()) {
      c.get().replaceAll(func);
    } else {
      Column nC = new Column(column);
      nC.replaceAll(func);
      columns.add(nC);
    }
  }

  /** Replaces all values in this frame with the result of func. */
  public void replaceAll(BiFunction<Object, Object, Object> func) {
    for (Column c : columns) {
      c.replaceAll(func);
    }
  }

  /**
   * Adds an entry to the provided column. If there is no column with this name, it will be created.
   */
  public void addEntry(String column, Object value) {
    Optional<Column> matchinColumn =
        columns.stream().filter(c -> Objects.equals(c.name, column)).findFirst();
    if (matchinColumn.isPresent()) {
      matchinColumn.get().addEntry(value);
    } else {
      Column nC = new Column(column);
      nC.addEntry(value);
      columns.add(nC);
    }
  }

  public Object getEntry(String column, Object key) {
    Optional<Column> col = columns.stream().filter(c -> Objects.equals(c.name, column)).findFirst();
    if (col.isPresent()) {
      return col.get().data.get(key);
    }
    return null;
  }

  /**
   * Appends all of data to the matching columns in this frame. Non-existing columns are copied as
   * they are to this frame.
   */
  public void append(DataFrame data) {
    for (Column col : data.columns) {
      Optional<Column> thisCol = this.get(col.name);
      if (thisCol.isPresent()) {
        thisCol.get().addEntries(col.data);
      } else {
        columns.add(col);
      }
    }
  }
}
