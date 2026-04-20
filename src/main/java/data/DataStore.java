package data;

import model.Room;
import model.Sensor;
import model.SensorReading;


import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


public class DataStore {
    private static final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private static final Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    private static final Map<String, List<SensorReading>> sensorReadings = new ConcurrentHashMap<>();

    static {
        Room room1 = new Room("LIB-301", "Library Quiet Study", 30);
        Room room2 = new Room("ENG-101", "Engineering Lab 1", 25);
        rooms.put(room1.getId(), room1);
        rooms.put(room2.getId(), room2);
    }

    private DataStore(){

    }

    public static Map<String, Room> getRooms() {
        return rooms;
    }

    public static Room getRoom(String id) {
        return rooms.get(id);
    }

    public static void addRoom(Room room) {
        rooms.put(room.getId(), room);
    }

    public static Room removeRoom(String id) {
        return rooms.remove(id);
    }

    public static boolean roomExists(String id) {
        return rooms.containsKey(id);
    }

    public static Map<String, Sensor> getSensors() {
        return sensors;
    }

    public static Sensor getSensor(String id) {
        return sensors.get(id);
    }

    public static void addSensor(Sensor sensor) {
        sensors.put(sensor.getId(), sensor);
        sensorReadings.putIfAbsent(sensor.getId(), new CopyOnWriteArrayList<>());
    }

    public static Sensor removeSensor(String id) {
        sensorReadings.remove(id);
        return sensors.remove(id);
    }

    public static boolean sensorExists(String id) {
        return sensors.containsKey(id);
    }

    public static List<SensorReading> getReadings(String sensorId) {
        return sensorReadings.computeIfAbsent(sensorId, k -> new CopyOnWriteArrayList<>());
    }

    public static void addReading(String sensorId, SensorReading reading) {
        sensorReadings.computeIfAbsent(sensorId, k -> new CopyOnWriteArrayList<>()).add(reading);
    }
}
