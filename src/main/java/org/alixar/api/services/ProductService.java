package org.alixar.api.services;

import org.alixar.api.dtos.FilterDTO;
import org.alixar.api.dtos.ProductDTO;
import org.alixar.api.dtos.ResponseDTO;
import org.alixar.api.entities.Product;
import org.alixar.api.mappers.ProductMapper;
import org.alixar.api.repositories.ProductRepository;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
public class ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private FileStorageService fileStorageService;

    public ResponseDTO list(FilterDTO filterDTO) {
        try {
            Pageable pageable = PageRequest.of(filterDTO.getPage() - 1, filterDTO.getItemsPerPage(),
                    "desc".equals(filterDTO.getOrderBy()) ? Sort.by(filterDTO.getOrder()).descending() : Sort.by(filterDTO.getOrder()).ascending());
            Page<@NotNull Product> products;
            if (filterDTO.getSearch() != null && !filterDTO.getSearch().isBlank()) {
                products = productRepository.findByNameContainingIgnoreCase(filterDTO.getSearch(), pageable);
                filterDTO.setTotalPages((int) Math.ceil((double) productRepository.countByNameContainingIgnoreCase(filterDTO.getSearch()) / filterDTO.getItemsPerPage()));
            } else {
                products = productRepository.findAll(pageable);
                filterDTO.setTotalPages((int) Math.ceil((double) productRepository.count() / filterDTO.getItemsPerPage()));
            }
            return new ResponseDTO(200, "Listado de productos", products.stream().map(productMapper::toDTO), filterDTO);
        } catch (Exception e) {
            logger.error("Error al obtener todos los productos: {}", e.getMessage());
            throw new RuntimeException("Error al obtener todos los productos", e);
        }
    }

    public ResponseDTO getProductById(Long id) {
        Optional<ProductDTO> productDTOOpt = Optional.empty();
        try {
            Optional<Product> product = productRepository.findById(id);
            if (product.isPresent()) {
                productDTOOpt = Optional.of(productMapper.toDTO(product.get()));
            } else {
                logger.info("No se encontró el producto con id: {}", id);
            }
            if (productDTOOpt.isPresent()) {
                return new ResponseDTO(200, "Producto obtenido", productDTOOpt.get());
            }
            return new ResponseDTO(404, "No se encontró el producto con id:" + id, null);
        } catch (Exception e) {
            logger.error("Error al buscar el producto: {}", e.getMessage());
            throw new RuntimeException("Error al buscar el producto", e);
        }
    }

    public ResponseDTO saveProduct(ProductDTO productDTO) {
        try {
            if (productRepository.findByName(productDTO.getName()).isPresent()) {
                logger.warn("Error al insertar un producto");
                return new ResponseDTO(409, "Error al crear el producto. Ya existe un producto con el mismo nombre.", null);
            }
            Product savedProduct = productRepository.save(productMapper.toEntity(productDTO));
            logger.info("Producto creado satisfactoriamente con id: {}", savedProduct.getId());
            return new ResponseDTO(200, "Producto insertado correctamente", productMapper.toDTO(savedProduct));
        } catch (Exception e) {
            logger.error("Error al crear el producto: {}", e.getMessage());
            return new ResponseDTO(500, "Error al crear el producto", null);
        }
    }

    public ResponseDTO deleteProduct(Long id) {
        try {
            if (productRepository.findById(id).isPresent()) {
                productRepository.deleteById(id);
                logger.info("Eliminada el producto con id: {} satisfactoriamente", id);
                return new ResponseDTO(200, "Producto eliminado correctamente", null);
            } else {
                return new ResponseDTO(500, "Error al eliminar el producto", null);
            }
        } catch (Exception e) {
            logger.error("Error al eliminar el producto: {}", e.getMessage());
            return new ResponseDTO(500, "Error al eliminar el producto", null);
        }
    }

    public ResponseEntity<?> upload(MultipartFile file, Long id) {
        String filename = null;
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            if (file != null && !file.isEmpty()) {
                filename = fileStorageService.saveFile(file);
                if (filename == null) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
                }
                product.setImage(filename);
                productRepository.save(product);
                return ResponseEntity.ok("Imagen subida correctamente");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public ResponseEntity<?> deleteProductImage(Long id) {
        try {
            if (productRepository.findById(id).isPresent()) {
                Optional<Product> productOPT = productRepository.findById(id);
                if (productOPT.isPresent()) {
                    fileStorageService.deleteFile(productOPT.get().getImage());
                    productOPT.get().setImage(null);
                    productRepository.save(productOPT.get());
                }
                logger.info("Eliminada la imagen del producto con id: {} satisfactoriamente", id);
                return ResponseEntity.ok().body(null);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error al eliminar la imagen del producto: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
