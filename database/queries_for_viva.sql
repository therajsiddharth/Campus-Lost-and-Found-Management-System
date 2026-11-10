USE campus_lost_found;

-- 1. List approved found items with reporter and category
SELECT i.item_id, i.item_name, u.full_name, c.category_name, i.location
FROM items i
JOIN users u ON u.user_id=i.reporter_id
JOIN categories c ON c.category_id=i.category_id
WHERE i.item_type='FOUND' AND i.item_status='APPROVED';

-- 2. Count items by category
SELECT c.category_name, COUNT(*) AS total_items
FROM items i JOIN categories c ON c.category_id=i.category_id
GROUP BY c.category_id, c.category_name
ORDER BY total_items DESC;

-- 3. Pending claims in FIFO order
SELECT claim_id, item_id, claimant_id, claim_date
FROM claims WHERE claim_status='PENDING' ORDER BY claim_date ASC;

-- 4. Recovery rate (safe against division by zero)
SELECT ROUND(100.0 * SUM(item_status='RETURNED') / NULLIF(COUNT(*),0), 2) AS recovery_percentage
FROM items;
