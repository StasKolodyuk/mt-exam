package by.pachkovsky.mt.model;

public class Situation {

	private Rule situationRule;
	private TerminalSymbol followSymbol;
	
	public Situation(Rule situationRule, TerminalSymbol followSymbol) {
		this.situationRule = situationRule;
		this.followSymbol = followSymbol;
	}	
	
	public Situation(Rule situationRule, Integer position, TerminalSymbol followSymbol) {
		this.situationRule = situationRule;
		this.situationRule.getRuleAlternatives().get(0).setPosition(position);
		this.followSymbol = followSymbol;
	}
	
	public Situation(NonTerminalSymbol nonTerminalSymbol, Chain chain, Integer position, TerminalSymbol followSymbol) {
		Chain newChain = new Chain(chain.toArray(new Symbol[]{}));
		newChain.setPosition(position);
		this.situationRule = Rule.of(nonTerminalSymbol, newChain);
		this.followSymbol = followSymbol;
	}

	public Rule getSituationRule() {
		return situationRule;
	}

	public void setSituationRule(Rule situationRule) {
		this.situationRule = situationRule;
	}

	public TerminalSymbol getFollowSymbol() {
		return followSymbol;
	}

	public void setFollowSymbol(TerminalSymbol followSymbol) {
		this.followSymbol = followSymbol;
	}

	@Override
	public String toString() {
		StringBuilder situationStringBuilder = new StringBuilder();
		situationStringBuilder.append("(").append(situationRule).append(", ").append(followSymbol).append(")");
		return situationStringBuilder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((followSymbol == null) ? 0 : followSymbol.hashCode());
		result = prime * result + ((situationRule == null) ? 0 : situationRule.hashCode());
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
		Situation other = (Situation) obj;
		if (followSymbol == null) {
			if (other.followSymbol != null) {
				return false;
			}
		} else if (!followSymbol.equals(other.followSymbol)) {
			return false;
		}
		if (situationRule == null) {
			if (other.situationRule != null) {
				return false;
			}
		} else if (!situationRule.equals(other.situationRule)) {
			return false;
		}
		return true;
	}
	
	
}
