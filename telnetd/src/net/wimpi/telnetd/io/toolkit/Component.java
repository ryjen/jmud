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
 * Class that represents an abstract toolkit component.
 * 
 * @author Dieter Wimberger
 * @version 2.0 (16/07/2006)
 */
public abstract class Component {

	protected String m_Name;
	protected BasicTerminalIO m_IO;
	protected Point m_Position;
	protected Dimension m_Dim;

	/**
	 * Constructor for an abstract toolkit component.
	 * 
	 * @param io
	 *            Instance of a class implementing the BasicTerminalIO.
	 * @param name
	 *            String that represents the components name.
	 */
	public Component(BasicTerminalIO io, String name) {
		m_IO = io;
		m_Name = name;
	}// constructor

	/**
	 * Method that draws the component.
	 */
	public abstract void draw() throws IOException;

	/**
	 * Accessor method for a components dimension.
	 * 
	 * @return Dimension that encapsulates the dimension in cols and rows.
	 */
	public Dimension getDimension() {
		return m_Dim;
	}// getDimension

	/**
	 * Accessor method for a components location.
	 * 
	 * @return Point that encapsulates the location.
	 */
	public Point getLocation() {
		return m_Position;
	}// getLocation

	/**
	 * Accessor method for the name property of a component.
	 * 
	 * @return String that represents the components name.
	 */
	public String getName() {
		return m_Name;
	}// getName

	/**
	 * Convenience mutator method for a components location.
	 * 
	 * @param col
	 *            int that represents a column coordinate.
	 * @param row
	 *            int that represents a row coordinate.
	 */
	public void setLocation(int col, int row) {
		if (m_Position != null) {
			m_Position.setColumn(col);
			m_Position.setRow(row);
		} else {
			m_Position = new Point(col, row);
		}
	}// set Location

	/**
	 * Mutator method for a components location.
	 * 
	 * @param pos
	 *            Point that encapsulates the (new) Location.
	 */
	public void setLocation(Point pos) {
		m_Position = pos;
	}// setLocation

	/**
	 * Mutator method for a components dimension.
	 * 
	 * @param dim
	 *            Dimension that encapsulates the dimension in cols and rows.
	 */
	protected void setDimension(Dimension dim) {
		m_Dim = dim;
	}// setDimension

}// class Component
