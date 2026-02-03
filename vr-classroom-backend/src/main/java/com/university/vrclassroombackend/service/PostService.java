package com.university.vrclassroombackend.service;

import com.university.vrclassroombackend.dto.PostCreateDTO;
import com.university.vrclassroombackend.dto.PostUpdateDTO;
import com.university.vrclassroombackend.vo.PostDetailVO;
import com.university.vrclassroombackend.vo.PostVO;
import com.university.vrclassroombackend.vo.UserPostVO;

import java.util.List;

public interface PostService {
    List<PostVO> getPublicPosts(Integer page, Integer categoryId, String keyword);
    PostDetailVO getPostDetail(Integer postId, Integer currentUserId);
    Integer createPost(PostCreateDTO dto, Integer authorId);
    boolean updatePost(Integer postId, PostUpdateDTO dto, Integer authorId);
    boolean deletePost(Integer postId, Integer authorId);
    List<UserPostVO> getUserPosts(Integer userId, Integer page);
}
