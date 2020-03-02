package com.github.nickid2018.chemistrylab.layer.container;

import java.util.*;

import com.github.nickid2018.chemistrylab.layer.*;

public class Containers {

	private static final Map<UUID, AbstractContainer> cons = new HashMap<>();

	public static final void addContainer(AbstractContainer c) {
		LayerRender.pushLayer(c);
		cons.put(c.uuid, c);
	}

	public static final AbstractContainer getContainer(UUID uuid) {
		AbstractContainer ret = cons.get(uuid);
		if (ret == null)
			throw new IllegalArgumentException("Can't find container with UUID " + uuid.toString());
		return ret;
	}

	public static final AbstractContainer getContainer(String uuid) {
		return getContainer(UUID.fromString(uuid));
	}

	public static final void removeContainer(AbstractContainer c) {
		LayerRender.popLayer(c);
		if (cons.remove(c.uuid) == null)
			throw new RuntimeException("The container isn't exists");
	}

	public static final void removeContainer(UUID uuid) {
		LayerRender.popLayer(cons.get(uuid));
		if (cons.remove(uuid) == null)
			throw new RuntimeException("The container isn't exists");
	}

	public static final void removeContainer(String uuid) {
		removeContainer(UUID.fromString(uuid));
	}
}
