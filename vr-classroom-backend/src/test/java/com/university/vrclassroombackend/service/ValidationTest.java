package com.university.vrclassroombackend.service;

import com.university.vrclassroombackend.module.forum.dto.CommentCreateDTO;
import com.university.vrclassroombackend.module.forum.dto.CommentUpdateDTO;
import com.university.vrclassroombackend.module.forum.dto.PostCreateDTO;
import com.university.vrclassroombackend.module.forum.dto.PostUpdateDTO;
import com.university.vrclassroombackend.module.user.dto.LoginDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("参数校验测试")
class ValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("PostCreateDTO 参数校验测试")
    class PostCreateDTOValidationTests {

        @Test
        @DisplayName("正常参数-校验通过")
        void testValidPostCreateDTO() {
            PostCreateDTO dto = new PostCreateDTO();
            dto.setTitle("测试标题");
            dto.setContent("测试内容");
            dto.setCategoryId(1);

            Set<ConstraintViolation<PostCreateDTO>> violations = validator.validate(dto);

            assertTrue(violations.isEmpty(), "正常参数应该通过校验");
        }

        @Test
        @DisplayName("空标题-校验失败")
        void testEmptyTitle() {
            PostCreateDTO dto = new PostCreateDTO();
            dto.setTitle("");
            dto.setContent("测试内容");
            dto.setCategoryId(1);

            Set<ConstraintViolation<PostCreateDTO>> violations = validator.validate(dto);

            assertFalse(violations.isEmpty(), "空标题应该校验失败");
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("标题不能为空")));
        }

        @Test
        @DisplayName("null标题-校验失败")
        void testNullTitle() {
            PostCreateDTO dto = new PostCreateDTO();
            dto.setTitle(null);
            dto.setContent("测试内容");
            dto.setCategoryId(1);

            Set<ConstraintViolation<PostCreateDTO>> violations = validator.validate(dto);

            assertFalse(violations.isEmpty(), "null标题应该校验失败");
        }

        @Test
        @DisplayName("超长标题-校验失败")
        void testTooLongTitle() {
            PostCreateDTO dto = new PostCreateDTO();
            dto.setTitle("a".repeat(201));
            dto.setContent("测试内容");
            dto.setCategoryId(1);

            Set<ConstraintViolation<PostCreateDTO>> violations = validator.validate(dto);

            assertFalse(violations.isEmpty(), "超长标题应该校验失败");
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("标题长度")));
        }

        @Test
        @DisplayName("标题长度边界-200字符")
        void testTitleBoundary() {
            PostCreateDTO dto = new PostCreateDTO();
            dto.setTitle("a".repeat(200));
            dto.setContent("测试内容");
            dto.setCategoryId(1);

            Set<ConstraintViolation<PostCreateDTO>> violations = validator.validate(dto);

            assertTrue(violations.isEmpty(), "200字符标题应该通过校验");
        }

        @Test
        @DisplayName("空内容-校验失败")
        void testEmptyContent() {
            PostCreateDTO dto = new PostCreateDTO();
            dto.setTitle("测试标题");
            dto.setContent("");
            dto.setCategoryId(1);

            Set<ConstraintViolation<PostCreateDTO>> violations = validator.validate(dto);

            assertFalse(violations.isEmpty(), "空内容应该校验失败");
        }

        @Test
        @DisplayName("超长内容-校验失败")
        void testTooLongContent() {
            PostCreateDTO dto = new PostCreateDTO();
            dto.setTitle("测试标题");
            dto.setContent("a".repeat(10001));
            dto.setCategoryId(1);

            Set<ConstraintViolation<PostCreateDTO>> violations = validator.validate(dto);

            assertFalse(violations.isEmpty(), "超长内容应该校验失败");
        }

        @Test
        @DisplayName("内容长度边界-10000字符")
        void testContentBoundary() {
            PostCreateDTO dto = new PostCreateDTO();
            dto.setTitle("测试标题");
            dto.setContent("a".repeat(10000));
            dto.setCategoryId(1);

            Set<ConstraintViolation<PostCreateDTO>> violations = validator.validate(dto);

            assertTrue(violations.isEmpty(), "10000字符内容应该通过校验");
        }

        @Test
        @DisplayName("null分类ID-校验失败")
        void testNullCategoryId() {
            PostCreateDTO dto = new PostCreateDTO();
            dto.setTitle("测试标题");
            dto.setContent("测试内容");
            dto.setCategoryId(null);

            Set<ConstraintViolation<PostCreateDTO>> violations = validator.validate(dto);

            assertFalse(violations.isEmpty(), "null分类ID应该校验失败");
        }

        @Test
        @DisplayName("图片数量超限-校验失败")
        void testTooManyImages() {
            PostCreateDTO dto = new PostCreateDTO();
            dto.setTitle("测试标题");
            dto.setContent("测试内容");
            dto.setCategoryId(1);
            List<String> images = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                images.add("image_" + i + ".jpg");
            }
            dto.setImages(images);

            Set<ConstraintViolation<PostCreateDTO>> violations = validator.validate(dto);

            assertFalse(violations.isEmpty(), "超过9张图片应该校验失败");
        }

        @Test
        @DisplayName("图片数量边界-9张")
        void testImagesBoundary() {
            PostCreateDTO dto = new PostCreateDTO();
            dto.setTitle("测试标题");
            dto.setContent("测试内容");
            dto.setCategoryId(1);
            List<String> images = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                images.add("image_" + i + ".jpg");
            }
            dto.setImages(images);

            Set<ConstraintViolation<PostCreateDTO>> violations = validator.validate(dto);

            assertTrue(violations.isEmpty(), "9张图片应该通过校验");
        }
    }

    @Nested
    @DisplayName("PostUpdateDTO 参数校验测试")
    class PostUpdateDTOValidationTests {

        @Test
        @DisplayName("正常参数-校验通过")
        void testValidPostUpdateDTO() {
            PostUpdateDTO dto = new PostUpdateDTO();
            dto.setTitle("测试标题");
            dto.setContent("测试内容");

            Set<ConstraintViolation<PostUpdateDTO>> violations = validator.validate(dto);

            assertTrue(violations.isEmpty(), "正常参数应该通过校验");
        }

        @Test
        @DisplayName("空标题-校验失败")
        void testEmptyTitle() {
            PostUpdateDTO dto = new PostUpdateDTO();
            dto.setTitle("");
            dto.setContent("测试内容");

            Set<ConstraintViolation<PostUpdateDTO>> violations = validator.validate(dto);

            assertFalse(violations.isEmpty(), "空标题应该校验失败");
        }

        @Test
        @DisplayName("超长标题-校验失败")
        void testTooLongTitle() {
            PostUpdateDTO dto = new PostUpdateDTO();
            dto.setTitle("a".repeat(201));
            dto.setContent("测试内容");

            Set<ConstraintViolation<PostUpdateDTO>> violations = validator.validate(dto);

            assertFalse(violations.isEmpty(), "超长标题应该校验失败");
        }

        @Test
        @DisplayName("空内容-校验失败")
        void testEmptyContent() {
            PostUpdateDTO dto = new PostUpdateDTO();
            dto.setTitle("测试标题");
            dto.setContent("");

            Set<ConstraintViolation<PostUpdateDTO>> violations = validator.validate(dto);

            assertFalse(violations.isEmpty(), "空内容应该校验失败");
        }

        @Test
        @DisplayName("超长内容-校验失败")
        void testTooLongContent() {
            PostUpdateDTO dto = new PostUpdateDTO();
            dto.setTitle("测试标题");
            dto.setContent("a".repeat(10001));

            Set<ConstraintViolation<PostUpdateDTO>> violations = validator.validate(dto);

            assertFalse(violations.isEmpty(), "超长内容应该校验失败");
        }
    }

    @Nested
    @DisplayName("CommentCreateDTO 参数校验测试")
    class CommentCreateDTOValidationTests {

        @Test
        @DisplayName("正常参数-校验通过")
        void testValidCommentCreateDTO() {
            CommentCreateDTO dto = new CommentCreateDTO();
            dto.setContent("测试评论内容");
            dto.setPostId(1);

            Set<ConstraintViolation<CommentCreateDTO>> violations = validator.validate(dto);

            assertTrue(violations.isEmpty(), "正常参数应该通过校验");
        }

        @Test
        @DisplayName("空内容-校验失败")
        void testEmptyContent() {
            CommentCreateDTO dto = new CommentCreateDTO();
            dto.setContent("");
            dto.setPostId(1);

            Set<ConstraintViolation<CommentCreateDTO>> violations = validator.validate(dto);

            assertFalse(violations.isEmpty(), "空内容应该校验失败");
        }

        @Test
        @DisplayName("null内容-校验失败")
        void testNullContent() {
            CommentCreateDTO dto = new CommentCreateDTO();
            dto.setContent(null);
            dto.setPostId(1);

            Set<ConstraintViolation<CommentCreateDTO>> violations = validator.validate(dto);

            assertFalse(violations.isEmpty(), "null内容应该校验失败");
        }

        @Test
        @DisplayName("超长内容-校验失败")
        void testTooLongContent() {
            CommentCreateDTO dto = new CommentCreateDTO();
            dto.setContent("a".repeat(1001));
            dto.setPostId(1);

            Set<ConstraintViolation<CommentCreateDTO>> violations = validator.validate(dto);

            assertFalse(violations.isEmpty(), "超长内容应该校验失败");
        }

        @Test
        @DisplayName("内容长度边界-1000字符")
        void testContentBoundary() {
            CommentCreateDTO dto = new CommentCreateDTO();
            dto.setContent("a".repeat(1000));
            dto.setPostId(1);

            Set<ConstraintViolation<CommentCreateDTO>> violations = validator.validate(dto);

            assertTrue(violations.isEmpty(), "1000字符内容应该通过校验");
        }
    }

    @Nested
    @DisplayName("CommentUpdateDTO 参数校验测试")
    class CommentUpdateDTOValidationTests {

        @Test
        @DisplayName("正常参数-校验通过")
        void testValidCommentUpdateDTO() {
            CommentUpdateDTO dto = new CommentUpdateDTO();
            dto.setContent("测试评论内容");

            Set<ConstraintViolation<CommentUpdateDTO>> violations = validator.validate(dto);

            assertTrue(violations.isEmpty(), "正常参数应该通过校验");
        }

        @Test
        @DisplayName("空内容-校验失败")
        void testEmptyContent() {
            CommentUpdateDTO dto = new CommentUpdateDTO();
            dto.setContent("");

            Set<ConstraintViolation<CommentUpdateDTO>> violations = validator.validate(dto);

            assertFalse(violations.isEmpty(), "空内容应该校验失败");
        }

        @Test
        @DisplayName("超长内容-校验失败")
        void testTooLongContent() {
            CommentUpdateDTO dto = new CommentUpdateDTO();
            dto.setContent("a".repeat(1001));

            Set<ConstraintViolation<CommentUpdateDTO>> violations = validator.validate(dto);

            assertFalse(violations.isEmpty(), "超长内容应该校验失败");
        }
    }

    @Nested
    @DisplayName("LoginDTO 参数校验测试")
    class LoginDTOValidationTests {

        @Test
        @DisplayName("正常参数-校验通过")
        void testValidLoginDTO() {
            LoginDTO dto = new LoginDTO();
            dto.setLoginCode("test_login_code");
            dto.setPhoneCode("test_phone_code");

            Set<ConstraintViolation<LoginDTO>> violations = validator.validate(dto);

            assertTrue(violations.isEmpty(), "正常参数应该通过校验");
        }

        @Test
        @DisplayName("空登录码-校验失败")
        void testEmptyLoginCode() {
            LoginDTO dto = new LoginDTO();
            dto.setLoginCode("");
            dto.setPhoneCode("test_phone_code");

            Set<ConstraintViolation<LoginDTO>> violations = validator.validate(dto);

            assertFalse(violations.isEmpty(), "空登录码应该校验失败");
        }

        @Test
        @DisplayName("null登录码-校验失败")
        void testNullLoginCode() {
            LoginDTO dto = new LoginDTO();
            dto.setLoginCode(null);
            dto.setPhoneCode("test_phone_code");

            Set<ConstraintViolation<LoginDTO>> violations = validator.validate(dto);

            assertFalse(violations.isEmpty(), "null登录码应该校验失败");
        }

        @Test
        @DisplayName("空手机号授权码-校验失败")
        void testEmptyPhoneCode() {
            LoginDTO dto = new LoginDTO();
            dto.setLoginCode("test_login_code");
            dto.setPhoneCode("");

            Set<ConstraintViolation<LoginDTO>> violations = validator.validate(dto);

            assertFalse(violations.isEmpty(), "空手机号授权码应该校验失败");
        }

        @Test
        @DisplayName("null手机号授权码-校验失败")
        void testNullPhoneCode() {
            LoginDTO dto = new LoginDTO();
            dto.setLoginCode("test_login_code");
            dto.setPhoneCode(null);

            Set<ConstraintViolation<LoginDTO>> violations = validator.validate(dto);

            assertFalse(violations.isEmpty(), "null手机号授权码应该校验失败");
        }
    }

    @Nested
    @DisplayName("边界值综合测试")
    class BoundaryComprehensiveTests {

        @Test
        @DisplayName("帖子标题-最小长度1字符")
        void testTitleMinLength() {
            PostCreateDTO dto = new PostCreateDTO();
            dto.setTitle("a");
            dto.setContent("测试内容");
            dto.setCategoryId(1);

            Set<ConstraintViolation<PostCreateDTO>> violations = validator.validate(dto);

            assertTrue(violations.isEmpty(), "1字符标题应该通过校验");
        }

        @Test
        @DisplayName("帖子内容-最小长度1字符")
        void testContentMinLength() {
            PostCreateDTO dto = new PostCreateDTO();
            dto.setTitle("测试标题");
            dto.setContent("a");
            dto.setCategoryId(1);

            Set<ConstraintViolation<PostCreateDTO>> violations = validator.validate(dto);

            assertTrue(violations.isEmpty(), "1字符内容应该通过校验");
        }

        @Test
        @DisplayName("评论内容-最小长度1字符")
        void testCommentContentMinLength() {
            CommentCreateDTO dto = new CommentCreateDTO();
            dto.setContent("a");
            dto.setPostId(1);

            Set<ConstraintViolation<CommentCreateDTO>> violations = validator.validate(dto);

            assertTrue(violations.isEmpty(), "1字符评论内容应该通过校验");
        }

        @Test
        @DisplayName("图片列表-空列表")
        void testEmptyImagesList() {
            PostCreateDTO dto = new PostCreateDTO();
            dto.setTitle("测试标题");
            dto.setContent("测试内容");
            dto.setCategoryId(1);
            dto.setImages(new ArrayList<>());

            Set<ConstraintViolation<PostCreateDTO>> violations = validator.validate(dto);

            assertTrue(violations.isEmpty(), "空图片列表应该通过校验");
        }

        @Test
        @DisplayName("图片列表-null")
        void testNullImagesList() {
            PostCreateDTO dto = new PostCreateDTO();
            dto.setTitle("测试标题");
            dto.setContent("测试内容");
            dto.setCategoryId(1);
            dto.setImages(null);

            Set<ConstraintViolation<PostCreateDTO>> violations = validator.validate(dto);

            assertTrue(violations.isEmpty(), "null图片列表应该通过校验");
        }
    }
}
