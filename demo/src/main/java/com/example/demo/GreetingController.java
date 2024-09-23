package com.example.demo; // 修正包名

import java.util.concurrent.atomic.AtomicLong;
import io.opentelemetry.api.trace.Span; // 添加這行以解決錯誤
import io.opentelemetry.api.trace.SpanContext; // 添加這行以導入 SpanContext

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable; // 添加這行以解決錯誤
import org.slf4j.Logger; // 新增這行
import org.slf4j.LoggerFactory; // 新增這行
import org.springframework.web.bind.annotation.RequestHeader; // 添加這行以解決錯誤

@RestController
public class GreetingController {

	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();

    private final Logger logger = LoggerFactory.getLogger(GreetingController.class);

	@GetMapping("/greeting")
	public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
		return new Greeting(counter.incrementAndGet(), String.format(template, name));
	}

    @GetMapping("/{message}")
    public String entry(@PathVariable String message, @RequestHeader(value = "X-done", required = false) String done) {
        String traceId = getOpenTelemetryTraceId();
		System.out.println("traceId: " + traceId);
		logger.info("entry: " + message);
        return "Hello World: " + message + " traceId: " + traceId; // 確保 message 是 String 類型
    }

	private static String getOpenTelemetryTraceId() {
        Span currentSpan = Span.current();
        if (currentSpan != null) {
            SpanContext spanContext = currentSpan.getSpanContext();
            if (spanContext.isValid()) {
                return spanContext.getTraceId();
            }
        }
        return null;
    }
}