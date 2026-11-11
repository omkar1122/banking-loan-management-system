package com.bank.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.Set;

public final class PageUtil {
    private PageUtil() {}

    /** Builds a Pageable, whitelisting the sort column so users cannot sort by arbitrary properties. */
    public static Pageable of(int page, int size, String sort, String dir, Set<String> allowed, String defaultSort) {
        String field = allowed.contains(sort) ? sort : defaultSort;
        Sort s = "desc".equalsIgnoreCase(dir) ? Sort.by(field).descending() : Sort.by(field).ascending();
        return PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 50), s);
    }

    public static String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
