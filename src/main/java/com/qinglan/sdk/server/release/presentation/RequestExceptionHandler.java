package com.qinglan.sdk.server.release.presentation;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.qinglan.sdk.server.common.JsonMapper;
import com.qinglan.sdk.server.release.Constants;


@ControllerAdvice
public class RequestExceptionHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(RequestExceptionHandler.class);

	@ExceptionHandler(Exception.class)
	@ResponseBody
	public String handleException(Exception ex) {
		logger.error("Exception Catching", ex);
		Map<String,Object> result = new HashMap<String,Object>();
		result.put(Constants.RESPONSE_CODE, Constants.RESPONSE_SERVER_EXCEPTION);
		return JsonMapper.toJson(result);// "{\"code\":\"2\"}";
	}
}