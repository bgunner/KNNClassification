package knn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/** 
 * Classe respons�vel pela manipula��o do arquivo contendo a base de dados
 * 
 * @author brunoufop@msn.com 
 */
public class AcessaArquivo {
	
	/**
	 * Percorre todo o arquivo e salva o seu conte�do
	 * 
	 * @param nomeDoArquivo Localiza��o e nome do arquivo com a sua devida extens�o
	 * 
	 * @return Uma String contendo todo o conte�do do arquivo
	 * 
	 * @throws FileNotFoundException Se o arquivo informado n�o foi encontrado
	 * @throws IOException Se um erro ocorre ao manipular o arquivo
	 */
	public static String leArquivo(String nomeDoArquivo)throws FileNotFoundException, IOException {
		File file = new File(nomeDoArquivo);

		if (! file.exists()) {
			return null;
		}

		BufferedReader br = new BufferedReader(new FileReader(nomeDoArquivo));
		StringBuffer bufSaida = new StringBuffer();

		String line;
		while( (line = br.readLine()) != null ){
			bufSaida.append(line + "\n");
		}
		br.close();
		return bufSaida.toString();
	}

}
