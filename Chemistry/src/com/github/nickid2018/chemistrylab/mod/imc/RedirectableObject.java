package com.github.nickid2018.chemistrylab.mod.imc;

public class RedirectableObject<T extends IConflictable<T>> {

	private T object;

	public RedirectableObject(T object) {
		this.object = object;
	}

	public T getObject() {
		return object;
	}

	public void setObject(T object) {
		this.object = object;
	}
}
