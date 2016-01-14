package by.pachkovsky.mt.logic;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import by.pachkovsky.mt.model.Chain;
import by.pachkovsky.mt.model.Grammar;
import by.pachkovsky.mt.model.Rule;
import by.pachkovsky.mt.model.Situation;
import by.pachkovsky.mt.model.Symbol;
import by.pachkovsky.mt.model.TerminalSymbol;

public class StatesAccumulator {
	
	private List<Set<Situation>> states;
	private List<Map<Symbol, Integer>> g;
	private List<Map<TerminalSymbol, Integer>> f;
	private Grammar grammar;
	private SituationClosureComputer closureComputer;
	private PrintWriter writer;
	private Set<Set<Situation>> nonObservedStates;
	private Set<Set<Situation>> observedStates;
	private boolean emptyStateGained = false;
	private FirstFunctionComputer firstFunctionComputer;
	private List<Rule> enumeratedRules;
	
	public StatesAccumulator(Grammar grammar, FirstFunctionComputer firstFunctionComputer, SituationClosureComputer closureComputer, PrintWriter writer) {
		this.grammar = grammar;
		this.closureComputer = closureComputer;
		this.writer = writer;
		this.states = new ArrayList<Set<Situation>>();
		this.nonObservedStates = new HashSet<Set<Situation>>();
		this.observedStates = new HashSet<Set<Situation>>();
		this.firstFunctionComputer = firstFunctionComputer;
		this.g = new ArrayList<Map<Symbol, Integer>>();
		this.f = new ArrayList<Map<TerminalSymbol, Integer>>();
		this.enumeratedRules = grammar.getEnumeratedRules();
	}

	public List<Set<Situation>> getStates() {
		return states;
	}

	public void setStates(List<Set<Situation>> states) {
		this.states = states;
	}
		
	public List<Map<Symbol, Integer>> getG() {
		return g;
	}
	
	

	public List<Map<TerminalSymbol, Integer>> getF() {
		return f;
	}

	public void computeStartState() {
		writer.print("q0 = <" + grammar.getInitialSituation() + "> = ");
		Set<Situation> startState = closureComputer.closure(grammar.getInitialSituation());
		writer.println(startState);
		writer.println(System.lineSeparator());
		this.states.add(startState);
		this.nonObservedStates.add(startState);
		this.g.add(new TreeMap<Symbol, Integer>());
		this.f.add(new TreeMap<TerminalSymbol, Integer>());
		
		for (Situation situation : startState) {
			if (situation.getSituationRule().getRuleAlternatives().get(0).isReduceSituationChain()) {
				Rule rule = Rule.of(situation.getSituationRule()
						.getNonTerminalSymbol(), new Chain(situation.getSituationRule().getRuleAlternatives().get(0)));
				int ruleIndex = this.enumeratedRules.indexOf(rule);
				this.f.get(0).put(situation.getFollowSymbol(), ruleIndex);			
			} else if (situation.getSituationRule().getRuleAlternatives().get(0).isPopSituationChain()) {
				TerminalSymbol terminalSymbol =(TerminalSymbol) situation.getSituationRule().getRuleAlternatives().get(0).getSubchainAfterPositionMarker()
						.getLeadingSymbol();
				this.f.get(0).put(terminalSymbol, -2);	
			}
		}
		
	
	}
	
