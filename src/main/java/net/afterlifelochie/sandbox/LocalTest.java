package net.afterlifelochie.sandbox;

import java.util.Iterator;
import java.util.Map.Entry;

public class LocalTest {

	public static void main(String[] args) {
		new LocalTest();
	}

	public LocalTest() {
		WatchedList<String, WatchedValue<Integer>> list = new WatchedList<String, WatchedValue<Integer>>();
		list.set("A", new WatchedValue<Integer>(list, 1));
		list.set("B", new WatchedValue<Integer>(list, 2));
		list.set("C", new WatchedValue<Integer>(list, 3));
		printList(list);

		WatchedList<String, WatchedValue<Integer>> dest = new WatchedList<String, WatchedValue<Integer>>();
		diff(list, dest);
		printList(dest);

		list.get("B").set(5);
		printList(list);

		diff(list, dest);
		printList(dest);
	}

	public void printList(WatchedList<?, ?> list) {
		StringBuilder result = new StringBuilder();
		result.append("WatchedList: { ");
		for (Entry<?, ?> entry : list.entrySet()) {
			result.append(entry.getKey().toString()).append(": ");
			result.append(entry.getValue().toString()).append(", ");
		}
		result.delete(result.length() - 2, result.length());
		result.append(" }.");
		System.out.println(result.toString());
	}

	public void diff(WatchedList<String, WatchedValue<Integer>> src, WatchedList<String, WatchedValue<Integer>> dst) {
		if (src.modified()) {
			Iterator<String> added = src.added().iterator();
			Iterator<String> removed = src.removed().iterator();
			while (removed.hasNext()) {
				String label = removed.next();
				dst.remove(label);
			}

			while (added.hasNext()) {
				String label = added.next();
				dst.set(label, src.get(label));
			}

			Iterator<String> labels = src.keys();
			while (labels.hasNext()) {
				String label = labels.next();
				WatchedValue<Integer> value = src.get(label);
				if (value.modified()) {
					dst.get(label).set(value.get());
					value.clearModified();
				}
			}

			src.clearModified();
			dst.clearModified();
		}
	}

}
