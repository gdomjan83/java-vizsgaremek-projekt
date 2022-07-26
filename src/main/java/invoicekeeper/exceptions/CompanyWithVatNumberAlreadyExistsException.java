package invoicekeeper.exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class CompanyWithVatNumberAlreadyExistsException extends AbstractThrowableProblem {
    public CompanyWithVatNumberAlreadyExistsException(String vatNumber) {
        super(URI.create("companies/company-already-exists"),
                "Already exists.", Status.CONFLICT, String.format("Company with VAT number %s already exists.", vatNumber));
    }
}
