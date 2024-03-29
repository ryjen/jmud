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
import java.util.Vector;

import net.wimpi.telnetd.io.BasicTerminalIO;

/**
 * Class that implements an Editarea.
 * 
 * @author Dieter Wimberger
 * @version 2.0 (16/07/2006)
 */
public class Editarea extends ActiveComponent {

	private int m_RowCursor = 0;
	private int m_Rows = 0;
	private boolean m_Firstrun = true;
	private int m_FirstVisibleRow = 0;
	private String m_Hardwrap = "\n";
	private String m_Softwrap = " ";

	// Associations
	private Vector<Editline> lines;
	private Editline line;

	public Editarea(BasicTerminalIO io, String name, int rowheight, int maxrows) {
		super(io, name);
		lines = new Vector<Editline>();
		m_Rows = maxrows;
		m_Firstrun = true;
		m_FirstVisibleRow = 0;
		setDimension(new Dimension(m_IO.getColumns(), rowheight));
	}// constructor

	public void clear() throws IOException {

		// Buffers
		lines.removeAllElements();
		// Cursor
		m_RowCursor = 0;
		// Screen
		draw();

	}// clear

	@Override
	public void draw() throws IOException {
		if (m_Position != null) {
			m_IO.setCursor(m_Position.getRow(), m_Position.getColumn());
			int count = 0;
			for (int i = m_FirstVisibleRow; i < (m_FirstVisibleRow + m_Dim
					.getHeight()) && i < lines.size(); i++) {
				m_IO.eraseToEndOfLine();
				Editline lin = lines.elementAt(i);
				m_IO.write(lin.getValue());
				m_IO.moveLeft(lin.size());
				m_IO.moveDown(1);
				count++;
			}
			int corr = (m_FirstVisibleRow + count) - m_RowCursor;
			if (corr > 0) {
				m_IO.moveUp(corr);
			}
		}
		m_IO.flush();
	}// draw

	public String getHardwrapString() {
		return m_Hardwrap;
	}// getHardwrapString

	/**
	 * Accessor method for field buffer size.
	 * 
	 * @return int that represents the number of chars in the fields buffer.
	 */
	public int getSize() {
		int size = 0;
		// iterate over buffers and accumulate size
		// think of solution for hardwraps
		return size;
	}// getSize

	public String getSoftwrapString() {
		return m_Softwrap;
	}// getSoftwrapString

	public String getValue() {
		StringBuffer sbuf = new StringBuffer();
		// iterate over buffers and accumulate size
		Editline el = null;
		for (int i = 0; i < lines.size(); i++) {
			el = getLine(i);
			sbuf.append(el.getValue()).append(
					((el.isHardwrapped()) ? m_Hardwrap : m_Softwrap));
		}
		return sbuf.toString();
	}// getValue

