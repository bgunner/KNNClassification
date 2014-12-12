KNNClassification
=================

Implementação do algoritmo KNN e também de uma versão deste, chamado MKNN (Modified KNN) que utiliza peso nos vizinhos para definir a classe.

Exemplo de uso:

1) Hould out

	 KnnModificado knn = new KnnModificado();


	String base = ""; //ler arquivo com a base de dados
	int instancias = knn.contaInstancias(base);
	int atributos = knn.contaAtributos(base);
	double treino = 90;		//porcentagem de instancias de treinamento 

	ArrayList <Instancia> list = knn.geraInstancias(base,atributos);
	KnnModificado.setQuantidadeDeVizinhos(17); 
	knn.particiona(treino, instancias, list);
	double [][]matriz  = knn.executaProcedimento(algoritmo); // matriz de confusao
	double exatidao = knn.calculaExatidao(matriz)*100.0;

2) K-Fold
	 KnnModificado knn = new KnnModificado();


	String base = ""; //ler arquivo com a base de dados
	int instancias = knn.contaInstancias(base);
	int atributos = knn.contaAtributos(base);
	
	ArrayList <Instancia> list = knn.geraInstancias(base,atributos);

	int folds = 7;
	double [][]matriz  = knn.treinaPorFolds(folds, instancias,list,algoritmo); // matriz de confusao
	double exatidaol = knn.calculaExatidao(matriz)*100.0;





