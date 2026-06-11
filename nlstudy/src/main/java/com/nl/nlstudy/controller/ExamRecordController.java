package com.nl.nlstudy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nl.nlstudy.common.Result;
import com.nl.nlstudy.entity.AnswerRecord;
import com.nl.nlstudy.entity.AnswerSnapshot;
import com.nl.nlstudy.entity.Exam;
import com.nl.nlstudy.entity.ExamRecord;
import com.nl.nlstudy.entity.PaperQuestion;
import com.nl.nlstudy.entity.Question;
import com.nl.nlstudy.mapper.AnswerRecordMapper;
import com.nl.nlstudy.mapper.AnswerSnapshotMapper;
import com.nl.nlstudy.mapper.ExamMapper;
import com.nl.nlstudy.mapper.ExamRecordMapper;
import com.nl.nlstudy.mapper.PaperQuestionMapper;
import com.nl.nlstudy.mapper.QuestionMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "考试记录管理")
@RestController
@RequestMapping("/examRecords")
@RequiredArgsConstructor
public class ExamRecordController {

    private final ExamRecordMapper examRecordMapper;
    private final AnswerRecordMapper answerRecordMapper;
    private final AnswerSnapshotMapper snapshotMapper;
    private final QuestionMapper questionMapper;
    private final ExamMapper examMapper;
    private final PaperQuestionMapper paperQuestionMapper;

    @Operation(summary = "获取考试记录详情")
    @GetMapping("/{recordId}")
    public Result<Map<String, Object>> getDetail(@PathVariable Long recordId) {
        ExamRecord record = examRecordMapper.selectById(recordId);
        if (record == null) {
            return Result.error("考试记录不存在");
        }
        
        // 查询所有答题记录
        List<AnswerRecord> answers = answerRecordMapper.selectList(
            new LambdaQueryWrapper<AnswerRecord>()
                .eq(AnswerRecord::getExamRecordId, recordId)
        );

        Map<Long, Integer> sortOrderMap = new HashMap<>();
        Exam exam = record.getExamId() != null ? examMapper.selectById(record.getExamId()) : null;
        if (exam != null && exam.getExamPaperId() != null) {
            List<PaperQuestion> paperQuestions = paperQuestionMapper.selectList(
                    new LambdaQueryWrapper<PaperQuestion>()
                            .eq(PaperQuestion::getPaperId, exam.getExamPaperId())
                            .orderByAsc(PaperQuestion::getSortOrder)
            );
            sortOrderMap = paperQuestions.stream()
                    .filter(item -> item.getQuestionId() != null)
                    .collect(java.util.stream.Collectors.toMap(PaperQuestion::getQuestionId, PaperQuestion::getSortOrder, (a, b) -> a));
        }
        Map<Long, Integer> finalSortOrderMap = sortOrderMap;
        answers.sort(java.util.Comparator.comparing(answer -> finalSortOrderMap.getOrDefault(answer.getQuestionId(), Integer.MAX_VALUE)));
        
        Map<Long, Question> questionMap = new HashMap<>();
        if (!answers.isEmpty()) {
            List<Long> questionIds = answers.stream()
                    .map(AnswerRecord::getQuestionId)
                    .filter(java.util.Objects::nonNull)
                    .toList();
            if (!questionIds.isEmpty()) {
                List<Question> questions = questionMapper.selectBatchIds(questionIds);
                questionMap = questions.stream().collect(java.util.stream.Collectors.toMap(Question::getId, q -> q));
            }
        }
        Map<Long, Question> finalQuestionMap = questionMap;

        List<Map<String, Object>> answerDetails = answers.stream().map(answer -> {
            Map<String, Object> item = new HashMap<>();
            item.put("id", answer.getId());
            item.put("examRecordId", answer.getExamRecordId());
            item.put("questionId", answer.getQuestionId());
            item.put("studentAnswer", answer.getStudentAnswer());
            item.put("isCorrect", answer.getIsCorrect());
            item.put("score", answer.getScore());
            item.put("firstAnswerTime", answer.getFirstAnswerTime());
            item.put("lastModifyTime", answer.getLastModifyTime());
            item.put("sortOrder", finalSortOrderMap.getOrDefault(answer.getQuestionId(), answer.getSortOrder()));
            Question question = finalQuestionMap.get(answer.getQuestionId());
            if (question != null) {
                item.put("questionContent", question.getContent());
                item.put("questionType", question.getType());
                item.put("correctAnswer", question.getAnswer());
                item.put("analysis", question.getAnalysis());
                item.put("fullScore", question.getScore());
            }
            return item;
        }).toList();

        Map<String, Object> result = new HashMap<>();
        result.put("record", record);
        result.put("answers", answerDetails);
        return Result.success(result);
    }

    @Operation(summary = "保存答题快照")
    @PostMapping("/{recordId}/snapshot")
    public Result<Map<String, Object>> saveSnapshot(
            @PathVariable Long recordId,
            @RequestBody Map<String, Object> body) {
        
        AnswerSnapshot snapshot = new AnswerSnapshot();
        snapshot.setExamRecordId(recordId);
        snapshot.setSnapshotData(body.get("answers").toString());
        snapshot.setSaveType((String) body.getOrDefault("saveType", "auto"));
        snapshot.setNetworkStatus((String) body.getOrDefault("networkStatus", "normal"));
        snapshot.setSaveTime(LocalDateTime.now());
        snapshotMapper.insert(snapshot);
        
        Map<String, Object> response = new HashMap<>();
        response.put("snapshotId", snapshot.getId());
        response.put("saveTime", snapshot.getSaveTime());
        return Result.success(response);
    }

    @Operation(summary = "恢复答题快照")
    @GetMapping("/{recordId}/restore")
    public Result<Map<String, Object>> restoreSnapshot(@PathVariable Long recordId) {
        // 获取最新的快照
        LambdaQueryWrapper<AnswerSnapshot> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AnswerSnapshot::getExamRecordId, recordId)
              .orderByDesc(AnswerSnapshot::getSaveTime)
              .last("LIMIT 1");
        AnswerSnapshot snapshot = snapshotMapper.selectOne(wrapper);
        
        Map<String, Object> result = new HashMap<>();
        if (snapshot != null) {
            result.put("hasSnapshot", true);
            result.put("snapshotData", snapshot.getSnapshotData());
            result.put("saveTime", snapshot.getSaveTime());
        } else {
            result.put("hasSnapshot", false);
        }
        return Result.success(result);
    }

    @Operation(summary = "提交试卷并自动评分")
    @PostMapping("/{recordId}/submit")
    public Result<Map<String, Object>> submitExam(@PathVariable Long recordId) {
        // 更新考试记录状态
        ExamRecord record = new ExamRecord();
        record.setId(recordId);
        record.setStatus("submitted");
        record.setSubmitTime(LocalDateTime.now());
        examRecordMapper.updateById(record);
        
        // 自动评分客观题
        List<AnswerRecord> answers = answerRecordMapper.selectList(
            new LambdaQueryWrapper<AnswerRecord>().eq(AnswerRecord::getExamRecordId, recordId)
        );
        
        double objectiveScore = 0;
        double totalObjectiveScore = 0;
        for (AnswerRecord ans : answers) {
            if (ans.getIsCorrect() != null && ans.getIsCorrect() && ans.getScore() != null) {
                objectiveScore += ans.getScore();
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("examRecordId", recordId);
        result.put("objectiveScore", objectiveScore);
        result.put("totalObjectiveScore", totalObjectiveScore);
        result.put("status", "submitted");
        result.put("message", "提交成功，客观题已自动评分");
        return Result.success(result);
    }
}
