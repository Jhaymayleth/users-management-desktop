CREATE TABLE IF NOT EXISTS products (
    id UUID PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    brand VARCHAR(100),
    description VARCHAR(500) NOT NULL,
    purchase_price NUMERIC(12, 2) NOT NULL CHECK (purchase_price > 0),
    sale_price NUMERIC(12, 2) NOT NULL CHECK (sale_price >= purchase_price),
    stock INTEGER NOT NULL CHECK (stock >= 0),
    minimum_stock INTEGER NOT NULL CHECK (minimum_stock >= 0),
    category_id UUID NOT NULL,
    image_url VARCHAR(500),
    status VARCHAR(20) NOT NULL CHECK (
        status IN ('AVAILABLE', 'LOW_STOCK', 'OUT_OF_STOCK', 'DISCONTINUED')
    ),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CHECK (
        status = 'DISCONTINUED'
        OR (stock = 0 AND status = 'OUT_OF_STOCK')
        OR (stock > 0 AND stock <= minimum_stock AND status = 'LOW_STOCK')
        OR (stock > minimum_stock AND status = 'AVAILABLE')
    )
);

CREATE INDEX IF NOT EXISTS idx_products_name ON products (name);
CREATE INDEX IF NOT EXISTS idx_products_category_status ON products (category_id, status);
CREATE INDEX IF NOT EXISTS idx_products_low_stock ON products (stock, minimum_stock);

CREATE TABLE IF NOT EXISTS product_suppliers (
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    supplier_id UUID NOT NULL,
    PRIMARY KEY (product_id, supplier_id)
);
