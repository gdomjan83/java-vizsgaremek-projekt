CREATE TABLE companies
(
    company_id          BIGINT AUTO_INCREMENT NOT NULL,
    name_of_company     VARCHAR(255) NOT NULL,
    vat_number          VARCHAR(255) NOT NULL UNIQUE,
    bank_account_number VARCHAR(255) NOT NULL,
    CONSTRAINT pk_companies PRIMARY KEY (company_id)
);