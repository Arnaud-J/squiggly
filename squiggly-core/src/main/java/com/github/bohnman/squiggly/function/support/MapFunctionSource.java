package com.github.bohnman.squiggly.function.support;

import com.github.bohnman.core.collect.CoreStreams;
import com.github.bohnman.core.lang.CoreObjects;
import com.github.bohnman.core.tuple.CorePair;
import com.github.bohnman.squiggly.function.SquigglyFunction;
import com.github.bohnman.squiggly.function.SquigglyFunctionSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

/**
 * Function repo backed by a map.
 */
@SuppressWarnings("unchecked")
public class MapFunctionSource implements SquigglyFunctionSource {

    private final Map<String, List<SquigglyFunction<Object>>> functionMap;

    public MapFunctionSource(SquigglyFunction<?>... functions) {
        this(Arrays.asList(functions));
    }

    @SuppressWarnings("RedundantCast")
    public <T> MapFunctionSource(Iterable<SquigglyFunction<?>> functions) {
        Map<String, List<SquigglyFunction<Object>>> functionMap = (Map) CoreStreams.of(functions)
                .flatMap(f -> Stream.concat(Stream.of(toPair(f.getName().toLowerCase(), f)), f.getAliases().stream().map(a -> toPair(a.toLowerCase(), f))))
                .collect(groupingBy(CorePair::getLeft, mapping(CorePair::getRight, toList())));

        this.functionMap = Collections.unmodifiableMap(functionMap);
    }

    private CorePair<String, SquigglyFunction<?>> toPair(String name, SquigglyFunction<?> function) {
        return CorePair.of(name, function);
    }

    @Override
    public List<SquigglyFunction<Object>> findByName(String name) {
        return CoreObjects.firstNonNull(functionMap.get(name.toLowerCase()), Collections.emptyList());
    }
}