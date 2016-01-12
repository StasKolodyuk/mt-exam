package by.pachkovsky.mt.logic;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import by.pachkovsky.mt.model.Chain;
import by.pachkovsky.mt.model.Grammar;
import by.pachkovsky.mt.model.NonTerminalSymbol;
import by.pachkovsky.mt.model.Rule;
import by.pachkovsky.mt.model.Situation;
import by.pachkovsky.mt.model.Symbol;
import by.pachkovsky.mt.model.TerminalSymbol;

public class SituationClosureComputer {

	private Grammar grammar;
	private FirstFunctionComputer firstFunctionComputer;
	private List<Rule> enumeratedRules;

	public SituationClosureComputer(Grammar grammar, FirstFunctionComputer firstFunctionComputer) {
		this.grammar = grammar;
		this.firstFunctionComputer = firstFunctionComputer;
		if (!this.firstFunctionComputer.isComputationComplete()) {
			this.firstFunctionComputer.forceComputation();
		}
		this.enumeratedRules = this.grammar.getEnumeratedRules();
	}

	public Set<Situation> closureForSituation(Situation situation) {
		Set<Situation> closureSet = new HashSet<Situation>();
		Chain subchain = situation.getSituationRule().getRuleAlternatives().get(0).getSubchainAfterPositionMarker();
		if (subchain.hasLeadingNonTerminal()) {
			NonTerminalSymbol nonTerminalSymbol = subchain.getLeadingNonTerminalSymbol();
			Chain deltaUChain = new Chain(subchain.subList(1, subchain.size()));
			deltaUChain.add(situation.getFollowSymbol());
			Set<TerminalSymbol> firstOfDeltaUChain = this.firstFunctionComputer.computeForChain(deltaUChain);
			for (TerminalSymbol terminalSymbol : firstOfDeltaUChain) {
				List<Chain> chainList = this.grammar.getRules().get(nonTerminalSymbol);
				for (Chain chain : chainList) {
					closureSet.add(new Situation(nonTerminalSymbol, chain, 0, terminalSymbol));
				}
			}
		}
		return closureSet;
	}

	public Set<Situation> closure(Set<Situation> situations) {
		Set<Situation> notObservedSituations = new HashSet<Situation>(situations);
		Set<Situation> observedSituations = new HashSet<Situation>();

		while (!notObservedSituations.isEmpty()) {
			Iterator<Situation> situationsIterator = notObservedSituations.iterator();
			if (situationsIterator.hasNext()) {
				Situation currentSituation = situationsIterator.next();
				situationsIterator.remove();
				observedSituations.add(currentSituation);

				Set<Situation> closureForCurrentSituation = this.closureForSituation(currentSituation);
				closureForCurrentSituation.removeAll(observedSituations);
				notObservedSituations.addAll(closureForCurrentSituation);
			}
		}
		return observedSituations;
	}

	public Set<Situation> closure(Situation situation) {
		Set<Situation> oneElementSet = new HashSet<Situation>();
		oneElementSet.add(situation);
		return this.closure(oneElementSet);
	}
	
	public Set<Situation> mu(Situation situation, Symbol symbol) {
		Set<Situation> oneSituationSet = new HashSet<Situation>();
		oneSituationSet.add(situation);
		return this.mu(oneSituationSet, symbol);
	}
	
	public Set<Situation> mu(Set<Situation> situations, Symbol symbol) {
		Set<Situation> resultSituationSet = new HashSet<Situation>();
		for (Situation situation : situations) {
			Chain situationRuleChain = situation.getSituationRule().getRuleAlternatives().get(0);
			Symbol leadingSymbol = situationRuleChain.getSubchainAfterPositionMarker().getLeadingSymbol();
			if (leadingSymbol.equals(symbol)) {
				Chain newChain = new Chain(situationRuleChain);
				newChain.setPosition(situationRuleChain.getPosition() + 1);
				resultSituationSet.add(new Situation(Rule.of(situation.getSituationRule().getNonTerminalSymbol(),
						newChain), situation.getFollowSymbol()));
			}
		}
		return resultSituationSet;
	}

}