package net.afterlifelochie.sandbox;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import pcl.common.helpers.NetworkHelpers;
import pcl.common.util.WorldLocation;
import pcl.lc.base.data.ObserverContext;
import pcl.lc.base.data.WatchedList;
import pcl.lc.base.network.PacketLogger;
import pcl.lc.base.network.packet.WatchedListSyncPacket;

public class LocalTest {

	public static void main(String[] args) {
		new LocalTest();
	}

	final PacketLogger log = new PacketLogger(new File("log.dat"));

	public LocalTest() {
		final NetworkHelpers helpers = new NetworkHelpers();
		helpers.init();

		try {
			log.open();
			final ObserverContext ac = new ObserverContext();
			final WatchedList<String, Object> alist = new WatchedList<String, Object>();

			final ObserverContext bc = new ObserverContext();
			final WatchedList<String, Object> blist = new WatchedList<String, Object>();

			alist.set("tomato", "red");
			alist.set("apple", "red");
			alist.set("orange", "orange");
			alist.set("bacon", "yummy");
			synchronize(ac, alist, bc, blist);
			if (!match(alist, blist))
				throw new IOException("Mismatch detected!");

			alist.remove("tomato");
			alist.remove("apple");
			synchronize(ac, alist, bc, blist);
			if (!match(alist, blist))
				throw new IOException("Mismatch detected!");
			
			alist.set("tomato", "round");
			alist.set("apple", "round too");
			alist.remove("tomato");
			alist.remove("orange");
			synchronize(ac, alist, bc, blist);
			if (!match(alist, blist))
				throw new IOException("Mismatch detected!");

		} catch (Throwable t) {
			t.printStackTrace();
			log.close();
		}
	}

	public boolean match(WatchedList<String, Object> a, WatchedList<String, Object> b) {
		Iterator<String> i = a.keys();
		System.out.println("----");
		while (i.hasNext()) {
			String key = i.next();
			System.out.println(String.format("%s: %s =>> %s", key, a.get(key), b.get(key)));
			if (b.get(key) == null && a.get(key) != null) {
				return false;
			}
			if (a.get(key) == null && b.get(key) != null) {
				return false;
			}
			if (!a.get(key).equals(b.get(key))) {
				return false;	
			}
		}
		System.out.println("----");
		return true;
	}

	public void synchronize(ObserverContext ao, WatchedList<String, Object> a, ObserverContext bo,
			WatchedList<String, Object> b) throws IOException {
		for (String s : a.added())
			System.out.println("+ " + s);
		for (String s : a.removed())
			System.out.println("- " + s);
		for (String s : a.modified()) 
			System.out.println("m " + s);
		
		WatchedListSyncPacket packet = new WatchedListSyncPacket(new WorldLocation(255, 255, 255, 255), a);
		a.clearModified(ao);

		log.logPacket(packet);

		ByteArrayOutputStream netstr = new ByteArrayOutputStream();
		packet.encode(new DataOutputStream(netstr));
		byte[] rstr = netstr.toByteArray();

		ByteArrayInputStream netin = new ByteArrayInputStream(rstr);
		WatchedListSyncPacket result = new WatchedListSyncPacket();
		result.decode(new DataInputStream(netin));
		result.apply(b);
		b.clearModified(bo);
	}

}
