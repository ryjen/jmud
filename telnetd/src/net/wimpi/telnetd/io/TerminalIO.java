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

package net.wimpi.telnetd.io;

import java.io.IOException;

import net.wimpi.telnetd.io.terminal.Terminal;
import net.wimpi.telnetd.io.terminal.TerminalManager;
import net.wimpi.telnetd.net.Connection;
import net.wimpi.telnetd.net.ConnectionData;
import net.wimpi.telnetd.net.ConnectionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for Terminal specific I/O. It represents the layer between the
 * application layer and the generic telnet I/O. Terminal specific I/O is
 * achieved via pluggable terminal classes
 * 
 * @author Dieter Wimberger
 * @version 2.0 (16/07/2006)
 * @see net.wimpi.telnetd.io.TelnetIO
 * @see net.wimpi.telnetd.io.terminal.Terminal
 */
public class TerminalIO implements BasicTerminalIO {

	private static Logger log = LoggerFactory.getLogger(TerminalIO.class);
	private TelnetIO m_TelnetIO; // low level I/O

	private final Connection m_Connection; // the connection this instance is
											// working for
	private final ConnectionData m_ConnectionData; // holds data of the
													// connection
	private Terminal m_Terminal; // active terminal object

	// Members
	private boolean m_AcousticSignalling; // flag for accoustic signalling
	private boolean m_Autoflush; // flag for autoflushing mode
	private boolean m_ForceBold; // flag for forcing bold output
	private boolean m_LineWrapping;

	/**
	 * Terminal independent representation constants for terminal functions.
	 */
	public static final int[] HOME = { 0, 0 };

	public static final int IOERROR = -1; // IO error

	public static final int// Positioning 10xx
	UP = 1001; // one up

	public static final int DOWN = 1002; // one down

	public static final int RIGHT = 1003; // one left

	public static final int LEFT = 1004; // one right
	// HOME=1005, //Home cursor pos(0,0)

	public static final int// Functions 105x
	STORECURSOR = 1051; // store cursor position + attributes

	/*** End of Visible character I/O methods ******************************/

	public static final int RESTORECURSOR = 1052; // restore cursor + attributes

	public static final int// Erasing 11xx
	EEOL = 1100; // erase to end of line

	public static final int EBOL = 1101; // erase to beginning of line

	public static final int EEL = 1103; // erase entire line

	public static final int EEOS = 1104; // erase to end of screen

	public static final int EBOS = 1105; // erase to beginning of screen

	public static final int EES = 1106; // erase entire screen

	/*** End of Erase methods **********************************************/

	public static final int// Escape Sequence-ing 12xx
	ESCAPE = 1200; // Escape

	public static final int BYTEMISSING = 1201; // another byte needed

	public static final int UNRECOGNIZED = 1202; // escape match missed

	public static final int// Control Characters 13xx
	ENTER = 1300; // LF is ENTER at the moment

	public static final int TABULATOR = 1301; // Tabulator

	public static final int DELETE = 1302; // Delete

	public static final int BACKSPACE = 1303; // BACKSPACE

	public static final int COLORINIT = 1304; // Color inited

	public static final int HANDLED = 1305;

	/*** End of cursor related methods **************************************/

	public static final int LOGOUTREQUEST = 1306; // CTRL-D beim login

	/**
	 * Internal UpdateType Constants
	 */
	public static final int LineUpdate = 475, CharacterUpdate = 476,
			ScreenpartUpdate = 477;

	/**
	 * Internal BufferType Constants
	 */
	public static final int EditBuffer = 575, LineEditBuffer = 576;

	/**
	 * Network Virtual Terminal Specific Keys Thats what we have to offer at
	 * least.
	 */
	public static final int BEL = 7;

	public static final int BS = 8;

	public static final int DEL = 127;

	public static final int CR = 13;

	public static final int LF = 10;

	public static final int FCOLOR = 10001;

	public static final int BCOLOR = 10002;

	public static final int STYLE = 10003;

	public static final int RESET = 10004;

	/*** End of special terminal function methods ***************************/

	/************************************************************************
	 * Auxiliary I/O methods *
	 ************************************************************************/

	public static final int BOLD = 1;

	public static final int BOLD_OFF = 22;

	public static final int ITALIC = 3;

	public static final int ITALIC_OFF = 23;

	public static final int BLINK = 5;

	public static final int BLINK_OFF = 25;

	public static final int UNDERLINED = 4;

	public static final int UNDERLINED_OFF = 24;

	/*** End of Auxiliary I/O methods **************************************/

	/************************************************************************
	 * Terminal management specific methods *
	 ************************************************************************/

	public static final int DEVICERESET = 10005;

	public static final int LINEWRAP = 10006;

