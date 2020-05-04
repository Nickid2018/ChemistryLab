package com.github.nickid2018.chemistrylab.chemicals;

import java.util.*;
import com.github.nickid2018.chemistrylab.init.*;
import com.github.nickid2018.chemistrylab.mod.*;
import com.github.nickid2018.chemistrylab.mod.imc.*;

public class ChemicalConflictManager extends ConflictManager<ChemicalResource> {

	ChemicalConflictManager() {
		Conflicts.putConflictManager(this);
	}

	@Override
	public Class<ChemicalResource> getConflictClass() {
		return ChemicalResource.class;
	}

	@Override
	public String getConflictName() {
		return "Chemical Resource";
	}

	@Override
	public void dealConflict(Set<ModIMCEntry> entries) {
		List<String> chemicals = new ArrayList<>(ChemicalLoader.CHEMICALS.keySet());
		Set<ChemicalResource> resources = new HashSet<>(ChemicalLoader.CHEMICALS.values());
		chemicals.sort((s1, s2) -> s1.compareTo(s2));
		// The Last Element cannot be computed, so add an element to ensure it is not
		// the last one
		List<ModIMCEntry> entrylist = new ArrayList<>(entries);
		// Release Set
		entries.clear();
		entries = null;
		entrylist.sort((e1, e2) -> e1.thingToSend.hashCode() - e2.thingToSend.hashCode());
		int nowIndex = 0;
		int startConflict = 0;
		int endConflict = 1;
		String lastName = chemicals.get(0);
		// avoid grow size
		List<ChemicalResource> conflicts = new ArrayList<>(MathHelper.floor(ModController.MODS.size() / 2.0f));
		List<ModIMCEntry> involves = new ArrayList<>(MathHelper.floor(ModController.MODS.size() / 2.0f));
		for (String name : chemicals) {
			nowIndex++;
			String final_name = name.split(":")[0];
			if (lastName.equals(final_name))
				endConflict++;
			else {
				conflicts.clear();
				// Deal Conflicts
				List<String> subList = chemicals.subList(startConflict, endConflict);
				// First: Delete Conflict Items, copy into new list
				subList.forEach(conflict -> {
					conflicts.add(ChemicalLoader.CHEMICALS.get(conflict));
					ChemicalLoader.CHEMICALS.remove(conflict);
				});
				// Second: Find IMC Entries about this conflict (maybe very large)
				involves.clear();
				// The algorithm can cut down half of time, O(log2n+n/2)
				boolean findstart = false;
				boolean over = false;
				for (ModIMCEntry entry : entrylist) {
					if (over)
						break;
					if (entry.thingToSend.equals(conflicts.get(0))) {
						findstart = true;
						involves.add(entry);
					} else {
						if (findstart)
							over = true;
					}
				}
				boolean override = false;
				boolean ignore = false;
				boolean merge = false;
				ChemicalResource resource = null;
				for (ModIMCEntry find : involves) {
					if (find.conflictFunction == ConflictType.OVERRIDE) {
						resource = (ChemicalResource) find.thingToSend;
						for (ChemicalResource res : conflicts) {
							res.setRedirectableObject(resource);
						}
						override = true;
						break;
					} else if (find.conflictFunction == ConflictType.IGNORE)
						ignore = true;
					else
						merge = true;
				}
				if (!override) {
					if (ignore && !merge) {
						for (int i = 0; i < subList.size(); i++) {
							ChemicalLoader.CHEMICALS.put(subList.get(i), conflicts.get(i));
							resources.remove(conflicts.get(i));
							conflicts.get(i).disposeRedirectable();
						}
					} else
						resource = conflicts.get(0).mergeAll(conflicts.toArray(new ChemicalResource[conflicts.size()]));
				}
				if (!ignore || merge || override) {
					ChemicalLoader.CHEMICALS.put(final_name, resource);
					resource.disposeRedirectable();
				}
				// Finally, prepare next dealing
				startConflict = nowIndex;
				endConflict = nowIndex + 1;
				lastName = final_name;
			}
		}
		// Redirect
		ChemicalLoader.CHEMICALS.values().forEach(ChemicalResource::doOnRedirect);
		// Unload ChemicalResource
//		for (ChemicalResource res : resources) {
//			try {
//				LoadingWorld.manager.unload(res.getResourcePath());
//			} catch (Exception e) {
//			}
//		}
		// Result: Released All Undealing Chemical Resources, Resources are independent
	}

}
