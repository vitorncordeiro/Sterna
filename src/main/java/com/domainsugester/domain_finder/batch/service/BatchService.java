package com.domainsugester.domain_finder.batch.service;

import com.domainsugester.domain_finder.batch.cache.BatchCacheService;
import com.domainsugester.domain_finder.batch.dto.batch.BatchResult;
import com.domainsugester.domain_finder.batch.dto.batch.DomainBatchInfo;
import com.domainsugester.domain_finder.batch.dto.request.BatchTextRequest;
import com.domainsugester.domain_finder.batch.dto.validation.LineError;
import com.domainsugester.domain_finder.batch.dto.validation.FileValidationResult;
import com.domainsugester.domain_finder.batch.dto.validation.TextValidationResult;
import com.domainsugester.domain_finder.batch.messaging.events.DomainSubmitedEvent;
import com.domainsugester.domain_finder.batch.messaging.events.FinishedBatchEvent;
import com.domainsugester.domain_finder.batch.messaging.publisher.BatchPublisher;
import com.domainsugester.domain_finder.domain.service.DomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BatchService {
    private final DomainService domainService;
    private final BatchPublisher batchPublisher;
    private final BatchCacheService batchCacheService;
    private final int MAX_LINES = 100;
    private final int MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final Pattern DOMAIN_PATTERN = Pattern.compile(
            "^(?=.{1,253}$)(?!-)[A-Za-z0-9-]{1,63}(?<!-)(\\.[A-Za-z0-9-]{1,63}(?<!-))+$"
    );

    public void processDomain(DomainSubmitedEvent event) throws IOException {
        Boolean result = domainService.getDomain(event.domain()) == null ? Boolean.TRUE : Boolean.FALSE;
        addDomainAvailabilityToBatchResult(event.batchId(), event.domain(), result);
        Integer remaining = batchCacheService.decr(event.batchId());
        if (remaining != null && remaining == 0) {
            BatchResult batchResult = batchCacheService.getBatchResult(event.batchId());
            String recipient = batchCacheService.getRecipient(event.batchId());
            publishFinishedBatchMessage(batchResult, recipient);
        }
    }

    public String processTextRequest(BatchTextRequest domains, String initiatorEmail) {
        TextValidationResult result = validateDomains(domains);
        processDomainBatch(new ArrayList<>(result.validDomains()), initiatorEmail);
        return "ok";
    }

    public String processTextFileRequest(MultipartFile file, String initiatorEmail) throws IOException {
        fileValidation(file);
        FileValidationResult result = validateAndReadDomains(file);
        processDomainBatch(result.validDomains(), initiatorEmail);
        return "ok";
    }

    private void processDomainBatch(List<String> domains, String initiatorEmail){
        DomainBatchInfo batchInfo = DomainBatchInfo.builder()
                .batchId(UUID.randomUUID())
                .batchRemaining(domains.size())
                .batchSize(domains.size())
                .build();

        batchCacheService.saveRemaining(batchInfo.batchId(), batchInfo.batchSize());
        batchCacheService.saveRecipient(batchInfo.batchId(), initiatorEmail);
        // initialize empty BatchResult to avoid NPE in listeners
        batchCacheService.saveBatchResult(batchInfo.batchId(), new BatchResult(new HashMap<>()));
        for (String domain : domains) {
            publishDomainMessage(domain, batchInfo.batchId());
        }
    }


    private void addDomainAvailabilityToBatchResult(UUID batchId, String domain, Boolean isAvailable) {
        BatchResult batchResult = batchCacheService.getBatchResult(batchId);
        if (batchResult == null) {
            batchResult = new BatchResult(new HashMap<>());
        }
        batchResult.domainAvailability().put(domain, isAvailable);
        batchCacheService.saveBatchResult(batchId, batchResult);
    }

    private TextValidationResult validateDomains(BatchTextRequest domains){
        return new TextValidationResult(domains.domains().stream()
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(domain -> DOMAIN_PATTERN.matcher(domain).matches())
                .collect(Collectors.toSet()));
    }

    private FileValidationResult validateAndReadDomains(MultipartFile file) throws IOException {
        List<String> validDomains = new ArrayList<>();
        List<LineError> errors = new ArrayList<>();
        Set<String> checked = new HashSet<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (lineNumber > MAX_LINES) {
                    throw new IllegalArgumentException("Limit of " + MAX_LINES + " lines exceeded.");
                }
                String domain = line.trim().toLowerCase();
                if (domain.isEmpty()) continue;
                if (!DOMAIN_PATTERN.matcher(domain).matches()) {
                    errors.add(new LineError(lineNumber, "Invalid domain: '" + domain + "'"));
                    continue;
                }
                if (!checked.add(domain)) {
                    errors.add(new LineError(lineNumber, "Domain duplicated: '" + domain + "'"));
                    continue;
                }
                validDomains.add(domain);
            }
        }
        return new FileValidationResult(validDomains, errors);
    }

    private void publishDomainMessage(String domain, UUID batchId){
        DomainSubmitedEvent event = new DomainSubmitedEvent(domain, batchId);
        batchPublisher.publish(event);
    }
    private void publishFinishedBatchMessage(BatchResult batchResult, String recipientEmail){
        FinishedBatchEvent event = new FinishedBatchEvent(batchResult.domainAvailability(), recipientEmail);
        batchPublisher.publish(event);
    }

    private void fileValidation(MultipartFile file) {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        if (file.getContentType() == null || !file.getContentType().equals("text/plain")) {
            throw new IllegalArgumentException("File must be a text file");
        }
        if(file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size must be less than 5MB");
        }
    }

}