	public static final int NOLINEWRAP = 10007;

	/**
	 * Constructor of the TerminalIO class.
	 * 
	 * @param con
	 *            Connection the instance will be working for
	 */
	public TerminalIO(Connection con) {
		m_Connection = con;
		m_AcousticSignalling = true;
		m_Autoflush = true;

		// store the associated ConnectionData instance
		m_ConnectionData = m_Connection.getConnectionData();
		try {
			// create a new telnet io
			m_TelnetIO = new TelnetIO();
			m_TelnetIO.setConnection(con);
			m_TelnetIO.initIO();
		} catch (Exception ex) {
			// handle, at least log
		}

		// set default terminal
		try {
			setDefaultTerminal();
		} catch (Exception ex) {
			log.error("TerminalIO()", ex);
			throw new RuntimeException();
		}
	}// constructor

	@Override
	public int available() throws IOException {
		return this.m_TelnetIO.available();
	}

	/**
	 * Method to write the NVT defined BEL onto the stream. If signalling is
	 * off, the method simply returns, without any action.
	 */
	@Override
	public synchronized void bell() throws IOException {
		if (m_AcousticSignalling) {
			m_TelnetIO.write(BEL);
		}
		if (m_Autoflush) {
			flush();
		}
	}// bell

	@Override
	public synchronized void close() {
		m_TelnetIO.closeOutput();
		m_TelnetIO.closeInput();
	}// close

	/*** End of terminal management specific methods ***********************/

	/** Constants Declaration **********************************************/

	/************************************************************************
	 * Visible character I/O methods *
	 ************************************************************************/

	@Override
	public double compressionRatio() {
		return m_TelnetIO.compressionRatio();
	}

	/**
	 * EXPERIMENTAL, not defined in the interface.
	 */
	@Override
	public synchronized boolean defineScrollRegion(int topmargin,
			int bottommargin) throws IOException {
		if (m_Terminal.supportsScrolling()) {
			m_TelnetIO.write(m_Terminal.getScrollMarginsSequence(topmargin,
					bottommargin));
			flush();
			return true;
		} else {
			return false;
		}
	}// defineScrollRegion

	@Override
	public synchronized void eraseLine() throws IOException {
		doErase(EEL);
	}// eraseLine

	@Override
	public synchronized void eraseScreen() throws IOException {
		doErase(EES);
	}// eraseScreen

	@Override
	public synchronized void eraseToBeginOfLine() throws IOException {
		doErase(EBOL);
	}// eraseToBeginOfLine

	@Override
	public synchronized void eraseToBeginOfScreen() throws IOException {
		doErase(EBOS);
	}// eraseToBeginOfScreen

	/**
	 * *********************************************************************
	 * Erase methods *
	 * **********************************************************************
	 */

	@Override
	public synchronized void eraseToEndOfLine() throws IOException {
		doErase(EEOL);
	}// eraseToEndOfLine

	@Override
	public synchronized void eraseToEndOfScreen() throws IOException {
		doErase(EEOS);
	}// eraseToEndOfScreen

	/**
	 * Method to flush the Low-Level Buffer
	 */
	@Override
	public synchronized void flush() throws IOException {
		m_TelnetIO.flush();
	}// flush (implements the famous iToilet)

	@Override
	public synchronized void forceBold(boolean b) {
		m_ForceBold = b;
	}// forceBold

	/**
   *
   */
	@Override
	public int getColumns() {
		return m_ConnectionData.getTerminalColumns();
	}// getColumns

	/**
   *
   */
	@Override
	public int getRows() {
		return m_ConnectionData.getTerminalRows();
	}// getRows

	/**
	 * Accessor method to get the active terminal object
	 * 
	 * @return Object that implements Terminal
	 */
	@Override
	public Terminal getTerminal() {
		return m_Terminal;
	}// getTerminal

	@Override
	public synchronized void homeCursor() throws IOException {
		m_TelnetIO.write(m_Terminal.getCursorPositioningSequence(HOME));
		if (m_Autoflush) {
			flush();
		}
	}// homeCursor

	/**
	 * Accessor method for the autoflushing mechanism.
	 */
	@Override
	public boolean isAutoflushing() {
		return m_Autoflush;
	}// isAutoflushing

	@Override
	public boolean isCompressing() {
		return m_TelnetIO.isCompressing();
	}

	@Override
	public boolean isLineWrapping() {
		return m_LineWrapping;
	}//

	@Override
	public synchronized boolean isSignalling() {
		return m_AcousticSignalling;
	}// isAcousticSignalling

	/**
	 * Accessor Method for the terminal geometry changed flag
	 */
	public boolean isTerminalGeometryChanged() {
		return m_ConnectionData.isTerminalGeometryChanged();
	}// isTerminalGeometryChanged

