package com.github.nickid2018.chemistrylab.mod.imc;

public interface IConflictable<T extends IConflictable<T>> {

	public T merge(T conflict);

	@SuppressWarnings("unchecked")
	public T mergeAll(T... conflicts);

	public RedirectableObject<T> getRedirectableObject();

	public void setRedirectableObject(T conflict);
	
	public void disposeRedirectable();
	
	public boolean equals(Object obj);
}
