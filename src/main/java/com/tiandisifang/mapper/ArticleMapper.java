package com.tiandisifang.mapper;

import com.tiandisifang.model.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
@Mapper
public interface ArticleMapper {

    @Select("select * from article")
    public List<Article> selectAll();
}
