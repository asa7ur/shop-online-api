package org.alixar.api.services;

import org.alixar.api.dtos.CategoryDTO;
import org.alixar.api.entities.Category;
import org.alixar.api.mappers.CategoryMapper;
import org.alixar.api.repositories.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    public List<CategoryDTO> list() {
        try {
            List<Category> categories = categoryRepository.findAll();
            return categories.stream().map(categoryMapper::toDTO).toList();
        } catch (Exception e) {
            logger.error("Error al obtener todas las categorías: {}", e.getMessage());
            throw new RuntimeException("Error al obtener todas las categorías", e);
        }
    }

    public Optional<CategoryDTO> getCategoryById(Long id) {
        Optional<CategoryDTO> categoryDTOOpt = Optional.empty();
        try {
            Optional<Category> category = categoryRepository.findById(id);
            if (category.isPresent()) {
                categoryDTOOpt = Optional.of(categoryMapper.toDTO(category.get()));
            } else {
                logger.info("No se encontró la categoría con id: {}", id);
            }
            return categoryDTOOpt;
        } catch (Exception e) {
            logger.error("Error al buscar la categoría: {}", e.getMessage());
            throw new RuntimeException("Error al buscar la categoría", e);
        }
    }


    public ResponseEntity<CategoryDTO> saveCategory(CategoryDTO category) {
        try {
            if (categoryRepository.findByName(category.getName()).isPresent()) {
                logger.warn("Error al insertar una categoría");
                return ResponseEntity.badRequest().body(null);
            }
            Category savedCategory = categoryRepository.save(categoryMapper.toEntity(category));
            logger.info("Categoría creada satisfactoriamente con id: {}", category.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(categoryMapper.toDTO(savedCategory));
        } catch (Exception e) {
            logger.error("Error al crear la categoria: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public ResponseEntity<?> deleteCategory(Long id) {
        try {
            if (getCategoryById(id).isPresent()) {
                categoryRepository.deleteById(id);
                logger.info("Eliminada la categoría con id: {} satisfactoriamente", id);
                return ResponseEntity.ok().body(null);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error al eliminar la categoria: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
