package knn;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/** 
 * 
 * Classe contendo os m�todos principais para a execu��o do algoritmo Mknn,
 * 
 * @author brunoufop@msn.com 
 */
public class KnnModificado {
	
	public static ArrayList<Instancia> instanciasDeTreinamento;
	public static int quantidadeDeVizinhos;
	public static ArrayList<Instancia> instanciasDeTeste;
	public static boolean normalizacaoFeita;
	public static ArrayList<Double> classesExistentes;
	public static boolean EXIBIR_MATRIZ_CONFUSAO = true; 
	
	public KnnModificado(){
		instanciasDeTreinamento = new ArrayList<Instancia>();
		instanciasDeTeste= new ArrayList<Instancia>();
		normalizacaoFeita = true;
		classesExistentes = new ArrayList<Double>();
	}
	
	/** Percorre a base de dados informada e conta o n�mero de atributos dessa base, incluindo a classe
	 * 
	 * @param base A string contendo a base de dados
	 * @return O n�mero de atributos dessa base, incluindo a classe
	 */
	public int contaAtributos (String base){
		int tamanho=0;
		int i;
		for(i = 0 ; base.charAt(i)!= '\n';i++)
			if(i!= base.length() && base.charAt(i) == ' ')
					tamanho ++;	
		return (tamanho +1);
	}
	
	/** Percorre a base de dados e conta o n�mero de inst�ncias presentes nessa base
	 * 
	 * @param base A string contendo a base de dados
	 * @return O n�mero de inst�ncias dessa base
	 */
	public int contaInstancias(String base){		
		int instancias = 0;
		for(int i = 0 ; i < base.length() && base.charAt(i)!= '\n' ; i++){						
			if(i+1 < base.length() && base.charAt(i+1)== '\n'){
				instancias ++;
				i++;				
			}			
		}
		return instancias;	
	}
	/**
	 * Gera as inst�ncias a partir do conteudo do arquivo lido
	 * 
	 * @param base A String contendo o conte�do do arquivo 
	 * @param numeroDeAtributos O n�mero de atributos do arquivo 
	 * @return Um ArrayList contendo todas as inst�ncias para a base de dados informada
	 */
	public ArrayList <Instancia >geraInstancias(String base,int numeroDeAtributos){
		ArrayList <Instancia> arrayDeInstancias = new ArrayList<Instancia>();
		double  [] valoresDaInstancia = new double [numeroDeAtributos];
		int atributoASerGerado = 0;
		String valor ="";	
		Instancia inst;
		
		for(int i = 0 ; i< base.length() && base.charAt(i)!= '\n' ; i++){
			int identificador = Instancia.ultimoIdentificador;
			if(base.charAt(i)!= ' '){
				valor = valor + base.charAt(i);				
			}
			else{
				valoresDaInstancia [atributoASerGerado] = Float.parseFloat(valor);
				valor="";
				atributoASerGerado ++;
			}
			if(base.charAt(i+1)== '\n'){				
				i++;
				valoresDaInstancia [atributoASerGerado] = Float.parseFloat(valor);
				valor="";
				inst = new Instancia();
				inst.setValores(valoresDaInstancia);
				inst.setClasse(valoresDaInstancia[valoresDaInstancia.length -1]);
				inst.setIdentificador(identificador);
				Instancia.ultimoIdentificador ++;
				arrayDeInstancias.add(inst);
				valoresDaInstancia = new double [numeroDeAtributos];
				atributoASerGerado = 0;
				if(classesExistentes.isEmpty() || !classesExistentes.contains(inst.getClasse())){
					classesExistentes.add(inst.getClasse());						
				}
				if(i == base.length())
					break;
			}	
		}
		return arrayDeInstancias;
	}
	
	/**
	 * Gera, a partir da string <b>linha</b> a instância que o algoritmo deverá classificar, isto é, em production mode.
	 * Essa instância não possui o valor relacionado à classe (ultimo atributo)
	 * @param linha
	 * @param numeroDeAtributos
	 * @return
	 */
	public Instancia geraInstanciaDesconhecida(String linha, int numeroDeAtributos){
		double  [] valoresDaInstancia = new double [numeroDeAtributos]; 
		int atributoASerGerado = 0;
		String valor ="";	
		
		for(int i = 0 ; i< linha.length(); i++){
			if(linha.charAt(i)!= ' '){
				valor = valor + linha.charAt(i);				
			}else{
				valoresDaInstancia [atributoASerGerado] = Float.parseFloat(valor);
				valor="";
				atributoASerGerado ++;
			}
		}
		
		Instancia inst = new Instancia();
		inst.setValores(valoresDaInstancia);
		inst.setIdentificador(++Instancia.ultimoIdentificador);
		
		return inst;
		
	}
	
