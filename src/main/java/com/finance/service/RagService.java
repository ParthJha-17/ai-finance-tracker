package com.finance.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

@Service
public class RagService {

    private final Map<String, List<RagChunk>> knowledgeBases;

    public RagService() {
        this.knowledgeBases = new HashMap<>();
        loadKnowledgeBase("categorization", "knowledge/categorization.md");
        loadKnowledgeBase("recommendations", "knowledge/recommendations.md");
    }

    public List<String> retrieve(String knowledgeBase, String query, int limit) {
        List<RagChunk> chunks = knowledgeBases.getOrDefault(knowledgeBase, List.of());
        if (chunks.isEmpty()) {
            return List.of();
        }
        Set<String> queryTokens = tokenize(query);
        if (queryTokens.isEmpty()) {
            return chunks.stream()
                    .limit(limit)
                    .map(RagChunk::formatted)
                    .collect(Collectors.toList());
        }

        return chunks.stream()
                .sorted(Comparator.comparingInt(chunk -> score(chunk, queryTokens)).reversed())
                .limit(limit)
                .map(RagChunk::formatted)
                .collect(Collectors.toList());
    }

    private int score(RagChunk chunk, Set<String> queryTokens) {
        int score = 0;
        for (String token : queryTokens) {
            if (chunk.tokens.contains(token)) {
                score++;
            }
        }
        return score;
    }

    private void loadKnowledgeBase(String key, String path) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            String content = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
            knowledgeBases.put(key, parseChunks(content));
        } catch (IOException ex) {
            knowledgeBases.put(key, Collections.emptyList());
        }
    }

    private List<RagChunk> parseChunks(String content) {
        String[] sections = content.split("(?m)^## ");
        List<RagChunk> chunks = new ArrayList<>();
        for (String section : sections) {
            String trimmed = section.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                continue;
            }
            String[] lines = trimmed.split("\n", 2);
            String title = lines[0].trim();
            String body = lines.length > 1 ? lines[1].trim() : "";
            chunks.add(new RagChunk(title, body));
        }
        return chunks;
    }

    private Set<String> tokenize(String text) {
        if (text == null || text.isBlank()) {
            return Set.of();
        }
        String normalized = text.toLowerCase(Locale.ROOT);
        String[] tokens = normalized.split("[^a-z0-9]+");
        Set<String> results = new HashSet<>();
        for (String token : tokens) {
            if (token.length() > 2) {
                results.add(token);
            }
        }
        return results;
    }

    private static class RagChunk {
        private final String title;
        private final String content;
        private final Set<String> tokens;

        private RagChunk(String title, String content) {
            this.title = title;
            this.content = content;
            this.tokens = new HashSet<>();
            tokens.addAll(tokenizeStatic(title + " " + content));
        }

        private String formatted() {
            return String.format("%s: %s", title, content);
        }

        private static Set<String> tokenizeStatic(String text) {
            if (text == null || text.isBlank()) {
                return Set.of();
            }
            String normalized = text.toLowerCase(Locale.ROOT);
            String[] tokens = normalized.split("[^a-z0-9]+");
            Set<String> results = new HashSet<>();
            for (String token : tokens) {
                if (token.length() > 2) {
                    results.add(token);
                }
            }
            return results;
        }
    }
}
