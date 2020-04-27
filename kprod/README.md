# kprod

Example Kafka Producer built on Spring Boot.

Periodically (each `report.fixed.delay.ms`) sends messages to Kafka.

### Message

- **value** - random alphanumeric string of `report.value.length` chars
- **unit** - string
- **timestamp** - current time in millis (long)

## Spring profiles

Set by `spring.profiles.active`:

- `simple` - using vanilla `KafkaProducer` from Kafka clients
- `template` - using `KafkaTemplate` from Spring

## Settings

- `server.port`

- `kafka.producer.bootstrapServers`
- `kafka.producer.clientId` - kafka client id, also used as a message key
- `kafka.producer.topic`

- `kafka.producer.retries`
- `kafka.producer.retryBackoffMs`

- `report.initial.delay.ms`
- `report.fixed.delay.ms`

- `report.value.length`
