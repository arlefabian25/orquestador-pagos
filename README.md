# Orquestador de pagos

Implementación del documento de diseño "Integración asíncrona con una pasarela de pagos".
Java 21, Spring Boot 4, PostgreSQL, Kafka y Resilience4j.

## Estructura (arquitectura hexagonal)

```
com.ecomerce.pagos
├── dominio          Java puro: Orden, IntentoPago (máquina de estados), Monto, eventos y excepciones
├── aplicacion
│   ├── puerto.entrada   casos de uso (interfaces)
│   ├── puerto.salida    lo que la aplicación necesita del exterior (repositorios, proveedor, broker)
│   ├── modelo           tipos de la capa de aplicación (inbox, outbox, idempotencia, parámetros)
│   └── servicio         implementación de los casos de uso y manejo de transacciones
└── infraestructura
    ├── entrada          REST, webhook y tareas periódicas
    ├── salida           JPA, cliente HTTP del proveedor, Kafka, generador de UUIDv7
    └── configuracion    seguridad, clientes HTTP, resiliencia, propiedades
```

Las dependencias apuntan hacia adentro; `ArquitecturaTest` lo verifica con ArchUnit.

## Estado del esqueleto

- Implementado: dominio, puertos, persistencia (entidades, consultas con `SKIP LOCKED`, inbox, outbox,
  idempotencia), migraciones Flyway, verificación HMAC, cliente HTTP con resiliencia, publicador Kafka,
  controladores, manejo de errores (RFC 9457) y correlación.
- Pendiente (marcado con `UnsupportedOperationException` y los pasos en comentarios):
  `IniciarPagoService`, `ProcesarEventoProveedorService`, `ConciliarPagoService` e `IdempotenciaService`.
- Las tareas periódicas están apagadas (`pagos.tareas.habilitadas=false`) hasta implementar esos casos de uso.

## Ejecutar en local

Requiere Docker. Spring levanta PostgreSQL y Kafka desde `compose.yaml`.

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

## Pruebas

```bash
./mvnw test
```
