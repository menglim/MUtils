import com.github.menglim.mutils.annotation.CSVField;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public  class Test {
    @CSVField(order = 2, ignore = true)
    private Long testId;

    @CSVField(order = 4)
    private String testName;

    @CSVField(order = 1, formatDate = "dd-MMM-yyyy")
    private Date testDate;
}
