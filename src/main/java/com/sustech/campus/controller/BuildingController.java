package com.sustech.campus.controller;

import com.sustech.campus.entity.Building;
import com.sustech.campus.entity.BuildingPhoto;
import com.sustech.campus.enums.UserType;
import com.sustech.campus.interceptor.Access;
import com.sustech.campus.service.BuildingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/building")
public class BuildingController {

    private final BuildingService buildingService;

    private static final Map<String, MediaType> MEDIA_TYPE_MAP;

    static {
        // Initialize the media type map
        MEDIA_TYPE_MAP = new HashMap<>();
        MEDIA_TYPE_MAP.put("png", MediaType.IMAGE_PNG);
        MEDIA_TYPE_MAP.put("jpg", MediaType.IMAGE_JPEG);
        MEDIA_TYPE_MAP.put("jpeg", MediaType.IMAGE_JPEG);
        MEDIA_TYPE_MAP.put("gif", MediaType.IMAGE_GIF);
        // Add more mappings as necessary
    }

    @Autowired
    public BuildingController(BuildingService buildingService) {
        this.buildingService = buildingService;
    }

    @GetMapping("/")
    public ResponseEntity<List<Building>> getAllBuildings() {
        List<Building> buildings = buildingService.listAllBuildings();
//        for(Building building : buildings){
//            String path="localhost:8082/building/image/"+buildingService.listBuildingPhotos(building.getId()).get(0).getPath();
//            building.setCoverPath(path);
//        }
        return ResponseEntity.ok(buildings);
    }

    private static final String IMAGE_FOLDER = System.getProperty("user.dir")+"/images/";
//    @PostMapping("/upload")
//    public String uploadImage(@RequestParam("image") MultipartFile imageFile) {
//        // 确保上传文件不为空
//        if (imageFile.isEmpty()) {
//            return "The file is empty.";
//        }
//        try {
//            // 获取文件名并构建本地文件路径
//            String fileName = imageFile.getOriginalFilename();
//            Path path = Paths.get(IMAGE_FOLDER + fileName);
//            // 确保存储路径存在
//            if (!Files.exists(path)) {
//                Files.createDirectories(path.getParent());
//            }
//            // 将上传的文件写入到指定的文件中
//            Files.copy(imageFile.getInputStream(), path);
//            return "You successfully uploaded " + fileName + "!";
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "Failed to upload " + imageFile.getOriginalFilename() + " due to " + e.getMessage();
//        }
//    }
    @Access(level = UserType.ADMIN)
    @PostMapping("/uploadimage")
    public String uploadImage(MultipartHttpServletRequest request) {
        Iterator<String> fileNames = request.getFileNames();
        while (fileNames.hasNext()) {
            String fileName = fileNames.next();
            MultipartFile file = request.getFile(fileName);
            if (file != null && !file.isEmpty()) {
                try {
                    String originalFileName = file.getOriginalFilename();
                    Path path = Paths.get(originalFileName);
                    if (!Files.exists(path)) {
                        Files.createDirectories(path.getParent());
                    }
                    Files.copy(file.getInputStream(), path);

                    Building building=new Building();
                    BuildingPhoto buildingPhoto=new BuildingPhoto();
                    buildingService.addBuildingPhoto(building.getId(),path.toString());

                    return "Successfully uploaded " + originalFileName + "!";
                } catch (IOException e) {
                    e.printStackTrace();
                    return "Failed to upload " + file.getOriginalFilename() + " due to " + e.getMessage();
                }
            } else {
                return "The file is empty.";
            }
        }
        return "No files were uploaded.";
    }

    private String getFileExtension(String filename) {
        if (filename.contains(".")) {
            return filename.substring(filename.lastIndexOf(".") + 1);
        } else {
            return ""; // No extension found
        }
    }
    @GetMapping("/image/{subpath}")
    public ResponseEntity<byte[]> getImageAsResponseEntity(@PathVariable String subpath) throws IOException {
        String path=IMAGE_FOLDER+subpath;
//        Resource image = new ClassPathResource(path);
//        byte[] imageContent = Files.readAllBytes(image.getFile().toPath());
        Path imagePath = Paths.get(path);
        if (!Files.exists(imagePath))
            return ResponseEntity.ok(null);
        byte[] imageContent = Files.readAllBytes(imagePath);
        String fileExtension = getFileExtension(subpath);
        MediaType mediaType = MEDIA_TYPE_MAP.getOrDefault(fileExtension.toLowerCase(), MediaType.APPLICATION_OCTET_STREAM);
        return ResponseEntity.ok().contentType(mediaType).body(imageContent);
    }

