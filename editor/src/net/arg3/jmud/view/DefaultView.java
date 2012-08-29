/**
 * Project: jmudeditor
 * Date: 2009-09-24
 * Package: net.arg3.jmud
 * Author: Ryan Jennings <c0der78@gmail.com>
 */
package net.arg3.jmud.view;

import java.awt.BorderLayout;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import net.arg3.jmud.FieldMetaData;
import net.arg3.jmud.Jmud;
import net.arg3.jmud.editors.AbstractCollectionEditor;
import net.arg3.jmud.editors.AbstractEditor;
import net.arg3.jmud.editors.NumberEditor;
import se.datadosen.component.RiverLayout;

/**
 * @author Ryan Jennings <c0der78@gmail.com>
 * 
 */
public class DefaultView extends AbstractDefaultView {

	private static final long serialVersionUID = 1L;
	JTabbedPane tabs;
	JPanel mainEditor;
	Collection<FieldMetaData> columns;

	public DefaultView(Class<?> type) {
		super(type);
		init();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.jennings.ryan.jMUD.AbstractEditorPanel#refresh(java.lang.Object)
	 */
	@Override
	public void refresh(final java.lang.Object obj) {

		new Thread(new Runnable() {
			@Override
			public void run() {

				if (columns.size() == 0 || obj.getClass() != getType()) {
					mainEditor.removeAll();
					tabs.removeAll();

					tabs.addTab("Edit", new JScrollPane(mainEditor));

					for (Method field : type.getMethods()) {
						if (!FieldMetaData.isColumn(field))
							continue;
						FieldMetaData fmd = new FieldMetaData(obj, field);

						if (fmd.isEmbedded()) {
							for (FieldMetaData eKey : fmd.getEmbeddedFields()) {
								addField(eKey);

							}
						} else {
							columns.add(fmd);

							addField(fmd);
						}

					}
				} else {
					for (FieldMetaData fmd : columns) {
						fmd.updateValue(obj);
					}
				}

				/*
				 * FieldMetaData id = new FieldMetaData(type, Persistance
				 * .getIdentifier(type));
				 * 
				 * addAllFields(id, obj);
				 * 
				 * for (String field : Persistance.getColumns(type)) {
				 * FieldMetaData col = new FieldMetaData(type, field);
				 * 
				 * addAllFields(col, obj); }
				 * 
				 * 
				 * columns.clear(); for (Method m : obj.getClass().getMethods())
				 * { if (FieldMetaData.isColumn(m)) { FieldMetaData col = new
				 * FieldMetaData(obj, m);
				 * 
				 * updateFields(col); } }
				 * 
				 * boolean firstRefresh = false; if (getType() != obj.getClass()
				 * || mainEditor.getComponentCount() == 0) { tabs.removeAll();
				 * mainEditor.removeAll(); tabs.addTab("Edit", new
				 * JScrollPane(mainEditor)); firstRefresh = true; } for
				 * (FieldMetaData col : columns) { if (firstRefresh)
				 * addField(col); col.setValue(); }
				 */
				validate();
			}
		}).start();
	}

	@Override
	public void save(java.lang.Object obj) {
		for (FieldMetaData col : columns) {
			col.save();
		}
	}

	private void addField(FieldMetaData fmd) {

		AbstractEditor ed = fmd.getEditor();

		if (ed instanceof AbstractCollectionEditor) {
			tabs.addTab(Jmud.capitalize(fmd.getFieldName()), ed);
			return;
		}
		JLabel label = new JLabel(Jmud.capitalize(fmd.getFieldName()) + ":");

		mainEditor.add("p left vtop", label);

		String mod = (ed instanceof NumberEditor) ? "tab" : "tab hfill";
		mainEditor.add(mod, ed);
	}

	private void init() {
		setLayout(new BorderLayout());
		tabs = new JTabbedPane();
		tabs.setBorder(BorderFactory.createEmptyBorder());
		columns = Collections
				.synchronizedCollection(new Vector<FieldMetaData>());
		mainEditor = new JPanel(new RiverLayout());
		mainEditor.setBorder(BorderFactory.createEmptyBorder());
		add(tabs, BorderLayout.CENTER);

	}
}
