package com.blackbooks.services;

import com.blackbooks.model.nonpersistent.Summary;

public interface SummaryService {
    Summary getSummary();

    int getFirstLetterCount();
}