	/**
	 * M�todo principal da execu��o do algoritmo pelo Holdout.
	 * � respons�vel por receber a base de dados e gerenciar a chamada dos m�todos necess�rios 
	 * para o correto funcionamento do algoritmo dentro do holdout. Gerencia qual m�todo deve ser chamado
	 * para infer�ncia da classe para a base de testes.
	 * 
	 * @param algoritmo Um inteiro representando o algoritmo a ser executado
	 * (1 para o Knn Modificado ou 2 para o original)
	 * @return A matriz de confus�o gerada
	 * 
	 */
	public double [][] executaProcedimento(int algoritmo){
		
		/*
		 * Primeiramente , faz a normalizacao das bases, caso ela ainda nao tenha sido feita
		 * 
		 * No metodo cross validation, a normalizacao somente precisa ser feita uma �nica vez.
		 * Nas demais chamadas a este m�todo, a normaliza��o j� foi feita, e portanto, outra normaliza��o 
		 * interferiria no resultado do algoritmo
		 */
//		if(!normalizacaoFeita){
//			normalizaBases();	
//			normalizacaoFeita = false;
//		}
//		
		/*
		 * Gera os vizinhos de cada exemplo do conjunto de treinamento
		 * e calcula a validade de cada exemplo
		 */
		Iterator <Instancia>it = instanciasDeTreinamento.iterator();
		while(it.hasNext()){
			Instancia inst = (Instancia)it.next();
			inst.geraVizinhos(quantidadeDeVizinhos);
			inst.calculaValidade();			
		}		
		/*
		 * Gera os vizinhos da base de teste e
		 * calcula o peso de cada um deles		
		 */
		Iterator <Instancia>it3 = instanciasDeTeste.iterator();
		while(it3.hasNext()){
			Instancia inst = (Instancia) it3.next();
			inst.geraVizinhos(quantidadeDeVizinhos);
			Iterator <Vizinho>it2 = inst.getVizinhos().iterator();
			while(it2.hasNext()){
				Vizinho viz = (Vizinho)it2.next();
				//System.out.println("Validade de " + viz.getInstancia().getIdentificador()+"� " +viz.getInstancia().getValidade());
				viz.calculaPeso();				
			}		
		//	System.out.println("-----------------");
			/*
			 * Define agora a classe do exemplo de teste
			 * e a armazena para comparacao posterior
			 */
			double cl;
			if(algoritmo == 1)
				cl = inst.retornaClasseKnnModificado();			
			else
				cl = inst.retornaClasseKnnOriginal();			
			inst.setClasseDefinidaPeloAlgoritmo(cl);
		}		
		double [][] matriz = null;
		if(EXIBIR_MATRIZ_CONFUSAO){
			matriz = executaComparacao();
		}
		normalizacaoFeita = false;
		return matriz;
		
	}
	/**Realiza a compara��o entre a classe definida pelo algoritmo para cada inst�ncia da base de teste
	 * e a classe real da inst�ncia j� conhecida para verificar a exatid�o dos c�lculos 
	 * feitos pelo algoritmo.
	 * 
	 * @return A matriz de confus�o
	 */
	private double [][] executaComparacao(){			
		Iterator <Double>it2 = classesExistentes.iterator();
		double array[]= new double[classesExistentes.size()];
		int indice = 0;
		while(it2.hasNext()){
			array[indice]= it2.next();
			indice ++;			
		}
		
		array = ordena  (array);
		
		double [][] matriz = new double[classesExistentes.size()][classesExistentes.size()];
		matriz = iniciaMatriz(matriz);
		
		Iterator <Instancia>it = instanciasDeTeste.iterator();
		while(it.hasNext()){
			Instancia inst = (Instancia)it.next();
			double classeCerta = inst.getClasse();
			double classeDefinida = inst.getClasseDefinidaPeloAlgoritmo();
			for(int i = 0 ; i < indice  ; i++){				
				if(classeCerta == array[i]){
					if(classeDefinida == array[i]){
						matriz [i][i]++;
						break;
					}
					else{
						for(int j = 0 ; j < indice  ;j++)
							if(classeDefinida == array[j]){
								matriz [j][i]++;
								break;
							}
						break;
					}					
				}
			}			
		}		
		return matriz;
	}
	
