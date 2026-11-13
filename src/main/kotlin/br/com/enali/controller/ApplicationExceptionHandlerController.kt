package br.com.enali.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException

@RestControllerAdvice
class ApplicationExceptionHandlerController {

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(
        ex: ResponseStatusException
    ): ResponseEntity<Map<String, String>> {
        val erro = mapOf(
            "erro" to (ex.reason ?: "Erro na requisição")
        )
        return ResponseEntity.status(ex.statusCode).body(erro)
    }
    
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(
        ex: IllegalArgumentException
    ): ResponseEntity<Map<String, String>> {
        val erro = mapOf(
            "erro" to (ex.message ?: "Erro de validação")
        )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodException(
        ex: MethodArgumentNotValidException
    ): ResponseEntity<Map<String, String>> {
        val erros = hashMapOf<String, String>()

        ex.bindingResult.allErrors.forEach { erro ->
            if (erro is FieldError) {
                val fieldName = erro.field
                val message = erro.defaultMessage
                    ?: "Erro no campo $fieldName"

                erros[fieldName] = message
            }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erros)
    }
}