	public void computeStates() {
		StringBuilder builder = new StringBuilder("Изначально имеем" + System.lineSeparator()).append(this.getPrettyStringStatesRepresentation()).append(System.lineSeparator() + System.lineSeparator());
		
		int iteration = 1;
		while (!nonObservedStates.isEmpty()) {
			Iterator<Set<Situation>> statesIterator = nonObservedStates.iterator();
			if (statesIterator.hasNext()) {
				builder.append("Итерация ").append(iteration++).append(": ");
				
				Set<Situation> currentState = statesIterator.next();
				int stateIndex = states.indexOf(currentState);
				this.nonObservedStates.remove(currentState);
				this.observedStates.add(currentState);
				
				if (!currentState.isEmpty()) {
					builder.append("Извлекаем состояние q").append(stateIndex);
				} else {
					builder.append("Извлекаем пусто состояние []");
				}
				
				builder.append(" из O и включаем в C, получаем: " + System.lineSeparator());
				builder.append(this.getPrettyStringStatesRepresentation()).append(System.lineSeparator());
				
				for (Symbol symbol : this.grammar.getAllSymbolsWithoutAxiom()) {
					Set<Situation> muedSituations = closureComputer.mu(currentState, symbol);
					builder.append("g(q").append(stateIndex).append(", ").append(symbol).append(") = <mu(q")
						.append(stateIndex).append(", ").append(symbol).append(")> = ")
						.append("<").append(muedSituations).append(">");
					Set<Situation> newState = closureComputer.closure(muedSituations);
					builder.append(" = ").append(newState);
					
					if (!newState.isEmpty()) {
						int newStateIndex = this.states.indexOf(newState);
						if (newStateIndex == -1) {
							this.states.add(newState);
							builder.append(" = q" + (this.states.size() - 1))
								.append(" (ранее не полученное состояние, добавляем в O)");
							this.nonObservedStates.add(newState);
							
							this.g.add(new TreeMap<Symbol, Integer>());
							this.g.get(stateIndex).put(symbol, this.states.size() - 1);
							
							this.f.add(new TreeMap<TerminalSymbol, Integer>());
							for (Situation situation : newState) {
								if (situation.getSituationRule().getRuleAlternatives().get(0).isReduceSituationChain()) {
									Rule rule = Rule.of(situation.getSituationRule()
											.getNonTerminalSymbol(), new Chain(situation.getSituationRule().getRuleAlternatives().get(0)));
									int ruleIndex = this.enumeratedRules.indexOf(rule);
									this.f.get(this.states.size() - 1).put(situation.getFollowSymbol(), ruleIndex);			
								} else if (situation.getSituationRule().getRuleAlternatives().get(0).isPopSituationChain()) {
									TerminalSymbol terminalSymbol = (TerminalSymbol) situation.getSituationRule().getRuleAlternatives().get(0).getSubchainAfterPositionMarker()
											.getLeadingSymbol();
									this.f.get(this.states.size() - 1).put(terminalSymbol, -2);	
								}
							}
							
						} else {
							builder.append(" = q" + newStateIndex).append(" (ранее полученное состояние)");
							this.g.get(stateIndex).put(symbol, newStateIndex);
						}
					} else {
						if (this.emptyStateGained) {
							builder.append(" (ранее полученное состояние)");
						} else {
							this.emptyStateGained = true;
							this.nonObservedStates.add(newState);
							builder.append(" (ранее не полученное состояние, добавляем в O)");
						}
						//emptyState = newState;
						builder.append(" (пустое состояние)");
					}
					builder.append(System.lineSeparator());
					
				}
				builder.append("В итоге имеем:" + System.lineSeparator()).append(this.getPrettyStringStatesRepresentation()).append(System.lineSeparator() + System.lineSeparator());
				writer.println(builder);
				builder = new StringBuilder();
				
			}
		}
		builder.append("Множество O пусто, заканчиваем вычисление состояний." + System.lineSeparator());
		writer.println(builder);		
	}
	
	private String getPrettyStringStatesRepresentation() {
		StringBuilder builder = new StringBuilder("Множество непросмотренных (открытых) состояний O = ") 
		.append(this.getStringStatesRepresentation(nonObservedStates))
		.append(", множество просмотренных (закрытых) состояний C = ")
		.append(this.getStringStatesRepresentation(observedStates));
		return builder.toString();
	}
	
	private String getStringStatesRepresentation(Set<Set<Situation>> situationsSet) {
		StringBuilder builder = new StringBuilder("[");
		Set<Integer> statesIndeces = new TreeSet<Integer>();
		for(Set<Situation> state : situationsSet) {
			Integer index = this.states.indexOf(state);
			if (index != -1) {
				statesIndeces.add(index);
			}
		}
		Iterator<Integer> statesIndecesIterator = statesIndeces.iterator();
		boolean flag = false;
		while (statesIndecesIterator.hasNext()) {
			flag = true;
			builder.append("q").append(statesIndecesIterator.next()).append(", ");
		}
		if (situationsSet.contains(new HashSet<Situation>())) {
			builder.append("[]");
			flag = false;
		}
		if (flag) {
			builder.deleteCharAt(builder.length() - 1);
			builder.deleteCharAt(builder.length() - 1);
		}
		builder.append("]");
		return builder.toString();
	}
	