	@Override
	public void run() throws IOException {
		boolean done = false;
		m_IO.setAutoflushing(false);
		// check flag
		if (m_Firstrun) {
			// reset flag
			m_Firstrun = false;
			// make a new editline
			line = createLine();
			appendLine(line);
		}

		do {
			// switch return of a line
			switch (line.run()) {
			case BasicTerminalIO.UP:
				if (m_RowCursor > 0) {
					if (m_FirstVisibleRow == m_RowCursor) {
						scrollUp();
					} else {
						cursorUp();
					}
				} else {
					m_IO.bell();
				}
				break;
			case BasicTerminalIO.DOWN:

				if (m_RowCursor < (lines.size() - 1)) {
					if (m_RowCursor == m_FirstVisibleRow
							+ (m_Dim.getHeight() - 1)) {
						scrollDown();
					} else {
						cursorDown();
					}
				} else {
					m_IO.bell();
				}
				break;
			case BasicTerminalIO.ENTER:
				/*
				 * System.out.println("DEBUG:firstVisibleRow:"+firstVisibleRow);
				 * System.out.println("DEBUG:rowCursor:"+rowCursor);
				 * System.out.println("DEBUG:lines:"+lines.size());
				 * System.out.println("DEBUG:maxRows:"+myRows);
				 * System.out.println("DEBUG:height:"+myDim.getHeight());
				 */
				// ensure exit on maxrows line
				if (m_RowCursor == (m_Rows - 1)) {
					done = true;
				} else {
					if (!hasLineSpace()) {
						m_IO.bell();
					} else {
						String wrap = line.getHardwrap();
						line.setHardwrapped(true);

						if (m_RowCursor == (lines.size() - 1)) {
							appendNewLine();
						} else {
							insertNewLine();
						}
						// cursor
						m_RowCursor++;
						// activate new row
						activateLine(m_RowCursor);
						// set value of new row
						try {
							line.setValue(wrap);
							line.setCursorPosition(0);
							m_IO.moveLeft(line.size());
						} catch (Exception ex) {
						}
					}
				}
				break;
			case BasicTerminalIO.TABULATOR:
				// set cursor to end of field?

				done = true;
				break;

			case BasicTerminalIO.LEFT:
				if (m_RowCursor > 0) {
					if (m_FirstVisibleRow == m_RowCursor) {
						scrollUp();
						line.setCursorPosition(line.size());
						m_IO.moveRight(line.size());
					} else {
						// Cursor
						m_RowCursor--;
						// buffer
						activateLine(m_RowCursor);
						line.setCursorPosition(line.size());

						// screen
						m_IO.moveUp(1);
						m_IO.moveRight(line.size());
					}
				} else {
					m_IO.bell();
				}
				break;
			case BasicTerminalIO.RIGHT:
				if (m_RowCursor < (lines.size() - 1)) {
					if (m_RowCursor == m_FirstVisibleRow
							+ (m_Dim.getHeight() - 1)) {
						line.setCursorPosition(0);
						m_IO.moveLeft(line.size());
						scrollDown();
					} else {
						// Cursor
						m_RowCursor++;
						// screen horizontal
						m_IO.moveLeft(line.size());
						// buffer
						activateLine(m_RowCursor);
						line.setCursorPosition(0);
						// screen
						m_IO.moveDown(1);
					}
				} else {
					m_IO.bell();
				}
				break;
			case BasicTerminalIO.BACKSPACE:
				if (m_RowCursor == 0 || line.size() != 0
						|| m_RowCursor == m_FirstVisibleRow) {
					m_IO.bell();
				} else {
					// take line from buffer
					// and draw update all below
					removeLine();
				}
				break;
			default:
				if (!hasLineSpace()) {
					m_IO.bell();
				} else {
					String wrap = line.getSoftwrap();
					// System.out.println("softwrap:"+wrap);
					line.setHardwrapped(false);

					if (m_RowCursor == (lines.size() - 1)) {
						appendNewLine();
					} else {
						insertNewLine();
					}
					// cursor
					m_RowCursor++;
					// activate new row
					activateLine(m_RowCursor);
					// set value of new row
					try {
						line.setValue(wrap);
						// getLine(rowCursor-1).getLastRelPos();
						// line.setCursorPosition(0);
						// myIO.moveLeft(line.size());
					} catch (Exception ex) {
					}
				}

			}
			m_IO.flush();
		} while (!done);
	}// run

	public void setHardwrapString(String str) {
		m_Hardwrap = str;
	}// setHardwrapString

	public void setSoftwrapString(String str) {
		m_Softwrap = str;
	}// setSoftwrapString

	public void setValue(String str) throws BufferOverflowException {

		// buffers
		lines.removeAllElements();
		// cursor
		m_RowCursor = 0;

	}// setValue

	private void activateLine(int pos) {
		line = getLine(pos);
	}// activateLine

	private void appendLine(Editline el) {
		lines.addElement(el);
	}// appendLine

