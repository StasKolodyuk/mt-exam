package by.pachkovsky.mt.model;

public class NonTerminalSymbol extends Symbol  {
	private static final long serialVersionUID = 799812630593158203L;

	private boolean isAxiom;
	
	public NonTerminalSymbol(Character character) {
		super(character);
	}
	
	public boolean isAxiom() {
		return isAxiom;
	}

	public void setAxiom(boolean isAxiom) {
		this.isAxiom = isAxiom;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (isAxiom ? 1231 : 1237);
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
		NonTerminalSymbol other = (NonTerminalSymbol) obj;
		if (isAxiom != other.isAxiom) {
			return false;
		}
		return true;
	}

	
}
