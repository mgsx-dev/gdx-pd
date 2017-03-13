package net.mgsx.pd.utils;

import com.badlogic.gdx.utils.GdxRuntimeException;

public class PdRuntimeException extends GdxRuntimeException
{
	private static final long serialVersionUID = -4463950573723477719L;

	public PdRuntimeException(String message, Throwable t) {
		super(message, t);
	}

	public PdRuntimeException(String message) {
		super(message);
	}

	public PdRuntimeException(Throwable t) {
		super(t);
	}

	public PdRuntimeException(int code) {
		super("Pd error code " + String.valueOf(code));
	}

}
