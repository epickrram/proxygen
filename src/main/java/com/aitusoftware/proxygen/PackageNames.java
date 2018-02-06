package com.aitusoftware.proxygen;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

final class PackageNames
{
    private PackageNames() {}

    static String getTopLevelPackage(final Set<String> classNames)
    {
        int ptr = 0;
        final List<String[]> tokenisedPackages = classNames.stream().
                map(cn -> cn.split("\\.")).collect(Collectors.toList());
        final List<String> commonPackages = new ArrayList<>();

        boolean processing = true;
        while (processing)
        {
            boolean allMatch = true;
            final String firstPackageToken = tokenisedPackages.get(0)[ptr];
            for (String[] tokenisedPackage : tokenisedPackages)
            {
                if (ptr == tokenisedPackage.length || !tokenisedPackage[ptr].equals(firstPackageToken))
                {
                    allMatch = false;
                    processing = false;
                }
            }
            if (allMatch)
            {
                commonPackages.add(firstPackageToken);
            }
            ptr++;
        }
        final StringJoiner joiner = new StringJoiner(".");
        commonPackages.forEach(joiner::add);
        return joiner.toString();
    }
}
