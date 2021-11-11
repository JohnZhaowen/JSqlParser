package net.sf.jsqlparser.parser.feature;

import org.junit.Test;

import static org.junit.Assert.*;

public class FeatureConfigurationTest {

    @Test
    public void testGetValue() {
        FeatureConfiguration configuration = new FeatureConfiguration();
        assertThrows(IllegalStateException.class, () -> configuration.getValue(Feature.alterIndex));

        assertTrue(configuration.getAsBoolean(Feature.allowComplexParsing));
    }

    @Test
    public void testSetValue(){
        FeatureConfiguration configuration = new FeatureConfiguration();
        assertFalse(configuration.getAsBoolean(Feature.allowPostgresSpecificSyntax));

        configuration.setValue(Feature.allowPostgresSpecificSyntax, true);
        assertTrue(configuration.getAsBoolean(Feature.allowPostgresSpecificSyntax));

    }
}
