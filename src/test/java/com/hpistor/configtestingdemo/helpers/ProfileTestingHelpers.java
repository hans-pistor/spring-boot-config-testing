package com.hpistor.configtestingdemo.helpers;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.function.Executable;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.io.Resource;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ProfileTestingHelpers {
    public static Executable[] convertExpectedMapIntoExecutableArray(Map<String, String> expectedProperties, AbstractEnvironment environment) {
        return expectedProperties
                .entrySet()
                .stream()
                .map(entry -> (Executable) () -> {
                    String key = entry.getKey();
                    String expected = entry.getValue();
                    String actual = environment.getProperty(key);

                    Assertions.assertThat(actual)
                            .withFailMessage("[%s] Expected: %s\nFound %s", key, expected, actual)
                            .isEqualTo(expected);
                })
                .toArray(Executable[]::new);
    }

    public static Map<String, String> createdExpectedYmlMapFromFile(Resource resource) throws IOException {
        Map<String, Object> ymlData = new Yaml().load(resource.getInputStream());

        return ymlData
                .entrySet()
                .stream()
                .flatMap(ProfileTestingHelpers::flattenedMapStructure)
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> String.valueOf(entry.getValue())));
    }

    public static Stream<Map.Entry<String, Object>> flattenedMapStructure(Map.Entry<String, Object> entry) {
        if (entry == null) return Stream.empty();

        Object value = entry.getValue();

        if (value instanceof Map<?, ?>) {
            Map<?, ?> map = (Map<?, ?>) value;
            return map
                    .entrySet()
                    .stream()
                    .flatMap(nestedEntry ->
                            ProfileTestingHelpers.flattenedMapStructure(new AbstractMap.SimpleEntry<>(
                                    String.format("%s.%s", entry.getKey(), nestedEntry.getKey()),
                                    nestedEntry.getValue())));
        }

        if (value instanceof List<?>) {
            List<?> list = (List<?>) value;
            return IntStream.range(0, list.size())
                    .mapToObj(index ->
                            new AbstractMap.SimpleEntry<String, Object>(
                                    String.format("%s[%d]", entry.getKey(), index),
                                    list.get(index)))
                    .flatMap(ProfileTestingHelpers::flattenedMapStructure);
        }

        return Stream.of(entry);
    }
}
