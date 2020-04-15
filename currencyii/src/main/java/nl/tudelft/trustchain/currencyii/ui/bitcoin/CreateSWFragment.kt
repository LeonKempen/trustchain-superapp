package nl.tudelft.trustchain.currencyii.ui.bitcoin

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_create_sw.*
import nl.tudelft.trustchain.currencyii.CoinCommunity
import nl.tudelft.trustchain.currencyii.R
import nl.tudelft.trustchain.currencyii.ui.BaseFragment
import org.bitcoinj.core.Coin


/**
 * A simple [Fragment] subclass.
 * Use the [CreateSWFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateSWFragment() : BaseFragment(R.layout.fragment_create_sw) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        create_sw_wallet_button.setOnClickListener {
            createSharedBitcoinWallet()
        }
    }

    private fun createSharedBitcoinWallet() {
        if (!validateCreationInput()) {
            alert_label.text = "Entrance fee should be a double, threshold an integer, both >0"
            return
        }
        alert_label.text = "Creating wallet, might take some time..."

        val currentEntranceFeeBTC = entrance_fee_tf.text.toString().toDouble()
        val currentEntranceFee = (Coin.COIN.value * currentEntranceFeeBTC).toLong()
        val currentThreshold = voting_threshold_tf.text.toString().toInt()
        voting_threshold_tf.isEnabled = false
        entrance_fee_tf.isEnabled = false

        try {
            // Try to create the bitcoin DAO
            getCoinCommunity().createBitcoinGenesisWallet(
                currentEntranceFee,
                currentThreshold,
                ::updateAlertLabel
            )
            resetWalletInitializationValues()
            alert_label.text = "Wallet created successfully!"
        } catch (t: Throwable) {
            resetWalletInitializationValues()
            alert_label.text = t.message ?: "Unexpected error occurred. Try again"
        }
    }

    private fun updateAlertLabel(progress: Double) {
        Log.i("Coin", "Coin: broadcast of transaction progress: $progress.")
        // TODO: fix co-routines, threads etc to show progress
//        val progressString = "%.1f".format(progress)
//        alert_label.text = "Current progress: $progressString%..."
    }

    private fun resetWalletInitializationValues() {
        alert_label.text = ""
    }

    private fun validateCreationInput(): Boolean {
        val entranceFee = entrance_fee_tf.text.toString().toDoubleOrNull()
        val votingThreshold = voting_threshold_tf.text.toString().toIntOrNull()
        return entranceFee != null &&
            entranceFee > 0 &&
            votingThreshold != null &&
            votingThreshold > 0 &&
            votingThreshold <= 100
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_sw, container, false)
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() = CreateSWFragment()
    }
}
