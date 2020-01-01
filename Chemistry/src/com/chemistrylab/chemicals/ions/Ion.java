package com.chemistrylab.chemicals.ions;

import com.alibaba.fastjson.*;
import com.chemistrylab.chemicals.*;

public class Ion extends Chemical {

	public Ion(JSONObject o) {
		super(o);
	}

	@Override
	public double getMess() {
		return 0;
	}

}
