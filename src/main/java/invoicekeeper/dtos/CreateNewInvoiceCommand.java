package invoicekeeper.dtos;

import invoicekeeper.model.InvoiceItem;
import invoicekeeper.model.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CreateNewInvoiceCommand {
    @NotBlank(message = "Invoice number can not be empty.")
    @Schema(description = "Invoice number.", example = "THI9545")
    private String invoiceNumber;

    @NotNull(message = "Issue date can not be empty.")
    @PastOrPresent(message = "Issue date can not be in the future.")
    @Schema(description = "Date of issue.", example = "2022-05-20")
    private LocalDate issueDate;

    @NotNull(message = "Due date can not be empty.")
    @Schema(description = "The latest date when the invoice has to be payed.", example = "2022-08-10")
    private LocalDate dueDate;

    @NotNull(message = "Payment status can not be empty.")
    @Schema(description = "The payment status of the invoice.", example = "UNPAYED")
    private PaymentStatus paymentStatus;

    @NotNull(message = "Item list can not be empty.")
    @Schema(description = "The items purchased on the invoice. Attributes needed: item name, pieces of items, cost of items")
    private List<InvoiceItem> items;

    @Min(message = "The amount can not be 0 or lower.", value = 1)
    @Schema(description = "The cost of the items in total.", example = "12000")
    private int amount;

    @NotBlank(message = "Company name can not be empty.")
    @Schema(description = "The company name on the invoice.", example = "Obsidian")
    private String companyName;

    @NotBlank(message = "VAT number can not be empty.")
    @Pattern(regexp = "^[0-9]{8}-[0-9]{1}-[0-9]{2}$", message = "VAT number is in incorrect format.")
    @Schema(description = "VAT number of company.", example = "55544333-1-10")
    private String vatNumber;

    @NotBlank(message = "Bank account number can not be empty.")
    @Pattern(regexp = "^[0-9]{8}-[0-9]{8}-[0-9]{8}$", message = "Bank account number is in incorrect format.")
    @Schema(description = "Bank account number of company.", example = "11700300-44445555-88887777")
    private String bankAccountNumber;
}
