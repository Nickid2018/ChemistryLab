package com.github.nickid2018.chemistrylab.util;

public class Pair<K, V> {

	public K key;
	public V value;

	public Pair(K k, V v) {
		key = k;
		value = v;
	}

	@Override
	public int hashCode() {
		return key.hashCode() + value.hashCode();
	}

	@Override
	public String toString() {
		return key + ":" + value;
	}
}
