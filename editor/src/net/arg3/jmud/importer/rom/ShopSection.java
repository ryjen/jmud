package net.arg3.jmud.importer.rom;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.arg3.jmud.NonPlayer;
import net.arg3.jmud.Persistance;
import net.arg3.jmud.Shop;
import net.arg3.jmud.importer.FileReader;
import net.arg3.jmud.importer.ImportException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class ShopSection extends AbstractSection {

	final Map<Integer, Shop> Loaded = new HashMap<Integer, Shop>();

	public ShopSection(RomImporter importer) {
		super(importer);
	}

	@Override
	public void commit() throws HibernateException {
		Session s = Persistance.getSession();

		Transaction tx = s.beginTransaction();

		for (Map.Entry<Integer, Shop> entry : Loaded.entrySet()) {
			if (!importer.Mobiles.Loaded.containsKey(entry.getKey())) {
				log("no mobile for shop ", Integer.toString(entry.getKey()));
				continue;
			}

			NonPlayer npc = importer.Mobiles.Loaded.get(entry.getKey());
			Shop shop = entry.getValue();

			npc.setShop(shop);
			shop.setOwner(npc);

			s.saveOrUpdate(shop);
			s.saveOrUpdate(npc);
		}

		tx.commit();
	}

	@Override
	public Integer load(FileReader reader) throws ImportException, IOException {
		for (;;) {
			int vnum = reader.readNumber();

			if (vnum == 0)
				break;

			Shop shop = new Shop();
			for (int i = 0; i < 5; i++)
				reader.readNumber();

			shop.setProfitBuy(reader.readNumber());
			shop.setProfitSell(reader.readNumber());
			shop.setOpenHour(reader.readNumber());
			shop.setCloseHour(reader.readNumber());
			reader.readToEol();

			Loaded.put(vnum, shop);
		}

		log("loaded ", Integer.toString(Loaded.size()), " shop");
		return Loaded.size();
	}
}
