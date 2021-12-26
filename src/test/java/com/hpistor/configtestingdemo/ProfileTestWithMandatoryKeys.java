package com.hpistor.configtestingdemo;

import com.hpistor.configtestingdemo.helpers.EnvironmentLoader;
import com.hpistor.configtestingdemo.helpers.ProfileTestingHelpers;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;

public class ProfileTestWithMandatoryKeys {
    @DisplayName("Validate the properties for each environment")
    @ParameterizedTest(name = "{2}")
    @MethodSource("provideTestArguments")
    void testProfiles(List<String> profiles, String expectedProfileFilename, String __) throws IOException {
        AbstractEnvironment environment = EnvironmentLoader.loadForProfiles(profiles);
        String resourceFilename = String.format("expected_profiles/%s", expectedProfileFilename);
        Resource resource = new ClassPathResource(resourceFilename);

        Map<String, String> expectedProperties = ProfileTestingHelpers.createdExpectedYmlMapFromFile(resource);
        Executable[] executablesValidatingExpectProfile = ProfileTestingHelpers.convertExpectedMapIntoExecutableArray(expectedProperties, environment);
        Executable[] executablesEnsuringMandatoryKeys = validateMandatoryKeysArePresent(expectedProperties);

        assertAll(executablesEnsuringMandatoryKeys);
        assertAll(executablesValidatingExpectProfile);
    }

    public Executable[] validateMandatoryKeysArePresent(Map<String, String> expectedProperties) {
        return Arrays.stream(Key.values())
                .map(Key::getKey)
                .map(key -> (Executable) () -> Assertions.assertThat(expectedProperties)
                        .withFailMessage("The property %s is not contained in the environment")
                        .containsKey(key))
                .toArray(Executable[]::new);
    }

    enum Key {
        CONSUMER_GROUP_KEY("spring.cloud.stream.bindings.consumer-in-0.group"),
        DESTINATION_KEY("spring.cloud.stream.bindings.consumer-in-0.destination");

        private final String key;

        public String getKey() {
            return key;
        }

        Key(String key) {
            this.key = key;
        }
    }

    // Parameterizing the test
    private static Stream<Arguments> provideTestArguments() {
        return Stream.of(
                Arguments.of(Arrays.asList("dev", "dc1"), "dev-dc1.yml", "DEV DC1 SETTINGS TEST"),
                Arguments.of(Arrays.asList("dev", "dc2"), "dev-dc2.yml", "DEV DC2 SETTINGS TEST"),
                Arguments.of(Arrays.asList("prod", "dc1"), "prod-dc1.yml", "PROD DC1 SETTINGS TEST"),
                Arguments.of(Arrays.asList("prod", "dc2"), "prod-dc2.yml", "PROD DC2 SETTINGS TEST")
        );
    }
}
