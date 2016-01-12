package by.pachkovsky.mt.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.tuple.Pair;

public class Chain extends ArrayList<Symbol> {
	
	private static final String POSITION_MARKER = "\u2022";
	
	private Integer position = null;
	
	public Chain(String source) {
		char[] chars = new char[source.length()];
		source.getChars(0, source.length(), chars, 0);
			
		for (char c : chars) {
			this.add(Symbol.of(c));
		}
	}
	
	public Chain(Symbol[] symbolsArray) {
		for (Symbol symbol : symbolsArray) {
			this.add(symbol);
		}
	}
	
	public Chain(List<Symbol> symbolsList) {
		this(symbolsList.toArray(new Symbol[0]));
	}
	
	public Chain(Symbol symbol) {
		this.add(symbol);
	}
	
	public static final Chain EMPTY_CHAIN = new Chain(Symbol.EMPTY_CHAIN_SYMBOL);

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}
	
	public Chain getSubchainBeforePositionMarker() {
		if (this.position != null) {
			Chain chain = new Chain(this.subList(0, this.position));
			if (chain.size() == 0) {
				return Chain.EMPTY_CHAIN;
			} else {
				return chain;
			}
		} else {
			return null;
		}
	}
	
	public Chain getSubchainAfterPositionMarker() {
		if (this.position != null) {
			Chain chain = new Chain(this.subList(this.position, this.size()));
			if (chain.size() == 0) {
				return Chain.EMPTY_CHAIN;
			} else {
				return chain;
			}
		} else {
			return null;
		}
	}
	

	public Set<NonTerminalSymbol> getNonTerminalSymbols() {
		Set<NonTerminalSymbol> nonTerminalSymbolsSet = new TreeSet<NonTerminalSymbol>();
		for (Symbol symbol : this) {
			if (symbol instanceof NonTerminalSymbol) {
				nonTerminalSymbolsSet.add((NonTerminalSymbol) symbol);
			}
		}
		return nonTerminalSymbolsSet;
	}
	
	public boolean hasLeadingNonTerminal() {
		return this.get(0) instanceof NonTerminalSymbol;
	}
	
	public NonTerminalSymbol getLeadingNonTerminalSymbol() {
		if (this.hasLeadingNonTerminal()) {
			return (NonTerminalSymbol) this.get(0);
		} else {
			return null;
		}
	}
	
	public Symbol getLeadingSymbol() {
		if (this.size() == 0) {
			return Symbol.EMPTY_CHAIN_SYMBOL;
		}
		return this.get(0);
	}
	
	
	
	public List<Pair<NonTerminalSymbol, Chain>> getRightBetaContexts() {
		List<Pair<NonTerminalSymbol, Chain>> rightBetaContexts = new ArrayList<Pair<NonTerminalSymbol, Chain>>();
		for (int i = 0; i < this.size(); i++) {
			if (this.get(i) instanceof NonTerminalSymbol) {
				if (i + 1 < this.size()) {
					rightBetaContexts.add(Pair.of((NonTerminalSymbol) this.get(i), 
							new Chain(this.subList(i + 1, this.size()))));
				} else {
					rightBetaContexts.add(Pair.of((NonTerminalSymbol) this.get(i), 
							Chain.EMPTY_CHAIN));
				}
			}
		}
		return rightBetaContexts;
	}
	
	public boolean isReduceSituationChain() {
		return (this.size() == this.position) || (this.getSubchainAfterPositionMarker().equals(Chain.EMPTY_CHAIN));
	}
	
	public boolean isPopSituationChain() {
		return ((this.size() > this.position)
				&& (this.getSubchainAfterPositionMarker().getLeadingSymbol() instanceof TerminalSymbol)
				&& !this.getSubchainAfterPositionMarker().equals(Chain.EMPTY_CHAIN));
	}
	
	@Override
	public String toString() {
		StringBuilder chainStringBuilder = new StringBuilder();
		for (Symbol symbol : this) {
			chainStringBuilder.append(symbol);
		}
		if (position != null) {
			chainStringBuilder.insert(position,  POSITION_MARKER);
		}
		return chainStringBuilder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((position == null) ? 0 : position.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Chain other = (Chain) obj;
		if (position == null) {
			if (other.position != null) {
				return false;
			}
		} else if (!position.equals(other.position)) {
			return false;
		}
		return true;
	}
	
	
	
}