	private void appendNewLine() throws IOException {
		// System.out.println("Debug:appendline");
		// buffer
		appendLine(createLine());

		if (m_RowCursor == m_FirstVisibleRow + (m_Dim.getHeight() - 1)) {
			// System.out.println("Debug:appendline:scroll");
			// this will "scroll"
			m_FirstVisibleRow++;
			// System.out.println("Debug:appendline:scroll:firstvis:"+firstVisibleRow);
			// System.out.println("Debug:appendline:scroll:rowCursor:"+rowCursor);
			// System.out.println("Debug:appendline:scroll:movevert:"+(myDim.getHeight()-1));
			// vertical
			m_IO.moveUp(m_Dim.getHeight() - 1);
			m_IO.moveLeft(line.getCursorPosition());
			line.getCursorPosition();
			for (int i = m_FirstVisibleRow; i < (m_FirstVisibleRow + m_Dim
					.getHeight()); i++) {
				// System.out.println("Debug:appendline:scroll:line:"+i);
				Editline lin = lines.elementAt(i);
				m_IO.eraseToEndOfLine();
				m_IO.write(lin.getValue());
				m_IO.moveLeft(lin.size());
				m_IO.moveDown(1);
			}
			// correct the move to down in last place
			m_IO.moveUp(1);

		} else {
			// System.out.println("Debug:appendline:NOscroll");
			// this wont need a scroll redraw
			m_IO.moveLeft(line.getCursorPosition());
			m_IO.moveDown(1);
		}
	}// appendNewLine

	private Editline createLine() {
		return new Editline(m_IO);
	}// newLine

	private void cursorDown() throws IOException {
		// System.out.println("Debug:cursor:down");
		int horizontalpos = line.getCursorPosition();
		// Cursor
		m_RowCursor++;
		// buffer
		activateLine(m_RowCursor);
		line.setCursorPosition(horizontalpos);
		// screen
		m_IO.moveDown(1);
		if (horizontalpos > line.getCursorPosition()) {
			m_IO.moveLeft(horizontalpos - line.getCursorPosition());
		}
	}// cursorDown

	private void cursorUp() throws IOException {
		// System.out.println("Debug:cursor:up");

		int horizontalpos = line.getCursorPosition();
		// Cursor
		m_RowCursor--;
		// buffer
		activateLine(m_RowCursor);
		line.setCursorPosition(horizontalpos);
		// screen
		// vertical
		m_IO.moveUp(1);
		// horizontal
		if (horizontalpos > line.getCursorPosition()) {
			m_IO.moveLeft(horizontalpos - line.getCursorPosition());
		}
	}// cursorUp

	private void deleteLine(int pos) {
		lines.removeElementAt(pos);
	}// deleteLine

	private Editline getLine(int pos) {
		return lines.elementAt(pos);
	}// getLine

	private boolean hasLineSpace() {
		return (lines.size() < m_Rows);
	}// hasLineSpace

	private void insertLine(int pos, Editline el) {
		lines.insertElementAt(el, pos);
	}// insertLine

	private void insertNewLine() throws IOException {
		// System.out.println("Debug:insertline:");
		// buffer
		insertLine(m_RowCursor + 1, createLine());

		if (m_RowCursor == m_FirstVisibleRow + (m_Dim.getHeight() - 1)) {
			// System.out.println("Debug:insertline:scroll");
			// this will "scroll"
			m_FirstVisibleRow++;
			// System.out.println("Debug:insertline:scroll:firstvis:"+firstVisibleRow);
			// System.out.println("Debug:appendline:scroll:rowCursor:"+rowCursor);
			// System.out.println("Debug:appendline:scroll:movevert:"+(myDim.getHeight()-1));
			// vertical
			m_IO.moveUp(m_Dim.getHeight() - 1);
			// content
			int lasthorizontal = line.getCursorPosition();
			for (int i = m_FirstVisibleRow; i < (m_FirstVisibleRow + m_Dim
					.getHeight()); i++) {
				// System.out.println("Debug:appendline:scroll:line:"+i);
				m_IO.moveLeft(lasthorizontal);
				Editline lin = lines.elementAt(i);
				lasthorizontal = lin.size();
				m_IO.eraseToEndOfLine();
				m_IO.write(lin.getValue());
				m_IO.moveDown(1);

			}
			// correct the move to down in last place
			m_IO.moveUp(1);

		} else {
			// System.out.println("Debug:insertline:NOscroll");
			// we have to redraw any line below rowCursor+1 anyway
			m_IO.moveDown(1);
			m_IO.moveLeft(line.getCursorPosition());

			int count = 0;
			for (int i = m_RowCursor + 1; i < (m_FirstVisibleRow + m_Dim
					.getHeight()) && i < lines.size(); i++) {
				// System.out.println("Debug:insertline:redrawing line:"+i);
				m_IO.eraseToEndOfLine();
				Editline lin = lines.elementAt(i);
				m_IO.write(lin.getValue());
				m_IO.moveLeft(lin.size());
				m_IO.moveDown(1);
				count++;
			}
			m_IO.moveUp(count);
		}

	}// insertNewLine

