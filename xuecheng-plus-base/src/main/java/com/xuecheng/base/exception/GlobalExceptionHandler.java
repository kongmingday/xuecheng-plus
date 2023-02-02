package com.xuecheng.base.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description 全局异常处理器
 * @author eotouch
 * @date 2022/9/6 11:29
 * @version 1.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(com.xuecheng.base.exception.XueChengPlusException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public com.xuecheng.base.exception.RestErrorResponse doXueChengPlusException(com.xuecheng.base.exception.XueChengPlusException e) {
		log.error("【系统异常】{}",e.getErrMessage(),e);
		return new com.xuecheng.base.exception.RestErrorResponse(e.getErrMessage());

	}

	@ExceptionHandler(value = MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public com.xuecheng.base.exception.RestErrorResponse doValidException(MethodArgumentNotValidException argumentNotValidException) {

		BindingResult bindingResult = argumentNotValidException.getBindingResult();
		StringBuffer errMsg = new StringBuffer();

		List<FieldError> fieldErrors = bindingResult.getFieldErrors();
		fieldErrors.forEach(error -> {
			errMsg.append(error.getDefaultMessage()).append(",");
		});
		log.error(errMsg.toString());
		return new com.xuecheng.base.exception.RestErrorResponse(errMsg.toString());
	}

	@ExceptionHandler(value = BindException.class)
	public com.xuecheng.base.exception.RestErrorResponse exceptionHandle(BindException exception) {

		BindingResult result = exception.getBindingResult();
		StringBuilder errorMsg = new StringBuilder();

		List<FieldError> fieldErrors = result.getFieldErrors();
		fieldErrors.forEach(error -> {
			log.error("field: " + error.getField() + ", msg:" + error.getDefaultMessage());
			errorMsg.append(error.getDefaultMessage()).append("!");
		});
		return new com.xuecheng.base.exception.RestErrorResponse(errorMsg.toString());
	}


	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public com.xuecheng.base.exception.RestErrorResponse exception(Exception e) {

		log.error("【系统异常】{}",e.getMessage(),e);
		e.printStackTrace();
		if(e.getMessage().equals("不允许访问")){
			return new com.xuecheng.base.exception.RestErrorResponse("没有操作此功能的权限");
		}
		return new com.xuecheng.base.exception.RestErrorResponse(com.xuecheng.base.exception.CommonError.UNKOWN_ERROR.getErrMessage());


	}
}
