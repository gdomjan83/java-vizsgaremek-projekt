package invoicekeeper.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDto {
    private Long id;
    private String companyName;
    private String vatNumber;
    private String bankAccountNumber;
    private List<InvoiceDto> invoices = new ArrayList<>();
}
