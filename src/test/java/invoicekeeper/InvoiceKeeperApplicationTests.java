package invoicekeeper;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest
@Sql(scripts = {"/cleartables.sql", "/testdata.sql"})
class InvoiceKeeperApplicationTests {
	@Test
	void contextLoads() {
	}
}
