package by.pachkovsky.mt.logic;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang3.tuple.Pair;

import by.pachkovsky.mt.model.Chain;
import by.pachkovsky.mt.model.Grammar;
import by.pachkovsky.mt.model.NonTerminalSymbol;
import by.pachkovsky.mt.model.Rule;
import by.pachkovsky.mt.model.Symbol;
import by.pachkovsky.mt.model.TerminalSymbol;

public class FollowFunctionComputor {

	private Grammar grammar;
	private Map<NonTerminalSymbol, Set<TerminalSymbol>> followMap;
	private Set<NonTerminalSymbol> nonTerminalSymbolsSet;
	private List<Rule> enumeratedRulesList;
	private FirstFunctionComputer firstFunctionComputer; 
	private boolean isComputationComplete = false;
	
	public FollowFunctionComputor(Grammar grammar, FirstFunctionComputer firstFunctionComputer) {
		this.grammar = grammar;
		this.firstFunctionComputer = firstFunctionComputer;
		if (!this.firstFunctionComputer.isComputationComplete()) {
			this.firstFunctionComputer.forceComputation();
		}
		this.enumeratedRulesList = grammar.getEnumeratedRules();
		this.nonTerminalSymbolsSet = grammar.getRules().keySet();
		this.followMap = new TreeMap<NonTerminalSymbol, Set<TerminalSymbol>>();
		for (NonTerminalSymbol nonTerminalSymbol : nonTerminalSymbolsSet) {
			followMap.put(nonTerminalSymbol, new TreeSet<TerminalSymbol>());
		}
		
		for (Rule rule : this.enumeratedRulesList) {
			List<Pair<NonTerminalSymbol, Chain>> rightBetaContexts = rule.getRuleAlternatives().get(0).getRightBetaContexts();
			for (Pair<NonTerminalSymbol, Chain> rightBetaContext : rightBetaContexts) {
				Set<TerminalSymbol> firstOfRightBetaContext = firstFunctionComputer.computeForChain(rightBetaContext.getValue());
				firstOfRightBetaContext.remove(Symbol.EMPTY_CHAIN_SYMBOL);
				this.followMap.get(rightBetaContext.getKey()).addAll(firstOfRightBetaContext);
			}
		}	
		
	}
	
	public boolean isComputationComplete() {
		return isComputationComplete;
	}
	
	public Map<NonTerminalSymbol, Set<TerminalSymbol>> nextIteration() {	
		boolean followMapChanged = false;
		for (Rule rule : this.enumeratedRulesList) {
			List<Pair<NonTerminalSymbol, Chain>> rightBetaContexts = rule.getRuleAlternatives().get(0).getRightBetaContexts();
			for (Pair<NonTerminalSymbol, Chain> rightBetaContext : rightBetaContexts) {
				if (firstFunctionComputer.computeForChain(rightBetaContext.getValue()).contains(Symbol.EMPTY_CHAIN_SYMBOL)) {
					followMapChanged |= this.followMap.get(rightBetaContext.getKey())
							.addAll(this.followMap.get(rule.getNonTerminalSymbol()));
				}
			}
		}
		this.isComputationComplete |= !followMapChanged;
		return followMap;
	}
	
	public Map<NonTerminalSymbol, Set<TerminalSymbol>> forceComputation() {
		while (!this.isComputationComplete()) {
			this.nextIteration();
		}
		return this.followMap;
	}
	
	public Map<NonTerminalSymbol, Set<TerminalSymbol>> getResult() {
		return followMap;
	}
	
}
