package org.justinnk.ssamason.evaluation;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.BiFunction;

public class Column {
	public HashMap<Integer, Object> data = new HashMap<Integer, Object>();
	public String name = "";

	public Column() {
		this.name = this.toString();
	}

	public Column(String name) {
		this.name = name;
	}

	public void addEntry(Object value) {
		Optional<Integer> last = data.keySet().stream().max((x, y) -> x.compareTo(y));
		if (last.isPresent()) {
			data.put(last.get() + 1, value);
		} else {
			data.put(0, value);
		}
	}

	public void addEntries(HashMap<Integer, Object> values) {
		Optional<Integer> last = data.keySet().stream().max((x, y) -> x.compareTo(y));
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
