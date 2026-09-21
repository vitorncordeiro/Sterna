package com.domainsugester.domain_finder.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NameVariationGeneratorService {
    private ChatClient chatClient;

    public NameVariationGeneratorService(ChatClient.Builder builder){
        this.chatClient = builder.build();
    }

    public List<String> generateNameVariations(String name) {
        String prompt = getPrompt(name);
        String response = chatClient.prompt(prompt).call().content();
        return parseVariations(response);
    }

    private List<String> parseVariations(String response) {
        System.out.println("\n\n\n\n\n\n" + response + "\n\n\n\n\n\n\n\n");
        List<String> variations = new ArrayList<>();
        if (response == null || response.isBlank()) {
            return variations;
        }

        int startIndex = response.indexOf('[');
        int endIndex = response.lastIndexOf(']');

        String content = (startIndex != -1 && endIndex != -1 && endIndex > startIndex)
                ? response.substring(startIndex + 1, endIndex)
                : response;

        Pattern pattern = Pattern.compile("\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            String candidate = matcher.group(1).trim().toLowerCase();
            if (!candidate.isEmpty() && !candidate.equalsIgnoreCase("variations") && !variations.contains(candidate)) {
                variations.add(candidate);
            }
        }

        return variations;
    }
    public String getPrompt(String originalDomain){
        return """
                You are a domain name variation generator specialized in creating CLOSE variations of existing brand names.
                
                The user provided a domain name that is unavailable.
                
                Your primary objective is to preserve the ORIGINAL NAME as much as possible while creating small, creative mutations that could potentially be registered.
                
                Original domain: """ +
                originalDomain
                + """
                IMPORTANT:
                The original name is the foundation of every variation.
                
                The generated names MUST remain visually and phonetically recognizable as variations of the original name.
                
                Do NOT invent completely new names.
                
                Do NOT replace the original concept with a different word.
                
                Do NOT create semantic alternatives or unrelated brand names.
                
                For example, if the original name is:
                
                sterna.ai
                
                Good variations include:
                
                steerna.ai
                steerna.com
                sternna.ai
                sternna.com
                stterna.ai
                xterna.com
                sternaa.com
                sternao.com
                
                Bad variations include:
                
                sterra.ai
                sternova.ai
                sternix.ai
                skywing.ai
                
                The bad examples are too different from the original name.
                
                Use the following mutation strategies:
                
                1. LETTER DUPLICATION
                
                Duplicate a letter while keeping the word visually recognizable.
                
                Examples:
                sterna -> sternna
                sterna -> steerna
                sterna -> sternaa
                
                2. LETTER REMOVAL
                
                Remove one letter while keeping the name recognizable.
                
                Examples:
                sterna -> sterna
                sterna -> stera
                sterna -> strna
                
                Only remove letters when the result still looks like the original name.
                
                3. LETTER SUBSTITUTION
                
                Replace one letter with a visually or phonetically similar letter.
                
                Examples:
                sterna -> sterna
                sterna -> sterne
                sterna -> sterno
                sterna -> xterna
                
                Do not perform multiple unrelated substitutions.
                
                4. LETTER INSERTION
                
                Insert one letter into the original name.
                
                Examples:
                sterna -> steerna
                sterna -> sternna
                sterna -> sterina
                sterna -> sterena
                
                5. PHONETIC VARIATION
                
                Make a very small spelling change that preserves approximately the same pronunciation.
                
                Examples:
                wizard -> weezard
                gecko -> gekko
                
                The result must still strongly resemble the original name.
                
                6. VOWEL VARIATION
                
                Experiment with nearby vowel sounds or duplicated vowels.
                
                Examples:
                sterna -> steerna
                sterna -> stirna
                sterna -> sterne
                sterna -> sterni
                
                Only use variations that remain visually close to the original.
                
                7. CONSONANT VARIATION
                
                Experiment with visually or phonetically similar consonants.
                
                Examples:
                sterna -> xterna
                sterna -> sterna
                sterna -> sternah
                
                Keep the mutation small.
                
                8. TYPOGRAPHIC / VISUAL VARIATION
                
                Create names that look similar when written.
                
                Examples:
                sterna -> sternna
                sterna -> steerna
                sterna -> stterna
                
                9. DOMAIN HACK
                
                Use the TLD creatively only when it preserves the original identity.
                
                Example:
                analisa.ai -> analis.ai
                
                The TLD should never be used as an excuse to create an unrelated name.
                
                10. TLD VARIATION
                
                Use TLDs such as:
                .com
                .ai
                .io
                .co
                .app
                .dev
                .tech
                .net
              
                
                However, TLD variation alone should NOT dominate the output.
                
                For example, do not generate:
                
                sterna.com
                sterna.ai
                sterna.io
                sterna.co
                sterna.app
                sterna.dev
                sterna.tech
                sterna.net
                
                as the majority of the results.
                
                Instead, first create a diverse set of mutated versions of the original name and then combine those names with appropriate TLDs.
                
                STRICT SIMILARITY RULE:
                
                Every variation must have a SMALL EDIT DISTANCE from the original name.
                
                Prefer mutations involving approximately 1-2 character changes.
                
                Avoid changing the structure of the word.
                
                Avoid adding unrelated words.
                
                Avoid prefixes such as:
                get
                try
                use
                my
                go
                hey
                join
                
                Avoid suffixes such as:
                hub
                lab
                hq
                app
                world
                online
                
                unless they create an exceptionally natural variation.
                
                The original name should remain immediately recognizable.
                
                BRAND IDENTITY RULE:
                
                If a person sees the original name and the generated variation side by side, they should immediately understand that the two names are related.
                
                The goal is:
                
                ORIGINAL NAME
                ↓
                small mutation
                ↓
                recognizable new brand/domain
                
                NOT:
                
                ORIGINAL NAME
                ↓
                new concept
                ↓
                completely different brand
                
                DIVERSITY RULE:
                
                Generate exactly 15 unique domains.
                
                The 15 domains should use different small mutation techniques.
                
                Do not generate the same base name repeatedly with different TLDs.
                
                Do not generate large numbers of "get{name}" or "{name}hub" domains.
                
                Do not repeat the same mutation.
                
                Each variation must be meaningfully different from the others while remaining close to the original.
                
                QUALITY FILTER:
                
                Before returning a variation, ask:
                
                1. Does it still strongly resemble the original name?
                2. Is it only a small mutation?
                3. Is it pronounceable?
                4. Does it look like a plausible brand?
                5. Would a human understand the connection to the original?
                6. Is it substantially different from the other generated variations?
                
                If the answer to questions 1 or 2 is NO, discard the variation.
                
                OUTPUT RULES:
                
                * Generate exactly 15 domains.
                * Every domain must contain a TLD.
                * Use lowercase.
                * No explanations.
                * No markdown.
                * No availability information.
                * No duplicate domains.
                * No duplicate base names unless the TLD creates a meaningful difference.
                * Return ONLY valid JSON.
                * The JSON must contain exactly one property called "variations".
                * "variations" must contain exactly 15 strings.
                
                Required format:
                
                {
                "variations": [
                "steerna.ai",
                "steerna.com",
                "sternna.ai",
                "sternna.com",
                "xterna.com"
                ]
                }
                
                """;
    }

}
