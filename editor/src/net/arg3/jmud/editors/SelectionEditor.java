/**
 * Project: jmudeditor
 * Date: 2009-09-29
 * Package: net.arg3.jmud.editors
 * Author: Ryan Jennings <c0der78@gmail.com>
 */
package net.arg3.jmud.editors;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import net.arg3.jmud.FieldMetaData;

/**
 * @author Ryan Jennings <c0der78@gmail.com>
 * 
 */
public class SelectionEditor extends AbstractEditor implements ActionListener {

	private class SelectionDialog extends JDialog {

		private static final long serialVersionUID = 1L;

		public SelectionDialog() {
			setSize(300, 200);
			setModal(true);
			add(new JScrollPane(list));

			JLabel help = new JLabel("Select a value:");
			add(help, BorderLayout.NORTH);

			JButton ok = new JButton("Cancel");
			ok.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					list.setSelectedIndex(-1);
					setVisible(false);
				}
			});
			JPanel bottom = new JPanel();
			bottom.add(ok);

			ok = new JButton("OK");
			ok.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					setVisible(false);
				}
			});

			bottom.add(ok);
			add(bottom, BorderLayout.SOUTH);
		}
	}

	private static final long serialVersionUID = 1L;
	JLabel label;
	JButton btn;
	JButton del;
	JList list;
	Object value;
	FieldMetaData fmd;

	public SelectionEditor() {

		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		label = new JLabel();
		btn = new JButton("...");

		btn.addActionListener(this);

		del = new JButton("X");
		del.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				value = null;
				label.setText("");
			}
		});
		panel.add(label);
		panel.add(btn);
		panel.add(del);

		add(panel);

		list = new JList();
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		SelectionDialog dlg = new SelectionDialog();
		dlg.setLocationRelativeTo(this);
		dlg.setVisible(true);

		Object obj = list.getSelectedValue();
		if (obj != null) {
			setValue(obj);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.jennings.ryan.jMUD.editors.AbstractEditor#getValue()
	 */
	@Override
	public Object getValue() {
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.jennings.ryan.jMUD.editors.AbstractEditor#setReadOnly(boolean)
	 */
	@Override
	public void setReadOnly(boolean value) {
		btn.setEnabled(value);
		del.setEnabled(value);
	}

	/*
	 * public void setType(final Class<?> type) { this.type = type; }
	 */

	public void setFieldMetaData(final FieldMetaData value) {
		fmd = value;

		list.setListData((Object[]) fmd.getValue(getValue()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.jennings.ryan.jMUD.editors.AbstractEditor#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Object value) {
		this.value = value;
		label.setText(value != null ? value.toString() : "");
	}
}
