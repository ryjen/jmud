package net.arg3.jmud.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.arg3.jmud.EditorMetaData;
import net.arg3.jmud.Jmud;
import net.arg3.jmud.Persistance;
import net.arg3.jmud.annotations.SubClasses;
import net.arg3.jmud.interfaces.IDataObject;
import net.arg3.jmud.interfaces.IFormatible;

import org.slf4j.LoggerFactory;

import se.datadosen.component.RiverLayout;

public abstract class AbstractCollectionView extends JPanel implements
		ListSelectionListener {

	public class EditorCellRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 1L;

		/** Creates a new instance of LocaleRenderer */
		public EditorCellRenderer() {
		}

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {

			super.getListCellRendererComponent(list, value, index, isSelected,
					cellHasFocus);
			if (value instanceof IFormatible)
				setText(((IFormatible) value).toString("Z"));
			else
				setText(value.toString());
			return this;
		}
	}

	private class CreateTypeActionListener implements ActionListener {

		Class<?> type;

		public CreateTypeActionListener(Class<?> type) {
			this.type = type;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				IDataObject<?> obj = (IDataObject<?>) type.newInstance();

				add(obj);

				refreshList();

				values.setSelectedValue(obj, true);

			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	private static final long serialVersionUID = 1L;
	private final JSplitPane splitter;
	private JList values;
	private AbstractDefaultView editor;

	private final JMenuBar toolBar;

	private JLabel title;

	// /private Collection<?> data;
	public AbstractCollectionView() {
		setLayout(new BorderLayout());

		setBorder(BorderFactory.createEmptyBorder());

		toolBar = new JMenuBar();
		toolBar.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		add(toolBar, BorderLayout.NORTH);

		splitter = new JSplitPane();
		splitter.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(100, 100));
		splitter.setLeftComponent(panel);
		panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		splitter.setRightComponent(panel);
		add(splitter, BorderLayout.CENTER);
	}

	public void setReadOnly(boolean value) {
		editor.setEnabled(value);
	}

	public void setEditor(EditorMetaData emd, java.lang.Object list) {

		if (emd == null) {
			LoggerFactory.getLogger(AbstractCollectionView.class).warn(
					"null editor meta data in setEditor for "
							+ list.getClass().getName());
			return;
		}
		setData(list);

		this.editor = emd.getEditor();

		// if (editor != splitter.getRightComponent())
		createToolBar();

		splitter.setRightComponent(editor);

		refreshList();
	}

	public void setTitle(String text) {
		if (title != null)
			title.setText(text + ": ");
	}

	public void setWaiting() {
		JPanel pan = new JPanel(new RiverLayout());
		pan.add("center hfill vfill", new JLabel(new ImageIcon(
				"resources/wait.gif")));
		splitter.setRightComponent(pan);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event
	 * .ListSelectionEvent)
	 */
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting())
			return;
		if (values.getSelectedIndex() != -1) {
			editor.refresh(values.getSelectedValue());
		}
	}

	private void createToolBar() {

		toolBar.removeAll();

		title = new JLabel();
		toolBar.add(title);

		SubClasses subTypes = editor.getType().getAnnotation(SubClasses.class);

		JButton button;

		if (subTypes == null) {
			button = new JButton("Create");
			button.addActionListener(new CreateTypeActionListener(editor
					.getType()));
			toolBar.add(button);
		} else {
			JMenu menu = new JMenu("Create");
			menu.setBorder(BorderFactory
					.createEtchedBorder(EtchedBorder.LOWERED));
			for (Class<?> type : subTypes.types()) {
				JMenuItem mitem = new JMenuItem(Jmud.camelToHuman(type
						.getSimpleName()));
				mitem.addActionListener(new CreateTypeActionListener(type));
				menu.add(mitem);
			}

			toolBar.add(menu);
		}

		button = new JButton("Delete");

		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int index = values.getSelectedIndex();
				if (index == -1) {
					JOptionPane.showMessageDialog(null,
							"You have to select something first.");
					return;
				}

				Persistance.delete(values.getSelectedValue());

				remove(values.getSelectedValue());

				// values.remove(index);

				refreshList();
			}
		});
		toolBar.add(button);

		button = new JButton("Save");

		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				final java.lang.Object obj = values.getSelectedValue();

				editor.save(obj);
				new Thread(new Runnable() {

					@Override
					public void run() {
						Persistance.save(obj);
					}
				}).start();

				if (values.getSelectedIndex() != -1) {
					editor.refresh(values.getSelectedValue());
				}
			}
		});
		toolBar.add(button);

	}

	private void refreshList() {
		java.lang.Object[] temp = getValues();
		if (temp == null) {
			return;
		}
		if (values == null) {
			values = new JList(temp);
			values.setCellRenderer(new EditorCellRenderer());
			values.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			values.addListSelectionListener(this);
			JScrollPane scroller = new JScrollPane(values);
			scroller.setPreferredSize(new Dimension(100, 100));
			splitter.setLeftComponent(scroller);
		} else {
			values.setListData(temp);
		}
		if (values.getModel().getSize() > 0) {
			values.setSelectedIndex(0);
		}
	}

	protected abstract void add(java.lang.Object obj);

	protected abstract Object[] getValues();

	protected abstract void remove(java.lang.Object obj);

	protected abstract void setData(java.lang.Object obj);
}
