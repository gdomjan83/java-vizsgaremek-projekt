package invoicekeeper.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "companies")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_id")
    private Long id;

    @Column(name = "name_of_company")
    private String companyName;

    @Column(name = "vat_number", unique = true)
    private String vatNumber;

    @Column(name = "bank_account_number")
    private String bankAccountNumber;

    @OneToMany(mappedBy = "company", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @Column(name = "invoices")
    private List<Invoice> invoices = new ArrayList<>();

    public Company(String companyName, String vatNumber, String bankAccountNumber) {
        this.companyName = companyName;
        this.vatNumber = vatNumber;
        this.bankAccountNumber = bankAccountNumber;
    }

    public void addInvoice(Invoice invoice) {
        invoices.add(invoice);
        invoice.setCompany(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Company company = (Company) o;
        return Objects.equals(vatNumber, company.vatNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vatNumber);
    }
}
