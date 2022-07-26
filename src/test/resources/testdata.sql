insert into companies (name_of_company, vat_number, bank_account_number)
values("Best Byte", "12345678-1-45", "12345876-86496452-11111111"),
      ("Euronics", "84512648-1-45", "84245689-12358698-22222222"),
      ("Euro Family", "98765432-2-55", "48695842-45236874-88888888"),
      ("Pannon Egyetem", "19265322-1-42", "17246879-30001000-00000000");

insert into invoices (invoice_number, date_of_issue, due_date, payment_status, amount_total, company_id)
values("123456AB", "2020-12-22", "2020-12-28", "PAYED", 1200, 1),
      ("84568BB", "2021-01-12", "2021-02-01", "UNPAYED", 10000, 2),
      ("995468RS", "2022-05-09", "2022-08-19", "PAYED", 6000, 2),
      ("XG45-12", "2022-06-10", "2022-06-20", "UNPAYED", 200, 3),
      ("45996EE", "2022-06-15", "2022-09-30", "UNPAYED", 5400, 1);

insert into items_on_invoices(invoice_id, name_of_item, pieces_of_items, total_price)
values(1, "RAM", 5, 1000),
      (1, "processzor", 1, 200),
      (2, "okostelefon", 1, 5000),
      (3, "4K TV", 1, 5000),
      (3, "Playstation 5", 2, 1000),
      (4, "termosz", 1, 200),
      (5, "RAM", 2, 2000),
      (5, "GPU", 2, 2400),
      (5, "alaplap", 1, 1000);
