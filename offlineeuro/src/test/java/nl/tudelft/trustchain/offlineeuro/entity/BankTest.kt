package nl.tudelft.trustchain.offlineeuro.entity

import nl.tudelft.trustchain.offlineeuro.communication.IPV8CommunicationProtocol
import nl.tudelft.trustchain.offlineeuro.community.OfflineEuroCommunity
import nl.tudelft.trustchain.offlineeuro.community.message.AddressMessage
import nl.tudelft.trustchain.offlineeuro.community.message.BilinearGroupCRSReplyMessage
import nl.tudelft.trustchain.offlineeuro.cryptography.BilinearGroup
import nl.tudelft.trustchain.offlineeuro.cryptography.CRSGenerator
import nl.tudelft.trustchain.offlineeuro.cryptography.PairingTypes
import nl.tudelft.trustchain.offlineeuro.db.AddressBookManager
import nl.tudelft.trustchain.offlineeuro.db.DepositedEuroManager
import nl.tudelft.trustchain.offlineeuro.enums.Role
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class BankTest {
    private val ttpGroup = BilinearGroup(PairingTypes.FromFile)
    private val crs = CRSGenerator.generateCRSMap(ttpGroup).first
    private val depositedEuroManager = Mockito.mock(DepositedEuroManager::class.java)

    @Test
    fun initWithSetupTest() {
        val addressBookManager = Mockito.mock(AddressBookManager::class.java)
        val community = Mockito.mock(OfflineEuroCommunity::class.java)
        val communicationProtocol = IPV8CommunicationProtocol(addressBookManager, community)

        whenever(community.messageList).thenReturn(communicationProtocol.messageList)
        whenever(community.getGroupDescriptionAndCRS()).then {
            communicationProtocol.messageList.add(
                BilinearGroupCRSReplyMessage(
                    ttpGroup.toGroupElementBytes(),
                    crs.toCRSBytes(),
                    AddressMessage("TTP", Role.TTP, "SomeBytes".toByteArray(), "More Bytes".toByteArray())
                )
            )
        }
        val ttpAddress = Address("TTP", Role.TTP, ttpGroup.generateRandomElementOfG(), "More Bytes".toByteArray())

        val publicKeyCaptor = argumentCaptor<ByteArray>()

        whenever(addressBookManager.getAddressByName("TTP")).thenReturn(ttpAddress)
        whenever(community.registerAtTTP(any(), publicKeyCaptor.capture(), any())).then { }

        val bankName = "SomeBank"
        val bank = Bank(bankName, BilinearGroup(PairingTypes.FromFile), communicationProtocol, null, depositedEuroManager)

        val capturedPKBytes = publicKeyCaptor.firstValue
        val capturedPK = ttpGroup.gElementFromBytes(capturedPKBytes)

        Assert.assertEquals(bankName, bank.name)
        Assert.assertEquals(ttpGroup, bank.group)
        Assert.assertEquals(crs, bank.crs)
        Assert.assertEquals(bank.publicKey, capturedPK)
        Assert.assertEquals(bank, communicationProtocol.participant)
    }

    @Test
    fun initWithoutSetUpTest() {
        val addressBookManager = Mockito.mock(AddressBookManager::class.java)
        val community = Mockito.mock(OfflineEuroCommunity::class.java)
        val communicationProtocol = IPV8CommunicationProtocol(addressBookManager, community)

        val bankName = "SomeOtherBank"
        val group = BilinearGroup(PairingTypes.FromFile)
        val bank = Bank(bankName, group, communicationProtocol, null, depositedEuroManager, false)

        verify(community, never()).getGroupDescriptionAndCRS()
        verify(community, never()).registerAtTTP(any(), any(), any())
        Assert.assertEquals(group, bank.group)

        Assert.assertThrows(UninitializedPropertyAccessException::class.java) {
            bank.crs.toCRSBytes()
        }
    }

    @Test
    fun getBlindSignatureRandomnessTest() {
        val bank = getBank()
        val publicKey = ttpGroup.generateRandomElementOfG()

        val firstRandomness = bank.getBlindSignatureRandomness(publicKey)
        val secondRandomness = bank.getBlindSignatureRandomness(publicKey)

        Assert.assertEquals("The same randomness should be returned", firstRandomness, secondRandomness)

        val newPublicKey = ttpGroup.generateRandomElementOfG()
        val thirdRandomness = bank.getBlindSignatureRandomness(newPublicKey)

        Assert.assertNotEquals(firstRandomness, thirdRandomness)
    }

    fun getBank(): Bank {
        val addressBookManager = Mockito.mock(AddressBookManager::class.java)
        val community = Mockito.mock(OfflineEuroCommunity::class.java)
        val communicationProtocol = IPV8CommunicationProtocol(addressBookManager, community)

        val bankName = "Bank"
        val group = ttpGroup
        val bank = Bank(bankName, group, communicationProtocol, null, depositedEuroManager, false)
        bank.crs = crs
        return bank
    }
}
