package by.pachkovsky.mt.logic;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

import by.pachkovsky.mt.exceptions.GrammarConsistencyException;
import by.pachkovsky.mt.model.Chain;
import by.pachkovsky.mt.model.Grammar;
import by.pachkovsky.mt.model.NonTerminalSymbol;
import by.pachkovsky.mt.model.Rule;

public class GrammarBuilder {
	
	private Map<NonTerminalSymbol, List<Chain>> rules;
	private Set<NonTerminalSymbol> leftSideNonTerminalSymbols;
	private Set<NonTerminalSymbol> chainNonTerminalSymbols; 
	
	public GrammarBuilder() {
		this.rules = new TreeMap<NonTerminalSymbol, List<Chain>>();
		this.leftSideNonTerminalSymbols = new TreeSet<NonTerminalSymbol>();
		this.chainNonTerminalSymbols = new TreeSet<NonTerminalSymbol>();
	}
	
	public void addRule(Rule rule) {
		this.leftSideNonTerminalSymbols.add(rule.getNonTerminalSymbol());
		this.chainNonTerminalSymbols.addAll(rule.getRuleAlternativesNonTerminalSymbols());
		if (this.rules.containsKey(rule.getNonTerminalSymbol())) {
			this.rules.get(rule.getNonTerminalSymbol()).addAll(rule.getRuleAlternatives());
		} else {
			this.rules.put(rule.getNonTerminalSymbol(), rule.getRuleAlternatives());
		}
	}
		
	public Grammar constructGrammar(NonTerminalSymbol axiom) throws GrammarConsistencyException {
		this.chainNonTerminalSymbols.removeAll(leftSideNonTerminalSymbols);
		if (!this.chainNonTerminalSymbols.isEmpty()) {
			String unretainedNonTerminalSymbolsString = StringUtils.join(this.chainNonTerminalSymbols.toArray(), ",");
			throw new GrammarConsistencyException(String.format(
					"The grammar is not consistent: the following non-terminal symbols are not appear in the left side of the rules: %s", 
					unretainedNonTerminalSymbolsString));
		}
		
		Grammar grammar = new Grammar();
		grammar.setRules(rules);
		grammar.setAxiom(axiom);
		
		return grammar;		
	}
	

}
