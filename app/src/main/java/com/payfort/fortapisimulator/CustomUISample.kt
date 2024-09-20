package com.payfort.fortapisimulator

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.payfort.fortapisimulator.activities.ResponseActivity
import com.payfort.fortpaymentsdk.callbacks.PayFortCallback
import com.payfort.fortpaymentsdk.domain.model.FortRequest
import com.payfort.fortpaymentsdk.utils.gone
import com.payfort.fortpaymentsdk.utils.visible
import com.payfort.fortpaymentsdk.views.model.PayComponents
import com.payfort.forttestapp.R
import com.payfort.forttestapp.databinding.CustomUiBinding


class CustomUISample : AppCompatActivity(), PayFortCallback {

    private lateinit var binding: CustomUiBinding

    private var gson = Gson()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_ui)
        val fortRequest: FortRequest = intent.getSerializableExtra("fortRequest") as FortRequest
        val environment = intent.getStringExtra("env")
        supportActionBar?.hide()

        with(binding) {
            val payComponents = PayComponents(
                etCardNumberView,
                cvvView = etCardCvv,
                etCardExpiry,
                holderNameView = cardHolderNameView
            )

            btnPay.setup(environment!!, fortRequest, payComponents, this@CustomUISample)
            btnDirectPay.setup(environment, fortRequest, this@CustomUISample)
            rememberMeTB.setOnCheckedChangeListener { _, isChecked ->
                btnPay.isRememberMeEnabled(isChecked)
            }
        }
    }

    override fun startLoading() {
        Log.e("startLoading", "startLoading")
        binding.progressContainer.visible()
        enableFields(false)
    }


    override fun onSuccess(requestParamsMap: Map<String, Any>, fortResponseMap: Map<String, Any>) {
        stopLoading()
        openResponsePage(gson.toJson(fortResponseMap))
    }

    override fun onFailure(requestParamsMap: Map<String, Any>, fortResponseMap: Map<String, Any>) {
        stopLoading()
        openResponsePage(gson.toJson(fortResponseMap))
    }


    private fun openResponsePage(responseString: String) {
        Log.e("Error", responseString)
        stopLoading()
        val openResponseActivityIntent = Intent(this, ResponseActivity::class.java)
        openResponseActivityIntent.putExtra("responseString", responseString)
        startActivity(openResponseActivityIntent)
    }


    fun stopLoading() {
        Log.e("startLoading", "startLoading")
        binding.progressContainer.gone()
        enableFields(true)
    }

    private fun enableFields(enableFields: Boolean) {
        with(binding) {
            cardHolderNameView.isEnabled = enableFields
            etCardNumberView.isEnabled = enableFields
            etCardExpiry.isEnabled = enableFields
            etCardCvv.isEnabled = enableFields
            btnPay.isEnabled = enableFields
        }
    }


}