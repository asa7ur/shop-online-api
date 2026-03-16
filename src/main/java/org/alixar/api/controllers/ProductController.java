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

    private <T> ResponseEntity<ResponseDTO<T>> buildResponse(ResponseDTO<T> response) {
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/")
    public ResponseEntity<ResponseDTO<List<ProductDTO>>> list(@RequestBody FilterDTO filterDTO) {
        return buildResponse(productService.list(filterDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<ProductDTO>> getProductById(@PathVariable Long id) {
        return buildResponse(productService.getProductById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping(value = "/save")
    public ResponseEntity<ResponseDTO<ProductDTO>> save(@Valid @RequestBody ProductDTO productDTO) {
        return buildResponse(productService.saveProduct(productDTO));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<Void>> deleteProduct(@PathVariable Long id) {
        return buildResponse(productService.deleteProduct(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping(value = "/{id}/upload", consumes = "multipart/form-data")
    public ResponseEntity<ResponseDTO<String>> upload(@PathVariable Long id, @RequestParam("image") MultipartFile image) {
        return buildResponse(productService.upload(image, id));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{id}/image")
    public ResponseEntity<ResponseDTO<Void>> deleteProductImage(@PathVariable Long id) {
        return buildResponse(productService.deleteProductImage(id));
    }

    // Este método es especial porque devuelve un flujo de archivo, no un JSON estándar
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
