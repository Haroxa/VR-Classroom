package com.university.vrclassroombackend.module.space.controller;

import com.university.vrclassroombackend.common.dto.ApiResponse;
import com.university.vrclassroombackend.module.space.service.SpaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Deprecated
@RestController
@RequestMapping("/api/space")
public class SpaceController {
    
    @Autowired
    private SpaceService spaceService;

    @Deprecated
    @GetMapping("/campuses")
    public ResponseEntity<?> getCampuses() {
        return ResponseEntity.ok().body(ApiResponse.success(spaceService.getCampuses()));
    }

    @Deprecated
    @GetMapping("/buildings")
    public ResponseEntity<?> getBuildings(@RequestParam Integer campusId) {
        return ResponseEntity.ok().body(ApiResponse.success(spaceService.getBuildings(campusId)));
    }

    @Deprecated
    @GetMapping("/classrooms")
    public ResponseEntity<?> getClassrooms(@RequestParam Integer buildingId) {
        return ResponseEntity.ok().body(ApiResponse.success(spaceService.getClassrooms(buildingId)));
    }

    @Deprecated
    @GetMapping("/seats")
    public ResponseEntity<?> getSeats(
            @RequestParam Integer classroomId,
            @RequestParam(defaultValue = "0") Integer page) {
        return ResponseEntity.ok().body(ApiResponse.success(spaceService.getSeats(classroomId, page)));
    }
}




