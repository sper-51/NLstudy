package com.nl.nlstudy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nl.nlstudy.common.PageResult;
import com.nl.nlstudy.common.Result;
import com.nl.nlstudy.entity.SysClass;
import com.nl.nlstudy.mapper.SysClassMapper;
import com.nl.nlstudy.service.ISysClassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "管理员-班级管理")
@RestController
@RequestMapping("/admin/classes")
@RequiredArgsConstructor
public class AdminClassController {

    private final ISysClassService classService;
    private final SysClassMapper classMapper;
    private final JdbcTemplate jdbcTemplate;

    @Operation(summary = "获取班级列表（分页）")
    @GetMapping
    public Result<PageResult<Map<String, Object>>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        Page<SysClass> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<SysClass> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(SysClass::getName, keyword).or().like(SysClass::getGrade, keyword);
        }
        wrapper.orderByDesc(SysClass::getCreateTime);
        Page<SysClass> result = classService.page(pageParam, wrapper);
        List<Map<String, Object>> records = new ArrayList<>();
        for (SysClass item : result.getRecords()) {
            Map<String, Object> row = new HashMap<>();
            row.put("id", item.getId());
            row.put("name", item.getName());
            row.put("grade", item.getGrade());
            row.put("description", item.getDescription());
            row.put("createTime", item.getCreateTime());
            row.put("updateTime", item.getUpdateTime());
            Integer studentCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM student_class WHERE class_id = ?",
                    Integer.class,
                    item.getId()
            );
            row.put("studentCount", studentCount == null ? 0 : studentCount);
            records.add(row);
        }
        return Result.success(PageResult.of(records, page, pageSize, result.getTotal()));
    }

    @Operation(summary = "创建班级")
    @PostMapping
    public Result<SysClass> create(@RequestBody SysClass sysClass) {
        classService.save(sysClass);
        return Result.success(sysClass);
    }

    @Operation(summary = "编辑班级")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody SysClass sysClass) {
        sysClass.setId(id);
        classService.updateById(sysClass);
        return Result.success();
    }

    @Operation(summary = "删除班级")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        classService.removeById(id);
        return Result.success();
    }

    @Operation(summary = "分配学生到班级")
    @PostMapping("/{classId}/students")
    public Result<Map<String, Object>> assignStudents(@PathVariable Long classId, @RequestBody Map<String, Object> body) {
        List<Long> studentIds = normalizeStudentIds(body.get("studentIds"));

        if (studentIds == null || studentIds.isEmpty()) {
            return Result.error("学生ID列表不能为空");
        }

        // 检查班级是否存在
        SysClass sysClass = classMapper.selectById(classId);
        if (sysClass == null) {
            return Result.error("班级不存在");
        }

        jdbcTemplate.update("DELETE FROM student_class WHERE class_id = ?", classId);

        int successCount = 0;
        int failCount = 0;
        Long semesterId = getDefaultSemesterId();

        for (Long studentId : studentIds) {
            try {
                Integer exists = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM sys_user WHERE id = ? AND role = 'student' AND deleted = 0",
                        Integer.class,
                        studentId
                );
                if (exists == null || exists == 0) {
                    failCount++;
                    continue;
                }
                jdbcTemplate.update(
                        "INSERT INTO student_class (student_id, class_id, semester_id) VALUES (?, ?, ?)",
                        studentId,
                        classId,
                        semesterId
                );
                successCount++;
            } catch (Exception e) {
                failCount++;
            }
        }

        Map<String, Object> result = new java.util.HashMap<>();
        result.put("successCount", successCount);
        result.put("failCount", failCount);
        result.put("message", "分配完成");
        return Result.success(result);
    }

    private List<Long> normalizeStudentIds(Object rawStudentIds) {
        List<Long> result = new ArrayList<>();
        if (!(rawStudentIds instanceof List<?> list)) {
            return result;
        }
        for (Object item : list) {
            if (item instanceof Number number) {
                result.add(number.longValue());
            } else if (item instanceof String text && !text.isBlank()) {
                result.add(Long.parseLong(text));
            }
        }
        return result;
    }

    private Long getDefaultSemesterId() {
        try {
            Number id = jdbcTemplate.queryForObject(
                    "SELECT id FROM semester ORDER BY status DESC, start_date DESC, id DESC LIMIT 1",
                    Number.class
            );
            return id == null ? null : id.longValue();
        } catch (Exception e) {
            return null;
        }
    }
}
