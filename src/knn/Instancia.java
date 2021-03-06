package knn;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/** 
 * Classe contendo a representa��o das Inst�ncias
 * 
 * @author brunoufop@msn.com 
 */
public class Instancia {
	
	public static int ultimoIdentificador = 0;
	private int identificador;
	private double [] valores;
	private double classe;
	private double classeDefinidaPeloAlgoritmo;
	private double validade;		
	private ArrayList <Vizinho> vizinhos;	
	public Instancia(){
		vizinhos = new ArrayList <Vizinho>();
	}
	
	
	/**
	 * Gera os vizinhos dessa inst�ncia, com base no crit�rio de vizinhan�a
	 * dos algoritmos Knn e Mknn
	 * 
	 * @param quantidadeDeVizinhos O n�mero K de vizinhos a ser considerado
	 */
	public void geraVizinhos(int quantidadeDeVizinhos){
		vizinhos.clear();
		Vizinho possivelVizinho;
		ArrayList <Vizinho> possivelVizinhanca = new ArrayList <Vizinho>();
		Instancia inst;
		Iterator <Instancia>it = KnnModificado.instanciasDeTreinamento.iterator();		
		while (it.hasNext()){
			possivelVizinho = new Vizinho();
			inst = (Instancia)it.next();
			if(inst.getIdentificador() != this.getIdentificador()){
				possivelVizinho.setInstancia(inst);			
				possivelVizinho.setDistancia(calculaDistancia(inst));				
				possivelVizinhanca.add(possivelVizinho); 
			}	
	
		}
		preencheListaDeVizinhos(possivelVizinhanca,quantidadeDeVizinhos);	
		
	}
	
	/**
	 * Realiza o c�lculo da dist�ncia entre esta inst�ncia e outra inst�ncia
	 * 
	 * @param inst A inst�ncia para a qual a dist�ncia deve ser calculada
	 * 
	 * @return A dist�ncia entre essa inst�ncia e a inst�ncia passada como par�metro
	 */
	protected double  calculaDistancia(Instancia inst){
		double  distancia = 0.0;
		for(int i = 0 ; i < valores.length - 1 ; i++)			
			distancia += Math.pow(valores[i] - inst.getValores()[i],2);
		distancia = (Math.round(Math.sqrt(distancia) * 10000.0))/10000.0;			
		return distancia;
	}
	/**
	 * Preenche a lista de vizinhos com base nas inst�ncias com menor dist�ncia para esta
	 * 
	 * @param possiveisVizinhos A lista contendo todas as inst�ncias possivelmente vizinhas
	 * @param quantidadeDeVizinhos O n�mero K de vizinhos a serem considerados
	 */
	private void preencheListaDeVizinhos(ArrayList <Vizinho> possiveisVizinhos,int quantidadeDeVizinhos){			
		Collections.sort(possiveisVizinhos,new Vizinho());
		for(int i = 0 ; i < quantidadeDeVizinhos ; i++)
			vizinhos.add(possiveisVizinhos.get(i));
		
		/*if(this.getIdentificador() == 132){
			System.out.println("Minha classe � "+this.getClasse()+ "e Vizinhos s�o ");
			for( int i = 0 ; i < KnnModificado.quantidadeDeVizinhos ; i++){
				Instancia inst = vizinhos.get(i).getInstancia();
				System.out.println("Id "+inst.getIdentificador()+" e classe "+ inst.getClasse());
			}
		}*/
	}
	
