-- =====================================================
-- Product Manager - Sample Data Migration Script
-- =====================================================
-- This script inserts comprehensive sample data for testing and demonstration
-- Execute after initial schema creation
-- =====================================================

-- Clean existing data (optional - comment out if you want to preserve data)
-- TRUNCATE TABLE saleorderitem, purchaseorderitem, stockmovement, stock, saleorder, purchaseorder, product, customer, supplier RESTART IDENTITY CASCADE;

-- =====================================================
-- 1. INSERT PRODUCTS
-- =====================================================
INSERT INTO product (id, name, description, category, price, purchasePrice, profitMargin, createdAt, updatedAt, createdBy) VALUES
-- Electronics
(1, 'Laptop Dell Inspiron 15', 'Intel Core i5, 8GB RAM, 256GB SSD, 15.6" Full HD display', 'Electronics', 899.99, 650.00, 38.46, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system'),
(2, 'Wireless Mouse Logitech MX Master 3', 'Ergonomic wireless mouse with USB-C charging', 'Electronics', 99.99, 65.00, 53.83, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system'),
(3, 'Mechanical Keyboard Razer BlackWidow', 'RGB mechanical gaming keyboard with green switches', 'Electronics', 139.99, 85.00, 64.69, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system'),
(4, 'USB-C Hub Anker 7-in-1', 'HDMI, USB 3.0, SD/TF card reader, 100W power delivery', 'Electronics', 49.99, 28.00, 78.54, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system'),
(5, 'Webcam Logitech C920 HD Pro', '1080p HD video calling, stereo audio, auto-focus', 'Electronics', 79.99, 52.00, 53.83, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system'),

-- Office Furniture
(6, 'Ergonomic Office Chair', 'Adjustable lumbar support, mesh back, armrests', 'Furniture', 249.99, 150.00, 66.66, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system'),
(7, 'Standing Desk Adjustable', 'Electric height adjustable desk, 48" x 30", memory presets', 'Furniture', 399.99, 250.00, 60.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system'),
(8, 'Filing Cabinet 3-Drawer', 'Lockable steel filing cabinet with label holders', 'Furniture', 159.99, 95.00, 68.41, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system'),
(9, 'Bookshelf 5-Tier', 'Modern wooden bookshelf, 70" H x 30" W', 'Furniture', 129.99, 75.00, 73.32, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system'),

-- Office Supplies
(10, 'Copy Paper A4 - Box of 5 Reams', 'Premium 75gsm white copy paper, 2500 sheets total', 'Office Supplies', 39.99, 22.00, 81.77, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system'),
(11, 'Ballpoint Pens - Box of 50', 'Blue ink, medium point, comfortable grip', 'Office Supplies', 12.99, 6.50, 99.85, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system'),
(12, 'Sticky Notes Multicolor Pack', '12 pads, 100 sheets each, assorted colors', 'Office Supplies', 9.99, 5.00, 99.80, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system'),
(13, 'Stapler Heavy Duty', 'Metal construction, 100-sheet capacity', 'Office Supplies', 24.99, 14.00, 78.50, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system'),
(14, 'Whiteboard Markers - 12 Pack', 'Dry erase markers, assorted colors, fine tip', 'Office Supplies', 15.99, 8.50, 88.12, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system'),

-- Networking Equipment
(15, 'WiFi Router TP-Link AX3000', 'Dual-band wireless router, WiFi 6, 4 gigabit ports', 'Networking', 149.99, 95.00, 57.88, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system'),
(16, 'Network Switch 8-Port Gigabit', 'Unmanaged switch, plug and play, metal housing', 'Networking', 29.99, 18.00, 66.61, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system'),
(17, 'CAT6 Ethernet Cable 50ft', 'High-speed network cable, RJ45 connectors', 'Networking', 12.99, 6.00, 116.50, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system'),

-- Monitors & Displays
(18, 'Monitor Dell 27" 4K UHD', 'IPS panel, 3840x2160, USB-C connectivity', 'Electronics', 449.99, 280.00, 60.71, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system'),
(19, 'Monitor Arm Dual Mount', 'Adjustable dual monitor stand, VESA compatible', 'Accessories', 79.99, 45.00, 77.76, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system'),

-- Storage & Backup
(20, 'External SSD 1TB Samsung T7', 'Portable solid-state drive, USB 3.2, 1050MB/s', 'Electronics', 119.99, 75.00, 59.99, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system');

-- Set sequence to continue from last ID
SELECT setval('product_id_seq', 20, true);

-- =====================================================
-- 2. INSERT CUSTOMERS
-- =====================================================
INSERT INTO customer (id, name, document, email, phone, address, createdAt) VALUES
-- Individual Customers
(1, 'John Anderson', '123.456.789-01', 'john.anderson@email.com', '(555) 123-4567', '123 Main St, New York, NY 10001', CURRENT_TIMESTAMP),
(2, 'Sarah Johnson', '234.567.890-12', 'sarah.johnson@email.com', '(555) 234-5678', '456 Oak Ave, Los Angeles, CA 90001', CURRENT_TIMESTAMP),
(3, 'Michael Chen', '345.678.901-23', 'michael.chen@email.com', '(555) 345-6789', '789 Pine Rd, Chicago, IL 60601', CURRENT_TIMESTAMP),
(4, 'Emily Rodriguez', '456.789.012-34', 'emily.rodriguez@email.com', '(555) 456-7890', '321 Elm St, Houston, TX 77001', CURRENT_TIMESTAMP),
(5, 'David Kim', '567.890.123-45', 'david.kim@email.com', '(555) 567-8901', '654 Maple Dr, Phoenix, AZ 85001', CURRENT_TIMESTAMP),

-- Business Customers
(6, 'TechStart Solutions LLC', '12.345.678/0001-90', 'procurement@techstart.com', '(555) 678-9012', '1000 Tech Park Dr, San Francisco, CA 94101', CURRENT_TIMESTAMP),
(7, 'Global Consulting Group', '23.456.789/0001-01', 'purchasing@globalconsult.com', '(555) 789-0123', '2500 Business Blvd, Boston, MA 02101', CURRENT_TIMESTAMP),
(8, 'Creative Design Studio', '34.567.890/0001-12', 'admin@creativedesign.com', '(555) 890-1234', '500 Innovation Way, Austin, TX 78701', CURRENT_TIMESTAMP),
(9, 'Summit Financial Advisors', '45.678.901/0001-23', 'office@summitfinancial.com', '(555) 901-2345', '3000 Finance Plaza, Seattle, WA 98101', CURRENT_TIMESTAMP),
(10, 'EduTech Academy', '56.789.012/0001-34', 'procurement@edutech.edu', '(555) 012-3456', '750 Education Ln, Miami, FL 33101', CURRENT_TIMESTAMP);

SELECT setval('customer_id_seq', 10, true);

-- =====================================================
-- 3. INSERT SUPPLIERS
-- =====================================================
INSERT INTO supplier (id, name, document, email, phone, address, createdAt) VALUES
(1, 'Tech Distributors Inc', '11.222.333/0001-44', 'sales@techdist.com', '(555) 111-2222', '100 Industrial Park, Newark, NJ 07101', CURRENT_TIMESTAMP),
(2, 'Office Supplies Wholesale', '22.333.444/0001-55', 'orders@officesupply.com', '(555) 222-3333', '200 Commerce St, Dallas, TX 75201', CURRENT_TIMESTAMP),
(3, 'Furniture Direct Manufacturing', '33.444.555/0001-66', 'wholesale@furnituredirect.com', '(555) 333-4444', '300 Factory Rd, Grand Rapids, MI 49501', CURRENT_TIMESTAMP),
(4, 'Network Solutions Provider', '44.555.666/0001-77', 'partners@netsolutions.com', '(555) 444-5555', '400 Tech Center, San Jose, CA 95101', CURRENT_TIMESTAMP),
(5, 'Global Electronics Supply', '55.666.777/0001-88', 'b2b@globalelectronics.com', '(555) 555-6666', '500 Distribution Way, Atlanta, GA 30301', CURRENT_TIMESTAMP);

SELECT setval('supplier_id_seq', 5, true);

-- =====================================================
-- 4. INSERT STOCK (Multi-Warehouse)
-- =====================================================
INSERT INTO stock (id, product_id, location, quantity, minQuantity, maxQuantity, lastUpdated) VALUES
-- Main Warehouse - Primary inventory
(1, 1, 'MAIN_WAREHOUSE', 25, 5, 50, CURRENT_TIMESTAMP),  -- Laptops
(2, 2, 'MAIN_WAREHOUSE', 80, 20, 150, CURRENT_TIMESTAMP), -- Mice
(3, 3, 'MAIN_WAREHOUSE', 45, 10, 100, CURRENT_TIMESTAMP), -- Keyboards
(4, 4, 'MAIN_WAREHOUSE', 120, 30, 200, CURRENT_TIMESTAMP), -- USB-C Hubs
(5, 5, 'MAIN_WAREHOUSE', 35, 10, 80, CURRENT_TIMESTAMP),  -- Webcams
(6, 6, 'MAIN_WAREHOUSE', 15, 5, 30, CURRENT_TIMESTAMP),   -- Office Chairs
(7, 7, 'MAIN_WAREHOUSE', 8, 3, 20, CURRENT_TIMESTAMP),    -- Standing Desks
(8, 8, 'MAIN_WAREHOUSE', 22, 5, 40, CURRENT_TIMESTAMP),   -- Filing Cabinets
(9, 9, 'MAIN_WAREHOUSE', 18, 5, 35, CURRENT_TIMESTAMP),   -- Bookshelves
(10, 10, 'MAIN_WAREHOUSE', 150, 50, 300, CURRENT_TIMESTAMP), -- Copy Paper
(11, 11, 'MAIN_WAREHOUSE', 200, 50, 400, CURRENT_TIMESTAMP), -- Pens
(12, 12, 'MAIN_WAREHOUSE', 180, 40, 300, CURRENT_TIMESTAMP), -- Sticky Notes
(13, 13, 'MAIN_WAREHOUSE', 60, 15, 120, CURRENT_TIMESTAMP),  -- Staplers
(14, 14, 'MAIN_WAREHOUSE', 90, 25, 180, CURRENT_TIMESTAMP),  -- Markers
(15, 15, 'MAIN_WAREHOUSE', 40, 10, 80, CURRENT_TIMESTAMP),   -- Routers
(16, 16, 'MAIN_WAREHOUSE', 75, 20, 150, CURRENT_TIMESTAMP),  -- Network Switches
(17, 17, 'MAIN_WAREHOUSE', 250, 60, 500, CURRENT_TIMESTAMP), -- Ethernet Cables
(18, 18, 'MAIN_WAREHOUSE', 20, 5, 40, CURRENT_TIMESTAMP),    -- Monitors
(19, 19, 'MAIN_WAREHOUSE', 30, 8, 60, CURRENT_TIMESTAMP),    -- Monitor Arms
(20, 20, 'MAIN_WAREHOUSE', 55, 15, 120, CURRENT_TIMESTAMP),  -- External SSDs

-- Secondary Warehouse - Overflow and backup stock
(21, 1, 'SECONDARY_WAREHOUSE', 10, 3, 25, CURRENT_TIMESTAMP),
(22, 2, 'SECONDARY_WAREHOUSE', 50, 10, 100, CURRENT_TIMESTAMP),
(23, 3, 'SECONDARY_WAREHOUSE', 30, 8, 60, CURRENT_TIMESTAMP),
(24, 10, 'SECONDARY_WAREHOUSE', 100, 30, 200, CURRENT_TIMESTAMP),
(25, 15, 'SECONDARY_WAREHOUSE', 20, 5, 50, CURRENT_TIMESTAMP),
(26, 18, 'SECONDARY_WAREHOUSE', 12, 3, 25, CURRENT_TIMESTAMP),

-- Retail Store - Customer-facing inventory
(27, 2, 'RETAIL_STORE', 25, 8, 50, CURRENT_TIMESTAMP),  -- Mice
(28, 3, 'RETAIL_STORE', 15, 5, 35, CURRENT_TIMESTAMP),  -- Keyboards
(29, 4, 'RETAIL_STORE', 40, 15, 80, CURRENT_TIMESTAMP), -- USB-C Hubs
(30, 5, 'RETAIL_STORE', 18, 6, 40, CURRENT_TIMESTAMP),  -- Webcams
(31, 11, 'RETAIL_STORE', 60, 20, 120, CURRENT_TIMESTAMP), -- Pens
(32, 12, 'RETAIL_STORE', 50, 15, 100, CURRENT_TIMESTAMP), -- Sticky Notes
(33, 14, 'RETAIL_STORE', 35, 10, 70, CURRENT_TIMESTAMP),  -- Markers
(34, 17, 'RETAIL_STORE', 80, 25, 150, CURRENT_TIMESTAMP), -- Ethernet Cables
(35, 20, 'RETAIL_STORE', 22, 8, 50, CURRENT_TIMESTAMP),   -- External SSDs

-- Distribution Center - B2B fulfillment
(36, 1, 'DISTRIBUTION_CENTER', 15, 5, 30, CURRENT_TIMESTAMP),  -- Laptops
(37, 6, 'DISTRIBUTION_CENTER', 20, 5, 40, CURRENT_TIMESTAMP),  -- Office Chairs
(38, 7, 'DISTRIBUTION_CENTER', 12, 3, 25, CURRENT_TIMESTAMP),  -- Standing Desks
(39, 8, 'DISTRIBUTION_CENTER', 18, 5, 35, CURRENT_TIMESTAMP),  -- Filing Cabinets
(40, 15, 'DISTRIBUTION_CENTER', 25, 8, 50, CURRENT_TIMESTAMP), -- Routers
(41, 16, 'DISTRIBUTION_CENTER', 45, 12, 90, CURRENT_TIMESTAMP), -- Network Switches
(42, 18, 'DISTRIBUTION_CENTER', 10, 3, 20, CURRENT_TIMESTAMP); -- Monitors

SELECT setval('stock_id_seq', 42, true);

-- =====================================================
-- 5. INSERT PURCHASE ORDERS
-- =====================================================
INSERT INTO purchaseorder (id, supplier_id, orderDate, status, location, totalAmount, notes, createdAt, updatedAt, createdBy) VALUES
(1, 1, CURRENT_DATE - INTERVAL '30 days', 'RECEIVED', 'MAIN_WAREHOUSE', 16249.75, 'Initial laptop inventory', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin'),
(2, 2, CURRENT_DATE - INTERVAL '25 days', 'RECEIVED', 'MAIN_WAREHOUSE', 4599.50, 'Office supplies restock', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin'),
(3, 3, CURRENT_DATE - INTERVAL '20 days', 'RECEIVED', 'MAIN_WAREHOUSE', 8749.70, 'Furniture order for new office', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin'),
(4, 4, CURRENT_DATE - INTERVAL '15 days', 'CONFIRMED', 'SECONDARY_WAREHOUSE', 3425.00, 'Network equipment expansion', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin'),
(5, 5, CURRENT_DATE - INTERVAL '10 days', 'RECEIVED', 'DISTRIBUTION_CENTER', 5620.80, 'Electronics restock', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin'),
(6, 1, CURRENT_DATE - INTERVAL '5 days', 'PENDING', 'MAIN_WAREHOUSE', 8999.00, 'Monitors for corporate client', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin'),
(7, 2, CURRENT_DATE - INTERVAL '3 days', 'CONFIRMED', 'RETAIL_STORE', 1245.75, 'Retail store supplies', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'store_manager');

SELECT setval('purchaseorder_id_seq', 7, true);

-- =====================================================
-- 6. INSERT PURCHASE ORDER ITEMS
-- =====================================================
INSERT INTO purchaseorderitem (id, purchaseOrder_id, product_id, quantity, unitPrice, subtotal) VALUES
-- Purchase Order 1: Laptops and accessories
(1, 1, 1, 25, 650.00, 16250.00),

-- Purchase Order 2: Office supplies
(2, 2, 10, 50, 22.00, 1100.00),
(3, 2, 11, 100, 6.50, 650.00),
(4, 2, 12, 150, 5.00, 750.00),
(5, 2, 13, 30, 14.00, 420.00),
(6, 2, 14, 80, 8.50, 680.00),

-- Purchase Order 3: Furniture
(7, 3, 6, 25, 150.00, 3750.00),
(8, 3, 7, 12, 250.00, 3000.00),
(9, 3, 8, 18, 95.00, 1710.00),
(10, 3, 9, 4, 75.00, 300.00),

-- Purchase Order 4: Networking (Confirmed, not yet received)
(11, 4, 15, 20, 95.00, 1900.00),
(12, 4, 16, 50, 18.00, 900.00),
(13, 4, 17, 100, 6.00, 600.00),

-- Purchase Order 5: Electronics
(14, 5, 2, 50, 65.00, 3250.00),
(15, 5, 5, 20, 52.00, 1040.00),
(16, 5, 20, 30, 75.00, 2250.00),

-- Purchase Order 6: Monitors (Pending)
(17, 6, 18, 20, 280.00, 5600.00),
(18, 6, 19, 15, 45.00, 675.00),

-- Purchase Order 7: Retail supplies
(19, 7, 11, 50, 6.50, 325.00),
(20, 7, 12, 80, 5.00, 400.00),
(21, 7, 14, 40, 8.50, 340.00);

SELECT setval('purchaseorderitem_id_seq', 21, true);

-- =====================================================
-- 7. INSERT SALE ORDERS
-- =====================================================
INSERT INTO saleorder (id, customer_id, orderDate, status, location, totalAmount, notes, createdAt, updatedAt, createdBy) VALUES
-- Recent sales - mix of statuses and locations
(1, 6, CURRENT_DATE - INTERVAL '7 days', 'RECEIVED', 'MAIN_WAREHOUSE', 8999.75, 'Corporate order for TechStart Solutions - 10 laptops', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'sales_rep'),
(2, 7, CURRENT_DATE - INTERVAL '6 days', 'RECEIVED', 'DISTRIBUTION_CENTER', 12998.85, 'Global Consulting office setup', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'sales_rep'),
(3, 1, CURRENT_DATE - INTERVAL '5 days', 'RECEIVED', 'RETAIL_STORE', 1049.97, 'John Anderson - home office setup', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'retail_clerk'),
(4, 8, CURRENT_DATE - INTERVAL '4 days', 'CONFIRMED', 'MAIN_WAREHOUSE', 5599.90, 'Creative Design Studio - workstation equipment', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'sales_rep'),
(5, 2, CURRENT_DATE - INTERVAL '3 days', 'RECEIVED', 'RETAIL_STORE', 229.97, 'Sarah Johnson - accessories purchase', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'retail_clerk'),
(6, 9, CURRENT_DATE - INTERVAL '2 days', 'CONFIRMED', 'DISTRIBUTION_CENTER', 15999.60, 'Summit Financial - office furniture bulk order', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'sales_rep'),
(7, 3, CURRENT_DATE - INTERVAL '1 day', 'PENDING', 'RETAIL_STORE', 569.96, 'Michael Chen - gaming peripherals', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'retail_clerk'),
(8, 10, CURRENT_DATE, 'PENDING', 'MAIN_WAREHOUSE', 8999.80, 'EduTech Academy - computer lab equipment', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'sales_rep'),
(9, 4, CURRENT_DATE, 'CONFIRMED', 'RETAIL_STORE', 199.98, 'Emily Rodriguez - office supplies', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'retail_clerk'),
(10, 5, CURRENT_DATE - INTERVAL '8 days', 'RECEIVED', 'MAIN_WAREHOUSE', 1349.95, 'David Kim - home network upgrade', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'sales_rep');

SELECT setval('saleorder_id_seq', 10, true);

-- =====================================================
-- 8. INSERT SALE ORDER ITEMS
-- =====================================================
INSERT INTO saleorderitem (id, saleOrder_id, product_id, quantity, unitPrice, subtotal) VALUES
-- Sale 1: Corporate laptops
(1, 1, 1, 10, 899.99, 8999.90),

-- Sale 2: Office setup (furniture + equipment)
(2, 2, 6, 12, 249.99, 2999.88),
(3, 2, 7, 8, 399.99, 3199.92),
(4, 2, 15, 5, 149.99, 749.95),
(5, 2, 18, 4, 449.99, 1799.96),
(6, 2, 2, 20, 99.99, 1999.80),
(7, 2, 3, 10, 139.99, 1399.90),

-- Sale 3: Home office
(8, 3, 1, 1, 899.99, 899.99),
(9, 3, 2, 1, 99.99, 99.99),
(10, 3, 4, 1, 49.99, 49.99),

-- Sale 4: Workstation equipment
(11, 4, 18, 4, 449.99, 1799.96),
(12, 4, 7, 2, 399.99, 799.98),
(13, 4, 6, 6, 249.99, 1499.94),
(14, 4, 3, 6, 139.99, 839.94),
(15, 4, 5, 4, 79.99, 319.96),

-- Sale 5: Accessories
(16, 5, 2, 1, 99.99, 99.99),
(17, 5, 4, 1, 49.99, 49.99),
(18, 5, 17, 2, 12.99, 25.98),
(19, 5, 20, 1, 119.99, 119.99),

-- Sale 6: Furniture bulk order
(20, 6, 6, 30, 249.99, 7499.70),
(21, 6, 7, 15, 399.99, 5999.85),
(22, 6, 8, 20, 159.99, 3199.80),

-- Sale 7: Gaming peripherals
(23, 7, 3, 2, 139.99, 279.98),
(24, 7, 2, 2, 99.99, 199.98),
(25, 7, 5, 1, 79.99, 79.99),

-- Sale 8: Computer lab
(26, 8, 1, 10, 899.99, 8999.90),

-- Sale 9: Office supplies
(27, 9, 11, 5, 12.99, 64.95),
(28, 9, 12, 10, 9.99, 99.90),
(29, 9, 14, 3, 15.99, 47.97),

-- Sale 10: Network upgrade
(30, 10, 15, 2, 149.99, 299.98),
(31, 10, 16, 4, 29.99, 119.96),
(32, 10, 17, 20, 12.99, 259.80),
(33, 10, 20, 2, 119.99, 239.98),
(34, 10, 4, 5, 49.99, 249.95);

SELECT setval('saleorderitem_id_seq', 34, true);

-- =====================================================
-- 9. INSERT STOCK MOVEMENTS (Historical tracking)
-- =====================================================
-- Note: This would typically be auto-generated by the system
-- Including a few examples for demonstration

INSERT INTO stockmovement (id, product_id, location, movementType, quantity, reason, createdAt, createdBy) VALUES
-- Initial stock additions from purchases
(1, 1, 'MAIN_WAREHOUSE', 'IN', 25, 'Purchase Order #1 - Initial inventory', CURRENT_DATE - INTERVAL '30 days', 'system'),
(2, 10, 'MAIN_WAREHOUSE', 'IN', 50, 'Purchase Order #2 - Office supplies', CURRENT_DATE - INTERVAL '25 days', 'system'),
(3, 6, 'MAIN_WAREHOUSE', 'IN', 25, 'Purchase Order #3 - Furniture', CURRENT_DATE - INTERVAL '20 days', 'system'),

-- Stock deductions from sales
(4, 1, 'MAIN_WAREHOUSE', 'OUT', 10, 'Sale Order #1 - TechStart Solutions', CURRENT_DATE - INTERVAL '7 days', 'system'),
(5, 6, 'DISTRIBUTION_CENTER', 'OUT', 12, 'Sale Order #2 - Global Consulting', CURRENT_DATE - INTERVAL '6 days', 'system'),

-- Manual adjustments
(6, 2, 'MAIN_WAREHOUSE', 'ADJUSTMENT', 5, 'Inventory count correction', CURRENT_DATE - INTERVAL '10 days', 'warehouse_manager'),
(7, 17, 'RETAIL_STORE', 'ADJUSTMENT', -3, 'Damaged items removed', CURRENT_DATE - INTERVAL '5 days', 'store_manager');

SELECT setval('stockmovement_id_seq', 7, true);

-- =====================================================
-- VERIFICATION QUERIES
-- =====================================================
-- Uncomment to verify data insertion

-- SELECT 'Products:', COUNT(*) FROM product;
-- SELECT 'Customers:', COUNT(*) FROM customer;
-- SELECT 'Suppliers:', COUNT(*) FROM supplier;
-- SELECT 'Stock Entries:', COUNT(*) FROM stock;
-- SELECT 'Purchase Orders:', COUNT(*) FROM purchaseorder;
-- SELECT 'Sale Orders:', COUNT(*) FROM saleorder;
-- SELECT 'Stock Movements:', COUNT(*) FROM stockmovement;

-- =====================================================
-- SUMMARY STATISTICS
-- =====================================================
-- Total inventory value by location
-- SELECT
--     s.location,
--     COUNT(DISTINCT s.product_id) as product_count,
--     SUM(s.quantity) as total_units,
--     SUM(s.quantity * p.price) as inventory_value
-- FROM stock s
-- JOIN product p ON s.product_id = p.id
-- GROUP BY s.location
-- ORDER BY s.location;

-- Top selling products
-- SELECT
--     p.name,
--     p.category,
--     SUM(si.quantity) as units_sold,
--     SUM(si.subtotal) as total_revenue
-- FROM saleorderitem si
-- JOIN product p ON si.product_id = p.id
-- GROUP BY p.id, p.name, p.category
-- ORDER BY total_revenue DESC
-- LIMIT 10;

COMMIT;
