package com.linuxdo.cdkdistribution.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for parsing and recognizing CDK codes from text input
 */
@Component
public class CdkParser {

    /**
     * Parse CDK text and split it into individual CDK codes
     *
     * @param cdkText The text containing CDK codes
     * @return List of individual CDK codes
     */
    public List<String> parseCdks(String cdkText) {
        if (StringUtils.isBlank(cdkText)) {
            return Collections.emptyList();
        }

        // Remove invisible characters
        cdkText = cdkText.replaceAll("\\p{C}", " ").trim();

        // Try to split by different delimiters
        List<String> cdks = new ArrayList<>();

        // 1. First split by lines
        String[] lines = cdkText.split("\\r?\\n");

        for (String line : lines) {
            line = line.trim();
            if (StringUtils.isBlank(line)) {
                continue;
            }

            // 2. Check if line contains common delimiters
            if (line.contains(",") || line.contains(";") || line.contains("，") || line.contains("；")) {
                // Split by delimiters
                String[] parts = line.split("[,;，；]");
                for (String part : parts) {
                    part = part.trim();
                    if (StringUtils.isNotBlank(part)) {
                        cdks.add(part);
                    }
                }
            } else {
                // No delimiters, use the whole line as a CDK
                cdks.add(line);
            }
        }

        // 3. Filter and clean
        return cdks.stream()
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .distinct() // Remove duplicates
                .collect(Collectors.toList());
    }
}
