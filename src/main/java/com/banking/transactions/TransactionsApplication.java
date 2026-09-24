package com.banking.transactions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Composition Root - Arranque de la app.
 * En raíz com.banking.transactions para que @ComponentScan vea application/ e
 * infrastructure/.
 * 
 * @SpringBootApplication = @Configuration + @EnableAutoConfiguration
 *                        + @ComponentScan.
 *                        Único punto donde Spring toca el proyecto. Public para
 *                        que Spring la instancie por reflexión.
 */
@SpringBootApplication
public class TransactionsApplication {

	public static void main(String[] args) {
		// Crea ApplicationContext, auto-configura DataSource/Jackson, levanta Tomcat y
		// registra adapters
		SpringApplication.run(TransactionsApplication.class, args);
	}
}