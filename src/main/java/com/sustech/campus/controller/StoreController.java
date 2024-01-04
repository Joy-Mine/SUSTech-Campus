package com.sustech.campus.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sustech.campus.entity.Goods;
import com.sustech.campus.entity.GoodsPhoto;
import com.sustech.campus.entity.Store;
import com.sustech.campus.enums.UserType;
import com.sustech.campus.interceptor.Access;
import com.sustech.campus.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/store")
public class StoreController {

    private final StoreService storeService;

    @Autowired
    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping("/")
    public ResponseEntity<List<Store>> listAllStores() {
        List<Store> stores = storeService.listAllStores();
        return ResponseEntity.ok(stores);
    }

//    private static final String IMAGE_FOLDER = "D:\\ProProject\\OOAD\\campus\\images\\";
    private static final String IMAGE_FOLDER = System.getProperty("user.dir")+"/images/";
//    @GetMapping("/image/{subpath}")
//    public ResponseEntity<byte[]> getGoodsImage(@PathVariable String subpath) throws IOException {
//        String path=IMAGE_FOLDER+subpath;
//        Resource image = new ClassPathResource(path);
//        byte[] imageContent = Files.readAllBytes(image.getFile().toPath());
//        String fileExtension = getFileExtension(subpath);
//        MediaType mediaType = MEDIA_TYPE_MAP.getOrDefault(fileExtension.toLowerCase(), MediaType.APPLICATION_OCTET_STREAM);
//        return ResponseEntity.ok().contentType(mediaType).body(imageContent);
//    }
    @GetMapping("/cuisine")
    public ResponseEntity<List<Goods>> listAllCuisines() {
        List<Goods> cuisines=storeService.listAllGoods(Long.valueOf(1));
        for(Goods cuisine : cuisines){
            String coverPath=storeService.listAllPhotosOfaGood(cuisine.getId()).get(0).getPath();
            cuisine.setImage("http://localhost:8082/store/image/"+coverPath);
        }
        return ResponseEntity.ok(cuisines);
    }
    @GetMapping("/product")
    public ResponseEntity<List<Goods>> listAllProducts() {
        List<Goods> products=storeService.listAllGoods(Long.valueOf(2));
        for(Goods product : products){
            String coverPath=storeService.listAllPhotosOfaGood(product.getId()).get(0).getPath();
            product.setImage("http://localhost:8082/store/image/"+coverPath);
        }
        return ResponseEntity.ok(products);
    }


    @Access(level = UserType.ADMIN)
    @PostMapping("/add")
    public ResponseEntity<String> addStore(@RequestBody Store store) {
        Long storeId = storeService.addStore(store.getName());
        if (storeId != null) {
            return ResponseEntity.ok("Store added successfully.");
        } else {
            return ResponseEntity.badRequest().body("Store already exists.");
        }
    }

    @Access(level = UserType.ADMIN)
    @DeleteMapping("/delete/{storeId}")
    public ResponseEntity<String> deleteStore(@PathVariable Long storeId) {
        boolean success = storeService.deleteStore(storeId);
        if (success) {
            return ResponseEntity.ok("Store deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Store not found.");
        }
    }

