package arquivos;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import gui.ClienteUI;
import servidor.Servidor;

public class Parser {

	private ArrayList<String> tags = new ArrayList<>();
	
	public Parser() {
		
	}
	
	public String lerArquivo(String path) throws IOException {
		FileInputStream stream = new FileInputStream(path);
		InputStreamReader reader = new InputStreamReader(stream);
		BufferedReader br =  new BufferedReader(reader);
		String arquivo = br.readLine();
		String str;
		while ( arquivo != null ) {
			str = br.readLine();
			if ( str == null ) {
				break;
			}
			arquivo =  arquivo + str;
			System.out.println(arquivo);
		}
		return arquivo;
	}
	
	public static void main(String[] args) throws IOException {
		
		ClienteUI cliente = new ClienteUI();
		cliente.montaTela();
		
	}
	
	private void initialize() {
		
	}
	
	private void parse(char caractere) {
		
		if ( "<".equals(caractere) ) {
			
		}
		else if ( "/".equals(caractere) ) {
			
		}
		
	}
	
}
