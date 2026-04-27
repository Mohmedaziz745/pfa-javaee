package com.fsm.shopflw.service;

import com.fsm.shopflw.dto.common.CategoryTreeResponse;
import com.fsm.shopflw.exception.NotFoundException;
import com.fsm.shopflw.model.Category;
import com.fsm.shopflw.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final MapperService mapperService;

    @Transactional(readOnly = true)
    public List<CategoryTreeResponse> getTree() {
        return categoryRepository.findByParentIsNull().stream().map(mapperService::toCategoryTreeResponse).toList();
    }

    @Transactional
    public CategoryTreeResponse create(String nom, String description, Long parentId) {
        Category parent = parentId == null ? null : categoryRepository.findById(parentId)
                .orElseThrow(() -> new NotFoundException("Categorie parente introuvable"));
        Category saved = categoryRepository.save(Category.builder().nom(nom).description(description).parent(parent).build());
        return mapperService.toCategoryTreeResponse(saved);
    }

    @Transactional
    public CategoryTreeResponse update(Long id, String nom, String description, Long parentId) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Categorie introuvable"));
        category.setNom(nom);
        category.setDescription(description);
        if (parentId != null) {
            category.setParent(categoryRepository.findById(parentId).orElseThrow(() -> new NotFoundException("Categorie parente introuvable")));
        } else {
            category.setParent(null);
        }
        return mapperService.toCategoryTreeResponse(category);
    }

    @Transactional
    public void delete(Long id) {
        categoryRepository.delete(categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Categorie introuvable")));
    }
}
