package com.university.vrclassroombackend.module.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.university.vrclassroombackend.module.admin.dto.PostAuditDTO;
import com.university.vrclassroombackend.module.admin.dto.CommentAuditDTO;
import com.university.vrclassroombackend.module.admin.vo.PostAuditVO;
import com.university.vrclassroombackend.module.admin.vo.CommentAuditVO;

import java.util.List;

/**
 * 后台管理服务接口
 */
public interface AdminService {
    /**
     * 获取帖子列表（后台审核用）
     * @param page 页码，每页固定20条数据
     * @param status 状态，没传则查全部
     * @param categoryId 分类id，没传则查全部
     * @param keyword 搜索关键词
     * @return 分页帖子列表
     */
    IPage<PostAuditVO> getPosts(Integer page, Integer status, Integer categoryId, String keyword);

    /**
     * 获取评论列表（后台审核用）
     * @param page 页码，每页固定20条数据
     * @param status 状态，没传则查全部
     * @return 分页评论列表
     */
    IPage<CommentAuditVO> getComments(Integer page, Integer status);

    /**
     * 审核帖子
     * @param postId 帖子ID
     * @param dto 审核信息
     */
    void auditPost(Integer postId, PostAuditDTO dto);

    /**
     * 审核评论
     * @param commentId 评论ID
     * @param dto 审核信息
     */
    void auditComment(Integer commentId, CommentAuditDTO dto);
}
