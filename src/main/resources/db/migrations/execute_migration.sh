#!/bin/bash

# =====================================================
# Database Migration Execution Script
# =====================================================
# This script executes SQL migration files for the Product Manager application
# =====================================================

set -e  # Exit on error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Default database configuration (override with environment variables)
DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-5432}"
DB_NAME="${DB_NAME:-product_db}"
DB_USER="${DB_USER:-product_user}"
DB_PASSWORD="${DB_PASSWORD:-product_pass}"

# Script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
MIGRATION_FILE="${SCRIPT_DIR}/V001__seed_sample_data.sql"

echo -e "${BLUE}═══════════════════════════════════════════════════${NC}"
echo -e "${BLUE}  Product Manager - Database Migration Tool${NC}"
echo -e "${BLUE}═══════════════════════════════════════════════════${NC}"
echo ""

# Check if migration file exists
if [ ! -f "$MIGRATION_FILE" ]; then
    echo -e "${RED}✗ Migration file not found: $MIGRATION_FILE${NC}"
    exit 1
fi

# Display connection info
echo -e "${YELLOW}Database Configuration:${NC}"
echo -e "  Host:     ${DB_HOST}"
echo -e "  Port:     ${DB_PORT}"
echo -e "  Database: ${DB_NAME}"
echo -e "  User:     ${DB_USER}"
echo ""

# Check if psql is available
if ! command -v psql &> /dev/null; then
    echo -e "${RED}✗ psql command not found. Please install PostgreSQL client.${NC}"
    echo ""
    echo -e "${YELLOW}Installation instructions:${NC}"
    echo -e "  macOS:   brew install postgresql"
    echo -e "  Ubuntu:  sudo apt-get install postgresql-client"
    echo -e "  RHEL:    sudo yum install postgresql"
    exit 1
fi

# Test database connection
echo -e "${YELLOW}Testing database connection...${NC}"
export PGPASSWORD="$DB_PASSWORD"
if psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -c '\q' 2>/dev/null; then
    echo -e "${GREEN}✓ Database connection successful${NC}"
else
    echo -e "${RED}✗ Failed to connect to database${NC}"
    echo -e "${YELLOW}Please ensure:${NC}"
    echo -e "  1. PostgreSQL is running"
    echo -e "  2. Database '$DB_NAME' exists"
    echo -e "  3. User '$DB_USER' has access"
    echo -e "  4. Password is correct"
    echo ""
    echo -e "${YELLOW}Try running:${NC}"
    echo -e "  docker-compose up -d product-db"
    exit 1
fi

echo ""

# Confirmation prompt
read -p "$(echo -e ${YELLOW}⚠ This will insert sample data into the database. Continue? [y/N]: ${NC})" -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo -e "${YELLOW}Migration cancelled.${NC}"
    exit 0
fi

echo ""
echo -e "${YELLOW}Executing migration...${NC}"
echo -e "${BLUE}═══════════════════════════════════════════════════${NC}"

# Execute migration
if psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -f "$MIGRATION_FILE"; then
    echo -e "${BLUE}═══════════════════════════════════════════════════${NC}"
    echo -e "${GREEN}✓ Migration completed successfully!${NC}"
    echo ""

    # Display summary statistics
    echo -e "${YELLOW}Database Summary:${NC}"
    psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -t -c "
        SELECT 'Products:         ' || COUNT(*) FROM product
        UNION ALL
        SELECT 'Customers:        ' || COUNT(*) FROM customer
        UNION ALL
        SELECT 'Suppliers:        ' || COUNT(*) FROM supplier
        UNION ALL
        SELECT 'Stock Entries:    ' || COUNT(*) FROM stock
        UNION ALL
        SELECT 'Purchase Orders:  ' || COUNT(*) FROM purchaseorder
        UNION ALL
        SELECT 'Sale Orders:      ' || COUNT(*) FROM saleorder
        UNION ALL
        SELECT 'Stock Movements:  ' || COUNT(*) FROM stockmovement;
    " | sed 's/^/  /'

    echo ""
    echo -e "${GREEN}✓ Sample data loaded successfully!${NC}"
    echo ""
    echo -e "${YELLOW}Next Steps:${NC}"
    echo -e "  1. Start the application:  ${BLUE}./gradlew quarkusDev${NC}"
    echo -e "  2. Access dashboard:       ${BLUE}http://localhost:8080${NC}"
    echo -e "  3. View stock analytics:   ${BLUE}http://localhost:8080/stock/dashboard${NC}"
    echo ""
else
    echo -e "${BLUE}═══════════════════════════════════════════════════${NC}"
    echo -e "${RED}✗ Migration failed. Check errors above.${NC}"
    exit 1
fi

unset PGPASSWORD
