package br.com.enali.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ApplicationExceptionHandlerController {
  
  @ExceptionHandler(IllegalArgumentException::class)
  fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<Map<String, String>> {
      val erro = mapOf("erro" to (ex.message ?: "Erro de validação"))
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro)
  }
    
}