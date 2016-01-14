package by.pachkovsky.mt.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import by.pachkovsky.mt.exceptions.GrammarConsistencyException;

public class Grammar {

	private NonTerminalSymbol axiom;
	private Map<NonTerminalSymbol, List<Chain>> rules;
	private boolean isAugmented = false;
	
	public NonTerminalSymbol getAxiom() {
		return axiom;
	}

	public void setAxiom(NonTerminalSymbol axiom) throws GrammarConsistencyException {
		if (this.rules.containsKey(axiom)) {
			this.axiom = axiom;
		} else {
			throw new GrammarConsistencyException(String.format("Non-terminal symbol '%s' cannot be considered as the grammar's axiom.", axiom.toString()));
		}
	}
	
	public Map<NonTerminalSymbol, List<Chain>> getRules() {
		return rules;
	}
	
	public List<Rule> getEnumeratedRules() {
		List<Rule> enumeratedRules = new ArrayList<Rule>();
		for (NonTerminalSymbol nonTerminalSymbol : this.rules.keySet()) {
			for (Chain ruleAlternative : this.rules.get(nonTerminalSymbol)) {
				enumeratedRules.add(Rule.of(nonTerminalSymbol, ruleAlternative));
			}
		}
		return enumeratedRules;
	}

	public void setRules(Map<NonTerminalSymbol, List<Chain>> rules) {
		this.rules = rules;
	}
	
	public void addRule(Rule rule) {
		if (this.rules.containsKey(rule.getNonTerminalSymbol())) {
			this.rules.get(rule.getNonTerminalSymbol()).addAll(rule.getRuleAlternatives());
		} else {
			this.rules.put(rule.getNonTerminalSymbol(), rule.getRuleAlternatives());
		}
	}
	
	public Set<Symbol> getAllSymbols() {
		Set<Symbol> allSymbols = new TreeSet<Symbol>();
		allSymbols.addAll(this.getAllNonTerminalSymbols());
		allSymbols.addAll(this.getAllTerminalSymbols());
		return allSymbols;
	}
	
	public Set<Symbol> getAllSymbolsWithoutAxiom() {
		Set<Symbol> allSymbols = new TreeSet<Symbol>();
		allSymbols.addAll(this.getAllNonTerminalSymbolsWithoutAxiom());
		allSymbols.addAll(this.getAllTerminalSymbols());
		return allSymbols;
	}
	
	public Set<TerminalSymbol> getAllTerminalSymbols() {
		Set<TerminalSymbol> allTerminalSymbols = new TreeSet<TerminalSymbol>();
		for (List<Chain> chainsList : this.getRules().values()) {
			for (Chain chain : chainsList) {
				for (Symbol symbol : chain) {
					if (symbol instanceof TerminalSymbol)
						if (!((TerminalSymbol) symbol).equals(Symbol.EMPTY_CHAIN_SYMBOL)) {
							allTerminalSymbols.add((TerminalSymbol) symbol);
						}
				}
			}
		}
		return allTerminalSymbols;
	}
	
	
	public Set<NonTerminalSymbol> getAllNonTerminalSymbols() {
		return this.getRules().keySet();
	}
	
	public Set<NonTerminalSymbol> getAllNonTerminalSymbolsWithoutAxiom() {
		Set<NonTerminalSymbol> allNonTerminalsSymbols = new TreeSet<NonTerminalSymbol>(this.getRules().keySet());
		allNonTerminalsSymbols.remove(this.axiom);
		return allNonTerminalsSymbols;
	}
	
	public void augmentWithAxiom(NonTerminalSymbol augmentAxiom) throws GrammarConsistencyException {
		if (rules.containsKey(augmentAxiom)) {
			throw new GrammarConsistencyException(String.format("Unnable to augment the grammar because a new axiom '%s' non-terminal symbol is already appears in the grammar's non-terminal symbols set."));
		}
		this.addRule(Rule.of(augmentAxiom, new Chain(this.axiom)));
		this.setAxiom(augmentAxiom);
		this.isAugmented = true;
	}

	public boolean isAugmented() {
		return isAugmented;
	}
	
	public Situation getInitialSituation() {
		if (this.isAugmented()) {
			return new Situation(Rule.of(axiom, new Chain(this.rules.get(this.axiom).get(0))), 0, Symbol.EMPTY_CHAIN_SYMBOL);
		} else {
			return null;
		}
	}
	
	private String toStringWithoutRules() {
		StringBuilder grammarStringBuilder = new StringBuilder();
		if (!this.isAugmented()) {
			grammarStringBuilder.append("Грамматика G = (N = ")
				.append(this.getAllNonTerminalSymbols()).append(", Σ = ")
				.append(this.getAllTerminalSymbols()).append(", P, ")
				.append(this.axiom).append("), ").append("аксиома '").append(this.axiom).append("'" + System.lineSeparator());
		} else {
			Set<NonTerminalSymbol> nonTerminalsWithoutAxiom = new TreeSet<NonTerminalSymbol>(this.getAllNonTerminalSymbols());
			nonTerminalsWithoutAxiom.remove(this.axiom);
			grammarStringBuilder.append("Пополненная грамматика G' = (N = ")
				.append(nonTerminalsWithoutAxiom).append(" U ").append("[")
				.append(this.axiom).append("]").append(", Σ = ")
				.append(this.getAllTerminalSymbols()).append(", P U ").append("[")
				.append(Rule.of(this.axiom, this.getRules().get(this.axiom)))						
				.append("], ")
				.append(this.axiom).append("), ").append("аксилма '").append(this.axiom).append("'" + System.lineSeparator());
		}
		return grammarStringBuilder.toString();
	}
	

	@Override
	public String toString() {
		StringBuilder grammarStringBuilder = new StringBuilder(this.toStringWithoutRules()).append("Множество правил P:" + System.lineSeparator());
		for (Map.Entry<NonTerminalSymbol, List<Chain>> entry : rules.entrySet()) {
			grammarStringBuilder.append(Rule.of(entry)).append(System.lineSeparator());
		}
		return grammarStringBuilder.toString();
	}
	
	public String toStringWithRulesEnumerated() {
		StringBuilder grammarStringBuilder = new StringBuilder(this.toStringWithoutRules()).append("Пронумерованный список правил из P:" + System.lineSeparator());
		List<Rule> enumeratedRules = this.getEnumeratedRules();
		for (int i = 0; i < enumeratedRules.size(); i++) {
			grammarStringBuilder.append(i).append(": ").append(enumeratedRules.get(i)).append(System.lineSeparator());
		}
		return grammarStringBuilder.toString();
	}

}
