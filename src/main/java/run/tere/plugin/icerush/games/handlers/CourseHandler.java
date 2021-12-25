package run.tere.plugin.icerush.games.handlers;

import run.tere.plugin.icerush.games.consts.Course;
import run.tere.plugin.icerush.utils.JsonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CourseHandler {

    private List<Course> courseList;

    public CourseHandler() {
        this.courseList = new ArrayList<>();
    }

    public List<Course> getCourseList() {
        return courseList;
    }

    public void addCourse(Course course) {
        this.courseList.add(course);
        save();
    }

    public void removeCourse(Course course) {
        this.courseList.remove(course);
        save();
    }

    public Course getCourse(UUID worldUUID) {
        for (Course course : courseList) {
            if (course.getWorldUUID().equals(worldUUID)) {
                return course;
            }
        }
        return null;
    }

    public void save() {
        JsonUtil.saveCourseHandler(this);
    }

}
