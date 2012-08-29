package net.arg3.jmud.importer;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.SwingWorker.StateValue;

public class ImportDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	JProgressBar progressBar;
	JTextArea txtArea;
	AbstractImporter importer;
	JButton btnOpen;
	JPanel topPanel;

	JButton btnCommit;

	public ImportDialog(JFrame frame, AbstractImporter importer) {
		super(frame, true);
		this.importer = importer;
		initialize();
	}

	private void initialize() {
		progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setVisible(false);

		topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		btnOpen = new JButton("Open");
		btnOpen.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					loadFile();
				} catch (Exception ex) {
					txtArea.append(ex.getMessage());
				}
			}

		});
		topPanel.add(btnOpen);

		btnCommit = new JButton("Commit");

		btnCommit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				btnCommit.setVisible(false);
				progressBar.setVisible(true);

				SwingWorker<Integer, String> worker = new SwingWorker<Integer, String>() {

					@Override
					protected Integer doInBackground() throws Exception {
						return importer.commit();
					}

				};

				worker.addPropertyChangeListener(new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						if (evt.getNewValue() == StateValue.DONE) {
							txtArea.append("Commit Complete.\n\r");
							progressBar.setVisible(false);

							btnCommit.setVisible(false);
						}
					}

				});

				txtArea.append("Starting commit...\n\r");
				worker.execute();

			}

		});
		btnCommit.setVisible(false);
		topPanel.add(btnCommit);

		importer.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getNewValue() == StateValue.DONE) {
					txtArea.append("Load Complete.\r\n");
					progressBar.setVisible(false);

					btnCommit.setVisible(true);
				}
			}

		});

		topPanel.add(progressBar, BorderLayout.NORTH);

		add(topPanel, BorderLayout.NORTH);
		txtArea = new JTextArea();
		importer.setOutput(txtArea);
		JScrollPane scroller = new JScrollPane(txtArea);
		add(scroller, BorderLayout.CENTER);

		loadFile();
	}

	private void loadFile() {
		File file;

		JFileChooser fc = new JFileChooser();
		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			file = fc.getSelectedFile();
		} else {
			return;
		}

		try {
			importer.setReader(new FileReader(file.getPath()));
		} catch (Exception e) {
			txtArea.append("Could not load file!\r\n\r\n" + e.toString());
			return;
		}

		progressBar.setVisible(true);
		importer.execute();
	}
}
