package com.university.vrclassroombackend.module.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.university.vrclassroombackend.constant.AppConstants;
import com.university.vrclassroombackend.exception.BusinessException;
import com.university.vrclassroombackend.module.admin.dto.CommentAuditDTO;
import com.university.vrclassroombackend.module.admin.dto.PostAuditDTO;
import com.university.vrclassroombackend.module.admin.service.AdminService;
import com.university.vrclassroombackend.module.admin.vo.*;
import com.university.vrclassroombackend.module.forum.mapper.CommentMapper;
import com.university.vrclassroombackend.module.forum.mapper.PostMapper;
import com.university.vrclassroombackend.module.forum.model.Comment;
import com.university.vrclassroombackend.module.forum.model.Post;
import com.university.vrclassroombackend.module.space.mapper.CategoryMapper;
import com.university.vrclassroombackend.module.space.mapper.CollegeMapper;
import com.university.vrclassroombackend.module.space.model.Category;
import com.university.vrclassroombackend.module.space.model.College;
import com.university.vrclassroombackend.module.user.mapper.UserMapper;
import com.university.vrclassroombackend.module.user.model.User;
import com.university.vrclassroombackend.aspect.AuditLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 后台管理服务实现类
 * <p>
 * 提供帖子审核、评论审核等后台管理功能
 * </p>
 *
 * @author University VR Classroom Team
 * @since 1.0.0
 */
@Service
public class AdminServiceImpl implements AdminService {

    /**
     * 日志记录器
     */
    private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

    /**
     * 日期时间格式化器
     */
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 帖子数据访问对象
     */
    private final PostMapper postMapper;

    /**
     * 评论数据访问对象
     */
    private final CommentMapper commentMapper;

    /**
     * 用户数据访问对象
     */
    private final UserMapper userMapper;

    /**
     * 学院数据访问对象
     */
    private final CollegeMapper collegeMapper;

    /**
     * 分类数据访问对象
     */
    private final CategoryMapper categoryMapper;

    /**
     * 构造方法，注入依赖
     *
     * @param postMapper    帖子数据访问对象
     * @param commentMapper 评论数据访问对象
     * @param userMapper    用户数据访问对象
     * @param collegeMapper 学院数据访问对象
     * @param categoryMapper 分类数据访问对象
     */
    @Autowired
    public AdminServiceImpl(PostMapper postMapper, CommentMapper commentMapper,
                           UserMapper userMapper, CollegeMapper collegeMapper,
                           CategoryMapper categoryMapper) {
        this.postMapper = postMapper;
        this.commentMapper = commentMapper;
        this.userMapper = userMapper;
        this.collegeMapper = collegeMapper;
        this.categoryMapper = categoryMapper;
    }

    /**
     * 获取帖子列表（后台审核用）
     * <p>
     * 支持分页查询，可按状态、分类ID、关键词筛选
     * 使用批量查询优化N+1查询问题
     * </p>
     *
     * @param page       页码，从1开始
     * @param status     帖子状态，可选
     * @param categoryId 分类ID，可选
     * @param keyword    搜索关键词，可选
     * @return 分页帖子列表
     */
    @Override
    public IPage<PostAuditVO> getPosts(Integer page, Integer pageSize, Integer status, Integer categoryId, String keyword) {
        // 构建分页，页码从1开始
        int currentPage = page != null && page > 0 ? page : 1;
        int size = pageSize != null && pageSize > 0 ? pageSize : AppConstants.Pagination.DEFAULT_PAGE_SIZE;
        Page<Post> pageQuery = new Page<>(currentPage, size);

        // 构建查询条件
        LambdaQueryWrapper<Post> queryWrapper = buildPostQueryWrapper(status, categoryId, keyword);

        // 查询帖子列表
        IPage<Post> postsPage = postMapper.selectPage(pageQuery, queryWrapper);
        List<Post> posts = postsPage.getRecords();

        // 批量查询关联数据
        Map<Integer, Category> categoryMap = batchQueryCategories(posts);
        Map<Integer, User> userMap = batchQueryUsers(posts);
        Map<Integer, College> collegeMap = batchQueryColleges(userMap.values());

        // 转换为VO
        List<PostAuditVO> postAuditVOs = convertToPostAuditVOs(posts, categoryMap, userMap, collegeMap);

        // 构建返回分页对象
        Page<PostAuditVO> resultPage = new Page<>(postsPage.getCurrent(), postsPage.getSize(), postsPage.getTotal());
        resultPage.setRecords(postAuditVOs);
        return resultPage;
    }

    /**
     * 构建帖子查询条件
     *
     * @param status     帖子状态
     * @param categoryId 分类ID
     * @param keyword    搜索关键词
     * @return 查询条件包装器
     */
    private LambdaQueryWrapper<Post> buildPostQueryWrapper(Integer status, Integer categoryId, String keyword) {
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Post::getDate);