	private void removeLine() throws IOException {

		// buffer
		deleteLine(m_RowCursor);
		activateLine(m_RowCursor - 1);
		// Cursor
		m_RowCursor--;

		int count = 0;
		for (int i = m_RowCursor + 1; i < (m_FirstVisibleRow + m_Dim
				.getHeight()); i++) {
			if (i < lines.size()) {
				// System.out.println("Debug:removeline:redrawing line:"+i);
				m_IO.eraseToEndOfLine();
				Editline lin = lines.elementAt(i);
				m_IO.write(lin.getValue());
				m_IO.moveLeft(lin.size());
				m_IO.moveDown(1);
				count++;
			} else {
				m_IO.eraseToEndOfLine();
				m_IO.moveDown(1);
				count++;
			}
		}
		// cursor readjustment
		// vertical
		m_IO.moveUp(count + 1);
		// horizontal

		line.setCursorPosition(line.size());
		m_IO.moveRight(line.size());
	}// removeLine

	private void scrollDown() throws IOException {
		// System.out.println("Debug:scrolling:down");
		int horizontalpos = line.getCursorPosition();

		// Cursors
		m_FirstVisibleRow++;
		m_RowCursor++;

		// buffer
		activateLine(m_RowCursor);
		line.setCursorPosition(horizontalpos);

		// screen
		// vertical:
		m_IO.moveUp(m_Dim.getHeight() - 1);
		// content:
		int lasthorizontal = horizontalpos;
		for (int i = m_FirstVisibleRow; i < (m_FirstVisibleRow + m_Dim
				.getHeight()); i++) {
			// System.out.println("Debug:scrolling:up:drawing line "+i);
			m_IO.moveLeft(lasthorizontal);
			Editline lin = lines.elementAt(i);
			lasthorizontal = lin.size();

			m_IO.eraseToEndOfLine();
			m_IO.write(lin.getValue());
			m_IO.moveDown(1);
		}
		// correct move down and last write
		m_IO.moveUp(1);
		// horizontal:
		if (lasthorizontal > horizontalpos) {
			m_IO.moveLeft(lasthorizontal - horizontalpos);
		} else if (lasthorizontal < horizontalpos) {
			m_IO.moveRight(horizontalpos - lasthorizontal);
		}

		if (horizontalpos > line.getCursorPosition()) {
			m_IO.moveLeft(horizontalpos - line.getCursorPosition());
		}
	}// scrollDown

	private void scrollUp() throws IOException {

		int horizontalpos = line.getCursorPosition();
		// System.out.println("Debug:scrolling:up:horpos:"+horizontalpos);
		// System.out.println("Debug:scrolling:up");
		// Cursors
		m_FirstVisibleRow--;
		m_RowCursor--;

		// buffer
		activateLine(m_RowCursor);
		line.setCursorPosition(horizontalpos);

		// screen
		// horizontal

		// content:
		int lasthorizontal = horizontalpos;
		int count = 0;
		for (int i = m_FirstVisibleRow; i < (m_FirstVisibleRow + m_Dim
				.getHeight()) && i < lines.size(); i++) {
			// System.out.println("Debug:scrolling:up:drawing line "+i);
			m_IO.moveLeft(lasthorizontal);
			Editline lin = lines.elementAt(i);
			lasthorizontal = lin.size();
			m_IO.eraseToEndOfLine();
			m_IO.write(lin.getValue());
			m_IO.moveDown(1);
			count++;
		}
		// vertical:
		m_IO.moveUp(count);
		// horizontal:
		if (lasthorizontal > horizontalpos) {
			m_IO.moveLeft(lasthorizontal - horizontalpos);
		} else if (lasthorizontal < horizontalpos) {
			m_IO.moveRight(horizontalpos - lasthorizontal);
		}

		if (horizontalpos > line.getCursorPosition()) {
			m_IO.moveLeft(horizontalpos - line.getCursorPosition());
		}
	}// scrollUp

}// class Editarea
