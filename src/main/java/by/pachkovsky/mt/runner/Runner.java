package by.pachkovsky.mt.runner;

import java.io.*;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import by.pachkovsky.mt.exceptions.MTException;
import by.pachkovsky.mt.logic.FirstFunctionComputer;
import by.pachkovsky.mt.logic.FollowFunctionComputor;
import by.pachkovsky.mt.logic.SituationClosureComputer;
import by.pachkovsky.mt.logic.StatesAccumulator;
import by.pachkovsky.mt.model.Grammar;
import by.pachkovsky.mt.model.NonTerminalSymbol;
import by.pachkovsky.mt.model.TerminalSymbol;
import by.pachkovsky.mt.parser.GrammarParser;

public class Runner {
	
	private static final String INPUT_FILENAME = "source-grammar.txt";
	private static final String OUTPUT_FILENAME = "output.txt";

	public static void main(String... args) {
		
				
		try {	
//			List<Rule> rules = new ArrayList<Rule>();
//			//String[] s = new String[] {"S~SaSb|?"};
//			String[] s = new String[] {"E~E+T", "E~T", "T~i", "T~(E)"};
//			for (String str : s) {
//				rules.add(GrammarParser.parseRule(str));
//				
//			}
//
//			GrammarBuilder builder = new GrammarBuilder();
//			for (Rule rule : rules) {
//				builder.addRule(rule);
//			}			
//			Grammar grammar = builder.constructGrammar(rules.get(0).getNonTerminalSymbol());

            if(args.length != 2) {
                throw new RuntimeException("Wrong Input Parameters");
            }

			Reader reader = new FileReader(args[0]);
			Grammar grammar = GrammarParser.parseGrammar(reader);
			reader.close();
			

			PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                    new FileOutputStream(args[1]), "UTF-8"));

			writer.println("Замечание: в [...] обозначается множество, в <...> обозначается замыкание.");
			writer.println(new Date());
			writer.println("-------- Исходная грамматика ---------------------------------------------------------------------------");
			writer.println(grammar);
			
			writer.println("-- №1 -- Пополнение грамматики -------------------------------------------------------------------------");
			grammar.augmentWithAxiom(new NonTerminalSymbol('@'));
			writer.println(grammar);
			
			writer.println("-- №1 -- Пополненая грамматика с пронумерованными правилами---------------------------------------------");
			writer.println(grammar.toStringWithRulesEnumerated());
			
			writer.println("-- №2-3 -- Вычисление First1 ---------------------------------------------------------------------------");
			FirstFunctionComputer firstFunctionComputer = new FirstFunctionComputer(grammar);
			int j = 1;
			StringBuilder firstsStringBuilder = new StringBuilder("Computation First1 function for all of the non-terminal symbols of the grammar. " + System.lineSeparator());
			while (!firstFunctionComputer.isComputationComplete()) {
				Map<NonTerminalSymbol, Set<TerminalSymbol>> currentFirstValues = firstFunctionComputer.nextIteration(); 
				firstsStringBuilder.append("Iteration ").append(j++).append(": ");
				for (NonTerminalSymbol nonTerminalSymbol : currentFirstValues.keySet()) {
					firstsStringBuilder.append("First1(").append(nonTerminalSymbol).append(") = ")
						.append(currentFirstValues.get(nonTerminalSymbol)).append("; ");
				}
				firstsStringBuilder.append(System.lineSeparator());
			}
			firstsStringBuilder.append(System.lineSeparator() + "Result for First1 function:" + System.lineSeparator());
			for (NonTerminalSymbol nonTerminalSymbol : firstFunctionComputer.getResult().keySet()) {
				firstsStringBuilder.append("First1(").append(nonTerminalSymbol).append(") = ")
					.append(firstFunctionComputer.getResult().get(nonTerminalSymbol)).append("; ");
			}
			firstsStringBuilder.append(System.lineSeparator());
			writer.println(firstsStringBuilder);
			
			writer.println("------- Follow1  -------------------------------------------------------");
			FollowFunctionComputor followFunctionComputor = new FollowFunctionComputor(grammar, firstFunctionComputer);
			j = 1;
			StringBuilder followssStringBuilder = new StringBuilder("Computation Follow1 function for all of the non-terminal symbols of the grammar. " + System.lineSeparator());
			while (!followFunctionComputor.isComputationComplete()) {
				Map<NonTerminalSymbol, Set<TerminalSymbol>> currentFirstValues = followFunctionComputor.nextIteration(); 
				followssStringBuilder.append("Iteration ").append(j++).append(": ");
				for (NonTerminalSymbol nonTerminalSymbol : currentFirstValues.keySet()) {
					followssStringBuilder.append("Follow(").append(nonTerminalSymbol).append(") = ")
						.append(currentFirstValues.get(nonTerminalSymbol)).append("; ");
				}
				followssStringBuilder.append(System.lineSeparator());
			}
			followssStringBuilder.append(System.lineSeparator() + "Result for Follow1 function:" + System.lineSeparator());
			for (NonTerminalSymbol nonTerminalSymbol : followFunctionComputor.getResult().keySet()) {
				followssStringBuilder.append("Follow1(").append(nonTerminalSymbol).append(") = ")
					.append(followFunctionComputor.getResult().get(nonTerminalSymbol)).append("; ");
			}
			followssStringBuilder.append(System.lineSeparator());
			writer.println(followssStringBuilder);
	
			writer.println("-- №4a -- Вычисление начального состояния q0 ------------------------------------------------------------");
			SituationClosureComputer closureComputer = new SituationClosureComputer(grammar, firstFunctionComputer);
			StatesAccumulator statesAccumulator = new StatesAccumulator(grammar, firstFunctionComputer, closureComputer, writer);
			statesAccumulator.computeStartState();
			
			writer.println("-- №4b -- Вычисление множества состояний ----------------------------------------------------------------");
			statesAccumulator.computeStates();
			statesAccumulator.printStateList();
			
			writer.println("--------- Таблица для функции g -------------------------------------------------------------------------");
			statesAccumulator.printGTable();
			
			writer.println("-- №5 -- Проверка состояний на противоречивость (определение в документе Рябого №5) ---------------------");
			statesAccumulator.performStatesTest();
			
			writer.println("-- №6 -- Таблица для функции f --------------------------------------------------------------------------" + System.lineSeparator());
			statesAccumulator.printFTable();

            writer.close();
			
		} catch (MTException exception) {
			exception.printStackTrace();
		}
		 catch (FileNotFoundException exception) {
			// TODO Auto-generated catch block
			exception.printStackTrace();
		} catch (IOException exception) {
			// TODO Auto-generated catch block
			exception.printStackTrace();
		} 
		
	}
	
}
