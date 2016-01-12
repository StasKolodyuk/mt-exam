package by.pachkovsky.mt.exceptions;

public class GrammarParseException extends MTException {

	public GrammarParseException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
	public GrammarParseException(String message) {
		super(message);
	}
	
}
