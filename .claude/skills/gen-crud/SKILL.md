---
description: Generate complete CRUD stack (entity, repository, service, controller, resource, templates)
disable-model-invocation: true
---

# /gen-crud - Generate Full CRUD Stack

Generate a complete CRUD implementation for a new entity.

## Usage
```
/gen-crud <EntityName> [field:type, field:type, ...]
```

## Examples
```
/gen-crud Category name:String, description:String?
/gen-crud Tag name:String, color:String?
```

## What Gets Generated

1. **Entity** - `domain/model/{EntityName}.kt`
2. **Repository** - `domain/repository/{EntityName}Repository.kt`
3. **Service** - `application/service/{EntityName}Service.kt`
4. **Controller** - `infrastructure/web/{EntityName}Controller.kt`
5. **Resource** - `infrastructure/web/{EntityName}Resource.kt`
6. **Templates**:
   - `resources/templates/{entityNames}/{entityName}List.html`
   - `resources/templates/{entityNames}/{entityName}Form.html`

## Generation Order

Execute in this order to satisfy dependencies:

1. Create Entity (`/gen-entity`)
2. Create Repository (included with entity)
3. Create Service (`/gen-service`)
4. Create Controller (`/gen-controller`)
5. Create Resource (`/gen-resource`)
6. Create Templates (`/gen-template list`, `/gen-template form`)

## File Structure Created
```
src/main/kotlin/com/example/product/
├── domain/
│   ├── model/
│   │   └── {EntityName}.kt
│   └── repository/
│       └── {EntityName}Repository.kt
├── application/
│   └── service/
│       └── {EntityName}Service.kt
└── infrastructure/
    └── web/
        ├── {EntityName}Controller.kt
        └── {EntityName}Resource.kt

src/main/resources/templates/
└── {entityNames}/
    ├── {entityName}List.html
    └── {entityName}Form.html
```

## Conventions
- Entity name: PascalCase singular (e.g., `Category`)
- Package paths: lowercase (e.g., `categories`)
- URL paths: lowercase plural (e.g., `/categories`, `/api/categories`)

## Post-Generation Steps
1. Add navigation link to templates
2. Update any related entities/services
3. Run `./gradlew build` to verify compilation
4. Test at http://localhost:8080/{entityNames}

## Reference
See existing patterns:
- Product: Full entity with CRUD and detail view
- Customer/Supplier: Simple CRUD entities
- PurchaseOrder/SaleOrder: Complex entities with relationships
