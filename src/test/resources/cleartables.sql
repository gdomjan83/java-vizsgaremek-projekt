delete from items_on_invoices;
delete from invoices;
delete from companies;

alter table companies auto_increment=1;
alter table invoices auto_increment=1;
alter table items_on_invoices auto_increment=1;