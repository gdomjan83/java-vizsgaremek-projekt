package invoicekeeper.exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class InvoiceNotFoundException extends AbstractThrowableProblem {
    public InvoiceNotFoundException(long id) {
        super(URI.create("invoices/invoice-not-found"),
                "Not found.", Status.NOT_FOUND, String.format("Invoice not found by id: %d", id));
    }
    public InvoiceNotFoundException(String invoiceNumber) {
        super(URI.create("invoices/invoice-not-found"),
                "Not found.", Status.NOT_FOUND, String.format("Invoice not found by invoice number: %s", invoiceNumber));
    }
}
