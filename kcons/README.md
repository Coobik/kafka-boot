# kcons

Example Kafka Consumer built on Spring Boot 2.2.6.RELEASE

Listens to messages on `kafka.consumer.topic` topic

### Message

- **value** - random alphanumeric string of `report.value.length` chars
- **unit** - string
- **timestamp** - current time in millis (long)

## Settings

- `server.port`

### kafka consumer settings

- `kafka.consumer.enabled` - `true`, `false`
- `kafka.consumer.bootstrapServers`
- `kafka.consumer.groupId`
- `kafka.consumer.topic`
- `kafka.consumer.maxPollIntervalMs`
- `kafka.consumer.autoOffsetReset` - `earliest`, `latest`

### kafka consumer retry settings

- `kafka.consumer.maxAttempts`
- `kafka.consumer.initialInterval`
- `kafka.consumer.multiplier`

### kafka consumer json deserializer settings

- `kafka.consumer.useHeadersIfPresent` - `true`, `false` - set to `false` if we don't have the original Message class locally
- `kafka.consumer.trustedPackages`
