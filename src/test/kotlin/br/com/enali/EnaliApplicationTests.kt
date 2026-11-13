package br.com.enali

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@Disabled("Sobe a aplicação inteira: precisa do PostgreSQL e das variáveis do .env")
class EnaliApplicationTests {

	@Test
	fun contextLoads() {
	}

}