	/** Faz uma ordena��o do vetor recebido atrav�s do m�todo bolha
	 * 
	 * @param vet O vetor a ser ordenado
	 * @return O vetor j� ordenado
	 */
	public double[] ordena(double [] vet){
		double x,y;
		for(int i = 0 ; i < vet.length ; i++){
			x = vet[i];
			if(i+1 < vet.length && x > vet[i+1]){
				y = vet[i+1] ;
				vet[i+1]= x;
				vet[i] = y;
			}			
		}
		return vet;
	}
	/**Inicia a matriz informada com o valor 0(zero) em todas as suas posi��es
	 * 
	 * @param matriz A matriz a ser iniciada
	 * @return A matriz pronta para uso.
	 */
	public double [][] iniciaMatriz(double [][]matriz){
		for(int i = 0 ; i < matriz.length ; i++)
			for(int j = 0 ; j < matriz.length ;j++)
				matriz[i][j] = 0.0;
		return matriz;
	}
	
	/**Calcula a exatid�o do algoritmo a partir da matriz de confus�o recebida
	 * 
	 * @param matriz A matriz de confus�o contendo o resultado do algoritmo	 
	 * @return A porcentagem de acertos do algoritmo
	 */
	public double calculaExatidao(double [][]matriz){
		double exatidao = 0.0, instancias = 0;
		for(int i = 0 ; i < matriz[0].length ; i++)
			for(int j = 0 ; j < matriz[0].length ; j++){
				instancias += matriz[i][j];
				if( i == j )
					exatidao+= matriz[i][j];
			}
		exatidao /= instancias; 
		exatidao = (Math.round(exatidao*10000.0))/10000.0;
		
		return exatidao;
		
	}
	
	private void normalizaBases(){
		normaliza(KnnModificado.instanciasDeTreinamento);
		normaliza(KnnModificado.instanciasDeTeste);
	}
	/**
	 * Faz a normaliza��o das bases de teste e treinamento
	 */
	private void normaliza(ArrayList<Instancia> list){
		
		int atributos = list.get(0).getValores().length-1;
		Iterator <Instancia>it = list.iterator();
		double [][] vet = new double[list.size()][atributos];
		int linha = 0;
		
		// Passa os dados para um vetor 
		while (it.hasNext()){
			Instancia inst = (Instancia) it.next();
			for(int coluna = 0 ; coluna < atributos ; coluna ++){
				vet[linha][coluna] = inst.getValores()[coluna];
			}
			linha++;
		}
		// acha o maior elemento da coluna 
		double maior;
		for(linha = 0 ; linha < atributos ; linha++){
			double [] vetorAuxiliar = new double[list.size()];
			for(int coluna = 0 ; coluna < list.size() ; coluna ++){
				vetorAuxiliar[coluna] = vet[coluna][linha];				
			}
			vetorAuxiliar = ordena(vetorAuxiliar);
			maior = vetorAuxiliar[list.size()-1];
			
			// atualiza esse elemento na base de dados (apenas na memoria -- nao salva a alteracao em disco)
			dadoNormalizado(linha,list,maior);
		}
	}
	/**
	 * Completa a normaliza��o da base por dividir cada atributo, pelo maior valor do mesmo atributo
	 * em outras inst�ncias
	 * 
	 * @param coluna A coluna onde est� o atributo a ser normalizado
	 * @param list A lista a ser normalizada ( teste ou treinamento)
	 * @param dado O maior valor para aquele atributo em toda a base
	 */
	private void dadoNormalizado(int coluna, ArrayList<Instancia> list,double dado){
		Iterator <Instancia>it = list.iterator();
		while (it.hasNext()){
			Instancia inst = (Instancia) it.next();
			inst.getValores()[coluna] /= dado;
		}
	}
	
	/**
	 * M�todo respons�vel por particionar a base de dados em uma base de treinamento e outra base de 
	 * teste (m�todo Holdout)
	 * 
	 * @param treino A porcentagem a ser separada para treinamento
	 * @param instancias A quantidade de inst�ncias da base de dados
	 * @param list A lista contendo as inst�ncias existentes
	 */
	public void particiona(double treino,int instancias,ArrayList <Instancia> list2){
		ArrayList <Instancia> list = new ArrayList<Instancia>();
		list.addAll(list2);
		KnnModificado.instanciasDeTeste.clear(); 
		KnnModificado.instanciasDeTreinamento.clear();
		ArrayList <Integer> listAux = new ArrayList <Integer>();
		int quantidade = (int)((double)instancias/100 * treino);		
		int q = 0;
		Random r = new Random();
		while(q < quantidade){
			int escolhida = r.nextInt(list.size());
			Instancia inst = list.get(escolhida);
			if(!listAux.contains(inst.getIdentificador())){
				KnnModificado.instanciasDeTreinamento.add(inst);
				list.remove(escolhida);
				listAux.add(inst.getIdentificador());
				q++;				
			}
		}
		KnnModificado.instanciasDeTeste = list;		
			
	}
	
