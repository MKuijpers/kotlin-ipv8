package nl.tudelft.ipv8

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.horizontalsystems.bitcoincore.BitcoinCore
import io.horizontalsystems.bitcoincore.core.Bip
import io.horizontalsystems.bitcoinkit.BitcoinKit
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BitcoinKitAndroidWalletTest {
    @Test
    fun useAppContext() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val syncMode = BitcoinCore.SyncMode.Api()
        val bip = Bip.BIP44
        val exampleTestWords = listOf(
            "spell",
            "seat",
            "genius",
            "horn",
            "argue",
            "family",
            "steel",
            "buyer",
            "spawn",
            "chef",
            "guard",
            "vast"
        )
        val walletId = "WalletId"
        val confirmationsThreshold = 1
        val bitcoinKit = BitcoinKit(
            context,
            exampleTestWords,
            walletId,
            BitcoinKit.NetworkType.TestNet,
            confirmationsThreshold,
            syncMode,
            bip = bip
        )

        bitcoinKit.start()
        Thread.sleep(5000)
        Log.i("Coin", "BitcoinKit: start test")
        Log.i("Coin", "BitcoinKit: receive address ${bitcoinKit.receiveAddress()}")
        Log.i("Coin", "BitcoinKit: balance ${bitcoinKit.balance}")
        Log.i("Coin", "BitcoinKit: status-info ${bitcoinKit.statusInfo()}")

    }
}

