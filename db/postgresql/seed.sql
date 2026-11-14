INSERT INTO products (
    id, code, name, brand, description, purchase_price, sale_price, stock,
    minimum_stock, category_id, image_url, status
) VALUES
    (
        '10000000-0000-0000-0000-000000000001',
        'SHM-001',
        'Shampoo automotriz',
        'Brilla',
        'Shampoo concentrado para lavado exterior',
        15000.00,
        20250.00,
        30,
        5,
        '20000000-0000-0000-0000-000000000001',
        NULL,
        'AVAILABLE'
    ),
    (
        '10000000-0000-0000-0000-000000000002',
        'CERA-01',
        'Cera protectora',
        'Brilla',
        'Cera protectora para pintura automotriz',
        9000.00,
        12000.00,
        3,
        5,
        '20000000-0000-0000-0000-000000000001',
        NULL,
        'LOW_STOCK'
    ),
    (
        '10000000-0000-0000-0000-000000000003',
        'PANO-01',
        'Paño de microfibra',
        NULL,
        'Paño suave para secado de vehiculos',
        4000.00,
        5500.00,
        0,
        4,
        '20000000-0000-0000-0000-000000000002',
        NULL,
        'OUT_OF_STOCK'
    )
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_suppliers (product_id, supplier_id) VALUES
    (
        '10000000-0000-0000-0000-000000000001',
        '30000000-0000-0000-0000-000000000001'
    ),
    (
        '10000000-0000-0000-0000-000000000002',
        '30000000-0000-0000-0000-000000000001'
    ),
    (
        '10000000-0000-0000-0000-000000000002',
        '30000000-0000-0000-0000-000000000002'
    )
ON CONFLICT (product_id, supplier_id) DO NOTHING;
