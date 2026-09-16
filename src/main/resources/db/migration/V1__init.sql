CREATE TABLE properties (
    id UUID PRIMARY KEY,
    address VARCHAR(255) NOT NULL,
    city VARCHAR(120) NOT NULL
);

CREATE TABLE tenants (
    id UUID PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    document_id VARCHAR(60) NOT NULL UNIQUE
);

CREATE TABLE lease_contracts (
    id UUID PRIMARY KEY,
    property_id UUID NOT NULL REFERENCES properties (id),
    tenant_id UUID NOT NULL REFERENCES tenants (id),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    monthly_rent NUMERIC(12, 2) NOT NULL,
    daily_interest_rate NUMERIC(6, 4) NOT NULL,
    status VARCHAR(20) NOT NULL
);

CREATE TABLE payments (
    id UUID PRIMARY KEY,
    contract_id UUID NOT NULL REFERENCES lease_contracts (id),
    due_date DATE NOT NULL,
    paid_date DATE,
    amount NUMERIC(12, 2) NOT NULL,
    status VARCHAR(20) NOT NULL
);

CREATE INDEX idx_lease_contracts_status ON lease_contracts (status);
CREATE INDEX idx_payments_status_due_date ON payments (status, due_date);
