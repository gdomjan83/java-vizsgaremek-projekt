package invoicekeeper.repositories;

import invoicekeeper.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    @Query("select c from Company c where :searchName is null or c.companyName like concat('%',:searchName ,'%')")
    List<Company> findAllCompanies(Optional<String> searchName);

    Optional<Company> findCompanyByVatNumber(String vatNumber);
}
