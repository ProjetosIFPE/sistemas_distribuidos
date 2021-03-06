package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;

import httplike.arquivos.Parser;

public class ServidorUDP {
	
	private Parser parser;
	private Collection<String> pacotes;
	private Collection<String> confirmacoes;
	private static final int PORTA_SERVIDOR = 6791;
	private static final int PORTA_CLIENTE = 6792;
	private static ServidorUDP servidor = null;
	private static DatagramSocket socket;
	private static final String PATH_ARQUIVO = "udp_servidor.txt";
	
	private ServidorUDP() {
		parser = new Parser();
		pacotes = new ArrayList<String>();
		confirmacoes = new ArrayList<String>();
		try {
			socket = new DatagramSocket(PORTA_SERVIDOR);
		} catch (SocketException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static ServidorUDP getInstancia() {
		if ( servidor == null ) {
			servidor = new ServidorUDP();
		}
		return servidor;
	}
	
	public void enviarPacote(String host, int porta, byte[] dados) throws IOException {
		InetAddress end = InetAddress.getByName(host);
		DatagramPacket packet = new DatagramPacket(dados, dados.length, end, porta);
		DatagramSocket socket = new DatagramSocket();
		socket.send(packet);
		socket.close();
	}
	
	private String extrairPacote(String pacote) {
		return pacote.substring(1, pacote.length());
	}
	
	private String extrairACK(String pacote) {
		return pacote.substring(0, 1);
	}
	
	private boolean verificarOrdem(String ack) {
		boolean emOrdem = Boolean.TRUE;
		int valorACK = Integer.valueOf(ack).intValue();
		if ( valorACK != 1 ) {
			Integer ultimoACK = Integer.valueOf((String)confirmacoes.toArray()[confirmacoes.size() - 1]);
			if ( !(valorACK - 1 == ultimoACK.intValue()) ) {
				emOrdem = Boolean.FALSE;
			}
		}
		return emOrdem;
	}
	
	private void salvarArquivo() {
		try {
			parser.escreverArquivoPorLinha(pacotes, PATH_ARQUIVO );
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	private void receberArquivo() throws SocketException {
		byte[] buffer = new byte[1024];
		String pacote;
		String ack;
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		try {
			do {
				System.out.println("Aguardando pacote...");
				socket.receive(packet);
				System.out.println(packet.getSocketAddress());
				pacote = new String(buffer, 0, packet.getLength());
				ack = extrairACK(pacote);
				pacote = extrairPacote(pacote);
				if ( "0".equals(ack) ) {
					System.out.println("Salvando arquivo...");
					salvarArquivo();
					System.out.println("Esvaziando buffers...");
					confirmacoes.removeAll(confirmacoes);
					pacotes.removeAll(pacotes);
				} else {
					if( verificarOrdem(ack)) {
						enviarPacote(packet.getAddress().getHostAddress(), PORTA_CLIENTE, ack.getBytes());
						confirmacoes.add(ack);
						pacotes.add(pacote);
						System.out.println("Pacote - " + pacote);
						System.out.println("ACK - " + ack );
					} else {
						System.out.println("Fora de ordem! Recebido pacote com ACK - " + ack );
					}
					
					
				}
				
			} while ( Boolean.TRUE );
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}
	public static void main(String[] args) throws SocketException {
		ServidorUDP.getInstancia().receberArquivo();
		
	}
	
	
} 