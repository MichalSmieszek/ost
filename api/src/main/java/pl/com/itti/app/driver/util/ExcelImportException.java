package pl.com.itti.app.driver.util;

import org.apache.poi.ss.usermodel.Row;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import pl.com.itti.app.driver.dto.ImportExcelTrialPositionDTO;

import java.util.List;

@ResponseStatus(HttpStatus.BAD_REQUEST)

public class ExcelImportException extends RuntimeException {

    public ExcelImportException(String message, ImportExcelTrialPositionDTO importExcelTrialPositionDTO, Exception e) {
        super(message, e);
        logIt(importExcelTrialPositionDTO);
    }

    public ExcelImportException(String message, ImportExcelTrialPositionDTO importExcelTrialPositionDTO) {
        super(message);
        logIt(importExcelTrialPositionDTO);
    }
    public ExcelImportException(String message, Row row, Exception e) {
        super(message, e);
        logIt(row);
    }

    public ExcelImportException(String message, Exception e) {
        super(message, e);
    }

    public ExcelImportException(String message, List<String> errorList) {
        super(message);
        logIt(errorList);
    }

    private <T> void logIt (T element)
    {
        LogService.saveToLogFile(element.toString());
    }
}

