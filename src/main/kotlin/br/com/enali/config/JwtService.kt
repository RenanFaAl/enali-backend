package br.com.enali.service

import br.com.enali.model.Administrador
import br.com.enali.model.Cliente
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Date
import java.util.HexFormat
import javax.crypto.SecretKey

@Service
class JwtService(
    @Value("\${jwt.secret}") private val secret: String
) {

    private val chave: SecretKey by lazy {
        Keys.hmacShaKeyFor(
            HexFormat.of().parseHex(secret)
        )
    }

    private val duracaoToken = 60 * 60 * 1000L

    fun gerarToken(administrador: Administrador): String {
        val agora = Date()
        val expiracao = Date(agora.time + duracaoToken)

        return Jwts.builder()
            .subject(administrador.email)
            .claim("id", administrador.id)
            .claim("tipoUsuario", "ADMINISTRADOR")
            .claim("nivelAcesso", administrador.nivelAcesso)
            .issuedAt(agora)
            .expiration(expiracao)
            .signWith(chave)
            .compact()
    }

    fun gerarToken(cliente: Cliente): String {
        val agora = Date()
        val expiracao = Date(agora.time + duracaoToken)

        return Jwts.builder()
            .subject(cliente.email)
            .claim("id", cliente.id)
            .claim("tipoUsuario", "CLIENTE")
            .issuedAt(agora)
            .expiration(expiracao)
            .signWith(chave)
            .compact()
    }

    fun extrairEmail(token: String): String {
        return obterClaims(token).subject
    }

    fun extrairTipoUsuario(token: String): String {
        return obterClaims(token)
            .get("tipoUsuario", String::class.java)
    }

    fun extrairId(token: String): Long {
        return (obterClaims(token)
            .get("id") as Number).toLong()
    }

    fun extrairNivelAcesso(token: String): String? {
        return obterClaims(token)
            .get("nivelAcesso", String::class.java)
    }

    fun validarToken(token: String): Boolean {
        return try {
            val claims = obterClaims(token)

            claims.subject != null &&
                    claims.expiration.after(Date()) &&
                    claims.get("tipoUsuario", String::class.java) in
                    setOf("ADMINISTRADOR", "CLIENTE")
        } catch (ex: Exception) {
            false
        }
    }

    private fun obterClaims(token: String) =
        Jwts.parser()
            .verifyWith(chave)
            .build()
            .parseSignedClaims(token)
            .payload
}
