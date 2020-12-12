package org.suorsdey;

import lombok.Builder;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public class ExcelFileUtils<T> {
    private InputStream inputStream;
    private int sheetIndex;
    private int rowStart;
    private int rowEnd;
    private String password;
    private HashMap<String, String> dataMatching;
    private Class<T> clzz;

    public <R> List<R> excelReader() throws Exception {
        ArrayList<HashMap<String, String>> listRow = new ArrayList<>();
        // Validate input from user
        sheetIndex = Math.max(sheetIndex - 1, 0);
        rowStart = Math.max(rowStart - 1, 0);
        rowEnd = Math.max(rowEnd - 1, 0);

        // Creating a Workbook from an Excel file (.xls or .xlsx)
        Workbook workbook = WorkbookFactory.create(inputStream, password);
        workbook.setMissingCellPolicy(Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

        // Getting the Sheet at index zero
        Sheet sheet = workbook.getSheetAt(sheetIndex);

        DataFormatter dataFormatter = new DataFormatter();

        for (int i = rowStart; i <= rowEnd; i++) {
            Row row = sheet.getRow(i);

            if (AppUtils.getInstance().nonNull(row)) {
                HashMap<String, String> mapCell = new HashMap<>();

                dataMatching.forEach((key, value) -> {
                    Cell cell = row.getCell(CellReference.convertColStringToIndex(key), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    mapCell.put(value, dataFormatter.formatCellValue(cell));
                });

                listRow.add(mapCell);
            }
        }

        workbook.close();
        return (List<R>) listRow.stream().map(this::castingObj).collect(Collectors.toList());
    }

    private <R> R castingObj(HashMap<String, String> entry) {
        return (R) AppUtils.getInstance().toObject(entry, clzz);
    }
}