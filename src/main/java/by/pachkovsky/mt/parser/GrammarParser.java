package by.pachkovsky.mt.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import by.pachkovsky.mt.exceptions.GrammarConsistencyException;
import by.pachkovsky.mt.exceptions.GrammarParseException;
import by.pachkovsky.mt.logic.GrammarBuilder;
import by.pachkovsky.mt.model.Chain;
import by.pachkovsky.mt.model.Grammar;
import by.pachkovsky.mt.model.NonTerminalSymbol;
import by.pachkovsky.mt.model.Rule;
import by.pachkovsky.mt.model.Symbol;


public class GrammarParser {
	
	private static final String RULE_REGEXP_STRING_MATCH_PATTERN = 
			"([A-Z])" + Symbol.RULE_ARROW_SYMBOL + 
			"([\\S&&[^\\" + Symbol.RULE_ALTERNATIVE_SYMBOL + "]]+)" +
			"(\\" + Symbol.RULE_ALTERNATIVE_SYMBOL +
			"([\\S&&[^\\" + Symbol.RULE_ALTERNATIVE_SYMBOL +"]]+))*";
	
	private static final String RULE_REGEXP_STRING_PATTERN = 
			"([A-Z])" + Symbol.RULE_ARROW_SYMBOL + 
			"([\\S&&[^\\" + Symbol.RULE_ALTERNATIVE_SYMBOL + "]]+)" +
			"((\\S+)?)";
	
	private static final String RULE_ALTERNATIVE_STRING_PATTERN = 
			"(\\" + Symbol.RULE_ALTERNATIVE_SYMBOL + "([\\S&&[^\\" + Symbol.RULE_ALTERNATIVE_SYMBOL + "]]+))";
		
	private static final Pattern RULE_REGEXP_MATCH_PATTERN = Pattern.compile(RULE_REGEXP_STRING_MATCH_PATTERN);
	private static final Pattern RULE_REGEXP_PATTER = Pattern.compile(RULE_REGEXP_STRING_PATTERN);
	private static final Pattern RULE_ALTERNATIVE_PATTERN = Pattern.compile(RULE_ALTERNATIVE_STRING_PATTERN);
	
	public static Rule parseRule(String source) throws GrammarParseException {
		if (RULE_REGEXP_MATCH_PATTERN.matcher(source).matches()) {
			Matcher ruleMatcher = RULE_REGEXP_PATTER.matcher(source);

			ruleMatcher.find();
			NonTerminalSymbol nonTerminalSymbol = new NonTerminalSymbol(ruleMatcher.group(1).charAt(0));

			List<Chain> ruleAlternatives = new ArrayList<Chain>();
			ruleAlternatives.add(new Chain(ruleMatcher.group(2)));

			Matcher ruleOtherAlternativesMatcher = RULE_ALTERNATIVE_PATTERN.matcher(ruleMatcher.group(3));
			while (ruleOtherAlternativesMatcher.find()) {
				ruleAlternatives.add(new Chain(ruleOtherAlternativesMatcher.group(2)));
			}

			return Rule.of(nonTerminalSymbol, ruleAlternatives);		
		} else {
			throw new GrammarParseException(String.format("Source string '%s' does not match rule pattern.", source));
		}
	}
	
	public static Grammar parseGrammar(Reader reader) throws GrammarParseException, GrammarConsistencyException {
		try {
			BufferedReader bufferedReader = new BufferedReader(reader);
			NonTerminalSymbol axiom = new NonTerminalSymbol(bufferedReader.readLine().charAt(0));
			
			String sourceRuleLine = null;
			GrammarBuilder grammarBuilder = new GrammarBuilder();
			
			while ((sourceRuleLine = bufferedReader.readLine()) != null) {
				Rule rule = GrammarParser.parseRule(sourceRuleLine);
				grammarBuilder.addRule(rule);
			}
			
			return grammarBuilder.constructGrammar(axiom);
			
		} catch (IOException exception) {
			throw new GrammarParseException("An I/O error occured on grammar parsing. Check input, bro.");
		}
	}

}
