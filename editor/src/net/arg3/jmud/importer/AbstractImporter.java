package net.arg3.jmud.importer;

import java.util.List;

import javax.swing.JTextArea;
import javax.swing.SwingWorker;

public abstract class AbstractImporter extends SwingWorker<Integer, String> {
	protected FileReader reader;
	protected JTextArea output;

	public abstract int commit();

	public JTextArea getOutput() {
		return output;
	}

	public FileReader getReader() {
		return reader;
	}

	public void log(String... str) {
		publish(str);
		publish("\r\n");
	}

	@Override
	public void process(List<String> args) {
		for (String str : args) {
			System.out.print(str);
		}
		if (output == null)
			return;

		for (String str : args) {
			output.append(str);
		}
	}

	public void setOutput(JTextArea output) {
		this.output = output;
	}

	public void setReader(FileReader reader) {
		this.reader = reader;
	}
}
