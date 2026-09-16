ALTER TABLE lease_contracts
    ADD COLUMN interest_type VARCHAR(20) NOT NULL DEFAULT 'FIXED';

ALTER TABLE lease_contracts
    ALTER COLUMN interest_type DROP DEFAULT;
