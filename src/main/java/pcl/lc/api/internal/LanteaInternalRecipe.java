package pcl.lc.api.internal;

import java.util.HashMap;

import net.minecraft.item.ItemStack;

public class LanteaInternalRecipe {

	private HashMap<Integer, ItemStack> r_stacklist;
	private HashMap<Integer, ItemStack> r_products;
	private boolean r_shapeless;

	public LanteaInternalRecipe(boolean shapeless, ItemStack[] products, ItemStack... args) {
		r_shapeless = shapeless;
		r_stacklist = new HashMap<Integer, ItemStack>();
		r_products = new HashMap<Integer, ItemStack>();
		for (int i = 0; i < products.length; i++)
			r_products.put(i, products[i]);
		for (int i = 0; i < args.length; i++)
			r_stacklist.put(i, args[i]);
	}

	public boolean shapeless() {
		return r_shapeless;
	}

	public int products() {
		return r_products.size();
	}

	public int reagents() {
		return r_stacklist.size();
	}

	public ItemStack reagent(int i) {
		return r_stacklist.get(i);
	}

	public ItemStack product(int i) {
		return r_products.get(i);
	}
}
