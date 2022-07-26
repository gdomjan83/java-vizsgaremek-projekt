CREATE TABLE invoices
(
    invoice_id     BIGINT AUTO_INCREMENT NOT NULL,
    invoice_number VARCHAR(255) NOT NULL UNIQUE,
    date_of_issue  date NOT NULL,
    due_date       date NOT NULL,
    payment_status VARCHAR(255) NOT NULL,
    amount_total   INT NOT NULL,
    company_id     BIGINT NOT NULL,
    CONSTRAINT pk_invoices PRIMARY KEY (invoice_id),
    CONSTRAINT FK_INVOICES_ON_COMPANY FOREIGN KEY (company_id) REFERENCES companies (company_id)
);

CREATE TABLE items_on_invoices
(
    invoice_id      BIGINT NOT NULL,
    name_of_item    VARCHAR(255) NOT NULL,
    pieces_of_items INT NOT NULL,
    total_price     INT NOT NULL,
    CONSTRAINT fk_items_on_invoices_on_invoice FOREIGN KEY (invoice_id) REFERENCES invoices (invoice_id)
);