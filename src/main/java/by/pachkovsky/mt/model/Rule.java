package by.pachkovsky.mt.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.tuple.MutablePair;

public class Rule extends MutablePair<NonTerminalSymbol, List<Chain>> {

	private Rule(NonTerminalSymbol nonTerminalSymbol, List<Chain> ruleAlternatives) {
		super(nonTerminalSymbol, ruleAlternatives);
	}
	
	public static Rule of(NonTerminalSymbol nonTerminalSymbol, List<Chain> ruleAlternatives) {
		return new Rule(nonTerminalSymbol, ruleAlternatives);
	}
	
	public static Rule of(NonTerminalSymbol nonTerminalSymbol, Chain ruleAlternative) {
		Rule rule = new Rule(nonTerminalSymbol, new ArrayList<Chain>());
		rule.addRuleAlternative(ruleAlternative);
		return rule;
	}
	
	public static Rule of(Map.Entry<NonTerminalSymbol, List<Chain>> rulesMapEntry) {
		return new Rule(rulesMapEntry.getKey(), rulesMapEntry.getValue());
	}
	
	public void addRuleAlternative(Chain ruleAlternative) {
		this.getValue().add(ruleAlternative);
	}
	
	public NonTerminalSymbol getNonTerminalSymbol() {
		return this.getKey();
	}
	
	public List<Chain> getRuleAlternatives() {
		return this.getValue();
	}
	
	public Set<NonTerminalSymbol> getRuleAlternativesNonTerminalSymbols() {
		Set<NonTerminalSymbol> nonTerminalSymbolsSet = new TreeSet<NonTerminalSymbol>();
		for (Chain chain : this.getRuleAlternatives()) {
			nonTerminalSymbolsSet.addAll(chain.getNonTerminalSymbols());
		}
		return nonTerminalSymbolsSet;
	}

	@Override
	public String toString() {
		StringBuilder ruleStringBuilder = new StringBuilder().append(this.getNonTerminalSymbol())
				.append(Symbol.RULE_ARROW_SYMBOL);
		
		Iterator<Chain> ruleAlternativesIterator = this.getRuleAlternatives().iterator();
		
		if (ruleAlternativesIterator.hasNext()) {
			ruleStringBuilder.append(ruleAlternativesIterator.next());
		}
		
		while (ruleAlternativesIterator.hasNext()) {
			ruleStringBuilder.append(Symbol.RULE_ALTERNATIVE_SYMBOL)
				.append(ruleAlternativesIterator.next());
		}
		
		return ruleStringBuilder.toString();
	}
	
	

}
