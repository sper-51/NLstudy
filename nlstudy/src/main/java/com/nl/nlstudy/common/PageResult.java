package com.nl.nlstudy.common;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class PageResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private List<T> list;
    private Pagination pagination;

    @Data
    public static class Pagination implements Serializable {
        private int page;
        private int pageSize;
        private long total;
        
        public Pagination(int page, int pageSize, long total) {
            this.page = page;
            this.pageSize = pageSize;
            this.total = total;
        }
    }

    public static <T> PageResult<T> of(List<T> list, int page, int pageSize, long total) {
        PageResult<T> result = new PageResult<>();
        result.setList(list);
        result.setPagination(new Pagination(page, pageSize, total));
        return result;
    }
}
