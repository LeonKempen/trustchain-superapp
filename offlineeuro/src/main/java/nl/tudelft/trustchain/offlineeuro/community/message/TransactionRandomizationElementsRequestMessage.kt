package nl.tudelft.trustchain.offlineeuro.community.message

import nl.tudelft.ipv8.Peer

class TransactionRandomizationElementsRequestMessage(
    val publicKey: ByteArray,
    val requestingPeer: Peer
) : ICommunityMessage {
    override val messageType = CommunityMessageType.TransactionRandomnessRequestMessage
}