	/**
	 * Calcula a validade para essa inst�ncia com base em seus vizinhos e a f�rmula definida
	 * pelo algoritmo do knn modificado
	 */
	public void calculaValidade(){
		double validade = 0;
		double soma = 0;
		Iterator <Vizinho>it = vizinhos.iterator();
		while(it.hasNext()){
			Vizinho v = (Vizinho)it.next();		
			soma += analisaSimilaridade(v.getInstancia());			
		}		
		validade = soma/KnnModificado.quantidadeDeVizinhos;
		setValidade(validade);
		//System.out.println("aux � "+ aux + "soma � " + soma +" validade � "+ validade);
		/*System.out.println("--------------------------");
		
		if(this.getIdentificador() == 132){
			System.out.println("Vizinhos s�o ");
			for( int i = 0 ; i < KnnModificado.quantidadeDeVizinhos ; i++){
				Instancia inst = vizinhos.get(i).getInstancia();
				System.out.println("Id "+inst.getIdentificador()+" e classe "+ inst.getClasse());
			}
			System.out.println("Minha validade � " + getValidade());
		}*/
	}
	public int analisaSimilaridade(Instancia inst){
		
		if(this.getClasse() == inst.getClasse()){		
			return 1;
		}	
		return 0;
	}
	/**
	 * Retorna a classe da inst�ncia com base na defini��o do Mknn
	 * 
	 * @return A classe inferida pelo algoritmo
	 * 
	 */
	public double retornaClasseKnnModificado(){		
		double classe = 0.0;
		Iterator <Vizinho>it = vizinhos.iterator();
		ArrayList <Double> classes = new ArrayList <Double>();
		ArrayList <Classe> classesCriadas = new ArrayList <Classe>();
		while(it.hasNext()){
			Vizinho v = (Vizinho)it.next();				
			double cl = v.getInstancia().getClasse();
			if(classes.isEmpty() || !classes.contains(cl)){
				//System.out.print("Criada ");
				classes.add(cl);
				Classe c1 = new Classe(cl);
				c1.adicionaPeso(v.getPeso());
				classesCriadas.add(c1);
				//System.out.println(" "+c1.getIdentificador());
			}
			else{
				Iterator <Classe>it2 = classesCriadas.iterator();				
				while(it2.hasNext()){
					Classe c = it2.next();
					if(c.getIdentificador() == v.getInstancia().getClasse())
						c.adicionaPeso(v.getPeso());
					//System.out.println(" "+c.getIdentificador());
				}
			}			
		}		
		
		Collections.sort( classesCriadas, Collections.reverseOrder(new Classe()));
		classe = classesCriadas.get(0).getIdentificador();	
		//imprimeClasses(classe,classesCriadas);
		return classe;
	}
	private void imprimeClasses(double classe,ArrayList <Classe> classesCriadas){
		Iterator <Classe>it2 = classesCriadas.iterator();
		System.out.println("Classes criadas");
		while(it2.hasNext()){
			Classe c = it2.next();
			System.out.println(" "+c.getIdentificador() + "peso "+ c.getVotosPonderados());
		}
		System.out.println("Classe retornada "+classe +"\n");
		//System.exit(0);
	}
	/**
	 * Retorna a classe da inst�ncia com base na defini��o do knn
	 * 
	 * @return A classe inferida pelo algoritmo
	 */
	public double retornaClasseKnnOriginal(){
		double classe = 0;
		Iterator <Vizinho>it = vizinhos.iterator();
		ArrayList <Double> classes = new ArrayList <Double>();
		ArrayList <Classe> classesCriadas = new ArrayList <Classe>();
		while(it.hasNext()){
			Vizinho v = (Vizinho)it.next();				
			double cl = v.getInstancia().getClasse();
			if(classes.isEmpty() || !classes.contains(cl)){
				classes.add(cl);
				Classe c1 = new Classe(cl);
				c1.adicionaPeso(1);
				classesCriadas.add(c1);
			}
			else{
				Iterator <Classe>it2 = classesCriadas.iterator();
				while(it2.hasNext()){
					Classe c = it2.next();
					if(c.getIdentificador() == v.getInstancia().getClasse())
						c.adicionaPeso(1);
				}
			}			
		}
		Collections.sort( classesCriadas, Collections.reverseOrder(new Classe()));
		classe = classesCriadas.get(0).getIdentificador();
		return classe;
	}
	

	 /* **************************************************
	 *                                                   *
	 *                M�todos Get e Set                  *
	 * 												     *
	 * **************************************************/
	
	public double [] getValores() {
		return valores;
	}
	public void setValores(double [] valores) {
		this.valores = valores;
	}
	public double  getClasse() {
		return classe;
	}
	public void setClasse(double  classe) {
		this.classe = classe;
	}

	public ArrayList<Vizinho>getVizinhos() {
		return vizinhos;
	}

	public void setVizinhos(ArrayList<Vizinho> vizinhos) {
		this.vizinhos = vizinhos;
	}
	
	public int getIdentificador() {
		return identificador;
	}

	public void setIdentificador(int identificador) {
		this.identificador = identificador;
	}
	public double getValidade() {
		return validade;
	}

	public void setValidade(double validade) {
		this.validade = validade;
	}


	public double getClasseDefinidaPeloAlgoritmo() {
		return classeDefinidaPeloAlgoritmo;
	}

	public void setClasseDefinidaPeloAlgoritmo(
			double classeDefinidaPeloAlgoritmo) {
		this.classeDefinidaPeloAlgoritmo = classeDefinidaPeloAlgoritmo;
	}
	
	
}
