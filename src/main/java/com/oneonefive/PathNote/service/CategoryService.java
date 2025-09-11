package com.oneonefive.PathNote.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oneonefive.PathNote.dto.CategoryDTO;
import com.oneonefive.PathNote.entity.Category;
import com.oneonefive.PathNote.repository.CategoryRepository;

import jakarta.transaction.Transactional;

@Service
public class CategoryService {
    
    @Autowired
    private CategoryRepository categoryRepository;

    // 카테고리 유무 조회
    @Transactional
    public boolean getHasCategoryByContent(String content) {
        return categoryRepository.existsByContent(content);
    }

    // 카테고리 전체 조회
    @Transactional
    public List<CategoryDTO> getAllCategories() {
        List<CategoryDTO> categoryDTOs = new ArrayList();
        for (Category category : categoryRepository.findAll()) {
            CategoryDTO categoryDTO = new CategoryDTO(
                category.getCategoryId(),
                category.getContent()
            );
            categoryDTOs.add(categoryDTO);
        }
        return categoryDTOs;
    }

    // 카테고리 엔티티 불러오기
    @Transactional
    public Category getAndCreateCategory(String content) {
        Category category = categoryRepository.findCategoryByContent(content);
        if (category != null) {
            return category;
        }
        else {
            category = new Category();
            category.setContent(content);
            return categoryRepository.save(category);
        }
    }

    // 카테고리 단건 조회 및 생성
    @Transactional
    public CategoryDTO findAndCreateCategory(String content) {
        Category category = categoryRepository.findCategoryByContent(content);
        if (category != null) {
            CategoryDTO categoryDTO = new CategoryDTO(category.getCategoryId(), category.getContent());
            return categoryDTO;
        }
        else {
            category = new Category();
            category.setContent(content);
            category = categoryRepository.save(category);
            CategoryDTO categoryDTO = new CategoryDTO(category.getCategoryId(), category.getContent());
            return categoryDTO;
        }
    }
}