	/**
	 * *********************************************************************
	 * Cursor related methods *
	 * **********************************************************************
	 */

	@Override
	public synchronized void moveCursor(int direction, int times)
			throws IOException {

		m_TelnetIO.write(m_Terminal.getCursorMoveSequence(direction, times));
		if (m_Autoflush) {
			flush();
		}
	}// moveCursor

	@Override
	public synchronized void moveDown(int times) throws IOException {
		moveCursor(DOWN, times);
	}// moveDown

	@Override
	public synchronized void moveLeft(int times) throws IOException {
		moveCursor(LEFT, times);
	}// moveLeft

	@Override
	public synchronized void moveRight(int times) throws IOException {
		moveCursor(RIGHT, times);
	}// moveRight

	@Override
	public synchronized void moveUp(int times) throws IOException {
		moveCursor(UP, times);
	}// moveUp

	/**
	 * Read a single character and take care for terminal function calls.
	 * 
	 * @return <ul>
	 *         <li>character read
	 *         <li>IOERROR in case of an error
	 *         <li>DELETE,BACKSPACE,TABULATOR,ESCAPE,COLORINIT,LOGOUTREQUEST
	 *         <li>UP,DOWN,LEFT,RIGHT
	 *         </ul>
	 */
	@Override
	public synchronized int read() throws IOException {
		int i = m_TelnetIO.read();
		// translate possible control sequences
		i = m_Terminal.translateControlCharacter(i);

		// catch & fire a logoutrequest event
		if (i == LOGOUTREQUEST) {
			m_Connection.processConnectionEvent(new ConnectionEvent(
					m_Connection, ConnectionEvent.CONNECTION_LOGOUTREQUEST));
			i = HANDLED;
		} else if (i > 256 && i == ESCAPE) {
			// translate an incoming escape sequence
			i = handleEscapeSequence(i);
		}

		// return i holding a char or a defined special key
		return i;
	}// read

	@Override
	public String readLine() throws IOException {
		return this.m_TelnetIO.readLine();
	}

	@Override
	public synchronized void resetAttributes() throws IOException {
		if (m_Terminal.supportsSGR()) {
			m_TelnetIO.write(m_Terminal.getGRSequence(RESET, 0));
		}
	}// resetGR

	@Override
	public synchronized void resetTerminal() throws IOException {
		m_TelnetIO.write(m_Terminal.getSpecialSequence(DEVICERESET));
	}

	@Override
	public synchronized void restoreCursor() throws IOException {
		m_TelnetIO.write(m_Terminal.getSpecialSequence(RESTORECURSOR));
	}// restore Cursor

	/**
	 * Mutator method for the autoflushing mechanism.
	 */
	@Override
	public synchronized void setAutoflushing(boolean b) {
		m_Autoflush = b;
	}// setAutoflushing

	@Override
	public synchronized void setBackgroundColor(int color) throws IOException {
		if (m_Terminal.supportsSGR()) {
			// this method adds the offset to the fg color by itself
			m_TelnetIO.write(m_Terminal.getGRSequence(BCOLOR, color + 10));
			if (m_Autoflush) {
				flush();
			}
		}
	}// setBackgroundColor

	@Override
	public synchronized void setBlink(boolean b) throws IOException {
		if (m_Terminal.supportsSGR()) {
			if (b) {
				m_TelnetIO.write(m_Terminal.getGRSequence(STYLE, BLINK));
			} else {
				m_TelnetIO.write(m_Terminal.getGRSequence(STYLE, BLINK_OFF));
			}
			if (m_Autoflush) {
				flush();
			}
		}
	}// setItalic

	@Override
	public synchronized void setBold(boolean b) throws IOException {
		if (m_Terminal.supportsSGR()) {
			if (b) {
				m_TelnetIO.write(m_Terminal.getGRSequence(STYLE, BOLD));
			} else {
				m_TelnetIO.write(m_Terminal.getGRSequence(STYLE, BOLD_OFF));
			}
			if (m_Autoflush) {
				flush();
			}
		}
	}// setBold

	@Override
	public synchronized void setCursor(int row, int col) throws IOException {
		int[] pos = new int[2];
		pos[0] = row;
		pos[1] = col;
		m_TelnetIO.write(m_Terminal.getCursorPositioningSequence(pos));
		if (m_Autoflush) {
			flush();
		}
	}// setCursor

	/**
	 * Sets the default terminal ,which will either be the negotiated one for
	 * the connection, or the systems default.
	 */
	@Override
	public void setDefaultTerminal() throws IOException {
		// set the terminal passing the negotiated string
		setTerminal(m_ConnectionData.getNegotiatedTerminalType());
	}// setDefaultTerminal

