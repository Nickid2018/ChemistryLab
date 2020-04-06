package com.github.nickid2018.chemistrylab.crashreport;

import com.google.common.eventbus.*;
import com.github.nickid2018.chemistrylab.event.*;

public class FatalErrorListener {

	public static Throwable error;
	public static Thread errorthread;

	public static void checkErrors() throws Throwable {
		if (error != null)
			throw error;
	}

	@Subscribe
	public static void listenError(FatalErrorEvent event) {
		error = event.error;
		errorthread = event.errorthread;
	}
}
