package com.oneonefive.PathNote.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oneonefive.PathNote.dto.CategoryDTO;
import com.oneonefive.PathNote.service.CategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    // GET /api/categories
    // 카테고리 전체 조회
    @GetMapping
    public List<CategoryDTO> getAllCategories() {
        return categoryService.getAllCategories();
    }

    // GET /api/categories/{category_name}
    // 카테고리 시작하는 글자로 조회
    @GetMapping("/{category_name}")
    public List<CategoryDTO> getCategoriesByContent(@PathVariable("category_name") String category_name) {
        return categoryService.getCategoreisStartingWith(category_name);
    }
}
