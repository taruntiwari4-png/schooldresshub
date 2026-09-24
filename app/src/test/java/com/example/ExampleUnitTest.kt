package com.example

import com.example.data.model.AvailableVouchers
import com.example.data.model.calculateProductPricing
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun defaultFivePercentPlatformMarginCalculation() {
    val basePrice = 500.0
    val defaultMargin = 5.0
    val pricing = calculateProductPricing(basePrice, defaultMargin)

    assertEquals(500.0, pricing.basePrice, 0.001)
    assertEquals(5.0, pricing.marginPercentage, 0.001)
    assertEquals(25.0, pricing.marginAmount, 0.001)
    assertEquals(525.0, pricing.customerPrice, 0.001)
    assertEquals(500.0, pricing.sellerPayout, 0.001)
    assertEquals(25.0, pricing.platformEarnings, 0.001)
  }

  @Test
  fun customAdminConfiguredPlatformMarginCalculation() {
    val basePrice = 1200.0 // e.g. Blazer
    val customMargin = 7.5 // Admin configured margin
    val pricing = calculateProductPricing(basePrice, customMargin)

    assertEquals(1200.0, pricing.basePrice, 0.001)
    assertEquals(7.5, pricing.marginPercentage, 0.001)
    assertEquals(90.0, pricing.marginAmount, 0.001)
    assertEquals(1290.0, pricing.customerPrice, 0.001)
    assertEquals(1200.0, pricing.sellerPayout, 0.001)
    assertEquals(90.0, pricing.platformEarnings, 0.001)
  }

  @Test
  fun voucherSavingsCalculations() {
    val school10Voucher = AvailableVouchers.list.first { it.code == "SCHOOL10" }
    // 10% off with max ₹150 discount on ₹1,000 subtotal
    val cartSubtotal = 1000.0
    val savings = school10Voucher.calculateSavings(cartSubtotal)
    assertEquals(100.0, savings, 0.001)

    // Flat ₹100 discount voucher on ₹1,200 subtotal
    val uniform100Voucher = AvailableVouchers.list.first { it.code == "UNIFORM100" }
    val flatSavings = uniform100Voucher.calculateSavings(1200.0)
    assertEquals(100.0, flatSavings, 0.001)
  }

  @Test
  fun combinedVoucherAndOnlinePaymentDiscountCalculation() {
    val subtotal = 1000.0
    val voucher = AvailableVouchers.list.first { it.code == "SCHOOL10" }
    val voucherSavings = voucher.calculateSavings(subtotal) // 10% = 100.0
    val onlinePaymentDiscount = subtotal * 0.05 // 5% online payment discount = 50.0
    val totalDiscount = voucherSavings + onlinePaymentDiscount

    assertEquals(150.0, totalDiscount, 0.001)
    val finalPayable = (subtotal - totalDiscount).coerceAtLeast(0.0)
    assertEquals(850.0, finalPayable, 0.001)
  }
}

