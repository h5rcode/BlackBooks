package com.blackbooks.services;

import com.blackbooks.model.nonpersistent.BookExport;

import java.util.List;

public interface ExportService {
    List<BookExport> getBookExportList(Integer limit);

    String previewBookExport(char textQualifier, char columnSeparator, boolean firstRowContainsHeaders);
}
