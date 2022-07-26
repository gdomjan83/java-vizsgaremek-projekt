package invoicekeeper.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PayInvoiceCommand {
    @NotBlank(message = "Invoice number can not be empty.")
    @Schema(description = "Invoice number.", example = "XG45-12")
    private String invoiceNumber;

    @Min(message = "The amount can not be 0 or lower.", value = 1)
    @Schema(description = "The cost of the items in total.", example = "200")
    private int amount;

    @NotBlank(message = "Bank account number can not be empty.")
    @Pattern(regexp = "^[0-9]{8}-[0-9]{8}-[0-9]{8}$", message = "Bank account number is in incorrect format.")
    @Schema(description = "The bank account number to transfer the money to.", example = "48695842-45236874-88888888")
    private String bankAccountNumber;
}
