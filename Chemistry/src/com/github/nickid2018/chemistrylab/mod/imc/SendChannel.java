package com.github.nickid2018.chemistrylab.mod.imc;

public final class SendChannel {

	public String from;
	public String to;

	public SendChannel(String from, String to) {
		this.from = from;
		this.to = to;
	}

	public SendChannel reverse() {
		return new SendChannel(to, from);
	}

	public boolean equals(Object obj) {
		return (obj instanceof SendChannel) && ((SendChannel) obj).from.equals(from)
				&& ((SendChannel) obj).to.equals(to);
	}

	@Override
	public int hashCode() {
		return from.hashCode() - to.hashCode();
	}

	@Override
	public String toString() {
		return from + " -> " + to;
	}
}