	public void printStateList() {
		StringBuilder builder = new StringBuilder("Список состояний:" + System.lineSeparator());
		int i = 0;
		for (Set<Situation> state : this.states) {
			builder.append("q").append(i++).append(" = ").append(state).append(System.lineSeparator());
		}
		if (this.emptyStateGained) {
			builder.append("[]" + System.lineSeparator());
		}
		this.writer.println(builder);
	}
	

	
	public boolean testStateFailed(Set<Situation> state) {
		boolean result = false;
		for (Situation s : state) {
			for (Situation t : state) {
				if (!s.equals(t)) {
					result |= (s.getFollowSymbol().equals(t.getFollowSymbol())
							&& s.getSituationRule().getRuleAlternatives().get(0).isReduceSituationChain() && t
							.getSituationRule().getRuleAlternatives().get(0).isReduceSituationChain());
						
					if (result)
						return true;
					
					result = false;
					
					result |= s.getSituationRule().getRuleAlternatives().get(0).isReduceSituationChain()
							&& t.getSituationRule().getRuleAlternatives().get(0).isPopSituationChain();
					
					Chain chain = new Chain(t.getSituationRule().getRuleAlternatives().get(0).getSubchainAfterPositionMarker());
					chain.add(t.getFollowSymbol());
					result = result && firstFunctionComputer.computeForChain(chain).contains(s.getFollowSymbol());
					
					if (result)
						return true;
					
				}
			}
		}
		return result;
	}
	
	
	public boolean performStatesTest() {
		StringBuilder builder = new StringBuilder();
		boolean generalTestSuccesed = false;
		int i = 0;
		for (Set<Situation> state : this.states) {
			boolean testFailed = testStateFailed(state); 
			builder.append("Тест для q").append(i++).append(": ").append(testFailed ? "fail - shit happens :(" : "ok").append(System.lineSeparator());
			generalTestSuccesed |= testFailed;
		}
		writer.println(builder);
		return generalTestSuccesed;
	}
	
	public void printGTable() {
		StringBuilder builder = new StringBuilder("g   |");
		Set<Symbol> symbols = new TreeSet<Symbol>(this.grammar.getAllSymbolsWithoutAxiom());
		
		for (Symbol symbol : symbols) {
			builder.append(String.format("%10s|", symbol.toString()));
		}
		builder.append(System.lineSeparator() + "------------------------------------------------------------------------" + System.lineSeparator());
		
		int i = 0;
		for (Map<Symbol, Integer> gmap : this.g) {
			builder.append(String.format("%4s|", "q" + i++));
			for (Symbol symbol : symbols) {
				builder.append(String.format("%10s|", (gmap.get(symbol) != null) ? gmap.get(symbol).toString() : "[]"));
			}
			builder.append(System.lineSeparator());
		}
		
		writer.println(builder);
	}
	
	public void printFTable() {
		StringBuilder builder = new StringBuilder("f   |");
		Set<TerminalSymbol> symbols = new TreeSet<TerminalSymbol>(this.grammar.getAllTerminalSymbols());
		symbols.add(Symbol.EMPTY_CHAIN_SYMBOL);
		
		for (TerminalSymbol symbol : symbols) {
			builder.append(String.format("%15s|", symbol.toString()));
		}
		builder.append(System.lineSeparator() + "------------------------------------------------------------------------" + System.lineSeparator());
		
		int i = 0;
		for (Map<TerminalSymbol, Integer> fmap : this.f) {
			builder.append(String.format("%4s|", "q" + i++));
			for (TerminalSymbol symbol : symbols) {
				String s = "ошибка";
				if (fmap.get(symbol) != null) {
					if (fmap.get(symbol) == 0) {
						s = "допуск";
					} else if (fmap.get(symbol) > 0) {
						s = "свертка: " + fmap.get(symbol);
					} else if (fmap.get(symbol) == -2) {
						s = "перенос";
					}
				}
				
				builder.append(String.format("%15s|", s));
			}
			builder.append(System.lineSeparator());
		}
		
		writer.println(builder);
	}
	
	
	

}
