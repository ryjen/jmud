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
import java.io.InputStream;
import java.io.StringReader;
import java.util.Vector;

import net.wimpi.telnetd.io.BasicTerminalIO;
import net.wimpi.telnetd.io.terminal.ColorHelper;

/**
 * Class implementing a pager.
 * 
 * @author Dieter Wimberger
 * @version 2.0 (16/07/2006);
 */
public class Pager {

	// Associations
	private BasicTerminalIO m_IO;
	// Members
	private StringReader m_Source;
	private String m_Prompt;
	private int m_StopKey;
	private Vector<String> m_Chunks;
	private int m_ChunkPos;
	private int m_LastNewChunk;
	private boolean m_EOS;
	private int m_TermRows;
	private int m_TermCols;
	private boolean m_NoPrompt;
	private boolean m_ShowPos;
	private Statusbar m_Status;

	/**
	 * Constant definitions
	 */
	private static final char DEFAULT_STOPKEY = 's';

	private static final String DEFAULT_PROMPT = "[Cursor Up,Cursor Down,Space,s (stop)] ";

	private static final int SPACE = 32;

	/**
	 * Constructor method
	 */
	public Pager(BasicTerminalIO io) {
		m_IO = io;
		setPrompt(DEFAULT_PROMPT);
		setStopKey(DEFAULT_STOPKEY);
		m_TermRows = m_IO.getRows();
		m_TermCols = m_IO.getColumns();
		m_Status = new Statusbar(m_IO, "Pager Status");
		m_Status.setAlignment(Statusbar.ALIGN_LEFT);
	}// constructor

	/**
	 * Constructor method for a pager with a prompt set and a default stop key.
	 * 
	 * @param prompt
	 *            String that represents the paging prompt.
	 * @param stopKey
	 *            String that represents the stop key.
	 */
	public Pager(BasicTerminalIO io, String prompt, char stopKey) {
		m_IO = io;
		setPrompt(prompt);
		m_StopKey = stopKey;
		m_TermRows = m_IO.getRows();
		m_TermCols = m_IO.getColumns();
		m_Status.setAlignment(Statusbar.ALIGN_LEFT);
	}// constructor

	/**
	 * Method that pages text read from an InputStream.
	 * 
	 * @param in
	 *            InputStream representing a source for paging.
	 */
	public void page(InputStream in) throws IOException {

		// buffer prepared for about 3k
		StringBuffer inbuf = new StringBuffer(3060);

		// int buffering read
		int b = 0;

		while (b != -1) {
			b = in.read();
			if (b != -1) {
				inbuf.append((char) b);
			}
		}

		// now page the string
		page(inbuf.toString());
	}// page(InputStream)

	/**
	 * Method that pages the String to the client terminal, being aware of its
	 * geometry, and its geometry changes.
	 * 
	 * @param str
	 *            String to be paged.
	 */
	public void page(String str) throws IOException {
		terminalGeometryChanged();
		boolean autoflush = m_IO.isAutoflushing();
		m_IO.setAutoflushing(true);
		// store raw
		m_Source = new StringReader(str);
		// do renderchunks
		m_ChunkPos = 0;
		m_LastNewChunk = 0;
		m_EOS = false;
		m_NoPrompt = false;

		renderChunks();
		if (m_Chunks.size() == 1) {
			m_IO.write(m_Chunks.elementAt(0));
		} else {
			m_IO.homeCursor();
			m_IO.eraseScreen();
			m_IO.write(m_Chunks.elementAt(m_ChunkPos));
			updateStatus();
			m_Status.draw();
			// storage for read byte
			int in = 0;
			do {
				m_NoPrompt = false;

				// get next key
				in = m_IO.read();
				if (terminalGeometryChanged()) {
					try {
						m_Source.reset();
					} catch (Exception ex) {
					}
					renderChunks();
					m_ChunkPos = 0;
					m_LastNewChunk = 0;
					m_EOS = false;
					m_NoPrompt = false;
					m_IO.homeCursor();
					m_IO.eraseScreen();
					m_IO.write(m_Chunks.elementAt(m_ChunkPos));
					updateStatus();
					m_Status.draw();
					continue;
				}
				switch (in) {
				case BasicTerminalIO.UP:
					drawPreviousPage();
					break;
				case BasicTerminalIO.DOWN:
					drawNextPage();
					break;
				case SPACE:
					drawNextPage();
					break;
				default:
					// test for stopkey, cant be switched because not constant
					if (in == m_StopKey) {
						// flag loop over
						in = -1;
						continue; // so that we omit prompt and return
					} else {
						m_IO.bell();
						continue;
					}
				}
				if (m_EOS) {
					in = -1;
					continue;
				}
				// prompt
				if (!m_NoPrompt) {
					updateStatus();
					m_Status.draw();
				}
			} while (in != -1);
			m_IO.eraseToEndOfLine();

		}
		m_IO.write("\n");
		m_Source.close();
		m_IO.setAutoflushing(autoflush);
	}// page(String)

