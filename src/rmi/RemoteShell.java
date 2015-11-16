
package rmi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class RemoteShell {

	private Runtime runtime;

	private Process process;

	private String stdin;

	private String stderr;

	public RemoteShell() {
		runtime = Runtime.getRuntime();
		setStdin("");
		setStderr("");
	}

	public void commandLine(String command, String[] envp, File dir) throws IOException, InterruptedException {

		process = runtime.exec(command, envp, dir);
		processData();
	}

	public void executeCommmand(String command) {

	}

	private void processData() throws InterruptedException {

		StringBuffer stdinBuffer = new StringBuffer();
		StringBuffer stderrBuffer = new StringBuffer();

		InputStream stdinStream = process.getInputStream();
		InputStream stderrStream = process.getErrorStream();

		new InputStreamHandler(stdinBuffer, stdinStream);
		new InputStreamHandler(stderrBuffer, stderrStream);

		process.waitFor();

		setStdin(stdinBuffer.toString());
		setStderr(stderrBuffer.toString());
	}

	private class InputStreamHandler extends Thread {

		private InputStream m_stream;

		private StringBuffer m_captureBuffer;

		InputStreamHandler(StringBuffer captureBuffer, InputStream stream) {
			m_stream = stream;
			m_captureBuffer = captureBuffer;
			start();
		}

		public void run() {

			try {
				int nextChar;
				while ((nextChar = m_stream.read()) != -1)
					m_captureBuffer.append((char) nextChar);
			} catch (IOException ioex) {
				System.err.println(ioex);
			}
		}
	}

	public static void main(String[] args) {

		RemoteShell shell = new RemoteShell();
		try {
			shell.commandLine("cmd /c dir", null, new File("/"));
			System.out.println(shell.getStdin());
			System.out.println(shell.getStderr());
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String getStdin() {

		return stdin;
	}

	public void setStdin(String stdin) {

		this.stdin = stdin;
	}

	public String getStderr() {

		return stderr;
	}

	public void setStderr(String stderr) {

		this.stderr = stderr;
	}
}
