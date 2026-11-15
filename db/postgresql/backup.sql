--
-- PostgreSQL database dump
--

-- Dumped from database version 15.3
-- Dumped by pg_dump version 15.3

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: product_suppliers; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.product_suppliers (
    product_id uuid NOT NULL,
    supplier_id uuid NOT NULL
);


--
-- Name: products; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.products (
    id uuid NOT NULL,
    code character varying(20) NOT NULL,
    name character varying(150) NOT NULL,
    brand character varying(100),
    description character varying(500) NOT NULL,
    purchase_price numeric(12,2) NOT NULL,
    sale_price numeric(12,2) NOT NULL,
    stock integer NOT NULL,
    minimum_stock integer NOT NULL,
    category_id uuid NOT NULL,
    image_url character varying(500),
    status character varying(20) NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT products_check CHECK ((sale_price >= purchase_price)),
    CONSTRAINT products_check1 CHECK ((((status)::text = 'DISCONTINUED'::text) OR ((stock = 0) AND ((status)::text = 'OUT_OF_STOCK'::text)) OR ((stock > 0) AND (stock <= minimum_stock) AND ((status)::text = 'LOW_STOCK'::text)) OR ((stock > minimum_stock) AND ((status)::text = 'AVAILABLE'::text)))),
    CONSTRAINT products_minimum_stock_check CHECK ((minimum_stock >= 0)),
    CONSTRAINT products_purchase_price_check CHECK ((purchase_price > (0)::numeric)),
    CONSTRAINT products_status_check CHECK (((status)::text = ANY ((ARRAY['AVAILABLE'::character varying, 'LOW_STOCK'::character varying, 'OUT_OF_STOCK'::character varying, 'DISCONTINUED'::character varying])::text[]))),
    CONSTRAINT products_stock_check CHECK ((stock >= 0))
);


--
-- Data for Name: product_suppliers; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.product_suppliers (product_id, supplier_id) FROM stdin;
10000000-0000-0000-0000-000000000001	30000000-0000-0000-0000-000000000001
10000000-0000-0000-0000-000000000002	30000000-0000-0000-0000-000000000001
10000000-0000-0000-0000-000000000002	30000000-0000-0000-0000-000000000002
\.


--
-- Data for Name: products; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.products (id, code, name, brand, description, purchase_price, sale_price, stock, minimum_stock, category_id, image_url, status, created_at, updated_at) FROM stdin;
10000000-0000-0000-0000-000000000001	SHM-001	Shampoo automotriz	Brilla	Shampoo concentrado para lavado exterior	15000.00	20250.00	30	5	20000000-0000-0000-0000-000000000001	\N	AVAILABLE	2026-11-14 19:57:05.819552	2026-11-14 19:57:05.819552
10000000-0000-0000-0000-000000000002	CERA-01	Cera protectora	Brilla	Cera protectora para pintura automotriz	9000.00	12000.00	3	5	20000000-0000-0000-0000-000000000001	\N	LOW_STOCK	2026-11-14 19:57:05.819552	2026-11-14 19:57:05.819552
10000000-0000-0000-0000-000000000003	PANO-01	Paño de microfibra	\N	Paño suave para secado de vehiculos	4000.00	5500.00	0	4	20000000-0000-0000-0000-000000000002	\N	OUT_OF_STOCK	2026-11-14 19:57:05.819552	2026-11-14 19:57:05.819552
\.


--
-- Name: product_suppliers product_suppliers_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.product_suppliers
    ADD CONSTRAINT product_suppliers_pkey PRIMARY KEY (product_id, supplier_id);


--
-- Name: products products_code_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.products
    ADD CONSTRAINT products_code_key UNIQUE (code);


--
-- Name: products products_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.products
    ADD CONSTRAINT products_pkey PRIMARY KEY (id);


--
-- Name: idx_products_category_status; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_products_category_status ON public.products USING btree (category_id, status);


--
-- Name: idx_products_low_stock; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_products_low_stock ON public.products USING btree (stock, minimum_stock);


--
-- Name: idx_products_name; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_products_name ON public.products USING btree (name);


--
-- Name: product_suppliers product_suppliers_product_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.product_suppliers
    ADD CONSTRAINT product_suppliers_product_id_fkey FOREIGN KEY (product_id) REFERENCES public.products(id) ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

