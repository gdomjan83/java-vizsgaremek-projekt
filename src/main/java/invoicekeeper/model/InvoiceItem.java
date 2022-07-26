package invoicekeeper.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class InvoiceItem {
    @Column(name = "name_of_item")
    private String name;

    @Column(name = "pieces_of_items")
    private int pieces;

    @Column(name = "total_price")
    private int priceTotal;
}
