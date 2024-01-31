package nl.tudelft.trustchain.offlineeuro.entity

import java.math.BigInteger

class BankRegistration (
    val id: Int,
    val bankDetails: BankDetails,
    val m: BigInteger?,
    val rm: BigInteger?,
    val v: BigInteger?,
    val r: BigInteger?
)
