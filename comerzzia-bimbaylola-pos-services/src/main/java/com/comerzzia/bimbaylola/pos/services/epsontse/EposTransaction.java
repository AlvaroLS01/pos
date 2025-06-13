package com.comerzzia.bimbaylola.pos.services.epsontse;

public class EposTransaction {

	private EposStorage storage;
	private String function;
	private EposInput input;
	private EposCompress compress;

	public EposTransaction() {
		super();
	}

	public EposTransaction(EposStorage storage, String function, EposInput input, EposCompress compress) {
		super();
		this.storage = storage;
		this.function = function;
		this.input = input;
		this.compress = compress;
	}

	public EposInput getInput() {
		return input;
	}

	public void setInput(EposInput input) {
		this.input = input;
	}

	public EposStorage getStorage() {
		return storage;
	}

	public void setStorage(EposStorage storage) {
		this.storage = storage;
	}

	public EposCompress getCompress() {
		return compress;
	}

	public void setCompress(EposCompress compress) {
		this.compress = compress;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

}
