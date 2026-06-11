package com.nl.nlstudy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nl.nlstudy.common.Result;
import com.nl.nlstudy.entity.StudentQuestionBank;
import com.nl.nlstudy.entity.StudentQuestionBankShare;
import com.nl.nlstudy.mapper.StudentQuestionBankMapper;
import com.nl.nlstudy.mapper.StudentQuestionBankShareMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "学生端-自建题库")
@RestController
@RequestMapping("/student/questionBanks")
@RequiredArgsConstructor
public class StudentQuestionBankController {

    private final StudentQuestionBankMapper bankMapper;
    private final StudentQuestionBankShareMapper shareMapper;

    @Operation(summary = "获取我的题库列表")
    @GetMapping
    public Result<List<Map<String, Object>>> list(
            @RequestHeader(value = "X-User-Id", required = false) Long studentId) {
        if (studentId == null) studentId = 201L;

        LambdaQueryWrapper<StudentQuestionBank> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentQuestionBank::getStudentId, studentId)
                .orderByDesc(StudentQuestionBank::getCreateTime);
        List<StudentQuestionBank> banks = bankMapper.selectList(wrapper);

        List<Map<String, Object>> result = banks.stream().map(bank -> {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", bank.getId());
            map.put("name", bank.getName());
            map.put("description", bank.getDescription());
            map.put("questionCount", bank.getQuestionCount());
            map.put("isShared", bank.getIsShared());
            map.put("shareCode", bank.getShareCode());
            map.put("createTime", bank.getCreateTime());
            return map;
        }).collect(Collectors.toList());

