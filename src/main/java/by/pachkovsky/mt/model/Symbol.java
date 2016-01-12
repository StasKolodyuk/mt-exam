package by.pachkovsky.mt.model;

import java.io.Serializable;

public class Symbol implements Cloneable, Comparable<Symbol>, Serializable {
	private static final long serialVersionUID = 1534561898406538841L;
	
	private static final char RULE_ARROW_CHAR = '~';
	private static final char RULE_ALTERNATIVE_CHAR = '|';
	private static final char EMPTY_CHAIN_CHAR = '?';	
	
	public static final Symbol RULE_ARROW_SYMBOL = new Symbol(RULE_ARROW_CHAR);
	public static final Symbol RULE_ALTERNATIVE_SYMBOL = new Symbol(RULE_ALTERNATIVE_CHAR);
	public static final TerminalSymbol EMPTY_CHAIN_SYMBOL = new TerminalSymbol(EMPTY_CHAIN_CHAR);
	
	private Character character;
	
	protected Symbol(Character character) {
		this.character = character;
	}
	
	public Character getCharacter() {
		return character;
	}

	public void setCharacter(Character character) {
		this.character = character;
	}
	
	public boolean isEmptyChainSymbol() {
		return this.getCharacter().equals(EMPTY_CHAIN_SYMBOL);
	}
	
	public static Symbol of(Character character) {
		if (Character.isLetter(character) && Character.isUpperCase(character) ||  character.equals('@')) {
			return new NonTerminalSymbol(character);
		} else if (character.equals(EMPTY_CHAIN_CHAR)) {
			return EMPTY_CHAIN_SYMBOL;
		} else if (character.equals(RULE_ARROW_CHAR)) {
			return RULE_ARROW_SYMBOL;
		} else if (character.equals(RULE_ALTERNATIVE_SYMBOL)) {
			return RULE_ALTERNATIVE_SYMBOL;
		} else {
			return new TerminalSymbol(character);
		}
	}	

	@Override
	public int compareTo(Symbol symbol) {
		return (this.getCharacter().compareTo(symbol.getCharacter()));
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public String toString() {
		return this.character.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((character == null) ? 0 : character.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Symbol other = (Symbol) obj;
		if (character == null) {
			if (other.character != null) {
				return false;
			}
		} else if (!character.equals(other.character)) {
			return false;
		}
		return true;
	}
	
}
