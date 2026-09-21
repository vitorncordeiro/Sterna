package com.domainsugester.domain_finder.batch.pdf;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PdfService {
    private final TemplateEngine templateEngine;

    public byte[] generatePdfFromTemplate(String template, Map<String, Object> model){
        Context context = new Context();
        context.setVariables(model);
        String html = templateEngine.process(template, context);
        return generatePdfFromHtml(html);
    }

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PdfService.class);

    public byte[] generatePdfFromHtml(String html){
        if (html == null) {
            throw new IllegalArgumentException("html must not be null");
        }
        String cleaned = html;
        // remove BOM if present
        if (cleaned.startsWith("\uFEFF")) {
            cleaned = cleaned.substring(1);
        }
        // remove any null chars
        cleaned = cleaned.replace("\u0000", "");
        // remove any characters before first '<'
        cleaned = cleaned.replaceFirst("^[^<]+", "");

        // preview for debugging
        try {
            int previewLen = Math.min(500, cleaned.length());
            log.debug("PDF HTML cleaned length={} preview=[{}]", cleaned.length(), cleaned.substring(0, previewLen));
        } catch (Exception ignored) {}

        // write cleaned HTML to /tmp for inspection
        try {
            java.nio.file.Path p = java.nio.file.Path.of("/tmp", "batch-pdf-" + java.util.UUID.randomUUID() + ".html");
            java.nio.file.Files.writeString(p, cleaned, java.nio.charset.StandardCharsets.UTF_8);
            log.info("Wrote cleaned PDF HTML to {}", p.toString());
        } catch (Exception e){
            log.warn("Could not write cleaned HTML to /tmp: {}", e.getMessage());
        }

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()){
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(cleaned, "");
            builder.toStream(os);
            builder.run();
            return os.toByteArray();
        } catch (Exception e){
            log.error("Failed to render PDF: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}