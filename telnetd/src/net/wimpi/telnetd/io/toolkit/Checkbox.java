//License
/***
 * Java TelnetD library (embeddable telnet daemon)
 * Copyright (c) 2000-2005 Dieter Wimberger 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the author nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *  
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER AND CONTRIBUTORS ``AS
 * IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 ***/

package net.wimpi.telnetd.io.toolkit;

import java.io.IOException;

import net.wimpi.telnetd.io.BasicTerminalIO;

/**
 * Class that implements a Checkbox component.
 * 
 * @author Dieter Wimberger
 * @version 2.0 (16/07/2006)
 */
public class Checkbox extends ActiveComponent {

	// Members
	private String m_Text = "";
	private boolean m_Selected = false;
	private String m_Mark;
	private String m_LeftBracket;
	private String m_RightBracket;

	public static final int SMALL_CHECKMARK = 10;

	public static final int LARGE_CHECKMARK = 11;

	public static final int SQUARED_BOXSTYLE = 1;

	public static final int ROUND_BOXSTYLE = 2;

	public static final int EDGED_BOXSTYLE = 3;

	private static final int SPACE = 32;

	/**
	 * Constructs a checkbox instance.
	 */
	public Checkbox(BasicTerminalIO io, String name) {
		super(io, name);
		setBoxStyle(SQUARED_BOXSTYLE);
		setMarkStyle(LARGE_CHECKMARK);
	}// constructor

	/**
	 * Method that draws the component.
	 */
	@Override
	public void draw() throws IOException {
		StringBuffer sbuf = new StringBuffer();
		sbuf.append(" "); // 1/1
		sbuf.append(m_LeftBracket); // 1/2
		if (m_Selected) {
			sbuf.append(m_Mark); // 1/3
		} else {
			sbuf.append(" ");
		}
		sbuf.append(m_RightBracket); // 1/4
		sbuf.append(" "); // 1/5
		sbuf.append(m_Text); // 1/5+myText.length

		if (m_Position != null) {
			m_IO.setCursor(m_Position.getRow(), m_Position.getColumn());
		}

		m_IO.write(sbuf.toString());
		m_IO.moveLeft(3 + m_Text.length()); // thats the mark position
		m_IO.flush();
	}// draw

	/**
	 * Accessor method for the state of the checkbox instance.
	 * 
	 * @return boolean that represents the state (true equals selected, false
	 *         equals not selected).
	 */
	public boolean isSelected() {
		return m_Selected;
	}// isSelected

	/**
	 * Method that will make the checkbox active, reading and processing input.
	 */
	@Override
	public void run() throws IOException {
		int in = 0;
		draw();
		m_IO.flush();
		do {
			// get next key
			in = m_IO.read();
			switch (in) {
			case SPACE:
				setSelected(!m_Selected); // toggle actual state, will redraw
											// mark
				break;
			case BasicTerminalIO.TABULATOR:
			case BasicTerminalIO.ENTER:
				in = -1;
				break;
			default:
				m_IO.bell();
			}
			m_IO.flush();
		} while (in != -1);
	}// run

	/**
	 * Mutator method for the boxstyle property of the checkbox. The *_BOXSTYLE
	 * constants should be passed as parameter.
	 * 
	 * @param style
	 *            int that represents one of the defined constants for
	 *            boxstyles.
	 */
	public void setBoxStyle(int style) {
		switch (style) {
		case ROUND_BOXSTYLE:
			m_LeftBracket = "(";
			m_RightBracket = ")";
			break;
		case EDGED_BOXSTYLE:
			m_LeftBracket = "<";
			m_RightBracket = ">";
			break;
		case SQUARED_BOXSTYLE:
		default:
			m_LeftBracket = "[";
			m_RightBracket = "]";
		}
	}// setBoxStyle

	/**
	 * Mutator method for the markstyle property of the checkbox. The
	 * *_CHECKMARK constants should be passed as parameter.
	 * 
	 * @param style
	 *            int that represents one of the defined constants for
	 *            checkmarks.
	 */
	public void setMarkStyle(int style) {
		switch (style) {
		case SMALL_CHECKMARK:
			m_Mark = "x";
			break;
		case LARGE_CHECKMARK:
		default:
			m_Mark = "X";
		}
	}// setMarkStyle

	/**
	 * Method to set the checkbox`s state.
	 * 
	 * @param b
	 *            boolean that represents the state (true equals selected, false
	 *            equals not selected).
	 */
	public void setSelected(boolean b) throws IOException {
		m_Selected = b;
		drawMark();
	}// setSelected

	/**
	 * Mutator method for the text property of the checkbox item. This text will
	 * be placed like a label, naturally you can leave this text empty and place
	 * a label if used in a form.
	 * 
	 * @param str
	 *            String that represents the text that will be displayed right
	 *            of the checkbox.
	 */
	public void setText(String str) {
		m_Text = str;
	}// setText

	private void drawMark() throws IOException {
		if (m_Position != null) {
			m_IO.storeCursor();
			m_IO.setCursor(m_Position.getRow(), m_Position.getColumn());
			m_IO.moveRight(2);
		}
		if (m_Selected) {
			m_IO.write(m_Mark);
		} else {
			m_IO.write(" ");
		}
		if (m_Position == null) {
			m_IO.moveLeft(1); // back to mark position
		} else {
			m_IO.restoreCursor();
		}
		m_IO.flush();
	}// drawMark

}// class Checkbox

