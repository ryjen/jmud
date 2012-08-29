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
package de.mud.jta;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Help display for JTA.
 * <P>
 * <B>Maintainer:</B> Matthias L. Jugel
 * 
 * @version $Id: Help.java 499 2005-09-29 08:24:54Z leo $
 * @author Matthias L. Jugel, Marcus Mei�ner
 */
public class Help {

	public static JEditorPane helpText = new JEditorPane();

	private static final Logger log = LoggerFactory.getLogger(Help.class);

	public static void show(Component parent, String url) {
		log.info("Help: " + url);

		try {
			helpText.setPage(Help.class.getResource(url));
		} catch (IOException e) {
			try {
				helpText.setPage(new URL(url));
			} catch (Exception ee) {
				log.error("unable to load help");
				JOptionPane
						.showMessageDialog(
								parent,
								"JTA - Telnet/SSH for the JAVA(tm) platform\r\n(c) 1996-2005 Matthias L. Jugel, Marcus Meißner\r\n\r\n",
								"jta", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
		}
		helpText.setEditable(false);

		JScrollPane scrollPane = new JScrollPane(helpText,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setSize(800, 600);

		final JFrame frame = new JFrame("HELP");
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(BorderLayout.CENTER, scrollPane);
		JPanel panel = new JPanel();
		JButton close = new JButton("Close Help");
		panel.add(close);
		close.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
			}
		});
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				frame.setVisible(false);
			}
		});
		frame.getContentPane().add(BorderLayout.SOUTH, close);
		frame.setSize(800, 600);
		frame.setVisible(true);
	}

}
