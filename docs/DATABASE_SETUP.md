# Database Setup Guide

## Quick Start

### 1. Start Database (Docker)

```bash
docker-compose up -d product-db
```

### 2. Load Sample Data

**Using Docker:**
```bash
cd src/main/resources/db/migrations
./execute_migration_docker.sh
```

**Using Local PostgreSQL:**
```bash
cd src/main/resources/db/migrations
./execute_migration.sh
```

### 3. Start Application

```bash
./gradlew quarkusDev
```

### 4. Access Application

- **Dashboard**: http://localhost:8080
- **Stock Analytics**: http://localhost:8080/stock/dashboard
- **Reports**: http://localhost:8080/reports

---

## Sample Data Overview

The migration script (`V001__seed_sample_data.sql`) provides:

### Products (20 items)

| Category | Count | Examples |
|----------|-------|----------|
| Electronics | 6 | Laptops, Monitors, Webcams, SSDs |
| Furniture | 4 | Office Chairs, Standing Desks, Filing Cabinets |
| Office Supplies | 5 | Copy Paper, Pens, Sticky Notes, Staplers |
| Networking | 3 | Routers, Switches, Ethernet Cables |
| Accessories | 2 | Monitor Arms, USB-C Hubs |

### Customers (10 records)

- **5 Individuals**: John Anderson, Sarah Johnson, Michael Chen, etc.
- **5 Businesses**: TechStart Solutions, Global Consulting, Creative Design Studio, etc.

### Suppliers (5 records)

- Tech Distributors Inc
- Office Supplies Wholesale
- Furniture Direct Manufacturing
- Network Solutions Provider
- Global Electronics Supply

### Stock Distribution (42 entries across 4 locations)

| Location | Products | Total Units | Percentage |
|----------|----------|-------------|------------|
| Main Warehouse | 20 | ~1,600 | 50% |
| Secondary Warehouse | 6 | ~500 | 15% |
| Retail Store | 9 | ~650 | 20% |
| Distribution Center | 7 | ~480 | 15% |

### Transaction History

- **7 Purchase Orders**: Mix of PENDING, CONFIRMED, and RECEIVED statuses
- **10 Sale Orders**: Recent transactions across all locations
- **7 Stock Movements**: Sample historical tracking

---

## Manual Database Operations

### Connect to Database

**Using Docker:**
```bash
docker exec -it product-db psql -U product_user -d product_db
```

**Using Local psql:**
```bash
psql -h localhost -U product_user -d product_db
```

### Useful Queries

**Check data counts:**
```sql
SELECT 'Products' as entity, COUNT(*) as count FROM product
UNION ALL
SELECT 'Customers', COUNT(*) FROM customer
UNION ALL
SELECT 'Suppliers', COUNT(*) FROM supplier
UNION ALL
SELECT 'Stock Entries', COUNT(*) FROM stock
UNION ALL
SELECT 'Purchase Orders', COUNT(*) FROM purchaseorder
UNION ALL
SELECT 'Sale Orders', COUNT(*) FROM saleorder;
```

**Inventory value by location:**
```sql
SELECT
    s.location,
    COUNT(DISTINCT s.product_id) as products,
    SUM(s.quantity) as units,
    TO_CHAR(SUM(s.quantity * p.price), '$999,999.99') as value
FROM stock s
JOIN product p ON s.product_id = p.id
GROUP BY s.location
ORDER BY s.location;
```

**Low stock alerts:**
```sql
SELECT
    p.name,
    s.location,
    s.quantity as current,
    s.minQuantity as minimum,
    (s.minQuantity - s.quantity) as needed
FROM stock s
JOIN product p ON s.product_id = p.id
WHERE s.quantity <= s.minQuantity
ORDER BY s.location, (s.minQuantity - s.quantity) DESC;
```

**Top selling products:**
```sql
SELECT
    p.name,
    p.category,
    SUM(si.quantity) as units_sold,
    TO_CHAR(SUM(si.subtotal), '$999,999.99') as revenue
FROM saleorderitem si
JOIN product p ON si.product_id = p.id
JOIN saleorder so ON si.saleOrder_id = so.id
WHERE so.status = 'RECEIVED'
GROUP BY p.id, p.name, p.category
ORDER BY SUM(si.subtotal) DESC
LIMIT 10;
```

