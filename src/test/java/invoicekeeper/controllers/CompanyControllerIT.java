package invoicekeeper.controllers;

import invoicekeeper.dtos.AddNewCompanyCommand;
import invoicekeeper.dtos.AddNewInvoiceCommand;
import invoicekeeper.dtos.CompanyDto;
import invoicekeeper.dtos.UpdateAccountNumberCommand;
import invoicekeeper.model.InvoiceItem;
import invoicekeeper.model.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.zalando.problem.violations.ConstraintViolationProblem;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"/cleartables.sql", "/testdata.sql"})
class CompanyControllerIT {

    @Autowired
    WebTestClient webTestClient;

    AddNewCompanyCommand addCompanyCommand;
    AddNewInvoiceCommand addInvoiceCommand;

    @BeforeEach
    void init() {
        addCompanyCommand = new AddNewCompanyCommand("Penny", "12345678-2-44", "11111111-22222222-33333333");
        addInvoiceCommand = new AddNewInvoiceCommand("123456", LocalDate.parse("2022-06-17"), LocalDate.parse("2022-06-25"),
                PaymentStatus.UNPAYED, List.of(new InvoiceItem("gyufa", 1, 500)), 500);
    }

    @Test
    @DisplayName("Test: add new company to database.")
    void testAddNewCompany() {
        webTestClient.post()
                .uri("/api/companies")
                .bodyValue(addCompanyCommand)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CompanyDto.class)
                .value(c -> assertEquals("Penny", c.getCompanyName()));
    }

    @Test
    @DisplayName("Test: add invoice to company.")
    void testAddInvoiceToCompany() {
        CompanyDto result = webTestClient.post()
                .uri("/api/companies")
                .bodyValue(addCompanyCommand)
                .exchange()
                .expectBody(CompanyDto.class)
                .returnResult().getResponseBody();

        assertThat(result.getInvoices()).hasSize(0);

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/api/companies/{id}").build(result.getId()))
                .bodyValue(addInvoiceCommand)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CompanyDto.class)
                .value(c -> assertThat(c.getInvoices()).hasSize(1));
    }

    @Test
    @DisplayName("Test: find company by its ID.")
    void testFindCompanyById() {
        CompanyDto result = webTestClient.post()
                .uri("/api/companies")
                .bodyValue(addCompanyCommand)
                .exchange()
                .expectBody(CompanyDto.class)
                .returnResult().getResponseBody();

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/companies/{id}").build(result.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(CompanyDto.class)
                .value(c -> assertEquals("12345678-2-44", c.getVatNumber()));
    }

    @Test
    @DisplayName("Test: find company by its VAT number.")
    void testFindCompanyByVatNumber() {
        CompanyDto result = webTestClient.post()
                .uri("/api/companies")
                .bodyValue(addCompanyCommand)
                .exchange()
                .expectBody(CompanyDto.class)
                .returnResult().getResponseBody();

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/companies/vat-number/{vat}").build(result.getVatNumber()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(CompanyDto.class)
                .value(c -> assertEquals("Penny", c.getCompanyName()));
    }

    @Test
    @DisplayName("Test: find all companies without filters.")
    void testFindAllCompanies() {
        webTestClient.get()
                .uri("/api/companies")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CompanyDto.class)
                .hasSize(4)
                .value(l -> assertThat(l).extracting(CompanyDto::getCompanyName).containsOnly("Best Byte", "Euronics", "Euro Family", "Pannon Egyetem"));
    }


    @Test
    @DisplayName("Test: find all companies filtering for name.")
    void testFindAllCompaniesWithFilter() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("api/companies/").queryParam("searchName", "uro").build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CompanyDto.class)
                .hasSize(2)
                .value(l -> assertThat(l).extracting(CompanyDto::getCompanyName).containsOnly("Euronics", "Euro Family"));
    }

    @Test
    @DisplayName("Test: change bank account number of company.")
    void testChangeBankAccountNumber() {
        CompanyDto result = webTestClient.post()
                .uri("/api/companies")
                .bodyValue(addCompanyCommand)
                .exchange()
                .expectBody(CompanyDto.class)
                .returnResult().getResponseBody();

        assertEquals("11111111-22222222-33333333", result.getBankAccountNumber());

        webTestClient.put()
                .uri(uriBuilder -> uriBuilder.path("/api/companies/{id}").build(result.getId()))
                .bodyValue(new UpdateAccountNumberCommand("44444444-55555555-88888888"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(CompanyDto.class)
                .value(c -> assertEquals("44444444-55555555-88888888", c.getBankAccountNumber()));
    }

    @Test
    @DisplayName("Test: delete company with given ID.")
    void testDeleteCompanyById() {
        CompanyDto result = webTestClient.post()
                .uri("/api/companies")
                .bodyValue(addCompanyCommand)
                .exchange()
                .expectBody(CompanyDto.class)
                .returnResult().getResponseBody();

        webTestClient.get()
                .uri("/api/companies")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CompanyDto.class)
                .hasSize(5)
                .value(l -> assertThat(l).extracting(CompanyDto::getCompanyName).containsOnly("Best Byte", "Euronics", "Euro Family", "Penny", "Pannon Egyetem"));

        webTestClient.delete()
                        .uri(uriBuilder -> uriBuilder.path("/api/companies/{id}").build(result.getId()))
                        .exchange()
                        .expectStatus().isNoContent();

        webTestClient.get()
                .uri("/api/companies")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CompanyDto.class)
                .hasSize(4)
                .value(l -> assertThat(l).extracting(CompanyDto::getCompanyName).containsOnly("Best Byte", "Euro Family", "Euronics", "Pannon Egyetem"));
    }

    @Test
    @DisplayName("Test: validate with wrong VAT format.")
    void testWrongVatFormatValidator() {
        addCompanyCommand.setVatNumber("111-2-45");
        webTestClient.post()
                .uri("/api/companies")
                .bodyValue(addCompanyCommand)
                .exchange()
                .expectBody(ConstraintViolationProblem.class)
                .value(p -> assertEquals("VAT number is in incorrect format.", p.getViolations().get(0).getMessage()));
    }

    @Test
    @DisplayName("Test: validate with empty name.")
    void testWrongNameValidator() {
        addCompanyCommand.setCompanyName("  ");
        webTestClient.post()
                .uri("/api/companies")
                .bodyValue(addCompanyCommand)
                .exchange()
                .expectBody(ConstraintViolationProblem.class)
                .value(p -> assertEquals("Company name can not be empty.", p.getViolations().get(0).getMessage()));
    }

    @Test
    @DisplayName("Test: validate with wrong bank account number.")
    void testWrongBankAccountNumberValidator() {
        addCompanyCommand.setBankAccountNumber("11115-21545-666");
        webTestClient.post()
                .uri("/api/companies")
                .bodyValue(addCompanyCommand)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ConstraintViolationProblem.class)
                .value(p -> assertEquals("Bank account number is in incorrect format.", p.getViolations().get(0).getMessage()));
    }
}