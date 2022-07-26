package invoicekeeper.repositories;

import invoicekeeper.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    @Query("select i from Invoice i where " +
            "(:companyName is null or i.company.companyName like concat('%', :companyName, '%')) and" +
            "(:vatNumber is null or i.company.vatNumber = :vatNumber) and " +
            "(:issuedAfter is null or i.issueDate > :issuedAfter)")
    List<Invoice> findInvoicesByParameters(Optional<String> companyName, Optional<String> vatNumber,
                                           Optional<LocalDate> issuedAfter);

    @Query("select distinct i from Invoice i left join fetch i.items itm where itm.name like %:name%")
    List<Invoice> findInvoiceByItemName(String name);
}
