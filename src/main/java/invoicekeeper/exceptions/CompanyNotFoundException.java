package invoicekeeper.exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class CompanyNotFoundException extends AbstractThrowableProblem {
    public CompanyNotFoundException(long id) {
        super(URI.create("companies/company-not-found"),
                "Not found.", Status.NOT_FOUND, String.format("Company not found by id: %d", id));
    }

    public CompanyNotFoundException(String vatNumber) {
        super(URI.create("companies/company-not-found"),
                "Not found.", Status.NOT_FOUND, String.format("Company not found by VAT number: %s", vatNumber));
    }
}
