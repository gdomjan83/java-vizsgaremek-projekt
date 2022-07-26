package invoicekeeper.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@AllArgsConstructor
@Tag(name = "Input data for adding new company.")
public class AddNewCompanyCommand {
    @NotBlank(message = "Company name can not be empty.")
    @Schema(description = "Name of company.", example = "One Bit Studio")
    private String companyName;

    @NotBlank(message = "VAT number can not be empty.")
    @Pattern(regexp = "^[0-9]{8}-[0-9]{1}-[0-9]{2}$", message = "VAT number is in incorrect format.")
    @Schema(description = "VAT number of company.", example = "11223344-5-10")
    private String vatNumber;

    @NotBlank(message = "Bank account number can not be empty.")
    @Pattern(regexp = "^[0-9]{8}-[0-9]{8}-[0-9]{8}$", message = "Bank account number is in incorrect format.")
    @Schema(description = "Bank account number of company.", example = "11111111-22222222-33333333")
    private String bankAccountNumber;
}
