package com.qinglan.sdk.server.release.domain.basic.event;

import com.qinglan.sdk.server.release.presentation.basic.dto.RoleEstablishPattern;
import com.zhidian3g.ddd.annotation.event.Event;

@Event
public class RoleEstablishEvent implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RoleEstablishPattern roleEstablishPattern;

	public RoleEstablishEvent(RoleEstablishPattern roleEstablishPattern) {
		this.roleEstablishPattern=roleEstablishPattern;
	}
	
	public RoleEstablishPattern getHelper() {
		return roleEstablishPattern;
	}
}
