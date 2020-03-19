package nl.tudelft.ipv8.android.demo.ui.bitcoin

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_join_network.*
import nl.tudelft.ipv8.android.demo.CoinCommunity
import nl.tudelft.ipv8.android.demo.R
import nl.tudelft.ipv8.android.demo.coin.CoinUtil
import nl.tudelft.ipv8.android.demo.ui.BaseFragment
import nl.tudelft.ipv8.util.toHex


/**
 * A simple [Fragment] subclass.
 * Use the [MySharedWalletFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MySharedWalletFragment() : BaseFragment(R.layout.fragment_my_shared_wallets) {

    private fun initListView() {
        val sharedWalletBlocks = getCoinCommunity().fetchLatestJoinedSharedWalletBlocks()
        val publicKey = getTrustChainCommunity().myPeer.publicKey.keyToBin().toHex()
        val adaptor =
            SharedWalletListAdapter(this, sharedWalletBlocks, publicKey, "Click to enter wallet")
        list_view.adapter = adaptor
        list_view.setOnItemClickListener { _, view, position, id ->
            val block = sharedWalletBlocks[position]
            val parsedTransaction = CoinUtil.parseTransaction(block.transaction)
            val votingThreshold = parsedTransaction.getInt(CoinCommunity.SW_VOTING_THRESHOLD)
            val entranceFee = parsedTransaction.getDouble(CoinCommunity.SW_ENTRANCE_FEE)
            val users =
                parsedTransaction.getJSONArray(CoinCommunity.SW_TRUSTCHAIN_PKS).length()
            findNavController().navigate(
                MySharedWalletFragmentDirections.actionMySharedWalletsFragmentToSharedWalletTransaction(
                    block.publicKey.toHex(),
                    votingThreshold,
                    entranceFee.toLong(),
                    users
                )
            )
            Log.i("Coin", "Clicked: $view, $position, $id")
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initListView()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_shared_wallets, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() = MySharedWalletFragment()
    }
}
