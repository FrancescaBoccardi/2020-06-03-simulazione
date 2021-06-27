package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	
	private PremierLeagueDAO dao;
	private Graph<Player,DefaultWeightedEdge> grafo;
	private Map<Integer,Player> idMap;
	private int totaleGrado;
	private int gradoMax;
	List<Player> result;
	
	public Model() {
		this.dao = new PremierLeagueDAO();
	}
	
	
	public void creaGrafo(float x) {
		
		grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		idMap = new HashMap<>();
		
		// aggiungo i vertici
		
		dao.getVertici(idMap, x);
		Graphs.addAllVertices(grafo, idMap.values());
		
		// aggiungo gli archi
		
		for(Adiacenza a : dao.getAdiacenze(idMap)) {
			if(a.getPeso()>0) {
				Graphs.addEdge(grafo, a.getP1(), a.getP2(), a.getPeso());
			} else if (a.getPeso()<0) {
				Graphs.addEdge(grafo, a.getP2(), a.getP1(), a.getPeso()*(-1));
			}
		}
		

	}
	
	public Player topPlayer() {
		
		int max = 0;
		Player top = null;
		
		for(Player p : idMap.values()) {
			if(grafo.outDegreeOf(p)>max) {
				top = p;
				max = grafo.outDegreeOf(p);
			}
		}

		return top;
	}
	
	public List<Adiacenza> adiacenzeTop() {

		List<Adiacenza> result = new ArrayList<>();

		for(DefaultWeightedEdge e : grafo.outgoingEdgesOf(this.topPlayer())) {
			Adiacenza a = new Adiacenza(this.topPlayer(),grafo.getEdgeTarget(e), grafo.getEdgeWeight(e));
			result.add(a);
		}
		
		Collections.sort(result);
		return result;
	}
	
	
	public List<Player> ricorsione(int k){
		List<Player> parziale = new ArrayList<>();
		totaleGrado=0;
		gradoMax = 0;
		this.run(parziale, 0, k);
		return result;
	}
	
	private void run(List<Player> parziale, int livello, int k) {
		
		//casi terminali
		
		if(parziale.size()>k) {
			return;
		}
		
		if(parziale.size()==k) {
			
			if(totaleGrado>gradoMax) {
				gradoMax = totaleGrado;
				result = new ArrayList<>(parziale);
			}
			
			return;
		}
		
		
		
		for(Player p : idMap.values()) {
			boolean no = false;
			for(DefaultWeightedEdge up : grafo.incomingEdgesOf(p)) {
				if(parziale.contains(grafo.getEdgeSource(up))) {
					no = true;
					break;
				}
			}
			
			if(!no && !parziale.contains(p)) {
				parziale.add(p);
				
				int pesoUscenti=0;
				int pesoEntranti=0;
				
				for(DefaultWeightedEdge e : grafo.outgoingEdgesOf(p)) {
					pesoUscenti += grafo.getEdgeWeight(e);
				}
				
				for(DefaultWeightedEdge e : grafo.incomingEdgesOf(p)) {
					pesoEntranti += grafo.getEdgeWeight(e);
				}
				
				totaleGrado += pesoUscenti-pesoEntranti;
				
				run(parziale, livello+1, k);
				parziale.remove(parziale.size()-1);
				totaleGrado -= (pesoUscenti-pesoEntranti);
				
			}
		}
	}
	
	


	public Graph<Player, DefaultWeightedEdge> getGrafo() {
		return grafo;
	}


	public int getGradoMax() {
		return gradoMax;
	}
	
	
	
	

	
	
	
}
