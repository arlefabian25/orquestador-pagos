package com.ecomerce.pagos.infraestructura.configuracion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.Executors;

/**
 * Clientes HTTP hacia la pasarela con timeouts explícitos (sección 8.1).
 * Crear y consultar tienen timeouts de lectura distintos, por eso hay dos clientes.
 */
@Configuration
public class ClienteHttpProveedorConfig {

    /** Pareja de clientes que usa el adaptador del proveedor. */
    public record ClientesProveedor(RestClient crear, RestClient consulta) { }

    @Bean
    public ClientesProveedor clientesProveedor(PropiedadesPagos propiedades) {
        PropiedadesPagos.Proveedor p = propiedades.proveedor();
        HttpClient http = HttpClient.newBuilder()
                .connectTimeout(p.timeoutConexion())
                .executor(Executors.newVirtualThreadPerTaskExecutor())
                .build();
        return new ClientesProveedor(
                cliente(http, p.urlBase(), p.timeoutLecturaCrear()),
                cliente(http, p.urlBase(), p.timeoutLecturaConsulta()));
    }

    private static RestClient cliente(HttpClient http, String urlBase, Duration timeoutLectura) {
        JdkClientHttpRequestFactory fabrica = new JdkClientHttpRequestFactory(http);
        fabrica.setReadTimeout(timeoutLectura);
        return RestClient.builder()
                .baseUrl(urlBase)
                .requestFactory(fabrica)
                .build();
    }
}
