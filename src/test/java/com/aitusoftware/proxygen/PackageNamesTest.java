package com.aitusoftware.proxygen;

import org.junit.Test;

import java.util.Set;

import static com.aitusoftware.proxygen.PackageNames.getTopLevelPackage;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PackageNamesTest
{
    private static final String BASE_PACKAGE = "com.aitusoftware.proxygen.test";

    @Test
    public void shouldDetermineFromSinglePackage()
    {
        assertThat(getTopLevelPackage(Set.of(
                BASE_PACKAGE + ".Ping",
                BASE_PACKAGE + ".Pong"
        )), is(BASE_PACKAGE));
    }

    @Test
    public void shouldDetermineFromMultipleLeafPackages()
    {
        assertThat(getTopLevelPackage(Set.of(
                BASE_PACKAGE + ".one.Ping",
                BASE_PACKAGE + ".two.Pong"
        )), is(BASE_PACKAGE));
    }

    @Test
    public void shouldDetermineFromHierarchyOfPackages()
    {
        assertThat(getTopLevelPackage(Set.of(
                BASE_PACKAGE + ".one.Ping",
                BASE_PACKAGE + ".two.Pong",
                BASE_PACKAGE + ".Base"
        )), is(BASE_PACKAGE));
    }
}