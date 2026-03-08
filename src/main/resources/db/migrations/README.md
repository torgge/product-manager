# Database Migrations

## Overview

This directory contains SQL migration scripts for the Product Manager application.

## Available Scripts

### V001__seed_sample_data.sql

Comprehensive sample data migration that includes:

- **20 Products** across multiple categories:
  - Electronics (laptops, monitors, peripherals)
  - Office Furniture (chairs, desks, cabinets)
  - Office Supplies (paper, pens, sticky notes)
  - Networking Equipment (routers, switches, cables)

- **10 Customers** (mix of individuals and businesses)

- **5 Suppliers** (various product distributors)

- **42 Stock Entries** distributed across 4 locations:
  - Main Warehouse (primary inventory)
  - Secondary Warehouse (backup stock)
  - Retail Store (customer-facing)
  - Distribution Center (B2B fulfillment)

- **7 Purchase Orders** with various statuses (PENDING, CONFIRMED, RECEIVED)

- **10 Sale Orders** with items across different locations

- **Sample Stock Movements** for historical tracking

## Executing Migrations

### Option 1: Using psql Command Line

```bash
# From project root
psql -h localhost -U product_user -d product_db -f src/main/resources/db/migrations/V001__seed_sample_data.sql
```

### Option 2: Using Docker Exec (if database is in container)

```bash
# Copy script to container
docker cp src/main/resources/db/migrations/V001__seed_sample_data.sql product-db:/tmp/

# Execute script
docker exec -it product-db psql -U product_user -d product_db -f /tmp/V001__seed_sample_data.sql
```

### Option 3: Using Database Client

Import the SQL file using your preferred database client:
- DBeaver
- pgAdmin
- DataGrip
- VSCode PostgreSQL extension

### Option 4: Automated Script (Recommended)

```bash
# Use the convenience script
cd src/main/resources/db/migrations
bash execute_migration.sh
```

## Data Summary

After running the migration, you will have:

| Entity | Count | Notes |
|--------|-------|-------|
| Products | 20 | Various categories with realistic pricing |
| Customers | 10 | Mix of B2C and B2B clients |
| Suppliers | 5 | Different product specializations |
| Stock Entries | 42 | Multi-warehouse distribution |
| Purchase Orders | 7 | Different statuses and locations |
| Sale Orders | 10 | Recent orders with various statuses |
| Stock Movements | 7 | Sample historical tracking |

## Cleaning Existing Data

If you want to reset the database before running the migration, uncomment the TRUNCATE statement at the beginning of the script:

```sql
TRUNCATE TABLE saleorderitem, purchaseorderitem, stockmovement, stock,
               saleorder, purchaseorder, product, customer, supplier
RESTART IDENTITY CASCADE;
```

⚠️ **WARNING**: This will delete ALL existing data!

## Verification

After running the migration, verify the data with these queries:

```sql
-- Count records
SELECT 'Products' as entity, COUNT(*) FROM product
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

-- Inventory value by location
SELECT
    s.location,
    COUNT(DISTINCT s.product_id) as product_count,
    SUM(s.quantity) as total_units,
    TO_CHAR(SUM(s.quantity * p.price), '$999,999.99') as inventory_value
FROM stock s
JOIN product p ON s.product_id = p.id
GROUP BY s.location
ORDER BY s.location;

-- Top selling products
SELECT
    p.name,
    p.category,
    SUM(si.quantity) as units_sold,
    TO_CHAR(SUM(si.subtotal), '$999,999.99') as total_revenue
FROM saleorderitem si
JOIN product p ON si.product_id = p.id
JOIN saleorder so ON si.saleOrder_id = so.id
WHERE so.status = 'RECEIVED'
GROUP BY p.id, p.name, p.category
ORDER BY SUM(si.subtotal) DESC
LIMIT 10;
```

## Sample Data Characteristics

### Stock Distribution
- **Main Warehouse**: 50% of total inventory (primary storage)
- **Secondary Warehouse**: 15% (backup and overflow)
- **Retail Store**: 20% (customer-facing products)
- **Distribution Center**: 15% (B2B fulfillment)

### Order Statuses
- **PENDING**: Newly created, awaiting confirmation
- **CONFIRMED**: Approved, awaiting fulfillment
- **RECEIVED/DELIVERED**: Completed transactions
- **CANCELLED**: Cancelled orders

### Business Logic Examples
1. **Low Stock Alerts**: Some products have quantities near minimum thresholds
2. **Multi-Location**: Products exist in multiple warehouses
3. **Historical Orders**: Mix of recent and older transactions
4. **Customer Diversity**: Individual buyers and corporate accounts
5. **Realistic Pricing**: Profit margins between 38% and 116%

## Troubleshooting

### Permission Denied
Ensure your database user has INSERT privileges:
```sql
GRANT INSERT ON ALL TABLES IN SCHEMA public TO product_user;
```

### Sequence Out of Sync
If you encounter duplicate key errors:
```sql
SELECT setval('product_id_seq', (SELECT MAX(id) FROM product));
SELECT setval('customer_id_seq', (SELECT MAX(id) FROM customer));
-- Repeat for other sequences
```

### Foreign Key Violations
Ensure tables are created in the correct order. The migration script handles dependencies automatically.

## Next Steps

After loading sample data:

1. Start the application: `./gradlew quarkusDev`
2. Access the dashboard: `http://localhost:8080`
3. Explore the stock analytics at: `http://localhost:8080/stock/dashboard`
4. Review reports at: `http://localhost:8080/reports`

## Development Tips

- Use this data for UI/UX testing
- Create automated tests against this dataset
- Benchmark performance with realistic data volumes
- Demonstrate features to stakeholders
