package com.versatech.rits.service

import com.versatech.rits.entity.Product
import com.versatech.rits.repository.ProductRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductService(
    private val productRepository: ProductRepository
) {

    fun getAllProducts(): List<Product> = productRepository.findAll()

    fun getProductById(id: Long): Product? = productRepository.findById(id).orElse(null)

    @Transactional
    fun createProduct(product: Product): Product = productRepository.save(product)

    @Transactional
    fun updateProduct(id: Long, updatedProduct: Product): Product? {
        val existingProduct = getProductById(id) ?: return null
        existingProduct.name = updatedProduct.name
        existingProduct.description = updatedProduct.description
        existingProduct.price = updatedProduct.price
        return productRepository.save(existingProduct)
    }

    @Transactional
    fun deleteProduct(id: Long) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id)
        }
    }
}
