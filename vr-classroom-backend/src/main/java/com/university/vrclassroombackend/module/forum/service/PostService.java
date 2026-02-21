package com.university.vrclassroombackend.module.forum.service;

import com.university.vrclassroombackend.module.forum.dto.PostCreateDTO;
import com.university.vrclassroombackend.module.forum.dto.PostUpdateDTO;
import com.university.vrclassroombackend.module.forum.vo.PostDetailVO;
import com.university.vrclassroombackend.module.forum.vo.PostVO;

import java.util.List;

public interface PostService {
    List<PostVO> getPublicPosts(Integer page, Integer categoryId, String keyword);
    PostDetailVO getPostDetail(Integer postId, Integer currentUserId);
    Integer createPost(PostCreateDTO dto, Integer authorId);
    boolean updatePost(Integer postId, PostUpdateDTO dto, Integer authorId);
    boolean deletePost(Integer postId, Integer authorId);
    List<com.university.vrclassroombackend.module.user.vo.UserPostVO> getUserPosts(Integer userId, Integer page);
}




