package com.university.vrclassroombackend.module.space.service;

import com.university.vrclassroombackend.module.space.model.Building;
import com.university.vrclassroombackend.module.space.model.Campus;
import com.university.vrclassroombackend.module.space.model.Category;
import com.university.vrclassroombackend.module.space.model.ClassRoom;
import com.university.vrclassroombackend.module.space.model.College;

import java.util.List;

public interface SpaceService {

    List<Campus> getAllCampuses();

    List<Building> getBuildingsByCampus(Integer campusId);

    List<Building> getAllBuildings();

    List<ClassRoom> getRoomsByBuilding(Integer buildingId);

    List<ClassRoom> getAllRooms();

    List<Category> getAllCategories();

    List<College> getAllColleges();

    ClassRoom getRoomById(Integer roomId);
}
