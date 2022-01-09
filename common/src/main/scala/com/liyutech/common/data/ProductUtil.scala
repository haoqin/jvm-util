package com.liyutech.common.data

object ProductUtil {
  implicit class ProductDecorator(product: Product) {
    // Whether or not the current product is a projection of the specified product, ie, if the latter contain all fields
    // of the former.
    def isProjectionOf(anotherProduct: Product): Boolean = {
      (0 until product.productArity) forall { fieldIndex =>
        val fieldValue = product.productElement(fieldIndex)
        val fieldName = product.productElementName(fieldIndex)
        val anotherFieldIndex = anotherProduct.productElementNames.indexOf(fieldName)
        val anotherFieldValue = anotherProduct.productElement(anotherFieldIndex)
        fieldValue == anotherFieldValue
      }
    }
  }
}