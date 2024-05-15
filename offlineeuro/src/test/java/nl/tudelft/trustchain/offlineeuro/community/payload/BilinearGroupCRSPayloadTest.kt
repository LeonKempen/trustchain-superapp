package nl.tudelft.trustchain.offlineeuro.community.payload

import nl.tudelft.trustchain.offlineeuro.cryptography.BilinearGroup
import nl.tudelft.trustchain.offlineeuro.cryptography.CRSGenerator
import org.junit.Assert
import org.junit.Test

class BilinearGroupCRSPayloadTest {

    @Test
    fun serializeAndDeserializeTest() {
        val group = BilinearGroup()
        val crs = CRSGenerator.generateCRSMap(group).first

        val groupElementBytes = group.toGroupElementBytes()
        val crsBytes = crs.toCRSBytes()

        val serializedPayload = BilinearGroupCRSPayload(groupElementBytes, crsBytes).serialize()
        val deserializedPayload = BilinearGroupCRSPayload.deserialize(serializedPayload).first
        val deserializedGroupElementBytes = deserializedPayload.bilinearGroupElements
        val deserializedCRSBytes = deserializedPayload.crs

        Assert.assertEquals("The group element bytes should be equal", groupElementBytes, deserializedGroupElementBytes)
        Assert.assertEquals("The CRS bytes should be equal", crsBytes, deserializedCRSBytes)
    }
}
