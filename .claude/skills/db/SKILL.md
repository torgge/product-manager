---
description: Database operations (start, stop, reset PostgreSQL)
disable-model-invocation: true
allowed-tools:
  - Bash(docker-compose*)
  - Bash(docker compose*)
---

# /db - Database Operations

Manage the PostgreSQL database container.

## Commands

### Start database
```bash
docker-compose up -d
```

### Stop database
```bash
docker-compose down
```

### Restart database
```bash
docker-compose restart
```

### View logs
```bash
docker-compose logs -f postgres
```

### Reset database (delete all data)
```bash
docker-compose down -v && docker-compose up -d
```

## Usage
- `/db start` - Start the PostgreSQL container
- `/db stop` - Stop the PostgreSQL container
- `/db restart` - Restart the PostgreSQL container
- `/db logs` - View PostgreSQL logs
- `/db reset` - Delete all data and restart fresh

## Connection Details
- Host: `localhost`
- Port: `5432`
- Database: `productdb`
- Username: `postgres`
- Password: `postgres`

## Notes
- Data is persisted in a Docker volume `postgres_data`
- Reset will delete all data permanently
