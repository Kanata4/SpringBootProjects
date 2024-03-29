package com.geekaca.news.mapper;

import com.geekaca.news.domain.News;
import com.geekaca.news.utils.PageInfo;
import com.geekaca.news.utils.PageQueryUtil;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author kanata
* @description 针对表【tb_news】的数据库操作Mapper
* @createDate 2023-09-08 15:24:32
* @Entity com.geekaca.news.domain.News
*/
// TODO:添加@Mapper
@Mapper
public interface NewsMapper {

    int deleteByPrimaryKey(Long id);

    int insert(News record);

    int insertSelective(News record);

    News selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(News record);

    int updateByPrimaryKey(News record);

    // 前台页面 新闻列表查询 泛型约束
    List<News> selectAll();

    News selectNewsAndCommentsById(Long id);

    //模糊查询 根据
    List<News> selectByName(News news);

    //获取新闻列表
    List<News> findNewsList();

    /**
     * 发送sql查询 数据 limit (页码 - 1) * 每页条数
     * @param start  limit 第一个参数
     * @param recordSize 去多少条数据
     * @return
     */
    List<News> selectByPage(@Param("start") Integer start, @Param("recordSize") Integer recordSize,
                            @Param("keyword") String keyword);
    //查询数量
    int selectNewsCount(@Param("keyword") String keyword);

    int increaseViews(@Param("newsId")Long newsId);

    int getTotalNews(PageQueryUtil pageUtil);

    List<News> getNewsPageByTagId(PageInfo pageUtil);

    int getTotalNewsByTagId(PageInfo pageUtil);

    List<News> findNewsListByType(@Param("type") int type, @Param("limit") int limit);

    int updateNewsCategorys(@Param("categoryName")String categoryName, @Param("categoryId") Integer categoryId,@Param("ids") Integer[] ids);

    int deleteBatch(Integer[] ids);
}