    @Access(level = UserType.USER)
    @GetMapping("/goods/{storeId}")
    public ResponseEntity<String> listAllGoods(
            @PathVariable Long storeId, @RequestParam(required = false) Boolean includeHidden
    ) throws JsonProcessingException {
        if (includeHidden == null) {
            includeHidden = false;
        }
        List<Goods> goods = storeService.listAllGoods(storeId, includeHidden);
        if (goods != null && !goods.isEmpty()) {
            goods = goods.stream()
                    .filter(Objects::nonNull)
                    .peek(
                            e -> e.setPhotos(
                                    storeService.listAllPhotosOfaGood(e.getId()).stream()
                                            .peek(e1 -> e1.setGoodsId(null))
                                            .peek(e1 -> e1.setId(null))
                                            .peek(e1 -> e1.setPath("http://localhost:8082/store/image/" + e1.getPath()))
                                            .toList()
                            )
                    )
                    .peek(e -> e.setStoreId(null))
                    .toList();
            ObjectMapper objectMapper = new ObjectMapper();
            return ResponseEntity.ok(objectMapper.writeValueAsString(goods));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

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
    private String getFileExtension(String filename) {
        if (filename.contains(".")) {
            return filename.substring(filename.lastIndexOf(".") + 1);
        } else {
            return ""; // No extension found
        }
    }
    @GetMapping("/image/{subpath}")
    public ResponseEntity<byte[]> getImageAsResponseEntity(@PathVariable String subpath) throws IOException {
//        String path=IMAGE_FOLDER+subpath;
//        Resource image = new ClassPathResource(path);
//        byte[] imageContent = Files.readAllBytes(image.getFile().toPath());
        String path = IMAGE_FOLDER + subpath;
        Path imagePath = Paths.get(path);
        if (!Files.exists(imagePath))
            return ResponseEntity.ok(null);
        byte[] imageContent = Files.readAllBytes(imagePath);
        String fileExtension = getFileExtension(subpath);
        MediaType mediaType = MEDIA_TYPE_MAP.getOrDefault(fileExtension.toLowerCase(), MediaType.APPLICATION_OCTET_STREAM);
        return ResponseEntity.ok().contentType(mediaType).body(imageContent);
    }

//    @Access(level = UserType.ADMIN)
//    @PostMapping("/goods/add")
//    public ResponseEntity<String> addGoods(@RequestBody Goods goods) {
//        Long goodsId = storeService.addGoods(goods.getStoreId(), goods.getName(), goods.getPrice(), goods.getQuantity());
//        if (goodsId != null) {
//            return ResponseEntity.ok("Goods added successfully. ID: " + goodsId);
//        } else {
//            return ResponseEntity.badRequest().body("Error adding goods.");
//        }
//    }
    @Access(level = UserType.ADMIN)
    @PostMapping("/addGood/{storeId}")
    public String addGoods(
            @PathVariable long storeId,
            @RequestParam("name") String name,
            @RequestParam("price") BigDecimal price,
            @RequestParam("quantity") Integer quantity,
            @RequestParam("image") MultipartFile image) {
        System.out.println(image);
        if (image!=null && !image.isEmpty()) {
            try {
                String originalFileName = image.getOriginalFilename();
//                Path filepath = Paths.get(IMAGE_FOLDER, originalFileName); // 构建文件保存路径
//                // 确保目标目录存在
//                Files.createDirectories(filepath.getParent());
//                // 保存文件
                Path abPath=Path.of(IMAGE_FOLDER+originalFileName);
                System.out.println(abPath);
                image.transferTo(abPath);

                Long goodsId =storeService.addGoods(storeId,name,price,quantity);
                storeService.addGoodsPhoto(goodsId, originalFileName);

                return "Added: " + name;
            } catch (IOException e) {
                e.printStackTrace();
                return "Failed to upload the file due to " + e.getMessage();
            }
        } else {
            return "No file uploaded";
        }
    }
    @Access(level = UserType.ADMIN)
    @PostMapping("/goods/delete")
    public ResponseEntity<String> deleteGoods(@RequestBody String goodsId) {
        boolean success = storeService.fakeDeleteGoods(Long.valueOf(goodsId));
        if (success) {
            return ResponseEntity.ok("Goods deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Goods not found.");
        }
    }
    @Access(level = UserType.ADMIN)
    @PostMapping("/edit")
    public boolean editGoods(
            @RequestParam("id") String goodsId,
            @RequestParam("name") String name,
            @RequestParam("price") BigDecimal price,
            @RequestParam("quantity") Integer quantity){
//            try {
                Long id=Long.valueOf(goodsId);
                return storeService.editGoods(id, name, price, quantity);
//                storeService.deleteGoodsPhoto(deletePathId);
//                storeService.addGoodsPhoto(id, originalFileName);

//            } catch (IOException e) {
//                e.printStackTrace();
//                return "Failed to upload the file due to " + e.getMessage();
//            }
    }


    @Access(level = UserType.ADMIN)
    @PatchMapping("/goods/change-price/{goodsId}")
    public ResponseEntity<String> changeGoodsPrice(@PathVariable Long goodsId, @RequestParam BigDecimal newPrice) {
        boolean success = storeService.changeGoodsPrice(goodsId, newPrice);
        if (success) {
            return ResponseEntity.ok("Goods price updated successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Goods not found.");
        }
    }

    @Access(level = UserType.ADMIN)
    @PatchMapping("/goods/change-quantity/{goodsId}")
    public ResponseEntity<String> changeGoodsQuantity(@PathVariable Long goodsId, @RequestParam Integer newQuantity) {
        boolean success = storeService.changeGoodsQuantity(goodsId, newQuantity);
        if (success) {
            return ResponseEntity.ok("Goods quantity updated successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Goods not found.");
        }
    }

    @Access(level = UserType.ADMIN)
    @PostMapping("/goods/photo/add")
    public ResponseEntity<String> addGoodsPhoto(@RequestParam Long goodsId, @RequestParam String photoPath) {
        Long photoId = storeService.addGoodsPhoto(goodsId, photoPath);
        if (photoId != null) {
            return ResponseEntity.ok("Goods photo added successfully. ID: " + photoId);
        } else {
            return ResponseEntity.badRequest().body("Error adding goods photo.");
        }
    }

    @Access(level = UserType.ADMIN)
    @GetMapping("/goods/photo/{goodsId}")
    public ResponseEntity<List<GoodsPhoto>> listAllPhotosOfaGood(@PathVariable Long goodsId) {
        List<GoodsPhoto> photos = storeService.listAllPhotosOfaGood(goodsId);
        if (photos != null && !photos.isEmpty()) {
            return ResponseEntity.ok(photos);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Access(level = UserType.ADMIN)
    @DeleteMapping("/goods/photo/delete/{photoId}")
    public ResponseEntity<String> deleteGoodsPhoto(@PathVariable Long photoId) {
        boolean success = storeService.deleteGoodsPhoto(photoId);
        if (success) {
            return ResponseEntity.ok("Goods photo deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Goods photo not found.");
        }
    }

}
