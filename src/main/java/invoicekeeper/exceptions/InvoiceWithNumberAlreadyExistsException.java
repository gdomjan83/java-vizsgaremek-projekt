package invoicekeeper.exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class InvoiceWithNumberAlreadyExistsException extends AbstractThrowableProblem {
    public InvoiceWithNumberAlreadyExistsException(String invoiceNumber) {
        super(URI.create("companies/company-already-exists"),
                "Already exists", Status.BAD_REQUEST, String.format("Invoice with invoice number %s already exists.", invoiceNumber));
    }
}
