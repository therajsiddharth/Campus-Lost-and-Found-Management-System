# Academic Mapping

## Data Structures & Algorithms
| Concept | Implementation | Why it is used |
|---|---|---|
| ArrayList | ItemDAO result lists / Swing browse results | Dynamic collection of matching items |
| HashMap | MatchingEngine.indexById | Average O(1) lookup by item ID |
| Queue | ClaimDAO.pendingQueue() using ArrayDeque | FIFO order for pending claim review |
| Linear search | MatchingEngine.possibleMatches | Scan candidate items for a possible match |
| Sorting | Comparator in MatchingEngine | Show recent possible matches first |

Complexities to explain:
- HashMap lookup: average O(1), worst-case can degrade.
- Linear scan: O(n).
- Sorting: O(n log n) using Java's list sort (TimSort implementation).

## Java OOP
- Encapsulation: private fields in User, Item and Claim.
- Inheritance: Student/Admin extend User; LostItem/FoundItem extend Item.
- Abstraction: User and Item are abstract classes; PortalActions is an interface.
- Polymorphism: role-specific dashboardTitle() and item getItemType() overrides.

## DBMS
- Primary keys: users.user_id, items.item_id, claims.claim_id, categories.category_id.
- Foreign keys: item reporter/category/verifier; claim item/claimant/reviewer; notification user; audit admin.
- Normalization: categories are stored separately; user data is not duplicated in item/claim records.
- JDBC prepared statements are used for parameterized SQL.
- Joins and aggregate query examples are included in README and schema comments.
