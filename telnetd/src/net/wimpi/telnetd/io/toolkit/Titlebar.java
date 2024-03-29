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
import net.wimpi.telnetd.io.terminal.ColorHelper;

/**
 * Class that implements a titlebar, for the top of the Terminal Window.
 * 
 * @author Dieter Wimberger
 * @version 2.0 (16/07/2006)
 */
public class Titlebar extends InertComponent {

	// Members
	private String m_Title;
	private int m_Align;
	private String m_BgColor;
	private String m_FgColor;

	public static final int ALIGN_RIGHT = 1;

	public static final int ALIGN_LEFT = 2;

	public static final int ALIGN_CENTER = 3;

	/**
	 * Constructor for a simple titlebar instance.
	 */
	public Titlebar(BasicTerminalIO io, String name) {
		super(io, name);
	}// constructor

	/**
	 * Method that draws the titlebar on the screen.
	 */
	@Override
	public void draw() throws IOException {
		m_IO.storeCursor();
		m_IO.homeCursor();
		m_IO.write(getBar());
		m_IO.restoreCursor();
	}// draw

	/**
	 * Accessor method for the titletext property of the titlebar component.
	 * 
	 * @return String that is displayed when the bar is drawn.
	 */
	public String getTitleText() {
		return m_Title;
	}// getTitleText

	/**
	 * Mutator method for the alignment property.
	 * 
	 * @param alignment
	 *            integer, valid if one of the ALIGN_* constants.
	 */
	public void setAlignment(int alignment) {
		if (alignment < 1 || alignment > 3) {
			alignment = 2; // left default
		} else {
			m_Align = alignment;
		}
	}// setAlignment

	/**
	 * Mutator method for the BackgroundColor property.
	 * 
	 * @param color
	 *            String, valid if it is a ColorHelper color constant.
	 */
	public void setBackgroundColor(String color) {
		m_BgColor = color;
	}// setBackgroundColor

	/**
	 * Mutator method for the SoregroundColor property.
	 * 
	 * @param color
	 *            String, valid if it is a ColorHelper color constant.
	 */
	public void setForegroundColor(String color) {
		m_FgColor = color;
	}// setForegroundColor

	// Constant definitions

	/**
	 * Mutator method for the titletext property of the titlebar component.
	 * 
	 * @param text
	 *            title String displayed in the titlebar.
	 */
	public void setTitleText(String text) {
		m_Title = text;
	}// setTitleText

	private void appendSpaceString(StringBuffer sbuf, int length) {
		for (int i = 0; i < length; i++) {
			sbuf.append(" ");
		}
	}// appendSpaceString

	/**
	 * Internal method that creates the true titlebarstring displayed on the
	 * terminal.
	 */
	private String getBar() {
		String ttitle = m_Title;
		// get actual screen width , remove the correction offset
		int width = m_IO.getColumns() - 1;
		// get actual titletext width
		int textwidth = (int) ColorHelper.getVisibleLength(m_Title);

		if (textwidth > width)
			ttitle = m_Title.substring(0, width);
		textwidth = (int) ColorHelper.getVisibleLength(ttitle);

		// prepare a buffer with enough space
		StringBuffer bar = new StringBuffer(width + textwidth);
		switch (m_Align) {
		case ALIGN_LEFT:
			bar.append(ttitle);
			appendSpaceString(bar, width - textwidth);
			break;
		case ALIGN_RIGHT:
			appendSpaceString(bar, width - textwidth);
			bar.append(ttitle);
			break;
		case ALIGN_CENTER:
			int left = ((width - textwidth != 0) ? ((width - textwidth) / 2)
					: (0));
			int right = width - textwidth - left;
			appendSpaceString(bar, left);
			bar.append(ttitle);
			appendSpaceString(bar, right);
		}
		// log.debug("Length of TB=" + bar.length());
		return ColorHelper.boldcolorizeText(bar.toString(), m_FgColor,
				m_BgColor);
	}// getBar

}// class Titlebar
