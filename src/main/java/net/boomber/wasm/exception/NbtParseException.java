package net.boomber.wasm.exception;

public class NbtParseException extends RuntimeException {
	private static final long serialVersionUID = -3220572045686261535L;

	public NbtParseException(Exception e) {
		super(e.getMessage());
	}
}
