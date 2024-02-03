package nl.tudelft.trustchain.offlineeuro.entity

import java.math.BigInteger

data class Receipt(
    val token: Token,
    val gamma: BigInteger,
    val challenge: BigInteger,
)

data class ReceiptEntry(
    val token: Token,
    val gamma: BigInteger,
    val challenge: BigInteger,
    val bankId: Long
)
