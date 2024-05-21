package nl.tudelft.trustchain.offlineeuro.community.payload

import nl.tudelft.trustchain.offlineeuro.cryptography.GrothSahaiProof
import nl.tudelft.trustchain.offlineeuro.cryptography.SchnorrSignature
import nl.tudelft.trustchain.offlineeuro.cryptography.TransactionProof
import nl.tudelft.trustchain.offlineeuro.entity.CentralAuthority
import nl.tudelft.trustchain.offlineeuro.entity.DigitalEuro
import nl.tudelft.trustchain.offlineeuro.entity.TransactionDetails
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger

class TransactionDetailsPayloadTest {
    private val group = CentralAuthority.groupDescription

    @Test
    fun serializeAndDeserializeInitialEuro() {
        val transactionDetails = generateTransactionDetails(0)
        val transactionDetailsBytes = transactionDetails.toTransactionDetailsBytes()
        val serializedBytes = TransactionDetailsPayload(transactionDetailsBytes).serialize()
        val deserializedBytes = TransactionDetailsPayload.deserialize(serializedBytes).first.transactionDetailsBytes
        val fromBytes = deserializedBytes.toTransactionDetails(group)
        Assert.assertEquals(transactionDetails, fromBytes)
    }

    @Test
    fun serializeAndDeserializeEuroOneProof() {
        val transactionDetails = generateTransactionDetails(1)
        val transactionDetailsBytes = transactionDetails.toTransactionDetailsBytes()
        val serializedBytes = TransactionDetailsPayload(transactionDetailsBytes).serialize()
        val deserializedBytes = TransactionDetailsPayload.deserialize(serializedBytes).first.transactionDetailsBytes
        val fromBytes = deserializedBytes.toTransactionDetails(group)
        Assert.assertEquals(transactionDetails, fromBytes)
    }

    @Test
    fun serializeAndDeserializeEuroFiveProofs() {
        val transactionDetails = generateTransactionDetails(5)
        val transactionDetailsBytes = transactionDetails.toTransactionDetailsBytes()
        val serializedBytes = TransactionDetailsPayload(transactionDetailsBytes).serialize()
        val deserializedBytes = TransactionDetailsPayload.deserialize(serializedBytes).first.transactionDetailsBytes
        val fromBytes = deserializedBytes.toTransactionDetails(group)
        Assert.assertEquals(transactionDetails, fromBytes)
    }

    fun generateTransactionDetails(numberOfProofs: Int): TransactionDetails {
        val digitalEuro = generateDigitalEuro(numberOfProofs)
        val transactionProof = generateTranactionProof()

        val previousSignature =
            if (numberOfProofs > 0) {
                generateSignature()
            } else {
                null
            }

        return TransactionDetails(
            digitalEuro,
            transactionProof,
            previousSignature,
            generateSignature(),
            group.generateRandomElementOfG()
        )
    }

    fun generateTranactionProof(): TransactionProof {
        return TransactionProof(
            generateGrothSahaiProof(),
            group.generateRandomElementOfH(),
            group.generateRandomElementOfH()
        )
    }

    fun generateDigitalEuro(numberOfProofs: Int): DigitalEuro {
        val proofs = arrayListOf<GrothSahaiProof>()
        for (i: Int in 0 until numberOfProofs) {
            proofs.add(generateGrothSahaiProof())
        }

        return DigitalEuro(
            "Test Serialnumber",
            group.generateRandomElementOfG(),
            generateSignature(),
            proofs
        )
    }

    fun generateSignature(): SchnorrSignature {
        return SchnorrSignature(
            BigInteger("32930721097129037029137"),
            BigInteger("24829734219852198372193"),
            "SchnorrSignatureTest".toByteArray()
        )
    }

    fun generateGrothSahaiProof(): GrothSahaiProof {
        return GrothSahaiProof(
            group.generateRandomElementOfG(),
            group.generateRandomElementOfG(),
            group.generateRandomElementOfH(),
            group.generateRandomElementOfH(),
            group.generateRandomElementOfG(),
            group.generateRandomElementOfG(),
            group.generateRandomElementOfH(),
            group.generateRandomElementOfH(),
            group.generateRandomElementOfGT()
        )
    }
}
