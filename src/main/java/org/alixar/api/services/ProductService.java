package org.alixar.api.services;

import lombok.RequiredArgsConstructor;
import org.alixar.api.dtos.FilterDTO;
import org.alixar.api.dtos.ProductDTO;
import org.alixar.api.dtos.ResponseDTO;
import org.alixar.api.entities.Product;
import org.alixar.api.exceptions.ResourceNotFoundException;
import org.alixar.api.mappers.ProductMapper;
import org.alixar.api.repositories.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    private final FileStorageService fileStorageService;

    public ResponseDTO<List<ProductDTO>> list(FilterDTO filterDTO) {
        Pageable pageable = PageRequest.of(filterDTO.getPage() - 1, filterDTO.getItemsPerPage(),
                "desc".equals(filterDTO.getOrderBy()) ? Sort.by(filterDTO.getOrder()).descending() : Sort.by(filterDTO.getOrder()).ascending());

        Page<Product> products = (filterDTO.getSearch() != null && !filterDTO.getSearch().isBlank())
                ? productRepository.findByNameContainingIgnoreCase(filterDTO.getSearch(), pageable)
                : productRepository.findAll(pageable);

        filterDTO.setTotalPages(products.getTotalPages());

        List<ProductDTO> data = products.getContent().stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseDTO.success("Listado obtenido", data, filterDTO);
    }

    public ResponseDTO<ProductDTO> getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto con ID " + id + " no encontrado"));

        return ResponseDTO.success("Producto obtenido", productMapper.toDTO(product));
    }

    public ResponseDTO<ProductDTO> saveProduct(ProductDTO productDTO) {
        if (productRepository.findByName(productDTO.getName()).isPresent()) {
            return ResponseDTO.error(409, "El nombre del producto ya existe");
        }

        Product savedProduct = productRepository.save(productMapper.toEntity(productDTO));
        return ResponseDTO.success("Producto guardado", productMapper.toDTO(savedProduct));
    }

    public ResponseDTO<Void> deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            return ResponseDTO.error(404, "No se puede eliminar: el producto no existe");
        }
        productRepository.deleteById(id);
        return ResponseDTO.success("Producto eliminado", null);
    }

    public ResponseDTO<String> upload(MultipartFile file, Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (file == null || file.isEmpty()) {
            return ResponseDTO.error(400, "El archivo es obligatorio");
        }

        String filename = fileStorageService.saveFile(file);
        product.setImage(filename);
        productRepository.save(product);

        return ResponseDTO.success("Imagen actualizada", filename);
    }

    public ResponseDTO<Void> deleteProductImage(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (product.getImage() != null) {
            fileStorageService.deleteFile(product.getImage());
            product.setImage(null);
            productRepository.save(product);
        }

        return ResponseDTO.success("Imagen eliminada", null);
    }
}