---

## Reset Database

### Option 1: Drop and Recreate Schema

```sql
-- Connect to database first
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
GRANT ALL ON SCHEMA public TO product_user;
```

Then restart the Quarkus application to recreate tables.

### Option 2: Truncate Tables (Keep Schema)

Uncomment the TRUNCATE statement in `V001__seed_sample_data.sql`:

```sql
TRUNCATE TABLE saleorderitem, purchaseorderitem, stockmovement, stock,
               saleorder, purchaseorder, product, customer, supplier
RESTART IDENTITY CASCADE;
```

Then run the migration script again.

### Option 3: Docker Volume Reset

```bash
# Stop and remove containers
docker-compose down

# Remove database volume
docker volume rm product-manager_postgres_data

# Start fresh
docker-compose up -d
./gradlew quarkusDev  # Let Quarkus create tables
cd src/main/resources/db/migrations
./execute_migration_docker.sh
```

---

## Database Configuration

### Environment Variables

```bash
# Database connection
DB_HOST=localhost
DB_PORT=5432
DB_NAME=product_db
DB_USER=product_user
DB_PASSWORD=product_pass

# Docker container
DB_CONTAINER=product-db
```

### Application Properties

Located in `src/main/resources/application.properties`:

```properties
# PostgreSQL DataSource
quarkus.datasource.db-kind=postgresql
quarkus.datasource.username=product_user
quarkus.datasource.password=product_pass
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/product_db

# Hibernate settings
quarkus.hibernate-orm.database.generation=update
quarkus.hibernate-orm.log.sql=true
```

---

## Troubleshooting

### Connection Refused

**Problem**: Cannot connect to database

**Solutions**:
1. Check if database is running: `docker ps`
2. Verify port is not in use: `lsof -i :5432`
3. Check Docker network: `docker network ls`
4. Review logs: `docker logs product-db`

### Permission Denied

**Problem**: User lacks INSERT privileges

**Solution**:
```sql
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO product_user;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO product_user;
```

### Duplicate Key Error

**Problem**: Sequence out of sync

**Solution**:
```sql
SELECT setval('product_id_seq', (SELECT MAX(id) FROM product));
SELECT setval('customer_id_seq', (SELECT MAX(id) FROM customer));
SELECT setval('supplier_id_seq', (SELECT MAX(id) FROM supplier));
-- Repeat for other sequences
```

### Foreign Key Violation

**Problem**: Missing referenced records

**Solution**: Ensure tables are populated in correct order (the migration script handles this automatically)

---

## Performance Optimization

### Create Indexes

```sql
-- Product searches
CREATE INDEX idx_product_name ON product(name);
CREATE INDEX idx_product_category ON product(category);

-- Stock lookups
CREATE INDEX idx_stock_product_location ON stock(product_id, location);
CREATE INDEX idx_stock_location ON stock(location);

-- Order filtering
CREATE INDEX idx_saleorder_status ON saleorder(status);
CREATE INDEX idx_purchaseorder_status ON purchaseorder(status);

-- Date range queries
CREATE INDEX idx_saleorder_date ON saleorder(orderDate);
CREATE INDEX idx_purchaseorder_date ON purchaseorder(orderDate);
```

### Analyze Tables

```sql
ANALYZE product;
ANALYZE stock;
ANALYZE saleorder;
ANALYZE purchaseorder;
```

---

## Backup and Restore

### Backup Database

```bash
# Full database backup
docker exec -t product-db pg_dump -U product_user product_db > backup.sql

# Compressed backup
docker exec -t product-db pg_dump -U product_user product_db | gzip > backup.sql.gz
```

### Restore Database

```bash
# From SQL file
docker exec -i product-db psql -U product_user -d product_db < backup.sql

# From compressed file
gunzip -c backup.sql.gz | docker exec -i product-db psql -U product_user -d product_db
```

---

## Next Steps

1. ✅ Load sample data
2. ✅ Start application
3. ✅ Explore dashboard
4. 📊 Review stock analytics
5. 📈 Generate reports
6. 🧪 Run tests with realistic data
7. 🎨 Customize for your needs

For more information, see:
- [Migration Scripts README](../src/main/resources/db/migrations/README.md)
- [Project Documentation](../README.md)
