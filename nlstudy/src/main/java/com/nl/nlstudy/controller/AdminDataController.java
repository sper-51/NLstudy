package com.nl.nlstudy.controller;

import com.nl.nlstudy.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "管理端-数据运维")
@RestController
@RequestMapping("/admin/data")
@RequiredArgsConstructor
public class AdminDataController {

    private final JdbcTemplate jdbcTemplate;

    @Operation(summary = "获取数据库存储统计")
    @GetMapping("/storage")
    public Result<Map<String, Object>> storage() {
        String sql = """
            SELECT table_name,
                   table_rows,
                   data_length + index_length AS total_bytes
            FROM information_schema.tables
            WHERE table_schema = DATABASE()
              AND table_type = 'BASE TABLE'
            ORDER BY total_bytes DESC, table_rows DESC
            """;
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        long totalBytes = 0;
        long totalRows = 0;
        for (Map<String, Object> row : rows) {
            totalBytes += toLong(row.get("total_bytes"));
            totalRows += toLong(row.get("table_rows"));
        }

        List<Map<String, Object>> tables = new ArrayList<>();
        String[] colors = {"#3b82f6", "#10b981", "#f59e0b", "#8b5cf6", "#ef4444", "#06b6d4", "#84cc16", "#ec4899"};
        int index = 0;
        for (Map<String, Object> row : rows) {
            long bytes = toLong(row.get("total_bytes"));
            long count = toLong(row.get("table_rows"));
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("table", row.get("table_name"));
            item.put("rows", count);
            item.put("size", formatBytes(bytes));
            item.put("bytes", bytes);
            item.put("percent", totalBytes > 0 ? BigDecimal.valueOf(bytes * 100.0 / totalBytes).setScale(1, RoundingMode.HALF_UP).doubleValue() : 0);
            item.put("color", colors[index++ % colors.length]);
            tables.add(item);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("databaseSize", formatBytes(totalBytes));
        result.put("totalBytes", totalBytes);
        result.put("totalRows", totalRows);
        result.put("tables", tables);
        return Result.success(result);
    }

    private long toLong(Object value) {
        return value instanceof Number number ? number.longValue() : 0L;
    }

    private String formatBytes(long bytes) {
        if (bytes >= 1024L * 1024L * 1024L) {
            return BigDecimal.valueOf(bytes / 1024.0 / 1024.0 / 1024.0).setScale(2, RoundingMode.HALF_UP) + " GB";
        }
        if (bytes >= 1024L * 1024L) {
            return BigDecimal.valueOf(bytes / 1024.0 / 1024.0).setScale(2, RoundingMode.HALF_UP) + " MB";
        }
        if (bytes >= 1024L) {
            return BigDecimal.valueOf(bytes / 1024.0).setScale(2, RoundingMode.HALF_UP) + " KB";
        }
        return bytes + " B";
    }
}
