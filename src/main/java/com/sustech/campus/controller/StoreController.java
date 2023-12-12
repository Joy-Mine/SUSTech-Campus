package com.sustech.campus.controller;

import com.sustech.campus.entity.Goods;
import com.sustech.campus.entity.Store;
import com.sustech.campus.enums.UserType;
import com.sustech.campus.interceptor.Access;
import com.sustech.campus.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
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

    @Access(level = UserType.ADMIN)
    @GetMapping("/goods/{storeId}")
    public ResponseEntity<List<Goods>> listAllGoods(@PathVariable Long storeId) {
        List<Goods> goods = storeService.listAllGoods(storeId);
        if (goods != null && !goods.isEmpty()) {
            return ResponseEntity.ok(goods);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Access(level = UserType.ADMIN)
    @PostMapping("/goods/add")
    public ResponseEntity<String> addGoods(@RequestBody Goods goods) {
        Long goodsId = storeService.addGoods(goods.getStoreId(), goods.getName(), goods.getPrice(), goods.getQuantity());
        if (goodsId != null) {
            return ResponseEntity.ok("Goods added successfully. ID: " + goodsId);
        } else {
            return ResponseEntity.badRequest().body("Error adding goods.");
        }
    }

    @Access(level = UserType.ADMIN)
    @DeleteMapping("/goods/delete/{goodsId}")
    public ResponseEntity<String> deleteGoods(@PathVariable Long goodsId) {
        boolean success = storeService.deleteGoods(goodsId);
        if (success) {
            return ResponseEntity.ok("Goods deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Goods not found.");
        }
    }
}
