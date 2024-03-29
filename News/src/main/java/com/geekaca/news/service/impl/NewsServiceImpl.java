package com.geekaca.news.service.impl;

import com.geekaca.news.domain.News;
import com.geekaca.news.domain.NewsCategory;
import com.geekaca.news.domain.NewsTag;
import com.geekaca.news.domain.NewsTagRelation;
import com.geekaca.news.mapper.NewsCategoryMapper;
import com.geekaca.news.mapper.NewsMapper;
import com.geekaca.news.mapper.NewsTagMapper;
import com.geekaca.news.mapper.NewsTagRelationMapper;
import com.geekaca.news.service.NewsService;
import com.geekaca.news.utils.PageInfo;
import com.geekaca.news.utils.PageResult;
import com.geekaca.news.utils.PatternUtil;
import com.geekaca.news.vo.BlogListVO;
import com.geekaca.news.vo.SimpleBlogListVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NewsServiceImpl implements NewsService {
    @Autowired
    private NewsMapper newsMapper;
    @Autowired
    private NewsTagMapper tagMapper;
    @Autowired
    private NewsTagRelationMapper tagRelationMapper;
    @Autowired
    private NewsCategoryMapper categoryMapper;

    @Override
    public Boolean saveNews(News news) {
        //1.向新闻表增加数据
        //2.向新闻和标签关联表   插入数据
        int insertSelective = newsMapper.insertSelective(news);
        List<NewsTag> newsTags1 = tagMapper.searchAll();
        Map<String, Integer> tagmap = new HashMap<>();
        String newsTags = news.getNewsTags();
        for (int i = 0; i < newsTags1.size(); i++) {
            NewsTag tag = newsTags1.get(i);
            tagmap.put(tag.getTagName(), tag.getTagId());
        }
        String[] tags = newsTags.split(",");
        for (int i = 0; i < tags.length; i++) {
            String tagName = tags[i];
            boolean isExsist = false;
            //发送sql，查询tag是否已经存在数据库中
            //如果存在就获取对应的tagId,用来插入关联表
            Integer tagId = tagmap.get(tagName);
            if (tagId == null) {
                NewsTag newsTag=new NewsTag();
                newsTag.setTagName(tagName);
                tagMapper.insertSelective(newsTag);
                tagId=newsTag.getTagId();
            }
            NewsTagRelation newsTagRelation=new NewsTagRelation();
            newsTagRelation.setNewsId(news.getNewsId());
            newsTagRelation.setTagId(tagId);
            tagRelationMapper.insertSelective(newsTagRelation);
            //如果不存在，说明是新的标签，插入标签表tb_news_tag,获取生产的tagId，插入关联表
        }
        return true;
    }

    public List<News> selectAll() {
        List<News> newsList = newsMapper.selectAll();
        return newsList;
    }

    public News selectById(Long id) {
        News news = newsMapper.selectNewsAndCommentsById(id);
        return news;
    }

    public List<News> selectByName(News news) {
        List<News> newsList = newsMapper.selectByName(news);
        return newsList;
    }

    public List<News> findNewsList() {
        List<News> newsList = newsMapper.findNewsList();
        return newsList;
    }

    @Override
    public PageResult getPageNews(Integer pageNo, Integer pageSize, String keyword) {
        int start = (pageNo - 1) * pageSize;
        List<News> newsList = newsMapper.selectByPage(start, pageSize,keyword);
        int count = newsMapper.selectNewsCount(keyword);
        PageResult pageResult = new PageResult(newsList,count,pageSize,pageNo);
        return pageResult;
    }

    @Override
    public int updateNewsViews(Long newsId) {
        int views = newsMapper.increaseViews(newsId);
        return views;
    }

    @Override
    public int getTotalNews() {
        return newsMapper.getTotalNews(null);
    }

    @Override
    public PageResult getBlogsPageByTag(String tagName, Integer page) {
        if (PatternUtil.validKeyword(tagName)) {
            NewsTag tag = tagMapper.selectByTagName(tagName);
            if (tag != null && page > 0) {
                Map param = new HashMap();
                param.put("page", page);
                param.put("limit", 9);
                param.put("tagId", tag.getTagId());
                PageInfo pageUtil = new PageInfo(param);
                List<News> newsList = newsMapper.getNewsPageByTagId(pageUtil);
                List<BlogListVO> blogListVOS = getBlogListVOsByBlogs(newsList);
                int total = newsMapper.getTotalNewsByTagId(pageUtil);
                PageResult pageResult = new PageResult(blogListVOS, total, pageUtil.getLimit(), pageUtil.getPage());
                return pageResult;
            }
        }
        return null;
    }

    private List<BlogListVO> getBlogListVOsByBlogs(List<News> newsList) {
        List<BlogListVO> blogListVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(newsList)) {
            List<Integer> categoryIds = newsList.stream().map(News::getNewsCategoryId).collect(Collectors.toList());
            Map<Integer, String> blogCategoryMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(categoryIds)) {
                List<NewsCategory> blogCategories = categoryMapper.selectByCategoryIds(categoryIds);
                if (!CollectionUtils.isEmpty(blogCategories)) {
                    blogCategoryMap = blogCategories.stream().collect(Collectors.toMap(NewsCategory::getCategoryId, NewsCategory::getCategoryIcon, (key1, key2) -> key2));
                }
            }
            for (News news : newsList) {
                BlogListVO blogListVO = new BlogListVO();
                BeanUtils.copyProperties(news, blogListVO);
                System.out.println(news);
                System.out.println(blogListVO);
                if (blogCategoryMap.containsKey(news.getNewsCategoryId())) {
                    blogListVO.setNewsCategoryIcon(blogCategoryMap.get(news.getNewsCategoryId()));
                } else {
                    blogListVO.setNewsCategoryId(0);
                    blogListVO.setNewsCategoryName("默认分类");
                    blogListVO.setNewsCategoryIcon("/admin/dist/img/category/00.png");
                }
                blogListVOS.add(blogListVO);
            }
        }
        return blogListVOS;
    }


    @Override
    public List<SimpleBlogListVO> getNewsListForIndexPage(int type) {
        List<SimpleBlogListVO> simpleBlogListVOListM = new ArrayList<>();
        List<News> newsList = newsMapper.findNewsListByType(type, 9);
        if (!CollectionUtils.isEmpty(newsList)) {
            for (News news : newsList) {
                SimpleBlogListVO simpleBlogListVO = new SimpleBlogListVO();
                BeanUtils.copyProperties(news,simpleBlogListVO);
                simpleBlogListVOListM.add(simpleBlogListVO);
            }
        }

        return simpleBlogListVOListM;
    }

    @Override
    public Boolean deleteBatch(Integer[] ids) {
        return newsMapper.deleteBatch(ids) > 0;
    }

    @Override
    public String updateNews(News news) {
        News newForUpdate = newsMapper.selectByPrimaryKey(news.getNewsId());
        if (newForUpdate == null) {
            return "数据不存在";
        }
        newForUpdate.setNewsTitle(news.getNewsTitle());
        newForUpdate.setNewsSubUrl(news.getNewsSubUrl());
        newForUpdate.setNewsContent(news.getNewsContent());
        newForUpdate.setNewsCoverImage(news.getNewsCoverImage());
        newForUpdate.setNewsStatus(news.getNewsStatus());
        newForUpdate.setEnableComment(news.getEnableComment());
        NewsCategory blogCategory = categoryMapper.selectByPrimaryKey(news.getNewsCategoryId());
        if (blogCategory == null) {
            newForUpdate.setNewsCategoryId(0);
            newForUpdate.setNewsCategoryName("默认分类");
        } else {
            //设置博客分类名称
            newForUpdate.setNewsCategoryName(blogCategory.getCategoryName());
            newForUpdate.setNewsCategoryId(blogCategory.getCategoryId());
            //分类的排序值加1
            blogCategory.setCategoryRank(blogCategory.getCategoryRank() + 1);
        }
        //处理标签数据
        String[] tags = news.getNewsTags().split(",");
        if (tags.length > 6) {
            return "标签数量限制为6";
        }
        newForUpdate.setNewsTags(news.getNewsTags());
        //新增的tag对象
        List<NewsTag> tagListForInsert = new ArrayList<>();
        //所有的tag对象，用于建立关系数据
        List<NewsTag> allTagsList = new ArrayList<>();
        for (int i = 0; i < tags.length; i++) {
            NewsTag tag = tagMapper.selectByTagName(tags[i]);
            if (tag == null) {
                //不存在就新增
                NewsTag tempTag = new NewsTag();
                tempTag.setTagName(tags[i]);
                tagListForInsert.add(tempTag);
            } else {
                allTagsList.add(tag);
            }
        }
        //新增标签数据不为空->新增标签数据
        if (!CollectionUtils.isEmpty(tagListForInsert)) {
            tagMapper.batchInsertBlogTag(tagListForInsert);
        }
        List<NewsTagRelation> blogTagRelations = new ArrayList<>();
        //新增关系数据
        allTagsList.addAll(tagListForInsert);
        for (NewsTag tag : allTagsList) {
            NewsTagRelation blogTagRelation = new NewsTagRelation();
            blogTagRelation.setNewsId(news.getNewsId());
            blogTagRelation.setTagId(tag.getTagId());
            blogTagRelations.add(blogTagRelation);
        }
        //修改blog信息->修改分类排序值->删除原关系数据->保存新的关系数据
        if (blogCategory != null) {
            categoryMapper.updateByPrimaryKeySelective(blogCategory);
        }
        tagRelationMapper.deleteByBlogId(news.getNewsId());
        tagRelationMapper.batchInsert(blogTagRelations);
        if (newsMapper.updateByPrimaryKeySelective(newForUpdate) > 0) {
            return "success";
        }
        return "修改失败";

    }

    @Override
    public News getNewsById(Long newsId) {
        return newsMapper.selectByPrimaryKey(newsId);
    }

}