	/**
	 * Gerencia a divis�o da base de dados por folds para variar a base de dados e treinamento
	 *  (m�todo CrossValidation)
	 *  
	 * @param folds A quantidade de folds a ser criada
	 * @param instancias A quantidade de inst�ncias contidas na base de dados
	 * @param list2 A lista contendo todas as inst�ncias
	 * @param algoritmo Indica o algoritmo a ser usado(Knn ou Mknn)
	 * @return A matriz de confus�o gerada pelo algoritmo
	 */
	public double [][] treinaPorFolds(int folds,int instancias,ArrayList <Instancia> list2, int algoritmo){
		int quantidade = instancias/folds;
		ArrayList <Instancia> list = new ArrayList<Instancia>();
		list.addAll(list2);
		int q = 0, iteracoes = 0;		
		double[][]matrizGeral = null;
		KnnModificado.instanciasDeTeste.clear();	
		KnnModificado.instanciasDeTreinamento.clear();
		Random r = new Random();		
		// garante q cada instancia so participa do treinamento uma vez	
		ArrayList<Instancia> listAux = new ArrayList<Instancia>();
		while(iteracoes < folds ){				
			if(list.size() <= quantidade){
				KnnModificado.instanciasDeTreinamento = list;
				q += list.size();				
			}			
			else{
				while(q < quantidade){
					int escolhida = r.nextInt(list.size());
					Instancia inst = list.get(escolhida);
					KnnModificado.instanciasDeTreinamento.add(inst);
					list.remove(escolhida);					
					q++;
				}							
			}
			
			// o teste ser� composto por todas as instancias que anteriormente foram de treinamento
			// e mais as que ainda estao na list ( ou seja, nao foram sorteadas pro treinamento ainda
			KnnModificado.instanciasDeTeste.addAll(list);			
			double [][]matriz = executaProcedimento(algoritmo);
			if(matrizGeral == null){
				matrizGeral = new double[matriz[0].length][matriz[0].length];
				matrizGeral = iniciaMatriz(matrizGeral);
			}
			matrizGeral = atualizaMatriz(matriz,matrizGeral);
			listAux.addAll(instanciasDeTreinamento);
			KnnModificado.instanciasDeTeste.clear();			
			KnnModificado.instanciasDeTeste.addAll(listAux);
			KnnModificado.instanciasDeTreinamento.clear();
			iteracoes++;
			q = 0;
		}		
		matrizGeral = atualizaMatriz2(matrizGeral,folds);		
		return matrizGeral;
	}
	
	/**
	 * Soma os valores em cada posi��o da primeira matriz com os valores no mesmo �ndice da segunda matriz
	 * afim de manter a quantidade de erros e acertos para posterior contagem de m�dia 
	 *  
	 * @param matriz A matriz atual com os valores a serem somados
	 * @param matrizGeral A matriz geral que vai guardar os valores somados
	 * @return
	 */
	private double[][]atualizaMatriz(double [][]matriz,double matrizGeral[][]){
		for(int i = 0 ; i < matriz[0].length ; i++)
			for(int j = 0 ; j < matriz[0].length ; j++)
				matrizGeral[i][j]+= matriz[i][j];
		return matrizGeral;
	}
	
	/**
	 * Realiza a m�dia dos valores em cada �ndice da matriz de confus�o 
	 * com base no n�mero de folds 
	 * 
	 * @param matriz A matriz de confus�o a ser normalizada
	 * @param folds O n�mero de folds que o algoritmo utilizou
	 * @return A matriz de confus�o ap�s calculo da m�dia
	 */
	private double[][]atualizaMatriz2(double [][]matriz,int folds){
		for(int i = 0 ; i < matriz[0].length ; i++)
			for(int j = 0 ; j < matriz[0].length ; j++)
				matriz[i][j]= (int)matriz[i][j]/folds;
		return matriz;
	}
	
	 /* **************************************************
	 *                                                   *
	 *                M�todos Get e Set                  *
	 * 												     *
	 * **************************************************/
	 
	public static ArrayList<Instancia> getInstanciasDeTreinamento() {
		return instanciasDeTreinamento;
	}

	public static void setInstanciasDeTreinamento(ArrayList<Instancia> instancias) {
		KnnModificado.instanciasDeTreinamento = instancias;
	}
	
	public static int getQuantidadeDeVizinhos() {
		return quantidadeDeVizinhos;
	}

	public static void setQuantidadeDeVizinhos(int quantidadeDeVizinhos) {
		KnnModificado.quantidadeDeVizinhos = quantidadeDeVizinhos;
	}

	public static ArrayList<Instancia> getInstanciasDeTeste() {
		return instanciasDeTeste;
	}

	public static void setInstanciasDeTeste(ArrayList<Instancia> instanciasDeTeste) {
		KnnModificado.instanciasDeTeste = instanciasDeTeste;
	}
}