	/**
	 * Mutator method for the pagers prompt.
	 * 
	 * @param prompt
	 *            String that represents the new promptkey.
	 */
	public void setPrompt(String prompt) {
		m_Prompt = prompt;
	}// setPrompt

	/**
	 * Method to make the pager add pager postion to the prompt.
	 */
	public void setShowPosition(boolean b) {
		m_ShowPos = b;
	}// setShowPosition

	/**
	 * Mutator method for the pagers stop key.
	 * 
	 * @param key
	 *            char that represents the new stop key.
	 */
	public void setStopKey(char key) {
		m_StopKey = key;
	}// setStopKey

	private void drawNewPage() throws IOException {

		// increase counters
		m_ChunkPos++;
		m_LastNewChunk++;
		// System.out.println("drawing new page chunkpos="+chunkpos+" lastnewchunk="+lastnewchunk);
		if (m_ChunkPos < m_Chunks.size()) {
			m_IO.homeCursor();
			m_IO.eraseScreen();
			m_IO.write(m_Chunks.elementAt(m_ChunkPos));
			// if(chunkpos==chunks.size()-1) {
			// eos=true;
			// noprompt=true;
			// }
		} else {
			// flag end
			m_EOS = true;
			m_NoPrompt = true;
		}
	}// drawNewPage

	private void drawNextPage() throws IOException {
		// System.out.println("drawing next page");
		if (m_ChunkPos == m_LastNewChunk) {
			drawNewPage();
		} else {
			m_IO.homeCursor();
			m_IO.eraseScreen();
			m_IO.write(m_Chunks.elementAt(++m_ChunkPos));
		}
	}// drawNextPage

	private void drawPreviousPage() throws IOException {
		// System.out.println("drawing previous page");
		if (m_ChunkPos > 0) {
			m_IO.homeCursor();
			m_IO.eraseScreen();
			m_IO.write(m_Chunks.elementAt(--m_ChunkPos));
		} else {
			m_IO.bell();
			m_NoPrompt = true;
		}
	}// drawPreviousPage

	private void renderChunks() {
		// System.out.println("Rendering Chunks");
		// System.out.println("Rows = " + m_TermRows + "::Columns = " +
		// m_TermCols);
		// prepare with 10 as default, shouldnt be much larger normally
		m_Chunks = new Vector<String>(20);
		// prepare a buffer the size of cols + security span
		StringBuffer sbuf = new StringBuffer((m_TermCols + 25) * 25);
		int b = 0;
		int cols = 0;
		int rows = 0;
		boolean colorskip = false;

		do {
			// System.out.println("LoopRows=" + rows + "LoopColumns=" + cols);
			// check rows to advance chunks
			if (rows == m_TermRows - 1) {
				// System.out.println("New page");
				// add chunk to vector
				m_Chunks.addElement(sbuf.toString());
				// replace for new buffer
				sbuf = new StringBuffer((m_TermCols + 25) * 25);
				// reset counters
				cols = 0;
				rows = 0;
			}
			// try read next byte
			try {
				b = m_Source.read();
			} catch (IOException ioex) {
				b = -1;
			}
			if (b == -1) {
				m_Chunks.addElement(sbuf.toString());
				continue; // will end the loop
			} else if (b == ColorHelper.MARKER_CODE || colorskip) {
				// add it, flag right for next byte and skip counting
				sbuf.append((char) b);
				if (!colorskip) {
					colorskip = true;
				} else {
					colorskip = false;
				}
				continue;
			} else if (b == 13) {
				// advance a row
				rows++;
				// reset cols
				cols = 0;
				// append a newline char
				sbuf.append("\n");
				// skip newline if given
				try {
					b = m_Source.read();
				} catch (IOException ex) {
					b = -1;
				}
				if (b == -1) {
					continue;
				}
				if (b != 10) {
					sbuf.append((char) b);
				}
				// System.out.println("Advancing a row (Newline).");
				// go into next loop run
				continue;
			} else if (b == 10) {
				// advance a row
				rows++;
				// reset cols
				cols = 0;
				// append a newline char
				sbuf.append("\n");
				continue;
			} else {
				sbuf.append((char) b);
				cols++;
			}

			// check cols to advance rows
			if (cols == m_TermCols) {
				rows++;
				// append a newline
				sbuf.append("\n");
				// reset cols!!!
				cols = 0;
				// System.out.println("Advancing a row (COLS).");
			}
		} while (b != -1);
		// System.out.println("renderChunks()::Done #="+ m_Chunks.size());
	}// renderChunks

	private boolean terminalGeometryChanged() {
		if (m_TermRows != m_IO.getRows() || m_TermCols != m_IO.getColumns()) {
			m_TermRows = m_IO.getRows();
			m_TermCols = m_IO.getColumns();
			return true;
		} else {
			return false;
		}
	}// terminalGeometryChanged

	private void updateStatus() {
		if (m_ShowPos) {
			m_Status.setStatusText(m_Prompt + " [" + (m_ChunkPos + 1) + "/"
					+ m_Chunks.size() + "]");
		} else {
			m_Status.setStatusText(m_Prompt);
		}
	}// updateStatus

}// class Pager
