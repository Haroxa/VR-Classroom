package com.university.vrclassroombackend.module.space.controller;

import com.university.vrclassroombackend.common.dto.ApiResponse;
import com.university.vrclassroombackend.module.space.model.Building;
import com.university.vrclassroombackend.module.space.model.Campus;
import com.university.vrclassroombackend.module.space.model.Category;
import com.university.vrclassroombackend.module.space.model.ClassRoom;
import com.university.vrclassroombackend.module.space.model.College;
import com.university.vrclassroombackend.module.space.service.SpaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "数据查询", description = "校区、楼栋、教室、分类、学院等数据查询接口")
public class SpaceController {

    private static final Logger logger = LoggerFactory.getLogger(SpaceController.class);

    @Autowired
    private SpaceService spaceService;

    @GetMapping("/campuses")
    @Operation(summary = "获取所有校区", description = "获取所有校区列表")
    public ResponseEntity<?> getAllCampuses() {
        List<Campus> campuses = spaceService.getAllCampuses();
        logger.info("获取校区列表成功: count={}", campuses.size());
        return ResponseEntity.ok().body(ApiResponse.success(campuses));
    }

    @GetMapping("/campuses/{campusId}/buildings")
    @Operation(summary = "获取校区下的楼栋", description = "根据校区ID获取该校区下的楼栋列表")
    public ResponseEntity<?> getBuildingsByCampus(
            @Parameter(description = "校区ID", example = "1") @PathVariable Integer campusId) {
        List<Building> buildings = spaceService.getBuildingsByCampus(campusId);
        logger.info("获取楼栋列表成功: campusId={}, count={}", campusId, buildings.size());
        return ResponseEntity.ok().body(ApiResponse.success(buildings));
    }

    @GetMapping("/buildings")
    @Operation(summary = "获取所有楼栋", description = "获取所有楼栋列表")
    public ResponseEntity<?> getAllBuildings() {
        List<Building> buildings = spaceService.getAllBuildings();
        logger.info("获取楼栋列表成功: count={}", buildings.size());
        return ResponseEntity.ok().body(ApiResponse.success(buildings));
    }

    @GetMapping("/buildings/{buildingId}/rooms")
    @Operation(summary = "获取楼栋下的教室", description = "根据楼栋ID获取该楼栋下的教室列表")
    public ResponseEntity<?> getRoomsByBuilding(
            @Parameter(description = "楼栋ID", example = "1") @PathVariable Integer buildingId) {
        List<ClassRoom> rooms = spaceService.getRoomsByBuilding(buildingId);
        logger.info("获取教室列表成功: buildingId={}, count={}", buildingId, rooms.size());
        return ResponseEntity.ok().body(ApiResponse.success(rooms));
    }

    @GetMapping("/rooms")
    @Operation(summary = "获取所有教室", description = "获取所有教室列表")
    public ResponseEntity<?> getAllRooms() {
        List<ClassRoom> rooms = spaceService.getAllRooms();
        logger.info("获取教室列表成功: count={}", rooms.size());
        return ResponseEntity.ok().body(ApiResponse.success(rooms));
    }

    @GetMapping("/rooms/{roomId}")
    @Operation(summary = "获取教室详情", description = "根据教室ID获取教室详情")
    public ResponseEntity<?> getRoomById(
            @Parameter(description = "教室ID", example = "1") @PathVariable Integer roomId) {
        ClassRoom room = spaceService.getRoomById(roomId);
        if (room == null) {
            logger.warn("教室不存在: roomId={}", roomId);
            return ResponseEntity.ok().body(ApiResponse.success(null));
        }
        logger.info("获取教室详情成功: roomId={}", roomId);
        return ResponseEntity.ok().body(ApiResponse.success(room));
    }

    @GetMapping("/categories")
    @Operation(summary = "获取所有分类", description = "获取所有帖子分类列表")
    public ResponseEntity<?> getAllCategories() {
        List<Category> categories = spaceService.getAllCategories();
        logger.info("获取分类列表成功: count={}", categories.size());
        return ResponseEntity.ok().body(ApiResponse.success(categories));
    }

    @GetMapping("/colleges")
    @Operation(summary = "获取所有学院", description = "获取所有学院列表")
    public ResponseEntity<?> getAllColleges() {
        List<College> colleges = spaceService.getAllColleges();
        logger.info("获取学院列表成功: count={}", colleges.size());
        return ResponseEntity.ok().body(ApiResponse.success(colleges));
    }
}
