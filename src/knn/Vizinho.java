package knn;

import java.util.Comparator;


/** 
 * Classe contendo a representa��o de um vizinho 
 * 
 * @author brunoufop@msn.com 
 */
public class Vizinho implements Comparator <Vizinho>{
	private Instancia instancia;	
	private double  distancia;
	private double peso;	
	
	/**
	 * Calcula o peso desse vizinho a partir da validade da sua inst�ncia
	 * 
	 * @param exemplo A inst�ncia da qual este objeto � vizinho
	 */
	public void calculaPeso(){			
		double aux = 1/(distancia + 0.5);			
		double peso = Math.round((instancia.getValidade()* aux) * 1000.0) / 1000.0;		
		//System.out.println("peso " + peso);
		this.setPeso(peso);		
		
	}
	
	public double  getDistancia() {
		return distancia;
	}
	public void setDistancia(double  distancia) {
		this.distancia = distancia;
	}
	
	public Instancia getInstancia() {
		return instancia;
	}
	public void setInstancia(Instancia instancia) {
		this.instancia = instancia;
	}	
	public double getPeso() {
		return peso;
	}
	public void setPeso(double peso) {
		this.peso = peso;
	}
	public int compare(Vizinho r1,Vizinho r2) {
		double d1 = r1.getDistancia();
		double d2 = r2.getDistancia();
		if (d1==d2)
			return 0;		
		else if(d1>d2)
			return 1;
		else 
			return -1;
	}

}
