package invoicekeeper.dtos;

import invoicekeeper.model.InvoiceItem;
import invoicekeeper.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDto {
    private Long id;
    private String invoiceNumber;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private PaymentStatus paymentStatus;
    private List<InvoiceItem> items = new ArrayList<>();
    private int amount;
    private String companyName;
}
