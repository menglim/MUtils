import com.github.menglim.mutils.AppUtils;

import java.util.Date;

public class CSVFileTest {
    public static void main(String[] args) {

//        Test test = new Test(1L, "MENGLIM", new Date());
//
//        String str = AppUtils.getInstance().toCSVText(test);
//        System.out.println("=> " + str);

        System.out.println("=> " + AppUtils.getInstance().toVariableFormat("This#string%contains^special*characters&."));
        System.out.println("=> " + AppUtils.getInstance().toVariableFormat("Payment Group"));

    }


}
