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

package net.wimpi.telnetd.util;

import java.util.StringTokenizer;

/**
 * Utility class with string manipulation methods.
 * 
 * @author Dieter Wimberger
 * @version 2.0 (16/07/2006)
 */
public final class StringUtil {

	/**
	 * Method that splits a string with delimited fields into an array of field
	 * strings.
	 * 
	 * @param str
	 *            String with delimited fields.
	 * @param delim
	 *            char that represents the delimiter.
	 * @return String[] holding all fields.
	 */
	public static String[] split(String str, char delim) {
		return StringUtil.split(str, String.valueOf(delim));
	}// split(String,char)

	/**
	 * Method that splits a string with delimited fields into an array of field
	 * strings.
	 * 
	 * @param str
	 *            String with delimited fields.
	 * @param delim
	 *            String that represents the delimiter.
	 * @return String[] holding all fields.
	 */
	public static String[] split(String str, String delim) {

		StringTokenizer strtok = new StringTokenizer(str, delim);
		String[] result = new String[strtok.countTokens()];

		for (int i = 0; i < result.length; i++) {
			result[i] = strtok.nextToken();
		}

		return result;
	}// split(String,String)

	/**
	 * Private constructor, to prevent construction.
	 */
	private StringUtil() {
	}// constructor

}// class StringUtil

