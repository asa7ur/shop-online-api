package org.alixar.api.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.alixar.api.dtos.FilterDTO;
import org.alixar.api.dtos.ProductDTO;
import org.alixar.api.dtos.ResponseDTO;
import org.alixar.api.services.FileStorageService;
import org.alixar.api.services.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final FileStorageService fileStorageService;

    // Cambiado de @RequestBody a @ModelAttribute para peticiones GET estándar
    @GetMapping("/")
    public ResponseEntity<ResponseDTO<List<ProductDTO>>> list(@ModelAttribute FilterDTO filterDTO) {
        List<ProductDTO> data = productService.list(filterDTO);
        return ResponseEntity.ok(ResponseDTO.success("Listado obtenido", data, filterDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<ProductDTO>> getProductById(@PathVariable Long id) {
        ProductDTO data = productService.getProductById(id);
        return ResponseEntity.ok(ResponseDTO.success("Producto obtenido", data));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/save")
    public ResponseEntity<ResponseDTO<ProductDTO>> save(@Valid @RequestBody ProductDTO productDTO) {
        ProductDTO data = productService.saveProduct(productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDTO.success("Producto guardado", data));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ResponseDTO.success("Producto eliminado", null));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping(value = "/{id}/upload", consumes = "multipart/form-data")
    public ResponseEntity<ResponseDTO<String>> upload(@PathVariable Long id, @RequestParam("image") MultipartFile image) {
        String filename = productService.upload(image, id);
        return ResponseEntity.ok(ResponseDTO.success("Imagen actualizada", filename));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{id}/image")
    public ResponseEntity<ResponseDTO<Void>> deleteProductImage(@PathVariable Long id) {
        productService.deleteProductImage(id);
        return ResponseEntity.ok(ResponseDTO.success("Imagen eliminada", null));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping(value = "/{image}/image")
    public ResponseEntity<?> readImage(@PathVariable String image) {
        try {
            return fileStorageService.readImage(image);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}