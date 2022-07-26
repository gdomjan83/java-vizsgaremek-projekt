package invoicekeeper.controllers;

import invoicekeeper.dtos.CreateNewInvoiceCommand;
import invoicekeeper.dtos.InvoiceDto;
import invoicekeeper.dtos.PayInvoiceCommand;
import invoicekeeper.service.InvoicingService;
import invoicekeeper.validators.Violation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/invoices")
@AllArgsConstructor
@Tag(name = "Operations on invoices")
public class InvoiceController {
    private InvoicingService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponse(responseCode = "201", description = "Invoice created and saved.")
    @Operation(summary = "Save new invoice to database.",
            description = "Saving new invoice to database. If the company which issued the invoice is not the the database, it is also saved.")
    public InvoiceDto saveNewInvoice(@Valid @RequestBody CreateNewInvoiceCommand command) {
        return service.saveNewInvoice(command);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(responseCode = "200", description = "Invoice found.")
    @Operation(summary = "Find an invoice by id.")
    public InvoiceDto getInvoiceById(@Parameter(example = "3") @PathVariable("id") long id) {
        return service.getInvoiceById(id);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(responseCode = "200", description = "Invoice found.")
    @Operation(summary = "Find invoice by parameters.",
            description = "Add paramteres in the URL to filter for: company name, VAT number, invoices issued after date, overdue invoices.")
    public List<InvoiceDto> getAllInvoices(@RequestParam Optional<String> companyName, @RequestParam Optional<String> vatNumber,
                                           @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Optional<LocalDate> issuedAfter,
                                           @RequestParam Optional<String> isOverDue) {
        return service.getAllInvoices(companyName, vatNumber, issuedAfter, isOverDue);
    }

    @GetMapping("/find-item")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(responseCode = "200", description = "Invoice found.")
    @Operation(summary = "Find invoices with given items on it.",
            description = "Add name parameter to URL and find all invoices with the given item.")
    public List<InvoiceDto> getAllInvoicesWithItem(@Parameter(example = "RAM") @RequestParam String itemName) {
        return service.getInvoicesByItemName(itemName);
    }

    @PutMapping("/payment")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(responseCode = "201", description = "Invoice payed.")
    @Operation(summary = "Pay invoice.")
    public InvoiceDto payInvoice(@Valid @RequestBody PayInvoiceCommand command) {
        return service.payInvoice(command);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponse(responseCode = "204", description = "Invoice with ID deleted.")
    @Operation(summary = "Delete an invoice with given id.")
    public boolean deleteInvoiceById(@Parameter(example = "1") @PathVariable("id") long id) {
        return service.deleteInvoiceById(id);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Problem> handleValidationError(MethodArgumentNotValidException exception) {
        List<Violation> violations =
                exception.getBindingResult().getFieldErrors().stream()
                        .map((FieldError fe) -> new Violation(fe.getField(), fe.getDefaultMessage()))
                        .collect(Collectors.toList());
        Problem problem = Problem.builder()
                .withType(URI.create("invoices/validation-error"))
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
