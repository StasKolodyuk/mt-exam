package by.pachkovsky.mt.exceptions;

public class MTException extends Exception {

	public MTException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public MTException(String message) {
		super(message);
	}
	
}
