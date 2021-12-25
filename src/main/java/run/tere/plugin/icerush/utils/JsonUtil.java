package run.tere.plugin.icerush.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.plugin.Plugin;
import run.tere.plugin.icerush.IceRush;
import run.tere.plugin.icerush.games.consts.Course;
import run.tere.plugin.icerush.games.handlers.CourseHandler;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

public class JsonUtil {

    public static void toJson(Plugin plugin, Object object, String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);
        try (Writer writer = new OutputStreamWriter(
                new FileOutputStream(file), StandardCharsets.UTF_8)) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(object, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Object fromJson(Plugin plugin, String fileName, Type typeOfT) {
        File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) return null;
        try (Reader reader = new InputStreamReader(
                new FileInputStream(file), StandardCharsets.UTF_8)) {
            Gson gson = new Gson();
            return gson.fromJson(reader, typeOfT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String courseHandlerFileName = "courseHandler.json";

    public static void saveCourseHandler(CourseHandler courseHandler) {
        toJson(IceRush.getPlugin(), courseHandler, courseHandlerFileName);
    }

    public static CourseHandler loadCourseHandler() {
        Object object = fromJson(IceRush.getPlugin(), courseHandlerFileName, CourseHandler.class);
        if (object == null) {
            CourseHandler courseHandler = new CourseHandler();
            saveCourseHandler(courseHandler);
            return courseHandler;
        }
        return (CourseHandler) object;
    }

}
