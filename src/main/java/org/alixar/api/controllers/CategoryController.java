package org.alixar.api.controllers;

import org.alixar.api.dtos.CategoryDTO;
import org.alixar.api.services.CategoryService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/")
    public ResponseEntity<@NotNull List<CategoryDTO>> list() {
        logger.info("Solicitando la lista de todas las categorías...");
        try {
            List<CategoryDTO> categories = categoryService.list();
            return ResponseEntity.ok().body(categories);
        } catch (Exception e) {
            logger.error("Error al listar las categorias: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<@NotNull CategoryDTO> getCategoryById(@PathVariable Long id) {
        logger.info("Solicitando la categoría con id: {}", id);
        try {
            Optional<CategoryDTO> categoryDTO = categoryService.getCategoryById(id);
            return categoryDTO.map(dto -> ResponseEntity.ok().body(dto)).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.error("Error al obtener la categoría: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/save")
    public ResponseEntity<@NotNull CategoryDTO> save(@RequestBody CategoryDTO categoryDTO) {
        logger.info("Insertando nueva categoría con nombre : {}", categoryDTO.getName());
        try {
            return categoryService.saveCategory(categoryDTO);
        } catch (Exception e) {
            logger.error("Error al crear la categoria: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        logger.info("Eliminando la categoría con id: {}", id);
        return categoryService.deleteCategory(id);
    }
}
