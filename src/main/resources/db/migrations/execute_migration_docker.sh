#!/bin/bash

# =====================================================
# Database Migration Execution Script (Docker Version)
# =====================================================
# This script executes SQL migration files when using Docker containers
# =====================================================

set -e  # Exit on error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Default Docker container name (override with environment variable)
CONTAINER_NAME="${DB_CONTAINER:-product-db}"
DB_NAME="${DB_NAME:-product_db}"
DB_USER="${DB_USER:-product_user}"

# Script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
MIGRATION_FILE="${SCRIPT_DIR}/V001__seed_sample_data.sql"
TEMP_PATH="/tmp/migration.sql"

echo -e "${BLUE}═══════════════════════════════════════════════════${NC}"
echo -e "${BLUE}  Product Manager - Docker Migration Tool${NC}"
echo -e "${BLUE}═══════════════════════════════════════════════════${NC}"
echo ""

# Check if migration file exists
if [ ! -f "$MIGRATION_FILE" ]; then
    echo -e "${RED}✗ Migration file not found: $MIGRATION_FILE${NC}"
    exit 1
fi

# Check if docker is available
if ! command -v docker &> /dev/null; then
    echo -e "${RED}✗ Docker command not found. Please install Docker.${NC}"
    exit 1
fi

# Check if container is running
echo -e "${YELLOW}Checking Docker container status...${NC}"
if ! docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
    echo -e "${RED}✗ Container '$CONTAINER_NAME' is not running${NC}"
    echo ""
    echo -e "${YELLOW}Available containers:${NC}"
    docker ps --format "  - {{.Names}} ({{.Status}})"
    echo ""
    echo -e "${YELLOW}To start the database container:${NC}"
    echo -e "  docker-compose up -d"
    echo ""
    echo -e "${YELLOW}Or specify a different container:${NC}"
    echo -e "  DB_CONTAINER=my-postgres-container $0"
    exit 1
fi

echo -e "${GREEN}✓ Container '$CONTAINER_NAME' is running${NC}"
echo ""

# Display container info
echo -e "${YELLOW}Container Configuration:${NC}"
echo -e "  Container:  ${CONTAINER_NAME}"
echo -e "  Database:   ${DB_NAME}"
echo -e "  User:       ${DB_USER}"
echo ""

# Confirmation prompt
read -p "$(echo -e ${YELLOW}⚠ This will insert sample data into the database. Continue? [y/N]: ${NC})" -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo -e "${YELLOW}Migration cancelled.${NC}"
    exit 0
fi

echo ""
echo -e "${YELLOW}Copying migration file to container...${NC}"

# Copy migration file to container
if docker cp "$MIGRATION_FILE" "${CONTAINER_NAME}:${TEMP_PATH}"; then
    echo -e "${GREEN}✓ File copied successfully${NC}"
else
    echo -e "${RED}✗ Failed to copy file to container${NC}"
    exit 1
fi

echo ""
echo -e "${YELLOW}Executing migration...${NC}"
echo -e "${BLUE}═══════════════════════════════════════════════════${NC}"

# Execute migration inside container
if docker exec -i "$CONTAINER_NAME" psql -U "$DB_USER" -d "$DB_NAME" -f "$TEMP_PATH"; then
    echo -e "${BLUE}═══════════════════════════════════════════════════${NC}"
    echo -e "${GREEN}✓ Migration completed successfully!${NC}"
    echo ""

    # Display summary statistics
    echo -e "${YELLOW}Database Summary:${NC}"
    docker exec -i "$CONTAINER_NAME" psql -U "$DB_USER" -d "$DB_NAME" -t -c "
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

    # Cleanup temp file
    docker exec -i "$CONTAINER_NAME" rm -f "$TEMP_PATH"

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

    # Cleanup temp file even on failure
    docker exec -i "$CONTAINER_NAME" rm -f "$TEMP_PATH" 2>/dev/null || true
    exit 1
fi