        // 状态条件
        if (status != null) {
            queryWrapper.eq(Post::getStatus, status);
        }

        // 分类ID条件
        if (categoryId != null) {
            queryWrapper.eq(Post::getCategoryId, categoryId);
        }

        // 关键词条件
        if (keyword != null && !keyword.trim().isEmpty()) {
            String searchKeyword = "%" + keyword.trim() + "%";
            queryWrapper.and(wrapper -> {
                wrapper.like(Post::getTitle, searchKeyword)
                       .or()
                       .like(Post::getContent, searchKeyword);
            });
        }

        return queryWrapper;
    }

    /**
     * 批量查询分类信息
     *
     * @param posts 帖子列表
     * @return 分类ID到分类对象的映射
     */
    private Map<Integer, Category> batchQueryCategories(List<Post> posts) {
        Set<Integer> categoryIds = posts.stream()
                .map(Post::getCategoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (categoryIds.isEmpty()) {
            return new HashMap<>();
        }

        List<Category> categories = categoryMapper.selectBatchIds(categoryIds);
        return categories.stream()
                .collect(Collectors.toMap(Category::getId, category -> category));
    }

    /**
     * 批量查询用户信息
     *
     * @param posts 帖子列表
     * @return 用户ID到用户对象的映射
     */
    private Map<Integer, User> batchQueryUsers(List<Post> posts) {
        Set<Integer> authorIds = posts.stream()
                .map(Post::getAuthorId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (authorIds.isEmpty()) {
            return new HashMap<>();
        }

        List<User> users = userMapper.selectBatchIds(authorIds);
        return users.stream()
                .collect(Collectors.toMap(User::getId, user -> user));
    }

    /**
     * 批量查询学院信息
     *
     * @param users 用户列表
     * @return 学院ID到学院对象的映射
     */
    private Map<Integer, College> batchQueryColleges(Collection<User> users) {
        Set<Integer> collegeIds = users.stream()
                .map(User::getCollegeId)
                .filter(Objects::nonNull)
                .map(this::parseCollegeId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (collegeIds.isEmpty()) {
            return new HashMap<>();
        }

        List<College> colleges = collegeMapper.selectBatchIds(collegeIds);
        return colleges.stream()
                .collect(Collectors.toMap(College::getId, college -> college));
    }

    /**
     * 解析学院ID
     *
     * @param collegeId 学院ID字符串
     * @return 学院ID整数，解析失败返回null
     */
    private Integer parseCollegeId(String collegeId) {
        try {
            return Integer.parseInt(collegeId);
        } catch (NumberFormatException e) {
            logger.warn("学院ID格式错误: collegeId={}", collegeId);
            return null;
        }
    }

    /**
     * 将帖子列表转换为审核VO列表
     *
     * @param posts       帖子列表
     * @param categoryMap 分类映射
     * @param userMap     用户映射
     * @param collegeMap  学院映射
     * @return 审核VO列表
     */
    private List<PostAuditVO> convertToPostAuditVOs(List<Post> posts, Map<Integer, Category> categoryMap,
                                                    Map<Integer, User> userMap, Map<Integer, College> collegeMap) {
        List<PostAuditVO> postAuditVOs = new ArrayList<>();

        for (Post post : posts) {
            PostAuditVO vo = new PostAuditVO();
            vo.setId(post.getId().toString());
            vo.setDate(post.getDate());
            vo.setTitle(post.getTitle());
            vo.setSummary(post.getSummary());
            vo.setStatus(post.getStatus());
            vo.setRejectReason(post.getRejectReason());

            // 设置分类名称
            vo.setCategoryName(getCategoryName(categoryMap, post.getCategoryId()));

            // 设置作者信息
            vo.setAuthor(buildUserPublicVO(userMap, collegeMap, post.getAuthorId()));

            postAuditVOs.add(vo);
        }

        return postAuditVOs;
    }

    /**
     * 获取分类名称
     *
     * @param categoryMap 分类映射
     * @param categoryId  分类ID
     * @return 分类名称，不存在或0返回"未分类"
     */
    private String getCategoryName(Map<Integer, Category> categoryMap, Integer categoryId) {
        if (categoryId == null || categoryId == 0) {
            return "未分类";
        }
        Category category = categoryMap.get(categoryId);
        return category != null ? category.getName() : "未分类";
    }

    /**
     * 构建用户公开信息VO
     *
     * @param userMap    用户映射
     * @param collegeMap 学院映射
     * @param userId     用户ID
     * @return 用户公开信息VO，用户不存在返回null
     */
    private UserPublicVO buildUserPublicVO(Map<Integer, User> userMap, Map<Integer, College> collegeMap, Integer userId) {
        User user = userMap.get(userId);
        if (user == null) {
            return null;
        }

        UserPublicVO userPublicVO = new UserPublicVO();
        userPublicVO.setId(user.getId().toString());
        userPublicVO.setName(user.getName());
        userPublicVO.setAvatar(user.getAvatar());
        userPublicVO.setCollegeId(user.getCollegeId());
        userPublicVO.setCollegeName(getCollegeName(collegeMap, user.getCollegeId()));
        userPublicVO.setVerified(user.getVerifyStatus() == 2);

        return userPublicVO;
    }

    /**
     * 获取学院名称
     *
     * @param collegeMap 学院映射
     * @param collegeId  学院ID字符串
     * @return 学院名称，不存在返回"未知学院"
     */
    private String getCollegeName(Map<Integer, College> collegeMap, String collegeId) {
        if (collegeId == null) {
            return "未知学院";
        }

        Integer id = parseCollegeId(collegeId);
        if (id == null) {
            return "未知学院";
        }

        College college = collegeMap.get(id);
        return college != null ? college.getName() : "未知学院";
    }

    /**
     * 获取评论列表（后台审核用）
     * <p>
     * 支持分页查询，可按状态筛选
     * 使用批量查询优化N+1查询问题
     * </p>
     *
     * @param page   页码，从1开始
     * @param status 评论状态，可选
     * @return 分页评论列表
     */
    @Override
    public IPage<CommentAuditVO> getComments(Integer page, Integer pageSize, Integer status) {
        // 构建分页，页码从1开始
        int currentPage = page != null && page > 0 ? page : 1;
        int size = pageSize != null && pageSize > 0 ? pageSize : AppConstants.Pagination.DEFAULT_PAGE_SIZE;
        Page<Comment> pageQuery = new Page<>(currentPage, size);

        // 构建查询条件
        LambdaQueryWrapper<Comment> queryWrapper = buildCommentQueryWrapper(status);

        // 查询评论列表
        IPage<Comment> commentsPage = commentMapper.selectPage(pageQuery, queryWrapper);
        List<Comment> comments = commentsPage.getRecords();

        // 批量查询关联数据
        Map<Integer, User> commenterMap = batchQueryCommenters(comments);
        Map<Integer, Post> postMap = batchQueryPosts(comments);
        Map<Integer, User> allUserMap = batchQueryAllUsers(commenterMap, postMap);
        Map<Integer, College> collegeMap = batchQueryColleges(allUserMap.values());

        // 转换为VO
        List<CommentAuditVO> commentAuditVOs = convertToCommentAuditVOs(comments, commenterMap, postMap, allUserMap, collegeMap);

        // 构建返回分页对象
        Page<CommentAuditVO> resultPage = new Page<>(commentsPage.getCurrent(), commentsPage.getSize(), commentsPage.getTotal());
        resultPage.setRecords(commentAuditVOs);
        return resultPage;
    }

    /**
     * 构建评论查询条件
     *
     * @param status 评论状态
     * @return 查询条件包装器
     */
    private LambdaQueryWrapper<Comment> buildCommentQueryWrapper(Integer status) {
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Comment::getDate);

        // 状态条件
        if (status != null) {
            queryWrapper.eq(Comment::getStatus, status);
        }

        return queryWrapper;
    }

    /**
     * 批量查询评论人信息
     *
     * @param comments 评论列表
     * @return 用户ID到用户对象的映射
     */
    private Map<Integer, User> batchQueryCommenters(List<Comment> comments) {
        Set<Integer> commenterIds = comments.stream()
                .map(Comment::getCommenterId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (commenterIds.isEmpty()) {
            return new HashMap<>();
        }

        List<User> commenters = userMapper.selectBatchIds(commenterIds);
        return commenters.stream()
                .collect(Collectors.toMap(User::getId, user -> user));
    }

    /**
     * 批量查询帖子信息
     *
     * @param comments 评论列表
     * @return 帖子ID到帖子对象的映射
     */
    private Map<Integer, Post> batchQueryPosts(List<Comment> comments) {
        Set<Integer> postIds = comments.stream()
                .map(Comment::getPostId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (postIds.isEmpty()) {
            return new HashMap<>();
        }

        List<Post> posts = postMapper.selectBatchIds(postIds);
        return posts.stream()
                .collect(Collectors.toMap(Post::getId, post -> post));
    }

    /**
     * 批量查询所有用户信息（评论人 + 帖子作者）
     *
     * @param commenterMap 评论人映射
     * @param postMap      帖子映射
     * @return 用户ID到用户对象的映射
     */
    private Map<Integer, User> batchQueryAllUsers(Map<Integer, User> commenterMap, Map<Integer, Post> postMap) {
        Set<Integer> allUserIds = new HashSet<>(commenterMap.keySet());

        Set<Integer> postAuthorIds = postMap.values().stream()
                .map(Post::getAuthorId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        allUserIds.addAll(postAuthorIds);

        if (allUserIds.isEmpty()) {
            return new HashMap<>();
        }

        List<User> allUsers = userMapper.selectBatchIds(allUserIds);
        return allUsers.stream()
                .collect(Collectors.toMap(User::getId, user -> user));
    }

    /**
     * 将评论列表转换为审核VO列表
     *
     * @param comments     评论列表
     * @param commenterMap 评论人映射
     * @param postMap      帖子映射
     * @param allUserMap   所有用户映射
     * @param collegeMap   学院映射
     * @return 审核VO列表
     */
    private List<CommentAuditVO> convertToCommentAuditVOs(List<Comment> comments, Map<Integer, User> commenterMap,
                                                          Map<Integer, Post> postMap, Map<Integer, User> allUserMap,
                                                          Map<Integer, College> collegeMap) {
        List<CommentAuditVO> commentAuditVOs = new ArrayList<>();

        for (Comment comment : comments) {
            CommentAuditVO vo = new CommentAuditVO();
            vo.setId(comment.getId().toString());
            vo.setDate(comment.getDate());
            vo.setContent(comment.getContent());
            vo.setStatus(comment.getStatus());
            vo.setRejectReason(comment.getRejectReason());

            // 设置评论人信息
            vo.setCommenter(buildUserPublicVO(commenterMap, collegeMap, comment.getCommenterId()));

            // 设置关联帖子信息
            vo.setRelatedPost(buildRelatedPostVO(postMap, allUserMap, collegeMap, comment.getPostId()));

            commentAuditVOs.add(vo);
        }

        return commentAuditVOs;
    }

    /**
     * 构建关联帖子VO
     *
     * @param postMap    帖子映射
     * @param allUserMap 所有用户映射
     * @param collegeMap 学院映射
     * @param postId     帖子ID
     * @return 关联帖子VO，帖子不存在返回null
     */
    private RelatedPostVO buildRelatedPostVO(Map<Integer, Post> postMap, Map<Integer, User> allUserMap,
                                             Map<Integer, College> collegeMap, Integer postId) {
        Post post = postMap.get(postId);
        if (post == null) {
            return null;
        }

        RelatedPostVO relatedPostVO = new RelatedPostVO();
        relatedPostVO.setId(post.getId().toString());
        relatedPostVO.setDate(post.getDate());
        relatedPostVO.setTitle(post.getTitle());
        relatedPostVO.setStatus(post.getStatus());
        relatedPostVO.setAuthor(buildUserPublicVO(allUserMap, collegeMap, post.getAuthorId()));

        return relatedPostVO;
    }

    /**
     * 审核帖子
     * <p>
     * 更新帖子状态和驳回理由
     * 已删除的帖子不能审核
     * </p>
     *
     * @param postId 帖子ID
     * @param dto    审核信息
     * @throws BusinessException 帖子不存在或已删除时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @AuditLog(operation = "审核帖子", module = "后台管理模块", logParams = true)
    public void auditPost(Integer postId, PostAuditDTO dto) {
        // 查询帖子
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException(404, "POST_NOT_FOUND", "帖子不存在: postId=" + postId);
        }

        // 状态校验：已删除的帖子不能审核
        if (post.getStatus() == -1) {
            throw new BusinessException(400, "POST_DELETED", "已删除的帖子不能审核");
        }

        // 更新状态和驳回理由
        post.setStatus(dto.getStatus());
        post.setRejectReason(dto.getRejectReason());

        // 保存更新
        postMapper.updateById(post);
        logger.info("帖子审核成功: postId={}, status={}", postId, dto.getStatus());
    }

    /**
     * 审核评论
     * <p>
     * 更新评论状态和驳回理由
     * 已删除的评论不能审核
     * </p>
     *
     * @param commentId 评论ID
     * @param dto       审核信息
     * @throws BusinessException 评论不存在或已删除时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @AuditLog(operation = "审核评论", module = "后台管理模块", logParams = true)
    public void auditComment(Integer commentId, CommentAuditDTO dto) {
        // 查询评论
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException(404, "COMMENT_NOT_FOUND", "评论不存在: commentId=" + commentId);
        }

        // 状态校验：已删除的评论不能审核
        if (comment.getStatus() == -1) {
            throw new BusinessException(400, "COMMENT_DELETED", "已删除的评论不能审核");
        }

        // 更新状态和驳回理由
        comment.setStatus(dto.getStatus());
        comment.setRejectReason(dto.getRejectReason());

        // 保存更新
        commentMapper.updateById(comment);
        logger.info("评论审核成功: commentId={}, status={}", commentId, dto.getStatus());
    }
}
