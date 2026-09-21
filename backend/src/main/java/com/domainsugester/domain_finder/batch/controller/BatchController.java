package com.domainsugester.domain_finder.batch.controller;

import com.domainsugester.domain_finder.auth.security.AuthenticatedUser;
import com.domainsugester.domain_finder.batch.dto.request.BatchTextRequest;
import com.domainsugester.domain_finder.batch.service.BatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/batch")
@RequiredArgsConstructor
public class BatchController {
    private final BatchService batchService;

    @PostMapping(value="/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> startBatchProcess(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal AuthenticatedUser principal) throws IOException {
        batchService.processTextFileRequest(file, principal.getUser().getEmail());
        return ResponseEntity.ok("Batch process started successfully.");
    }
    @PostMapping("/text")
    public ResponseEntity startBatchProcessByText(
            @RequestBody BatchTextRequest domains,
            @AuthenticationPrincipal AuthenticatedUser principal) {

        batchService.processTextRequest(domains, principal.getUser().getEmail());
        return ResponseEntity.ok("Batch process started successfully.");
    }
}
