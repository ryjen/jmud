/**
 * Project: jmudeditor
 * Date: 2009-09-23
 * Package: net.arg3.jmud
 * Author: Ryan Jennings <c0der78@gmail.com>
 */
package net.arg3.jmud;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.SplashScreen;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import net.arg3.jmud.importer.ImportDialog;
import net.arg3.jmud.importer.rom.RomImporter;
import net.arg3.jmud.view.AbstractCollectionView;
import net.arg3.jmud.view.CollectionView;

/**
 * @author Ryan Jennings <c0der78@gmail.com>
 * 
 */
public class Editor extends JFrame {

	private class EditorMenuItem extends JMenuItem {

		private static final long serialVersionUID = 1L;
		EditorMetaData emd;

		public EditorMenuItem(EditorMetaData emd) {
			super(emd.toString());
			this.emd = emd;
		}

		public EditorMetaData getMetaData() {
			return emd;
		}
	}

	private static final long serialVersionUID = 1L;
	static final SplashScreen splash = SplashScreen.getSplashScreen();
	private static Editor instance;

	public static Editor getInstance() {
		if (instance == null) {
			instance = new Editor();
		}
		return instance;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame.setDefaultLookAndFeelDecorated(true);

		try {
			UIManager
					.setLookAndFeel(new com.jgoodies.looks.plastic.PlasticLookAndFeel());
		} catch (Exception e) {
			System.out.println("Failed to load substance look and feel");
			e.printStackTrace();
			System.exit(1);
		}
		Editor frame = Editor.getInstance();
		Persistance.getSession();
		frame.setLocationRelativeTo(null);
		if (splash != null)
			splash.close();
		frame.setVisible(true);
	}

	private final JLabel statusBar;

	private final AbstractCollectionView mainPanel;

	private Editor() {
		setSize(800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setJMenuBar(createMenuBar());
		mainPanel = new CollectionView();
		add(mainPanel, BorderLayout.CENTER);
		statusBar = new JLabel();
		statusBar.setPreferredSize(new Dimension(10, 10));
		add(statusBar, BorderLayout.SOUTH);
	}

	public void setEditor(final EditorMetaData emd) {

		mainPanel.setWaiting();

		new Thread(new Runnable() {

			@Override
			public void run() {
				mainPanel.setEditor(emd, Persistance.getAll(emd.getType()));
				mainPanel.setTitle(emd.toString() + " Editor");
			}

		}).start();

		validate();
	}

	public void setWaiting() {
		mainPanel.setWaiting();
	}

	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();

		JMenu menu = new JMenu("File");
		menuBar.add(menu);

		JMenuItem item = new JMenu("Import");
		menu.add(item);

		JMenuItem subItem = new JMenuItem("Rom File...");
		item.add(subItem);

		subItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				ImportDialog dlg = new ImportDialog(Editor.getInstance(),
						new RomImporter());

				dlg.setLocationRelativeTo(null);
				dlg.setSize(400, 300);
				dlg.setVisible(true);
			}
		});

		item = new JMenuItem("Exit");
		menu.add(item);

		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});

		menu = new JMenu("Editor");

		menuBar.add(menu);

		for (EditorMetaData emd : EditorMetaData.getAll()) {
			if (emd.isHidden()) {
				continue;
			}

			item = new EditorMenuItem(emd);
			item.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					EditorMenuItem item = (EditorMenuItem) arg0.getSource();
					setEditor(item.getMetaData());
				}
			});
			menu.add(item);
		}

		return menuBar;
	}

	void showMessageDialog(String msg) {
		JOptionPane.showMessageDialog(this, msg);
	}
}
