package nl.tudelft.ipv8.android.demo.sharedWallet

import nl.tudelft.ipv8.attestation.trustchain.TrustChainBlock
import nl.tudelft.ipv8.attestation.trustchain.TrustChainTransaction
import nl.tudelft.ipv8.attestation.trustchain.store.TrustChainStore
import org.json.JSONObject

abstract class SWBlockTransactionData(jsonData: JSONObject, val blockType: String) {
    val jsonData = jsonData

    fun getJsonString(): String {
        return jsonData.toString()
    }

    fun getTransactionData(): TrustChainTransaction {
        return mapOf("message" to getJsonString())
    }
}