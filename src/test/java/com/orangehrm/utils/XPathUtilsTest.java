package com.orangehrm.utils;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Test(groups = "framework")
public class XPathUtilsTest {

    @Test
    public void shouldUseSingleQuotesByDefault() {
        assertEquals(XPathUtils.literal("John"), "'John'");
    }

    @Test
    public void shouldUseDoubleQuotesWhenValueContainsApostrophe() {
        assertEquals(XPathUtils.literal("O'Neil"), "\"O'Neil\"");
    }

    @Test
    public void shouldUseConcatWhenValueContainsBothQuoteTypes() {
        assertEquals(XPathUtils.literal("O'Neil \"Jr\""), "concat('O', \"'\", 'Neil \"Jr\"')");
    }

    @Test
    public void shouldMatchClassAsWholeToken() {
        assertEquals(XPathUtils.hasClass("oxd-input-group"),
                "contains(concat(' ', normalize-space(@class), ' '), ' oxd-input-group ')");
    }
}
