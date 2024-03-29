package com.geekaca.news.service;

import com.geekaca.news.domain.News;

import com.geekaca.news.utils.PageQueryUtil;
import com.geekaca.news.utils.PageResult;
import com.geekaca.news.vo.SimpleBlogListVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Transactional
public interface NewsService {
    Boolean saveNews(News news);

    /**
     * 前台页面 新闻列表查询
     *
     * @return
     */
    List<News> selectAll();

    /**
     * 根据id查询新闻 带出评论
     *
     * @param newsId
     * @return
     */
    News selectById(Long newsId);

    /**
     * 根据新闻标题或新闻内容 查询新闻
     *
     * @param news
     * @return
     */
    List<News> selectByName(News news);

    /**
     * 后台 获取新闻列表
     *
     * @return
     */
    List<News> findNewsList();

    /**
     * 查询新闻列表 带有分页
     * @param pageNo 页码 第几页
     * @param pageSize 每页记录条数
     * @param keyword
     * @return
     */
    PageResult getPageNews(Integer pageNo, Integer pageSize, String keyword);

    int updateNewsViews(Long newsId);

    int getTotalNews();

    PageResult getBlogsPageByTag(String tagName, Integer page);



    /**
     * 首页侧边栏 数据列表
     * 0-点击最多 1-最新发布
     * @param type
     * @return
     */
    List<SimpleBlogListVO> getNewsListForIndexPage(int type);

    Boolean deleteBatch(Integer[] ids);

    /**
     * 后台修改
     *
     * @param blog
     * @return
     */
    String updateNews(News blog);

    News getNewsById(Long newsId);
}
