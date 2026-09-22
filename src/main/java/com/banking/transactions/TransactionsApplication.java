package com.banking.transactions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * CLASE DE ARRANQUE - Composition Root
 * 
 * PATH: com.banking.transactions/ -> Va en la raíz
 * porque @SpringBootApplication
 * hace @ComponentScan desde su paquete hacia abajo. Si la pones en domain/ o
 * en infrastructure/web/, no escanearía el resto de módulos. En la raíz ve
 * todo.
 * 
 * POR QUÉ @SpringBootApplication: Es 3 anotaciones en 1:
 * 1. @Configuration: Marca esta clase como fuente de beans.
 * 2. @EnableAutoConfiguration: Activa el auto-configure de Spring Boot
 * (DataSource, Jackson, etc).
 * 3. @ComponentScan: Escanea @Component, @Service, @Controller, @Repository
 * desde
 * com.banking.transactions.* hacia abajo (application/usecase/,
 * infrastructure/...).
 * Es el único sitio donde permites que Spring toque el proyecto.
 * 
 * POR QUÉ public class: Tiene que ser public para que Spring la pueda
 * instanciar
 * vía reflexión al arrancar.
 * 
 * POR QUÉ public static void main: Punto de entrada estándar de Java.
 * static porque se ejecuta sin crear instancia. void porque no devuelve nada.
 * args: argumentos de consola (ej: --server.port=8081).
 */
@SpringBootApplication
public class TransactionsApplication {

	public static void main(String[] args) {
		// Delega todo el arranque a Spring Boot. Crea el ApplicationContext,
		// levanta Tomcat embebido y registra todos los adapters.
		SpringApplication.run(TransactionsApplication.class, args);
	}
}