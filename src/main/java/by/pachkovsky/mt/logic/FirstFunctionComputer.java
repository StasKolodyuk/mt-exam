package by.pachkovsky.mt.logic;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import by.pachkovsky.mt.model.Chain;
import by.pachkovsky.mt.model.Grammar;
import by.pachkovsky.mt.model.NonTerminalSymbol;
import by.pachkovsky.mt.model.Symbol;
import by.pachkovsky.mt.model.TerminalSymbol;

public class FirstFunctionComputer {
	
	private Grammar grammar;
	private Map<NonTerminalSymbol, Set<TerminalSymbol>> firstMap;
	private Set<NonTerminalSymbol> nonTerminalSymbolsSet;
	private boolean isComputationComplete = false;
	
	public FirstFunctionComputer(Grammar grammar) {
		this.grammar = grammar;
		this.nonTerminalSymbolsSet = grammar.getRules().keySet();
		this.firstMap = new TreeMap<NonTerminalSymbol, Set<TerminalSymbol>>();
		for (NonTerminalSymbol nonTerminalSymbol : nonTerminalSymbolsSet) {
			firstMap.put(nonTerminalSymbol, new TreeSet<TerminalSymbol>());
		}		
	}
	
	public boolean isComputationComplete() {
		return isComputationComplete;
	}
	
	public Map<NonTerminalSymbol, Set<TerminalSymbol>> nextIteration() {	
		boolean firstMapChanged = false;
		for (NonTerminalSymbol nonTerminalSymbol : nonTerminalSymbolsSet) {
			if (grammar.getRules().get(nonTerminalSymbol).contains(Chain.EMPTY_CHAIN)) {
				firstMapChanged |= firstMap.get(nonTerminalSymbol).add(Symbol.EMPTY_CHAIN_SYMBOL);
			}
			
			for (Chain chain : grammar.getRules().get(nonTerminalSymbol)) {
				if (!chain.equals(Chain.EMPTY_CHAIN)) {
					
					firstMapChanged |= firstMap.get(nonTerminalSymbol).addAll(getFirstSetForSymbol(chain.get(0)));
					for (int i = 1; i < chain.size(); i++) {
					
						if (getFirstSetForSymbol(chain.get(i - 1)).contains(Symbol.EMPTY_CHAIN_SYMBOL)) {
							firstMapChanged |= firstMap.get(nonTerminalSymbol).addAll(getFirstSetForSymbol(chain.get(i)));
							if (i == chain.size() - 1) {
								firstMapChanged |= firstMap.get(nonTerminalSymbol).add(Symbol.EMPTY_CHAIN_SYMBOL);
							}
						} else {
							break;
						}
					}
				}				
			}	
		}	
		this.isComputationComplete |= !firstMapChanged;
		return firstMap;		
	}
	
	public Set<TerminalSymbol> getFirstSetForSymbol(Symbol symbol) {		
		if (symbol instanceof TerminalSymbol) {
			return Collections.singleton((TerminalSymbol) symbol);
		} else if (symbol instanceof NonTerminalSymbol) {
			return firstMap.get((NonTerminalSymbol) symbol);
		} else {
			return Collections.emptySet();
		}
	}	
	
	public Map<NonTerminalSymbol, Set<TerminalSymbol>> forceComputation() {
		while (!this.isComputationComplete()) {
			this.nextIteration();
		}
		return this.firstMap;
	}
	
	public Map<NonTerminalSymbol, Set<TerminalSymbol>> getResult() {
		return firstMap;
	}	
	
	public Set<TerminalSymbol> computeForChain(Chain chain) {
		Set<TerminalSymbol> firstSet = new TreeSet<TerminalSymbol>();
		firstSet.addAll(this.getFirstSetForSymbol(chain.get(0)));
		if (chain.size() > 1) {
			firstSet.remove(Symbol.EMPTY_CHAIN_SYMBOL);
		}
		for (int i = 1; i < chain.size(); i++) {
			if (getFirstSetForSymbol(chain.get(i - 1)).contains(Symbol.EMPTY_CHAIN_SYMBOL)) {
				firstSet.addAll(this.getFirstSetForSymbol(chain.get(i)));
				firstSet.remove(Symbol.EMPTY_CHAIN_SYMBOL);
				if (i == chain.size() - 1) {
					firstSet.add(Symbol.EMPTY_CHAIN_SYMBOL);
				}
			} else {
				break;
			}
		}
		return firstSet;
	}
}
