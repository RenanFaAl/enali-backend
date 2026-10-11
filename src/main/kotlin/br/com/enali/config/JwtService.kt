package br.com.enali.service

import br.com.enali.model.Administrador
import io.jsonwebtoken.Jwts
import java.util.HexFormat
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Date
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
            .claim("nivelAcesso", administrador.nivelAcesso)
            .issuedAt(agora)
            .expiration(expiracao)
            .signWith(chave)
            .compact()
    }

    fun extrairEmail(token: String): String {
        return Jwts.parser()
            .verifyWith(chave)
            .build()
            .parseSignedClaims(token)
            .payload
            .subject
    }

    fun validarToken(token: String): Boolean {
        return try {
            val claims = Jwts.parser()
                .verifyWith(chave)
                .build()
                .parseSignedClaims(token)
                .payload

            claims.subject != null &&
                    claims.expiration.after(Date())
        } catch (ex: Exception) {
            false
        }
    }
}
