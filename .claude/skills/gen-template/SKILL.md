---
description: Generate Qute HTML templates with PatternFly styling
disable-model-invocation: true
---

# /gen-template - Generate Qute Template

Generate Qute HTML templates with PatternFly v6 styling.

## Usage
```
/gen-template <type> <EntityName>
```

Types: `list`, `form`, `detail`

## Examples
```
/gen-template list Category
/gen-template form Category
/gen-template detail Order
```

## Project Pattern

Location: `src/main/resources/templates/{entityNames}/`

### List Template (`{entityName}List.html`)
```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>{EntityNames} - Product Manager</title>
    <link rel="stylesheet" href="https://unpkg.com/@patternfly/patternfly@6/patternfly.min.css">
</head>
<body>
    <nav class="pf-v6-c-page__header">
        <div class="pf-v6-c-page__header-brand">
            <div class="pf-v6-c-page__header-brand-link">
                <a href="/" class="pf-v6-c-brand">
                    <h1 style="color: white; margin: 0; font-size: 1.5rem;">Product Manager</h1>
                </a>
            </div>
        </div>
        <div class="pf-v6-c-page__header-nav">
            <nav class="pf-v6-c-nav pf-m-horizontal">
                <ul class="pf-v6-c-nav__list">
                    <li class="pf-v6-c-nav__item">
                        <a href="/" class="pf-v6-c-nav__link">Home</a>
                    </li>
                    <li class="pf-v6-c-nav__item">
                        <a href="/products" class="pf-v6-c-nav__link">Products</a>
                    </li>
                    <li class="pf-v6-c-nav__item">
                        <a href="/{entityNames}" class="pf-v6-c-nav__link pf-m-current">{EntityNames}</a>
                    </li>
                </ul>
            </nav>
        </div>
    </nav>

    <main class="pf-v6-c-page__main" style="padding: 2rem;">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem;">
            <h1>{EntityNames}</h1>
            <a href="/{entityNames}/new" class="pf-v6-c-button pf-m-primary">Add {EntityName}</a>
        </div>

        {#if {entityNames}.isEmpty()}
        <div class="pf-v6-c-empty-state" style="margin-top: 2rem;">
            <div class="pf-v6-c-empty-state__content">
                <h2 class="pf-v6-c-title pf-m-lg">No {entityNames} found</h2>
                <a href="/{entityNames}/new" class="pf-v6-c-button pf-m-primary">Add {EntityName}</a>
            </div>
        </div>
        {#else}
        <table class="pf-v6-c-table" style="margin-top: 1rem;">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                {#for item in {entityNames}}
                <tr>
                    <td>{item.name}</td>
                    <td>
                        <a href="/{entityNames}/{item.id}/edit" class="pf-v6-c-button pf-m-small pf-m-secondary">Edit</a>
                        <form method="post" action="/{entityNames}/{item.id}/delete" style="display: inline;">
                            <button type="submit" class="pf-v6-c-button pf-m-small pf-m-danger"
                                onclick="return confirm('Are you sure?')">Delete</button>
                        </form>
                    </td>
                </tr>
                {/for}
            </tbody>
        </table>
        {/if}
    </main>
</body>
</html>
```

### Form Template (`{entityName}Form.html`)
```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>{#if action == 'create'}New{#else}Edit{/if} {EntityName} - Product Manager</title>
    <link rel="stylesheet" href="https://unpkg.com/@patternfly/patternfly@6/patternfly.min.css">
</head>
<body>
    <!-- Navigation same as list template -->

    <main class="pf-v6-c-page__main" style="padding: 2rem;">
        <a href="/{entityNames}" class="pf-v6-c-button pf-m-link" style="margin-bottom: 1rem;">&larr; Back</a>

        <div class="pf-v6-c-card" style="max-width: 600px;">
            <div class="pf-v6-c-card__title">
                <h1>{#if action == 'create'}Add New{#else}Edit{/if} {EntityName}</h1>
            </div>
            <div class="pf-v6-c-card__body">
                <form method="post" action="{#if action == 'create'}/{entityNames}{#else}/{entityNames}/{{entityName}.id}{/if}" class="pf-v6-c-form">
                    <div class="pf-v6-c-form__group">
                        <label class="pf-v6-c-form__label" for="name">
                            <span class="pf-v6-c-form__label-text">Name</span>
                            <span class="pf-v6-c-form__label-required">*</span>
                        </label>
                        <input class="pf-v6-c-form-control" type="text" id="name" name="name"
                            value="{{entityName}.name}" required>
                    </div>

                    <div class="pf-v6-c-form__group pf-m-action" style="margin-top: 1rem;">
                        <button type="submit" class="pf-v6-c-button pf-m-primary">
                            {#if action == 'create'}Create{#else}Update{/if}
                        </button>
                        <a href="/{entityNames}" class="pf-v6-c-button pf-m-link">Cancel</a>
                    </div>
                </form>
            </div>
        </div>
    </main>
</body>
</html>
```

## PatternFly v6 Classes
- `pf-v6-c-button` - Buttons (add `pf-m-primary`, `pf-m-secondary`, `pf-m-danger`)
- `pf-v6-c-table` - Data tables
- `pf-v6-c-form` - Forms
- `pf-v6-c-form-control` - Form inputs
- `pf-v6-c-card` - Card containers
- `pf-v6-c-label` - Status labels
- `pf-v6-c-empty-state` - Empty state messages

## Qute Syntax
- `{variable}` - Output value
- `{#if condition}...{#else}...{/if}` - Conditionals
- `{#for item in list}...{/for}` - Loops
- `{item.property}` - Property access

## Reference Files
- `products/productList.html`
- `products/productForm.html`
- `customers/customerList.html`
