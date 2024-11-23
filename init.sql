-- Create `cards` table
CREATE TABLE public.cards (
    card_id VARCHAR(50) PRIMARY KEY,
    card_number VARCHAR(16) NOT NULL,
    expiry_date DATE NOT NULL,
    cvv VARCHAR(4) NOT NULL
);

ALTER TABLE IF EXISTS public.cards
    OWNER TO postgres;

-- Insert data into `cards`
INSERT INTO public.cards (card_id, card_number, expiry_date, cvv) VALUES
    ('241b557e-23a8-4f8e-a08e-310e4ca6514f', '1122334411223344', '2024-09-15', '789'),
    ('51ead5cc-a4b5-4d8d-9098-f91368446808', '8765432187654321', '2026-06-30', '456'),
    ('c375277a-5708-419b-9170-70898a5fd0e3', '1234567812345678', '2025-12-31', '123');

-- Create `payments` table
CREATE TABLE public.payments (
    payment_id VARCHAR(50) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    order_details TEXT NOT NULL,
    amount NUMERIC(10, 2) NOT NULL,
    payment_method VARCHAR(100) NOT NULL
);

ALTER TABLE IF EXISTS public.payments
    OWNER TO postgres;

-- Insert data into `payments`
INSERT INTO public.payments (payment_id, title, order_details, amount, payment_method) VALUES
    ('5e7927d1-097c-4639-81e1-cfacacbdf983', 'Оплата заказа #3', 'Детали заказа: 2 товара', 800.00, 'Карта'),
    ('b797be10-01ad-4a66-9fe2-94a606f59458', 'Оплата заказа #1', 'Детали заказа: 3 товара', 1200.50, 'Карта'),
    ('fba4764b-e1c8-4709-9515-11b39105730a', 'Оплата заказа #2', 'Детали заказа: 5 товаров', 3400.75, 'Наличные');
