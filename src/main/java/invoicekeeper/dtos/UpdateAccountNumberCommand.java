package invoicekeeper.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAccountNumberCommand {

    @NotBlank(message = "Bank account number can not be empty.")
    @Schema(description = "The new bank account number.", example = "11112222-00000000-99999999")
    @Pattern(regexp = "^[0-9]{8}-[0-9]{8}-[0-9]{8}$", message = "Bank account number is in incorrect format.")
    private String bankAccountNumber;
}
