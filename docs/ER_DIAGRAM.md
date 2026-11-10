# ER Diagram (text specification)

```mermaid
erDiagram
    USERS ||--o{ ITEMS : reports
    CATEGORIES ||--o{ ITEMS : classifies
    USERS ||--o{ CLAIMS : submits
    ITEMS ||--o{ CLAIMS : receives
    USERS ||--o{ CLAIMS : reviews
    USERS ||--o{ NOTIFICATIONS : receives
    USERS ||--o{ ADMIN_LOGS : performs

    USERS {
      int user_id PK
      string full_name
      string email
      string password_hash
      enum role
      enum account_status
    }
    CATEGORIES {
      int category_id PK
      string category_name
    }
    ITEMS {
      int item_id PK
      int reporter_id FK
      int category_id FK
      string item_name
      enum item_type
      enum item_status
    }
    CLAIMS {
      int claim_id PK
      int item_id FK
      int claimant_id FK
      int reviewed_by FK
      enum claim_status
    }
```

A user may report many items. A category may classify many items. An item may receive multiple claims. A user may submit multiple claims and an administrator may review many claims.
