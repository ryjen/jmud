/*
 * This file is part of "JTA - Telnet/SSH for the JAVA(tm) platform".
 *
 * (c) Matthias L. Jugel, Marcus Meißner 1996-2005. All Rights Reserved.
 *
 * Please visit http://javatelnet.org/ for updates and contact.
 *
 * --LICENSE NOTICE--
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 * --LICENSE NOTICE--
 *
 */

package de.mud.jta.plugin;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mud.jta.FilterPlugin;
import de.mud.jta.Plugin;
import de.mud.jta.PluginBus;
import de.mud.jta.PluginConfig;
import de.mud.jta.VisualPlugin;
import de.mud.jta.event.ConfigurationListener;

/**
 * A capture plugin that captures data and stores it in a defined location. The
 * location is specified as a plugin configuration option Capture.url and can be
 * used in conjunction with the UploadServlet from the tools directory.
 * <P>
 * Parametrize the plugin carefully:<br>
 * <b>Capture.url</b> should contain a unique URL can may have parameters for
 * identifying the upload.<br>
 * <i>Example:</i> http://mg.mud.de/servlet/UpladServlet?id=12345
 * <p>
 * The actually captured data will be appended as the parameter <b>content</b>.
 * <P>
 * <B>Maintainer:</B> Matthias L. Jugel
 * 
 * @version $Id: Capture.java 499 2005-09-29 08:24:54Z leo $
 * @author Matthias L. Jugel, Marcus Mei�ner
 */
