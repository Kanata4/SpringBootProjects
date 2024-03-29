package com.geekaca.news.mapper;

import com.geekaca.news.domain.NewsCategory;
import com.geekaca.news.utils.PageQueryUtil;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author kanata
* @description 针对表【tb_news_category】的数据库操作Mapper
* @createDate 2023-09-08 15:24:32
* @Entity com.geekaca.news.domain.NewsCategory
*/
@Mapper
public interface NewsCategoryMapper {

    int deleteByIds(Integer[] ids);

    int insert(NewsCategory record);

    int insertSelective(NewsCategory record);

    NewsCategory selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(NewsCategory record);

    int updateByPrimaryKey(NewsCategory record);

    List<NewsCategory> findAll();

    int getTotalCategories(PageQueryUtil pageUtil);

    List<NewsCategory> selectByCategoryIds(@Param("categoryIds") List<Integer> categoryIds);

    List<NewsCategory> findCategoryList(PageQueryUtil pageUtil);

    NewsCategory selectByCategoryName(String categoryName);
}
