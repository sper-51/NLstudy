package com.nl.nlstudy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nl.nlstudy.entity.PaperQuestion;
import com.nl.nlstudy.entity.Question;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PaperQuestionMapper extends BaseMapper<PaperQuestion> {

    @Delete("DELETE FROM exam_paper_question WHERE exam_paper_id = #{paperId}")
    void deleteByPaperId(Long paperId);

    @Select("SELECT * FROM exam_paper_question WHERE exam_paper_id = #{paperId} ORDER BY sort_order")
    List<PaperQuestion> selectByPaperId(Long paperId);

    @Select("SELECT q.*, pq.score, pq.sort_order FROM exam_paper_question pq " +
            "LEFT JOIN question q ON pq.question_id = q.id " +
            "WHERE pq.exam_paper_id = #{paperId} ORDER BY pq.sort_order")
    List<Question> selectQuestionsByPaperId(Long paperId);
}