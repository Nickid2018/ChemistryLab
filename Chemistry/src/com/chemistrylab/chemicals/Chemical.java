package com.chemistrylab.chemicals;

import com.alibaba.fastjson.*;
import proguard.annotation.*;

@KeepImplementations
public abstract class Chemical {

	public Chemical(JSONObject o) {
	}
	
	public abstract double getMess();
	
	public boolean isActualMess(){
		return false;
	}
}
