package com.nl.nlstudy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nl.nlstudy.common.Result;
import com.nl.nlstudy.entity.Semester;
import com.nl.nlstudy.mapper.SemesterMapper;
import com.nl.nlstudy.service.ISemesterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "管理员-学期管理")
@RestController
@RequestMapping("/admin/semesters")
@RequiredArgsConstructor
public class AdminSemesterController {

    private final ISemesterService semesterService;
    private final SemesterMapper semesterMapper;

    @Operation(summary = "获取学期列表")
    @GetMapping
    public Result<List<Semester>> list() {
        LambdaQueryWrapper<Semester> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Semester::getCreateTime);
        return Result.success(semesterService.list(wrapper));
    }

    @Operation(summary = "创建学期")
    @PostMapping
    public Result<Semester> create(@RequestBody Semester semester) {
        semesterService.save(semester);
        return Result.success(semester);
    }

    @Operation(summary = "编辑学期")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Semester semester) {
        semester.setId(id);
        semesterService.updateById(semester);
        return Result.success();
    }

    @Operation(summary = "删除学期")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        semesterService.removeById(id);
        return Result.success();
    }
}
