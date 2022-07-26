package invoicekeeper.exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class IncorrectPaymentException extends AbstractThrowableProblem {
    public IncorrectPaymentException(int amount) {
        super(URI.create("invoices/incorrect-amount"),
                "Incorrect amount.", Status.CONFLICT, String.format("You can not pay more than the invoice amount: %d.", amount));
    }
    public IncorrectPaymentException(String bankAccountNumber) {
        super(URI.create("invoices/incorrect-account-number"),
                "Incorrect bank account number.", Status.CONFLICT,
                String.format("The bank account provided (%s) is not matching the bank account number of the company.", bankAccountNumber));
    }

}
