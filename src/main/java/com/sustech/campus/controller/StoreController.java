package com.sustech.campus.controller;

import com.sustech.campus.entity.Goods;
import com.sustech.campus.entity.Store;
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

    @PostMapping("/add")
    public ResponseEntity<String> addStore(@RequestBody Store store) {
        boolean success = storeService.addStore(store.getName());
        if (success) {
            return ResponseEntity.ok("Store added successfully.");
        } else {
            return ResponseEntity.badRequest().body("Store already exists.");
        }
    }

    @DeleteMapping("/delete/{storeName}")
    public ResponseEntity<String> deleteStore(@PathVariable String storeName) {
        boolean success = storeService.deleteStore(storeName);
        if (success) {
            return ResponseEntity.ok("Store deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Store not found.");
        }
    }

    @GetMapping("/goods/{storeName}")
    public ResponseEntity<List<Goods>> listAllGoods(@PathVariable String storeName) {
        List<Goods> goods = storeService.listAllGoods(storeName);
        if (goods != null && !goods.isEmpty()) {
            return ResponseEntity.ok(goods);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/goods/add")
    public ResponseEntity<String> addGoods(@RequestBody Goods goods) {
        Long goodsId = storeService.addGoods(goods.getStore(), goods.getName(), goods.getPrice(), goods.getQuantity());
        if (goodsId != null) {
            return ResponseEntity.ok("Goods added successfully. ID: " + goodsId);
        } else {
            return ResponseEntity.badRequest().body("Error adding goods.");
        }
    }

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