    @GetMapping("/buildingCover")
    public ResponseEntity<List<BuildingCover>> getAllBuildingsCover() {
        List<Building> buildings = buildingService.listAllBuildings();
        List<BuildingCover> buildingCovers = new ArrayList<>();

        for (Building building : buildings) {
            BuildingCover cover = new BuildingCover();
            cover.setId(building.getId());
            cover.setName(building.getName());
            cover.setLocation(new double[]{building.getLongitude(), building.getLatitude()});
            cover.setCategory(building.getTag());
            cover.setIntroduction(building.getDescription());

            List<BuildingPhoto> photos = buildingService.listBuildingPhotos(building.getId());
            if (photos != null && !photos.isEmpty()) {
                cover.setPath("http://localhost:8082/building/image/"+photos.get(0).getPath());
            }
            buildingCovers.add(cover);
        }
        return ResponseEntity.ok(buildingCovers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Building> getBuildingByName(@PathVariable String id) {
        System.out.println(id);
        Building building = buildingService.getBuildingById(Long.valueOf(id));
        if (building != null) {
            if (!buildingService.listBuildingPhotos((building.getId())).isEmpty()) {
                building.setCoverPath(
                        "http://localhost:8082/building/image/" +
                                buildingService.listBuildingPhotos((building.getId())).get(0).getPath()
                );
            }
            return ResponseEntity.ok(building);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Access(level = UserType.ADMIN)
    @PostMapping("/add")
    public ResponseEntity<String> addBuilding(@RequestBody Building building) {
        Long buildingId = buildingService.addBuilding(building.getName(), building.getTag(), building.getDescription(), building.getDetails(), building.getLatitude(), building.getLongitude());
        if (buildingId != null) {
            return ResponseEntity.ok("Building added successfully.");
        } else {
            return ResponseEntity.badRequest().body("Building already exists.");
        }
    }

    @Access(level = UserType.ADMIN)
    @PostMapping("/addBuilding")
    public String addBuilding(
            @RequestParam("name") String name,
            @RequestParam("tag") String tag,
            @RequestParam("description") String description,
            @RequestParam("details") String details,
            @RequestParam("latitude") Double latitude,
            @RequestParam("longitude") Double longitude,
            @RequestParam("file") MultipartFile file) {
        System.out.println("Received data - Name: " + name + ", Tag: " + tag + ", Description: " + description
                + ", Details: " + details + ", Latitude: " + latitude + ", Longitude: " + longitude);
        System.out.println(file);
        if (file!=null && !file.isEmpty()) {
            try {
//                saveFile(IMAGE_FOLDER, file.getOriginalFilename(), file);
                String originalFileName = file.getOriginalFilename();
//                Path path = Paths.get(originalFileName);
                Path filepath = Paths.get(IMAGE_FOLDER, originalFileName); // 构建文件保存路径
                // 确保目标目录存在
                Files.createDirectories(filepath.getParent());
                // 保存文件
                Path abPath=Path.of(IMAGE_FOLDER+originalFileName);
                file.transferTo(abPath);

                Long buildingId =buildingService.addBuilding(name,tag,description,details,latitude,longitude);
                buildingService.addBuildingPhoto(buildingId,originalFileName);

                return "Building added successfully with the name: " + name;
            } catch (IOException e) {
                e.printStackTrace();
                return "Failed to upload the file due to " + e.getMessage();
            }
        } else {
            return "No file uploaded";
        }
    }
    private void saveFile(String uploadDir, String fileName, MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        try {
            Path filePath = uploadPath.resolve(fileName);
            file.transferTo(filePath);
        } catch (IOException e) {
            throw new IOException("Could not save file: " + fileName, e);
        }
    }

    @Access(level = UserType.ADMIN)
    @PostMapping("/edit")
    public ResponseEntity<String> editBuilding(@RequestBody Building building) {
//        Long buildingId = buildingService.addBuilding(building.getName(), building.getTag(), building.getDescription(), building.getDetails(), building.getLatitude(), building.getLongitude());
        Building responseBuilding=buildingService.getBuildingById(building.getId());
        boolean success=buildingService.edit(responseBuilding.getId(), building.getName(), building.getTag(), building.getDescription(), building.getDetails(), building.getLatitude(), building.getLongitude());
        if (responseBuilding != null && success) {
            return ResponseEntity.ok("Building added successfully.");
        } else {
            return ResponseEntity.badRequest().body("Building already exists.");
        }
    }

    @Access(level = UserType.ADMIN)
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteBuilding(@PathVariable long id) {
        boolean success = buildingService.deleteBuilding(id);
        if (success) {
            return ResponseEntity.ok("Building deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Building not found.");
        }
    }

    @Access(level = UserType.ADMIN)
    @PutMapping("/updateLocation/{id}")
    public ResponseEntity<String> changeBuildingLocation(@PathVariable long id, @RequestParam double latitude, @RequestParam double longitude) {
        boolean success = buildingService.changeBuildingLocation(id, latitude, longitude);
        if (success) {
            return ResponseEntity.ok("Building location updated successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Building not found.");
        }
    }

    @Access(level = UserType.ADMIN)
    @PutMapping("/updateDescription/{id}")
    public ResponseEntity<String> changeBuildingDescription(@PathVariable long id, @RequestBody String description) {
        boolean success = buildingService.changeBuildingDescription(id, description);
        if (success) {
            return ResponseEntity.ok("Building description updated successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Building not found.");
        }
    }

    @Access(level = UserType.ADMIN)
    @PutMapping("/updateTag/{id}")
    public ResponseEntity<String> changeBuildingTag(@PathVariable long id, @RequestBody String tag) {
        boolean success = buildingService.changeBuildingTag(id, tag);
        if (success) {
            return ResponseEntity.ok("Building tag updated successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Building not found.");
        }
    }

    @GetMapping("/photos/{buildingId}")
    public ResponseEntity<List<BuildingPhoto>> listBuildingPhotos(@PathVariable long buildingId) {
        List<BuildingPhoto> photos = buildingService.listBuildingPhotos(buildingId);
        if (photos != null && !photos.isEmpty()) {
            return ResponseEntity.ok(photos);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    static class BuildingCover{
        private Long id;
        private String name;
        private double[] location;
        private String category;
        private String introduction;
        private String path;
        public Long getId() {
            return id;
        }
        public void setId(Long id) {
            this.id = id;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public double[] getLocation() {
            return location;
        }
        public void setLocation(double[] location) {
            this.location = location;
        }
        public String getCategory() {
            return category;
        }
        public void setCategory(String category) {
            this.category = category;
        }
        public String getIntroduction() {
            return introduction;
        }
        public void setIntroduction(String introduction) {
            this.introduction = introduction;
        }
        public String getPath() {
            return path;
        }
        public void setPath(String path) {
            this.path = path;
        }
        public BuildingCover(){}
    }
}