package invoicekeeper.controllers;

import invoicekeeper.dtos.CreateNewInvoiceCommand;
import invoicekeeper.dtos.InvoiceDto;
import invoicekeeper.dtos.PayInvoiceCommand;
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
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"/cleartables.sql", "/testdata.sql"})
class InvoiceControllerIT {

    @Autowired
    WebTestClient webTestClient;

    CreateNewInvoiceCommand createCommandWithNewCompany;
    CreateNewInvoiceCommand createCommandWithExistingCompany;

    @BeforeEach
    void init() {
        createCommandWithNewCompany = new CreateNewInvoiceCommand("123456", LocalDate.parse("2022-06-17"), LocalDate.parse("2022-06-25"),
                PaymentStatus.UNPAYED, List.of(new InvoiceItem("gyufa", 1, 500)), 500,
                "Penny", "12345678-2-44", "11111111-22222222-33333333");
        createCommandWithExistingCompany = new CreateNewInvoiceCommand("2468", LocalDate.parse("2022-06-17"), LocalDate.parse("2022-06-25"),
                PaymentStatus.UNPAYED, List.of(new InvoiceItem("gyufa", 1, 500)), 500,
                "Euronics", "84512648-1-45", "84245689-12358698-22222222");
    }

    @Test
    @DisplayName("Test: save a new invoice to database with a new company.")
    void testSaveNewInvoiceWithNewCompany() {
        webTestClient.post()
                .uri("/api/invoices")
                .bodyValue(createCommandWithNewCompany)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(InvoiceDto.class)
                .value(i -> assertEquals("123456", i.getInvoiceNumber()));
    }

    @Test
    @DisplayName("Test: save a new invoice to database with an existing company.")
    void testSaveNewInvoiceWithExistingCompany() {
        webTestClient.post()
                .uri("/api/invoices")
                .bodyValue(createCommandWithExistingCompany)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(InvoiceDto.class)
                .value(i -> assertEquals("2468", i.getInvoiceNumber()));
    }

    @Test
    @DisplayName("Test: finding an invoice with given ID.")
    void testGetInvoiceById() {
        InvoiceDto result = webTestClient.post()
                .uri("/api/invoices")
                .bodyValue(createCommandWithNewCompany)
                .exchange()
                .expectBody(InvoiceDto.class)
                .returnResult().getResponseBody();

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("api/invoices/{id}").build(result.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(InvoiceDto.class)
                .value(i -> assertEquals("123456", i.getInvoiceNumber()));
    }

    @Test
    @DisplayName("Test: find all invoices without using any filters.")
    void testGetAllInvoicesWithNoParameters() {
        webTestClient.get()
                .uri("/api/invoices")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(InvoiceDto.class)
                .hasSize(5)
                .value(i -> assertThat(i).extracting(InvoiceDto::getCompanyName).containsOnly("Best Byte", "Euronics", "Euro Family"));
    }

    @Test
    @DisplayName("Test: find all invoices where the company name equals the parameter.")
    void testGetAllInvoicesWithNameParameter() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/invoices/").queryParam("companyName", "uro").build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(InvoiceDto.class)
                .hasSize(3)
                .value(i -> assertThat(i).extracting(InvoiceDto::getInvoiceNumber).containsOnly("995468RS", "84568BB", "XG45-12"));
    }

    @Test
    @DisplayName("Test: find all invoices issued after the given parameter.")
    void testGetAllInvoicesWithIssueDateParameter() {
        webTestClient.get()
                .uri("/api/invoices?issuedAfter=2021-05-08")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(InvoiceDto.class)
                .hasSize(3)
                .value(i -> assertThat(i).extracting(InvoiceDto::getInvoiceNumber).containsOnly("995468RS", "XG45-12", "45996EE"));
    }

    @Test
    @DisplayName("Test: Find all invoices which has the given item on it.")
    void testGetAllInvoicesWithItem() {
        webTestClient.get()
                .uri("/api/invoices/find-item?itemName=RAM")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(InvoiceDto.class)
                .hasSize(2)
                .value(l -> assertThat(l).extracting(InvoiceDto::getInvoiceNumber).containsOnly("123456AB","45996EE"));
    }

    @Test
    @DisplayName("Test: Pay an invoice.")
    void testPayInvoice() {
        webTestClient.post()
                .uri("/api/invoices")
                .bodyValue(createCommandWithNewCompany)
                .exchange()
                .expectBody(InvoiceDto.class)
                .value(i -> assertEquals(PaymentStatus.UNPAYED, i.getPaymentStatus()));

        webTestClient.put()
                .uri("/api/invoices/payment")
                .bodyValue(new PayInvoiceCommand(createCommandWithNewCompany.getInvoiceNumber(), createCommandWithNewCompany.getAmount(), createCommandWithNewCompany.getBankAccountNumber()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(InvoiceDto.class)
                .value(i -> assertEquals(PaymentStatus.PAYED, i.getPaymentStatus()));
    }

    @Test
    @DisplayName("Test: delete an invoice with given ID.")
    void testDeleteInvoiceById() {
        InvoiceDto result = webTestClient.post()
                .uri("/api/invoices")
                .bodyValue(createCommandWithNewCompany)
                .exchange()
                .expectBody(InvoiceDto.class)
                .returnResult().getResponseBody();

        webTestClient.get()
                .uri("/api/invoices")
                .exchange()
                .expectBodyList(InvoiceDto.class)
                .hasSize(6)
                .value(l -> assertThat(l).extracting(InvoiceDto::getInvoiceNumber).containsOnly("123456", "123456AB", "84568BB", "995468RS", "45996EE", "XG45-12"));

        webTestClient.delete()
                .uri(uriBuilder -> uriBuilder.path("/api/invoices/{id}").build(result.getId()))
                .exchange()
                .expectStatus().isNoContent();

        webTestClient.get()
                .uri("/api/invoices")
                .exchange()
                .expectBodyList(InvoiceDto.class)
                .hasSize(5)
                .value(l -> assertThat(l).extracting(InvoiceDto::getInvoiceNumber).containsOnly( "123456AB", "84568BB", "995468RS", "45996EE", "XG45-12"));
    }

    @Test
    @DisplayName("Test: save invoice with future issue date.")
    void testValidationWithWrongIssueDate() {
        createCommandWithNewCompany.setIssueDate(LocalDate.parse("2035-08-30"));
        webTestClient.post()
                .uri("/api/invoices")
                .bodyValue(createCommandWithNewCompany)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ConstraintViolationProblem.class)
                .value(p -> assertEquals("Issue date can not be in the future.", p.getViolations().get(0).getMessage()));
    }

    @Test
    @DisplayName("Test: create invoice with 0 amount.")
    void testValidationWithWrongAmount() {
        createCommandWithNewCompany.setAmount(0);
        webTestClient.post()
                .uri("/api/invoices")
                .bodyValue(createCommandWithNewCompany)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ConstraintViolationProblem.class)
                .value(p -> assertEquals("The amount can not be 0 or lower.", p.getViolations().get(0).getMessage()));
    }
}