public class Capture extends Plugin implements FilterPlugin, VisualPlugin,
		ActionListener {

	// this enables or disables the compilation of menu entries
	private final static boolean personalJava = false;

	// for debugging output
	private final static Logger log = LoggerFactory.getLogger(Capture.class);

	/** The remote storage URL */
	protected Hashtable<String, String> remoteUrlList = new Hashtable<String, String>();

	/** The plugin menu */
	protected JMenu menu;
	protected JDialog errorDialog;
	protected JDialog fileDialog;
	protected JDialog doneDialog;

	/** Whether the capture is currently enabled or not */
	protected boolean captureEnabled = false;

	// menu entries and the viewing frame/textarea
	private JMenuItem start, stop, clear;
	private JFrame frame;
	private JTextArea textArea;
	private JTextField fileName;

	// this is where we get the data from (left side in plugins list)
	protected FilterPlugin source;

	/**
	 * Initialize the Capture plugin. This sets up the menu entries and
	 * registers the plugin on the bus.
	 */
	public Capture(final PluginBus bus, final String id) {
		super(bus, id);

		if (!personalJava) {

			// set up viewing frame
			frame = new JFrame("Java Telnet Applet: Captured Text");
			frame.getContentPane().setLayout(new BorderLayout());
			frame.getContentPane().add(textArea = new JTextArea(24, 80),
					BorderLayout.CENTER);
			textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					frame.setVisible(false);
				}
			});
			frame.pack();

			// an error dialogue, in case the upload fails
			errorDialog = new JDialog(frame, "Error", true);
			errorDialog.getContentPane().setLayout(new BorderLayout());
			errorDialog.getContentPane().add(
					new JLabel("Cannot store data on remote server!"),
					BorderLayout.NORTH);
			JPanel panel = new JPanel();
			JButton button = new JButton("Close Dialog");
			panel.add(button);
			errorDialog.getContentPane().add(panel, BorderLayout.SOUTH);
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					errorDialog.setVisible(false);
				}
			});

			// an error dialogue, in case the upload fails
			doneDialog = new JDialog(frame, "Success", true);
			doneDialog.getContentPane().setLayout(new BorderLayout());
			doneDialog.getContentPane().add(
					new JLabel("Successfully saved data!"), BorderLayout.NORTH);
			panel = new JPanel();
			button = new JButton("Close Dialog");
			panel.add(button);
			doneDialog.getContentPane().add(panel, BorderLayout.SOUTH);
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					errorDialog.setVisible(false);
				}
			});

			fileDialog = new JDialog(frame, "Enter File Name", true);
			fileDialog.getContentPane().setLayout(new BorderLayout());
			ActionListener saveFileListener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String params = remoteUrlList.get("URL.file.params.orig");
					params = params == null ? "" : params + "&";
					try {
						remoteUrlList.put(
								"URL.file.params",
								params
										+ "file="
										+ URLEncoder.encode(fileName.getText(),
												"UTF-8"));
						saveFile("URL.file");
						fileDialog.setVisible(false);
					} catch (UnsupportedEncodingException ex) {
						log.error(ex.getMessage());
					}
				}
			};
			panel = new JPanel();
			panel.add(new JLabel("File Name: "));
			panel.add(fileName = new JTextField(30));
			fileName.addActionListener(saveFileListener);
			fileDialog.getContentPane().add(panel, BorderLayout.CENTER);
			panel = new JPanel();
			panel.add(button = new JButton("Cancel"));
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					fileDialog.setVisible(false);
				}
			});
			panel.add(button = new JButton("Save File"));
			button.addActionListener(saveFileListener);
			fileDialog.getContentPane().add(panel, BorderLayout.SOUTH);
			fileDialog.pack();

			// set up menu entries
			menu = new JMenu("Capture");
			start = new JMenuItem("Start");
			start.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					log.debug("start capturing");
					captureEnabled = true;
					start.setEnabled(false);
					stop.setEnabled(true);
				}
			});
			menu.add(start);

			stop = new JMenuItem("Stop");
			stop.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					log.debug("stop capturing");
					captureEnabled = false;
					start.setEnabled(true);
					stop.setEnabled(false);

				}
			});
			stop.setEnabled(false);
			menu.add(stop);

			clear = new JMenuItem("Clear");
			clear.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					log.debug("cleared captured text");
					textArea.setText("");
				}
			});
			menu.add(clear);
			menu.addSeparator();

			JMenuItem view = new JMenuItem("View/Hide Text");
			view.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					log.debug("view/hide text: " + frame.isVisible());
					if (frame.isVisible()) {
						frame.setVisible(false);
					} else {
						frame.setVisible(true);
					}
				}
			});
			menu.add(view);

		} // !personalJava

		// configure the remote URL
		bus.registerPluginListener(new ConfigurationListener() {
			@Override
			public void setConfiguration(PluginConfig config) {
				String tmp;

				JMenuItem save = new JMenuItem("Save As File");
				menu.add(save);

				if ((tmp = config.getProperty("Capture", id, "file.url")) != null) {
					remoteUrlList.put("URL.file", /* new URL(tmp) */tmp);
					if ((tmp = config.getProperty("Capture", id, "file.params")) != null) {
						remoteUrlList.put("URL.file.params.orig", tmp);
					}

					save.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							fileDialog.setVisible(true);
						}
					});
					save.setActionCommand("URL.file");

				} else {
					save.setEnabled(false);
				}

				int i = 1;
				while ((tmp = config.getProperty("Capture", id, i + ".url")) != null) {
					try {
						String urlID = "URL." + i;
						URL remoteURL = new URL(tmp);
						remoteUrlList.put(urlID, tmp);
						if ((tmp = config.getProperty("Capture", id, i
								+ ".params")) != null) {
							remoteUrlList.put(urlID + ".params", tmp);
						}
						// use name if applicable or URL
						if ((tmp = config.getProperty("Capture", id, i
								+ ".name")) != null) {
							save = new JMenuItem("Save As " + tmp);
						} else {
							save = new JMenuItem("Save As "
									+ remoteURL.toString());
						}
						// enable menu entry
						save.setEnabled(true);
						save.addActionListener(Capture.this);
						save.setActionCommand(urlID);
						menu.add(save);
						// count up
						i++;
					} catch (MalformedURLException e) {
						log.error("capture url invalid: " + e);
					}
				}
			}
		});

		if (!personalJava) {
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String urlID = e.getActionCommand();
		log.debug("storing text: " + urlID + ": " + remoteUrlList.get(urlID));
		saveFile(urlID);
	}

	@Override
	public FilterPlugin getFilterSource() {
		return source;
	}

	/**
	 * The Capture menu for the menu bar as configured in the constructor.
	 * 
	 * @return the drop down menu
	 */
	@Override
	public JMenu getPluginMenu() {
		return menu;
	}

	/**
	 * The Capture plugin has no visual component that is embedded in the JTA
	 * main frame, so this returns null.
	 * 
	 * @return always null
	 */
	@Override
	public JComponent getPluginVisual() {
		return null;
	}

	/**
	 * Read data from the left side plugin, capture the content and pass it on
	 * to the next plugin which called this method.
	 * 
	 * @param b
	 *            the buffer to store data into
	 */
	@Override
	public int read(byte[] b) throws IOException {
		int size = source.read(b);
		if (captureEnabled && size > 0) {
			String tmp = new String(b, 0, size);
			textArea.append(tmp);
		}
		return size;
	}

	/**
	 * The filter source is the plugin where Capture is connected to. In the
	 * list of plugins this is the one to the left.
	 * 
	 * @param source
	 *            the next plugin
	 */
	@Override
	public void setFilterSource(FilterPlugin source) {
		log.debug("connected to: " + source);
		this.source = source;
	}

	/**
	 * Write data to the backend but also append it to the capture buffer.
	 * 
	 * @param b
	 *            the buffer with data to write
	 */
	@Override
	public void write(byte[] b) throws IOException {
		if (captureEnabled) {
			textArea.append(new String(b));
		}
		source.write(b);
	}

	private void saveFile(String urlID) {
		String urlname = remoteUrlList.get(urlID);
		try {
			URL url = new URL(urlname);
			URLConnection urlConnection = url.openConnection();
			DataOutputStream out;
			BufferedReader in;

			// Let the RTS know that we want to do output.
			urlConnection.setDoInput(true);
			// Let the RTS know that we want to do output.
			urlConnection.setDoOutput(true);
			// No caching, we want the real thing.
			urlConnection.setUseCaches(false);
			// Specify the content type.
			urlConnection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");

			// retrieve extra arguments
			// Send POST output.
			// send the data to the url receiver ...
			out = new DataOutputStream(urlConnection.getOutputStream());
			String content = remoteUrlList.get(urlID + ".params");
			content = (content == null ? "" : content + "&") + "content="
					+ URLEncoder.encode(textArea.getText(), "UTF-8");
			log.debug(content);
			out.writeBytes(content);
			out.flush();
			out.close();

			// retrieve response from the remote host and display it.
			log.debug("reading response");
			in = new BufferedReader(new InputStreamReader(
					urlConnection.getInputStream()));
			String str;
			while (null != ((str = in.readLine()))) {
				log.info(str);
			}
			in.close();

			doneDialog.pack();
			doneDialog.setVisible(true);

		} catch (IOException ioe) {
			log.error("cannot store text on remote server: " + urlname);
			ioe.printStackTrace();
			JTextArea errorMsg = new JTextArea(ioe.toString(), 5, 30);
			errorMsg.setEditable(false);
			errorDialog.add(errorMsg, BorderLayout.CENTER);
			errorDialog.pack();
			errorDialog.setVisible(true);
		}
		log.debug("storage complete: " + urlname);
	}

}
