package nl.tudelft.ipv8.android.demo.ui.bitcoin

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_bitcoin.*
import nl.tudelft.ipv8.android.demo.R
import nl.tudelft.ipv8.android.demo.coin.AddressPrivateKeyPair
import nl.tudelft.ipv8.android.demo.coin.BitcoinNetworkOptions
import nl.tudelft.ipv8.android.demo.coin.WalletManagerAndroid
import nl.tudelft.ipv8.android.demo.coin.WalletManagerConfiguration
import nl.tudelft.ipv8.android.demo.ui.BaseFragment
import org.bitcoinj.core.Address
import org.bitcoinj.script.Script


/**
 * A simple [Fragment] subclass.
 * Use the [BitcoinFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BitcoinFragment : BaseFragment(R.layout.fragment_bitcoin),
    ImportKeyDialog.ImportKeyDialogListener {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initClickListeners()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        Log.i("Coin", "Resuming")
        refresh()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // TODO: Try catch not too nice.
        try {
            WalletManagerAndroid.getInstance()
        } catch (e: IllegalStateException) {
            Log.w("Coin", "Wallet Manager not initialized.")
            return
        }

        inflater.inflate(R.menu.bitcoin_options, menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_blockchain_download_progress -> {
                Log.i("Coin", "Navigating from BitcoinFragment to BlockchainDownloadFragment")
                findNavController().navigate(BitcoinFragmentDirections.actionBitcoinFragmentToBlockchainDownloadFragment())
                true
            }
            R.id.exit_wallet -> {
                WalletManagerAndroid.close()
                findNavController().navigate(BitcoinFragmentDirections.actionBitcoinFragmentToDaoLoginChoice())
                true
            }
            R.id.item_blockchain_refresh -> {
                this.refresh(true)
                Log.i(
                    "Coin",
                    WalletManagerAndroid.getInstance().kit.wallet().toString(
                        true,
                        false,
                        false,
                        null
                    )
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initClickListeners() {
        show_wallet_button.setOnClickListener {
            Log.i("Coin", "Navigating from BitcoinFragment to MySharedWalletsFragment")
            findNavController().navigate(BitcoinFragmentDirections.actionBitcoinFragmentToMySharedWalletsFragment())
        }

        create_wallet_button.setOnClickListener {
            Log.i("Coin", "Navigating from BitcoinFragment to CreateSWFragment")
            findNavController().navigate(BitcoinFragmentDirections.actionBitcoinFragmentToCreateSWFragment())
        }

        search_wallet_button.setOnClickListener {
            Log.i("Coin", "Navigating from BitcoinFragment to JoinNetworkFragment")
            findNavController().navigate(BitcoinFragmentDirections.actionBitcoinFragmentToJoinNetworkFragment())
        }

        import_custom_keys.setOnClickListener {
            val dialog = ImportKeyDialog()
            dialog.setTargetFragment(this, 0)
            dialog.show(parentFragmentManager, "Import Key")
        }

        bitcoin_refresh_swiper.setOnRefreshListener {
            this.refresh()
            Handler().postDelayed({
                bitcoin_refresh_swiper.isRefreshing = false
            }, 1500)
        }
    }

    private fun refresh(animation: Boolean? = false) {
        if (animation!!) {
            bitcoin_refresh_swiper.isRefreshing = true
            Handler().postDelayed({
                bitcoin_refresh_swiper.isRefreshing = false
            }, 1500)
        }

        if (!WalletManagerAndroid.isRunning) {
            return
        }


        var walletManager = WalletManagerAndroid.getInstance()

        walletStatus.text = "Status: ${walletManager.kit.state()}"
        walletBalance.text =
            "Bitcoin available: ${walletManager.kit.wallet().balance.toFriendlyString()}"
        chosenNetwork.text = "Network: ${walletManager.params.id}"
        val seed = walletManager.toSeed()
        walletSeed.setText("${seed.seed}, ${seed.creationTime}")
        yourPublicHex.text = "Public (Protocol) Key: ${walletManager.networkPublicECKeyHex()}"
        protocolKey.setText(
            Address.fromKey(
                walletManager.params,
                walletManager.protocolECKey(),
                Script.ScriptType.P2PKH
            ).toString()
        )

        requireActivity().invalidateOptionsMenu()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bitcoin, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment bitcoinFragment.
         */
        @JvmStatic
        fun newInstance() = BitcoinFragment()
    }

    override fun onImport(address: String, privateKey: String, testNet: Boolean) {
        if (!WalletManagerAndroid.isRunning) {
            val config = WalletManagerConfiguration(
                if (testNet) BitcoinNetworkOptions.TEST_NET else BitcoinNetworkOptions.PRODUCTION,
                null,
                AddressPrivateKeyPair(address, privateKey)
            )

            WalletManagerAndroid.Factory(this.requireContext().applicationContext)
                .setConfiguration(config)
                .init()
        } else {
            WalletManagerAndroid.getInstance().addKey(privateKey)
        }

    }

    override fun onImportDone() {
        this.refresh(true)
        Handler().postDelayed({
            findNavController().navigate(BitcoinFragmentDirections.actionBitcoinFragmentToBlockchainDownloadFragment())
        }, 1500)
    }

}
