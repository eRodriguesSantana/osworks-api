package com.algaworks.osworks.api.exceptionhandler;

import java.time.OffsetDateTime;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.algaworks.osworks.domain.exception.EntidadeNaoEncontradaException;
import com.algaworks.osworks.domain.exception.NegocioException;

@ControllerAdvice //indica que é um componente do spring onde será tratada as exceptions da classe ClienteController 
                  //para retornar respostas de erro de forma mais amigavél para o cliente que consome esta API.
//A classe ResponseEntityExceptionHandler possui vários tratamentos de exception
public class ApiExceptionHandler extends ResponseEntityExceptionHandler{
	
	//injecão da instancia MessageSource do Spring Framework
	//MessageSource é uma interface do Spring pra resolver problemas de messages
	@Autowired //atribui uma instancia a variavel
	private MessageSource messageSource;
	
	//indica que se uma classe Controller lançar uma determinada excessão, esse metodo tem um tratamento especifico pra ela
	@ExceptionHandler(EntidadeNaoEncontradaException.class)
	public ResponseEntity<Object> handleEntidadeNaoEncontrada(NegocioException ex, WebRequest request) {
		//WebRequest é uma interface que indica uma requisição da web
		
		var status = HttpStatus.NOT_FOUND;
		
		var problema = new Problema();
		problema.setStatus(status.value());
		problema.setTitulo(ex.getMessage());
		problema.setDataHora(OffsetDateTime.now());
		
		//retorna status 404 - Not Found
		return handleExceptionInternal(ex, problema, new HttpHeaders(), status, request);
	}
	
	//indica que se uma classe Controller lançar uma determinada excessão, esse metodo tem um tratamento especifico pra ela
	@ExceptionHandler(NegocioException.class)
	public ResponseEntity<Object> handleNegocio(NegocioException ex, WebRequest request) {
		//WebRequest é uma interface que indica uma requisição da web
		
		var status = HttpStatus.BAD_REQUEST;
		
		var problema = new Problema();
		problema.setStatus(status.value());
		problema.setTitulo(ex.getMessage());
		problema.setDataHora(OffsetDateTime.now());
		
		//retorna status 400 - Bad Request
		return handleExceptionInternal(ex, problema, new HttpHeaders(), status, request);
	}
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		
		//instancia uma lista do metodo Campo da classe Problema
		var campos = new ArrayList<Problema.Campo>();
		
		//A instancia da classe passada por parâmetro MethodArgumentNotValidException expoe o getBindingResult()
		//O getBindingResult() da classe MethodArgumentNotValidException possui o metodo getAllErrors() que retorna ObjectError
		for (ObjectError error: ex.getBindingResult().getAllErrors()) {
			String nome = ((FieldError) error).getField();
			
			//LocaleContextHolder.getLocale() captura o idioma local
			String mensagem = messageSource.getMessage(error, LocaleContextHolder.getLocale());
			
			campos.add(new Problema.Campo(nome, mensagem));
		}
		
		var problema = new Problema();
		
		//pega o código de erro passado por parâmetro e atribui a variavel status da classe Problema
		problema.setStatus(status.value());
		
		//atribui um titulo a variavel titulo da classe Problema
		problema.setTitulo("Um ou mais campos estão inválidos. Faça o preenchimento correto e tente novamente");
		
		//pega a data e hora atual em que ocorreu o erro e atribui a variavel dataHora da classe Problema
		problema.setDataHora(OffsetDateTime.now());
		
		//atribui valor nas variaveis do metodo estatico Campos da classe Problema que estivem em desacordo de preenchimento
		problema.setCampos(campos);
		
		return super.handleExceptionInternal(ex, problema, headers, status, request);
	}
}
