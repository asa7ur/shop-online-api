package org.alixar.api.controllers;

import jakarta.validation.Valid;
import org.alixar.api.dtos.FilterDTO;
import org.alixar.api.dtos.ProductDTO;
import org.alixar.api.dtos.ResponseDTO;
import org.alixar.api.services.FileStorageService;
import org.alixar.api.services.ProductService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/")
    public ResponseEntity<@NotNull ResponseDTO> list(@RequestBody FilterDTO filterDTO) {
        logger.info("Solicitando la lista de todos los productos ...");
        try {
            return ResponseEntity.ok().body(productService.list(filterDTO));
        } catch (Exception e) {
            logger.error("Error al listar los productos: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<@NotNull ResponseDTO> getProductById(@PathVariable Long id) {
        logger.info("Solicitando el producto con id: {}", id);
        try {
            return ResponseEntity.ok().body(productService.getProductById(id));
        } catch (Exception e) {
            logger.error("Error al obtener el producto: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping(value = "/save", consumes = "multipart/form-data")
    public ResponseEntity<@NotNull ResponseDTO> save(@Valid @RequestBody ProductDTO productDTO) {
        logger.info("Insertando nuevo producto con nombre : {}", productDTO.getName());
        try {
            return ResponseEntity.ok().body(productService.saveProduct(productDTO));
        } catch (Exception e) {
            logger.error("Error al crear el producto: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<@NotNull ResponseDTO> deleteProduct(@PathVariable Long id) {
        logger.info("Eliminando el producto con id: {}", id);
        return ResponseEntity.ok().body(productService.deleteProduct(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping(value = "/{id}/upload", consumes = "multipart/form-data")
    public ResponseEntity<?> upload(@PathVariable Long id, @RequestParam("image") MultipartFile image) {
        logger.info("Insertando nueva imagen de producto con id : {}", id);
        try {
            return productService.upload(image, id);
        } catch (Exception e) {
            logger.error("Error al insertar la imagen del producto: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @RequestMapping(value = "/{image}/image")
    public ResponseEntity<?> readImage(@PathVariable String image) {
        logger.info("Obteniendo imagen de producto: {}", image);
        try {
            return fileStorageService.readImage(image);
        } catch (Exception e) {
            logger.error("Error al insertar la imagen del producto: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{id}/image")
    public ResponseEntity<?> deleteProductImage(@PathVariable Long id) {
        logger.info("Eliminando la imagen producto con id: {}", id);
        return productService.deleteProductImage(id);
    }
}