	@Override
	public void setEcho(boolean value) throws IOException {
		this.m_TelnetIO.setEcho(value);
	}

	@Override
	public synchronized void setForegroundColor(int color) throws IOException {
		if (m_Terminal.supportsSGR()) {
			m_TelnetIO.write(m_Terminal.getGRSequence(FCOLOR, color));
			if (m_Autoflush) {
				flush();
			}
		}
	}// setForegroundColor

	@Override
	public synchronized void setItalic(boolean b) throws IOException {
		if (m_Terminal.supportsSGR()) {
			if (b) {
				m_TelnetIO.write(m_Terminal.getGRSequence(STYLE, ITALIC));
			} else {
				m_TelnetIO.write(m_Terminal.getGRSequence(STYLE, ITALIC_OFF));
			}
			if (m_Autoflush) {
				flush();
			}
		}
	}// setItalic

	@Override
	public synchronized void setLinewrapping(boolean b) throws IOException {
		if (b && !m_LineWrapping) {
			m_TelnetIO.write(m_Terminal.getSpecialSequence(LINEWRAP));
			m_LineWrapping = true;
			return;
		}
		if (!b && m_LineWrapping) {
			m_TelnetIO.write(m_Terminal.getSpecialSequence(NOLINEWRAP));
			m_LineWrapping = false;
			return;
		}
	}// setLineWrapping

	/**
	 * *********************************************************************
	 * Special terminal function methods *
	 * **********************************************************************
	 */

	@Override
	public synchronized void setSignalling(boolean bool) {
		m_AcousticSignalling = bool;
	}// setAcousticSignalling

	/**
	 * Mutator method to set the active terminal object If the String does not
	 * name a terminal we support then the vt100 is the terminal of selection
	 * automatically.
	 * 
	 * @param terminalName
	 *            String that represents common terminal name
	 */
	@Override
	public void setTerminal(String terminalName) throws IOException {

		m_Terminal = TerminalManager.getReference().getTerminal(terminalName);
		// Terminal is set we init it....
		initTerminal();
		// debug message
		log.debug("Set terminal to " + m_Terminal.toString());
	}// setTerminal

	@Override
	public synchronized void setUnderlined(boolean b) throws IOException {
		if (m_Terminal.supportsSGR()) {
			if (b) {
				m_TelnetIO.write(m_Terminal.getGRSequence(STYLE, UNDERLINED));
			} else {
				m_TelnetIO.write(m_Terminal
						.getGRSequence(STYLE, UNDERLINED_OFF));
			}
			if (m_Autoflush) {
				flush();
			}

		}
	}// setUnderlined

	@Override
	public synchronized void storeCursor() throws IOException {
		m_TelnetIO.write(m_Terminal.getSpecialSequence(STORECURSOR));
	}// store Cursor

	@Override
	public synchronized void write(byte b) throws IOException {
		m_TelnetIO.write(b);
		if (m_Autoflush) {
			flush();
		}
	}// write

	@Override
	public synchronized void write(char ch) throws IOException {
		m_TelnetIO.write(ch);
		if (m_Autoflush) {
			flush();
		}
	}// write(char)

	@Override
	public synchronized void write(String str) throws IOException {
		if (m_ForceBold) {
			m_TelnetIO.write(m_Terminal.formatBold(str));
		} else {
			m_TelnetIO.write(m_Terminal.format(str));
		}
		if (m_Autoflush) {
			flush();
		}
	}// write(String)

	private synchronized void doErase(int funcConst) throws IOException {

		m_TelnetIO.write(m_Terminal.getEraseSequence(funcConst));
		if (m_Autoflush) {
			flush();
		}
	}// erase

	/**
	 * Method that parses forward for escape sequences
	 */
	private int handleEscapeSequence(int i) throws IOException {
		if (i == ESCAPE) {
			int[] bytebuf = new int[m_Terminal.getAtomicSequenceLength()];
			// fill atomic length
			// FIXME: ensure CAN, broken Escapes etc.
			for (int m = 0; m < bytebuf.length; m++) {
				bytebuf[m] = m_TelnetIO.read();
			}
			return m_Terminal.translateEscapeSequence(bytebuf);
		}
		if (i == BYTEMISSING) {
			// FIXME:longer escapes etc...
		}

		return HANDLED;
	}// handleEscapeSequence

	/**
	 * Terminal initialization
	 */
	private synchronized void initTerminal() throws IOException {
		m_TelnetIO.write(m_Terminal.getInitSequence());
		flush();
	}// initTerminal

	/** end Constants Declaration ******************************************/

}// class TerminalIO
