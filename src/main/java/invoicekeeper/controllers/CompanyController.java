package invoicekeeper.controllers;

import invoicekeeper.dtos.*;
import invoicekeeper.service.InvoicingService;
import invoicekeeper.validators.Violation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/companies")
@AllArgsConstructor
@Tag(name = "Operations on companies")
public class CompanyController {
    private InvoicingService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponse(responseCode = "201", description = "Company created and saved.")
    @Operation(summary = "Adding new company to database.")
    public CompanyDto addNewCompany(@Valid @RequestBody AddNewCompanyCommand command) {
        return service.addNewCompany(command);
    }

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponse(responseCode = "201", description = "New invoice added to company.")
    @Operation(summary = "Adding new invoice to company.")
    public CompanyDto addInvoiceToCompany(@Parameter(example = "1") @PathVariable("id") long id, @Valid @RequestBody AddNewInvoiceCommand command) {
        return service.addNewInvoiceToCompany(id, command);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(responseCode = "200", description = "Company found.")
    @Operation(summary = "Finding a company by its ID.")
    public CompanyDto findCompanyById(@Parameter(example = "2") @PathVariable("id") long id) {
        return service.getCompanyById(id);
    }

    @GetMapping("/vat-number/{vat}")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(responseCode = "200", description = "Company found.")
    @Operation(summary = "Finding a company by its VAT number.")
    public CompanyDto findCompanyByVatNumber(@Parameter(example = "84512648-1-45") @PathVariable("vat") String vatNumber) {
        return service.getCompanyByVatNumber(vatNumber);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(responseCode = "200", description = "Company found.")
    @Operation(summary = "Search for companies by name using a string of text.")
    public List<CompanyDto> findAllCompanies(@Parameter(example = "Euro") @RequestParam Optional<String> searchName) {
        return service.findAllCompanies(searchName);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(responseCode = "200", description = "Bank account number changed.")
    @Operation(summary = "Update the bank account number of a company.")
    public CompanyDto changeCompanyAccountNumber(@Parameter(example = "1") @PathVariable("id") long id, @Valid @RequestBody UpdateAccountNumberCommand command) {
        return service.updateAccountNumber(id, command);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponse(responseCode = "204", description = "Company with ID deleted.")
    @Operation(summary = "Delete company by ID.")
    public boolean deleteCompanyById(@Parameter(example = "1") @PathVariable("id") long id) {
        return service.deleteCompanyById(id);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Problem> handleValidationError(MethodArgumentNotValidException exception) {
        List<Violation> violations =
                exception.getBindingResult().getFieldErrors().stream()
                        .map((FieldError fe) -> new Violation(fe.getField(), fe.getDefaultMessage()))
                        .collect(Collectors.toList());
        Problem problem = Problem.builder()
                .withType(URI.create("companies/validation-error"))
                .withTitle("Validation error")
                .withStatus(Status.BAD_REQUEST)
                .withDetail(exception.getMessage())
                .with("violations", violations)
                .build();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }
}