        return Result.success(result);
    }

    @Operation(summary = "创建题库")
    @PostMapping
    public Result<Map<String, Object>> create(
            @RequestHeader(value = "X-User-Id", required = false) Long studentId,
            @RequestBody Map<String, String> body) {
        if (studentId == null) studentId = 201L;

        StudentQuestionBank bank = new StudentQuestionBank();
        bank.setStudentId(studentId);
        bank.setName(body.get("name"));
        bank.setDescription(body.get("description"));
        bank.setSource("original");
        bank.setQuestionCount(0);
        bank.setIsShared(0);
        bank.setStatus(1);
        bank.setCreateTime(LocalDateTime.now());
        bank.setUpdateTime(LocalDateTime.now());

        bankMapper.insert(bank);
        return Result.success(Map.of("bankId", bank.getId()));
    }

    @Operation(summary = "编辑题库")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Map<String, String> body) {
        StudentQuestionBank bank = bankMapper.selectById(id);
        if (bank == null) {
            return Result.error("题库不存在");
        }

        if (body.get("name") != null) {
            bank.setName(body.get("name"));
        }
        if (body.get("description") != null) {
            bank.setDescription(body.get("description"));
        }
        bank.setUpdateTime(LocalDateTime.now());
        bankMapper.updateById(bank);
        return Result.success();
    }

    @Operation(summary = "删除题库")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        bankMapper.deleteById(id);
        return Result.success();
    }

    @Operation(summary = "添加题目到题库")
    @PostMapping("/{bankId}/questions")
    public Result<Map<String, Object>> addQuestion(
            @PathVariable Long bankId,
            @RequestBody Map<String, Object> body) {
        StudentQuestionBank bank = bankMapper.selectById(bankId);
        if (bank == null) {
            return Result.error("题库不存在");
        }

        // 更新题库题目数量
        bank.setQuestionCount(bank.getQuestionCount() != null ? bank.getQuestionCount() + 1 : 1);
        bank.setUpdateTime(LocalDateTime.now());
        bankMapper.updateById(bank);

        return Result.success(Map.of("bankQuestionId", 1L));
    }

    @Operation(summary = "从题库移除题目")
    @DeleteMapping("/{bankId}/questions/{questionId}")
    public Result<Void> removeQuestion(@PathVariable Long bankId, @PathVariable Long questionId) {
        StudentQuestionBank bank = bankMapper.selectById(bankId);
        if (bank == null) {
            return Result.error("题库不存在");
        }

        // 更新题库题目数量
        if (bank.getQuestionCount() != null && bank.getQuestionCount() > 0) {
            bank.setQuestionCount(bank.getQuestionCount() - 1);
            bank.setUpdateTime(LocalDateTime.now());
            bankMapper.updateById(bank);
        }

        return Result.success();
    }

    @Operation(summary = "分享题库")
    @PostMapping("/{bankId}/share")
    public Result<Map<String, Object>> share(
            @RequestHeader(value = "X-User-Id", required = false) Long studentId,
            @PathVariable Long bankId,
            @RequestBody Map<String, Object> body) {
        if (studentId == null) studentId = 201L;

        StudentQuestionBank bank = bankMapper.selectById(bankId);
        if (bank == null) {
            return Result.error("题库不存在");
        }

        String shareCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // 创建分享记录
        StudentQuestionBankShare share = new StudentQuestionBankShare();
        share.setBankId(bankId);
        share.setShareCode(shareCode);
        share.setCreatorId(studentId);
        if (body.get("expireTime") != null && !((String) body.get("expireTime")).isEmpty()) {
            share.setExpireTime(LocalDateTime.parse((CharSequence) body.get("expireTime")));
        }
        share.setStatus(1);
        share.setUsedCount(0);
        shareMapper.insert(share);

        // 更新题库状态
        bank.setIsShared(1);
        bank.setShareCode(shareCode);
        if (share.getExpireTime() != null) {
            bank.setShareExpireTime(share.getExpireTime());
        }
        bank.setUpdateTime(LocalDateTime.now());
        bankMapper.updateById(bank);

        return Result.success(Map.of(
            "shareCode", shareCode,
            "shareLink", "/api/v1/student/questionBanks/join?code=" + shareCode,
            "expireTime", body.get("expireTime")
        ));
    }

    @Operation(summary = "加入分享的题库")
    @PostMapping("/join")
    public Result<Map<String, Object>> join(
            @RequestHeader(value = "X-User-Id", required = false) Long studentId,
            @RequestBody Map<String, String> body) {
        if (studentId == null) studentId = 201L;

        String shareCode = body.get("code");
        if (shareCode == null || shareCode.isEmpty()) {
            return Result.error("分享码不能为空");
        }

        // 查找分享记录
        LambdaQueryWrapper<StudentQuestionBankShare> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentQuestionBankShare::getShareCode, shareCode)
                .eq(StudentQuestionBankShare::getStatus, 1);
        StudentQuestionBankShare share = shareMapper.selectOne(wrapper);

        if (share == null) {
            return Result.error("分享码无效或已过期");
        }

        // 检查是否已过期
        if (share.getExpireTime() != null && share.getExpireTime().isBefore(LocalDateTime.now())) {
            return Result.error("分享已过期");
        }

        // 复制题库
        StudentQuestionBank originalBank = bankMapper.selectById(share.getBankId());
        if (originalBank == null) {
            return Result.error("原题库不存在");
        }

        StudentQuestionBank newBank = new StudentQuestionBank();
        newBank.setStudentId(studentId);
        newBank.setName(originalBank.getName() + "(副本)");
        newBank.setDescription(originalBank.getDescription());
        newBank.setSource("shared");
        newBank.setSourceBankId(originalBank.getId());
        newBank.setQuestionCount(originalBank.getQuestionCount());
        newBank.setStatus(1);
        newBank.setCreateTime(LocalDateTime.now());
        newBank.setUpdateTime(LocalDateTime.now());
        bankMapper.insert(newBank);

        // 更新使用次数
        share.setUsedCount(share.getUsedCount() != null ? share.getUsedCount() + 1 : 1);
        share.setReceiverId(studentId);
        shareMapper.updateById(share);

        return Result.success(Map.of(
            "bankId", newBank.getId(),
            "bankName", newBank.getName(),
            "questionCount", originalBank.getQuestionCount()
        ));
    }

    @Operation(summary = "获取我加入的共享题库")
    @GetMapping("/shared")
    public Result<List<Map<String, Object>>> sharedBanks(
            @RequestHeader(value = "X-User-Id", required = false) Long studentId) {
        if (studentId == null) studentId = 201L;

        LambdaQueryWrapper<StudentQuestionBank> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentQuestionBank::getStudentId, studentId)
                .eq(StudentQuestionBank::getSource, "shared")
                .orderByDesc(StudentQuestionBank::getCreateTime);
        List<StudentQuestionBank> banks = bankMapper.selectList(wrapper);

        List<Map<String, Object>> result = banks.stream().map(bank -> {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", bank.getId());
            map.put("name", bank.getName());
            map.put("description", bank.getDescription());
            map.put("questionCount", bank.getQuestionCount());
            map.put("sourceBankId", bank.getSourceBankId());
            map.put("createTime", bank.getCreateTime());
            return map;
        }).collect(Collectors.toList());

        return Result.success(result);
    }
}
