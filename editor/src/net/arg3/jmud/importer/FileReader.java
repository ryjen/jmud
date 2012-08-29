/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.arg3.jmud.importer;

import java.io.File;
import java.io.IOException;

import net.arg3.jmud.Jmud;

/**
 * 
 * @author Ryan
 */
public class FileReader {

	public static long flagConvert(char letter) {
		long bitsum = 0;
		char i;

		if ('A' <= letter && letter <= 'Z') {
			bitsum = 1;
			for (i = letter; i > 'A'; i--) {
				bitsum *= 2;
			}
		} else if ('a' <= letter && letter <= 'z') {
			bitsum = 67108864; /* 2^26 */
			for (i = letter; i > 'a'; i--) {
				bitsum *= 2;
			}
		}

		return bitsum;
	}

	private final String file;
	private final File path;
	private int pos;

	private int line;

	public FileReader(String path) throws IOException {
		this.path = new File(path);
		file = Jmud.readFileAsString(path);
		pos = 0;
		line = 1;
	}

	public boolean EOF() {
		return pos >= file.length() - 1;
	}

	public File getFile() {
		return path;
	}

	public int getLine() {
		return line;
	}

	public char read() {
		char value = file.charAt(pos++);
		if (value == '\n')
			line++;
		return value;
	}

	public long readFlag() {
		long number = 0;

		char c = readLetter();

		if (Character.isLetter(c)) /* ROM OLC */{
			while (('A' <= c && c <= 'Z') || ('a' <= c && c <= 'z')) {
				number += flagConvert(c);
				c = read();
			}
		}

		while (Character.isDigit(c)) {
			number = number * 10 + c - '0';
			c = read();
		}

		if (c == '|') {
			number += readFlag();
		} else if (c != ' ') {
			unRead();
		}

		return number;
	}

	public char readLetter() {
		char letter;

		do {
			letter = read();
		} while (Character.isWhitespace(letter));

		return letter;
	}

	public String readLine() {
		char letter = read();

		StringBuilder line = new StringBuilder();

		while (!EOF() && letter != '\n' && letter != '\r') {
			line.append(letter);
			letter = read();
		}

		while (!EOF() && (letter == '\n' || letter == '\r')) {
			letter = read();
		}

		unRead();

		return line.toString();
	}

	/*
	 * Read a number from a file.
	 */
	public int readNumber() throws IOException {
		int number = 0;
		boolean sign = false;
		char c = readLetter();

		if (c == '+') {
			c = read();
		} else if (c == '-') {
			sign = true;
			c = read();
		}

		if (!Character.isDigit(c)) {
			throw new IOException("bad number format");
		}

		do {
			number = number * 10 + c - '0';
			c = read();
		} while (Character.isDigit(c));

		if (sign) {
			number = 0 - number;
		}

		if (c == '|') {
			number += readNumber();
		} else if (c != ' ') {
			unRead();
		}

		return number;
	}

	public String readString() {
		char c = readLetter();
		StringBuilder buf = new StringBuilder();

		while (!EOF() && c != '~') {
			switch (c) {
			default:
				buf.append(c);
				break;
			case '\n':
				buf.append("\r\n");
				break;
			case '\r':
				break;
			}
			c = read();
		}

		return buf.toString();
	}

	public String readStringEol() {
		char c = readLetter();
		StringBuilder buf = new StringBuilder();

		while (!EOF() && c != '~' && c != '\n' && c != '\r') {

			buf.append(c);

			c = read();
		}

		while (!EOF() && (c == '\n' || c == '\r')) {
			c = read();
		}

		unRead();

		return buf.toString();
	}

	public void readToEol() {
		char c;

		do {
			c = read();
		} while (c != '\n' && c != '\r');

		do {
			c = read();
		} while (c == '\n' || c == '\r');

		unRead();
		return;
	}

	public String readWord() {
		char cEnd = readLetter();
		StringBuilder word = new StringBuilder();

		if (cEnd != '"' && cEnd != '\'') {
			word.append(cEnd);
			cEnd = ' ';
		}

		while (!EOF()) {
			char next = read();

			if (cEnd == ' ' ? Character.isWhitespace(next) : next == cEnd) {
				if (cEnd == ' ') {
					unRead();
				}

				return word.toString();
			}

			word.append(next);
		}

		return word.toString();
	}

	public void unRead() {
		if (pos > 0) {
			pos--;
			if (file.charAt(pos) == '\n' && line > 0)
				line--;
		}
	}
}
